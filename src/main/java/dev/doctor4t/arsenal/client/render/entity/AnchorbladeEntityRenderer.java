package dev.doctor4t.arsenal.client.render.entity;

import dev.doctor4t.arsenal.entity.AnchorbladeEntity;
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
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class AnchorbladeEntityRenderer extends EntityRenderer<AnchorbladeEntity> {
    private final ItemRenderer itemRenderer;
    private final BakedModelManager bakedModelManager;

    public AnchorbladeEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.itemRenderer = ctx.getItemRenderer();
        this.bakedModelManager = ctx.getModelManager();
    }

    @Override
    public void render(AnchorbladeEntity anchorbladeEntity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        float yawAngle = MathHelper.lerp(tickDelta, anchorbladeEntity.prevYaw, anchorbladeEntity.getYaw());
        float pitchAngle = MathHelper.lerp(tickDelta, anchorbladeEntity.prevPitch, anchorbladeEntity.getPitch());

        matrices.push();
        matrices.translate(0, .6, 0);

        float scale = 1.6f;
        matrices.scale(scale, scale, scale);

        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(yawAngle + 90));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-pitchAngle + 45));

        BakedModel model = this.bakedModelManager.getModel(AnchorbladeItem.Skin.DEFAULT.anchorbladeEntityModel);
        RenderLayer chainLayer = RenderLayer.getEntitySmoothCutout(AnchorbladeItem.Skin.DEFAULT.chainTexture);
        AnchorbladeItem.Skin skin = AnchorbladeItem.Skin.fromString(ArsenalCosmetics.getSkin(anchorbladeEntity.getStack()));
        if (skin != null) {
            model = this.bakedModelManager.getModel(skin.anchorbladeEntityModel);
            chainLayer = RenderLayer.getEntitySmoothCutout(skin.chainTexture);
        }
        this.itemRenderer.renderItem(anchorbladeEntity.getStack(), ModelTransformationMode.FIXED, false, matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV, model);

        matrices.pop();

        if (anchorbladeEntity.getOwner() instanceof LivingEntity owner) {
            matrices.push();
            Vec3d pos = anchorbladeEntity.getLerpedPos(tickDelta);
            Vec3d ringPos = new Vec3d((skin == AnchorbladeItem.Skin.AMBESSA ? 0f : 1f), 0, 0).rotateZ(pitchAngle * MathHelper.RADIANS_PER_DEGREE).rotateY((yawAngle + 90) * MathHelper.RADIANS_PER_DEGREE).add(0, anchorbladeEntity.getHeight() / 2f, 0);
            Vec3d ownerPos = owner.getLeashPos(tickDelta).subtract(pos);
            float length = (float) ringPos.distanceTo(ownerPos);
            MatrixStack.Entry matrixEntry = matrices.peek();
            Matrix4f modelMatrix = matrixEntry.getPositionMatrix();
            Matrix3f normal = matrixEntry.getNormalMatrix();
            float minU = 0;
            float maxU = 1;
            float minV = 0;
            float maxV = length / 8f;
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(chainLayer);
            Vec3d offset = ownerPos.subtract(ringPos).normalize().multiply(0.25, 0, 0.25).rotateY((float) (Math.PI / 2));
            Vec3d vert1 = ringPos.add(offset);
            Vec3d vert2 = ownerPos.add(offset);
            Vec3d vert3 = ownerPos.subtract(offset);
            Vec3d vert4 = ringPos.subtract(offset);
            int chainLight = LightmapTextureManager.pack(this.getBlockLight(anchorbladeEntity, owner.getBlockPos()), this.getSkyLight(anchorbladeEntity, owner.getBlockPos()));
            this.vertex(vert1, vertexConsumer, minU, minV, modelMatrix, normal, light);
            this.vertex(vert2, vertexConsumer, minU, maxV, modelMatrix, normal, chainLight);
            this.vertex(vert3, vertexConsumer, maxU, maxV, modelMatrix, normal, chainLight);
            this.vertex(vert4, vertexConsumer, maxU, minV, modelMatrix, normal, light);
            matrices.pop();
        }
    }

    private void vertex(Vec3d vec, VertexConsumer vertexConsumer, float u, float v, Matrix4f modelMatrix, Matrix3f normal, int light) {
        vertexConsumer.vertex(modelMatrix, (float) vec.x, (float) vec.y, (float) vec.z).color(255, 255, 255, 255).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normal, 0, 1, 0).next();
    }

    @Override
    public Identifier getTexture(AnchorbladeEntity entity) {
        return null;
    }
}
