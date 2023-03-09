package doctor4t.arsenal.mixin;

import doctor4t.arsenal.common.util.WeaponSlotHolder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PersistentProjectileEntity.class)
public abstract class PersistentProjectileEntityMixin extends ProjectileEntityMixin {
	@Shadow public PersistentProjectileEntity.PickupPermission pickupType;
	@Shadow protected abstract ItemStack asItemStack();

	@Inject(method = "tryPickup", at = @At("HEAD"), cancellable = true)
	private void arsenal$pickupSlot(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
		if (this.arsenal$getOwnedSlot() == -1) return;
		if (this.pickupType == PersistentProjectileEntity.PickupPermission.ALLOWED) {
			if (player.getInventory() instanceof WeaponSlotHolder holder) {
				if (holder.arsenal$tryInsertIntoSlot(this.arsenal$getOwnedSlot(), this.asItemStack())) {
					cir.setReturnValue(true);
				}
			}
		}
	}
}
