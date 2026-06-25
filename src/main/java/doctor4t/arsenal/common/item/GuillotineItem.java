package doctor4t.arsenal.common.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import doctor4t.arsenal.common.Arsenal;
import doctor4t.arsenal.common.init.ModItems;
import doctor4t.arsenal.common.init.ModParticles;
import doctor4t.arsenal.common.init.ModSoundEvents;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class GuillotineItem extends ToolItem implements GUIHeldVaryingRenderItem, CustomHitParticleItem, CustomHitSoundItem, CustomColorItem, ShieldDisablingItem, ReapingItem {
	/*
		GUILLOTINE MODES:
		- Gild (0): Leech effect on hit (part of the damage is restored to the attacker as health), lowered damage
		- Scythe (1): Reaping (bring in players on crit)
		- Cleaver (2): Berserk damage scaling (damage bonus the lower the attacker's health is), disables shields, higher damage but slower speed
	 */

	public static final String NBT_GUILLOTINE_MODE = "GuillotineMode";
	public static final int SCYTHE_MODE = 0;
	public static final int GILD_MODE = 1;
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

		setAttributeModifiersForMode(0);
	}

	public static void cycleGuillotineMode(ItemStack stack) {
		NbtCompound nbt = stack.getOrCreateNbt();
		int guillotineMode = getGuillotineMode(nbt);
		int newGuillotineMode = (guillotineMode + 1) % Arsenal.GUILLOTINE_VARIATIONS.size();
		nbt.putInt(NBT_GUILLOTINE_MODE, newGuillotineMode);
		((GuillotineItem) stack.getItem()).setAttributeModifiersForMode(newGuillotineMode);
	}

	private void setAttributeModifiersForMode(int mode) {
		ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();

		// default damage and speed
		float attackDamageAddition = 0;
		float attackSpeedAddition = 0;

		if (mode == GILD_MODE) { // gild mode: lower damage
			attackDamageAddition = -2f;
		} else if (mode == CLEAVER_MODE) { // cleaver mode: slower but higher damage
			attackDamageAddition = 2f;
			attackSpeedAddition = -.6f;
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
	public ParticleEffect getHitParticle(LivingEntity attacker, Entity target, ItemStack stack) {
		return switch (getGuillotineMode(stack)) {
			case SCYTHE_MODE -> ModParticles.GUILLOTINE_SCYTHE_ATTACK_PARTICLE;
			case CLEAVER_MODE -> ModParticles.GUILLOTINE_CLEAVER_ATTACK_PARTICLE;
			default -> ModParticles.GUILLOTINE_GILD_ATTACK_PARTICLE;
		};
	}

	@Override
	public void playHitSound(LivingEntity player) {
		player.playSound(getHitSound(player.getMainHandStack()), 1.0F, 1.0F + (player.getRandom().nextFloat() * .2f - .1f));
	}

	private static SoundEvent getHitSound(ItemStack stack) {
		return switch (getGuillotineMode(stack)) {
			case SCYTHE_MODE -> ModSoundEvents.ITEM_GUILLOTINE_HIT_SCYTHE;
			case CLEAVER_MODE -> ModSoundEvents.ITEM_GUILLOTINE_HIT_CLEAVER;
			default -> ModSoundEvents.ITEM_GUILLOTINE_HIT_GILD;
		};
	}

	public static SoundEvent getTwirlSound(ItemStack stack) {
		return switch (getGuillotineMode(stack)) {
			case SCYTHE_MODE -> ModSoundEvents.ITEM_GUILLOTINE_TWIRL_SCYTHE;
			case CLEAVER_MODE -> ModSoundEvents.ITEM_GUILLOTINE_TWIRL_CLEAVER;
			default -> ModSoundEvents.ITEM_GUILLOTINE_TWIRL_GILD;
		};
	}

	@Override
	public int getNameColor() {
		return 0x746060;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack stack = user.getStackInHand(hand);
		cycleGuillotineMode(stack);
		return TypedActionResult.consume(stack);
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

	public static int getGuillotineModeColor(ItemStack stack) {
		return switch (getGuillotineMode(stack)) {
			case SCYTHE_MODE -> 0xC60000;
			case CLEAVER_MODE -> 0xFF5000;
			default -> 0xFFB200;
		};
	}

	public static boolean isGuillotineAndMode(ItemStack stack, int mode) {
		return stack.isOf(ModItems.GUILLOTINE) && GuillotineItem.getGuillotineMode(stack) == mode;
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		// default nbt
		NbtCompound nbt = stack.getOrCreateNbt();
		if (!nbt.contains(NBT_GUILLOTINE_MODE)) {
			nbt.putInt(NBT_GUILLOTINE_MODE, GILD_MODE);
		}

		// drop stack if not allowed to have it
		if (world instanceof ServerWorld serverWorld) {
			String serverMotd = serverWorld.getServer().getServerMotd();
//			if (serverMotd != null && !serverMotd.equals("balls")) {
//				stack.decrement(Integer.MAX_VALUE);
//			}
		}
		super.inventoryTick(stack, world, entity, slot, selected);
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		String guillotineModeName = getGuillotineModeName(stack);
		tooltip.add(Text.translatable("item.arsenal.guillotine.tooltip").setStyle(Style.EMPTY.withColor(Formatting.GRAY)).append(
			Text.translatable("item.arsenal.guillotine." + guillotineModeName).setStyle(Style.EMPTY.withColor(getGuillotineModeColor(stack)))
		));
		tooltip.add(Text.translatable("item.arsenal.guillotine." + guillotineModeName + ".tooltip").setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY)));
	}

	@Override
	public boolean shouldDisableShield(ItemStack stack) {
		return getGuillotineMode(stack) == SCYTHE_MODE; // only disable shields on scythe mode
	}

	@Override
	public float getReapingVelocityMultiplier(ItemStack stack) {
		return getGuillotineMode(stack) == SCYTHE_MODE ? 0.25f : 0f;
	}

	@Override
	public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
		return !miner.isCreative();
	}
}
