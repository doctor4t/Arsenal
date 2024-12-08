package dev.doctor4t.arsenal.item;

import dev.doctor4t.arsenal.Arsenal;
import dev.doctor4t.arsenal.cca.ArsenalComponents;
import dev.doctor4t.arsenal.cca.WeaponSkinComponent;
import dev.doctor4t.arsenal.client.particle.contract.ColoredParticleInitialData;
import dev.doctor4t.arsenal.entity.AnchorbladeEntity;
import dev.doctor4t.arsenal.index.ArsenalEnchantments;
import dev.doctor4t.arsenal.index.ArsenalParticles;
import dev.doctor4t.arsenal.index.ArsenalSounds;
import dev.doctor4t.arsenal.util.AnchorOwner;
import dev.doctor4t.ratatouille.util.TextUtils;
import net.fabricmc.yarn.constants.MiningLevels;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class AnchorbladeItem extends PickaxeItem implements CustomHitParticleItem, CustomHitSoundItem, CustomNameColorItem, ArsenalWeaponItem {
    public AnchorbladeItem(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
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
        if (user != null && user.isSneaking() && (blockStateClicked.isOf(Blocks.ANVIL) || blockStateClicked.isOf(Blocks.SMITHING_TABLE))) {
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
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (user instanceof AnchorOwner owner) {
            boolean reeling = EnchantmentHelper.getLevel(ArsenalEnchantments.REELING, stack) > 0;
            if (owner.arsenal$isAnchorActive(reeling)) {
                owner.arsenal$getAnchor(reeling).setRecalled(true);
                return TypedActionResult.fail(stack);
            }
            int riptide = EnchantmentHelper.getRiptide(stack);
            if (riptide <= 0 || user.isTouchingWaterOrRain()) {
                if (!world.isClient) {
                    stack.damage(1, user, p -> p.sendToolBreakStatus(user.getActiveHand()));
                    if (riptide == 0) {
                        AnchorbladeEntity anchorbladeEntity = new AnchorbladeEntity(world, user, stack);
                        anchorbladeEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 2.5F + (float) riptide * 0.5F, 1.0F);
                        owner.arsenal$setAnchor(anchorbladeEntity);
                        world.spawnEntity(anchorbladeEntity);
                        world.playSoundFromEntity(null, anchorbladeEntity, ArsenalSounds.ITEM_ANCHORBLADE_THROW, SoundCategory.PLAYERS, 1.0F, 1.0F);

                        return TypedActionResult.success(user.getStackInHand(hand));
                    }
                }
                user.incrementStat(Stats.USED.getOrCreateStat(this));
            }
        }
        return TypedActionResult.pass(user.getStackInHand(hand));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        WeaponSkinComponent weaponSkinComponent = ArsenalComponents.WEAPON_SKIN_COMPONENT.getNullable(stack);
        if (weaponSkinComponent != null && !weaponSkinComponent.getSkinName().equals(Skin.DEFAULT.getName())) {
            Skin skin = Skin.fromString(weaponSkinComponent.getSkinName());
            if (skin != null) {
                tooltip.add(Text.literal(TextUtils.formatValueString(weaponSkinComponent.getSkinName())).styled(style -> style.withColor(skin.getFirstColor())));
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
            if (weaponSkinComponent != null) {
                Skin skin = Skin.fromString(weaponSkinComponent.getSkinName());

                if (skin != null) {
                    double deltaX = -MathHelper.sin((float) (player.getYaw() * (Math.PI / 180F)));
                    double deltaZ = MathHelper.cos((float) (player.getYaw() * (Math.PI / 180F)));

                    Pair<Integer, Integer> colorPair = skin.getRandomParticleColorPair();
                    serverWorld.spawnParticles(ArsenalParticles.SWEEP_PARTICLE.setData(new ColoredParticleInitialData(colorPair.getLeft())), player.getX() + deltaX, player.getBodyY(0.5D), player.getZ() + deltaZ, 0, deltaX, 0.0D, deltaZ, 0.0D);
                    serverWorld.spawnParticles(ArsenalParticles.SWEEP_SHADOW_PARTICLE.setData(new ColoredParticleInitialData(colorPair.getRight())), player.getX() + deltaX, player.getBodyY(0.5D), player.getZ() + deltaZ, 0, deltaX, 0.0D, deltaZ, 0.0D);
                }
            }
        }
    }

    @Override
    public void playHitSound(PlayerEntity player) {
        player.playSound(ArsenalSounds.ITEM_ANCHORBLADE_HIT, 1.0F, (float) (1.0F + player.getRandom().nextGaussian() / 10f));
    }

    public static class AnchorBladeToolMaterial implements ToolMaterial {
        public static final AnchorBladeToolMaterial INSTANCE = new AnchorBladeToolMaterial();

        @Override
        public int getDurability() {
            return 2560;
        }

        @Override
        public float getMiningSpeedMultiplier() {
            return 9.0F;
        }

        @Override
        public float getAttackDamage() {
            return 4.0F;
        }

        @Override
        public int getMiningLevel() {
            return MiningLevels.NETHERITE;
        }

        @Override
        public int getEnchantability() {
            return 28;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.ofItems(Items.COPPER_BLOCK);
        }
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return !miner.isCreative();
    }

    public enum Skin {
        DEFAULT(new int[]{0xFF2B2632}, new int[]{0xFF1B1B1B}, null),
        LUX(new int[]{0xFFFF5E00, 0xFF37965B, 0xFFA51BB7}, new int[]{0xFFC52400, 0xFF115642, 0xFF671081}, "tooltip.arsenal.anchorblade_lux"),
        CARRION(new int[]{0xFFE9DFB8}, new int[]{0xFF9D806E}, null),
        GILDED(new int[]{0xFFF1BC5A}, new int[]{0xFFE28634}, null);

        public final Identifier chainTexture;
        public final Identifier anchorbladeEntityModel;
        public final int[] colors;
        public final int[] shadowColors;
        public final @Nullable String lore;
        public final Random random;

        Skin(int[] colors, int[] shadowColors, @Nullable String lore) {
            this.chainTexture = Arsenal.id(this.getName().equals("default") ? "textures/entity/chain.png" : "textures/entity/chain_" + this.getName() + ".png");
            this.anchorbladeEntityModel = Arsenal.id(this.getName().equals("default") ? "item/anchorblade_in_hand" : "item/anchorblade_" + this.getName() + "_in_hand");
            this.colors = colors;
            this.shadowColors = shadowColors;
            this.lore = lore;
            this.random = new Random();
        }

        public String getName() {
            return this.name().toLowerCase(Locale.ROOT);
        }

        public int getFirstColor() {
            return this.colors[0];
        }

        public Pair<Integer, Integer> getRandomParticleColorPair() {
            int i = this.random.nextInt(this.colors.length);
            return new Pair<>(this.colors[i], this.shadowColors[i]);
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
