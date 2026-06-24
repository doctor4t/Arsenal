package doctor4t.arsenal.common.init;

import doctor4t.arsenal.common.Arsenal;
import doctor4t.arsenal.common.enchantment.ReelingEnchantment;
import doctor4t.arsenal.common.enchantment.SpewingEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.LinkedHashMap;
import java.util.Map;

public interface ModEnchantments {
	Map<Enchantment, Identifier> ENCHANTMENTS = new LinkedHashMap<>();

	Enchantment SPEWING = createEnchantment("spewing", new SpewingEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND));
	Enchantment REELING = createEnchantment("reeling", new ReelingEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND));

	private static Enchantment createEnchantment(String name, Enchantment enchantment) {
		ENCHANTMENTS.put(enchantment, new Identifier(Arsenal.MOD_ID, name));
		return enchantment;
	}

	static void initialize() {
		ENCHANTMENTS.keySet().forEach(enchantment -> Registry.register(Registry.ENCHANTMENT, ENCHANTMENTS.get(enchantment), enchantment));
	}
}
