package cofh.thermalexpansion.util.parsers.machine;

import cofh.thermalexpansion.util.managers.machine.CrucibleManager;
import cofh.thermalexpansion.util.parsers.BaseParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.Set;

public class CrucibleParser extends BaseParser {

	int defaultEnergy = CrucibleManager.DEFAULT_ENERGY;

	@Override
	public void parseArray(JsonArray contentArray) {

		for (JsonElement recipe : contentArray) {
			JsonObject content = recipe.getAsJsonObject();

			if (content.has(COMMENT)) {
				continue;
			}
			ItemStack input;
			FluidStack output;
			int energy = defaultEnergy;

			/* INPUT */
			input = parseItemStack(content.get(INPUT));

			/* REMOVAL */
			if (content.has(REMOVE) && content.get(REMOVE).getAsBoolean()) {
				removeQueue.add(input);
				continue;
			}

			/* OUTPUT */
			output = parseFluidStack(content.get(OUTPUT));

			/* ENERGY */
			if (content.has(ENERGY)) {
				energy = content.get(ENERGY).getAsInt();
			} else if (content.has(ENERGY_MOD)) {
				energy = content.get(ENERGY_MOD).getAsInt() * defaultEnergy / 100;
			}

			if (CrucibleManager.addRecipe(energy, input, output) != null) {
				parseCount++;
			} else {
				errorCount++;
			}
		}
	}

	@Override
	public void postProcess() {

		for (ItemStack stack : removeQueue) {
			CrucibleManager.removeRecipe(stack);
		}
	}

	Set<ItemStack> removeQueue = new ObjectOpenHashSet<>();

}
