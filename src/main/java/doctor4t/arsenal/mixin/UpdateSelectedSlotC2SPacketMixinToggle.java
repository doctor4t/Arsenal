package doctor4t.arsenal.mixin;

import doctor4t.arsenal.common.util.WeaponSlotToggle;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(UpdateSelectedSlotS2CPacket.class)
public class UpdateSelectedSlotC2SPacketMixinToggle implements WeaponSlotToggle {
	@Unique private boolean selectedWeapon = false;

	@Override
	public void arsenal$setWeaponSlot(boolean weaponSlot) {
		this.selectedWeapon = weaponSlot;
	}

	@Override
	public boolean arsenal$shouldWeaponSlot() {
		return this.selectedWeapon;
	}
}
