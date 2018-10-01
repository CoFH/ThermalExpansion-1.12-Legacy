package cofh.thermalexpansion.util.parsers.machine;

import cofh.thermalexpansion.util.managers.machine.RefineryManager;
import cofh.thermalexpansion.util.managers.machine.SawmillManager;
import cofh.thermalexpansion.util.parsers.BaseParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.Set;

public class RefineryParser extends BaseParser {

	public static final String FOSSILFUEL = "fossil-fuel";
	public static final String FOSSIL_FUEL = "fossil_fuel";
	public static final String BIOFUEL = "bio-fuel";
	public static final String BIO_FUEL = "bio_fuel";

	int defaultEnergy = RefineryManager.DEFAULT_ENERGY;

	@Override
	public void parseArray(JsonArray contentArray) {

		for (JsonElement recipe : contentArray) {
			JsonObject content = recipe.getAsJsonObject();

			if (content.has(COMMENT)) {
				continue;
			}
			FluidStack input;
			FluidStack output;
			ItemStack output2 = ItemStack.EMPTY;
			int energy = defaultEnergy;
			int chance = 100;

			/* INPUT */
			input = parseFluidStack(content.get(INPUT));

			/* REMOVAL */
			if (content.has(REMOVE) && content.get(REMOVE).getAsBoolean()) {
				removeQueue.add(input);
				continue;
			}

			/* OUTPUT */
			output = parseFluidStack(content.get(OUTPUT));

			if (input == null || output == null) {
				errorCount++;
				continue;
			}
			if (content.has(OUTPUT2)) {
				JsonElement outputElement = content.get(OUTPUT2);
				output2 = parseItemStack(outputElement);
				chance = getChance(outputElement);
			}

			/* TYPE */
			if (content.has(TYPE)) {
				String type = content.get(TYPE).getAsString();
				if (BIOFUEL.equals(type) || BIO_FUEL.equals(type)) {
					RefineryManager.addBioFuel(input.getFluid());
				}
				if (FOSSILFUEL.equals(type) || FOSSIL_FUEL.equals(type)) {
					RefineryManager.addFossilFuel(input.getFluid());
				}
			}

			/* ENERGY */
			if (content.has(ENERGY)) {
				energy = content.get(ENERGY).getAsInt();
			} else if (content.has(ENERGY_MOD)) {
				energy = content.get(ENERGY_MOD).getAsInt() * defaultEnergy / 100;
			}

			if (RefineryManager.addRecipe(energy, input, output, output2, chance) != null) {
				parseCount++;
			} else {
				errorCount++;
			}
		}
	}

	@Override
	public void postProcess() {

		for (FluidStack stack : removeQueue) {
			RefineryManager.removeRecipe(stack);
		}
	}

	Set<FluidStack> removeQueue = new ObjectOpenHashSet<>();

}
