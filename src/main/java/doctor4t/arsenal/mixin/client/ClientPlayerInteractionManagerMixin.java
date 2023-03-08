package doctor4t.arsenal.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import doctor4t.arsenal.common.util.WeaponSlotToggle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.network.Packet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
	@Shadow @Final private MinecraftClient client;

	@WrapOperation(method = "syncSelectedSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V"))
	private void arsenal$syncWeaponSlot(ClientPlayNetworkHandler clientPlayNetworkHandler, Packet<?> packet, Operation<Void> operation) {
		if (packet instanceof WeaponSlotToggle selection && this.client.player.getInventory() instanceof WeaponSlotToggle weaponSlotToggle) {
			selection.arsenal$setWeaponSlot(weaponSlotToggle.arsenal$shouldWeaponSlot());
		}
		operation.call(clientPlayNetworkHandler, packet);
	}
}
