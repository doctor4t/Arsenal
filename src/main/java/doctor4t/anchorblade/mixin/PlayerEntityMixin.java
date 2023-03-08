package doctor4t.anchorblade.mixin;

import doctor4t.anchorblade.common.init.ModSoundEvents;
import doctor4t.anchorblade.common.item.AnchorbladeItem;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tag.FluidTags;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
	@Shadow public abstract float getAttackCooldownProgress(float baseTime);

	protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	@Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getAttackCooldownProgress(F)F"))
	private void attack(Entity target, CallbackInfo ci) {
		if (this.getMainHandStack().getItem() instanceof AnchorbladeItem) {
			if (this.getAttackCooldownProgress(0.5F) > 0.9F) {
				AnchorbladeItem.spawnSweepParticles((PlayerEntity) (Object) this, AnchorbladeItem.LUX_ANCHORBLADE_SWEEP_PARTICLES[this.getRandom().nextInt(AnchorbladeItem.LUX_ANCHORBLADE_SWEEP_PARTICLES.length)]);
				this.world.playSound(null, this.getX(), this.getY(), this.getZ(), ModSoundEvents.ANCHORBLADE_HIT, this.getSoundCategory(), 1.0F, 1.0F);
			}
		}
	}

	@Inject(method = "getBlockBreakingSpeed", at = @At("RETURN"), cancellable = true)
	public void getBlockBreakingSpeed(BlockState block, CallbackInfoReturnable<Float> cir) {
		if (this.getMainHandStack().getItem() instanceof AnchorbladeItem && this.isSubmergedIn(FluidTags.WATER)) {
			cir.setReturnValue(cir.getReturnValue() * 2f);
		}
	}
}
