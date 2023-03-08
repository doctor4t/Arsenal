package doctor4t.arsenal.mixin;

import doctor4t.arsenal.common.util.WeaponSlotHolder;
import doctor4t.arsenal.common.util.WeaponSlotToggle;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin implements WeaponSlotToggle, WeaponSlotHolder {
	@Shadow @Final public PlayerEntity player;

	@Unique private boolean selectedWeapon = false;
	@Unique private final SimpleInventory weapon = new SimpleInventory(1);

	@Inject(method = "getMainHandStack", at = @At("HEAD"), cancellable = true)
	private void arsenal$mainHandSlot(CallbackInfoReturnable<ItemStack> cir) {
		if (this.selectedWeapon) {
			if (!this.weapon.isEmpty()) {
				cir.setReturnValue(this.weapon.getStack(0));
			} else {
				this.selectedWeapon = false;
			}
		}
	}

	@Inject(method = "updateItems", at = @At("TAIL"))
	private void arsenal$selectSlot(CallbackInfo ci) {
		if (!this.weapon.isEmpty()) {
			this.weapon.getStack(0).inventoryTick(this.player.world, this.player, 0, this.selectedWeapon);
		}
	}

	@Inject(method = "getBlockBreakingSpeed", at = @At("HEAD"), cancellable = true)
	private void arsenal$slotBreaking(BlockState block, CallbackInfoReturnable<Float> cir) {
		if (this.selectedWeapon) {
			if (!this.weapon.isEmpty()) {
				cir.setReturnValue(this.weapon.getStack(0).getMiningSpeedMultiplier(block));
			} else {
				this.selectedWeapon = false;
			}
		}
	}

	@Inject(method = "addPickBlock", at = @At("HEAD"))
	private void arsenal$nonPick(CallbackInfo ci) {
		this.selectedWeapon = false;
	}

	@Inject(method = "swapSlotWithHotbar", at = @At("HEAD"))
	private void arsenal$nonSwap(int slot, CallbackInfo ci) {
		this.selectedWeapon = false;
	}

	@Inject(method = "scrollInHotbar", at = @At("HEAD"))
	private void arsenal$nonScroll(double scrollAmount, CallbackInfo ci) {
		this.selectedWeapon = false;
	}

	@Inject(method = "clone", at = @At("HEAD"))
	private void arsenal$cloned(PlayerInventory playerInventory, CallbackInfo ci) {
		if (playerInventory instanceof WeaponSlotToggle selection) {
			this.selectedWeapon = selection.arsenal$shouldWeaponSlot();
		}
	}

	@Override
	public void arsenal$setWeaponSlot(boolean weaponSlot) {
		this.selectedWeapon = weaponSlot;
	}

	@Override
	public boolean arsenal$shouldWeaponSlot() {
		return this.selectedWeapon;
	}

	@Override
	public void arsenal$setWeapon(ItemStack weapon) {
		this.weapon.setStack(0, weapon);
	}

	@Override
	public SimpleInventory arsenal$getWeaponSlot() {
		return this.weapon;
	}

	@Override
	public ItemStack arsenal$getWeapon() {
		return this.weapon.getStack(0);
	}
}
