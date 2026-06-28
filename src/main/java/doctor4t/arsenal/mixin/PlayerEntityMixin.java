package doctor4t.arsenal.mixin;

import com.llamalad7.mixinextras.sugar.Local;
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
			ItemStack mainHandStack = this.getMainHandStack();
			if (mainHandStack.getItem() instanceof CustomHitParticleItem customHitParticleItem) {
				customHitParticleItem.spawnHitParticle(this, target, mainHandStack);
			}
			if (mainHandStack.getItem() instanceof CustomHitSoundItem customHitSoundItem) {
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

	@Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;onAttacking(Lnet/minecraft/entity/Entity;)V"))
	private void arsenal$reapAndGild(Entity target, CallbackInfo ci, @Local(ordinal = 0) boolean isFullSwing, @Local(ordinal = 0) float damage) {
		ItemStack mainHandStack = this.getStackInHand(Hand.MAIN_HAND);
		DamageSource source = DamageSource.player((PlayerEntity) (Object) this);

		if (target instanceof LivingEntity livingEntity && livingEntity.blockedByShield(source)) {
			return;
		}

		if (isFullSwing) {
			// reaping
			if (mainHandStack.getItem() instanceof ReapingItem reapingItem) {
				float reapingVelocityMultiplier = reapingItem.getReapingVelocityMultiplier(mainHandStack);
				if (reapingVelocityMultiplier != 0f) {
					target.setVelocity(this.getPos().subtract(target.getPos()).multiply(reapingVelocityMultiplier));
					target.velocityModified = true;
				}
			}

			// guillotine gild
			if (target instanceof LivingEntity livingTarget
				&& GuillotineItem.isGuillotineAndMode(this.getMainHandStack(), GuillotineItem.GILD_MODE)) {

				this.setAbsorptionAmount(Math.min(this.getAbsorptionAmount() + damage * .4f, 20));
			}
		}
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
