package dev.doctor4t.arsenal.item;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import dev.doctor4t.arsenal.Arsenal;
import dev.doctor4t.arsenal.cca.ArsenalComponents;
import dev.doctor4t.arsenal.cca.WeaponSkinComponent;
import dev.doctor4t.arsenal.entity.BloodScytheEntity;
import dev.doctor4t.arsenal.index.ArsenalDamageTypes;
import dev.doctor4t.arsenal.index.ArsenalEnchantments;
import dev.doctor4t.arsenal.index.ArsenalSounds;
import dev.doctor4t.arsenal.util.SweepParticleUtil;
import dev.doctor4t.ratatouille.item.CustomHitParticleItem;
import dev.doctor4t.ratatouille.item.CustomHitSoundItem;
import dev.doctor4t.ratatouille.util.TextUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ScytheItem extends MiningToolItem implements CustomHitParticleItem, CustomHitSoundItem, ArsenalWeaponItem {
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
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);

        WeaponSkinComponent weaponSkinComponent = ArsenalComponents.WEAPON_SKIN_COMPONENT.getNullable(stack);
        if (weaponSkinComponent != null && !weaponSkinComponent.getSkinName().equals(Skin.DEFAULT.getName()) && entity instanceof PlayerEntity player) {
            if (!Arsenal.isSupporter(player.getUuid())) {
                if (world.isClient) {
                    player.sendMessage(Text.translatable("tooltip.supporter_only").styled(style -> style.withColor(0xCC0000)));
                    player.playSound(SoundEvents.ITEM_SHIELD_BREAK, 0.5f, 1.0f);
                }

                weaponSkinComponent.setSkin(Skin.DEFAULT.getName());
            }
        }
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockState blockStateClicked = context.getWorld().getBlockState(context.getBlockPos());
        PlayerEntity user = context.getPlayer();
        if (user != null && user.isSneaking() && (blockStateClicked.isIn(BlockTags.ANVIL) || blockStateClicked.isOf(Blocks.SMITHING_TABLE))) {
            if (Arsenal.isSupporter(user.getUuid())) {
                WeaponSkinComponent weaponSkinComponent = ArsenalComponents.WEAPON_SKIN_COMPONENT.getNullable(user.getStackInHand(context.getHand()));
                if (weaponSkinComponent != null) {
                    Skin currentSkin = Skin.fromString(weaponSkinComponent.getSkinName());

                    if (currentSkin == null) {
                        currentSkin = Skin.DEFAULT;
                    }

                    Skin nextSkin = Skin.getNext(currentSkin);
                    weaponSkinComponent.setSkin(nextSkin.getName());

                    context.getPlayer().playSound(SoundEvents.BLOCK_SMITHING_TABLE_USE, 0.5f, 1.0f);

                    return ActionResult.SUCCESS;
                }
            } else {
                if (context.getWorld().isClient) {
                    user.sendMessage(Text.translatable("tooltip.supporter_only").styled(style -> style.withColor(0xCC0000)));
                    context.getPlayer().playSound(SoundEvents.ITEM_SHIELD_BREAK, 0.5f, 1.0f);
                }
                return ActionResult.FAIL;
            }
        }
        return super.useOnBlock(context);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (EnchantmentHelper.getEquipmentLevel(ArsenalEnchantments.SPEWING, player) > 0) {
            float f = 1.0f;

            if (!world.isClient) {
                BloodScytheEntity bloodScythe = new BloodScytheEntity(world, player);
                bloodScythe.setOwner(player);
                bloodScythe.setVelocity(player, player.getPitch(), player.getYaw(), 0.0f, f * 3.0f, 1.0f);
                bloodScythe.setDamage(bloodScythe.getDamage());
                player.getStackInHand(hand).damage(1, player, p -> p.sendToolBreakStatus(hand));
                bloodScythe.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;

                ArrayList<StatusEffectInstance> statusEffectsHalved = new ArrayList<>();
                float absorption = player.getAbsorptionAmount();
                for (StatusEffectInstance statusEffect : player.getStatusEffects()) {
                    StatusEffectInstance statusHalved = new StatusEffectInstance(statusEffect.getEffectType(), statusEffect.getDuration() / 2, statusEffect.getAmplifier(), statusEffect.isAmbient(), statusEffect.shouldShowParticles(), statusEffect.shouldShowIcon());
                    bloodScythe.addEffect(statusHalved);
                    statusEffectsHalved.add(statusHalved);
                }
                player.clearStatusEffects();
                for (StatusEffectInstance statusEffectInstance : statusEffectsHalved) {
                    player.addStatusEffect(statusEffectInstance);
                }
                player.setAbsorptionAmount(absorption);

                player.damage(world.getDamageSources().create(ArsenalDamageTypes.SPEWING), 3f);
                player.getItemCooldownManager().set(this, 20);

                world.spawnEntity(bloodScythe);

                if (player.getWorld() instanceof ServerWorld serverWorld) {
                    WeaponSkinComponent weaponSkinComponent = ArsenalComponents.WEAPON_SKIN_COMPONENT.getNullable(player.getMainHandStack());

                    Skin skin = Skin.DEFAULT;
                    if (weaponSkinComponent != null) {
                        Skin toSkin = Skin.fromString(weaponSkinComponent.getSkinName());
                        if (toSkin != null) {
                            skin = toSkin;
                        }
                    }

                    Pair<Integer, Integer> colorPair = new Pair<>(skin.color, skin.shadowColor);
                    SweepParticleUtil.sendSweepPacketToClient(serverWorld, colorPair, player.getX() + -MathHelper.sin((float) (player.getYaw() * (Math.PI / 180F))), player.getBodyY(0.5D), player.getZ() + MathHelper.cos((float) (player.getYaw() * (Math.PI / 180F))));
                }
            }
            world.playSound(null, player.getX(), player.getY(), player.getZ(), ArsenalSounds.ITEM_SCYTHE_SPEWING, SoundCategory.PLAYERS, 1.0f, 1.0f);
            return TypedActionResult.success(player.getStackInHand(hand));
        }
        return super.use(world, player, hand);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        WeaponSkinComponent weaponSkinComponent = ArsenalComponents.WEAPON_SKIN_COMPONENT.getNullable(stack);
        if (weaponSkinComponent != null && !weaponSkinComponent.getSkinName().equals(Skin.DEFAULT.getName())) {
            Skin skin = Skin.fromString(weaponSkinComponent.getSkinName());

            if (skin != null) {
                tooltip.add(Text.literal(skin.tooltipName != null ? skin.tooltipName : TextUtils.formatValueString(skin.getName())).styled(style -> style.withColor(skin.color)));
                if (skin.lore != null) {
                    if (Screen.hasShiftDown()) {
                        MutableText translatable = Text.translatable(skin.lore);
                        for (String line : translatable.getString().split("\n")) {
                            tooltip.add(Text.literal(line).styled(style -> style.withColor(Formatting.DARK_GRAY)));
                        }
                    } else {
                        tooltip.add(Text.translatable("tooltip.arsenal.hidden").styled(style -> style.withColor(Formatting.DARK_GRAY)));
                    }
                }
            }
        }

        super.appendTooltip(stack, world, tooltip, context);
    }

    @Override
    public void spawnHitParticles(PlayerEntity player) {
        if (player.getWorld() instanceof ServerWorld serverWorld) {
            WeaponSkinComponent weaponSkinComponent = ArsenalComponents.WEAPON_SKIN_COMPONENT.getNullable(player.getMainHandStack());

            Skin skin = Skin.DEFAULT;
            if (weaponSkinComponent != null) {
                Skin toSkin = Skin.fromString(weaponSkinComponent.getSkinName());
                if (toSkin != null) {
                    skin = toSkin;
                }
            }

            Pair<Integer, Integer> colorPair = new Pair<>(skin.color, skin.shadowColor);
            SweepParticleUtil.sendSweepPacketToClient(serverWorld, colorPair, player.getX() + -MathHelper.sin((float) (player.getYaw() * (Math.PI / 180F))), player.getBodyY(0.5D), player.getZ() + MathHelper.cos((float) (player.getYaw() * (Math.PI / 180F))));
        }
    }

    @Override
    public void playHitSound(PlayerEntity player) {
        player.playSound(ArsenalSounds.ITEM_SCYTHE_HIT, 1.0F, (float) (1.0F + player.getRandom().nextGaussian() / 10f));
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return !miner.isCreative();
    }

    public enum Skin {
        DEFAULT(0xFFD9D9D9, 0xFF7F8885, null, null),
        GRACE(0xFFD90420, 0xFF8C0420, null, "tooltip.arsenal.scythe_grace"),
        CARRION(0xFFE9DFB8, 0xFF9D806E, null, null),
        GILDED(0xFFF1BC5A, 0xFFE28634, null, null),
        ROZE(0xFFB70066, 0xFF710949, null, null),
        FOLLY(0xFFFF005A, 0xFFBC0045, "Folly Tree Branch", null);

        public final int color;
        public final int shadowColor;
        public final @Nullable String lore;
        public final @Nullable String tooltipName;

        Skin(int color, int shadowColor, @Nullable String tooltipName, @Nullable String lore) {
            this.color = color;
            this.shadowColor = shadowColor;
            this.lore = lore;
            this.tooltipName = tooltipName;
        }

        public String getName() {
            return this.name().toLowerCase(Locale.ROOT);
        }

        @Nullable
        public static Skin fromString(String name) {
            for (Skin skin : Skin.values()) if (skin.getName().equalsIgnoreCase(name)) return skin;
            return null;
        }

        @Nullable
        public static Skin getNext(Skin skin) {
            Skin[] values = Skin.values();
            return values[(skin.ordinal() + 1) % values.length];
        }
    }
}
