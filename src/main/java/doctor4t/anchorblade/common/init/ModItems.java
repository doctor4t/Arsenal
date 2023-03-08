package doctor4t.anchorblade.common.init;

import doctor4t.anchorblade.common.Anchorblade;
import doctor4t.anchorblade.common.item.AnchorbladeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterials;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

import java.util.LinkedHashMap;
import java.util.Map;

public interface ModItems {
	Map<Item, Identifier> ITEMS = new LinkedHashMap<>();

//	Item MOD_ITEM = createItem("mod_item", new ModItem(new QuiltItemSettings()));
	Item ANCHORBLADE = createItem("anchorblade", new AnchorbladeItem( ToolMaterials.NETHERITE, 5.0f, -3.0f,new Item.Settings().rarity(Rarity.EPIC)));

	private static <T extends Item> T createItem(String name, T item) {
		ITEMS.put(item, new Identifier(Anchorblade.MOD_ID, name));
		return item;
	}

	static void initialize() {
		ITEMS.keySet().forEach(item -> {
			Registry.register(Registry.ITEM, ITEMS.get(item), item);
//			ModItemGroup.addToItemGroup(ModItemGroup.MOD_ITEMS, item);
		});
	}
}
