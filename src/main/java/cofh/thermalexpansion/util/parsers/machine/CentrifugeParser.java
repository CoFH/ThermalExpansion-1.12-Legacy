package cofh.thermalexpansion.util.parsers.machine;

import cofh.thermalexpansion.util.managers.machine.CentrifugeManager;
import cofh.thermalexpansion.util.parsers.BaseParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Set;

public class CentrifugeParser extends BaseParser {

	int defaultEnergy = CentrifugeManager.DEFAULT_ENERGY;

	@Override
	public void parseArray(JsonArray contentArray) {

		for (JsonElement recipe : contentArray) {
			JsonObject content = recipe.getAsJsonObject();

			if (content.has(COMMENT)) {
				continue;
			}
			ItemStack input;

			ArrayList<ItemStack> output = new ArrayList<>();
			ArrayList<Integer> chance = new ArrayList<>();
			FluidStack fluid = null;
			int energy = defaultEnergy;

			/* INPUT */
			input = parseItemStack(content.get(INPUT));

			/* OUTPUT */
			if (content.has(OUTPUT)) {
				JsonElement outputElement = content.get(OUTPUT);
				output.add(parseItemStack(outputElement));
				chance.add(getChance(outputElement));
			}
			if (content.has(OUTPUT2)) {
				JsonElement outputElement = content.get(OUTPUT2);
				output.add(parseItemStack(outputElement));
				chance.add(getChance(outputElement));
			}
			if (content.has(OUTPUT3)) {
				JsonElement outputElement = content.get(OUTPUT3);
				output.add(parseItemStack(outputElement));
				chance.add(getChance(outputElement));
			}
			if (content.has(OUTPUT4)) {
				JsonElement outputElement = content.get(OUTPUT4);
				output.add(parseItemStack(outputElement));
				chance.add(getChance(outputElement));
			}

			/* REMOVAL */
			if (content.has(REMOVE) && content.get(REMOVE).getAsBoolean()) {
				removeQueue.add(input);
				continue;
			}

			/* FLUID */
			if (content.has(FLUID)) {
				fluid = parseFluidStack(content.get(FLUID));
			}

			/* ENERGY */
			if (content.has(ENERGY)) {
				energy = content.get(ENERGY).getAsInt();
			} else if (content.has(ENERGY_MOD)) {
				energy = content.get(ENERGY_MOD).getAsInt() * defaultEnergy / 100;
			}

			if (CentrifugeManager.addRecipe(energy, input, output, chance, fluid) != null) {
				parseCount++;
			} else {
				errorCount++;
			}
		}
	}

	@Override
	public void postProcess() {

		for (ItemStack stack : removeQueue) {
			CentrifugeManager.removeRecipe(stack);
		}
	}

	Set<ItemStack> removeQueue = new ObjectOpenHashSet<>();

}
