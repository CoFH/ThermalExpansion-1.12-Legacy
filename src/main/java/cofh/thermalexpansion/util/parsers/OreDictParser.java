package cofh.thermalexpansion.util.parsers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;

public class OreDictParser extends BaseParser {

	@Override
	public void parseArray(JsonArray contentArray) {

		for (JsonElement recipe : contentArray) {
			JsonObject content = recipe.getAsJsonObject();

			if (content.has(COMMENT)) {
				continue;
			}
			String ore;
			ItemStack entry;

			/* ORE */
			ore = content.get(ORE).getAsString();

			/* OUTPUT */
			entry = parseItemStack(content.get(ENTRY));

			if (ORES.put(ore, entry) == null) {
				parseCount++;
			} else {
				errorCount++;
			}
		}
	}

}
