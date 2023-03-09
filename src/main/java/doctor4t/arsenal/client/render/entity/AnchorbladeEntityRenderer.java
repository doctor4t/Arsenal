package doctor4t.arsenal.client.render.entity;

import com.mojang.blaze3d.vertex.VertexConsumer;
import doctor4t.arsenal.common.Arsenal;
import doctor4t.arsenal.common.entity.AnchorbladeEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
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
	public void render(AnchorbladeEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
		// entity
		float yawAngle = MathHelper.lerp(tickDelta, entity.prevYaw, entity.getYaw()) - 90.0F;
		float pitchAngle = MathHelper.lerp(tickDelta, entity.prevPitch, entity.getPitch()) + 90f;

		matrices.push();
		matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(yawAngle));
		matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(pitchAngle));

		matrices.translate(0, 1.5, 0);
		matrices.scale(-1.0F, -1.0F, 1.0F);
		this.model.setAngles(entity, tickDelta, 0.0F, -0.1F, 0.0F, 0.0F);
		this.model.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(this.getTexture(entity))), light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 0.3F);
		matrices.pop();

		if (entity.getOwner() instanceof LivingEntity owner) {
			Vec3d entityPos = entity.getLerpedPos(tickDelta);

			// set up chain attachment point
			matrices.push();
			matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(yawAngle));
			matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(pitchAngle));
			matrices.translate(0, 1.5, 0);
			matrices.scale(-1.0F, -1.0F, 1.0F);

			Vector4f attachmentPos = new Vector4f(this.model.getAttachmentPosition());
			attachmentPos.transform(matrices.peek().getModel());
			matrices.pop();

			// do the actual rendering
			matrices.push();
			//get back to absolute world coordinates
			matrices.translate(-entityPos.getX(), -entityPos.getY(), -entityPos.getZ());



			// render chain
			VertexConsumer vertices = vertexConsumers.getBuffer(CHAIN_LAYER);
//			VertexConsumer vertices = vertexConsumers.getBuffer(RenderLayer.getLines());
			int chainLight = LightmapTextureManager.pack(getBlockLight(entity, owner.getBlockPos()), getSkyLight(entity, owner.getBlockPos()));

			Vec3d leashPosWorld = owner.getLeashHoldPosition(tickDelta);
			Vector4f leashPosRendering = new Vector4f((float) leashPosWorld.getX(), (float) leashPosWorld.getY(), (float) leashPosWorld.getZ(), 1.0F);
			leashPosRendering.transform(matrices.peek().getModel());

			//TODO debug
