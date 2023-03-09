package doctor4t.arsenal.mixin;

import doctor4t.arsenal.common.init.ModSoundEvents;
import doctor4t.arsenal.common.item.AnchorbladeItem;
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

	@Shadow public abstract float getAttackCooldownProgress(float baseTime);
	@Shadow public abstract PlayerInventory getInventory();

	@Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getAttackCooldownProgress(F)F"))
	private void arsenal$anchorSweep(Entity target, CallbackInfo ci) {
		if (this.getMainHandStack().getItem() instanceof AnchorbladeItem) {
			if (this.getAttackCooldownProgress(0.5F) > 0.9F) {
				AnchorbladeItem.spawnSweepParticles((PlayerEntity) (Object) this);
				this.world.playSound(null, this.getX(), this.getY(), this.getZ(), ModSoundEvents.ANCHORBLADE_HIT, this.getSoundCategory(), 1.0F, 1.0F);
			}
		}
	}

	@Inject(method = "getBlockBreakingSpeed", at = @At("RETURN"), cancellable = true)
	public void arsenal$anchorBreak(BlockState block, CallbackInfoReturnable<Float> cir) {
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
}
