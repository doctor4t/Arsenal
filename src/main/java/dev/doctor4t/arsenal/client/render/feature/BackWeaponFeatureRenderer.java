package dev.doctor4t.arsenal.client.render.feature;

import dev.doctor4t.arsenal.cca.BackWeaponComponent;
import dev.doctor4t.arsenal.util.WeaponSlotCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class BackWeaponFeatureRenderer<T extends PlayerEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
    public BackWeaponFeatureRenderer(FeatureRendererContext<T, M> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (BackWeaponComponent.isHoldingBackWeapon(entity)) return;
        ItemStack stack = BackWeaponComponent.getBackWeapon(entity);
        if (stack.isEmpty()) return;

        matrices.push();

        if (entity.isSneaking()) {
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(32.5f));
            matrices.translate(0, 0.25, -0.15);
        } else if (entity.isSprinting()) {
            // TODO: Slight tilt when sprinting to not clip with legs, need to make a component to store sprinting ticks and lerp
            matrices.translate(0.0F, 0.0F, 0.125F);
            double d = MathHelper.lerp(tickDelta, entity.prevCapeX, entity.capeX)
                    - MathHelper.lerp(tickDelta, entity.prevX, entity.getX());
            double e = MathHelper.lerp(tickDelta, entity.prevCapeY, entity.capeY)
                    - MathHelper.lerp(tickDelta, entity.prevY, entity.getY());
            double m = MathHelper.lerp(tickDelta, entity.prevCapeZ, entity.capeZ)
                    - MathHelper.lerp(tickDelta, entity.prevZ, entity.getZ());
            float n = MathHelper.lerpAngleDegrees(tickDelta, entity.prevBodyYaw, entity.bodyYaw);
            double o = MathHelper.sin(n * (float) (Math.PI / 180.0));
            double p = (-MathHelper.cos(n * (float) (Math.PI / 180.0)));
            float q = (float)e * 10.0F;
            q = MathHelper.clamp(q, -6.0F, 32.0F);
            float r = (float)(d * o + m * p) * 100.0F;
            r = MathHelper.clamp(r, 0.0F, 150.0F);
            float s = (float)(d * p - m * o) * 100.0F;
            s = MathHelper.clamp(s, -20.0F, 20.0F);
            if (r < 0.0F) {
                r = 0.0F;
            }

            float t = MathHelper.lerp(tickDelta, entity.prevStrideDistance, entity.strideDistance);
            q += MathHelper.sin(MathHelper.lerp(tickDelta, entity.prevHorizontalSpeed, entity.horizontalSpeed) * 6.0F) * 32.0F * t;
            if (entity.isInSneakingPose()) {
                q += 25.0F;
            }

            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(6.0F + r / 2.0F + q));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(s / 2.0F));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F - s / 2.0F));

//            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(32.5f));
//            matrices.translate(0, 0.0, -0.15);
        }

        ActionResult result = WeaponSlotCallback.EVENT.invoker().interact(entity, stack);
        if (result == ActionResult.FAIL) {
            matrices.translate(0.0, 0.25, 0.275);
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
            matrices.scale(1.5f, 1.5f, 1.5f);
        } else {
            matrices.translate(0, 0.35, 0.25);
        }

        MinecraftClient.getInstance().getItemRenderer().renderItem(entity, stack, ModelTransformationMode.FIXED, false, matrices, vertexConsumers, entity.getWorld(), light, OverlayTexture.DEFAULT_UV, 0);
        matrices.pop();
    }
}
