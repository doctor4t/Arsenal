package doctor4t.arsenal.common.enchantment;

import doctor4t.arsenal.common.init.ModItems;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class SpewingEnchantement extends Enchantment {
	public SpewingEnchantement(Rarity weight, EquipmentSlot... slot) {
		super(weight, EnchantmentTarget.WEAPON, slot);
	}

	@Override
	public int getMinPower(int level) {
		return 20;
	}

	@Override
	public int getMaxPower(int level) {
		return 50;
	}

	@Override
	public int getMaxLevel() {
		return 1;
	}

	@Override
	public boolean isAcceptableItem(ItemStack stack) {
		return stack.isOf(ModItems.CLOWN_SCYTHE) || stack.isOf(Items.BOOK) || stack.isOf(Items.ENCHANTED_BOOK);
	}
}
