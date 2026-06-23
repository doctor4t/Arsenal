package doctor4t.arsenal.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import doctor4t.arsenal.common.entity.AnchorbladeEntity;
import doctor4t.arsenal.common.item.*;
import doctor4t.arsenal.common.util.AnchorOwner;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("WrongEntityDataParameterClass")
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements AnchorOwner {
	@Unique private static final TrackedData<Integer> BASIC_ANCHOR = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);
	@Unique private static final TrackedData<Integer> REELING_ANCHOR = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);

	protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	@Shadow public abstract float getAttackCooldownProgress(float baseTime);

	@Inject(method = "initDataTracker", at = @At("TAIL"))
	private void arsenal$initDataTracker(CallbackInfo ci) {
		this.dataTracker.startTracking(BASIC_ANCHOR, -1);
		this.dataTracker.startTracking(REELING_ANCHOR, -1);
	}

	@Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getAttackCooldownProgress(F)F"))
	private void arsenal$spawnCustomHitParticlesAndPlayCustomHitSound(Entity target, CallbackInfo ci) {
		if (this.getAttackCooldownProgress(0.5F) > 0.9F) {
			if (this.getMainHandStack().getItem() instanceof CustomHitParticleItem customHitParticleItem) {
				customHitParticleItem.spawnHitParticles((PlayerEntity) (Object) this);
			}
			if (this.getMainHandStack().getItem() instanceof CustomHitSoundItem customHitSoundItem) {
				customHitSoundItem.playHitSound((PlayerEntity) (Object) this);
			}
		}
	}

	@Inject(method = "getBlockBreakingSpeed", at = @At("RETURN"), cancellable = true)
	public void arsenal$multiplyAnchorbladeMiningSpeedUnderwater(BlockState block, CallbackInfoReturnable<Float> cir) {
		if (this.getMainHandStack().getItem() instanceof AnchorbladeItem && this.isSubmergedIn(FluidTags.WATER)) {
			cir.setReturnValue(cir.getReturnValue() * 2f);
		}
	}

	@Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;addCritParticles(Lnet/minecraft/entity/Entity;)V"))
	private void arsenal$scytheReapTargetOnCrit(Entity target, CallbackInfo ci) {
		ItemStack mainHandStack = this.getStackInHand(Hand.MAIN_HAND);
		if (mainHandStack.getItem() instanceof ReapingItem reapingItem) {
			float reapingVelocityMultiplier = reapingItem.getReapingVelocityMultiplier(mainHandStack);
			if (reapingVelocityMultiplier != 0f) {
				target.setVelocity(this.getPos().subtract(target.getPos()).multiply(reapingVelocityMultiplier));
				target.velocityModified = true;
			}
		}
	}

	@WrapOperation(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
	private boolean arsenal$doGuillotineGild(Entity instance, DamageSource source, float amount, Operation<Boolean> original) {
		if (instance instanceof LivingEntity
			&& source.getAttacker() instanceof LivingEntity livingAttacker
			&& GuillotineItem.isGuillotineAndMode(livingAttacker.getMainHandStack(), GuillotineItem.GILD_MODE)) {
			float appliedDamage = livingAttacker.modifyAppliedDamage(source, amount);
			livingAttacker.timeUntilRegen = 5;
			livingAttacker.setAbsorptionAmount(livingAttacker.getAbsorptionAmount() + appliedDamage * .2f);
		}

		return original.call(instance, source, amount);
	}

	@Override
	public void arsenal$setAnchor(AnchorbladeEntity anchor) {
		boolean reeling = anchor.hasReeling();
		this.dataTracker.set(reeling ? REELING_ANCHOR : BASIC_ANCHOR, anchor.getId());
	}

	@Override
	public AnchorbladeEntity arsenal$getAnchor(boolean reeling) {
		return this.world.getEntityById(reeling ? this.dataTracker.get(REELING_ANCHOR) : this.dataTracker.get(BASIC_ANCHOR)) instanceof AnchorbladeEntity anchor ? anchor : null;
	}

	@Override
	public boolean arsenal$isAnchorActive(boolean reeling) {
		AnchorbladeEntity anchor = this.arsenal$getAnchor(reeling);
		return anchor != null && anchor.isAlive();
	}
}
