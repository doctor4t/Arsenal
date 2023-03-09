package doctor4t.arsenal.common;

import doctor4t.arsenal.common.init.ModEntities;
import doctor4t.arsenal.common.init.ModItems;
import doctor4t.arsenal.common.init.ModParticles;
import doctor4t.arsenal.common.init.ModSoundEvents;
import doctor4t.arsenal.common.util.WeaponSlotHolder;
import doctor4t.arsenal.common.util.WeaponSlotToggle;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

public class Arsenal implements ModInitializer {
	public static final String MOD_ID = "arsenal";

	public static final Identifier swapWeaponPacketId = id("swap_packet");

	@Override
	public void onInitialize(ModContainer mod) {
		ModEntities.initialize();
		ModItems.initialize();
		ModSoundEvents.initialize();
		ModParticles.initialize();

		ServerPlayNetworking.registerGlobalReceiver(swapWeaponPacketId, (server, player, handler, buf, responseSender) -> {
			if (!player.isSpectator() && player.getInventory() instanceof WeaponSlotHolder holder && player.getInventory() instanceof WeaponSlotToggle toggle) {
				boolean toggled = toggle.arsenal$shouldWeaponSlot();
				toggle.arsenal$setWeaponSlot(false);
				ItemStack itemStack = holder.arsenal$getWeapon();
				holder.arsenal$setWeapon(player.getStackInHand(Hand.MAIN_HAND));
				player.setStackInHand(Hand.MAIN_HAND, itemStack);
				player.clearActiveItem();
				toggle.arsenal$setWeaponSlot(toggled);
			}
		});
	}

	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}
}
