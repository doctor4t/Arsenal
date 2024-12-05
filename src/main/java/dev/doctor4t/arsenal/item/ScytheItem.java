package dev.doctor4t.arsenal.item;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import dev.doctor4t.arsenal.client.particle.contract.ColoredParticleInitialData;
import dev.doctor4t.arsenal.entity.BloodScytheEntity;
import dev.doctor4t.arsenal.index.ArsenalDamageTypes;
import dev.doctor4t.arsenal.index.ArsenalEnchantments;
import dev.doctor4t.arsenal.index.ArsenalParticles;
import dev.doctor4t.arsenal.index.ArsenalSoundEvents;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ScytheItem extends MiningToolItem implements GUIHeldVaryingRenderItem, CustomHitParticleItem, CustomHitSoundItem, CustomColorItem {
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
        if (EnchantmentHelper.getEquipmentLevel(ArsenalEnchantments.SPEWING, user) > 0) {
            float f = 1.0f;

            if (!world.isClient) {
                BloodScytheEntity bloodScythe = new BloodScytheEntity(world, user);
                bloodScythe.setOwner(user);
                bloodScythe.setVelocity(user, user.getPitch(), user.getYaw(), 0.0f, f * 3.0f, 1.0f);
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

                user.damage(world.getDamageSources().create(ArsenalDamageTypes.SPEWING), 3f);
                user.getItemCooldownManager().set(this, 20);

                world.spawnEntity(bloodScythe);

                if (world instanceof ServerWorld serverWorld) {
                    double d = -MathHelper.sin(user.getYaw() * ((float) Math.PI / 180));
                    double e = MathHelper.cos(user.getYaw() * ((float) Math.PI / 180));
                    double pitch = user.getPitch() * -0.02;

                    double deltaX = -MathHelper.sin((float) (user.getYaw() * (Math.PI / 180F)));
                    double deltaZ = MathHelper.cos((float) (user.getYaw() * (Math.PI / 180F)));

                    ColoredParticleInitialData data = new ColoredParticleInitialData(0xFFAEB4B4);
                    serverWorld.spawnParticles(ArsenalParticles.SWEEP_ATTACK_PARTICLE.setData(data), user.getX() + deltaX, user.getBodyY(0.5D), user.getZ() + deltaZ, 0, deltaX, 0.0D, deltaZ, 0.0D);
                }
            }
            world.playSound(null, user.getX(), user.getY(), user.getZ(), ArsenalSoundEvents.ITEM_SCYTHE_SPEWING, SoundCategory.PLAYERS, 1.0f, 1.0f);
            return TypedActionResult.success(user.getStackInHand(hand));
        }
        return super.use(world, user, hand);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context); // 0x850909
    }

    @Override
    public void spawnHitParticles(PlayerEntity player) {
        double d0 = (-MathHelper.sin(player.getYaw() * ((float) Math.PI / 180F)));
        double d1 = MathHelper.cos(player.getYaw() * ((float) Math.PI / 180F));
        if (player.getWorld() instanceof ServerWorld serverWorld) {
            double deltaX = -MathHelper.sin((float) (player.getYaw() * (Math.PI / 180F)));
            double deltaZ = MathHelper.cos((float) (player.getYaw() * (Math.PI / 180F)));

            ColoredParticleInitialData data = new ColoredParticleInitialData(0xFFAEB4B4);
            serverWorld.spawnParticles(ArsenalParticles.SWEEP_ATTACK_PARTICLE.setData(data), player.getX() + deltaX, player.getBodyY(0.5D), player.getZ() + deltaZ, 0, deltaX, 0.0D, deltaZ, 0.0D);
        }
    }

    @Override
    public void playHitSound(PlayerEntity player) {
        player.playSound(ArsenalSoundEvents.ITEM_SCYTHE_HIT, 1.0F, (float) (1.0F + player.getRandom().nextGaussian() / 10f));
    }

    @Override
    public int getNameColor() {
        return 0xB00C0C;
    }
}
