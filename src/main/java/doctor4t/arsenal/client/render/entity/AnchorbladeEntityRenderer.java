package doctor4t.arsenal.client.render.entity;

import com.mojang.blaze3d.vertex.VertexConsumer;
import doctor4t.arsenal.common.Arsenal;
import doctor4t.arsenal.common.entity.AnchorbladeEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

public class AnchorbladeEntityRenderer extends EntityRenderer<AnchorbladeEntity> {
	private static final Identifier ANCHOR_TEXTURE = Arsenal.id("textures/item/lux_anchorblade.png");
	private static final Identifier CHAIN_TEXTURE = Arsenal.id("textures/entity/lux_chain.png");
	private static final RenderLayer CHAIN_LAYER = RenderLayer.getEntitySmoothCutout(CHAIN_TEXTURE);
	private final AnchorBladeEntityModel model;

	public AnchorbladeEntityRenderer(EntityRendererFactory.Context ctx) {
		super(ctx);
		this.model = new AnchorBladeEntityModel(ctx.getPart(ModEntityModelLayers.ANCHORBLADE));
	}

	@Override
	public void render(AnchorbladeEntity anchorbladeEntity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
		// entity
		float yawAngle = MathHelper.lerp(tickDelta, anchorbladeEntity.prevYaw, anchorbladeEntity.getYaw()) - 90.0F;
		float pitchAngle = MathHelper.lerp(tickDelta, anchorbladeEntity.prevPitch, anchorbladeEntity.getPitch()) + 90f;
		Vec3d rotationVector = this.getRotationVector(pitchAngle, yawAngle);

		matrices.push();
		matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(yawAngle));
		matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(pitchAngle));

		matrices.translate(0, 1.5, 0);
		matrices.scale(-1.0F, -1.0F, 1.0F);
		this.model.setAngles(anchorbladeEntity, tickDelta, 0.0F, -0.1F, 0.0F, 0.0F);
		this.model.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(this.getTexture(anchorbladeEntity))), light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1F);

		matrices.pop();

		// chain
		if (anchorbladeEntity.getOwner() instanceof PlayerEntity player) {
			matrices.push();
			Vec3d anchorbladePos = rotationVector.multiply(1.5f);

			Vec3d playerPos = player.getLeashHoldPosition(tickDelta);
			float distanceX = (float) (playerPos.getX() - anchorbladeEntity.getX());
			float distanceY = (float) (playerPos.getY() - anchorbladeEntity.getY());
			float distanceZ = (float) (playerPos.getZ() - anchorbladeEntity.getZ());

			this.renderChain(distanceX, distanceY, distanceZ, tickDelta, anchorbladeEntity.age, matrices, vertexConsumers, light, anchorbladePos);
			matrices.pop();
		}

		super.render(anchorbladeEntity, yaw, tickDelta, matrices, vertexConsumers, light);
	}

	protected final Vec3d getRotationVector(float pitch, float yaw) {
		float f = pitch * (float) (Math.PI / 180.0);
		float g = -yaw * (float) (Math.PI / 180.0);
		float h = MathHelper.cos(g);
		float i = MathHelper.sin(g);
		float j = MathHelper.cos(f);
		float k = MathHelper.sin(f);
		return new Vec3d(i * j, -k, h * j);
	}

	public void renderChain(float x, float y, float z, float tickDelta, int age, MatrixStack stack, VertexConsumerProvider provider, int light, Vec3d anchorbladePos) {
		float lengthXY = MathHelper.sqrt(x * x + z * z);
		float squaredLength = x * x + y * y + z * z;
		float length = MathHelper.sqrt(squaredLength);

		stack.push();
		stack.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion((float) (-Math.atan2(z, x)) - 1.5707964F));
		stack.multiply(Vec3f.POSITIVE_X.getRadialQuaternion((float) (-Math.atan2(lengthXY, y)) - 1.5707964F));
		stack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(90));

		VertexConsumer vertexConsumer = provider.getBuffer(CHAIN_LAYER);
		float vertX1 = 0F;
		float vertY1 = 0.25F;
		float vertX2 = MathHelper.sin(6.2831855F) * 0.125F;
		float vertY2 = MathHelper.cos(6.2831855F) * 0.125F;
		float minU = 0F;
		float maxU = 1;
		float minV = 0.0F;
		float maxV = MathHelper.sqrt(squaredLength) / 8F;
		MatrixStack.Entry entry = stack.peek();
		Matrix4f matrix4f = entry.getModel();
		Matrix3f matrix3f = entry.getNormal();

		stack.translate(0, -0.52, anchorbladePos.getZ());
		stack.scale(3, 3, 1);

		vertexConsumer.vertex(matrix4f, vertX1, vertY1, 0F).color(255, 255, 255, 255).uv(minU, minV).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).next();
		vertexConsumer.vertex(matrix4f, vertX1, vertY1, length).color(255, 255, 255, 255).uv(minU, maxV).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).next();
		vertexConsumer.vertex(matrix4f, vertX2, vertY2, length).color(255, 255, 255, 255).uv(maxU, maxV).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).next();
		vertexConsumer.vertex(matrix4f, vertX2, vertY2, 0F).color(255, 255, 255, 255).uv(maxU, minV).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).next();

		stack.pop();
	}

	@Override
	public Identifier getTexture(AnchorbladeEntity entity) {
		return ANCHOR_TEXTURE;
	}
}
