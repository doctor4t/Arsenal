package doctor4t.arsenal.mixin;

import doctor4t.arsenal.common.enchantment.SpewingEnchantement;
import doctor4t.arsenal.common.item.ScytheItem;
import net.minecraft.enchantment.*;
import net.minecraft.item.BookItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
	@Inject(method = "getPossibleEntries", at = @At("RETURN"), cancellable = true)
	private static void arsenal$noFireAspectOrKnockbackOnScythe(int power, ItemStack stack, boolean treasureAllowed, CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir) {
		if (stack.getItem() instanceof ScytheItem) {
			List<EnchantmentLevelEntry> possibleEntries = cir.getReturnValue();
			for (int i = possibleEntries.size() - 1; i >= 0; i--) {
				Enchantment enchantment = possibleEntries.get(i).enchantment;
				if (enchantment instanceof FireAspectEnchantment || enchantment instanceof KnockbackEnchantment) {
					possibleEntries.remove(i);
				}
			}
			cir.setReturnValue(possibleEntries);
		} else if (!(stack.getItem() instanceof BookItem)) {
			List<EnchantmentLevelEntry> possibleEntries = cir.getReturnValue();
			for (int i = possibleEntries.size() - 1; i >= 0; i--) {
				Enchantment enchantment = possibleEntries.get(i).enchantment;
				if (enchantment instanceof SpewingEnchantement) {
					possibleEntries.remove(i);
				}
			}
			cir.setReturnValue(possibleEntries);
		}
	}
}
