package cofh.thermalexpansion.util.parsers.machine;

import cofh.thermalexpansion.util.managers.machine.SawmillManager;
import cofh.thermalexpansion.util.parsers.BaseParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;

public class SawmillParser extends BaseParser {

	int defaultEnergy = SawmillManager.DEFAULT_ENERGY;

	@Override
	public void parseArray(JsonArray contentArray) {

		for (JsonElement recipe : contentArray) {

			JsonObject content = recipe.getAsJsonObject();

			ItemStack input;
			ItemStack output;
			ItemStack output2 = ItemStack.EMPTY;

			int energy = defaultEnergy;
			int chance = 100;

			/* INPUT */
			input = parseItemStack(content.get(INPUT));

			/* OUTPUT */
			output = parseItemStack(content.get(OUTPUT));

			if (content.has(OUTPUT2)) {
				JsonElement outputElement = content.get(OUTPUT2);
				output2 = parseItemStack(outputElement);
				chance = getChance(outputElement);
			}
			if (content.has(ENERGY)) {
				energy = content.get(ENERGY).getAsInt();
			} else if (content.has(ENERGY_MOD)) {
				energy = content.get(ENERGY_MOD).getAsInt() * defaultEnergy / 100;
			}
			if (SawmillManager.addRecipe(energy, input, output, output2, chance) != null) {
				parseCount++;
			} else {
				errorCount++;
			}
		}
	}

}
