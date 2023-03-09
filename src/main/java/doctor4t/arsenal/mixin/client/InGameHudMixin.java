package doctor4t.arsenal.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.systems.RenderSystem;
import doctor4t.arsenal.common.util.WeaponSlotHolder;
import doctor4t.arsenal.common.util.WeaponSlotToggle;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin extends DrawableHelper {
	@Shadow private int scaledWidth;
	@Shadow private int scaledHeight;
	@Shadow protected abstract PlayerEntity getCameraPlayer();
	@Shadow protected abstract void renderHotbarItem(int x, int y, float tickDelta, PlayerEntity player, ItemStack stack, int seed);

	@Inject(method = "renderHotbar", at = @At("TAIL"))
	private void arsenal$renderWeaponSlot(float tickDelta, MatrixStack matrices, CallbackInfo ci) {
		PlayerEntity player = this.getCameraPlayer();
		if (player == null) return;
		if (player.getInventory() instanceof WeaponSlotHolder holder && !holder.arsenal$getWeapon().isEmpty()) {
			ItemStack stack = holder.arsenal$getWeapon();
			if (stack.isEmpty()) return;
			int i = this.scaledWidth / 2;
			if (player.getInventory() instanceof WeaponSlotToggle selection && selection.arsenal$shouldWeaponSlot()) {
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
				RenderSystem.setShader(GameRenderer::getPositionTexShader);
				RenderSystem.setShaderTexture(0, ClickableWidget.WIDGETS_TEXTURE);
				int j = this.getZOffset();
				this.setZOffset(-90);
				this.drawTexture(matrices, i - 12, this.scaledHeight - 23 - 70, 0, 22, 24, 24);
				RenderSystem.enableBlend();
				this.drawTexture(matrices, i - 12 + 4, this.scaledHeight - 23 - 70 + 4, 24 + 3, 22 + 4, 16, 16);
				RenderSystem.defaultBlendFunc();
				this.setZOffset(j);
				int o = i - 90 + 4 * 20 + 2;
				int p = this.scaledHeight - 19 - 70;
				this.renderHotbarItem(o, p, tickDelta, player, stack, 1);
				RenderSystem.disableBlend();
			} else {
				Arm arm = player.getMainArm().getOpposite();
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
				RenderSystem.setShader(GameRenderer::getPositionTexShader);
				RenderSystem.setShaderTexture(0, ClickableWidget.WIDGETS_TEXTURE);
				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();
				if (arm == Arm.RIGHT) {
					this.drawTexture(matrices, i - 91 - 29, this.scaledHeight - 23, 24, 22, 29, 24);
				} else {
					this.drawTexture(matrices, i + 91, this.scaledHeight - 23, 53, 22, 29, 24);
				}
				int n = this.scaledHeight - 16 - 3;
				if (arm == Arm.RIGHT) {
					this.renderHotbarItem(i - 91 - 26, n, tickDelta, player, stack, 0);
				} else {
					this.renderHotbarItem(i + 91 + 10, n, tickDelta, player, stack, 0);
				}
				RenderSystem.disableBlend();
			}
		}
	}

	@WrapOperation(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V", ordinal = 1))
	private void arsenal$selection(InGameHud hud, MatrixStack matrices, int x, int y, int u, int v, int width, int height, Operation<Void> operation) {
		if (this.getCameraPlayer() != null && this.getCameraPlayer().getInventory() instanceof WeaponSlotToggle selection && selection.arsenal$shouldWeaponSlot()) {
			return;
		}
		operation.call(hud, matrices, x, y, u, v, width, height);
	}
}
