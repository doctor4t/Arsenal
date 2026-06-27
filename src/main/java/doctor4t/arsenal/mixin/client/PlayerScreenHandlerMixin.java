package doctor4t.arsenal.mixin.client;

import doctor4t.arsenal.common.components.BackWeaponComponent;
import doctor4t.arsenal.common.util.WeaponSlot;
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
		this.addSlot(new WeaponSlot(BackWeaponComponent.getBackWeaponInventory(inventory.player), BackWeaponComponent.SLOT, 77, 26));
	}
}
