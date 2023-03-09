package doctor4t.arsenal.mixin;

import doctor4t.arsenal.common.util.WeaponSlotHolder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerScreenHandler.class)
public abstract class PlayerScreenHandlerMixin extends AbstractRecipeScreenHandler<CraftingInventory> {
	public PlayerScreenHandlerMixin(ScreenHandlerType<?> screenHandlerType, int i) {
		super(screenHandlerType, i);
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	private void arsenal$init(PlayerInventory inventory, boolean onServer, PlayerEntity owner, CallbackInfo ci) {
		if (owner.getInventory() instanceof WeaponSlotHolder holder) {
			this.addSlot(new Slot(holder.arsenal$getWeaponSlot(), 0, 77, 44));
		}
	}
}
