package doctor4t.arsenal.mixin.client;

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
			if (player.getInventory() instanceof WeaponSlotToggle selection && selection.arsenal$shouldWeaponSlot()) {
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
				RenderSystem.setShader(GameRenderer::getPositionTexShader);
				RenderSystem.setShaderTexture(0, ClickableWidget.WIDGETS_TEXTURE);
				int i = this.scaledWidth / 2;
				int j = this.getZOffset();
				this.setZOffset(-90);
				this.drawTexture(matrices, i - 12, this.scaledHeight - 23 - 70, 0, 22, 24, 24);
				this.setZOffset(j);
				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();
				int o = i - 90 + 4 * 20 + 2;
				int p = this.scaledHeight - 19 - 70;
				this.renderHotbarItem(o, p, tickDelta, player, holder.arsenal$getWeapon(), 1);
				RenderSystem.disableBlend();
			}
		}
	}
}
