package cofh.thermalexpansion.util.managers.machine;

import cofh.core.inventory.ComparableItemStack;
import cofh.core.inventory.ComparableItemStackValidated;
import cofh.core.inventory.OreValidator;
import cofh.core.util.helpers.ItemHelper;
import cofh.core.util.helpers.StringHelper;
import cofh.thermalfoundation.init.TFEquipment.ArmorSet;
import cofh.thermalfoundation.init.TFEquipment.HorseArmor;
import cofh.thermalfoundation.init.TFEquipment.ToolSet;
import cofh.thermalfoundation.init.TFEquipment.ToolSetVanilla;
import cofh.thermalfoundation.item.ItemMaterial;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static java.util.Arrays.asList;

public class SmelterManager {

	private static Map<List<ComparableItemStackValidated>, SmelterRecipe> recipeMap = new Object2ObjectOpenHashMap<>();
	private static Set<ComparableItemStackValidated> validationSet = new ObjectOpenHashSet<>();
	private static Set<ComparableItemStackValidated> lockSet = new ObjectOpenHashSet<>();
	private static OreValidator oreValidator = new OreValidator();

	static {
		oreValidator.addPrefix(ComparableItemStack.BLOCK);
		oreValidator.addPrefix(ComparableItemStack.ORE);
		oreValidator.addPrefix(ComparableItemStack.DUST);
		oreValidator.addPrefix(ComparableItemStack.INGOT);
		oreValidator.addPrefix(ComparableItemStack.NUGGET);
		oreValidator.addPrefix(ComparableItemStack.GEM);
		oreValidator.addPrefix(ComparableItemStack.PLATE);

		oreValidator.addExact("sand");

		oreValidator.addExact("crystalSlag");
		oreValidator.addExact("crystalSlagRich");
		oreValidator.addExact("crystalCinnabar");

		oreValidator.addExact("itemSlag");
		oreValidator.addExact("itemSlagRich");
		oreValidator.addExact("itemCinnabar");
		oreValidator.addExact("fuelCoke");
	}

	static final ItemStack BLOCK_SAND = new ItemStack(Blocks.SAND);
	static final ItemStack BLOCK_SOUL_SAND = new ItemStack(Blocks.SOUL_SAND);

	static final int ORE_MULTIPLIER = 2;
	static final int ORE_MULTIPLIER_SPECIAL = 3;

	public static final int DEFAULT_ENERGY = 4000;

	private static SmelterRecipe getRecipeFwd(ItemStack primaryInput, ItemStack secondaryInput) {

		ComparableItemStackValidated query = convertInput(primaryInput);
		ComparableItemStackValidated querySecondary = convertInput(secondaryInput);

		SmelterRecipe recipe = recipeMap.get(asList(query, querySecondary));

		if (recipe == null) {
			if (isItemFlux(primaryInput)) {
				querySecondary.metadata = OreDictionary.WILDCARD_VALUE;
			} else {
				query.metadata = OreDictionary.WILDCARD_VALUE;
			}
			recipe = recipeMap.get(asList(query, querySecondary));
		}
		return recipe;
	}

	//	private static SmelterRecipe getRecipeRev(ItemStack primaryInput, ItemStack secondaryInput) {
	//
	//		ComparableItemStackValidated query = convertInput(primaryInput);
	//		ComparableItemStackValidated querySecondary = convertInput(secondaryInput);
	//
	//		SmelterRecipe recipe = recipeMap.get(asList(querySecondary, query));
	//
	//		if (recipe == null) {
	//			if (isItemFlux(primaryInput)) {
	//				querySecondary.metadata = OreDictionary.WILDCARD_VALUE;
	//			} else {
	//				query.metadata = OreDictionary.WILDCARD_VALUE;
	//			}
	//			recipe = recipeMap.get(asList(querySecondary, query));
	//		}
	//		return recipe;
	//	}

	public static boolean isRecipeReversed(ItemStack primaryInput, ItemStack secondaryInput) {

		if (primaryInput.isEmpty() || secondaryInput.isEmpty()) {
			return false;
		}
		return getRecipeFwd(primaryInput, secondaryInput) == null;
	}

