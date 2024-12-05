package dev.doctor4t.arsenal.index;

import dev.doctor4t.arsenal.Arsenal;
import dev.doctor4t.arsenal.item.AnchorbladeItem;
import dev.doctor4t.arsenal.item.ScytheItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterials;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.LinkedHashMap;
import java.util.Map;

public interface ArsenalItems {
    Map<Item, Identifier> ITEMS = new LinkedHashMap<>();

    Item SCYTHE = create("scythe", new ScytheItem(ToolMaterials.NETHERITE, 5.0f, -3.0f, new Item.Settings().rarity(Rarity.COMMON)));
    Item ANCHORBLADE = create("anchorblade", new AnchorbladeItem(AnchorbladeItem.AnchorBladeToolMaterial.INSTANCE, 5, -3.0f, new FabricItemSettings().rarity(Rarity.COMMON)));

    static <T extends Item> T create(String name, T item) {
        ITEMS.put(item, Arsenal.id(name));

        return item;
    }

    static void initialize() {
        ITEMS.forEach((item, id) -> Registry.register(Registries.ITEM, id, item));

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(ArsenalItems::addCombatEntries);
    }

    private static void addCombatEntries(FabricItemGroupEntries fabricItemGroupEntries) {
        fabricItemGroupEntries.addAfter(Items.TRIDENT, ArsenalItems.SCYTHE);
        fabricItemGroupEntries.addAfter(ArsenalItems.SCYTHE, ArsenalItems.ANCHORBLADE);
    }
}
