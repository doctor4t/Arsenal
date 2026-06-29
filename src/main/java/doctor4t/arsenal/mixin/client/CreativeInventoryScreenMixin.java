package doctor4t.arsenal.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.systems.RenderSystem;
import doctor4t.arsenal.client.ArsenalClient;
import doctor4t.arsenal.common.components.BackWeaponComponent;
import doctor4t.arsenal.common.util.WeaponSlot;
import doctor4t.arsenal.mixin.accessors.CreativeSlotAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemGroup;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(CreativeInventoryScreen.class)
public abstract class CreativeInventoryScreenMixin extends AbstractInventoryScreen<CreativeInventoryScreen.CreativeScreenHandler> {
	@Shadow private static int selectedTab;

	public CreativeInventoryScreenMixin(CreativeInventoryScreen.CreativeScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
		super(screenHandler, playerInventory, text);
	}

//	@WrapOperation(method = "setSelectedTab", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/collection/DefaultedList;add(Ljava/lang/Object;)Z", ordinal = 3))
//	private boolean arsenal$moveWeaponSlot(DefaultedList<Slot> slots, Object object, Operation<Boolean> operation) {
//		boolean ret = operation.call(slots, object);
//
//		DefaultedList<Slot> screenHandlerSlots = this.client.player.playerScreenHandler.slots;
//		for (Slot screenHandlerSlot : screenHandlerSlots) {
//			if (screenHandlerSlot instanceof WeaponSlot weaponSlot) {
//				Slot slot = new CreativeInventoryScreen.CreativeSlot(weaponSlot, screenHandlerSlots.indexOf(screenHandlerSlot), 127, 20);
//				this.handler.slots.add(slot);
//			}
//		}
//
//		return ret;
//	}

	@Inject(method = "drawBackground", at = @At(value = "TAIL"))
	private void arsenal$drawSlots(MatrixStack matrices, float delta, int mouseX, int mouseY, CallbackInfo ci) {
		ItemGroup itemGroup = ItemGroup.GROUPS[selectedTab];
		if (itemGroup == ItemGroup.INVENTORY) {
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.setShaderTexture(0, HandledScreen.BACKGROUND_TEXTURE);
			int i = this.x + 126;
			int j = this.y + 19;
			this.drawTexture(matrices, i, j, 76, 61, 18, 18);

			if (BackWeaponComponent.getBackWeaponInventory(MinecraftClient.getInstance().player).isEmpty()) {
				RenderSystem.setShader(GameRenderer::getPositionTexShader);
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
				RenderSystem.setShaderTexture(0, ArsenalClient.SLOT_TEXTURE);
				drawTexture(matrices, i + 1, j + 1, 0, 0, 16, 16, 16, 16);
			}
		}
	}
}