	public static SmelterRecipe getRecipe(ItemStack primaryInput, ItemStack secondaryInput) {

		if (primaryInput.isEmpty() || secondaryInput.isEmpty()) {
			return null;
		}
		ComparableItemStackValidated query = convertInput(primaryInput);
		ComparableItemStackValidated querySecondary = convertInput(secondaryInput);

		SmelterRecipe recipe = recipeMap.get(asList(query, querySecondary));

		if (recipe == null) {
			recipe = recipeMap.get(asList(querySecondary, query));
		}
		if (recipe == null) {
			if (isItemFlux(primaryInput)) {
				querySecondary.metadata = OreDictionary.WILDCARD_VALUE;
			} else {
				query.metadata = OreDictionary.WILDCARD_VALUE;
			}
			recipe = recipeMap.get(asList(query, querySecondary));
			if (recipe == null) {
				recipe = recipeMap.get(asList(querySecondary, query));
			}
		}
		if (recipe == null) {
			return null;
		}
		return recipe;
	}

	public static boolean recipeExists(ItemStack primaryInput, ItemStack secondaryInput) {

		return getRecipe(primaryInput, secondaryInput) != null;
	}

	public static SmelterRecipe[] getRecipeList() {

		return recipeMap.values().toArray(new SmelterRecipe[0]);
	}

	public static boolean isItemValid(ItemStack input) {

		if (input.isEmpty()) {
			return false;
		}
		ComparableItemStackValidated query = convertInput(input);
		if (validationSet.contains(query)) {
			return true;
		}
		query.metadata = OreDictionary.WILDCARD_VALUE;
		return validationSet.contains(query);
	}

	public static boolean isItemFlux(ItemStack input) {

		return !input.isEmpty() && lockSet.contains(convertInput(input));
	}

	public static void preInit() {

		/* FLUXES */
		{
			addFlux(BLOCK_SAND);
			addFlux(BLOCK_SOUL_SAND);
			addFlux(ItemMaterial.crystalSlagRich);
			addFlux(ItemMaterial.crystalCinnabar);
		}
	}

