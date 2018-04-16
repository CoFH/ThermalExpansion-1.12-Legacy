package cofh.thermalexpansion.util.parsers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;

public class ConstantParser extends BaseParser {

	@Override
	public void parseArray(JsonArray contentArray) {

		for (JsonElement recipe : contentArray) {
			JsonObject content = recipe.getAsJsonObject();

			if (content.has(COMMENT)) {
				continue;
			}
			String name;
			ItemStack entry;

			/* CONSTANT */
			name = content.get(NAME).getAsString();

			/* OUTPUT */
			entry = parseItemStack(content.get(ENTRY));

			if (constants.put(name, entry) == null) {
				parseCount++;
			} else {
				errorCount++;
			}
		}
	}

}
