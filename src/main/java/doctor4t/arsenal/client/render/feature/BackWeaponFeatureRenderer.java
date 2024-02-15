package doctor4t.arsenal.client.render.feature;

import doctor4t.arsenal.common.components.BackWeaponComponent;
import doctor4t.arsenal.common.util.WeaponSlotCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;

public class BackWeaponFeatureRenderer<T extends PlayerEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
	public BackWeaponFeatureRenderer(FeatureRendererContext<T, M> context) {
		super(context);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
		if (BackWeaponComponent.isHoldingBackWeapon(entity)) return;
		ItemStack stack = BackWeaponComponent.getBackWeapon(entity);
		if (stack.isEmpty()) return;
		ActionResult result = WeaponSlotCallback.EVENT.invoker().interact(entity, stack);
		if (result == ActionResult.FAIL) return;
		matrices.push();
		matrices.translate(0, 0.35, 0.25);
		MinecraftClient.getInstance().getItemRenderer().renderItem(entity, stack, ModelTransformation.Mode.FIXED, false, matrices, vertexConsumers, entity.world, light, OverlayTexture.DEFAULT_UV,0);
		matrices.pop();
	}
}
