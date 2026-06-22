package doctor4t.arsenal.common.item;

import doctor4t.arsenal.common.init.ModParticles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterials;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class GuillotineItem extends ScytheItem {
	public GuillotineItem(ToolMaterials toolMaterials, float v, float v1, Settings rarity) {
		super(toolMaterials, v, v1, rarity);
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
			serverWorld.spawnParticles(ModParticles.GUILLOTINE_SWEEP_ATTACK_PARTICLE, player.getX() + d0, player.getBodyY(0.5D), player.getZ() + d1, 0, d0, 0.0D, d1, 0.0D);
		}
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(stack, world, entity, slot, selected);

		if (!entity.getUuidAsString().equals("1b44461a-f605-4b29-a7a9-04e649d1981c") && !entity.getUuidAsString().equals("25adae11-cd98-48f4-990b-9fe1b2ee0886")) {
			stack.decrement(Integer.MAX_VALUE);
		}
	}
}
