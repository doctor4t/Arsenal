package doctor4t.arsenal.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import doctor4t.arsenal.client.ArsenalClient;
import doctor4t.arsenal.common.components.BackWeaponComponent;
import doctor4t.arsenal.common.util.WeaponSlotHolder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractInventoryScreen<PlayerScreenHandler> {
	public InventoryScreenMixin(PlayerScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
		super(screenHandler, playerInventory, text);
	}

	@Inject(method = "drawBackground", at = @At(value = "TAIL"))
	private void arsenal$drawSlots(MatrixStack matrices, float delta, int mouseX, int mouseY, CallbackInfo ci) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, HandledScreen.BACKGROUND_TEXTURE);
		int i = this.x + 76;
		int j = this.y + 25;
		this.drawTexture(matrices, i, j, 76, 61, 18, 18);

		if (BackWeaponComponent.getBackWeaponInventory(MinecraftClient.getInstance().player).isEmpty()) {
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.setShaderTexture(0, ArsenalClient.SLOT_TEXTURE);
			drawTexture(matrices, i + 1, j + 1, 0, 0, 16, 16, 16, 16);
		}
	}
}
