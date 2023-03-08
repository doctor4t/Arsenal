package doctor4t.anchorblade.mixin;

import doctor4t.anchorblade.common.util.AnchorSelection;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(UpdateSelectedSlotS2CPacket.class)
public class UpdateSelectedSlotC2SPacketMixin implements AnchorSelection {
	@Unique private boolean selectedAnchor = false;

	@Override
	public void anchorblade$setSelectedAnchor(boolean selectedAnchor) {
		this.selectedAnchor = selectedAnchor;
	}

	@Override
	public boolean anchorblade$hasSelectedAnchor() {
		return this.selectedAnchor;
	}
}