	public static void initialize() {

		/* ORES */
		{
			addDefaultRecipes("Iron", ItemMaterial.ingotIron, ItemMaterial.ingotNickel);
			addDefaultRecipes("Gold", ItemMaterial.ingotGold, ItemStack.EMPTY, 20);

			addDefaultRecipes("Copper", ItemMaterial.ingotCopper, ItemMaterial.ingotGold);
			addDefaultRecipes("Tin", ItemMaterial.ingotTin, ItemMaterial.ingotIron);
			addDefaultRecipes("Silver", ItemMaterial.ingotSilver, ItemMaterial.ingotLead);
			addDefaultRecipes("Aluminum", ItemMaterial.ingotAluminum, ItemMaterial.ingotIron);
			addDefaultRecipes("Lead", ItemMaterial.ingotLead, ItemMaterial.ingotSilver);
			addDefaultRecipes("Nickel", ItemMaterial.ingotNickel, ItemMaterial.ingotPlatinum, 15);
			addDefaultRecipes("Platinum", ItemMaterial.ingotPlatinum, ItemMaterial.ingotIridium);
			addDefaultRecipes("Iridium", ItemMaterial.ingotIridium, ItemMaterial.ingotPlatinum);
			addDefaultRecipes("Mithril", ItemMaterial.ingotMithril, ItemMaterial.ingotGold);
		}

		/* DUSTS */
		{
			addDefaultRecipes("Steel", ItemMaterial.ingotSteel);
			addDefaultRecipes("Electrum", ItemMaterial.ingotElectrum);
			addDefaultRecipes("Invar", ItemMaterial.ingotInvar);
			addDefaultRecipes("Bronze", ItemMaterial.ingotBronze);
			addDefaultRecipes("Constantan", ItemMaterial.ingotConstantan);
			addDefaultRecipes("Signalum", ItemMaterial.ingotSignalum);
			addDefaultRecipes("Lumium", ItemMaterial.ingotLumium);
			addDefaultRecipes("Enderium", ItemMaterial.ingotEnderium);
		}

		/* RECYCLING */
		{
			// Output is 1/2, round down, minimum of 1.

			/* IRON */
			int energy = DEFAULT_ENERGY * 3 / 2;
			ItemStack ingot = new ItemStack(Items.IRON_INGOT);

			addRecycleRecipe(energy, new ItemStack(Items.IRON_SWORD), ingot, 1);
			addRecycleRecipe(energy, new ItemStack(Items.IRON_PICKAXE), ingot, 1);
			addRecycleRecipe(energy, new ItemStack(Items.IRON_AXE), ingot, 1);
			addRecycleRecipe(energy, new ItemStack(Items.IRON_SHOVEL), ingot, 1);
			addRecycleRecipe(energy, new ItemStack(Items.IRON_HOE), ingot, 1);

			addRecycleRecipe(energy, new ItemStack(Items.IRON_HELMET), ingot, 2);
			addRecycleRecipe(energy, new ItemStack(Items.IRON_CHESTPLATE), ingot, 4);
			addRecycleRecipe(energy, new ItemStack(Items.IRON_LEGGINGS), ingot, 3);
			addRecycleRecipe(energy, new ItemStack(Items.IRON_BOOTS), ingot, 2);

			addRecycleRecipe(energy, new ItemStack(Items.IRON_HORSE_ARMOR), ingot, 2, false);

			/* GOLD */
			ingot = new ItemStack(Items.GOLD_INGOT);

			addRecycleRecipe(energy, new ItemStack(Items.GOLDEN_SWORD), ingot, 1);
			addRecycleRecipe(energy, new ItemStack(Items.GOLDEN_PICKAXE), ingot, 1);
			addRecycleRecipe(energy, new ItemStack(Items.GOLDEN_AXE), ingot, 1);
			addRecycleRecipe(energy, new ItemStack(Items.GOLDEN_SHOVEL), ingot, 1);
			addRecycleRecipe(energy, new ItemStack(Items.GOLDEN_HOE), ingot, 1);

			addRecycleRecipe(energy, new ItemStack(Items.GOLDEN_HELMET), ingot, 2);
			addRecycleRecipe(energy, new ItemStack(Items.GOLDEN_CHESTPLATE), ingot, 4);
			addRecycleRecipe(energy, new ItemStack(Items.GOLDEN_LEGGINGS), ingot, 3);
			addRecycleRecipe(energy, new ItemStack(Items.GOLDEN_BOOTS), ingot, 2);

			addRecycleRecipe(energy, new ItemStack(Items.GOLDEN_HORSE_ARMOR), ingot, 2, false);

			/* THERMAL FOUNDATION */
			for (ToolSetVanilla tool : new ToolSetVanilla[] { ToolSetVanilla.IRON, ToolSetVanilla.GOLD }) {
				ingot = ItemHelper.getOre(tool.ingot);

				if (tool.enable[0]) {
					addRecycleRecipe(energy, tool.toolBow, ingot, 1);
				}
				if (tool.enable[1]) {
					addRecycleRecipe(energy, tool.toolFishingRod, ingot, 1);
				}
				if (tool.enable[2]) {
					addRecycleRecipe(energy, tool.toolShears, ingot, 1);
				}
				if (tool.enable[3]) {
					addRecycleRecipe(energy, tool.toolSickle, ingot, 1);
				}
				if (tool.enable[4]) {
					addRecycleRecipe(energy, tool.toolHammer, ingot, 2);
				}
				if (tool.enable[5]) {
					addRecycleRecipe(energy, tool.toolExcavator, ingot, 1);
				}
				if (tool.enable[6]) {
					addRecycleRecipe(energy, tool.toolShield, ingot, 3);
				}
			}
			for (ToolSet tool : ToolSet.values()) {
				ingot = ItemHelper.getOre(tool.ingot);

				if (tool.enable[0]) {
					addRecycleRecipe(energy, tool.toolSword, ingot, 1);
				}
				if (tool.enable[1]) {
					addRecycleRecipe(energy, tool.toolPickaxe, ingot, 1);
				}
				if (tool.enable[2]) {
					addRecycleRecipe(energy, tool.toolAxe, ingot, 1);
				}
				if (tool.enable[3]) {
					addRecycleRecipe(energy, tool.toolShovel, ingot, 1);
				}
				if (tool.enable[4]) {
					addRecycleRecipe(energy, tool.toolHoe, ingot, 1);
				}
				if (tool.enable[5]) {
					addRecycleRecipe(energy, tool.toolBow, ingot, 1);
				}
				if (tool.enable[6]) {
					addRecycleRecipe(energy, tool.toolFishingRod, ingot, 1);
				}
				if (tool.enable[7]) {
					addRecycleRecipe(energy, tool.toolShears, ingot, 1);
				}
				if (tool.enable[8]) {
					addRecycleRecipe(energy, tool.toolSickle, ingot, 1);
				}
				if (tool.enable[9]) {
					addRecycleRecipe(energy, tool.toolHammer, ingot, 2);
				}
				if (tool.enable[10]) {
					addRecycleRecipe(energy, tool.toolExcavator, ingot, 1);
				}
				if (tool.enable[11]) {
					addRecycleRecipe(energy, tool.toolShield, ingot, 3);
				}
			}
			for (ArmorSet armor : ArmorSet.values()) {
				ingot = ItemHelper.getOre(armor.ingot);

				if (armor.enable[0]) {
					addRecycleRecipe(energy, armor.armorHelmet, ingot, 2);
				}
				if (armor.enable[1]) {
					addRecycleRecipe(energy, armor.armorChestplate, ingot, 4);
				}
				if (armor.enable[2]) {
					addRecycleRecipe(energy, armor.armorLegs, ingot, 3);
				}
				if (armor.enable[3]) {
					addRecycleRecipe(energy, armor.armorBoots, ingot, 2);
				}
			}
			for (HorseArmor armor : HorseArmor.values()) {
				ingot = ItemHelper.getOre(armor.ingot);
				addRecycleRecipe(energy, armor.armor, ingot, 2, false);
			}
		}

		/* GENERAL SCAN */
		{
			String[] oreNames = OreDictionary.getOreNames();
			String oreType;

			for (String oreName : oreNames) {
				if (oreName.startsWith("ore")) {
					if (isStandardOre(oreName)) {
						oreType = oreName.substring(3);
						addDefaultRecipes(oreType, "");
					}
				} else if (oreName.startsWith("dust")) {
					if (isStandardOre(oreName)) {
						oreType = oreName.substring(4);
						addDefaultRecipes(oreType, "");
					}
				}
			}
		}
	}

