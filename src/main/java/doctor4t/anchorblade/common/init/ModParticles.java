package doctor4t.anchorblade.common.init;

import doctor4t.anchorblade.client.particle.SweepAttackParticle;
import doctor4t.anchorblade.common.Anchorblade;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.BiConsumer;

public interface ModParticles {
	//	ParticleWithTypeParticleType PARTICLE_WITH_TYPE = new ParticleWithTypeParticleType(true);
//	DefaultParticleType DEFAULT_PARTICLE = FabricParticleTypes.simple(true);
	DefaultParticleType LUX_ANCHORLADE_SWEEP_1 = FabricParticleTypes.simple(true);
	DefaultParticleType LUX_ANCHORLADE_SWEEP_2 = FabricParticleTypes.simple(true);
	DefaultParticleType LUX_ANCHORLADE_SWEEP_3 = FabricParticleTypes.simple(true);

	static void initialize() {
		initParticles(bind(Registry.PARTICLE_TYPE));
	}

	static void registerFactories() {
//		ParticleFactoryRegistry.getInstance().register(PARTICLE_WITH_TYPE, ParticleWithTypeParticleType.Factory::new);
		ParticleFactoryRegistry.getInstance().register(LUX_ANCHORLADE_SWEEP_1, SweepAttackParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(LUX_ANCHORLADE_SWEEP_2, SweepAttackParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(LUX_ANCHORLADE_SWEEP_3, SweepAttackParticle.Factory::new);
	}

	private static void initParticles(BiConsumer<ParticleType<?>, Identifier> registry) {
//		registry.accept(PARTICLE_WITH_TYPE, new Identifier(Expedition.MOD_ID, "particle_with_type"));
		registry.accept(LUX_ANCHORLADE_SWEEP_1, new Identifier(Anchorblade.MOD_ID, "lux_anchorblade_sweep_attack_1"));
		registry.accept(LUX_ANCHORLADE_SWEEP_2, new Identifier(Anchorblade.MOD_ID, "lux_anchorblade_sweep_attack_2"));
		registry.accept(LUX_ANCHORLADE_SWEEP_3, new Identifier(Anchorblade.MOD_ID, "lux_anchorblade_sweep_attack_3"));
	}

	private static <T> BiConsumer<T, Identifier> bind(Registry<? super T> registry) {
		return (t, id) -> Registry.register(registry, id, t);
	}
}

