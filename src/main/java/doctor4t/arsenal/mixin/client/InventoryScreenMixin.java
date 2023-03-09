package doctor4t.arsenal.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import doctor4t.arsenal.common.Arsenal;
import doctor4t.arsenal.common.util.WeaponSlotHolder;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractInventoryScreen<PlayerScreenHandler> {
	public InventoryScreenMixin(PlayerScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
		super(screenHandler, playerInventory, text);
		this.inventory = playerInventory;
	}

	@Unique private static final Identifier SLOT_TEXTURE = Arsenal.id("textures/gui/weapon_slot.png");
	@Unique private final PlayerInventory inventory;

	@Inject(method = "drawBackground", at = @At(value = "TAIL"))
	private void arsenal$drawSlots(MatrixStack matrices, float delta, int mouseX, int mouseY, CallbackInfo ci) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
		int i = this.x + 76;
		int j = this.y + 43;
		this.drawTexture(matrices, i, j, 76, 61, 18, 18);
		if (this.inventory instanceof WeaponSlotHolder holder && holder.arsenal$getWeapon().isEmpty()) {
			RenderSystem.setShaderTexture(0, SLOT_TEXTURE);
			this.drawTexture(matrices, i, j, 0, 0, 18, 18);
		}
	}
}
