package cofh.thermalexpansion.plugins.jei;

import cofh.lib.util.helpers.StringHelper;
import gnu.trove.map.hash.THashMap;
import mezz.jei.api.IModRegistry;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

public class Descriptions {

	public static Map<ItemStack, String> descMap = new THashMap<>();

	private Descriptions() {

	}

	public static void register(IModRegistry registry) {

		ItemStack stack;
		String loc, line;
		ArrayList<String> description = new ArrayList<>();
		int i;

		for (Entry<ItemStack, String> entry : descMap.entrySet()) {
			stack = entry.getKey();
			loc = entry.getValue();
			description.clear();

			i = 0;
			line = "desc.thermalexpansion.jei." + loc + "." + i;
			while (StringHelper.canLocalize(line)) {
				description.add(line);
				i++;
				line = "desc.thermalexpansion.jei." + loc + "." + i;
			}
			registry.addDescription(stack, description.toArray(new String[description.size()]));
		}
		descMap.clear();
	}

	public static void addDescription(ItemStack stack, String loc) {

		if (stack == null || loc == null) {
			return;
		}
		descMap.put(stack, loc);
	}

}
