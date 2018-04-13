package cofh.thermalexpansion.util.parsers.machine;

import cofh.thermalexpansion.util.managers.machine.CompactorManager;
import cofh.thermalexpansion.util.managers.machine.CompactorManager.Mode;
import cofh.thermalexpansion.util.parsers.BaseParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;

public class CompactorParser extends BaseParser {

	public static final String PLATE = "plate";
	public static final String COIN = "coin";
	public static final String MINT = "mint";
	public static final String GEAR = "gear";

	int defaultEnergy = CompactorManager.DEFAULT_ENERGY;

	@Override
	public void parseArray(JsonArray contentArray) {

		for (JsonElement recipe : contentArray) {
			JsonObject content = recipe.getAsJsonObject();

			if (content.has(COMMENT)) {
				continue;
			}
			ItemStack input;
			ItemStack output;
			int energy = defaultEnergy;
			Mode mode = Mode.ALL;

			/* INPUT */
			input = parseItemStack(content.get(INPUT));

			/* OUTPUT */
			output = parseItemStack(content.get(OUTPUT));

			/* TYPE */
			if (content.has(TYPE)) {
				switch (content.get(TYPE).getAsString()) {
					case PLATE:
						mode = Mode.PLATE;
						break;
					case MINT:
					case COIN:
						mode = Mode.MINT;
						break;
					case GEAR:
						mode = Mode.GEAR;
						break;
					default:
						mode = Mode.ALL;
				}
			}
			/* ENERGY */
			if (content.has(ENERGY)) {
				energy = content.get(ENERGY).getAsInt();
			} else if (content.has(ENERGY_MOD)) {
				energy = content.get(ENERGY_MOD).getAsInt() * defaultEnergy / 100;
			}
			if (CompactorManager.addRecipe(energy, input, output, mode) != null) {
				parseCount++;
			} else {
				errorCount++;
			}
		}
	}

}
