package dev.doctor4t.arsenal.index;

import dev.doctor4t.arsenal.Arsenal;
import dev.doctor4t.arsenal.client.particle.BloodBubbleParticle;
import dev.doctor4t.arsenal.client.particle.BloodBubbleSplatterParticle;
import dev.doctor4t.arsenal.client.particle.ShockwaveParticle;
import dev.doctor4t.arsenal.client.particle.SweepAttackParticle;
import dev.doctor4t.arsenal.client.particle.type.SweepParticleType;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;

public interface ArsenalParticles {
    Map<ParticleType<?>, Identifier> PARTICLES = new LinkedHashMap<>();

    SweepParticleType SWEEP_PARTICLE = create("sweep", new SweepParticleType(true));
    SweepParticleType SWEEP_SHADOW_PARTICLE = create("sweep_shadow", new SweepParticleType(true));
    DefaultParticleType BLOOD_BUBBLE = create("blood_bubble", FabricParticleTypes.simple(true));
    DefaultParticleType BLOOD_BUBBLE_SPLATTER = create("blood_bubble_splatter", FabricParticleTypes.simple(true));
    DefaultParticleType SHOCKWAVE = create("shockwave", FabricParticleTypes.simple(true));

    static void initialize() {
        PARTICLES.keySet().forEach(particle -> Registry.register(Registries.PARTICLE_TYPE, PARTICLES.get(particle), particle));
    }

    private static <T extends ParticleType<?>> T create(String name, T particle) {
        PARTICLES.put(particle, Arsenal.id(name));
        return particle;
    }

    static void registerFactories() {
        ParticleFactoryRegistry.getInstance().register(SWEEP_PARTICLE, SweepAttackParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(SWEEP_SHADOW_PARTICLE, SweepAttackParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(BLOOD_BUBBLE, BloodBubbleParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(BLOOD_BUBBLE_SPLATTER, BloodBubbleSplatterParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(SHOCKWAVE, ShockwaveParticle.Factory::new);
    }
}
