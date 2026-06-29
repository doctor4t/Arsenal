package doctor4t.arsenal.common.components;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ClientTickingComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import doctor4t.arsenal.common.Arsenal;
import doctor4t.arsenal.common.ArsenalComponents;
import doctor4t.arsenal.common.init.ModItems;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.NotNull;

public class BackWeaponComponent implements AutoSyncedComponent, ServerTickingComponent {
	public static final int SLOT = 0;
	private final PlayerEntity player;
	private final SimpleInventory backWeapon = new SimpleInventory(1);
	private boolean holdingBackWeapon = false;
	private ItemStack cachedStack = ItemStack.EMPTY;

	public BackWeaponComponent(PlayerEntity player) {
		this.player = player;
	}

	private void sync() {
		ArsenalComponents.BACK_WEAPON_COMPONENT.sync(this.player);
	}

	@Override
	public void readFromNbt(@NotNull NbtCompound tag) {
		this.backWeapon.setStack(SLOT, ItemStack.fromNbt(tag.getCompound("backWeapon")));
		this.holdingBackWeapon = tag.getBoolean("holdingBackWeapon");
	}

	@Override
	public void writeToNbt(@NotNull NbtCompound tag) {
		tag.put("backWeapon", this.backWeapon.getStack(SLOT).writeNbt(new NbtCompound()));
		tag.putBoolean("holdingBackWeapon", this.holdingBackWeapon);
	}

	public ItemStack getBackWeapon() {
		return this.backWeapon.getStack(SLOT);
	}

	public static ItemStack getBackWeapon(PlayerEntity player) {
		return ArsenalComponents.BACK_WEAPON_COMPONENT.get(player).getBackWeapon();
	}

	public boolean setBackWeapon(ItemStack backWeapon) {
		this.backWeapon.setStack(SLOT, backWeapon.copy());
		sync();
		return true;
	}

	public static boolean setBackWeapon(PlayerEntity player, ItemStack backWeapon) {
		return ArsenalComponents.BACK_WEAPON_COMPONENT.get(player).setBackWeapon(backWeapon);
	}

	public SimpleInventory getBackWeaponInventory() {
		return this.backWeapon;
	}

	public static SimpleInventory getBackWeaponInventory(PlayerEntity player) {
		return ArsenalComponents.BACK_WEAPON_COMPONENT.get(player).getBackWeaponInventory();
	}

	public boolean isHoldingBackWeapon() {
		return this.holdingBackWeapon;
	}

	public static boolean isHoldingBackWeapon(PlayerEntity player) {
		return ArsenalComponents.BACK_WEAPON_COMPONENT.get(player).isHoldingBackWeapon();
	}

	public void setHoldingBackWeapon(boolean holdingBackWeapon) {
		this.holdingBackWeapon = holdingBackWeapon;
		sync();
	}

	public static void setHoldingBackWeapon(PlayerEntity player, boolean holdingBackWeapon) {
		if (player.world.isClient()) {
			PacketByteBuf buf = PacketByteBufs.create();
			buf.writeBoolean(holdingBackWeapon);
			ClientPlayNetworking.send(Arsenal.holdWeaponPacketId, buf);
			return;
		}
		ArsenalComponents.BACK_WEAPON_COMPONENT.get(player).setHoldingBackWeapon(holdingBackWeapon);
	}

	@Override
	public void serverTick() {
		ItemStack currentStack = this.getBackWeapon();
		if (!cachedStack.getOrCreateNbt().equals(currentStack.getOrCreateNbt())) {
			this.sync();
		}
		cachedStack = currentStack.copy();
	}
}
