package doctor4t.arsenal.common.entity;

import doctor4t.arsenal.common.init.ModDamageSources;
import doctor4t.arsenal.common.init.ModEntities;
import doctor4t.arsenal.common.init.ModSoundEvents;
import doctor4t.arsenal.common.util.ProjectileSlotHolder;
import doctor4t.arsenal.common.util.WeaponSlotHolder;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class AnchorbladeEntity extends PersistentProjectileEntity {
	private ItemStack anchorbladeStack = new ItemStack(Items.TRIDENT);
	private boolean dealtDamage;
	public int returnTimer;
	private int slot = 0;

	public AnchorbladeEntity(EntityType<? extends AnchorbladeEntity> entityType, World world) {
		super(entityType, world);
	}

	public AnchorbladeEntity(World world, LivingEntity owner, ItemStack stack) {
		super(ModEntities.ANCHORBLADE, owner, world);
		this.anchorbladeStack = stack.copy();
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		if (nbt.contains("Trident", NbtElement.COMPOUND_TYPE)) {
			this.anchorbladeStack = ItemStack.fromNbt(nbt.getCompound("Trident"));
		}
		if (nbt.contains("Slot", NbtElement.INT_TYPE)) {
			this.slot = nbt.getInt("Slot");
		}
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.put("Anchorblade", this.anchorbladeStack.writeNbt(new NbtCompound()));
		nbt.putInt("Slot", this.slot);
	}

	@Override
	protected ItemStack asItemStack() {
		return this.anchorbladeStack.copy();
	}

	public int getSlot() {
		return this.slot;
	}

	public void setSlot(int slot) {
		this.slot = slot;
	}

	@Override
	public void tick() {
		Entity entity = this.getOwner();
		double d = 0.2;
		if ((this.dealtDamage || this.isNoClip()) && entity != null) {
			if (!this.isOwnerAlive()) {
				if (!this.world.isClient && this.pickupType == PersistentProjectileEntity.PickupPermission.ALLOWED) {
					this.dropStack(this.asItemStack(), 0.1F);
				}
				this.discard();
			} else {
				this.setNoClip(true);
				Vec3d vec3d = entity.getEyePos().subtract(this.getPos());
				this.setPos(this.getX(), this.getY() + vec3d.y * 0.015 * d, this.getZ());
				if (this.world.isClient) {
					this.lastRenderY = this.getY();
				}
				this.setVelocity(this.getVelocity().multiply(0.95).add(vec3d.normalize().multiply(d)));
				++this.returnTimer;
			}
		}
		if (this.getOwner() != null && this.isOwnerAlive() && this.getPos().distanceTo(this.getOwner().getPos()) > 10) {
			this.dealtDamage = true;
		}
		super.tick();
	}

	@Override
	public void setPitch(float pitch) {
		if (!this.dealtDamage) {
			super.setPitch(pitch);
		}
	}

	@Override
	public void setYaw(float yaw) {
		if (!this.dealtDamage) {
			super.setYaw(yaw);
		}
	}

	@Override
	public boolean hasNoGravity() {
		return true;
	}

	private boolean isOwnerAlive() {
		Entity entity = this.getOwner();
		if (entity == null || !entity.isAlive()) {
			return false;
		} else {
			return !(entity instanceof ServerPlayerEntity) || !entity.isSpectator();
		}
	}

	@Override
	protected void onEntityHit(EntityHitResult entityHitResult) {
		Entity entity = entityHitResult.getEntity();
		float f = 4.0F;
		if (entity instanceof LivingEntity livingEntity) {
			f += EnchantmentHelper.getAttackDamage(this.anchorbladeStack, livingEntity.getGroup());
		}
		Entity entity2 = this.getOwner();
		DamageSource damageSource = ModDamageSources.anchor(this, entity2 == null ? this : entity2);
		this.dealtDamage = true;
		SoundEvent soundEvent = this.getHitSound();
		if (entity.damage(damageSource, f)) {
			if (entity.getType() == EntityType.ENDERMAN) {
				return;
			}
			if (entity instanceof LivingEntity livingEntity2) {
				if (entity2 instanceof LivingEntity) {
					EnchantmentHelper.onUserDamaged(livingEntity2, entity2);
					EnchantmentHelper.onTargetDamaged((LivingEntity) entity2, livingEntity2);
				}
				this.onHit(livingEntity2);
			}
		}
		this.setVelocity(this.getVelocity().multiply(-0.01, -0.1, -0.01));
		this.playSound(soundEvent, 1.0f, 1.0f);
	}

	@Override
	protected boolean tryPickup(PlayerEntity player) {
		if (this.pickupType == PersistentProjectileEntity.PickupPermission.ALLOWED) {
			if (this instanceof ProjectileSlotHolder projectile) {
				if (projectile.arsenal$getOwnedSlot() != -1) {
					if (player.getInventory() instanceof WeaponSlotHolder holder) {
						if (holder.arsenal$tryInsertIntoSlot(projectile.arsenal$getOwnedSlot(), this.asItemStack())) {
							return true;
						}
					}
				}
			}
		}
		return super.tryPickup(player) || this.isNoClip() && this.isOwner(player) && player.getInventory().insertStack(this.asItemStack());
	}

	@Override
	protected SoundEvent getHitSound() {
		return ModSoundEvents.ANCHORBLADE_LAND;
	}

	@Override
	public void age() {
		if (this.pickupType != PersistentProjectileEntity.PickupPermission.ALLOWED) {
			super.age();
		}
	}

	@Override
	protected float getDragInWater() {
		return 0.99F;
	}

	@Override
	public boolean shouldRender(double cameraX, double cameraY, double cameraZ) {
		return true;
	}
}
