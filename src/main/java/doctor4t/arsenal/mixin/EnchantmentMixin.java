package doctor4t.arsenal.mixin;

import doctor4t.arsenal.common.item.ScytheItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.FireAspectEnchantment;
import net.minecraft.enchantment.KnockbackEnchantment;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public abstract class EnchantmentMixin {
	@Inject(method = "isAcceptableItem", at = @At("RETURN"), cancellable = true)
	protected void arsenal$isAcceptableItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
		if (((Object) this instanceof FireAspectEnchantment || (Object) this instanceof KnockbackEnchantment) && stack.getItem() instanceof ScytheItem) {
			cir.setReturnValue(false);
		}
	}
}
