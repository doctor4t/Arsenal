package doctor4t.arsenal.mixin.client;

import doctor4t.arsenal.client.render.feature.AnchorbladeFeatureRenderer;
import doctor4t.arsenal.client.render.feature.BackWeaponFeatureRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
	public PlayerEntityRendererMixin(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
		super(ctx, model, shadowRadius);
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	public void arsenal$backBlade(EntityRendererFactory.Context ctx, boolean slim, CallbackInfo ci) {
		this.addFeature(new BackWeaponFeatureRenderer<>(this));
		this.addFeature(new AnchorbladeFeatureRenderer<>(this));
	}
}
