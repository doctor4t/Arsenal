package doctor4t.arsenal.common.util;

import doctor4t.arsenal.common.entity.AnchorbladeEntity;

public interface AnchorOwner {
	void arsenal$setAnchor(AnchorbladeEntity anchor);
	AnchorbladeEntity arsenal$getAnchor(boolean reeling);
	boolean arsenal$isAnchorActive(boolean reeling);
}
