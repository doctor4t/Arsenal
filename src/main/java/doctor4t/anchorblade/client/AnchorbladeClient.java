package doctor4t.anchorblade.client;

import doctor4t.anchorblade.client.particle.SweepAttackParticle;
import doctor4t.anchorblade.client.render.item.AnchorbladeItemRenderer;
import doctor4t.anchorblade.common.init.ModItems;
import doctor4t.anchorblade.common.init.ModParticles;
import doctor4t.anchorblade.common.util.AnchorSelection;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.option.KeyBind;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.lwjgl.glfw.GLFW;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

public class AnchorbladeClient implements ClientModInitializer {
	private static KeyBind anchorKeybind;

	@Override
	public void onInitializeClient(ModContainer mod) {
		Identifier anchorbladeId = Registry.ITEM.getId(ModItems.ANCHORBLADE);
		AnchorbladeItemRenderer anchorbladeItemRenderer = new AnchorbladeItemRenderer(anchorbladeId);
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(anchorbladeItemRenderer);
		BuiltinItemRendererRegistry.INSTANCE.register(ModItems.ANCHORBLADE, anchorbladeItemRenderer);
		ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> {
			out.accept(new ModelIdentifier(anchorbladeId.getNamespace(), anchorbladeId.getPath() + "_gui", "inventory"));
			out.accept(new ModelIdentifier(anchorbladeId.getNamespace(), anchorbladeId.getPath() + "_handheld", "inventory"));
		});

		ParticleFactoryRegistry.getInstance().register(ModParticles.LUX_ANCHORLADE_SWEEP_1, SweepAttackParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(ModParticles.LUX_ANCHORLADE_SWEEP_2, SweepAttackParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(ModParticles.LUX_ANCHORLADE_SWEEP_3, SweepAttackParticle.Factory::new);

		anchorKeybind = KeyBindingHelper.registerKeyBinding(new KeyBind(
				"key.anchorblade.selectanchor",
				GLFW.GLFW_KEY_R,
				"category.anchorblade"
		));
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (anchorKeybind.wasPressed()) {
				PlayerEntity player = client.player;
				if (player != null && player.getInventory() instanceof AnchorSelection selection) {
					selection.anchorblade$setSelectedAnchor(!selection.anchorblade$hasSelectedAnchor());
					if (client.getNetworkHandler() != null) {
						UpdateSelectedSlotC2SPacket packet = new UpdateSelectedSlotC2SPacket(player.getInventory().selectedSlot);
						if (packet instanceof AnchorSelection selectPacket) {
							selectPacket.anchorblade$setSelectedAnchor(selection.anchorblade$hasSelectedAnchor());
						} else {
							System.out.println("Packet is not an instance of AnchorSelection");
						}
						client.getNetworkHandler().sendPacket(packet);
					}
				}
			}
		});

//		// block render layer map
//		BlockRenderLayerMap.put(RenderLayer.getCutout(), ModBlock.MOD_BLOCK);

//		// entity renderer registration
//		EntityRendererRegistry.register(ModEntities.MOD_ENTITY, ModEntityRenderer::new);
	}
}
