package doctor4t.arsenal.client.render.entity;

import doctor4t.arsenal.common.Arsenal;
import doctor4t.arsenal.common.entity.AnchorbladeEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;

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
	public Identifier getTexture(AnchorbladeEntity entity) {
		return ANCHOR_TEXTURE;
	}

	@Override
	public void render(AnchorbladeEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
		matrices.push();
		float yawAngle = MathHelper.lerp(tickDelta, entity.prevYaw, entity.getYaw());
		float pitchAngle = MathHelper.lerp(tickDelta, entity.prevPitch, entity.getPitch());
		matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(yawAngle - 90));
		matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(pitchAngle - 90));
		matrices.translate(0, -1.45, 0);
		this.model.setAngles(entity, tickDelta, 0, -0.1F, 0, 0);
		this.model.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(this.getTexture(entity))), light, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
		matrices.pop();
		if (entity.getOwner() instanceof LivingEntity owner) {
			matrices.push();
			Vec3d pos = entity.getLerpedPos(tickDelta);
			Vec3d ringPos = new Vec3d(1.5, 0, 0).rotateZ(pitchAngle * MathHelper.RADIANS_PER_DEGREE).rotateY((yawAngle + 90) * MathHelper.RADIANS_PER_DEGREE);
			Vec3d ownerPos = owner.getLeashPos(tickDelta).subtract(pos);
			float length = (float) ringPos.distanceTo(ownerPos);
			MatrixStack.Entry matrixEntry = matrices.peek();
			Matrix4f modelMatrix = matrixEntry.getPositionMatrix();
			Matrix3f normal = matrixEntry.getNormalMatrix();
			float minU = 0;
			float maxU = 1;
			float minV = 0;
			float maxV = length / 8f;
			VertexConsumer vertexConsumer = vertexConsumers.getBuffer(CHAIN_LAYER);
			Vec3d offset = ownerPos.subtract(ringPos).normalize().multiply(0.25, 0, 0.25).rotateY((float) (Math.PI / 2));
			Vec3d vert1 = ringPos.add(offset);
			Vec3d vert2 = ownerPos.add(offset);
			Vec3d vert3 = ownerPos.subtract(offset);
			Vec3d vert4 = ringPos.subtract(offset);
			int chainLight = LightmapTextureManager.pack(this.getBlockLight(entity, owner.getBlockPos()), this.getSkyLight(entity, owner.getBlockPos()));
			this.vertex(vert1, vertexConsumer, minU, minV, modelMatrix, normal, light);
			this.vertex(vert2, vertexConsumer, minU, maxV, modelMatrix, normal, chainLight);
			this.vertex(vert3, vertexConsumer, maxU, maxV, modelMatrix, normal, chainLight);
			this.vertex(vert4, vertexConsumer, maxU, minV, modelMatrix, normal, light);
			matrices.pop();
		}
		super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
	}

	private void vertex(Vec3d vec, VertexConsumer vertexConsumer, float u, float v, Matrix4f modelMatrix, Matrix3f normal, int light) {
		vertexConsumer.vertex(modelMatrix, (float) vec.x, (float) vec.y, (float) vec.z).color(255, 255, 255, 255).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normal, 0, 1, 0).next();
	}
}
