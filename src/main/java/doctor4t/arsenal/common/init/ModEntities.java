package doctor4t.arsenal.common.init;

import doctor4t.arsenal.common.Arsenal;
import doctor4t.arsenal.common.entity.AnchorbladeEntity;
import doctor4t.arsenal.common.entity.BloodScytheEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.LinkedHashMap;
import java.util.Map;

public interface ModEntities {
	Map<EntityType<? extends Entity>, Identifier> ENTITIES = new LinkedHashMap<>();

	EntityType<BloodScytheEntity> BLOOD_SCYTHE = createEntity("blood_scythe", FabricEntityTypeBuilder.<BloodScytheEntity>create(SpawnGroup.MISC, BloodScytheEntity::new).disableSaving().dimensions(EntityDimensions.changing(5.0f, 0.2f)).build());
	EntityType<AnchorbladeEntity> ANCHORBLADE = createEntity("anchorblade", FabricEntityTypeBuilder.<AnchorbladeEntity>create(SpawnGroup.MISC, AnchorbladeEntity::new).disableSaving().dimensions(EntityDimensions.fixed(1.2f, 1.2f)).trackRangeChunks(128).build());

	private static <T extends EntityType<? extends Entity>> T createEntity(String name, T entity) {
		ENTITIES.put(entity, new Identifier(Arsenal.MOD_ID, name));
		return entity;
	}

	static void initialize() {
		ENTITIES.keySet().forEach(entityType -> Registry.register(Registry.ENTITY_TYPE, ENTITIES.get(entityType), entityType));
	}
}
