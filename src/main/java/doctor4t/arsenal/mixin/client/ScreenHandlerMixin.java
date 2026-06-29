package doctor4t.arsenal.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.emi.trinkets.SurvivalTrinketSlot;
import doctor4t.arsenal.common.util.WeaponSlot;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ScreenHandler.class)
public class ScreenHandlerMixin {
	@WrapOperation(method = "addSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/collection/DefaultedList;add(Ljava/lang/Object;)Z", ordinal = 0))
	protected boolean arsenal$fuckTrinketsReaddingTheirSlotsAndOverwritingOtherSlotsImTakingBackMyFuckingSlotAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIveSpent3DaysDebuggingThisShit(DefaultedList instance, Object o, Operation<Boolean> original) {
		if (((Slot)o) instanceof WeaponSlot weaponSlot) {
			weaponSlot.id = 46;
			return original.call(instance, weaponSlot);
		}
		return original.call(instance, o);
	}
}
