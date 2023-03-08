package doctor4t.anchorblade.client.render;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import doctor4t.anchorblade.common.init.ModItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3f;

import java.util.Optional;

public class AnchorbladeFeatureRenderer<T extends PlayerEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
    public AnchorbladeFeatureRenderer(FeatureRendererContext<T, M> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        ItemStack anchor = getWornAnchor(entity);
		if (anchor != ItemStack.EMPTY) {
			matrices.push();
			matrices.translate(-0.1, 0.25, 0.275);
			matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(135));
			matrices.scale(1.4f, 1.4f, 1.4f);
			MinecraftClient.getInstance().getItemRenderer().renderItem(anchor, ModelTransformation.Mode.FIXED, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, 0);
			matrices.pop();
		}
    }

	private static ItemStack getWornAnchor(LivingEntity livingEntity) {
		Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(livingEntity);
		if (component.isPresent()) {
			for (Pair<SlotReference, ItemStack> pair : component.get().getAllEquipped()) {
				if (pair.getRight().isOf(ModItems.ANCHORBLADE)) {
					return pair.getRight();
				}
			}
		}
		return ItemStack.EMPTY;
	}
}
