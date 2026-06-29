package doctor4t.arsenal.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import doctor4t.arsenal.common.util.ArsenalSlots;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerSyncHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Supplier;

@Mixin(ScreenHandler.class)
public class ScreenHandlerMixin {
	@WrapOperation(method = "addSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/collection/DefaultedList;add(Ljava/lang/Object;)Z", ordinal = 0))
	protected boolean arsenal$fuckTrinketsReaddingTheirSlotsAndOverwritingOtherSlotsImTakingBackMyFuckingSlotAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIveSpent3DaysDebuggingThisShit(DefaultedList instance, Object o, Operation<Boolean> original) {
		// survival inventory fixed slot index
		if (((Slot) o) instanceof ArsenalSlots.WeaponSlot weaponSlot) {
			weaponSlot.id = 46;
			return original.call(instance, weaponSlot);
		}
//		if (((Slot) o) instanceof ArsenalSlots.CreativeWeaponSlot creativeSlot && creativeSlot.containedSlot instanceof ArsenalSlots.WeaponSlot weaponSlot) {
//			creativeSlot.id = 47;
//			weaponSlot.id = 47;
//			return original.call(instance, creativeSlot);
//		}
		return original.call(instance, o);
	}

	@WrapOperation(method = "checkSlotUpdates", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/ScreenHandlerSyncHandler;updateSlot(Lnet/minecraft/screen/ScreenHandler;ILnet/minecraft/item/ItemStack;)V"))
	private void checkSlotUpdates(ScreenHandlerSyncHandler instance, ScreenHandler screenHandler, int slotNumber, ItemStack stack, Operation<Void> original) {
//		if (screenHandler.getSlot(slotNumber) instanceof ArsenalSlots.WeaponSlot) {
//			return;
//		}
		original.call(instance, screenHandler, slotNumber, stack);
	}
}
