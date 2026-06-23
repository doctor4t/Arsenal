package doctor4t.arsenal.common.item;

import doctor4t.arsenal.common.entity.AnchorbladeEntity;
import doctor4t.arsenal.common.init.ModEnchantments;
import doctor4t.arsenal.common.init.ModParticles;
import doctor4t.arsenal.common.init.ModSoundEvents;
import doctor4t.arsenal.common.util.AnchorOwner;
import net.fabricmc.yarn.constants.MiningLevels;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import xyz.amymialee.mialeemisc.util.MialeeText;

import java.util.List;

public class AnchorbladeItem extends PickaxeItem implements GUIHeldVaryingRenderItem, CustomHitParticleItem, CustomHitSoundItem, CustomColorItem {
	public static final DefaultParticleType[] LUX_ANCHORBLADE_SWEEP_PARTICLES = {ModParticles.LUX_ANCHORBLADE_SWEEP_1, ModParticles.LUX_ANCHORBLADE_SWEEP_2, ModParticles.LUX_ANCHORBLADE_SWEEP_3};

	public AnchorbladeItem(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings) {
		super(material, attackDamage, attackSpeed, settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack stack = user.getStackInHand(hand);
		if (user instanceof AnchorOwner owner) {
			boolean reeling = EnchantmentHelper.getLevel(ModEnchantments.REELING, stack) > 0;
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
						world.playSoundFromEntity(null, anchorbladeEntity, ModSoundEvents.ITEM_ANCHORBLADE_THROW, SoundCategory.PLAYERS, 1.0F, 1.0F);
					}
				}
				user.incrementStat(Stats.USED.getOrCreateStat(this));
			}
		}
		return TypedActionResult.pass(user.getStackInHand(hand));
	}

	@Override
	public int getNameColor() {
		return 0xFF8700;
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		if (Screen.hasShiftDown()) {
			for (int i = 1; i <= 6; i++) {
				tooltip.add(MialeeText.withColor(Text.translatable("item.arsenal.anchorblade.tooltip_" + i), 0xC35913));
			}
		} else {
			tooltip.add(Text.translatable("tooltip.arsenal.hidden").setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY)));
		}
		super.appendTooltip(stack, world, tooltip, context);
	}

	@Override
	public void spawnHitParticles(PlayerEntity player) {
		double deltaX = -MathHelper.sin((float) (player.getYaw() * (Math.PI / 180F)));
		double deltaZ = MathHelper.cos((float) (player.getYaw() * (Math.PI / 180F)));
		if (player.world instanceof ServerWorld serverWorld) {
			serverWorld.spawnParticles(LUX_ANCHORBLADE_SWEEP_PARTICLES[player.getRandom().nextInt(AnchorbladeItem.LUX_ANCHORBLADE_SWEEP_PARTICLES.length)], player.getX() + deltaX, player.getBodyY(0.5D), player.getZ() + deltaZ, 0, deltaX, 0.0D, deltaZ, 0.0D);
		}
	}

	@Override
	public void playHitSound(PlayerEntity player) {
		player.playSound(ModSoundEvents.ITEM_ANCHORBLADE_HIT, 1.0F,1.0F +  (player.getRandom().nextFloat() * .2f - .1f));
	}

	@Override
	public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {}

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
}
