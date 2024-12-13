package dev.doctor4t.arsenal.client.render.entity;

import dev.doctor4t.arsenal.client.render.item.TridentDynamicItemRenderer;
import dev.doctor4t.arsenal.index.ArsenalCosmetics;
import dev.doctor4t.arsenal.item.AnchorbladeItem;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class ArsenalTridentEntityRenderer extends EntityRenderer<TridentEntity> {
    private final ItemRenderer itemRenderer;
    private final BakedModelManager bakedModelManager;
    private ItemStack tridentStack = new ItemStack(Items.TRIDENT); // TODO: Replace with a synced stack so skins work

    public ArsenalTridentEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.itemRenderer = ctx.getItemRenderer();
        this.bakedModelManager = ctx.getModelManager();
    }

    @Override
    public void render(TridentEntity tridentEntity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        float yawAngle = MathHelper.lerp(tickDelta, tridentEntity.prevYaw, tridentEntity.getYaw());
        float pitchAngle = MathHelper.lerp(tickDelta, tridentEntity.prevPitch, tridentEntity.getPitch());

        matrices.push();

        float scale = 1.6f;
        matrices.scale(scale, scale, scale);

        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(yawAngle + 90));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-pitchAngle + 45));
        matrices.translate(.3, -.3, 0);

        BakedModel model = this.bakedModelManager.getModel(TridentDynamicItemRenderer.DEFAULT_MODEL_IDENTIFIER.getRight());
        this.itemRenderer.renderItem(tridentStack, ModelTransformationMode.FIXED, false, matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV, model);

        matrices.pop();
    }

    private void vertex(Vec3d vec, VertexConsumer vertexConsumer, float u, float v, Matrix4f modelMatrix, Matrix3f normal, int light) {
        vertexConsumer.vertex(modelMatrix, (float) vec.x, (float) vec.y, (float) vec.z).color(255, 255, 255, 255).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normal, 0, 1, 0).next();
    }

    @Override
    public Identifier getTexture(TridentEntity entity) {
        return null;
    }
}
