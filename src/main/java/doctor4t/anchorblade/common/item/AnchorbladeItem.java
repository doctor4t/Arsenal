package doctor4t.anchorblade.common.item;

import com.google.common.collect.Multimap;
import doctor4t.anchorblade.common.init.ModParticles;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import xyz.amymialee.mialeemisc.util.MialeeText;

import java.util.List;

public class AnchorbladeItem extends PickaxeItem {
	public static final DefaultParticleType[] LUX_ANCHORBLADE_SWEEP_PARTICLES = {ModParticles.LUX_ANCHORLADE_SWEEP_1, ModParticles.LUX_ANCHORLADE_SWEEP_2, ModParticles.LUX_ANCHORLADE_SWEEP_3};

	public AnchorbladeItem(ToolMaterial material, float attackDamage, float attackSpeed, Settings settings) {
		super(material, (int) attackDamage, attackSpeed, settings);
	}

	public static void spawnSweepParticles(PlayerEntity player, DefaultParticleType type) {
		double d0 = (-MathHelper.sin(player.getYaw() * ((float) Math.PI / 180F)));
		double d1 = MathHelper.cos(player.getYaw() * ((float) Math.PI / 180F));
		if (player.world instanceof ServerWorld) {
			((ServerWorld) player.world).spawnParticles(type, player.getX() + d0, player.getBodyY(0.5D), player.getZ() + d1, 0, d0, 0.0D, d1, 0.0D);
		}
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		return TypedActionResult.success(user.getStackInHand(hand));
	}

	@Override
	public Text getName(ItemStack stack) {
		return MialeeText.withColor(super.getName(stack), 0xFF8700);
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		super.appendTooltip(stack, world, tooltip, context);
	}
}
