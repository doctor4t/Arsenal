package doctor4t.anchorblade.common.init;

import doctor4t.anchorblade.common.Anchorblade;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.LinkedHashMap;
import java.util.Map;

public interface ModEntities {
	Map<EntityType<? extends Entity>, Identifier> ENTITIES = new LinkedHashMap<>();

//	public static EntityType<ModEntity> MOD_ENTITY = createEntity("mod_entity", QuiltEntityTypeBuilder.<ModEntity>create(SpawnGroup.MISC, ModEntity::new).setDimensions(EntityDimensions.fixed(0f, 0f)).maxChunkTrackingRange(128).build());

	private static <T extends EntityType<? extends Entity>> T createEntity(String name, T entity) {
		ENTITIES.put(entity, new Identifier(Anchorblade.MOD_ID, name));
		return entity;
	}

	static void initialize() {
		ENTITIES.keySet().forEach(entityType -> Registry.register(Registry.ENTITY_TYPE, ENTITIES.get(entityType), entityType));
	}
}
