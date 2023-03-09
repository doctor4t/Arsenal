package doctor4t.arsenal.common.util;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;

public interface WeaponSlotHolder {
	void arsenal$setWeapon(ItemStack weapon);
	ItemStack arsenal$getWeapon();
	SimpleInventory arsenal$getWeaponSlot();
	int arsenal$getSlotHolding(ItemStack stack);
	boolean arsenal$tryInsertIntoSlot(int slot, ItemStack stack);
}
