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
import net.fabricmc.yarn.constants.MiningLevels;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

public class AnchorbladeItem extends PickaxeItem implements CustomHitParticleItem, CustomHitSoundItem, CustomNameColorItem, ArsenalWeaponItem {
    public AnchorbladeItem(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);

        WeaponSkinComponent weaponSkinComponent = ArsenalComponents.WEAPON_SKIN_COMPONENT.getNullable(stack);
        if (weaponSkinComponent != null && entity instanceof PlayerEntity player) {
            if (!Arsenal.isSupporter(player.getUuid())) {
                // TODO: Send message to player saying cosmetics are exclusive to supporters
                weaponSkinComponent.setSkin(Skin.DEFAULT.getName());
            }
        }
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockState blockStateClicked = context.getWorld().getBlockState(context.getBlockPos());
        PlayerEntity user = context.getPlayer();
        // TODO: Send message to player saying cosmetics are exclusive to supporters
        // TODO: Add sound effects and maybe particles?
        if (user != null && user.isSneaking() && Arsenal.isSupporter(user.getUuid()) && (blockStateClicked.isOf(Blocks.ANVIL) || blockStateClicked.isOf(Blocks.SMITHING_TABLE))) {
            WeaponSkinComponent weaponSkinComponent = ArsenalComponents.WEAPON_SKIN_COMPONENT.getNullable(user.getStackInHand(context.getHand()));
            if (weaponSkinComponent != null) {
                Skin currentSkin = Skin.fromString(weaponSkinComponent.getSkinName());

                if (currentSkin == null) {
                    currentSkin = Skin.DEFAULT;
                }

                Skin nextSkin = Skin.getNext(currentSkin);
                weaponSkinComponent.setSkin(nextSkin.getName());

                return ActionResult.SUCCESS;
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

                        // TODO: Make it so the counter only enables when hitting an entity with the anchorblade instead?
                        user.getItemCooldownManager().set(this, 20);

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
        // TODO: Display skin name and color
        // TODO: Only apply that tooltip for the custom lux skin
//        if (Screen.hasShiftDown()) {
//            for (int i = 1; i <= 6; i++) {
//                tooltip.add(Text.translatable("item.arsenal.anchorblade.tooltip_" + i).styled(style -> style.withColor(0xC35913)));
//            }
//        } else {
//            tooltip.add(Text.translatable("tooltip.arsenal.hidden").setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY)));
//        }
        super.appendTooltip(stack, world, tooltip, context);
    }

    @Override
    public void spawnHitParticles(PlayerEntity player) {
        // TODO: Custom colors for each skin
        double deltaX = -MathHelper.sin((float) (player.getYaw() * (Math.PI / 180F)));
        double deltaZ = MathHelper.cos((float) (player.getYaw() * (Math.PI / 180F)));

        if (player.getWorld() instanceof ServerWorld serverWorld) {
            ColoredParticleInitialData data = new ColoredParticleInitialData(0xFF2B2632);
            serverWorld.spawnParticles(ArsenalParticles.SWEEP_ATTACK_PARTICLE.setData(data), player.getX() + deltaX, player.getBodyY(0.5D), player.getZ() + deltaZ, 0, deltaX, 0.0D, deltaZ, 0.0D);
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
        DEFAULT,
        LUX,
        CARRION,
        GILDED;

        public final Identifier chainTexture;
        public final Identifier anchorbladeEntityModel;

        Skin() {
            this.chainTexture = Arsenal.id(this.getName().equals("default") ? "textures/entity/chain.png" : "textures/entity/chain_" + this.getName() + ".png");
            this.anchorbladeEntityModel = Arsenal.id(this.getName().equals("default") ? "item/anchorblade_in_hand" : "item/anchorblade_" + this.getName() + "_in_hand");
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
