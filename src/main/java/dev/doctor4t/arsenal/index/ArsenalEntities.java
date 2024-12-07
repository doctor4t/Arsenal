package dev.doctor4t.arsenal.index;

import dev.doctor4t.arsenal.Arsenal;
import dev.doctor4t.arsenal.entity.AnchorbladeEntity;
import dev.doctor4t.arsenal.entity.BloodScytheEntity;
import dev.doctor4t.arsenal.entity.WeaponRackEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;

public interface ArsenalEntities {
    Map<EntityType<? extends Entity>, Identifier> ENTITIES = new LinkedHashMap<>();

    EntityType<BloodScytheEntity> BLOOD_SCYTHE = createEntity("blood_scythe", FabricEntityTypeBuilder.<BloodScytheEntity>create(SpawnGroup.MISC, BloodScytheEntity::new).disableSaving().dimensions(EntityDimensions.changing(5.0f, 0.2f)).build());
    EntityType<AnchorbladeEntity> ANCHORBLADE = createEntity("anchorblade", FabricEntityTypeBuilder.<AnchorbladeEntity>create(SpawnGroup.MISC, AnchorbladeEntity::new).disableSaving().dimensions(EntityDimensions.fixed(1.2f, 1.2f)).trackRangeChunks(128).build());
    EntityType<WeaponRackEntity> WEAPON_RACK = createEntity("weapon_rack", FabricEntityTypeBuilder.<WeaponRackEntity>create(SpawnGroup.MISC, WeaponRackEntity::new).dimensions(EntityDimensions.fixed(0.4F, 0.4F)).trackRangeChunks(10).trackedUpdateRate(Integer.MAX_VALUE).build());

    private static <T extends EntityType<? extends Entity>> T createEntity(String name, T entity) {
        ENTITIES.put(entity, new Identifier(Arsenal.MOD_ID, name));
        return entity;
    }

    static void initialize() {
        ENTITIES.keySet().forEach(entityType -> Registry.register(Registries.ENTITY_TYPE, ENTITIES.get(entityType), entityType));
    }
}
