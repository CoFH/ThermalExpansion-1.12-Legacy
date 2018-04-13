package cofh.thermalexpansion.util.parsers.dynamo;

import cofh.thermalexpansion.util.managers.dynamo.MagmaticManager;
import cofh.thermalexpansion.util.parsers.BaseParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class MagmaticParser extends BaseParser {

	int defaultEnergy = MagmaticManager.DEFAULT_ENERGY;

	@Override
	public void parseArray(JsonArray contentArray) {

		for (JsonElement contentElement : contentArray) {
			JsonObject content = contentElement.getAsJsonObject();

			if (content.has(COMMENT)) {
				continue;
			}
			String fluidName;
			int energy = defaultEnergy;

			/* FLUID */
			fluidName = content.get(FLUID).getAsString();

			/* ENERGY */
			if (content.has(ENERGY)) {
				energy = content.get(ENERGY).getAsInt();
			} else if (content.has(ENERGY_MOD)) {
				energy = content.get(ENERGY_MOD).getAsInt() * defaultEnergy / 100;
			}
			if (MagmaticManager.addFuel(fluidName, energy)) {
				parseCount++;
			} else {
				errorCount++;
			}
		}
	}

}
