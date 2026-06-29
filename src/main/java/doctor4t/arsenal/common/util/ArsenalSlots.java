package doctor4t.arsenal.common.util;

import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.slot.Slot;

public class ArsenalSlots {
	public static class WeaponSlot extends Slot {
		public WeaponSlot(Inventory inventory, int index, int x, int y) {
			super(inventory, index, x, y);
		}
	}

	public static class CreativeWeaponSlot extends CreativeInventoryScreen.CreativeSlot {
		public Slot containedSlot;

		public CreativeWeaponSlot(Slot slot, int invSlot, int x, int y) {
			super(slot, invSlot, x, y);
			this.containedSlot = slot;
		}
	}
}
