package dev.doctor4t.arsenal.util;

import dev.doctor4t.arsenal.entity.AnchorbladeEntity;
import net.minecraft.util.Hand;

public interface AnchorOwner {
    void arsenal$setAnchor(Hand hand, AnchorbladeEntity anchor);

    AnchorbladeEntity arsenal$getAnchor(Hand hand, boolean reeling);

    boolean arsenal$isAnchorActive(Hand hand, boolean reeling);
}
