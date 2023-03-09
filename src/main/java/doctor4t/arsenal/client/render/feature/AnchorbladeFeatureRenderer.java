package doctor4t.arsenal.client.render.feature;

import doctor4t.arsenal.common.util.WeaponSlotHolder;
import doctor4t.arsenal.common.util.WeaponSlotToggle;
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
import net.minecraft.util.math.Vec3f;

public class AnchorbladeFeatureRenderer<T extends PlayerEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
	public AnchorbladeFeatureRenderer(FeatureRendererContext<T, M> context) {
		super(context);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
		if (entity.getInventory() instanceof WeaponSlotHolder holder) {
			ItemStack anchor = holder.arsenal$getWeapon();
			if (anchor.isEmpty()) return;
			if (entity.getInventory() instanceof WeaponSlotToggle selection) {
				if (selection.arsenal$shouldWeaponSlot()) return;
			}
			matrices.push();
			matrices.translate(-0.1, 0.25, 0.275);
			matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(135));
			matrices.scale(1.4f, 1.4f, 1.4f);
			MinecraftClient.getInstance().getItemRenderer().renderItem(anchor, ModelTransformation.Mode.FIXED, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, 0);
			matrices.pop();
		}
	}
}
