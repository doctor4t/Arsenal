package doctor4t.arsenal.common.init;

import doctor4t.arsenal.common.Arsenal;
import doctor4t.arsenal.common.item.AnchorbladeItem;
import doctor4t.arsenal.common.item.ScytheItem;
import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterials;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

import java.util.LinkedHashMap;
import java.util.Map;

public interface ModItems {
	Map<Item, Identifier> ITEMS = new LinkedHashMap<>();
//	MialeeItemGroup MOD_ITEMS = MialeeItemGroup.create(Arsenal.id("arsenal"));

	Item CLOWN_SCYTHE = createItem("clown_scythe", new ScytheItem(ToolMaterials.NETHERITE, 5.0f, -3.0f, new Item.Settings().rarity(Rarity.COMMON)));
	Item ANCHORBLADE = createItem("anchorblade", new AnchorbladeItem(AnchorbladeItem.AnchorBladeToolMaterial.INSTANCE, 5, -3.0f, new QuiltItemSettings().rarity(Rarity.COMMON)));

	private static <T extends Item> T createItem(String name, T item) {
		ITEMS.put(item, Arsenal.id(name));
		return item;
	}

	static void initialize() {
		ITEMS.keySet().forEach(item -> Registry.register(Registry.ITEM, ITEMS.get(item), item));
//		initItemGroups();
	}

//	static void initItemGroups() {
//		DefaultedList<ItemStack> stacks = DefaultedList.of();
//		ITEMS.keySet().forEach(item -> stacks.add(new ItemStack(item)));
//		MOD_ITEMS.setItems((itemStacks, itemGroup) -> {
//			for (Item item : ITEMS.keySet()) {
//				if (item == Items.AIR) continue;
//				itemStacks.add(item.getDefaultStack());
//			}
//		});
//		MOD_ITEMS.setIcon(stacks.toArray(new ItemStack[0]));
//	}
}
