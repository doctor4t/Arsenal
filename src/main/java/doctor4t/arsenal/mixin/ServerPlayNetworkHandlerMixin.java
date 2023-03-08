package doctor4t.arsenal.mixin;

import doctor4t.arsenal.common.util.WeaponSlotToggle;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
	@Shadow public ServerPlayerEntity player;

	@Inject(method = "onUpdateSelectedSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;getInventory()Lnet/minecraft/entity/player/PlayerInventory;", ordinal = 1))
	private void arsenal$updateSlot(UpdateSelectedSlotC2SPacket packet, CallbackInfo ci) {
		if (packet instanceof WeaponSlotToggle selectPacket && this.player.getInventory() instanceof WeaponSlotToggle selection) {
			selection.arsenal$setWeaponSlot(selectPacket.arsenal$shouldWeaponSlot());
		}
	}

	@Inject(method = "onPlayerAction", at = @At(value = "HEAD"))
	private void arsenal$swapHands(PlayerActionC2SPacket packet, CallbackInfo ci) {
		if (packet.getAction() == PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND) {
			if (this.player instanceof WeaponSlotToggle weaponSlotToggle) {
				weaponSlotToggle.arsenal$setWeaponSlot(false);
			}
		}
	}
}
