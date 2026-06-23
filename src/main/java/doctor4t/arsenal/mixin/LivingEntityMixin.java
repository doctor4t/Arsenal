package doctor4t.arsenal.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import doctor4t.arsenal.common.item.CleavingItem;
import doctor4t.arsenal.common.item.GuillotineItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
	@Shadow
	public abstract ItemStack getMainHandStack();

	@WrapMethod(method = "disablesShield")
	public boolean disablesShield(Operation<Boolean> original) {
		ItemStack mainHandStack = this.getMainHandStack();
		return original.call() || (mainHandStack.getItem() instanceof CleavingItem cleavingItem && cleavingItem.shouldDisableShield(mainHandStack));
	}

	@ModifyVariable(method = "damage", at = @At("HEAD"), argsOnly = true)
	private float arsenal$guillotineCleaverBerserk(float value, DamageSource source) {
		Entity attacker = source.getSource();
		if (attacker instanceof LivingEntity livingAttacker
			&& !livingAttacker.getWorld().isClient
			&& GuillotineItem.isGuillotineAndMode(livingAttacker.getMainHandStack(), GuillotineItem.CLEAVER_MODE)) {
			value += MathHelper.map(livingAttacker.getHealth(), livingAttacker.getMaxHealth(), 2f, 0f, 5f); // 0 bonus on full health, 5 bonus damage 1 heart
		}

//		if (attacker instanceof LivingEntity livingAttacker && !livingAttacker.world.isClient) livingAttacker.sendMessage(Text.literal(String.valueOf(value)));

		return value;
	}
}
