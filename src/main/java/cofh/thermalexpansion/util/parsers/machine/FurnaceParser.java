package cofh.thermalexpansion.util.parsers.machine;

import cofh.thermalexpansion.util.managers.machine.FurnaceManager;
import cofh.thermalexpansion.util.parsers.BaseParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;

public class FurnaceParser extends BaseParser {

	public static final String CREOSOTE = "creosote";

	int defaultEnergy = FurnaceManager.DEFAULT_ENERGY;

	@Override
	public void parseArray(JsonArray contentArray) {

		for (JsonElement recipe : contentArray) {
			JsonObject content = recipe.getAsJsonObject();

			if (content.has(COMMENT)) {
				continue;
			}
			ItemStack input;
			ItemStack output;
			int creosote;
			int energy = defaultEnergy;

			/* INPUT */
			input = parseItemStack(content.get(INPUT));

			/* OUTPUT */
			output = parseItemStack(content.get(OUTPUT));

			/* ENERGY */
			if (content.has(ENERGY)) {
				energy = content.get(ENERGY).getAsInt();
			} else if (content.has(ENERGY_MOD)) {
				energy = content.get(ENERGY_MOD).getAsInt() * defaultEnergy / 100;
			}
			/* CREOSOTE */
			if (content.has(CREOSOTE)) {
				creosote = content.get(CREOSOTE).getAsInt();
				if (FurnaceManager.addRecipePyrolysis(energy, input, output, creosote) != null) {
					parseCount++;
				} else {
					errorCount++;
				}
			} else {
				if (FurnaceManager.addRecipe(energy, input, output) != null) {
					parseCount++;
				} else {
					errorCount++;
				}
			}
		}
	}

}
