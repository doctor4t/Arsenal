package doctor4t.arsenal.common.item;

import doctor4t.arsenal.common.entity.BloodScytheEntity;
import doctor4t.arsenal.common.init.ModDamageSources;
import doctor4t.arsenal.common.init.ModEnchantments;
import doctor4t.arsenal.common.init.ModParticles;
import doctor4t.arsenal.common.init.ModSoundEvents;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import xyz.amymialee.mialeemisc.util.MialeeText;

import java.util.ArrayList;
import java.util.List;

public class ScytheItem extends MiningToolItem implements GUIHeldVaryingRenderItem, CustomHitParticleItem, CustomHitSoundItem {
	public ScytheItem(ToolMaterial material, float damage, float speed, Settings settings) {
		super(damage, speed, material, BlockTags.HOE_MINEABLE, settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		if (EnchantmentHelper.getEquipmentLevel(ModEnchantments.SPEWING, user) > 0) {
			float f = 1.0f;

			if (!world.isClient) {
				BloodScytheEntity bloodScythe = new BloodScytheEntity(world, user);
				bloodScythe.setOwner(user);
				bloodScythe.setProperties(user, user.getPitch(), user.getYaw(), 0.0f, f * 3.0f, 1.0f);
				bloodScythe.setDamage(bloodScythe.getDamage());
				user.getStackInHand(hand).damage(1, user, p -> p.sendToolBreakStatus(hand));
				bloodScythe.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;

				ArrayList<StatusEffectInstance> statusEffectsHalved = new ArrayList<>();
				float absorption = user.getAbsorptionAmount();
				for (StatusEffectInstance statusEffect : user.getStatusEffects()) {
					StatusEffectInstance statusHalved = new StatusEffectInstance(statusEffect.getEffectType(), statusEffect.getDuration() / 2, statusEffect.getAmplifier(), statusEffect.isAmbient(), statusEffect.shouldShowParticles(), statusEffect.shouldShowIcon());
					bloodScythe.addEffect(statusHalved);
					statusEffectsHalved.add(statusHalved);
				}
				user.clearStatusEffects();
				for (StatusEffectInstance statusEffectInstance : statusEffectsHalved) {
					user.addStatusEffect(statusEffectInstance);
				}
				user.setAbsorptionAmount(absorption);

				user.damage(ModDamageSources.spewing(), 3f);
				user.getItemCooldownManager().set(this, 20);

				world.spawnEntity(bloodScythe);

				double d = -MathHelper.sin(user.getYaw() * ((float) Math.PI / 180));
				double e = MathHelper.cos(user.getYaw() * ((float) Math.PI / 180));
				if (world instanceof ServerWorld) {
					((ServerWorld) world).spawnParticles(ModParticles.CLOWN_SCYTHE_SWEEP_ATTACK_PARTICLE, user.getX() + d, user.getBodyY(0.5), user.getZ() + e, 0, d, 0.0, e, 0.0);
				}
			}
			world.playSound(null, user.getX(), user.getY(), user.getZ(), ModSoundEvents.ITEM_SCYTHE_SPEWING, SoundCategory.PLAYERS, 1.0f, 1.0f);
			return TypedActionResult.success(user.getStackInHand(hand));
		}

		return super.use(world, user, hand);
	}

	@Override
	public Text getName(ItemStack stack) {
		return MialeeText.withColor(super.getName(stack), 0xB00C0C);
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		super.appendTooltip(stack, world, tooltip, context); // 0x850909
	}

	@Override
	public void spawnHitParticles(PlayerEntity player) {
		double d0 = (-MathHelper.sin(player.getYaw() * ((float) Math.PI / 180F)));
		double d1 = MathHelper.cos(player.getYaw() * ((float) Math.PI / 180F));
		if (player.world instanceof ServerWorld) {
			((ServerWorld) player.world).spawnParticles(ModParticles.CLOWN_SCYTHE_SWEEP_ATTACK_PARTICLE, player.getX() + d0, player.getBodyY(0.5D), player.getZ() + d1, 0, d0, 0.0D, d1, 0.0D);
		}
	}

	@Override
	public void playHitSound(PlayerEntity player) {
		player.playSound(ModSoundEvents.ITEM_SCYTHE_HIT, 1.0F, (float) (1.0F + player.getRandom().nextGaussian() / 10f));
	}

}
