package doctor4t.arsenal.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import doctor4t.arsenal.common.components.BackWeaponComponent;
import doctor4t.arsenal.common.init.ModItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
	@Shadow
	@Nullable
	public ClientPlayerEntity player;

	@Inject(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getInventory()Lnet/minecraft/entity/player/PlayerInventory;"))
	private void arsenal$inputSlot(CallbackInfo ci) {
		if (this.player != null) BackWeaponComponent.setHoldingBackWeapon(this.player, false);
	}

	@Inject(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isSpectator()Z", ordinal = 1))
	private void arsenal$swapStop(CallbackInfo ci) {
		if (this.player != null) BackWeaponComponent.setHoldingBackWeapon(this.player, false);
	}

	@Inject(method = "doItemPick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;isValidHotbarIndex(I)Z"))
	private void arsenal$pickSlot(CallbackInfo ci) {
		if (this.player != null) BackWeaponComponent.setHoldingBackWeapon(this.player, false);
	}

	@WrapOperation(method = "doItemUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;resetEquipProgress(Lnet/minecraft/util/Hand;)V"))
	private void arsenal$disableGuillotineResetEquip(HeldItemRenderer instance, Hand hand, Operation<Void> original) {
		if (player.getStackInHand(hand).isOf(ModItems.GUILLOTINE)) {
			return;
		}

		original.call(instance, hand);
	}
}

