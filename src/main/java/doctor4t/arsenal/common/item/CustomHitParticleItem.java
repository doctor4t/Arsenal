package doctor4t.arsenal.common.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public interface CustomHitParticleItem {
	default void spawnHitParticle(LivingEntity attacker, Entity target, ItemStack stack) {
		Vec3d targetPos = new Vec3d(target.getX(), target.getBodyY(0.8D), target.getZ());
		Vec3d diff = attacker.getPos().subtract(targetPos).normalize().multiply(0.5f);
		Vec3d partPos = targetPos.add(diff);
		if (target.world instanceof ServerWorld serverWorld) {
			serverWorld.spawnParticles(this.getHitParticle(attacker, target, stack), partPos.getX(), partPos.getY(), partPos.getZ(), 1, 0f, 0.0D, 0f, 0.0D);
		}
	}

	ParticleEffect getHitParticle(LivingEntity attacker, Entity target, ItemStack stack);
}
