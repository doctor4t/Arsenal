package doctor4t.arsenal.client.render.entity;

import com.mojang.blaze3d.vertex.VertexConsumer;
import doctor4t.arsenal.common.Arsenal;
import doctor4t.arsenal.common.entity.BloodScytheEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

public class BloodScytheEntityRenderer<T extends BloodScytheEntity> extends EntityRenderer<T> {
	public static final Identifier TEXTURE = new Identifier(Arsenal.MOD_ID, "textures/entity/blood_scythe.png");

	public BloodScytheEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
	}

	@Override
	public void render(T bloodScythe, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		matrixStack.push();
		matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(MathHelper.lerp(g, bloodScythe.prevYaw, bloodScythe.getYaw()) - 90.0f));
		matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(MathHelper.lerp(g, bloodScythe.prevPitch, bloodScythe.getPitch())));
		matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(45.0f));
		matrixStack.scale(0.4f, 0.4f, 0.4f);
		matrixStack.translate(-4.0, 0.0, 0.0);
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutout(this.getTexture(bloodScythe)));
		MatrixStack.Entry entry = matrixStack.peek();
		Matrix4f matrix4f = entry.getModel();
		Matrix3f matrix3f = entry.getNormal();
		for (int u = 0; u < 4; ++u) {
			matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90f));
		}
		matrixStack.pop();
		super.render(bloodScythe, f, g, matrixStack, vertexConsumerProvider, i);
	}

	@Override
	public Identifier getTexture(T entity) {
		return TEXTURE;
	}

	public void vertex(Matrix4f positionMatrix, Matrix3f normalMatrix, VertexConsumer vertexConsumer, int x, int y, int z, float u, float v, int normalX, int normalZ, int normalY, int light) {
		vertexConsumer.vertex(positionMatrix, x, y, z).color(255, 255, 255, 255).uv(u, v).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normalMatrix, normalX, normalY, normalZ).next();
	}
}

