package doctor4t.arsenal.common;

import doctor4t.arsenal.common.components.BackWeaponComponent;
import doctor4t.arsenal.common.init.ModEnchantments;
import doctor4t.arsenal.common.init.ModEntities;
import doctor4t.arsenal.common.init.ModItems;
import doctor4t.arsenal.common.init.ModParticles;
import doctor4t.arsenal.common.init.ModSoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

public class Arsenal implements ModInitializer {
	public static final String MOD_ID = "arsenal";
	//PacketIdentifiers
	public static final Identifier holdWeaponPacketId = id("hold_packet");
	public static final Identifier swapWeaponPacketId = id("swap_packet");
	public static final Identifier swapInventoryPacketId = id("swap_inventory_packet");

	@Override
	public void onInitialize(ModContainer mod) {
		ModEntities.initialize();
		ModItems.initialize();
		ModEnchantments.initialize();
		ModSoundEvents.initialize();
		ModParticles.initialize();
		ServerPlayNetworking.registerGlobalReceiver(holdWeaponPacketId, (server, player, handler, buf, responseSender) -> {
			boolean hold = buf.readBoolean();
			BackWeaponComponent.setHoldingBackWeapon(player, hold);
		});
		ServerPlayNetworking.registerGlobalReceiver(swapWeaponPacketId, (server, player, handler, buf, responseSender) -> {
			if (!player.isSpectator()) {
				boolean toggled = BackWeaponComponent.isHoldingBackWeapon(player);
				BackWeaponComponent.setHoldingBackWeapon(player, false);
				ItemStack itemStack = BackWeaponComponent.getBackWeapon(player);
				boolean success = BackWeaponComponent.setBackWeapon(player, player.getStackInHand(Hand.MAIN_HAND));
				if (success) {
					player.setStackInHand(Hand.MAIN_HAND, itemStack);
				}
				player.clearActiveItem();
				BackWeaponComponent.setHoldingBackWeapon(player, toggled);
			}
		});
		ServerPlayNetworking.registerGlobalReceiver(swapInventoryPacketId, (server, player, handler, buf, responseSender) -> {
			int slotId = buf.readInt();
			if (!player.isSpectator()) {
				if (!player.currentScreenHandler.isValidSlotIndex(slotId)) {
					return;
				}
				Slot slot = player.currentScreenHandler.getSlot(slotId);
				ItemStack itemStack = BackWeaponComponent.getBackWeapon(player);
				boolean success = BackWeaponComponent.setBackWeapon(player, slot.getStack());
				if (success) {
					slot.setStack(itemStack);
				}
			}
		});
	}

	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}
}
