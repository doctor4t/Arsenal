package doctor4t.arsenal.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import doctor4t.arsenal.common.util.WeaponSlotToggle;
import net.minecraft.network.Packet;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
	@WrapOperation(method = "sendPlayerStatus", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V"))
	private static void arsenal$sendPlayerSlot(ServerPlayNetworkHandler networkHandler, Packet<?> packet, Operation<Void> operation) {
		if (packet instanceof WeaponSlotToggle selectionPacket && networkHandler.player.getInventory() instanceof WeaponSlotToggle selection) {
			selectionPacket.arsenal$setWeaponSlot(selection.arsenal$shouldWeaponSlot());
		}
		operation.call(networkHandler, packet);
	}
}
