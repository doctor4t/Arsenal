package doctor4t.anchorblade.client;

import doctor4t.anchorblade.client.particle.SweepAttackParticle;
import doctor4t.anchorblade.client.render.item.AnchorbladeItemRenderer;
import doctor4t.anchorblade.common.init.ModItems;
import doctor4t.anchorblade.common.init.ModParticles;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

public class AnchorbladeClient implements ClientModInitializer {
	@Override
	public void onInitializeClient(ModContainer mod) {
		Identifier anchorbladeId = Registry.ITEM.getId(ModItems.ANCHORBLADE);
		AnchorbladeItemRenderer anchorbladeItemRenderer = new AnchorbladeItemRenderer(anchorbladeId);
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(anchorbladeItemRenderer);
		BuiltinItemRendererRegistry.INSTANCE.register(ModItems.ANCHORBLADE, anchorbladeItemRenderer);
		ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> {
			out.accept(new ModelIdentifier(anchorbladeId.getNamespace(), anchorbladeId.getPath() + "_gui", "inventory"));
			out.accept(new ModelIdentifier(anchorbladeId.getNamespace(), anchorbladeId.getPath() + "_handheld", "inventory"));
		});

		ParticleFactoryRegistry.getInstance().register(ModParticles.LUX_ANCHORLADE_SWEEP_1, SweepAttackParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(ModParticles.LUX_ANCHORLADE_SWEEP_2, SweepAttackParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(ModParticles.LUX_ANCHORLADE_SWEEP_3, SweepAttackParticle.Factory::new);

//		// block render layer map
//		BlockRenderLayerMap.put(RenderLayer.getCutout(), ModBlock.MOD_BLOCK);

//		// entity renderer registration
//		EntityRendererRegistry.register(ModEntities.MOD_ENTITY, ModEntityRenderer::new);
	}
}
