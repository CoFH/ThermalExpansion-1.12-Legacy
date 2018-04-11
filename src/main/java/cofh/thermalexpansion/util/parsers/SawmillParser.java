package cofh.thermalexpansion.util.parsers;

import cofh.thermalexpansion.util.managers.machine.SawmillManager;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;

public class SawmillParser extends BaseParser {

	@Override
	public boolean addRecipe(JsonObject content) {

		ItemStack input;
		ItemStack output;
		ItemStack output2 = ItemStack.EMPTY;

		int energy = SawmillManager.DEFAULT_ENERGY;
		int chance = 100;

		/* INPUT */
		input = parseItemStack(content.get(INPUT));

		/* OUTPUT */
		output = parseItemStack(content.get(OUTPUT));

		if (content.has(OUTPUT2)) {
			output2 = parseItemStack(content.get(OUTPUT2));
		}
		if (content.has(ENERGY)) {
			energy = content.get(ENERGY).getAsInt();
		}
		if (content.has(CHANCE)) {
			chance = content.get(CHANCE).getAsInt();
		}
		return SawmillManager.addRecipe(energy, input, output, output2, chance) != null;
	}

	@Override
	public boolean removeRecipe(JsonObject content) {

		ItemStack input = parseItemStack(content.get("input"));
		return SawmillManager.removeRecipe(input) != null;
	}

}
