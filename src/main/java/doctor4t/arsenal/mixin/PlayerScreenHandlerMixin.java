package doctor4t.arsenal.mixin;

import com.mojang.datafixers.util.Pair;
import doctor4t.arsenal.common.Arsenal;
import doctor4t.arsenal.common.util.WeaponSlotHolder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerScreenHandler.class)
public abstract class PlayerScreenHandlerMixin extends AbstractRecipeScreenHandler<CraftingInventory> {
	public PlayerScreenHandlerMixin(ScreenHandlerType<?> screenHandlerType, int i) {
		super(screenHandlerType, i);
	}

	@Unique private static final Identifier EMPTY_WEAPON_SLOT = Arsenal.id("gui/weapon_slot");

	@Inject(method = "<init>", at = @At("TAIL"))
	private void arsenal$init(PlayerInventory inventory, boolean onServer, PlayerEntity owner, CallbackInfo ci) {
		if (owner.getInventory() instanceof WeaponSlotHolder holder) {
			this.addSlot(new Slot(holder.arsenal$getWeaponSlot(), 0, 77, 44) {
				@Override
				public @NotNull Pair<Identifier, Identifier> getBackgroundSprite() {
					return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, PlayerScreenHandler.EMPTY_OFFHAND_ARMOR_SLOT);
				}

				@Override
				public boolean canInsert(ItemStack stack) {
					return stack.getMaxCount() == 1;
				}
			});
		}
	}
}
