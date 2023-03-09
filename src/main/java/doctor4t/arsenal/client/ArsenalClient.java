package doctor4t.arsenal.client;

import doctor4t.arsenal.client.particle.SweepAttackParticle;
import doctor4t.arsenal.client.render.entity.AltAnchorbladeEntityRenderer;
import doctor4t.arsenal.client.render.entity.AnchorbladeEntityRenderer;
import doctor4t.arsenal.client.render.entity.ModEntityModelLayers;
import doctor4t.arsenal.client.render.item.AnchorbladeItemRenderer;
import doctor4t.arsenal.common.Arsenal;
import doctor4t.arsenal.common.init.ModEntities;
import doctor4t.arsenal.common.init.ModItems;
import doctor4t.arsenal.common.init.ModParticles;
import doctor4t.arsenal.common.util.WeaponSlotCallback;
import doctor4t.arsenal.common.util.WeaponSlotToggle;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.option.KeyBind;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.lwjgl.glfw.GLFW;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

@SuppressWarnings("unused")
public class ArsenalClient implements ClientModInitializer {
	private static KeyBind weaponKeybind;
	private static KeyBind swapKeybind;

	@Override
	public void onInitializeClient(ModContainer mod) {
		{
			Identifier anchorbladeId = Registry.ITEM.getId(ModItems.ANCHORBLADE);
			AnchorbladeItemRenderer anchorbladeItemRenderer = new AnchorbladeItemRenderer(anchorbladeId);
			ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(anchorbladeItemRenderer);
			BuiltinItemRendererRegistry.INSTANCE.register(ModItems.ANCHORBLADE, anchorbladeItemRenderer);
			ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> {
				out.accept(new ModelIdentifier(anchorbladeId.getNamespace(), anchorbladeId.getPath() + "_gui", "inventory"));
				out.accept(new ModelIdentifier(anchorbladeId.getNamespace(), anchorbladeId.getPath() + "_handheld", "inventory"));
			});
		}

		ModEntityModelLayers.initialize();

		EntityRendererRegistry.register(ModEntities.ANCHORBLADE, AnchorbladeEntityRenderer::new);

		ParticleFactoryRegistry.getInstance().register(ModParticles.LUX_ANCHORBLADE_SWEEP_1, SweepAttackParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(ModParticles.LUX_ANCHORBLADE_SWEEP_2, SweepAttackParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(ModParticles.LUX_ANCHORBLADE_SWEEP_3, SweepAttackParticle.Factory::new);

		WeaponSlotCallback.EVENT.register((player, holder, stack) -> {
			if (stack.getItem() == ModItems.ANCHORBLADE) {
				return ActionResult.FAIL;
			}
			return ActionResult.PASS;
		});

		weaponKeybind = KeyBindingHelper.registerKeyBinding(new KeyBind(
				"key.arsenal.select_weapon",
				GLFW.GLFW_KEY_R,
				"category.arsenal"
		));
		swapKeybind = KeyBindingHelper.registerKeyBinding(new KeyBind(
				"key.arsenal.swap_weapon",
				GLFW.GLFW_KEY_G,
				"category.arsenal"
		));
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (weaponKeybind.wasPressed()) {
				PlayerEntity player = client.player;
				if (player != null && player.getInventory() instanceof WeaponSlotToggle selection) {
					selection.arsenal$setWeaponSlot(!selection.arsenal$shouldWeaponSlot());
					if (client.getNetworkHandler() != null) {
						UpdateSelectedSlotC2SPacket packet = new UpdateSelectedSlotC2SPacket(player.getInventory().selectedSlot);
						//noinspection ConstantValue
						if (packet instanceof WeaponSlotToggle selectPacket) {
							selectPacket.arsenal$setWeaponSlot(selection.arsenal$shouldWeaponSlot());
						}
						client.getNetworkHandler().sendPacket(packet);
					}
				}
			}
			if (swapKeybind.wasPressed()) {
				ClientPlayNetworking.send(Arsenal.swapWeaponPacketId, PacketByteBufs.empty());
			}
		});
	}
}
