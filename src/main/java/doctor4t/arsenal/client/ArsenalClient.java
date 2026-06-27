package doctor4t.arsenal.client;

import dev.emi.trinkets.TrinketsClient;
import doctor4t.arsenal.client.render.entity.AnchorbladeEntityRenderer;
import doctor4t.arsenal.client.render.entity.BloodScytheEntityRenderer;
import doctor4t.arsenal.client.render.entity.ModEntityModelLayers;
import doctor4t.arsenal.client.render.item.GUIHeldVaryingItemRenderer;
import doctor4t.arsenal.client.render.item.GuillotineGUIHeldVaryingItemRenderer;
import doctor4t.arsenal.common.Arsenal;
import doctor4t.arsenal.common.components.BackWeaponComponent;
import doctor4t.arsenal.common.init.ModEntities;
import doctor4t.arsenal.common.init.ModItems;
import doctor4t.arsenal.common.init.ModParticles;
import doctor4t.arsenal.common.item.GuillotineItem;
import doctor4t.arsenal.common.util.WeaponSlotCallback;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.item.Item;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.lwjgl.glfw.GLFW;

import java.util.List;

@SuppressWarnings("unused")
public class ArsenalClient implements ClientModInitializer {
	public static KeyBinding weaponKeybind;
	public static KeyBinding swapKeybind;

	public static void registerGUIHandheldVaryingWeaponRenderer(Item item) {
		Identifier weaponId = Registry.ITEM.getId(item);
		GUIHeldVaryingItemRenderer GUIHeldVaryingItemRenderer = new GUIHeldVaryingItemRenderer(weaponId);
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(GUIHeldVaryingItemRenderer);
		BuiltinItemRendererRegistry.INSTANCE.register(item, GUIHeldVaryingItemRenderer);
		ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> {
			out.accept(new ModelIdentifier(weaponId.getNamespace(), weaponId.getPath() + "_gui", "inventory"));
			out.accept(new ModelIdentifier(weaponId.getNamespace(), weaponId.getPath() + "_handheld", "inventory"));
		});
	}

	public static void registerGuillotineRenderer(Item item) {
		Identifier weaponId = Registry.ITEM.getId(item);
		GUIHeldVaryingItemRenderer GUIHeldVaryingItemRenderer = new GuillotineGUIHeldVaryingItemRenderer(weaponId);
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(GUIHeldVaryingItemRenderer);
		BuiltinItemRendererRegistry.INSTANCE.register(item, GUIHeldVaryingItemRenderer);
		List<String> variations = Arsenal.GUILLOTINE_VARIATIONS;
		ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> {
			for (String variation : variations) {
				out.accept(new ModelIdentifier(weaponId.getNamespace(), weaponId.getPath() + "_gui_" + variation, "inventory"));
				out.accept(new ModelIdentifier(weaponId.getNamespace(), weaponId.getPath() + "_handheld_" + variation, "inventory"));
			}
		});
	}

	@Override
	public void onInitializeClient() {
		// custom item renderers registration
		registerGUIHandheldVaryingWeaponRenderer(ModItems.CLOWN_SCYTHE);
		registerGUIHandheldVaryingWeaponRenderer(ModItems.ANCHORBLADE);
		registerGuillotineRenderer(ModItems.GUILLOTINE);

		// model layers initialization
		ModEntityModelLayers.initialize();

		// entity renderers registration
		EntityRendererRegistry.register(ModEntities.BLOOD_SCYTHE, BloodScytheEntityRenderer::new);
		EntityRendererRegistry.register(ModEntities.ANCHORBLADE, AnchorbladeEntityRenderer::new);

		// particle renderers registration
		ModParticles.registerFactories();

		// amy's bullshit sick ass custom weapon slot
		WeaponSlotCallback.EVENT.register((player, stack) -> {
			if (stack.getItem() == ModItems.ANCHORBLADE) {
				return ActionResult.FAIL;
			}
			if (stack.getItem() == ModItems.CLOWN_SCYTHE) {
				return ActionResult.FAIL;
			}
			if (stack.getItem() == ModItems.GUILLOTINE) {
				return ActionResult.FAIL;
			}
			return ActionResult.PASS;
		});

		// keybindings
		weaponKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.arsenal.select_weapon",
				GLFW.GLFW_KEY_R,
				"category.arsenal"
		));
		swapKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.arsenal.swap_weapon",
				GLFW.GLFW_KEY_G,
				"category.arsenal"
		));

		// client tick events
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (weaponKeybind.wasPressed() && client.player != null) {
				BackWeaponComponent.setHoldingBackWeapon(client.player, !BackWeaponComponent.isHoldingBackWeapon(client.player));
			}
			if (swapKeybind.wasPressed()) {
				ClientPlayNetworking.send(Arsenal.swapWeaponPacketId, PacketByteBufs.empty());
			}
		});

		// guillotine alternate mode textures
//		ModelPredicateProviderRegistry.register(Arsenal.id("guillotine_mode"), (stack, world, entity, seed) -> stack.getOrCreateNbt().getInt(GuillotineItem.NBT_GUILLOTINE_MODE));
	}
}
