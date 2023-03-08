package doctor4t.anchorblade.mixin;

import doctor4t.anchorblade.common.util.AnchorSelection;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(UpdateSelectedSlotC2SPacket.class)
public class UpdateSelectedSlotS2CPacketMixin implements AnchorSelection {
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
