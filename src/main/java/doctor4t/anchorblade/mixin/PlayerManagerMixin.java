package doctor4t.anchorblade.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import doctor4t.anchorblade.common.util.AnchorSelection;
import net.minecraft.network.Packet;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
	@WrapOperation(method = "sendPlayerStatus", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V"))
	private static void anchorblade$sendPlayerStatus(ServerPlayNetworkHandler networkHandler, Packet<?> packet, Operation<Void> operation) {
		if (packet instanceof AnchorSelection selectionPacket && networkHandler.player.getInventory() instanceof AnchorSelection selection) {
			selectionPacket.anchorblade$setSelectedAnchor(selection.anchorblade$hasSelectedAnchor());
		}
		operation.call(networkHandler, packet);
	}
}
