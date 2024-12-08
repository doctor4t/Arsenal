package dev.doctor4t.arsenal.client.particle.type;

import dev.doctor4t.arsenal.client.particle.contract.ColoredParticleInitialData;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;

public class SweepParticleType extends DefaultParticleType {
    public ColoredParticleInitialData initialData;

    public SweepParticleType(boolean alwaysShow) {
        super(alwaysShow);
    }

    public ParticleEffect setData(ColoredParticleInitialData target) {
        this.initialData = target;
        return this;
    }
}
