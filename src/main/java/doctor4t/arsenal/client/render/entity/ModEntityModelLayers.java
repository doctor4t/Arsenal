package doctor4t.arsenal.client.render.entity;

import doctor4t.arsenal.common.Arsenal;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry.TexturedModelDataProvider;
import net.minecraft.client.render.entity.model.EntityModelLayer;

import java.util.LinkedHashMap;
import java.util.Map;

public interface ModEntityModelLayers {
	Map<EntityModelLayer, TexturedModelDataProvider> MODEL_LAYERS = new LinkedHashMap<>();

	EntityModelLayer ANCHORBLADE = createModelLayer("anchorblade", AnchorBladeEntityModel::getTexturedModelData);

	private static EntityModelLayer createModelLayer(String name, TexturedModelDataProvider provider) {
		EntityModelLayer entityModelLayer = createMain(name);
		MODEL_LAYERS.put(entityModelLayer, provider);
		return entityModelLayer;
	}

	private static EntityModelLayer createMain(String id) {
		return create(id, "main");
	}

	private static EntityModelLayer create(String id, String layer) {
		return new EntityModelLayer(Arsenal.id(id), layer);
	}

	static void initialize() {
		MODEL_LAYERS.forEach(EntityModelLayerRegistry::registerModelLayer);
	}
}
