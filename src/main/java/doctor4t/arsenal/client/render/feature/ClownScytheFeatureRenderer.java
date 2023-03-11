package doctor4t.arsenal.client.render.feature;

import doctor4t.arsenal.common.components.BackWeaponComponent;
import doctor4t.arsenal.common.init.ModItems;
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

public class ClownScytheFeatureRenderer<T extends PlayerEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
	public ClownScytheFeatureRenderer(FeatureRendererContext<T, M> context) {
		super(context);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
		if (BackWeaponComponent.isHoldingBackWeapon(entity)) return;
		ItemStack stack = BackWeaponComponent.getBackWeapon(entity);
		if (stack.isEmpty()) return;
		if (stack.getItem() != ModItems.CLOWN_SCYTHE) {
			return;
		}
		matrices.push();
		matrices.translate(0, 0, 0.275);
		matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180));
		matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180));
		matrices.scale(1.65f, 1.65f, 1.65f);
		MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Mode.FIXED, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, 0);
		matrices.pop();
	}
}
