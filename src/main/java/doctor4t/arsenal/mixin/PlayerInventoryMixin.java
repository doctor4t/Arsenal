package doctor4t.arsenal.mixin;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import doctor4t.arsenal.common.components.BackWeaponComponent;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.swing.text.html.StyleSheet;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {
	@Shadow @Final public PlayerEntity player;

	@Shadow
	@Final
	@Mutable
	private List<DefaultedList<ItemStack>> combinedInventory;

	@Inject(method = "<init>", at = @At("TAIL"))
	public void arsenal$addBackSlotToCombinedInventory(PlayerEntity player, CallbackInfo ci) {
//		List<DefaultedList<ItemStack>> newCombined = new ArrayList<>(this.combinedInventory);
//		newCombined.add(BackWeaponComponent.getBackWeaponInventory(player).stacks);
//		this.combinedInventory = ImmutableList.copyOf(newCombined);
	}

	@WrapOperation(method = "remove", at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/Inventories;remove(Lnet/minecraft/inventory/Inventory;Ljava/util/function/Predicate;IZ)I", ordinal = 1))
	public int arsenal$removeBackSlot(Inventory inventory, Predicate<ItemStack> shouldRemove, int maxCount, boolean dryRun, Operation<Integer> original) {
		return original.call(BackWeaponComponent.getBackWeaponInventory(player), shouldRemove, maxCount, dryRun) + original.call(inventory, shouldRemove, maxCount, dryRun);
	}

//	@Inject(method = "removeOne", at = @At(value = "TAIL"), cancellable = true)
//	public void arsenal$removeOneBackSlot(ItemStack stack, CallbackInfo ci) {
//		for (ItemStack defaultedList : BackWeaponComponent.getBackWeaponInventory(player).stacks) {
//			for (int i = 0; i < defaultedList.size(); i++) {
//				if (defaultedList.get(i) == stack) {
//					defaultedList.set(i, ItemStack.EMPTY);
//					break;
//				}
//			}
//		}
//	}


	@Inject(method = "getMainHandStack", at = @At("HEAD"), cancellable = true)
	private void arsenal$mainHandSlot(CallbackInfoReturnable<ItemStack> cir) {
		if (BackWeaponComponent.isHoldingBackWeapon(this.player)) {
			if (!BackWeaponComponent.getBackWeapon(this.player).isEmpty()) {
				cir.setReturnValue(BackWeaponComponent.getBackWeapon(this.player));
			} else {
				BackWeaponComponent.setHoldingBackWeapon(this.player, false);
			}
		}
	}

	@Inject(method = "updateItems", at = @At("TAIL"))
	private void arsenal$selectSlot(CallbackInfo ci) {
		if (!BackWeaponComponent.getBackWeapon(this.player).isEmpty()) {
			BackWeaponComponent.getBackWeapon(this.player).inventoryTick(this.player.world, this.player, 0, BackWeaponComponent.isHoldingBackWeapon(this.player));
		}
	}

	@Inject(method = "getBlockBreakingSpeed", at = @At("HEAD"), cancellable = true)
	private void arsenal$slotBreaking(BlockState block, CallbackInfoReturnable<Float> cir) {
		if (BackWeaponComponent.isHoldingBackWeapon(this.player)) {
			if (!BackWeaponComponent.getBackWeapon(this.player).isEmpty()) {
				cir.setReturnValue(BackWeaponComponent.getBackWeapon(this.player).getMiningSpeedMultiplier(block));
			} else {
				BackWeaponComponent.setHoldingBackWeapon(this.player, false);
			}
		}
	}

	@Inject(method = "addPickBlock", at = @At("HEAD"))
	private void arsenal$nonPick(CallbackInfo ci) {
		BackWeaponComponent.setHoldingBackWeapon(this.player, false);
	}

	@Inject(method = "swapSlotWithHotbar", at = @At("HEAD"))
	private void arsenal$nonSwap(int slot, CallbackInfo ci) {
		BackWeaponComponent.setHoldingBackWeapon(this.player, false);
	}

	@Inject(method = "scrollInHotbar", at = @At("HEAD"))
	private void arsenal$nonScroll(double scrollAmount, CallbackInfo ci) {
		BackWeaponComponent.setHoldingBackWeapon(this.player, false);
	}
	@Inject(method = "dropAll", at = @At("TAIL"))
	private void arsenal$dropBackslot(CallbackInfo ci) {
		this.player.dropItem(BackWeaponComponent.getBackWeapon(this.player).copy(), true, false);
		BackWeaponComponent.setBackWeapon(this.player, ItemStack.EMPTY);
	}
}
