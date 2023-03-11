package doctor4t.arsenal.mixin.client;

import doctor4t.arsenal.client.render.feature.AmariteLongswordFeatureRenderer;
import doctor4t.arsenal.client.render.feature.AnchorbladeFeatureRenderer;
import doctor4t.arsenal.client.render.feature.BackWeaponFeatureRenderer;
import doctor4t.arsenal.client.render.feature.ClownScytheFeatureRenderer;
import doctor4t.arsenal.common.init.ModEnchantments;
import doctor4t.arsenal.common.init.ModItems;
import doctor4t.arsenal.common.util.AnchorOwner;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.quiltmc.loader.api.QuiltLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
	public PlayerEntityRendererMixin(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
		super(ctx, model, shadowRadius);
	}

	@Inject(method = "getArmPose", at = @At("HEAD"), cancellable = true)
	private static void amarite$swordPoses(AbstractClientPlayerEntity player, Hand hand, CallbackInfoReturnable<BipedEntityModel.ArmPose> cir) {
		ItemStack stack = player.getStackInHand(hand);
		if (stack.isOf(ModItems.ANCHORBLADE)) {
			boolean reeling = EnchantmentHelper.getLevel(ModEnchantments.REELING, stack) > 0;
			if (player instanceof AnchorOwner owner && owner.arsenal$isAnchorActive(reeling)) {
				cir.setReturnValue(BipedEntityModel.ArmPose.EMPTY);
			}
		}
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	public void arsenal$backBlade(EntityRendererFactory.Context ctx, boolean slim, CallbackInfo ci) {
		this.addFeature(new BackWeaponFeatureRenderer<>(this));
		this.addFeature(new AnchorbladeFeatureRenderer<>(this));
		this.addFeature(new ClownScytheFeatureRenderer<>(this));
		if (QuiltLoader.isModLoaded("amarite")) this.addFeature(new AmariteLongswordFeatureRenderer<>(this));
	}
}
