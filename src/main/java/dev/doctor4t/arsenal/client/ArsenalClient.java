package dev.doctor4t.arsenal.client;

import dev.doctor4t.arsenal.Arsenal;
import dev.doctor4t.arsenal.cca.BackWeaponComponent;
import dev.doctor4t.arsenal.client.particle.contract.ColoredParticleInitialData;
import dev.doctor4t.arsenal.client.render.entity.AnchorbladeEntityRenderer;
import dev.doctor4t.arsenal.client.render.entity.BloodScytheEntityRenderer;
import dev.doctor4t.arsenal.client.render.entity.ModEntityModelLayers;
import dev.doctor4t.arsenal.client.render.entity.WeaponRackEntityRenderer;
import dev.doctor4t.arsenal.client.render.item.AnchorbladeDynamicItemRenderer;
import dev.doctor4t.arsenal.client.render.item.ScytheDynamicItemRenderer;
import dev.doctor4t.arsenal.index.ArsenalEntities;
import dev.doctor4t.arsenal.index.ArsenalItems;
import dev.doctor4t.arsenal.index.ArsenalParticles;
import dev.doctor4t.arsenal.item.AnchorbladeItem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.Locale;

@SuppressWarnings("unused")
public class ArsenalClient implements ClientModInitializer {
    public static ModelTransformationMode currentMode = ModelTransformationMode.NONE;

    static {
        for (var mode : ModelTransformationMode.values()) {
            ModelPredicateProviderRegistry.register(Arsenal.id(mode.name().toLowerCase(Locale.ROOT)), (stack, world, entity, seed) -> mode == currentMode ? 1.0F : 0.0F);
        }
    }

    public static KeyBinding weaponKeybind;
    public static KeyBinding swapKeybind;

    @Override
    public void onInitializeClient() {

        // Register integrated resource pack
        FabricLoader.getInstance().getModContainer(Arsenal.MOD_ID).ifPresent(modContainer -> {
            ResourceManagerHelper.registerBuiltinResourcePack(new Identifier(Arsenal.MOD_ID, "classic"), modContainer, ResourcePackActivationType.NORMAL);
        });

        // Builtin Item Renderers
        BuiltinItemRendererRegistry.INSTANCE.register(ArsenalItems.SCYTHE, new ScytheDynamicItemRenderer());
        BuiltinItemRendererRegistry.INSTANCE.register(ArsenalItems.ANCHORBLADE, new AnchorbladeDynamicItemRenderer());

        // Force load the weapon models (otherwise since they're never called they wouldn't be loaded by default)
        ModelLoadingPlugin.register(pluginContext -> pluginContext.addModels(ScytheDynamicItemRenderer.MODELS_TO_REGISTER));
        ModelLoadingPlugin.register(pluginContext -> pluginContext.addModels(AnchorbladeDynamicItemRenderer.MODELS_TO_REGISTER));
        ModelLoadingPlugin.register(pluginContext -> pluginContext.addModels(WeaponRackEntityRenderer.MODEL));

        // model layers initialization
        ModEntityModelLayers.initialize();

        // entity renderers registration
        EntityRendererRegistry.register(ArsenalEntities.BLOOD_SCYTHE, BloodScytheEntityRenderer::new);
        EntityRendererRegistry.register(ArsenalEntities.ANCHORBLADE, AnchorbladeEntityRenderer::new);
        EntityRendererRegistry.register(ArsenalEntities.WEAPON_RACK, WeaponRackEntityRenderer::new);

        // particle renderers registration
        ArsenalParticles.registerFactories();

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
                ClientPlayNetworking.send(Arsenal.SERVERBOUND_SWAP_WEAPON_PACKET, PacketByteBufs.empty());
            }
        });

        // Anchorblade entity model init
        for (AnchorbladeItem.Skin value : AnchorbladeItem.Skin.values()) {
            ModelLoadingPlugin.register(context -> context.addModels(value.anchorbladeEntityModel));
        }

        // attack sweep particle packet
        ClientPlayNetworking.registerGlobalReceiver(Arsenal.CLIENTBOUND_SWEEP_PACKET, (client, handler, buf, responseSender) -> {
            int color = buf.readInt();
            int shadowColor = buf.readInt();
            double x = buf.readDouble();
            double y = buf.readDouble();
            double z = buf.readDouble();

            client.execute(() -> {
                if (client.world != null) {
                    client.world.addParticle(ArsenalParticles.SWEEP_PARTICLE.setData(new ColoredParticleInitialData(color)), x, y, z, 0, 0, 0);
                    client.world.addParticle(ArsenalParticles.SWEEP_SHADOW_PARTICLE.setData(new ColoredParticleInitialData(shadowColor)), x, y, z, 0, 0, 0);
                }
            });
        });

    }
}
