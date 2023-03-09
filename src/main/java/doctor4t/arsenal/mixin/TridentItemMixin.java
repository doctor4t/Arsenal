package doctor4t.arsenal.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import doctor4t.arsenal.common.util.ProjectileSlotHolder;
import doctor4t.arsenal.common.util.WeaponSlotHolder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TridentItem.class)
public class TridentItemMixin {
	@WrapOperation(method = "onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"))
	private boolean arsenal$spawnEntity(World world, Entity entity, Operation<Boolean> operation, @Local(ordinal = 0) ItemStack stack, @Local(ordinal = 0) LivingEntity user) {
		if (user instanceof PlayerEntity player && player.getInventory() instanceof WeaponSlotHolder holder && entity instanceof ProjectileSlotHolder slotHolder) {
			int index = holder.arsenal$getSlotHolding(stack);
			if (index != -1) {
				slotHolder.arsenal$setOwnedSlot(index);
			}
		}
		return operation.call(world, entity);
	}
}
