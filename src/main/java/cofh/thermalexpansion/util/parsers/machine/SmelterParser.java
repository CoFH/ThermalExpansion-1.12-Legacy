package cofh.thermalexpansion.util.parsers.machine;

import cofh.thermalexpansion.util.managers.machine.SmelterManager;
import cofh.thermalexpansion.util.parsers.BaseParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Set;

public class SmelterParser extends BaseParser {

	int defaultEnergy = SmelterManager.DEFAULT_ENERGY;

	@Override
	public void parseArray(JsonArray contentArray) {

		for (JsonElement recipe : contentArray) {
			JsonObject content = recipe.getAsJsonObject();

			if (content.has(COMMENT)) {
				continue;
			}
			ItemStack input;
			ItemStack input2;
			ItemStack output;
			ItemStack output2 = ItemStack.EMPTY;
			int energy = defaultEnergy;
			int chance = 100;

			/* INPUT */
			input = parseItemStack(content.get(INPUT));
			input2 = parseItemStack(content.get(INPUT2));

			/* REMOVAL */
			if (content.has(REMOVE) && content.get(REMOVE).getAsBoolean()) {
				removeQueue.add(Pair.of(input, input2));
				continue;
			}

			/* OUTPUT */
			output = parseItemStack(content.get(OUTPUT));

			if (content.has(OUTPUT2)) {
				JsonElement outputElement = content.get(OUTPUT2);
				output2 = parseItemStack(outputElement);
				chance = getChance(outputElement);
			}

			/* ENERGY */
			if (content.has(ENERGY)) {
				energy = content.get(ENERGY).getAsInt();
			} else if (content.has(ENERGY_MOD)) {
				energy = content.get(ENERGY_MOD).getAsInt() * defaultEnergy / 100;
			}

			if (SmelterManager.addRecipe(energy, input, input2, output, output2, chance) != null) {
				parseCount++;
			} else {
				errorCount++;
			}
		}
	}

	@Override
	public void postProcess() {

		for (Pair<ItemStack, ItemStack> removal : removeQueue) {
			SmelterManager.removeRecipe(removal.getLeft(), removal.getRight());
			SmelterManager.removeRecipe(removal.getRight(), removal.getLeft());
		}
	}

	Set<Pair<ItemStack, ItemStack>> removeQueue = new ObjectOpenHashSet<>();
}
