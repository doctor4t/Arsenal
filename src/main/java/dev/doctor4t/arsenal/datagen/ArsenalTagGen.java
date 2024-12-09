package dev.doctor4t.arsenal.datagen;

import dev.doctor4t.arsenal.index.ArsenalDamageTypes;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.DamageTypeTags;

import java.util.concurrent.CompletableFuture;

public class ArsenalTagGen {
    public static class DefileDamageTagGen extends FabricTagProvider<DamageType> {
        public DefileDamageTagGen(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, RegistryKeys.DAMAGE_TYPE, registriesFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup arg) {
            this.getOrCreateTagBuilder(DamageTypeTags.IS_PROJECTILE)
                    .addOptional(ArsenalDamageTypes.ANCHOR)
                    .addOptional(ArsenalDamageTypes.BLOOD_SCYTHE);

            this.getOrCreateTagBuilder(DamageTypeTags.BYPASSES_ENCHANTMENTS)
                    .addOptional(ArsenalDamageTypes.SPEWING);

            this.getOrCreateTagBuilder(DamageTypeTags.BYPASSES_ARMOR)
                    .addOptional(ArsenalDamageTypes.SPEWING);
        }
    }
}
