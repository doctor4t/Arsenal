package doctor4t.arsenal.mixin.client;

import doctor4t.arsenal.common.util.WeaponSlotToggle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
	@Shadow @Final private MinecraftClient client;

	@Inject(method = "onHeldItemChange", at = @At(value = "TAIL"))
	private void arsenal$syncWeaponSlot(UpdateSelectedSlotS2CPacket packet, CallbackInfo ci) {
		if (packet instanceof WeaponSlotToggle selectPacket && this.client.player.getInventory() instanceof WeaponSlotToggle selection) {
			selection.arsenal$setWeaponSlot(selectPacket.arsenal$shouldWeaponSlot());
		}
	}
}