//			var lines = vertexConsumers.getBuffer(RenderLayer.getLines());
//			lines.vertex(attachmentPos.getX(), attachmentPos.getY(), attachmentPos.getZ()).color(1.0F, 0.0F, 0.0F, 1.0F).normal(matrices.peek().getNormal(), 0, 0, 1).next();
//			lines.vertex(leashPosRendering.getX(), leashPosRendering.getY(), leashPosRendering.getZ()).color(1.0F, 0.0F, 0.0F, 1.0F).normal(matrices.peek().getNormal(), 0, 0, 1).next();

			renderChain(new Vec3f(leashPosRendering.getX(), leashPosRendering.getY(), leashPosRendering.getZ()), new Vec3f(attachmentPos.getX(), attachmentPos.getY(), attachmentPos.getZ()), matrices, vertices, OverlayTexture.DEFAULT_UV, chainLight, 1.0F, 1.0F, 1.0F, 1.0F);
			matrices.pop();
		}

		super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
	}

	public static void renderChain(Vec3f start, Vec3f end, MatrixStack matrices, VertexConsumer vertexConsumer, int overlay, int light, float red, float green, float blue, float alpha) {
		Vec3f diff = end.copy();
		diff.subtract(start);

//		matrices.push();
//		matrices.translate(start.getX(), start.getY(), start.getZ());
//		matrices.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion((float) (-Math.atan2(diff.getZ(), diff.getX())) - 1.5707964F));
//		matrices.multiply(Vec3f.POSITIVE_X.getRadialQuaternion((float) -Math.atan2(MathHelper.sqrt(diff.getX() * diff.getX() + diff.getZ() * diff.getZ()), diff.getY())));
//		matrices.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion(MathHelper.HALF_PI));
//
//		float vertX1 = 0.0F;
//		float vertY1 = 0.25F;
//		float vertX2 = 0.0F;
//		float vertY2 = 0.125F;
//
//		float length = MathHelper.sqrt(diff.getX() * diff.getX() + diff.getY() * diff.getY() + diff.getZ() * diff.getZ());
//		float minU = 0.0F;
//		float maxU = 1.0F;
//		float minV = 0.0F;
//		float maxV = length / 8F;
//
//		Matrix4f model = matrices.peek().getModel();
//		Matrix3f normal = matrices.peek().getNormal();
//
//		vertexConsumer.vertex(model, vertX1, vertY1, 0F).color(255, 255, 255, 255).uv(minU, minV).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normal, 0.0F, 1.0F, 0.0F).next();
//		vertexConsumer.vertex(model, vertX1, vertY1, length).color(255, 255, 255, 255).uv(minU, maxV).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normal, 0.0F, 1.0F, 0.0F).next();
//		vertexConsumer.vertex(model, vertX2, vertY2, length).color(255, 255, 255, 255).uv(maxU, maxV).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normal, 0.0F, 1.0F, 0.0F).next();
//		vertexConsumer.vertex(model, vertX2, vertY2, 0F).color(255, 255, 255, 255).uv(maxU, minV).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normal, 0.0F, 1.0F, 0.0F).next();
//
//
//		matrices.pop();

		Vec3f up = Vec3f.POSITIVE_Z.copy();
		Vec3f offsetStart = diff.copy();
		offsetStart.normalize();
		Vec3f offsetEnd = offsetStart.copy();

		offsetStart.scale(0.1F);
		offsetEnd.scale(0.07F);
		offsetStart.cross(up);
		offsetEnd.cross(up);

		float length = MathHelper.sqrt(diff.getX() * diff.getX() + diff.getY() * diff.getY() + diff.getZ() * diff.getZ());
		float minU = 0.0F;
		float maxU = 1.0F;
		float minV = 0.0F;
		float maxV = Math.min(length / 8F, 1.0F);
		Matrix3f normal = matrices.peek().getNormal();

		Vec3f v1 = start.copy();
		v1.subtract(offsetStart);

		Vec3f v2 = start.copy();
		v2.add(offsetStart);

		Vec3f v3 = end.copy();
		v3.add(offsetEnd);

		Vec3f v4 = end.copy();
		v4.subtract(offsetEnd);

//		vertexConsumer.vertex(v1.getX(), v1.getY(), v1.getZ()).color(1.0F, 0.0F, 0.0F, 1.0F).normal(normal, up.getX(), up.getY(), up.getZ()).next();
//		vertexConsumer.vertex(v2.getX(), v2.getY(), v2.getZ()).color(1.0F, 1.0F, 0.0F, 1.0F).normal(normal, up.getX(), up.getY(), up.getZ()).next();
//		vertexConsumer.vertex(v2.getX(), v2.getY(), v2.getZ()).color(1.0F, 1.0F, 0.0F, 1.0F).normal(normal, up.getX(), up.getY(), up.getZ()).next();
//		vertexConsumer.vertex(v3.getX(), v3.getY(), v3.getZ()).color(0.0F, 1.0F, 0.0F, 1.0F).normal(normal, up.getX(), up.getY(), up.getZ()).next();
//		vertexConsumer.vertex(v3.getX(), v3.getY(), v3.getZ()).color(0.0F, 1.0F, 0.0F, 1.0F).normal(normal, up.getX(), up.getY(), up.getZ()).next();
//		vertexConsumer.vertex(v4.getX(), v4.getY(), v4.getZ()).color(0.0F, 0.0F, 1.0F, 1.0F).normal(normal, up.getX(), up.getY(), up.getZ()).next();
//		vertexConsumer.vertex(v4.getX(), v4.getY(), v4.getZ()).color(0.0F, 0.0F, 1.0F, 1.0F).normal(normal, up.getX(), up.getY(), up.getZ()).next();
//		vertexConsumer.vertex(v1.getX(), v1.getY(), v1.getZ()).color(1.0F, 0.0F, 0.0F, 1.0F).normal(normal, up.getX(), up.getY(), up.getZ()).next();

		vertexConsumer.vertex(v1.getX(), v1.getY(), v1.getZ()).color(red, green, blue, alpha).uv(maxU, maxV).overlay(overlay).light(light).normal(normal, up.getX(), up.getY(), up.getZ()).next();
		vertexConsumer.vertex(v2.getX(), v2.getY(), v2.getZ()).color(red, green, blue, alpha).uv(minU, maxV).overlay(overlay).light(light).normal(normal, up.getX(), up.getY(), up.getZ()).next();
		vertexConsumer.vertex(v3.getX(), v3.getY(), v3.getZ()).color(red, green, blue, alpha).uv(minU, minV).overlay(overlay).light(light).normal(normal, up.getX(), up.getY(), up.getZ()).next();
		vertexConsumer.vertex(v4.getX(), v4.getY(), v4.getZ()).color(red, green, blue, alpha).uv(maxU, minV).overlay(overlay).light(light).normal(normal, up.getX(), up.getY(), up.getZ()).next();
	}

	@Override
	public Identifier getTexture(AnchorbladeEntity entity) {
		return ANCHOR_TEXTURE;
	}
}
