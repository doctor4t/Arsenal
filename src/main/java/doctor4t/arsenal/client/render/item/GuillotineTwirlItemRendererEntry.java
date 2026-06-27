package doctor4t.arsenal.client.render.item;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;

public class GuillotineTwirlItemRendererEntry {
	private ItemStack cachedStack;
	private long lastCachedTime;
	public ItemStack prevModeStack;
	public long twirlStartTime;

	public GuillotineTwirlItemRendererEntry(ItemStack cachedStack, ItemStack prevModeStack, int twirlStartTime) {
		this.cachedStack = cachedStack;
		this.prevModeStack = prevModeStack;
		this.twirlStartTime = twirlStartTime;
	}

	public void setCachedStack(ItemStack cachedStack) {
		this.cachedStack = cachedStack;
		if (!cachedStack.isEmpty()) {
			this.lastCachedTime = MinecraftClient.getInstance().world.getTime() % 36000;
		} else {
			this.lastCachedTime = -1;
		}
	}

	public ItemStack getCachedStack() {
		return cachedStack;
	}

	public long getLastCachedTime() {
		return lastCachedTime;
	}
}
