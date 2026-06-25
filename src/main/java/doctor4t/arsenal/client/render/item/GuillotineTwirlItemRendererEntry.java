package doctor4t.arsenal.client.render.item;

import net.minecraft.item.ItemStack;

public class GuillotineTwirlItemRendererEntry {
	public ItemStack cachedStack;
	public ItemStack prevModeStack;
	public long twirlStartTime;

	public GuillotineTwirlItemRendererEntry(ItemStack cachedStack, ItemStack prevModeStack, int twirlStartTime) {
		this.cachedStack = cachedStack;
		this.prevModeStack = prevModeStack;
		this.twirlStartTime = twirlStartTime;
	}
}
