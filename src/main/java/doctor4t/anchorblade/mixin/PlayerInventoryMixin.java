package doctor4t.anchorblade.mixin;

import doctor4t.anchorblade.common.item.AnchorbladeItem;
import doctor4t.anchorblade.common.util.AnchorSelection;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
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
public class PlayerInventoryMixin implements AnchorSelection {
	@Shadow @Final public PlayerEntity player;
	@Unique private boolean selectedAnchor = false;

	@Inject(method = "getMainHandStack", at = @At("HEAD"), cancellable = true)
	private void anchorblade$getMainHandStack(CallbackInfoReturnable<ItemStack> cir) {
		if (this.selectedAnchor) {
			ItemStack anchorStack = AnchorbladeItem.getWornAnchor(this.player);
			if (!anchorStack.isEmpty() && anchorStack.getItem() instanceof AnchorbladeItem) {
				cir.setReturnValue(anchorStack);
			} else {
				this.selectedAnchor = false;
			}
		}
	}

	@Inject(method = "updateItems", at = @At("TAIL"))
	private void anchorblade$updateItems(CallbackInfo ci) {
		ItemStack anchorStack = AnchorbladeItem.getWornAnchor(this.player);
		if (anchorStack.isEmpty() || !(anchorStack.getItem() instanceof AnchorbladeItem)) {
			anchorStack.inventoryTick(this.player.world, this.player, 0, this.selectedAnchor);
		}
	}

	@Inject(method = "getBlockBreakingSpeed", at = @At("HEAD"), cancellable = true)
	private void anchorblade$getBlockBreakingSpeed(BlockState block, CallbackInfoReturnable<Float> cir) {
		if (this.selectedAnchor) {
			ItemStack anchorStack = AnchorbladeItem.getWornAnchor(this.player);
			if (!anchorStack.isEmpty() && anchorStack.getItem() instanceof AnchorbladeItem) {
				cir.setReturnValue(anchorStack.getMiningSpeedMultiplier(block));
			} else {
				this.selectedAnchor = false;
			}
		}
	}

	@Inject(method = "addPickBlock", at = @At("HEAD"))
	private void anchorblade$addPickBlock(CallbackInfo ci) {
		this.selectedAnchor = false;
	}

	@Inject(method = "swapSlotWithHotbar", at = @At("HEAD"))
	private void anchorblade$swapSlotWithHotbar(int slot, CallbackInfo ci) {
		this.selectedAnchor = false;
	}

	@Inject(method = "scrollInHotbar", at = @At("HEAD"))
	private void anchorblade$scrollInHotbar(double scrollAmount, CallbackInfo ci) {
		this.selectedAnchor = false;
	}

	@Inject(method = "clone", at = @At("HEAD"))
	private void anchorblade$clone(PlayerInventory playerInventory, CallbackInfo ci) {
		if (playerInventory instanceof AnchorSelection selection) {
			this.selectedAnchor = selection.anchorblade$hasSelectedAnchor();
		}
	}

	@Override
	public void anchorblade$setSelectedAnchor(boolean selectedAnchor) {
		this.selectedAnchor = selectedAnchor;
	}

	@Override
	public boolean anchorblade$hasSelectedAnchor() {
		return this.selectedAnchor;
	}
}
