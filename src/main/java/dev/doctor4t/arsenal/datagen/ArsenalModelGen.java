package dev.doctor4t.arsenal.datagen;

import dev.doctor4t.arsenal.Arsenal;
import dev.doctor4t.arsenal.index.ArsenalItems;
import dev.doctor4t.arsenal.item.AnchorbladeItem;
import dev.doctor4t.arsenal.item.ScytheItem;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.*;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ArsenalModelGen extends FabricModelProvider {
    public static final Model BIG_WEAPON_IN_HAND = model("item/template_big_weapon_in_hand", "_in_hand", TextureKey.LAYER0);


    public ArsenalModelGen(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator generator) {
    }

    @Override
    public void generateItemModels(ItemModelGenerator generator) {
        registerBuiltinModel(ArsenalItems.SCYTHE, generator);
        for (ScytheItem.Skin value : ScytheItem.Skin.values()) {
            registerBigWeapon(value == ScytheItem.Skin.DEFAULT ? null : value.getName(), ArsenalItems.SCYTHE, generator);
        }

        registerBuiltinModel(ArsenalItems.ANCHORBLADE, generator);
        for (AnchorbladeItem.Skin value : AnchorbladeItem.Skin.values()) {
            registerBigWeapon(value == AnchorbladeItem.Skin.DEFAULT ? null : value.getName(), ArsenalItems.ANCHORBLADE, generator);
        }

        generator.register(ArsenalItems.WEAPON_RACK, Models.GENERATED);
    }

    private static Model model(String parent, @Nullable String variant, TextureKey... keys) {
        return new Model(Optional.of(Arsenal.id(parent)), Optional.ofNullable(variant), keys);
    }

    private static Model model(String parent, TextureKey... keys) {
        return model(parent, null, keys);
    }

    private void registerBigWeapon(@Nullable String name, Item item, ItemModelGenerator generator) {
        Identifier handheldModelName = (name == null ? ModelIds.getItemSubModelId(item, "_in_hand") : ModelIds.getItemSubModelId(item, "_" + name + "_in_hand"));
        Identifier inventoryModelName = (name == null ? ModelIds.getItemSubModelId(item, "_inventory") : ModelIds.getItemSubModelId(item, "_" + name + "_inventory"));
        Identifier handheldTexture = (name == null ? TextureMap.getId(item) : TextureMap.getSubId(item, "_" + name));
        Identifier inventoryTexture = (name == null ? TextureMap.getSubId(item, "_inventory") : TextureMap.getSubId(item, "_" + name + "_inventory"));

        Models.HANDHELD.upload(inventoryModelName, TextureMap.layer0(inventoryTexture), generator.writer); // this is actually the inventory model
        BIG_WEAPON_IN_HAND.upload(handheldModelName, TextureMap.layer0(handheldTexture), generator.writer); // this is the actual handheld model
    }

    private void registerBuiltinModel(Item item, ItemModelGenerator generator) {
        generator.writer.accept(ModelIds.getItemModelId(item), new SimpleModelSupplier(new Identifier("builtin/entity")));
    }

    protected BlockStateVariant variant() {
        return BlockStateVariant.create();
    }

    protected <T> BlockStateVariant variant(VariantSetting<T> variantSetting, T value) {
        return this.variant().put(variantSetting, value);
    }

    protected <T> BlockStateVariant variant(Identifier model, VariantSetting<T> variantSetting, T value) {
        return this.model(model).put(variantSetting, value);
    }

    protected BlockStateVariant model(Identifier model) {
        return this.variant(VariantSettings.MODEL, model);
    }

    protected BlockStateVariant rotateForFace(BlockStateVariant variant, Direction direction, boolean uvlock) {
        if (uvlock) variant.put(VariantSettings.UVLOCK, true);
        switch (direction) {
            case EAST -> variant.put(VariantSettings.Y, VariantSettings.Rotation.R90);
            case SOUTH -> variant.put(VariantSettings.Y, VariantSettings.Rotation.R180);
            case WEST -> variant.put(VariantSettings.Y, VariantSettings.Rotation.R270);
            case UP -> variant.put(VariantSettings.X, VariantSettings.Rotation.R270);
            case DOWN -> variant.put(VariantSettings.X, VariantSettings.Rotation.R90);
        }
        return variant;
    }

    protected BlockStateVariant rotateForAxis(BlockStateVariant variant, Direction.Axis axis) {
        return switch (axis) {
            case X -> variant.put(VariantSettings.Y, VariantSettings.Rotation.R270);
            case Y -> variant.put(VariantSettings.X, VariantSettings.Rotation.R90);
            case Z -> variant;
        };
    }
}
