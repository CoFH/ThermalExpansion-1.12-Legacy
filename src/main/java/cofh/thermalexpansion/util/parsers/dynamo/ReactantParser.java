package cofh.thermalexpansion.util.parsers.dynamo;

import cofh.thermalexpansion.util.managers.dynamo.ReactantManager;
import cofh.thermalexpansion.util.parsers.BaseParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;

public class ReactantParser extends BaseParser {

	public static final String ELEMENTAL = "elemental";

	int defaultEnergy = ReactantManager.DEFAULT_ENERGY;

	@Override
	public void parseArray(JsonArray contentArray) {

		for (JsonElement contentElement : contentArray) {
			JsonObject content = contentElement.getAsJsonObject();

			if (content.has(COMMENT)) {
				continue;
			}
			ItemStack input;
			String fluidName;
			int energy = defaultEnergy;

			/* INPUT */
			input = parseItemStack(content.get(INPUT));

			/* FLUID */
			fluidName = content.get(FLUID).getAsString();

			/* ENERGY */
			if (content.has(ENERGY)) {
				energy = content.get(ENERGY).getAsInt();
			} else if (content.has(ENERGY_MOD)) {
				energy = content.get(ENERGY_MOD).getAsInt() * defaultEnergy / 100;
			}
			/* TYPE */
			if (content.has(TYPE) && ELEMENTAL.equals(content.get(TYPE).getAsString())) {
				if (ReactantManager.addElementalReaction(input, FluidRegistry.getFluid(fluidName), energy)) {
					parseCount++;
				} else {
					errorCount++;
				}
			} else {
				if (ReactantManager.addReaction(input, FluidRegistry.getFluid(fluidName), energy)) {
					parseCount++;
				} else {
					errorCount++;
				}
			}
		}
	}

}
