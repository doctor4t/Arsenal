package dev.doctor4t.arsenal.entity;

import dev.doctor4t.arsenal.index.ArsenalEntities;
import dev.doctor4t.arsenal.index.ArsenalItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class WeaponRackEntity extends ItemFrameEntity {

    public WeaponRackEntity(EntityType<? extends WeaponRackEntity> entityType, World world) {
        super(entityType, world);
    }

    public WeaponRackEntity(World world, BlockPos pos, Direction facing) {
        this(ArsenalEntities.WEAPON_RACK, world, pos, facing);
    }

    public WeaponRackEntity(EntityType<? extends WeaponRackEntity> type, World world, BlockPos pos, Direction facing) {
        super(type, world, pos, facing);
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (player.isSneaking() && !this.getHeldItemStack().isEmpty()) {
            this.setInvisible(!this.isInvisible());
            return ActionResult.SUCCESS;
        }

        return super.interact(player, hand);
    }

    @Override
    public boolean isInvisible() {
        return super.isInvisible() && !this.getHeldItemStack().isEmpty();
    }

    @Override
    public boolean canStayAttached() {
        return true;
    }

    protected ItemStack getAsItemStack() {
        return new ItemStack(ArsenalItems.WEAPON_RACK);
    }
}
