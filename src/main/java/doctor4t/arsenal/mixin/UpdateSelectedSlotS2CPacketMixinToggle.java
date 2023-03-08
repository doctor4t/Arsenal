package doctor4t.arsenal.mixin;

import doctor4t.arsenal.common.util.WeaponSlotToggle;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(UpdateSelectedSlotC2SPacket.class)
public class UpdateSelectedSlotS2CPacketMixinToggle implements WeaponSlotToggle {
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
