package doctor4t.arsenal.common.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import doctor4t.arsenal.common.Arsenal;
import doctor4t.arsenal.common.init.ModItems;
import doctor4t.arsenal.common.init.ModParticles;
import doctor4t.arsenal.common.init.ModSoundEvents;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class GuillotineItem extends ToolItem implements GUIHeldVaryingRenderItem, CustomHitParticleItem, CustomHitSoundItem, CustomColorItem, CleavingItem, ReapingItem {
	/*
		GUILLOTINE MODES:
		- Gild (0): Leech effect on hit (part of the damage is restored to the attacker as health), regular damage and speed
		- Scythe (1): Reaping (bring in players on crit), lower damage but faster speed
		- Cleaver (2): Berserk damage scaling (damage bonus the lower the attacker's health is), disables shields, higher damage but slower speed
	 */

	public static final String NBT_GUILLOTINE_MODE = "GuillotineMode";
	public static final int GILD_MODE = 0;
	public static final int SCYTHE_MODE = 1;
	public static final int CLEAVER_MODE = 2;

	private final ToolMaterial toolMaterial;
	private final float attackDamage;
	private final float attackSpeed;

	private Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;

	public GuillotineItem(ToolMaterial toolMaterial, float attackDamage, float attackSpeed, Item.Settings settings) {
		super(toolMaterial, settings);
		this.toolMaterial = toolMaterial;
		this.attackDamage = attackDamage;
		this.attackSpeed = attackSpeed;

		setAttributeModifiersForMode(GILD_MODE);
	}

	private void setAttributeModifiersForMode(int mode) {
		ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();

		// default mode: low damage but high speed
		float attackDamageAddition = -2f;
		float attackSpeedAddition = .6f;

		switch (mode) {
			case SCYTHE_MODE -> { // scythe mode: medium damage and speed
				attackDamageAddition = -1f;
				attackSpeedAddition = .3f;
			}
			case CLEAVER_MODE -> { // cleaver mode: high damage but low speed
				attackDamageAddition = 0f;
				attackSpeedAddition = 0f;
			}
		}

		builder.put(
			EntityAttributes.GENERIC_ATTACK_DAMAGE,
			new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", this.attackDamage + this.toolMaterial.getAttackDamage() + attackDamageAddition, EntityAttributeModifier.Operation.ADDITION)
		);
		builder.put(
			EntityAttributes.GENERIC_ATTACK_SPEED,
			new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", this.attackSpeed + attackSpeedAddition, EntityAttributeModifier.Operation.ADDITION)
		);
		builder.put(
			ReachEntityAttributes.ATTACK_RANGE,
			new EntityAttributeModifier(UUID.fromString("911af262-067d-4da2-854c-20f03cc2dd8b"), "Weapon modifier", 1f, EntityAttributeModifier.Operation.ADDITION)
		);

		this.attributeModifiers = builder.build();
	}

	@Override
	public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
 		return LinkedHashMultimap.create(slot == EquipmentSlot.MAINHAND ? this.attributeModifiers : super.getAttributeModifiers(slot));
	}

	@Override
	public void playHitSound(PlayerEntity player) {
		player.playSound(getHitSound(player.getMainHandStack()), 1.0F, 1.0F + (player.getRandom().nextFloat() * .2f - .1f));
	}

	private static SoundEvent getHitSound(ItemStack stack) {
		return switch (getGuillotineMode(stack)) {
			case SCYTHE_MODE -> ModSoundEvents.ITEM_GUILLOTINE_HIT_SCYTHE;
			case CLEAVER_MODE -> ModSoundEvents.ITEM_GUILLOTINE_HIT_CLEAVER;
			default -> ModSoundEvents.ITEM_GUILLOTINE_HIT;
		};
	}

	@Override
	public int getNameColor() {
		return 0x746060;
	}

	@Override
	public void spawnHitParticles(PlayerEntity player) {
		double d0 = (-MathHelper.sin(player.getYaw() * ((float) Math.PI / 180F)));
		double d1 = MathHelper.cos(player.getYaw() * ((float) Math.PI / 180F));
		if (player.world instanceof ServerWorld serverWorld) {
			serverWorld.spawnParticles(ModParticles.GUILLOTINE_SWEEP_ATTACK_PARTICLE, player.getX() + d0, player.getBodyY(0.5D), player.getZ() + d1, GILD_MODE, d0, 0.0D, d1, 0.0D);
		}
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack stack = user.getStackInHand(hand);

		NbtCompound nbt = stack.getOrCreateNbt();
		int guillotineMode = getGuillotineMode(nbt);
		int newGuillotineMode = (guillotineMode + 1) % 3;
		nbt.putInt(NBT_GUILLOTINE_MODE, newGuillotineMode);
		setAttributeModifiersForMode(newGuillotineMode);

		return TypedActionResult.success(stack);
	}

	public static int getGuillotineMode(ItemStack stack) {
		NbtCompound nbt = stack.getOrCreateNbt();
		return getGuillotineMode(nbt);
	}

	public static int getGuillotineMode(NbtCompound nbt) {
		return nbt.contains(GuillotineItem.NBT_GUILLOTINE_MODE) ? nbt.getInt(GuillotineItem.NBT_GUILLOTINE_MODE) : 0;
	}

	public static String getGuillotineModeName(ItemStack stack) {
		return Arsenal.GUILLOTINE_VARIATIONS.get(getGuillotineMode(stack));
	}

	public static Formatting getGuillotineModeFormatting(ItemStack stack) {
		return switch (getGuillotineMode(stack)) {
			case SCYTHE_MODE -> Formatting.RED;
			case CLEAVER_MODE -> Formatting.DARK_RED;
			default -> Formatting.GOLD;
		};
	}

	public static boolean isGuillotineAndMode(ItemStack stack, int mode) {
		return stack.isOf(ModItems.GUILLOTINE) && GuillotineItem.getGuillotineMode(stack) == mode;
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(stack, world, entity, slot, selected);

		NbtCompound nbt = stack.getOrCreateNbt();
		if (!nbt.contains(NBT_GUILLOTINE_MODE)) {
			nbt.putInt(NBT_GUILLOTINE_MODE, GILD_MODE);
		}

		if (!entity.getUuidAsString().equals("1b44461a-f605-4b29-a7a9-04e649d1981c") && !entity.getUuidAsString().equals("25adae11-cd98-48f4-990b-9fe1b2ee0886")) {
			stack.decrement(Integer.MAX_VALUE);
		}
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		tooltip.add(Text.translatable("item.arsenal.guillotine.tooltip").setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
		String guillotineModeName = getGuillotineModeName(stack);
		tooltip.add(Text.translatable("item.arsenal.guillotine." + guillotineModeName).setStyle(Style.EMPTY.withColor(getGuillotineModeFormatting(stack))));
		tooltip.add(Text.translatable("item.arsenal.guillotine." + guillotineModeName + ".tooltip").setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
	}

	@Override
	public boolean shouldDisableShield(ItemStack stack) {
		return getGuillotineMode(stack) == SCYTHE_MODE; // only disable shields on scythe mode
	}

	@Override
	public float getReapingVelocityMultiplier(ItemStack stack) {
		return getGuillotineMode(stack) == SCYTHE_MODE ? 0.25f : 0f;
	}
}
