package dev.doctor4t.arsenal.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.doctor4t.arsenal.cca.BackWeaponComponent;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Shadow
    private int scaledWidth;
    @Shadow
    private int scaledHeight;

    @Shadow
    private ItemStack currentStack;

    @Shadow
    protected abstract PlayerEntity getCameraPlayer();

    @Shadow
    protected abstract void renderHotbarItem(DrawContext context, int x, int y, float f, PlayerEntity player, ItemStack stack, int seed);

    @Inject(method = "renderHotbar", at = @At("TAIL"))
    private void arsenal$renderWeaponSlot(float tickDelta, DrawContext context, CallbackInfo ci) {
        PlayerEntity player = this.getCameraPlayer();
        if (player == null) return;
        ItemStack stack = BackWeaponComponent.getBackWeapon(player);
        if (!stack.isEmpty()) {
            int i = this.scaledWidth / 2;
            if (BackWeaponComponent.isHoldingBackWeapon(player)) {
                context.drawTexture(ClickableWidget.WIDGETS_TEXTURE, i - 12, this.scaledHeight - 23 - 70, 0, 22, 24, 24);
                RenderSystem.enableBlend();
                context.drawTexture(ClickableWidget.WIDGETS_TEXTURE, i - 12 + 4, this.scaledHeight - 23 - 70 + 4, 24 + 3, 22 + 4, 16, 16);
                RenderSystem.defaultBlendFunc();

                int o = i - 90 + 4 * 20 + 2;
                int p = this.scaledHeight - 19 - 70;
                this.renderHotbarItem(context, o, p, tickDelta, player, stack, 1);
                RenderSystem.disableBlend();
            } else {
                Arm arm = player.getMainArm().getOpposite();
                RenderSystem.enableBlend();
                if (arm == Arm.RIGHT) {
                    context.drawTexture(ClickableWidget.WIDGETS_TEXTURE, i - 91 - 29, this.scaledHeight - 23, 24, 22, 29, 24);
                } else {
                    context.drawTexture(ClickableWidget.WIDGETS_TEXTURE, i + 91, this.scaledHeight - 23, 53, 22, 29, 24);
                }
                RenderSystem.defaultBlendFunc();

                int n = this.scaledHeight - 16 - 3;
                if (arm == Arm.RIGHT) {
                    this.renderHotbarItem(context, i - 91 - 26, n, tickDelta, player, stack, 0);
                } else {
                    this.renderHotbarItem(context, i + 91 + 10, n, tickDelta, player, stack, 0);
                }
                RenderSystem.disableBlend();
            }
        }
    }

    @WrapOperation(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V", ordinal = 1))
    private void arsenal$selection(DrawContext instance, Identifier texture, int x, int y, int u, int v, int width, int height, Operation<Void> original) {
        if (this.getCameraPlayer() != null && BackWeaponComponent.isHoldingBackWeapon(this.getCameraPlayer())) {
            return;
        }
        original.call(instance, texture, x, y, u, v, width, height);
    }
}
