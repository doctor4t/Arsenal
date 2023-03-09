package doctor4t.arsenal.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.particle.ExplosionLargeParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

public class ShockwaveParticle extends ExplosionLargeParticle {
	public ShockwaveParticle(ClientWorld world, double x, double y, double z, double d, SpriteProvider spriteProvider) {
		super(world, x, y, z, d, spriteProvider);
		this.maxAge = 8;
		this.scale = 8f;
		this.gravityStrength = 0;
		this.velocityX = 0;
		this.velocityY = 0;
		this.velocityZ = 0;
		this.colorRed = 1;
		this.colorGreen = 1;
		this.colorBlue = 1;
		this.colorAlpha = 0.5f;
		setSpriteForAge(spriteProvider);
	}


	@Override
	public float getSize(float tickDelta) {
		float d = (this.age + tickDelta) / (this.maxAge);
		return this.scale * MathHelper.clamp(d, 0, 1);
	}

	@Override
	public void tick() {
		super.tick();
	}

	@Override
	public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
		Vec3d vec3d = camera.getPos();
		float f = (float) (MathHelper.lerp(tickDelta, this.prevPosX, this.x) - vec3d.getX());
		float g = (float) (MathHelper.lerp(tickDelta, this.prevPosY, this.y) - vec3d.getY());
		float h = (float) (MathHelper.lerp(tickDelta, this.prevPosZ, this.z) - vec3d.getZ());
		Quaternion quaternion;

		quaternion = camera.getRotation();

		Vec3f[] vec3fs = new Vec3f[]{new Vec3f(-1, -1, 0), new Vec3f(-1, 1, 0), new Vec3f(1, 1, 0), new Vec3f(1, -1, 0)};
		float size = this.getSize(tickDelta);
		for (int i = 0; i < 4; ++i) {
			Vec3f vec3f = vec3fs[i];
			vec3f.rotate(quaternion);
			vec3f.scale(size);
			vec3f.add(f, g, h);
		}

		int brightness = this.getBrightness(tickDelta);
		this.colorAlpha = (float) MathHelper.lerp((float) this.age / this.getMaxAge(), 0.5, 0);

		this.vertex(vertexConsumer, vec3fs[0], this.getMaxU(), this.getMaxV(), brightness);
		this.vertex(vertexConsumer, vec3fs[1], this.getMaxU(), this.getMinV(), brightness);
		this.vertex(vertexConsumer, vec3fs[2], this.getMinU(), this.getMinV(), brightness);
		this.vertex(vertexConsumer, vec3fs[3], this.getMinU(), this.getMaxV(), brightness);
	}

	private void vertex(VertexConsumer vertexConsumer, Vec3f pos, float u, float v, int light) {
		vertexConsumer.vertex(pos.getX(), pos.getY(), pos.getZ()).uv(u, v).color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha).light(light).next();
	}

	@Override
	public int getBrightness(float tint) {
		return 240;
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
	}

	public static class Factory
			implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		@Override
		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			return new ShockwaveParticle(clientWorld, d, e, f, g, this.spriteProvider);
		}
	}
}
