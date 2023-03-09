package doctor4t.arsenal.mixin.client;

import doctor4t.arsenal.common.init.ModEnchantments;
import doctor4t.arsenal.common.init.ModItems;
import doctor4t.arsenal.common.util.AnchorOwner;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {
	@Inject(method = "renderFirstPersonItem", at = @At("HEAD"), cancellable = true)
    public void amarite$renderDiscs(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        ItemStack stack = player.getStackInHand(hand);
		if (stack.isOf(ModItems.ANCHORBLADE)) {
			boolean reeling = EnchantmentHelper.getLevel(ModEnchantments.REELING, stack) > 0;
			if (player instanceof AnchorOwner owner && owner.arsenal$isAnchorActive(reeling)) {
				ci.cancel();
			}
		}
    }
}
