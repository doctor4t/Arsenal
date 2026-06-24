package doctor4t.arsenal.common.item;

import net.minecraft.item.ItemStack;

public interface ShieldDisablingItem {
	boolean shouldDisableShield(ItemStack stack);
}
