package doctor4t.anchorblade.mixin.client;

import doctor4t.anchorblade.common.util.AnchorSelection;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
	@Shadow @Nullable public ClientPlayerEntity player;

	@Inject(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getInventory()Lnet/minecraft/entity/player/PlayerInventory;"))
	private void anchorblade$handleInputEvents(CallbackInfo ci) {
		if (this.player != null && this.player.getInventory() instanceof AnchorSelection selection) {
			selection.anchorblade$setSelectedAnchor(false);
		}
	}

	@Inject(method = "doItemPick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;isValidHotbarIndex(I)Z"))
	private void anchorblade$doItemPick(CallbackInfo ci) {
		if (this.player != null && this.player.getInventory() instanceof AnchorSelection selection) {
			selection.anchorblade$setSelectedAnchor(false);
		}
	}
}
