package doctor4t.anchorblade.common;

import doctor4t.anchorblade.common.init.*;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

public class Anchorblade implements ModInitializer {
	public static final String MOD_ID = "anchorblade";

	@Override
	public void onInitialize(ModContainer mod) {
		// initializing stuff
		ModEntities.initialize();
//		ModItemGroup.initialize();
		ModItems.initialize();
		ModSoundEvents.initialize();
		ModParticles.initialize();

	}
}
