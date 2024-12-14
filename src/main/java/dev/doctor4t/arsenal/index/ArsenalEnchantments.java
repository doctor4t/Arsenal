package dev.doctor4t.arsenal.index;

import dev.doctor4t.arsenal.Arsenal;
import dev.doctor4t.arsenal.enchantment.ReelingEnchantment;
import dev.doctor4t.arsenal.enchantment.SpewingEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;

public interface ArsenalEnchantments {
    Map<Enchantment, Identifier> ENCHANTMENTS = new LinkedHashMap<>();

    Enchantment SPEWING = createEnchantment("spewing", new SpewingEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND));
    Enchantment REELING = createEnchantment("reeling", new ReelingEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND));
//    Enchantment HEFT = createEnchantment("heft", new HeftEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND));

    private static Enchantment createEnchantment(String name, Enchantment enchantment) {
        ENCHANTMENTS.put(enchantment, new Identifier(Arsenal.MOD_ID, name));
        return enchantment;
    }

    static void initialize() {
        ENCHANTMENTS.keySet().forEach(enchantment -> Registry.register(Registries.ENCHANTMENT, ENCHANTMENTS.get(enchantment), enchantment));
    }
}
