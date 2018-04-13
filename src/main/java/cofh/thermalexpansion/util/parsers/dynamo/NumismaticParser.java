package cofh.thermalexpansion.util.parsers.dynamo;

import cofh.thermalexpansion.util.managers.dynamo.NumismaticManager;
import cofh.thermalexpansion.util.parsers.BaseParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;

public class NumismaticParser extends BaseParser {

	int defaultEnergy = NumismaticManager.DEFAULT_ENERGY;

	@Override
	public void parseArray(JsonArray contentArray) {

		for (JsonElement contentElement : contentArray) {
			JsonObject content = contentElement.getAsJsonObject();

			if (content.has(COMMENT)) {
				continue;
			}
			ItemStack input;
			int energy = defaultEnergy;

			/* INPUT */
			input = parseItemStack(content.get(INPUT));

			/* ENERGY */
			if (content.has(ENERGY)) {
				energy = content.get(ENERGY).getAsInt();
			} else if (content.has(ENERGY_MOD)) {
				energy = content.get(ENERGY_MOD).getAsInt() * defaultEnergy / 100;
			}
			if (NumismaticManager.addFuel(input, energy)) {
				parseCount++;
			} else {
				errorCount++;
			}
		}
	}

}