	public static void refresh() {

		Map<List<ComparableItemStackValidated>, SmelterRecipe> tempMap = new Object2ObjectOpenHashMap<>(recipeMap.size());
		Set<ComparableItemStackValidated> tempSet = new ObjectOpenHashSet<>();
		SmelterRecipe tempRecipe;

		for (Entry<List<ComparableItemStackValidated>, SmelterRecipe> entry : recipeMap.entrySet()) {
			tempRecipe = entry.getValue();
			ComparableItemStackValidated primary = convertInput(tempRecipe.primaryInput);
			ComparableItemStackValidated secondary = convertInput(tempRecipe.secondaryInput);

			tempMap.put(asList(primary, secondary), tempRecipe);
			tempSet.add(primary);
			tempSet.add(secondary);
		}
		recipeMap.clear();
		recipeMap = tempMap;

		validationSet.clear();
		validationSet = tempSet;

		Set<ComparableItemStackValidated> tempSet2 = new ObjectOpenHashSet<>();
		for (ComparableItemStackValidated entry : lockSet) {
			ComparableItemStackValidated lock = convertInput(new ItemStack(entry.item, entry.stackSize, entry.metadata));
			tempSet2.add(lock);
		}
		lockSet.clear();
		lockSet = tempSet2;
	}

	/* ADD RECIPES */
	public static SmelterRecipe addRecipe(int energy, ItemStack primaryInput, ItemStack secondaryInput, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance) {

		if (primaryInput.isEmpty() || secondaryInput.isEmpty() || primaryOutput.isEmpty() || energy <= 0 || recipeExists(primaryInput, secondaryInput)) {
			return null;
		}
		SmelterRecipe recipe = new SmelterRecipe(primaryInput, secondaryInput, primaryOutput, secondaryOutput, secondaryOutput.isEmpty() ? 0 : secondaryChance, energy);
		recipeMap.put(asList(convertInput(primaryInput), convertInput(secondaryInput)), recipe);
		validationSet.add(convertInput(primaryInput));
		validationSet.add(convertInput(secondaryInput));
		return recipe;
	}

