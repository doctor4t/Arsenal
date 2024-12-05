package dev.doctor4t.arsenal.client.render.item;

import dev.doctor4t.arsenal.Arsenal;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public class AnchorbladeDynamicItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    public static final ModelIdentifier WEAPON = new ModelIdentifier(Arsenal.id("anchorblade_inventory"), "inventory");
    public static final ModelIdentifier WEAPON_IN_HAND = new ModelIdentifier(Arsenal.id("anchorblade_in_hand"), "inventory");

    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        boolean inHand = mode.isFirstPerson() || mode == ModelTransformationMode.THIRD_PERSON_LEFT_HAND || mode == ModelTransformationMode.THIRD_PERSON_RIGHT_HAND || mode == ModelTransformationMode.HEAD || mode == ModelTransformationMode.FIXED;

        matrices.push();
        matrices.translate(.5, .5, .5);

        BakedModel model = MinecraftClient.getInstance().getBakedModelManager().getModel(inHand ? WEAPON_IN_HAND : WEAPON);

        // FIXME: Weird lighting for blocks in the inventory when held?
        if (!inHand) {
            DiffuseLighting.enableGuiDepthLighting();
        }
        MinecraftClient.getInstance().getItemRenderer().renderItem(stack, mode, false, matrices, vertexConsumers, light, overlay, model);
        if (!inHand) {
            DiffuseLighting.disableGuiDepthLighting();
        }
        matrices.pop();
    }
}
