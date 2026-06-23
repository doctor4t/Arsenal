package doctor4t.arsenal.client.render.item;

import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class GUIHeldVaryingItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer, SimpleSynchronousResourceReloadListener {
	protected static final Set<ModelTransformation.Mode> inventoryModes = Set.of(ModelTransformation.Mode.GUI, ModelTransformation.Mode.GROUND);
	protected final Identifier id;
	protected final Identifier weaponId;
	protected ItemRenderer itemRenderer;
	protected BakedModel inventoryWeaponModel;
	protected BakedModel worldWeaponModel;

	public GUIHeldVaryingItemRenderer(Identifier weaponId) {
		this.id = new Identifier(weaponId.getNamespace(), weaponId.getPath() + "_renderer");
		this.weaponId = weaponId;
	}

	@Override
	public Identifier getFabricId() {
		return this.id;
	}

	@Override
	public Collection<Identifier> getFabricDependencies() {
		return Collections.singletonList(ResourceReloadListenerKeys.MODELS);
	}

	@Override
	public void reload(ResourceManager manager) {
		final MinecraftClient client = MinecraftClient.getInstance();
		this.itemRenderer = client.getItemRenderer();
		this.inventoryWeaponModel = client.getBakedModelManager().getModel(new ModelIdentifier(this.weaponId.getNamespace(), this.weaponId.getPath() + "_gui", "inventory"));
		this.worldWeaponModel = client.getBakedModelManager().getModel(new ModelIdentifier(this.weaponId.getNamespace(), this.weaponId.getPath() + "_handheld", "inventory"));
	}

	@Override
	public void render(ItemStack stack, ModelTransformation.Mode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		matrices.pop();
		matrices.push();
		if (inventoryModes.contains(mode)) {
			this.itemRenderer.renderItem(stack, mode, false, matrices, vertexConsumers, light, overlay, this.inventoryWeaponModel);
		} else {
			boolean leftHanded;
			switch (mode) {
				case FIRST_PERSON_LEFT_HAND, THIRD_PERSON_LEFT_HAND -> leftHanded = true;
				default -> leftHanded = false;
			}
			this.itemRenderer.renderItem(stack, mode, leftHanded, matrices, vertexConsumers, light, overlay, this.worldWeaponModel);
		}
	}
}
