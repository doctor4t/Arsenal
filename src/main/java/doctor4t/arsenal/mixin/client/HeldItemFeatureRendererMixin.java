package doctor4t.arsenal.mixin.client;

import doctor4t.arsenal.common.init.ModEnchantments;
import doctor4t.arsenal.common.init.ModItems;
import doctor4t.arsenal.common.util.AnchorOwner;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemFeatureRenderer.class)
public abstract class HeldItemFeatureRendererMixin {
    @Inject(method = "renderItem", at = @At(value = "HEAD"), cancellable = true)
    private void arsenal$thrown(LivingEntity entity, ItemStack stack, ModelTransformation.Mode transformationMode, Arm arm, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
		if (stack.isOf(ModItems.ANCHORBLADE)) {
			boolean reeling = EnchantmentHelper.getLevel(ModEnchantments.REELING, stack) > 0;
			if (entity instanceof AnchorOwner owner && owner.arsenal$isAnchorActive(reeling)) {
				ci.cancel();
			}
		}
    }
}
