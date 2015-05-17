package cofh.thermalexpansion.util.crafting;

import cofh.core.CoFHProps;
import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import cpw.mods.fml.common.registry.GameData;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map.Entry;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

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
					ThermalExpansion.log.debug("Crafting entry added: \"" + craftingEntry.getKey() + "\"");
				} else {
					ThermalExpansion.log
							.error("Error handling entry: \""
									+ craftingEntry.getKey()
									+ "\" > Please check the parameters. If adding a recipe, it *may* conflict with an existing recipe or entry. If removing a recipe, the recipe may not have existed.");
				}
			}
		}
	}

	private static boolean acquireCraftingEntry(String name, JsonElement templateObject) {

		JsonObject recipe = templateObject.getAsJsonObject();

		String type = recipe.get("type").getAsString().toLowerCase();
		boolean remove = false;

		if (recipe.has("remove")) {
			remove = recipe.get("remove").getAsBoolean();
		}
		if (!remove) {
			/* ADDITION */
			if (type.equals("furnace") || type.equalsIgnoreCase("furnaceAdd")) {
				return addFurnaceRecipe(name, recipe);
			} else if (type.equals("pulverizer") || type.equalsIgnoreCase("pulverizerAdd")) {
				return addPulverizerRecipe(name, recipe);
			} else if (type.equals("sawmill") || type.equalsIgnoreCase("sawmillAdd")) {
				return addSawmillRecipe(name, recipe);
			} else if (type.equals("smelter") || type.equalsIgnoreCase("smelterAdd")) {
				return addSmelterRecipe(name, recipe);
			} else if (type.equals("crucible") || type.equalsIgnoreCase("crucibleAdd")) {
				return addCrucibleRecipe(name, recipe);
			} else if (type.equals("transposer") || type.equalsIgnoreCase("transposerAdd")) {
				return addTransposerRecipe(name, recipe);
			} else if (type.equals("charger") || type.equalsIgnoreCase("chargerAdd")) {
				return addChargerRecipe(name, recipe);
			} else if (type.equals("insolator") || type.equalsIgnoreCase("insolatorAdd")) {
				return addInsolatorRecipe(name, recipe);
			}
		}
		/* REMOVAL */
		if (type.equals("furnace") || type.equals("furnaceRemove") || type.equalsIgnoreCase("furnaceRem")) {
			return removeFurnaceRecipe(name, recipe);
		} else if (type.equals("pulverizer") || type.equals("pulverizerRemove") || type.equalsIgnoreCase("pulverizerRem")) {
			return removePulverizerRecipe(name, recipe);
		} else if (type.equals("sawmill") || type.equals("sawmillRemove") || type.equalsIgnoreCase("sawmillRem")) {
			return removeSawmillRecipe(name, recipe);
		} else if (type.equals("smelter") || type.equals("smelterRemove") || type.equalsIgnoreCase("smelterRem")) {
			return removeSmelterRecipe(name, recipe);
		} else if (type.equals("crucible") || type.equals("crucibleRemove") || type.equalsIgnoreCase("crucibleRem")) {
			return removeCrucibleRecipe(name, recipe);
		} else if (type.equals("transposer") || type.equals("transposerRemove") || type.equalsIgnoreCase("transposerRem")) {
			return removeTransposerRecipe(name, recipe);
		} else if (type.equals("charger") || type.equals("chargerRemove") || type.equalsIgnoreCase("chargerRem")) {
			return removeChargerRecipe(name, recipe);
		} else if (type.equals("insolator") || type.equals("insolatorRemove") || type.equalsIgnoreCase("insolatorRem")) {
			return removeInsolatorRecipe(name, recipe);
		}
		return false;
	}

	/* ADD RECIPES */
	private static boolean addFurnaceRecipe(String name, JsonObject templateObject) {

		if (!templateObject.has("input") || !templateObject.has("output")) {
			return false;
		}
		ItemStack input = parseItemStack(templateObject.get("input"));
		ItemStack output = parseItemStack(templateObject.get("output"));
		int energy = FurnaceManager.DEFAULT_ENERGY;

		/* ENERGY */
		if (templateObject.has("energy")) {
			energy = templateObject.get("energy").getAsInt();
		}
		if (energy <= 0) {
			energy = FurnaceManager.DEFAULT_ENERGY;
		}
		return FurnaceManager.addRecipe(energy, input, output, true);
	}

	private static boolean addPulverizerRecipe(String name, JsonObject templateObject) {

		if (!templateObject.has("input")) {
			return false;
		}
		if (!templateObject.has("output") && !templateObject.has("primaryOutput")) {
			return false;
		}
		ItemStack input = parseItemStack(templateObject.get("input"));
		ItemStack primaryOutput = null;
		ItemStack secondaryOutput = null;
		int energy = PulverizerManager.DEFAULT_ENERGY;
		int secondaryChance = 100;

		/* OUTPUT */
		if (templateObject.has("output")) {
			JsonElement element = templateObject.get("output");
			if (element.isJsonObject()) {
				JsonObject outputObj = (JsonObject) element;

				primaryOutput = parseItemStack(outputObj.get("primary"));
				secondaryOutput = parseItemStack(outputObj.get("secondary"));
			} else {
				primaryOutput = parseItemStack(templateObject.get("output"));

				if (templateObject.has("secondaryOutput")) {
					secondaryOutput = parseItemStack(templateObject.get("secondaryOutput"));
				}
			}
		} else {
			if (templateObject.has("primaryOutput")) {
				primaryOutput = parseItemStack(templateObject.get("primaryOutput"));
			}
			if (templateObject.has("secondaryOutput")) {
				secondaryOutput = parseItemStack(templateObject.get("secondaryOutput"));
			}
		}
		/* ENERGY */
		if (templateObject.has("energy")) {
			energy = templateObject.get("energy").getAsInt();
		}
		if (energy <= 0) {
			energy = PulverizerManager.DEFAULT_ENERGY;
		}
		/* CHANCE */
		if (templateObject.has("chance")) {
			secondaryChance = templateObject.get("chance").getAsInt();
		} else if (templateObject.has("secondaryChance")) {
			secondaryChance = templateObject.get("secondaryChance").getAsInt();
		}
		if (secondaryChance <= 0) {
			secondaryChance = 100;
		}
		return PulverizerManager.addRecipe(energy, input, primaryOutput, secondaryOutput, secondaryChance, true);
	}

	private static boolean addSawmillRecipe(String name, JsonObject templateObject) {

		if (!templateObject.has("input")) {
			return false;
		}
		if (!templateObject.has("output") && !templateObject.has("primaryOutput")) {
			return false;
		}
		ItemStack input = parseItemStack(templateObject.get("input"));
		ItemStack primaryOutput = null;
		ItemStack secondaryOutput = null;
		int energy = SawmillManager.DEFAULT_ENERGY;
		int secondaryChance = 100;

		/* OUTPUT */
		if (templateObject.has("output")) {
			JsonElement element = templateObject.get("output");
			if (element.isJsonObject()) {
				JsonObject outputObj = (JsonObject) element;

				primaryOutput = parseItemStack(outputObj.get("primary"));
				secondaryOutput = parseItemStack(outputObj.get("secondary"));
			} else {
				primaryOutput = parseItemStack(templateObject.get("output"));

				if (templateObject.has("secondaryOutput")) {
					secondaryOutput = parseItemStack(templateObject.get("secondaryOutput"));
				}
			}
		} else {
			if (templateObject.has("primaryOutput")) {
				primaryOutput = parseItemStack(templateObject.get("primaryOutput"));
			}
			if (templateObject.has("secondaryOutput")) {
				secondaryOutput = parseItemStack(templateObject.get("secondaryOutput"));
			}
		}
		/* ENERGY */
		if (templateObject.has("energy")) {
			energy = templateObject.get("energy").getAsInt();
		}
		if (energy <= 0) {
			energy = SawmillManager.DEFAULT_ENERGY;
		}
		/* CHANCE */
		if (templateObject.has("chance")) {
			secondaryChance = templateObject.get("chance").getAsInt();
		} else if (templateObject.has("secondaryChance")) {
			secondaryChance = templateObject.get("secondaryChance").getAsInt();
		}
		if (secondaryChance <= 0) {
			secondaryChance = 100;
		}
		return SawmillManager.addRecipe(energy, input, primaryOutput, secondaryOutput, secondaryChance, true);
	}

	private static boolean addSmelterRecipe(String name, JsonObject templateObject) {

		if (!templateObject.has("input") && !templateObject.has("primaryInput")) {
			return false;
		}
		if (!templateObject.has("output") && !templateObject.has("primaryOutput")) {
			return false;
		}
		ItemStack primaryInput = null;
		ItemStack secondaryInput = null;
		ItemStack primaryOutput = null;
		ItemStack secondaryOutput = null;
		int energy = SmelterManager.DEFAULT_ENERGY;
		int secondaryChance = 100;

		/* INPUT */
		if (templateObject.has("input")) {
			JsonElement element = templateObject.get("input");
			if (element.isJsonObject()) {
				JsonObject inputObj = (JsonObject) element;
				primaryInput = parseItemStack(inputObj.get("primary"));
				secondaryInput = parseItemStack(inputObj.get("secondary"));
			} else {
				primaryInput = parseItemStack(templateObject.get("input"));
				if (templateObject.has("secondaryInput")) {
					secondaryInput = parseItemStack(templateObject.get("secondaryInput"));
				}
			}
		} else {
			if (templateObject.has("primaryInput")) {
				primaryInput = parseItemStack(templateObject.get("primaryInput"));
			}
			if (templateObject.has("secondaryInput")) {
				secondaryInput = parseItemStack(templateObject.get("secondaryInput"));
			}
		}
		/* OUTPUT */
		if (templateObject.has("output")) {
			JsonElement element = templateObject.get("output");
			if (element.isJsonObject()) {
				JsonObject outputObj = (JsonObject) element;

				primaryOutput = parseItemStack(outputObj.get("primary"));
				secondaryOutput = parseItemStack(outputObj.get("secondary"));
			} else {
				primaryOutput = parseItemStack(templateObject.get("output"));

				if (templateObject.has("secondaryOutput")) {
					secondaryOutput = parseItemStack(templateObject.get("secondaryOutput"));
				}
			}
		} else {
			if (templateObject.has("primaryOutput")) {
				primaryOutput = parseItemStack(templateObject.get("primaryOutput"));
			}
			if (templateObject.has("secondaryOutput")) {
				secondaryOutput = parseItemStack(templateObject.get("secondaryOutput"));
			}
		}
		/* ENERGY */
		if (templateObject.has("energy")) {
			energy = templateObject.get("energy").getAsInt();
		}
		if (energy <= 0) {
			energy = SmelterManager.DEFAULT_ENERGY;
		}
		/* CHANCE */
		if (templateObject.has("chance")) {
			secondaryChance = templateObject.get("chance").getAsInt();
		} else if (templateObject.has("secondaryChance")) {
			secondaryChance = templateObject.get("secondaryChance").getAsInt();
		}
		if (secondaryChance <= 0) {
			secondaryChance = 100;
		}
		return SmelterManager.addRecipe(energy, primaryInput, secondaryInput, primaryOutput, secondaryOutput, secondaryChance, true);
	}

	private static boolean addCrucibleRecipe(String name, JsonObject templateObject) {

		if (!templateObject.has("input") || !templateObject.has("output")) {
			return false;
		}
		ItemStack input = parseItemStack(templateObject.get("input"));
		FluidStack output = parseFluidStack(templateObject.get("output"));
		int energy = CrucibleManager.DEFAULT_ENERGY;

		/* ENERGY */
		if (templateObject.has("energy")) {
			energy = templateObject.get("energy").getAsInt();
		}
		if (energy <= 0) {
			energy = CrucibleManager.DEFAULT_ENERGY;
		}
		return CrucibleManager.addRecipe(energy, input, output, true);
	}

	private static boolean addTransposerRecipe(String name, JsonObject templateObject) {

		if (!templateObject.has("input") || !templateObject.has("output") || !templateObject.has("fluid")) {
			return false;
		}
		ItemStack input = parseItemStack(templateObject.get("input"));
		ItemStack output = parseItemStack(templateObject.get("output"));
		FluidStack fluid = parseFluidStack(templateObject.get("fluid"));
		int energy = TransposerManager.DEFAULT_ENERGY;
		int chance = 100;
		boolean extractRecipe = false;
		boolean reversible = false;

		/* STYLE */
		if (templateObject.has("style")) {
			String style = templateObject.get("style").getAsString();

			if (style.equalsIgnoreCase("extract")) {
				extractRecipe = true;
			}
		}
		/* REVERSIBLE */
		if (templateObject.has("reversible")) {
			reversible = templateObject.get("reversible").getAsBoolean();
		}
		/* ENERGY */
		if (templateObject.has("energy")) {
			energy = templateObject.get("energy").getAsInt();
		}
		if (energy <= 0) {
			energy = TransposerManager.DEFAULT_ENERGY;
		}
		/* CHANCE */
		if (templateObject.has("chance")) {
			chance = templateObject.get("chance").getAsInt();
			extractRecipe = true;
		} else if (templateObject.has("secondaryChance")) {
			chance = templateObject.get("secondaryChance").getAsInt();
			extractRecipe = true;
		}
		if (chance <= 0) {
			chance = 100;
		}
		if (extractRecipe) {
			return TransposerManager.addExtractionRecipe(energy, input, output, fluid, chance, reversible, true);
		}
		return TransposerManager.addFillRecipe(energy, input, output, fluid, reversible, true);
	}

	private static boolean addChargerRecipe(String name, JsonObject templateObject) {

		if (!templateObject.has("input") || !templateObject.has("output")) {
			return false;
		}
		ItemStack input = parseItemStack(templateObject.get("input"));
		ItemStack output = parseItemStack(templateObject.get("output"));
		int energy = ChargerManager.DEFAULT_ENERGY;

		/* ENERGY */
		if (templateObject.has("energy")) {
			energy = templateObject.get("energy").getAsInt();
		}
		if (energy <= 0) {
			energy = ChargerManager.DEFAULT_ENERGY;
		}
		return ChargerManager.addRecipe(energy, input, output, true);
	}

	private static boolean addInsolatorRecipe(String name, JsonObject templateObject) {

		if (!templateObject.has("input") && !templateObject.has("primaryInput")) {
			return false;
		}
		if (!templateObject.has("output") && !templateObject.has("primaryOutput")) {
			return false;
		}
		ItemStack primaryInput = null;
		ItemStack secondaryInput = null;
		ItemStack primaryOutput = null;
		ItemStack secondaryOutput = null;
		int energy = InsolatorManager.DEFAULT_ENERGY;
		int secondaryChance = 100;

		/* INPUT */
		if (templateObject.has("input")) {
			JsonElement element = templateObject.get("input");
			if (element.isJsonObject()) {
				JsonObject inputObj = (JsonObject) element;
				primaryInput = parseItemStack(inputObj.get("primary"));
				secondaryInput = parseItemStack(inputObj.get("secondary"));
			} else {
				primaryInput = parseItemStack(templateObject.get("input"));
				if (templateObject.has("secondaryInput")) {
					secondaryInput = parseItemStack(templateObject.get("secondaryInput"));
				}
			}
		} else {
			if (templateObject.has("primaryInput")) {
				primaryInput = parseItemStack(templateObject.get("primaryInput"));
			}
			if (templateObject.has("secondaryInput")) {
				secondaryInput = parseItemStack(templateObject.get("secondaryInput"));
			}
		}
		/* OUTPUT */
		if (templateObject.has("output")) {
			JsonElement element = templateObject.get("output");
			if (element.isJsonObject()) {
				JsonObject outputObj = (JsonObject) element;
				primaryOutput = parseItemStack(outputObj.get("primary"));
				secondaryOutput = parseItemStack(outputObj.get("secondary"));
			} else {
				primaryOutput = parseItemStack(templateObject.get("output"));

				if (templateObject.has("secondaryOutput")) {
					secondaryOutput = parseItemStack(templateObject.get("secondaryOutput"));
				}
			}
		} else {
			if (templateObject.has("primaryOutput")) {
				primaryOutput = parseItemStack(templateObject.get("primaryOutput"));
			}
			if (templateObject.has("secondaryOutput")) {
				secondaryOutput = parseItemStack(templateObject.get("secondaryOutput"));
			}
		}
		/* ENERGY */
		if (templateObject.has("energy")) {
			energy = templateObject.get("energy").getAsInt();
		}
		if (energy <= 0) {
			energy = InsolatorManager.DEFAULT_ENERGY;
		}
		/* CHANCE */
		if (templateObject.has("chance")) {
			secondaryChance = templateObject.get("chance").getAsInt();
		} else if (templateObject.has("secondaryChance")) {
			secondaryChance = templateObject.get("secondaryChance").getAsInt();
		}
		if (secondaryChance <= 0) {
			secondaryChance = 100;
		}
		return InsolatorManager.addRecipe(energy, primaryInput, secondaryInput, primaryOutput, secondaryOutput, secondaryChance, true);
	}

	/* REMOVE RECIPES */
	private static boolean removeFurnaceRecipe(String name, JsonObject templateObject) {

		if (!templateObject.has("input")) {
			return false;
		}
		ItemStack input = parseItemStack(templateObject.get("input"));
		return FurnaceManager.removeRecipe(input);
	}

	private static boolean removePulverizerRecipe(String name, JsonObject templateObject) {

		if (!templateObject.has("input")) {
			return false;
		}
		ItemStack input = parseItemStack(templateObject.get("input"));
		return PulverizerManager.removeRecipe(input);
	}

	private static boolean removeSawmillRecipe(String name, JsonObject templateObject) {

		if (!templateObject.has("input")) {
			return false;
		}
		ItemStack input = parseItemStack(templateObject.get("input"));
		return SawmillManager.removeRecipe(input);
	}

	private static boolean removeSmelterRecipe(String name, JsonObject templateObject) {

		if (!templateObject.has("input") && !templateObject.has("primaryInput")) {
			return false;
		}
		ItemStack primaryInput = null;
		ItemStack secondaryInput = null;

		if (templateObject.has("input")) {
			JsonElement element = templateObject.get("input");
			if (element.isJsonObject()) {
				JsonObject inputObj = (JsonObject) element;

				primaryInput = parseItemStack(inputObj.get("primary"));
				secondaryInput = parseItemStack(inputObj.get("secondary"));
			} else {
				primaryInput = parseItemStack(templateObject.get("input"));
				if (templateObject.has("secondaryInput")) {
					secondaryInput = parseItemStack(templateObject.get("secondaryInput"));
				}
			}
		} else {
			if (templateObject.has("primaryInput")) {
				primaryInput = parseItemStack(templateObject.get("primaryInput"));
			}
			if (templateObject.has("secondaryInput")) {
				secondaryInput = parseItemStack(templateObject.get("secondaryInput"));
			}
		}
		return SmelterManager.removeRecipe(primaryInput, secondaryInput);
	}

	private static boolean removeCrucibleRecipe(String name, JsonObject templateObject) {

		if (!templateObject.has("input")) {
			return false;
		}
		ItemStack input = parseItemStack(templateObject.get("input"));
		return CrucibleManager.removeRecipe(input);
	}

	private static boolean removeTransposerRecipe(String name, JsonObject templateObject) {

		if (!templateObject.has("input")) {
			return false;
		}
		ItemStack input = parseItemStack(templateObject.get("input"));
		FluidStack fluid = null;
		boolean extractRecipe = true;

		if (templateObject.has("fluid")) {
			fluid = parseFluidStack(templateObject.get("fluid"));
			extractRecipe = false;
		}
		if (extractRecipe) {
			return TransposerManager.removeExtractionRecipe(input);
		}
		return TransposerManager.removeFillRecipe(input, fluid);
	}

	private static boolean removeChargerRecipe(String name, JsonObject templateObject) {

		if (!templateObject.has("input")) {
			return false;
		}
		ItemStack input = parseItemStack(templateObject.get("input"));
		return ChargerManager.removeRecipe(input);
	}

	private static boolean removeInsolatorRecipe(String name, JsonObject templateObject) {

		if (!templateObject.has("input") && !templateObject.has("primaryInput")) {
			return false;
		}
		ItemStack primaryInput = null;
		ItemStack secondaryInput = null;

		if (templateObject.has("input")) {
			JsonElement element = templateObject.get("input");
			if (element.isJsonObject()) {
				JsonObject inputObj = (JsonObject) element;

				primaryInput = parseItemStack(inputObj.get("primary"));
				secondaryInput = parseItemStack(inputObj.get("secondary"));
			} else {
				primaryInput = parseItemStack(templateObject.get("input"));
				if (templateObject.has("secondaryInput")) {
					secondaryInput = parseItemStack(templateObject.get("secondaryInput"));
				}
			}
		} else {
			if (templateObject.has("primaryInput")) {
				primaryInput = parseItemStack(templateObject.get("primaryInput"));
			}
			if (templateObject.has("secondaryInput")) {
				secondaryInput = parseItemStack(templateObject.get("secondaryInput"));
			}
		}
		return InsolatorManager.removeRecipe(primaryInput, secondaryInput);
	}

	/* HELPERS */
	public static ItemStack parseItemStack(JsonElement itemElement) {

		if (itemElement.isJsonNull()) {
			return null;
		}
		int metadata = 0, stackSize = 1;
		ItemStack stack;

		if (itemElement.isJsonPrimitive()) {
			stack = new ItemStack(GameData.getItemRegistry().getObject(itemElement.getAsString()), 1, metadata);
		} else {
			JsonObject item = itemElement.getAsJsonObject();
			if (item.has("meta")) {
				metadata = item.get("meta").getAsInt();
			} else if (item.has("metadata")) {
				metadata = item.get("metadata").getAsInt();
			}
			if (item.has("stackSize")) {
				stackSize = item.get("stackSize").getAsInt();
			} else if (item.has("quantity")) {
				stackSize = item.get("quantity").getAsInt();
			} else if (item.has("amount")) {
				stackSize = item.get("amount").getAsInt();
			}
			if (stackSize <= 0) {
				stackSize = 1;
			}
			if (item.has("oreName") && ItemHelper.oreNameExists(item.get("oreName").getAsString())) {
				ItemStack oreStack = OreDictionary.getOres(item.get("oreName").getAsString()).get(0);
				stack = ItemHelper.cloneStack(oreStack, stackSize);
			} else {
				if (!item.has("name")) {
					ThermalExpansion.log.error("Item entry missing valid name or oreName!");
					return null;
				}
				stack = new ItemStack(GameData.getItemRegistry().getObject(item.get("name").getAsString()), stackSize, metadata);
			}
			if (item.has("nbt")) {
				try {
					NBTBase nbtbase = JsonToNBT.func_150315_a(item.get("nbt").getAsString());

					if (!(nbtbase instanceof NBTTagCompound)) {
						ThermalExpansion.log.error("Item has invalid NBT data.");
					}
					stack.setTagCompound((NBTTagCompound) nbtbase);
				} catch (NBTException t) {
					ThermalExpansion.log.error("Item has invalid NBT data.", t);
				}
			}
		}
		if (stack.getItem() == null) {
			return null;
		}
		return stack;
	}

	public static FluidStack parseFluidStack(JsonElement fluidElement) {

		if (fluidElement.isJsonNull()) {
			return null;
		}
		FluidStack stack;
		int amount = FluidContainerRegistry.BUCKET_VOLUME;

		if (fluidElement.isJsonPrimitive()) {
			stack = new FluidStack(FluidRegistry.getFluid(fluidElement.getAsString()), amount);
		} else {
			JsonObject fluid = fluidElement.getAsJsonObject();

			if (fluid.has("amount")) {
				amount = fluid.get("amount").getAsInt();
			}
			if (amount <= 0) {
				amount = FluidContainerRegistry.BUCKET_VOLUME;
			}
			if (!fluid.has("name")) {
				ThermalExpansion.log.error("Fluid entry missing valid name!");
				return null;
			}
			stack = new FluidStack(FluidRegistry.getFluid(fluidElement.getAsString()), amount);

			if (fluid.has("nbt")) {
				try {
					NBTBase nbtbase = JsonToNBT.func_150315_a(fluid.get("nbt").getAsString());

					if (!(nbtbase instanceof NBTTagCompound)) {
						ThermalExpansion.log.error("Fluid has invalid NBT data.");
					}
					stack.tag = (NBTTagCompound) nbtbase;
				} catch (NBTException t) {
					ThermalExpansion.log.error("Fluid has invalid NBT data.", t);
				}
			}
		}
		if (stack.getFluid() == null) {
			return null;
		}
		return stack;
	}

}