	public static SmelterRecipe addRecipe(int energy, ItemStack primaryInput, ItemStack secondaryInput, ItemStack primaryOutput, ItemStack secondaryOutput) {

		return addRecipe(energy, primaryInput, secondaryInput, primaryOutput, secondaryOutput, 100);
	}

	public static SmelterRecipe addRecipe(int energy, ItemStack primaryInput, ItemStack secondaryInput, ItemStack primaryOutput) {

		return addRecipe(energy, primaryInput, secondaryInput, primaryOutput, ItemStack.EMPTY, 0);
	}

	/* REMOVE RECIPES */
	public static SmelterRecipe removeRecipe(ItemStack primaryInput, ItemStack secondaryInput) {

		SmelterRecipe recipe = recipeMap.remove(asList(convertInput(primaryInput), convertInput(secondaryInput)));
		return recipe != null ? recipe : recipeMap.remove(asList(convertInput(secondaryInput), convertInput(primaryInput)));
	}

	/* HELPERS */
	public static ComparableItemStackValidated convertInput(ItemStack stack) {

		return new ComparableItemStackValidated(stack, oreValidator);
	}

	private static void addFlux(ItemStack flux) {

		lockSet.add(convertInput(flux));
	}

	private static void addDefaultRecipes(ItemStack ore, ItemStack dust, ItemStack plate, ItemStack gear, ItemStack ingot, ItemStack related, int richSlagChance) {

		if (ingot.isEmpty()) {
			return;
		}
		addOreRecipe(DEFAULT_ENERGY, ore, ingot, related, richSlagChance, 75);
		addBasicRecipe(DEFAULT_ENERGY / 4, dust, ingot, 25);
		addBasicRecipe(DEFAULT_ENERGY / 2, plate, ingot, 10);
		addBasicRecipe(DEFAULT_ENERGY * 3 / 4, gear, ItemHelper.cloneStack(ingot, 4), 20);
	}

	private static void addDefaultRecipes(String oreType, String relatedOre) {

		if (oreType == null || oreType.isEmpty()) {
			return;
		}
		String oreName = "ore" + StringHelper.titleCase(oreType);
		String dustName = "dust" + StringHelper.titleCase(oreType);
		String plateName = "plate" + StringHelper.titleCase(oreType);
		String gearName = "gear" + StringHelper.titleCase(oreType);
		String ingotName = "ingot" + StringHelper.titleCase(oreType);

		ItemStack ore = ItemHelper.getOre(oreName);
		ItemStack dust = ItemHelper.getOre(dustName);
		ItemStack plate = ItemHelper.getOre(plateName);
		ItemStack gear = ItemHelper.getOre(gearName);
		ItemStack ingot = ItemHelper.getOre(ingotName);
		ItemStack related = relatedOre.isEmpty() ? ItemStack.EMPTY : ItemHelper.getOre(relatedOre);

		addDefaultRecipes(ore, dust, plate, gear, ingot, related, 5);
	}

	private static void addDefaultRecipes(String oreType, ItemStack primary, ItemStack secondary, int chance) {

		String oreName = "ore" + StringHelper.titleCase(oreType);
		String dustName = "dust" + StringHelper.titleCase(oreType);
		String plateName = "plate" + StringHelper.titleCase(oreType);
		String gearName = "gear" + StringHelper.titleCase(oreType);

		ItemStack ore = ItemHelper.getOre(oreName);
		ItemStack dust = ItemHelper.getOre(dustName);
		ItemStack plate = ItemHelper.getOre(plateName);
		ItemStack gear = ItemHelper.getOre(gearName);

		addDefaultRecipes(ore, dust, plate, gear, primary, secondary, chance);
	}

