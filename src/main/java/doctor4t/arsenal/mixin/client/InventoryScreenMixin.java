package doctor4t.arsenal.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractInventoryScreen<PlayerScreenHandler> {
	public InventoryScreenMixin(PlayerScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
		super(screenHandler, playerInventory, text);
	}

//	@Unique private static final Identifier SLOT_TEXTURE = Arsenal.id("textures/item/weapon_slot.png");
//	@Unique private WeaponSlotHolder inventory;

//	@Inject(method = "<init>", at = @At(value = "TAIL"))
//	private void arsenal$init(PlayerEntity player, CallbackInfo ci) {
//		if (player.getInventory() instanceof WeaponSlotHolder holder) {
//			this.inventory = holder;
//		}
//	}

	@Inject(method = "drawBackground", at = @At(value = "TAIL"))
	private void arsenal$drawSlots(MatrixStack matrices, float delta, int mouseX, int mouseY, CallbackInfo ci) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, HandledScreen.BACKGROUND_TEXTURE);
		int i = this.x + 76;
		int j = this.y + 25;
		this.drawTexture(matrices, i, j, 76, 61, 18, 18);
//		if (this.inventory.arsenal$getWeapon().isEmpty()) {					fixme: I don't know why, I don't want to know why, the texture won't render in the slot
//			RenderSystem.setShader(GameRenderer::getPositionTexShader);
//			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
//			RenderSystem.setShaderTexture(0, SLOT_TEXTURE);
//			this.drawTexture(matrices, i + 1, j + 1, 0, 0, 16, 16);
//		}
	}
}
