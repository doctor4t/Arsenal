package dev.doctor4t.arsenal.client.render.item;

import dev.doctor4t.arsenal.Arsenal;
import dev.doctor4t.arsenal.index.ArsenalCosmetics;
import dev.doctor4t.arsenal.item.ScytheItem;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ScytheDynamicItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    public static final List<ModelIdentifier> MODELS_TO_REGISTER = new ArrayList<>();

    public static final Pair<ModelIdentifier, ModelIdentifier> DEFAULT_MODEL_IDENTIFIER = registerVariantModelPair("");
    public static final Pair<ModelIdentifier, ModelIdentifier> CLOWN_MODEL_IDENTIFIER = registerVariantModelPair(ScytheItem.Skin.CLOWN.getName());
    public static final Pair<ModelIdentifier, ModelIdentifier> CARRION_MODEL_IDENTIFIER = registerVariantModelPair(ScytheItem.Skin.CARRION.getName());
    public static final Pair<ModelIdentifier, ModelIdentifier> GILDED_MODEL_IDENTIFIER = registerVariantModelPair(ScytheItem.Skin.GILDED.getName());
    public static final Pair<ModelIdentifier, ModelIdentifier> ROZE_MODEL_IDENTIFIER = registerVariantModelPair(ScytheItem.Skin.ROZE.getName());
    public static final Pair<ModelIdentifier, ModelIdentifier> FOLLY_MODEL_IDENTIFIER = registerVariantModelPair(ScytheItem.Skin.FOLLY.getName());
    public static final Pair<ModelIdentifier, ModelIdentifier> SCISSORS_MODEL_IDENTIFIER = registerVariantModelPair(ScytheItem.Skin.SCISSORS.getName());

    private static @NotNull Pair<ModelIdentifier, ModelIdentifier> registerVariantModelPair(String name) {
        String s = "scythe" + (name.isEmpty() ? "" : "_") + name;

        ModelIdentifier inventoryModelIdentifier = new ModelIdentifier(Arsenal.id(s + "_inventory"), "inventory");
        ModelIdentifier inHandModelIdentifier = new ModelIdentifier(Arsenal.id(s + "_in_hand"), "inventory");

        MODELS_TO_REGISTER.add(inventoryModelIdentifier);
        MODELS_TO_REGISTER.add(inHandModelIdentifier);

        return new Pair<>(inventoryModelIdentifier, inHandModelIdentifier);
    }

    private static @NotNull Pair<ModelIdentifier, ModelIdentifier> getModelIdentifierModelIdentifierPair(ItemStack stack) {
        Pair<ModelIdentifier, ModelIdentifier> modelIdentifierPair = DEFAULT_MODEL_IDENTIFIER;
        ScytheItem.Skin skin = ScytheItem.Skin.fromString(ArsenalCosmetics.getSkin(stack));
        if (skin != null) {
            switch (skin) {
                case CLOWN -> modelIdentifierPair = CLOWN_MODEL_IDENTIFIER;
                case CARRION -> modelIdentifierPair = CARRION_MODEL_IDENTIFIER;
                case GILDED -> modelIdentifierPair = GILDED_MODEL_IDENTIFIER;
                case ROZE -> modelIdentifierPair = ROZE_MODEL_IDENTIFIER;
                case FOLLY -> modelIdentifierPair = FOLLY_MODEL_IDENTIFIER;
                case SCISSORS -> modelIdentifierPair = SCISSORS_MODEL_IDENTIFIER;
            }
        }
        return modelIdentifierPair;
    }

    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        boolean inHand = mode.isFirstPerson() || mode == ModelTransformationMode.THIRD_PERSON_LEFT_HAND || mode == ModelTransformationMode.THIRD_PERSON_RIGHT_HAND || mode == ModelTransformationMode.HEAD || mode == ModelTransformationMode.FIXED;
        boolean inInventory = mode == ModelTransformationMode.GUI;

        matrices.push();
        matrices.translate(.5, .5, .5);

        Pair<ModelIdentifier, ModelIdentifier> modelIdentifierPair = getModelIdentifierModelIdentifierPair(stack);
        BakedModel model = MinecraftClient.getInstance().getBakedModelManager().getModel(!inHand ? modelIdentifierPair.getLeft() : modelIdentifierPair.getRight());

        if (inInventory) {
            DiffuseLighting.disableGuiDepthLighting();
        }

        MinecraftClient.getInstance().getItemRenderer().renderItem(stack, mode, false, matrices, vertexConsumers, light, overlay, model);
        if (vertexConsumers instanceof VertexConsumerProvider.Immediate immediate) {
            immediate.draw();
        }

        if (inInventory) {
            DiffuseLighting.enableGuiDepthLighting();
        }

        matrices.pop();
    }
}
