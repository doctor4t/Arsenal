package doctor4t.arsenal.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import doctor4t.arsenal.client.render.item.GuillotineTwirlItemRendererEntry;
import doctor4t.arsenal.common.init.ModItems;
import doctor4t.arsenal.common.item.GUIHeldVaryingRenderItem;
import doctor4t.arsenal.common.item.GuillotineItem;
import doctor4t.arsenal.common.util.Easing;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.sound.EntityTrackingSoundInstance;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Pair;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.UUID;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
	@Unique
	Pair<HashMap<UUID, GuillotineTwirlItemRendererEntry>, HashMap<UUID, GuillotineTwirlItemRendererEntry>> guillotineTwirlEntries = new Pair<>(new HashMap<>(), new HashMap<>());

	@Shadow @Final private ItemModels models;

	@Inject(method = "getModel", at = @At("HEAD"), cancellable = true)
	private void arsenal$getInventoryItem(ItemStack stack, World world, LivingEntity entity, int seed, CallbackInfoReturnable<BakedModel> cir) {
		if (stack.getItem() instanceof GUIHeldVaryingRenderItem) {
			BakedModel bakedModel = this.models.getModelManager().getModel(new ModelIdentifier("minecraft:trident_in_hand#inventory")); // this is the model type (not the texture), its insane that copy-pasting this works first try
			ClientWorld clientWorld = world instanceof ClientWorld ? (ClientWorld) world : null;
			BakedModel bakedModel2 = bakedModel.getOverrides().apply(bakedModel, stack, clientWorld, entity, seed);
			cir.setReturnValue(bakedModel2 == null ? this.models.getModelManager().getMissingModel() : bakedModel2);
		}
	}

	@WrapMethod(method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/world/World;III)V")
	public void renderItem(LivingEntity entity, ItemStack stack, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, World world, int light, int overlay, int seed, Operation<Void> original) {
		if (stack.isOf(ModItems.GUILLOTINE)
			&& (renderMode.equals(ModelTransformation.Mode.FIRST_PERSON_RIGHT_HAND) || renderMode.equals(ModelTransformation.Mode.FIRST_PERSON_LEFT_HAND) || renderMode.equals(ModelTransformation.Mode.THIRD_PERSON_RIGHT_HAND) || renderMode.equals(ModelTransformation.Mode.THIRD_PERSON_LEFT_HAND))) {
			HashMap<UUID, GuillotineTwirlItemRendererEntry> guillotineMap = leftHanded ? guillotineTwirlEntries.getLeft() : guillotineTwirlEntries.getRight();
			UUID uuid = entity.getUuid();
			GuillotineTwirlItemRendererEntry g = guillotineMap.get(uuid);
			if (g == null) {
				guillotineMap.put(uuid, new GuillotineTwirlItemRendererEntry(ItemStack.EMPTY, ItemStack.EMPTY, -Integer.MAX_VALUE));
				g = guillotineMap.get(uuid);
			}

			// cache previous rendered guillotine mode to know when to twirl
			MinecraftClient client = MinecraftClient.getInstance();
			long time = client.world.getTime() % 1000L;
			if (g.cachedStack.isOf(ModItems.GUILLOTINE)
				&& g.cachedStack.getName().equals(stack.getName())
				&& GuillotineItem.getGuillotineMode(g.cachedStack) != GuillotineItem.getGuillotineMode(stack)) {

				g.twirlStartTime = time;
				g.prevModeStack = stack.copy();
				g.prevModeStack.getOrCreateNbt().putInt(GuillotineItem.NBT_GUILLOTINE_MODE, GuillotineItem.getGuillotineMode(g.cachedStack));
				client.execute(() -> client.getSoundManager().play(new EntityTrackingSoundInstance(GuillotineItem.getTwirlSound(entity.getMainHandStack()), SoundCategory.MASTER, 1.0F, 1.0F, entity, seed)));
			}
			g.cachedStack = stack.copy();

			float twirlTime = 10f;
			float switchDelta = .3f;

			float twirlDelta = MathHelper.map(time + client.getTickDelta(), g.twirlStartTime, g.twirlStartTime + twirlTime, 0f, 1f);
			if (twirlDelta >= 0f && twirlDelta <= 1f) {
				float easedTwirlDelta = Easing.IN_OUT_SINE.apply(twirlDelta);
				matrices.push();
				matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(easedTwirlDelta * 360f));
				float rotOffset = .5f;
				matrices.multiply(Vec3f.POSITIVE_Z.getRadialQuaternion((float) Math.sin(easedTwirlDelta * Math.PI) * (leftHanded ? rotOffset : -rotOffset)));
				original.call(entity, easedTwirlDelta < switchDelta ? g.prevModeStack : stack, renderMode, leftHanded, matrices, vertexConsumers, world, light, overlay, seed);
				matrices.pop();
				return;
			}
		}

		original.call(entity, stack, renderMode, leftHanded, matrices, vertexConsumers, world, light, overlay, seed);
	}
}
