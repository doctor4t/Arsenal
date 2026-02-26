package doctor4t.arsenal.common.init;

import doctor4t.arsenal.client.particle.BloodBubbleParticle;
import doctor4t.arsenal.client.particle.BloodBubbleSplatterParticle;
import doctor4t.arsenal.client.particle.ShockwaveParticle;
import doctor4t.arsenal.client.particle.SweepAttackParticle;
import doctor4t.arsenal.common.Arsenal;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.BiConsumer;

public interface ModParticles {
	DefaultParticleType CLOWN_SCYTHE_SWEEP_ATTACK_PARTICLE = FabricParticleTypes.simple(true);
	DefaultParticleType GUILLOTINE_SWEEP_ATTACK_PARTICLE = FabricParticleTypes.simple(true);
	DefaultParticleType BLOOD_BUBBLE = FabricParticleTypes.simple(true);
	DefaultParticleType BLOOD_BUBBLE_SPLATTER = FabricParticleTypes.simple(true);
	DefaultParticleType LUX_ANCHORBLADE_SWEEP_1 = FabricParticleTypes.simple(true);
	DefaultParticleType LUX_ANCHORBLADE_SWEEP_2 = FabricParticleTypes.simple(true);
	DefaultParticleType LUX_ANCHORBLADE_SWEEP_3 = FabricParticleTypes.simple(true);
	DefaultParticleType SHOCKWAVE = FabricParticleTypes.simple(true);

	static void initialize() {
		initParticles(bind(Registry.PARTICLE_TYPE));
	}

	static void registerFactories() {
		ParticleFactoryRegistry.getInstance().register(CLOWN_SCYTHE_SWEEP_ATTACK_PARTICLE, SweepAttackParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(GUILLOTINE_SWEEP_ATTACK_PARTICLE, SweepAttackParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(BLOOD_BUBBLE, BloodBubbleParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(BLOOD_BUBBLE_SPLATTER, BloodBubbleSplatterParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(LUX_ANCHORBLADE_SWEEP_1, SweepAttackParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(LUX_ANCHORBLADE_SWEEP_2, SweepAttackParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(LUX_ANCHORBLADE_SWEEP_3, SweepAttackParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(SHOCKWAVE, ShockwaveParticle.Factory::new);
	}

	private static void initParticles(BiConsumer<ParticleType<?>, Identifier> registry) {
		registry.accept(CLOWN_SCYTHE_SWEEP_ATTACK_PARTICLE, Arsenal.id("clown_scythe_sweep_attack"));
		registry.accept(GUILLOTINE_SWEEP_ATTACK_PARTICLE, Arsenal.id("guillotine_sweep_attack"));
		registry.accept(BLOOD_BUBBLE, Arsenal.id("blood_bubble"));
		registry.accept(BLOOD_BUBBLE_SPLATTER, Arsenal.id("blood_bubble_splatter"));
		registry.accept(LUX_ANCHORBLADE_SWEEP_1, Arsenal.id("lux_anchorblade_sweep_attack_1"));
		registry.accept(LUX_ANCHORBLADE_SWEEP_2, Arsenal.id("lux_anchorblade_sweep_attack_2"));
		registry.accept(LUX_ANCHORBLADE_SWEEP_3, Arsenal.id("lux_anchorblade_sweep_attack_3"));
		registry.accept(SHOCKWAVE, Arsenal.id("shockwave"));
	}

	private static <T> BiConsumer<T, Identifier> bind(Registry<? super T> registry) {
		return (t, id) -> Registry.register(registry, id, t);
	}
}
