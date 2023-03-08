package doctor4t.anchorblade.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class SweepAttackParticle extends SpriteBillboardParticle {
	private final SpriteProvider spriteWithAge;

	private SweepAttackParticle(ClientWorld world, double x, double y, double z, double scale, SpriteProvider spriteWithAge) {
		super(world, x, y, z, 0.0D, 0.0D, 0.0D);
		this.spriteWithAge = spriteWithAge;
		this.maxAge = 4;
		this.scale = 1.0F - (float) scale * 0.5F;
		this.setSpriteForAge(spriteWithAge);
	}

	@Override
	protected int getBrightness(float tint) {
		return 15728880;
	}

	@Override
	public void tick() {
		this.prevPosX = this.x;
		this.prevPosY = this.y;
		this.prevPosZ = this.z;
		if (this.age++ >= this.maxAge) {
			this.markDead();
		} else {
			this.setSpriteForAge(this.spriteWithAge);
		}
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_LIT;
	}

	@Environment(EnvType.CLIENT)
	public record Factory(SpriteProvider spriteSet) implements ParticleFactory<DefaultParticleType> {
		@Override
		public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
			return new SweepAttackParticle(world, x, y, z, velocityX, this.spriteSet);
		}
	}
}
