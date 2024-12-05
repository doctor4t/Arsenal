package dev.doctor4t.arsenal.client.particle.type;

import dev.doctor4t.arsenal.client.particle.contract.ColoredParticleInitialData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;

@Environment(EnvType.CLIENT)
public class SweepAttackParticleType extends DefaultParticleType {
    public ColoredParticleInitialData initialData;

    public SweepAttackParticleType(boolean alwaysShow) {
        super(alwaysShow);
    }

    public ParticleEffect setData(ColoredParticleInitialData target) {
        this.initialData = target;
        return this;
    }
}
