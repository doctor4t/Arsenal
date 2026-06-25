package doctor4t.arsenal.client.render.item;

import com.google.common.collect.ImmutableList;
import doctor4t.arsenal.common.Arsenal;
import doctor4t.arsenal.common.item.GuillotineItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GuillotineGUIHeldVaryingItemRenderer extends GUIHeldVaryingItemRenderer {
	private static final Set<ModelTransformation.Mode> inventoryModes = Set.of(ModelTransformation.Mode.GUI, ModelTransformation.Mode.GROUND);
	private List<Pair<BakedModel, BakedModel>> models;

	public GuillotineGUIHeldVaryingItemRenderer(Identifier weaponId) {
		super(weaponId);
	}

	@Override
	public void reload(ResourceManager manager) {
		super.reload(manager);

		final MinecraftClient client = MinecraftClient.getInstance();

		ArrayList<Pair<BakedModel, BakedModel>> models = new ArrayList<>();
		for (String variation : Arsenal.GUILLOTINE_VARIATIONS) {
			models.add(
				Arsenal.GUILLOTINE_VARIATIONS.indexOf(variation),
				new Pair<>(
					client.getBakedModelManager().getModel(new ModelIdentifier(this.weaponId.getNamespace(), this.weaponId.getPath() + "_gui_" + variation, "inventory")),
					client.getBakedModelManager().getModel(new ModelIdentifier(this.weaponId.getNamespace(), this.weaponId.getPath() + "_handheld_" + variation, "inventory"))
				)
			);
		}
		this.models = ImmutableList.copyOf(models);
	}

	@Override
	public void render(ItemStack stack, ModelTransformation.Mode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		matrices.pop();
		matrices.push();

		int guillotineMode = GuillotineItem.getGuillotineMode(stack);
		Pair<BakedModel, BakedModel> modelPair = this.models.get(guillotineMode);
		BakedModel inventoryWeaponModel = modelPair.getLeft();
		BakedModel worldWeaponModel = modelPair.getRight();

		if (inventoryModes.contains(mode)) {
			this.itemRenderer.renderItem(stack, mode, false, matrices, vertexConsumers, light, overlay, inventoryWeaponModel);
		} else {
			boolean leftHanded;
			switch (mode) {
				case FIRST_PERSON_LEFT_HAND, THIRD_PERSON_LEFT_HAND -> leftHanded = true;
				default -> leftHanded = false;
			}
			this.itemRenderer.renderItem(stack, mode, leftHanded, matrices, vertexConsumers, light, overlay, worldWeaponModel);
		}
	}
}
