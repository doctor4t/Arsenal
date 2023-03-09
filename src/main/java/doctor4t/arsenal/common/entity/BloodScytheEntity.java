package doctor4t.arsenal.common.entity;

import com.google.common.collect.Sets;
import doctor4t.arsenal.common.init.ModDamageSources;
import doctor4t.arsenal.common.init.ModEntities;
import doctor4t.arsenal.common.init.ModParticles;
import doctor4t.arsenal.common.init.ModSoundEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

import java.util.Set;

public class BloodScytheEntity extends PersistentProjectileEntity {
	private final Set<StatusEffectInstance> effects = Sets.newHashSet();
	public int ticksUntilRemove = 5;

	public BloodScytheEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
		super(entityType, world);
	}

	public BloodScytheEntity(World world, LivingEntity owner) {
		super(ModEntities.BLOOD_SCYTHE, owner, world);
	}

	@Override
	protected ItemStack asItemStack() {
		return ItemStack.EMPTY;
	}

	public void addEffect(StatusEffectInstance effect) {
		this.effects.add(effect);
	}

	@Override
	public void tick() {
		super.tick();

		for (float x = -3; x <= 3; x += 0.1) {
			world.addParticle(ModParticles.BLOOD_BUBBLE, this.getX() + x * Math.cos(getYaw()), this.getY(), this.getZ() + x * Math.sin(getYaw()), this.getVelocity().getX(), this.getVelocity().getY(), this.getVelocity().getZ());
		}

		if (this.inGround || this.age > 20) {
			for (int i = 0; i < 50; i++) {
				world.addParticle(ModParticles.BLOOD_BUBBLE_SPLATTER, this.getX() + (random.nextGaussian() * 2) * Math.cos(getYaw()), this.getY(), this.getZ() + (random.nextGaussian() * 2) * Math.sin(getYaw()), random.nextGaussian() / 10, random.nextFloat() / 2, random.nextGaussian() / 10);
			}
			ticksUntilRemove--;
		}

		if (ticksUntilRemove <= 0) {
			this.discard();
		}

		if (!world.isClient) {
			for (LivingEntity livingEntity : world.getEntitiesByClass(LivingEntity.class, this.getBoundingBox(), livingEntity -> this.getOwner() != livingEntity)) {
				livingEntity.damage(ModDamageSources.bloodScythe(this, this.getOwner()), 12.0f);
				for (StatusEffectInstance effect : effects) {
					livingEntity.addStatusEffect(effect);
				}
			}
		}
	}

	@Override
	protected SoundEvent getHitSound() {
		return ModSoundEvents.ENTITY_BLOOD_SCYTHE_HIT;
	}

	@Override
	public boolean hasNoGravity() {
		return true;
	}

	@Override
	protected void onEntityHit(EntityHitResult entityHitResult) {
	}
}
