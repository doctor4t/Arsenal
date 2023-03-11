package doctor4t.arsenal.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import doctor4t.arsenal.common.item.CustomColorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Rarity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.amymialee.mialeemisc.util.MialeeText;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
	@Shadow
	public abstract Item getItem();

	@Inject(method = "getName", at = @At("RETURN"), cancellable = true)
	public void getName(CallbackInfoReturnable<Text> cir) {
		if (this instanceof CustomColorItem colorItem) {
			cir.setReturnValue(MialeeText.withColor(cir.getReturnValue(), colorItem.getNameColor()));
		}
	}

	@Inject(method = "getRarity", at = @At("RETURN"), cancellable = true)
	public void getRarity(CallbackInfoReturnable<Rarity> cir) {
		if (this.getItem() instanceof CustomColorItem) {
			cir.setReturnValue(Rarity.COMMON);
		}
	}

	@ModifyExpressionValue(
			method = "getTooltip",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/text/MutableText;formatted(Lnet/minecraft/util/Formatting;)Lnet/minecraft/text/MutableText;", ordinal = 0)
	)
	private MutableText arsenal$tooltipChangeItemNameColor(MutableText mutableText) {
		if (this.getItem() instanceof CustomColorItem colorItem) {
			return mutableText.setStyle(mutableText.getStyle().withColor(colorItem.getNameColor()));
		}
		return mutableText;
	}

	@ModifyExpressionValue(
			method = "toHoverableText",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/text/MutableText;formatted(Lnet/minecraft/util/Formatting;)Lnet/minecraft/text/MutableText;", ordinal = 1)
	)
	private MutableText arsenal$hoverTextChangeItemNameColor(MutableText mutableText) {
		if (this.getItem() instanceof CustomColorItem colorItem) {
			return mutableText.setStyle(mutableText.getStyle().withColor(colorItem.getNameColor()));
		}
		return mutableText;
	}
}
