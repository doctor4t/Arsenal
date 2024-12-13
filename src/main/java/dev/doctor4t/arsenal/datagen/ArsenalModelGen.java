package dev.doctor4t.arsenal.datagen;

import dev.doctor4t.arsenal.Arsenal;
import dev.doctor4t.arsenal.index.ArsenalItems;
import dev.doctor4t.arsenal.item.AnchorbladeItem;
import dev.doctor4t.arsenal.item.ScytheItem;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.*;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ArsenalModelGen extends FabricModelProvider {
    public static final Model BIG_WEAPON_IN_HAND = model("item/template_big_weapon_in_hand", "_in_hand", TextureKey.LAYER0);
    public static final Model TRIDENT_IN_HAND = model("item/template_trident_in_hand", "_in_hand", TextureKey.LAYER0);


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
            registerTemplateWeapon(BIG_WEAPON_IN_HAND, value == ScytheItem.Skin.DEFAULT ? null : value.getName(), ArsenalItems.SCYTHE, generator);
        }

        registerBuiltinModel(ArsenalItems.ANCHORBLADE, generator);
        for (AnchorbladeItem.Skin value : AnchorbladeItem.Skin.values()) {
            if (value == AnchorbladeItem.Skin.AMBESSA) {
                registerTemplateWeaponInventory(BIG_WEAPON_IN_HAND, value.getName(), ArsenalItems.ANCHORBLADE, generator);
            } else {
                registerTemplateWeapon(BIG_WEAPON_IN_HAND, value == AnchorbladeItem.Skin.DEFAULT ? null : value.getName(), ArsenalItems.ANCHORBLADE, generator);
            }
        }

        registerBuiltinModel(Items.TRIDENT, generator);
        registerTemplateWeaponHandheld(TRIDENT_IN_HAND, null, Arsenal.id("trident"), generator);
        registerTemplateWeaponInventory(TRIDENT_IN_HAND, null, Arsenal.id("trident"), ModelIds.getItemModelId(Items.TRIDENT), generator);

        generator.register(ArsenalItems.WEAPON_RACK, Models.GENERATED);
    }

    private static Model model(String parent, @Nullable String variant, TextureKey... keys) {
        return new Model(Optional.of(Arsenal.id(parent)), Optional.ofNullable(variant), keys);
    }

    private static Model model(String parent, TextureKey... keys) {
        return model(parent, null, keys);
    }

    private void registerTemplateWeapon(Model templateModel, @Nullable String name, Item item, ItemModelGenerator generator) {
        this.registerTemplateWeaponHandheld(templateModel, name, item, generator);
        this.registerTemplateWeaponInventory(templateModel, name, item, generator);
    }

    private void registerTemplateWeapon(Model templateModel, @Nullable String name, Identifier itemId, ItemModelGenerator generator) {
        this.registerTemplateWeaponHandheld(templateModel, name, itemId, generator);
        this.registerTemplateWeaponInventory(templateModel, name, itemId, generator);
    }

    private void registerTemplateWeaponHandheld(Model templateModel, @Nullable String name, Item item, ItemModelGenerator generator) {
        registerTemplateWeaponHandheld(templateModel, name, Registries.ITEM.getId(item), generator);
    }

    private void registerTemplateWeaponHandheld(Model templateModel, @Nullable String name, Identifier itemId, ItemModelGenerator generator) {
        Identifier handheldModelName = (name == null ? getItemSubId(itemId, "_in_hand") : getItemSubId(itemId, "_" + name + "_in_hand"));
        Identifier handheldTexture = (name == null ? getItemId(itemId) : getItemSubId(itemId, "_" + name));

        templateModel.upload(handheldModelName, TextureMap.layer0(handheldTexture), generator.writer); // this is the actual handheld model
    }

    private void registerTemplateWeaponInventory(Model templateModel, @Nullable String name, Item item, ItemModelGenerator generator) {
        registerTemplateWeaponInventory(templateModel, name, Registries.ITEM.getId(item), generator);
    }

    private void registerTemplateWeaponInventory(Model templateModel, @Nullable String name, Identifier itemId, ItemModelGenerator generator) {
        Identifier inventoryTexture = (name == null ? getItemSubId(itemId, "_inventory") : getItemSubId(itemId, "_" + name + "_inventory"));
        registerTemplateWeaponInventory(templateModel, name, itemId, inventoryTexture, generator);
    }

    private void registerTemplateWeaponInventory(Model templateModel, @Nullable String name, Identifier itemModelId, Identifier inventoryTexture, ItemModelGenerator generator) {
        Identifier inventoryModelName = (name == null ? getItemSubId(itemModelId, "_inventory") : getItemSubId(itemModelId, "_" + name + "_inventory"));

        Models.HANDHELD.upload(inventoryModelName, TextureMap.layer0(inventoryTexture), generator.writer); // this is actually the inventory model
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

    public static Identifier getItemId(Identifier itemId) {
        return itemId.withPrefixedPath("item/");
    }

    public static Identifier getItemSubId(Identifier itemId, String suffix) {
        return itemId.withPath(path -> "item/" + path + suffix);
    }
}
