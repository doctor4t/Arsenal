package doctor4t.arsenal.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class BloodBubbleParticle extends SpriteBillboardParticle {
    private final SpriteProvider spriteProvider;

    public BloodBubbleParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.spriteProvider = spriteProvider;
        this.setSpriteForAge(spriteProvider);
        this.scale *= 0.25f + random.nextFloat() * 0.50f;
    }

    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_LIT;
    }

    public void tick() {
        this.setSpriteForAge(spriteProvider);

        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        if (this.age++ >= this.maxAge) {
            this.markDead();
            return;
        }
        this.velocityY = 0;
        this.move(this.velocityX, this.velocityY, this.velocityZ);
        if (this.yMotionBlockedSpeedUp && this.y == this.prevPosY) {
            this.velocityX *= 1.1;
            this.velocityZ *= 1.1;
        }
//        this.velocityX *= this.velocityMultiplier;
//        this.velocityY *= this.velocityMultiplier;
//        this.velocityZ *= this.velocityMultiplier;
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return new BloodBubbleParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
        }
    }

}
