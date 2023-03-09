package doctor4t.arsenal.common.util;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;

public interface WeaponSlotCallback {
	Event<WeaponSlotCallback> EVENT = EventFactory.createArrayBacked(WeaponSlotCallback.class,
			(listeners) -> (player, holder, anchor) -> {
				for (WeaponSlotCallback listener : listeners) {
					ActionResult result = listener.interact(player, holder, anchor);
					if (result != ActionResult.PASS) {
						return result;
					}
				}
				return ActionResult.PASS;
			});

	ActionResult interact(PlayerEntity player, WeaponSlotHolder holder, ItemStack anchor);
}
