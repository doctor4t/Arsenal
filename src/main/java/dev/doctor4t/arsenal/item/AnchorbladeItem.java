package dev.doctor4t.arsenal.item;

import dev.doctor4t.arsenal.Arsenal;
import dev.doctor4t.arsenal.cca.ArsenalComponents;
import dev.doctor4t.arsenal.cca.WeaponOwnerComponent;
import dev.doctor4t.arsenal.entity.AnchorbladeEntity;
import dev.doctor4t.arsenal.index.ArsenalCosmetics;
import dev.doctor4t.arsenal.index.ArsenalEnchantments;
import dev.doctor4t.arsenal.index.ArsenalSounds;
import dev.doctor4t.arsenal.util.AnchorOwner;
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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.tag.BlockTags;
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

import java.util.List;
import java.util.Locale;
import java.util.Random;

public class AnchorbladeItem extends PickaxeItem implements CustomHitParticleItem, CustomHitSoundItem, ArsenalWeaponItem {
    public AnchorbladeItem(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockState blockStateClicked = context.getWorld().getBlockState(context.getBlockPos());
        PlayerEntity user = context.getPlayer();
        if (user != null && user.isSneaking() && (blockStateClicked.isIn(BlockTags.ANVIL) || blockStateClicked.isOf(Blocks.SMITHING_TABLE)) && context.getWorld().isClient) {
            if (ArsenalCosmetics.isSupporter(user.getUuid())) {
                WeaponOwnerComponent weaponOwnerComponent = ArsenalComponents.WEAPON_OWNER_COMPONENT.get(user.getStackInHand(context.getHand()));
                Skin currentSkin = Skin.fromString(ArsenalCosmetics.getSkin(context.getStack()));

                if (currentSkin == null) {
                    currentSkin = Skin.DEFAULT;
                }

                ArsenalCosmetics.setSkin(weaponOwnerComponent.getOwner(), context.getStack(), Skin.getNext(currentSkin).getName());
                context.getPlayer().playSound(SoundEvents.BLOCK_SMITHING_TABLE_USE, 0.5f, 1.0f);

                return ActionResult.SUCCESS;
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
            if (owner.arsenal$isAnchorActive(hand, reeling)) {
                owner.arsenal$getAnchor(hand, reeling).setRecalled(user.getStackInHand(hand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND).isEmpty());
                return TypedActionResult.fail(stack);
            }
            int riptide = EnchantmentHelper.getRiptide(stack);
            if (riptide <= 0 || user.isTouchingWaterOrRain()) {
                if (!world.isClient) {
                    stack.damage(1, user, p -> p.sendToolBreakStatus(user.getActiveHand()));
                    if (riptide == 0) {
                        AnchorbladeEntity anchorbladeEntity = new AnchorbladeEntity(world, user, stack);
                        anchorbladeEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 2.5F + (float) riptide * 0.5F, 1.0F);
                        owner.arsenal$setAnchor(hand, anchorbladeEntity);
                        world.spawnEntity(anchorbladeEntity);
                        world.playSoundFromEntity(null, anchorbladeEntity, ArsenalSounds.ITEM_ANCHORBLADE_THROW, SoundCategory.PLAYERS, 1.0F, 1.0F);

                        return TypedActionResult.success(user.getStackInHand(hand));
                    }
                }
                user.incrementStat(Stats.USED.getOrCreateStat(this));
            }
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        Skin skin = Skin.fromString(ArsenalCosmetics.getSkin(stack));

        if (skin != null && skin != Skin.DEFAULT) {
            tooltip.add(Text.literal(skin.tooltipName != null ? skin.tooltipName : TextUtils.formatValueString(skin.getName())).styled(style -> style.withColor(skin.getFirstColor())));
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

        super.appendTooltip(stack, world, tooltip, context);
    }

    @Override
    public void spawnHitParticles(PlayerEntity player) {
        if (player.getWorld() instanceof ServerWorld serverWorld) {
            Skin skin = Skin.DEFAULT;
            Skin toSkin = Skin.fromString(ArsenalCosmetics.getSkin(player.getMainHandStack()));
            if (toSkin != null) {
                skin = toSkin;
            }

            Pair<Integer, Integer> colorPair = skin.getRandomParticleColorPair();
            SweepParticleUtil.sendSweepPacketToClient(serverWorld, colorPair, player.getX() + -MathHelper.sin((float) (player.getYaw() * (Math.PI / 180F))), player.getBodyY(0.5D), player.getZ() + MathHelper.cos((float) (player.getYaw() * (Math.PI / 180F))));
        }
    }

    @Override
    public void playHitSound(PlayerEntity player) {
        player.playSound(ArsenalSounds.ITEM_ANCHORBLADE_HIT, 1.0F, (float) (1.0F + player.getRandom().nextGaussian() / 10f));
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return !miner.isCreative();
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);

        if (entity instanceof PlayerEntity player) {
            WeaponOwnerComponent weaponOwnerComponent = ArsenalComponents.WEAPON_OWNER_COMPONENT.get(stack);
            weaponOwnerComponent.setOwner(player.getUuid());
        }
    }

    public enum Skin {
        DEFAULT(new int[]{0xFF2B2632}, new int[]{0xFF1B1B1B}, null, null),
        LUXINTRUS(new int[]{0xFFE7761F, 0xFF37965B, 0xFFA51BB7}, new int[]{0xFFA84701, 0xFF115642, 0xFF671081}, "L'Ancre", "tooltip.arsenal.anchorblade_luxintrus"),
        CARRION(new int[]{0xFFE9DFB8}, new int[]{0xFF9D806E}, null, null),
        GILDED(new int[]{0xFFF1BC5A}, new int[]{0xFFE28634}, null, null),
        WINSWEEP(new int[]{0xFFFFDC00, 0xFFC676F1}, new int[]{0xFFBE5F00, 0xFF7546A0}, "Wanchorblade", null),
        AMBESSA(new int[]{0xFFA9A9A7}, new int[]{0xFF6A6D66}, "Crescent Blade", null);

        public final Identifier chainTexture;
        public final Identifier anchorbladeEntityModel;
        public final int[] colors;
        public final int[] shadowColors;
        public final @Nullable String tooltipName;
        public final @Nullable String lore;
        public final Random random;

        Skin(int[] colors, int[] shadowColors, @Nullable String tooltipName, @Nullable String lore) {
            this.chainTexture = Arsenal.id(this.getName().equals("default") ? "textures/entity/chain.png" : "textures/entity/chain_" + this.getName() + ".png");
            this.anchorbladeEntityModel = Arsenal.id(this.getName().equals("default") ? "item/anchorblade_in_hand" : "item/anchorblade_" + this.getName() + "_in_hand");
            this.colors = colors;
            this.shadowColors = shadowColors;
            this.tooltipName = tooltipName;
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

        public static Skin getNext(Skin skin) {
            Skin[] values = Skin.values();
            return values[(skin.ordinal() + 1) % values.length];
        }
    }
}
