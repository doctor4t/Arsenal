package doctor4t.arsenal.common.init;

import doctor4t.arsenal.common.Arsenal;
import doctor4t.arsenal.common.entity.AnchorbladeEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.qsl.entity.api.QuiltEntityTypeBuilder;

import java.util.LinkedHashMap;
import java.util.Map;

public interface ModEntities {
	Map<EntityType<? extends Entity>, Identifier> ENTITIES = new LinkedHashMap<>();

	EntityType<AnchorbladeEntity> ANCHORBLADE = createEntity("anchorblade", QuiltEntityTypeBuilder.<AnchorbladeEntity>create(SpawnGroup.MISC, AnchorbladeEntity::new).setDimensions(EntityDimensions.fixed(1.2f, 1.2f)).maxChunkTrackingRange(128).build());

	private static <T extends EntityType<? extends Entity>> T createEntity(String name, T entity) {
		ENTITIES.put(entity, new Identifier(Arsenal.MOD_ID, name));
		return entity;
	}

	static void initialize() {
		ENTITIES.keySet().forEach(entityType -> Registry.register(Registry.ENTITY_TYPE, ENTITIES.get(entityType), entityType));
	}
}
