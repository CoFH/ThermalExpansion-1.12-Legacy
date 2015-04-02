package cofh.thermalexpansion.util.crafting;

import cofh.core.CoFHProps;
import cofh.thermalexpansion.ThermalExpansion;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import cpw.mods.fml.common.registry.GameRegistry;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map.Entry;

import net.minecraft.block.Block;

public class TECraftingParser {

	private static File craftingFolder;

	private TECraftingParser() {

	}

	public static void initialize() {

		craftingFolder = new File(CoFHProps.configDir, "/cofh/thermalexpansion/crafting/");

		if (!craftingFolder.exists()) {
			try {
				craftingFolder.mkdir();
			} catch (Throwable t) {
				// pokemon!
			}
		}
	}

	private static void addFiles(ArrayList<File> list, File folder) {

		File[] fList = folder.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File file, String name) {

				if (name == null) {
					return false;
				}
				return name.toLowerCase().endsWith(".json") || new File(file, name).isDirectory();
			}
		});

		if (fList == null || fList.length <= 0) {
			ThermalExpansion.log.info("There are no crafting files present in " + folder + ".");
			return;
		}
		ThermalExpansion.log.info(fList.length + " crafting files present in " + folder + "/.");
		list.addAll(Arrays.asList(fList));
	}

	public static void parseCraftingFiles() {

		JsonParser parser = new JsonParser();

		ArrayList<File> craftingFileList = new ArrayList<File>();
		addFiles(craftingFileList, craftingFolder);

		for (int i = 0; i < craftingFileList.size(); ++i) {
			File craftingFile = craftingFileList.get(i);
			if (craftingFile.isDirectory()) {
				addFiles(craftingFileList, craftingFile);
				continue;
			}
			JsonObject craftingList;
			try {
				craftingList = (JsonObject) parser.parse(new FileReader(craftingFile));
			} catch (Throwable t) {
				ThermalExpansion.log.error("Critical error reading from a crafting file: " + craftingFile + " > Please be sure the file is correct!", t);
				continue;
			}
			ThermalExpansion.log.info("Reading template info from: " + craftingFile + ":");
			for (Entry<String, JsonElement> craftingEntry : craftingList.entrySet()) {
				if (acquireCraftingEntry(craftingEntry.getKey(), craftingEntry.getValue())) {
					ThermalExpansion.log.debug("Crafting entry acquired: \"" + craftingEntry.getKey() + "\"");
				} else {
					ThermalExpansion.log.error("Error acquiring entry: \"" + craftingEntry.getKey()
							+ "\" > Please check the parameters. It *may* be a duplicate.");
				}
			}
		}
	}

	private static boolean acquireCraftingEntry(String name, JsonElement templateObject) {

		JsonObject recipe = templateObject.getAsJsonObject();

		String type = recipe.get("type").getAsString().toLowerCase();

		if (type.equals("furnace")) {
			return parseFurnaceRecipe(name, recipe);
		} else if (type.equals("pulverizer")) {
			return parsePulverizerRecipe(name, recipe);
		} else if (type.equals("sawmill")) {
			return parseSawmillRecipe(name, recipe);
		} else if (type.equals("smelter")) {
			return parseSmelterRecipe(name, recipe);
		} else if (type.equals("crucible")) {
			return parseCrucibleRecipe(name, recipe);
		} else if (type.equals("transposer")) {
			return parseTransposerRecipe(name, recipe);
		} else if (type.equals("charger")) {
			return parseChargerRecipe(name, recipe);
		} else if (type.equals("insolator")) {
			return parseInsolatorRecipe(name, recipe);
		}
		return false;
	}

	private static boolean parseFurnaceRecipe(String name, JsonObject templateObject) {

		if (!templateObject.has("input") || !templateObject.has("output")) {
			return false;
		}

		return false;
	}

	private static boolean parsePulverizerRecipe(String name, JsonObject templateObject) {

		if (!templateObject.has("input")) {
			return false;
		}
		if (!templateObject.has("output") && !templateObject.has("primaryOutput")) {

		}

		return false;
	}

	private static boolean parseSawmillRecipe(String name, JsonObject templateObject) {

		if (!templateObject.has("input")) {
			return false;
		}
		if (!templateObject.has("output") && !templateObject.has("primaryOutput")) {

		}

		return false;
	}

	private static boolean parseSmelterRecipe(String name, JsonObject templateObject) {

		if (!templateObject.has("input")) {
			return false;
		}
		if (!templateObject.has("output") && !templateObject.has("primaryOutput")) {

		}

		return false;
	}

	private static boolean parseCrucibleRecipe(String name, JsonObject templateObject) {

		if (!templateObject.has("input") || !templateObject.has("output")) {
			return false;
		}

		return false;
	}

	private static boolean parseTransposerRecipe(String name, JsonObject templateObject) {

		if (!templateObject.has("input") || !templateObject.has("output")) {
			return false;
		}

		return false;
	}

	private static boolean parseChargerRecipe(String name, JsonObject templateObject) {

		if (!templateObject.has("input") || !templateObject.has("output")) {
			return false;
		}

		return false;
	}

	private static boolean parseInsolatorRecipe(String name, JsonObject templateObject) {

		if (!templateObject.has("input")) {
			return false;
		}
		if (!templateObject.has("output") && !templateObject.has("primaryOutput")) {

		}

		return false;
	}

	/* HELPERS */
	public static Block parseBlockName(String blockRaw) {

		String[] blockTokens = blockRaw.split(":", 2);
		int i = 0;
		return GameRegistry.findBlock(blockTokens.length > 1 ? blockTokens[i++] : "minecraft", blockTokens[i]);
	}

}