	private static void addDefaultRecipes(String oreType, ItemStack primary, ItemStack secondary) {

		addDefaultRecipes(oreType, primary, secondary, 5);
	}

	private static void addDefaultRecipes(String oreType, ItemStack primary) {

		addDefaultRecipes(oreType, primary, ItemStack.EMPTY, 0);
	}

	private static void addOreRecipe(int energy, ItemStack input, ItemStack output, ItemStack secondary, int richSlagChance, int slagChance) {

		addOreRecipe(energy, input, output, secondary, richSlagChance, slagChance, ORE_MULTIPLIER, ORE_MULTIPLIER_SPECIAL);
	}

	private static void addOreRecipe(int energy, ItemStack input, ItemStack output, ItemStack secondary, int richSlagChance, int slagChance, int oreMultiplier, int oreMultiplierSpecial) {

		ItemStack ingot2 = ItemHelper.cloneStack(output, oreMultiplier);
		ItemStack ingot3 = ItemHelper.cloneStack(output, oreMultiplierSpecial);

		addRecipe(energy, input, BLOCK_SAND, ingot2, ItemMaterial.crystalSlagRich, richSlagChance);
		addRecipe(energy, input, ItemMaterial.crystalSlagRich, ingot3, ItemMaterial.crystalSlag, slagChance);

		if (!secondary.isEmpty()) {
			addRecipe(energy, input, ItemMaterial.crystalCinnabar, ingot3, secondary, 100);
		} else {
			addRecipe(energy, input, ItemMaterial.crystalCinnabar, ingot3, ItemMaterial.crystalSlagRich, 75);
		}
	}

	private static void addBasicRecipe(int energy, ItemStack input, ItemStack output, int slagChance) {

		addRecipe(energy, input, BLOCK_SAND, output, ItemMaterial.crystalSlag, slagChance);
	}

	public static void addRecycleRecipe(int energy, ItemStack input, ItemStack output, int outputSize) {

		addRecycleRecipe(energy, input, output, outputSize, true);
	}

	public static void addRecycleRecipe(int energy, ItemStack input, ItemStack output, int outputSize, boolean wildcard) {

		ItemStack recycleInput = wildcard ? new ItemStack(input.getItem(), 1, OreDictionary.WILDCARD_VALUE) : input.copy();
		addBasicRecipe(energy, recycleInput, ItemHelper.cloneStack(output, outputSize), Math.min(50, outputSize * 5 + 5));
	}

	private static boolean isStandardOre(String oreName) {

		return ItemHelper.oreNameExists(oreName) && FurnaceManager.recipeExists(OreDictionary.getOres(oreName, false).get(0), false);
	}

	public static boolean isOre(ItemStack stack) {

		return ItemHelper.isOre(stack);
	}

	/* RECIPE CLASS */
	public static class SmelterRecipe {

		final ItemStack primaryInput;
		final ItemStack secondaryInput;
		final ItemStack primaryOutput;
		final ItemStack secondaryOutput;
		final int secondaryChance;
		final int energy;
		final boolean hasFlux;

		SmelterRecipe(ItemStack secondaryInput, ItemStack primaryInput, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance, int energy) {

			if (isItemFlux(primaryInput) && !isItemFlux(secondaryInput)) {
				this.primaryInput = secondaryInput;
				this.secondaryInput = primaryInput;
			} else {
				this.primaryInput = primaryInput;
				this.secondaryInput = secondaryInput;
			}
			this.primaryOutput = primaryOutput;
			this.secondaryOutput = secondaryOutput;
			this.secondaryChance = secondaryChance;
			this.energy = energy;
			this.hasFlux = isItemFlux(secondaryInput) || isItemFlux(primaryInput);
		}

		public ItemStack getPrimaryInput() {

			return primaryInput;
		}

		public ItemStack getSecondaryInput() {

			return secondaryInput;
		}

		public ItemStack getPrimaryOutput() {

			return primaryOutput;
		}

		public ItemStack getSecondaryOutput() {

			return secondaryOutput;
		}

		public int getSecondaryOutputChance() {

			return secondaryChance;
		}

		public int getEnergy() {

			return energy;
		}

		public boolean hasFlux() {

			return hasFlux;
		}
	}

}
