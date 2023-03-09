package doctor4t.arsenal.common.init;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.ProjectileDamageSource;
import org.jetbrains.annotations.Nullable;

public interface ModDamageSources {
	static DamageSource anchor(Entity anchor, @Nullable Entity attacker) {
		return new ProjectileDamageSource("anchor", anchor, attacker).setProjectile();
	}

	static DamageSource bloodScythe(Entity bloodScythe, @Nullable Entity attacker) {
		return new ProjectileDamageSource("blood_scythe", bloodScythe, attacker).setProjectile();
	}

	static DamageSource spewing() {
		return new DamageSource("spewing").setUnblockable();
	}
}
