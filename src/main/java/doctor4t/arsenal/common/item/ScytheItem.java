package doctor4t.arsenal.common.item;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import doctor4t.arsenal.common.entity.BloodScytheEntity;
import doctor4t.arsenal.common.init.ModDamageSources;
import doctor4t.arsenal.common.init.ModEnchantments;
import doctor4t.arsenal.common.init.ModParticles;
import doctor4t.arsenal.common.init.ModSoundEvents;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.UUID;

public class ScytheItem extends MiningToolItem implements GUIHeldVaryingRenderItem, CustomHitParticleItem, CustomHitSoundItem, CustomColorItem, ShieldDisablingItem, ReapingItem {
	private static final EntityAttributeModifier REACH_MODIFIER = new EntityAttributeModifier(UUID.fromString("911af262-067d-4da2-854c-20f03cc2dd8b"), "Weapon modifier", 0.5, EntityAttributeModifier.Operation.ADDITION);

	public ScytheItem(ToolMaterial material, float damage, float speed, Settings settings) {
		super(damage, speed, material, BlockTags.HOE_MINEABLE, settings);
	}

	@Override
	public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
		Multimap<EntityAttribute, EntityAttributeModifier> map = LinkedHashMultimap.create(super.getAttributeModifiers(slot));
		if (slot == EquipmentSlot.MAINHAND) {
			map.put(ReachEntityAttributes.ATTACK_RANGE, REACH_MODIFIER);
		}
		return map;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		if (EnchantmentHelper.getEquipmentLevel(ModEnchantments.SPEWING, user) > 0) {
			float f = 1.0f;

			if (!world.isClient) {
				BloodScytheEntity bloodScythe = new BloodScytheEntity(world, user);
				bloodScythe.setOwner(user);
				bloodScythe.setVelocity(user, user.getPitch(), user.getYaw(), 0.0f, f * 3.0f, 1.0f);
				bloodScythe.setDamage(bloodScythe.getDamage());
				user.getStackInHand(hand).damage(1, user, p -> p.sendToolBreakStatus(hand));
				bloodScythe.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;

				ArrayList<StatusEffectInstance> statusEffectsHalved = new ArrayList<>();
				for (StatusEffectInstance statusEffect : user.getStatusEffects()) {
					StatusEffectInstance statusHalved = new StatusEffectInstance(statusEffect.getEffectType(), statusEffect.getDuration() / 2, statusEffect.getAmplifier(), statusEffect.isAmbient(), statusEffect.shouldShowParticles(), statusEffect.shouldShowIcon());
					bloodScythe.addEffect(statusHalved);
					statusEffectsHalved.add(statusHalved);
				}
				user.clearStatusEffects();
//				for (StatusEffectInstance statusEffectInstance : statusEffectsHalved) {
//					user.addStatusEffect(statusEffectInstance);
//				}

				user.damage(ModDamageSources.spewing(), 4f);
				user.getItemCooldownManager().set(this, 20);

				world.spawnEntity(bloodScythe);

				if (world instanceof ServerWorld serverWorld) {
					double d = -MathHelper.sin(user.getYaw() * ((float) Math.PI / 180));
					double e = MathHelper.cos(user.getYaw() * ((float) Math.PI / 180));
					double pitch = user.getPitch() * -0.02;
					serverWorld.spawnParticles(ModParticles.CLOWN_SCYTHE_SWEEP_ATTACK_PARTICLE, user.getX() + d, user.getBodyY(0.5) + pitch, user.getZ() + e, 0, d, 0.0, e, 0.0);
				}
			}
			world.playSound(null, user.getX(), user.getY(), user.getZ(), ModSoundEvents.ITEM_SCYTHE_SPEWING, SoundCategory.PLAYERS, 1.0f, 1.0f);
			return TypedActionResult.success(user.getStackInHand(hand));
		}
		return super.use(world, user, hand);
	}

	@Override
	public ParticleEffect getHitParticle(LivingEntity attacker, Entity target, ItemStack stack) {
		return ModParticles.CLOWN_SCYTHE_SWEEP_ATTACK_PARTICLE;
	}

	@Override
	public void playHitSound(LivingEntity player) {
		player.playSound(ModSoundEvents.ITEM_SCYTHE_HIT, 1.0F, 1.0F + (player.getRandom().nextFloat() * .2f - .1f));
	}

	@Override
	public int getNameColor() {
		return 0xB00C0C;
	}

	@Override
	public boolean shouldDisableShield(ItemStack stack) {
		return true;
	}

	@Override
	public float getReapingVelocityMultiplier(ItemStack mainHandStack) {
		return 0.25f;
	}
}
