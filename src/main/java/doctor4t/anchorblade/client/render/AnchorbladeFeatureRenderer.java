package doctor4t.anchorblade.client.render;

import doctor4t.anchorblade.common.item.AnchorbladeItem;
import doctor4t.anchorblade.common.util.AnchorSelection;
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
        ItemStack anchor = AnchorbladeItem.getWornAnchor(entity);
		if (anchor != ItemStack.EMPTY) {
			if (entity.getInventory() instanceof AnchorSelection selection) {
				if (selection.anchorblade$hasSelectedAnchor()) return;
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
