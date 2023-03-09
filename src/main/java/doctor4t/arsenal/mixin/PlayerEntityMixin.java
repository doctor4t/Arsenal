package doctor4t.arsenal.mixin;

import doctor4t.arsenal.common.item.AnchorbladeItem;
import doctor4t.arsenal.common.item.ScytheItem;
import doctor4t.arsenal.common.item.CustomHitParticleItem;
import doctor4t.arsenal.common.item.CustomHitSoundItem;
import doctor4t.arsenal.common.util.WeaponSlotHolder;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
	protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	@Shadow
	public abstract float getAttackCooldownProgress(float baseTime);

	@Shadow
	public abstract PlayerInventory getInventory();

	@Shadow
	public abstract void disableShield(boolean sprinting);

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

	@Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
	private void arsenal$readNbt(NbtCompound nbt, CallbackInfo ci) {
		if (this.getInventory() instanceof WeaponSlotHolder holder) {
			if (nbt.contains("arsenal$weapon")) {
				holder.arsenal$setWeapon(ItemStack.fromNbt(nbt.getCompound("arsenal$weapon")));
			}
		}
	}

	@Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
	private void arsenal$writeNbt(NbtCompound nbt, CallbackInfo ci) {
		if (this.getInventory() instanceof WeaponSlotHolder holder) {
			ItemStack weapon = holder.arsenal$getWeapon();
			if (!weapon.isEmpty()) {
				nbt.put("arsenal$weapon", weapon.writeNbt(new NbtCompound()));
			}
		}
	}

	@Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;addCritParticles(Lnet/minecraft/entity/Entity;)V"))
	private void arsenal$scytheReelTargetOnCrit(Entity target, CallbackInfo ci) {
		if (this.getStackInHand(Hand.MAIN_HAND).getItem() instanceof ScytheItem) {
			target.setVelocity(this.getPos().subtract(target.getPos()).multiply(0.25f));
			target.velocityModified = true;
		}
	}

	@Inject(method = "takeShieldHit", at = @At("HEAD"))
	protected void arsenal$scytheDisableShield(LivingEntity attacker, CallbackInfo ci) {
		if (attacker.getMainHandStack().getItem() instanceof ScytheItem) {
			this.disableShield(true);
		}
	}
}
