package cofh.thermalexpansion.util.managers.machine;

import cofh.core.inventory.ComparableItemStack;
import cofh.core.inventory.ComparableItemStackValidated;
import cofh.core.inventory.OreValidator;
import cofh.core.util.helpers.ItemHelper;
import cofh.core.util.helpers.StringHelper;
import cofh.core.util.oredict.OreDictionaryArbiter;
import cofh.thermalfoundation.init.TFEquipment.ArmorSet;
import cofh.thermalfoundation.init.TFEquipment.ToolSet;
import cofh.thermalfoundation.init.TFEquipment.ToolSetVanilla;
import cofh.thermalfoundation.item.ItemMaterial;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static java.util.Arrays.asList;

public class SmelterManager {

	private static Map<List<ComparableItemStackValidated>, SmelterRecipe> recipeMap = new THashMap<>();
	private static Set<ComparableItemStackValidated> validationSet = new THashSet<>();
	private static Set<ComparableItemStackValidated> lockSet = new THashSet<>();
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

	public static boolean isRecipeReversed(ItemStack primaryInput, ItemStack secondaryInput) {

		if (primaryInput.isEmpty() || secondaryInput.isEmpty()) {
			return false;
		}
		ComparableItemStackValidated query = convertInput(primaryInput);
		ComparableItemStackValidated querySecondary = convertInput(secondaryInput);

		SmelterRecipe recipe = recipeMap.get(asList(query, querySecondary));
		return recipe == null && recipeMap.get(asList(querySecondary, query)) != null;
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
			return null;
		}
		return recipe;
	}

	public static boolean recipeExists(ItemStack primaryInput, ItemStack secondaryInput) {

		return getRecipe(primaryInput, secondaryInput) != null;
	}

	public static SmelterRecipe[] getRecipeList() {

		return recipeMap.values().toArray(new SmelterRecipe[recipeMap.size()]);
	}

	public static boolean isItemValid(ItemStack input) {

		return !input.isEmpty() && validationSet.contains(convertInput(input));
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
			addDefaultOreDictionaryRecipe("oreIron", "dustIron", ItemMaterial.ingotIron, ItemMaterial.ingotNickel);
			addDefaultOreDictionaryRecipe("oreGold", "dustGold", ItemMaterial.ingotGold, ItemStack.EMPTY, 20, 75, 25);

			addDefaultOreDictionaryRecipe("oreCopper", "dustCopper", ItemMaterial.ingotCopper, ItemMaterial.ingotGold);
			addDefaultOreDictionaryRecipe("oreTin", "dustTin", ItemMaterial.ingotTin, ItemMaterial.ingotIron);
			addDefaultOreDictionaryRecipe("oreSilver", "dustSilver", ItemMaterial.ingotSilver, ItemMaterial.ingotLead);
			addDefaultOreDictionaryRecipe("oreAluminum", "dustAluminum", ItemMaterial.ingotAluminum, ItemMaterial.ingotIron);
			addDefaultOreDictionaryRecipe("oreLead", "dustLead", ItemMaterial.ingotLead, ItemMaterial.ingotSilver);
			addDefaultOreDictionaryRecipe("oreNickel", "dustNickel", ItemMaterial.ingotNickel, ItemMaterial.ingotPlatinum, 15, 75, 25);
			addDefaultOreDictionaryRecipe("orePlatinum", "dustPlatinum", ItemMaterial.ingotPlatinum, ItemMaterial.ingotIridium);
			addDefaultOreDictionaryRecipe("oreIridium", "dustIridium", ItemMaterial.ingotIridium, ItemMaterial.ingotPlatinum);
			addDefaultOreDictionaryRecipe("oreMithril", "dustMithril", ItemMaterial.ingotMithril, ItemMaterial.ingotGold);
		}

		/* DUSTS */
		{
			addDefaultOreDictionaryRecipe(null, "dustSteel", ItemMaterial.ingotSteel);
			addDefaultOreDictionaryRecipe(null, "dustElectrum", ItemMaterial.ingotElectrum);
			addDefaultOreDictionaryRecipe(null, "dustInvar", ItemMaterial.ingotInvar);
			addDefaultOreDictionaryRecipe(null, "dustBronze", ItemMaterial.ingotBronze);
			addDefaultOreDictionaryRecipe(null, "dustConstantan", ItemMaterial.ingotConstantan);
			addDefaultOreDictionaryRecipe(null, "dustSignalum", ItemMaterial.ingotSignalum);
			addDefaultOreDictionaryRecipe(null, "dustLumium", ItemMaterial.ingotLumium);
			addDefaultOreDictionaryRecipe(null, "dustEnderium", ItemMaterial.ingotEnderium);
		}

		/* RECYCLING */
		{
			int energy = DEFAULT_ENERGY * 3 / 2;
			// Output is 1/2, round down, minimum of 1.

			/* IRON TOOLS / ARMOR */
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

			addRecycleRecipe(energy, new ItemStack(Items.IRON_HORSE_ARMOR), ingot, 2);

			/* GOLD TOOLS / ARMOR */
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

			addRecycleRecipe(energy, new ItemStack(Items.GOLDEN_HORSE_ARMOR), ingot, 2);

			/* TF TOOLS / ARMOR */
			for (ToolSetVanilla tool : new ToolSetVanilla[] { ToolSetVanilla.IRON, ToolSetVanilla.GOLD }) {
				ingot = ItemHelper.getOre(tool.ingot);

				addRecycleRecipe(energy, tool.toolBow, ingot, 1);
				addRecycleRecipe(energy, tool.toolFishingRod, ingot, 1);
				addRecycleRecipe(energy, tool.toolShears, ingot, 1);
				addRecycleRecipe(energy, tool.toolSickle, ingot, 1);
				addRecycleRecipe(energy, tool.toolHammer, ingot, 2);
				addRecycleRecipe(energy, tool.toolShield, ingot, 3);
			}
			for (ToolSet tool : ToolSet.values()) {
				ingot = ItemHelper.getOre(tool.ingot);

				addRecycleRecipe(energy, tool.toolSword, ingot, 1);
				addRecycleRecipe(energy, tool.toolPickaxe, ingot, 1);
				addRecycleRecipe(energy, tool.toolAxe, ingot, 1);
				addRecycleRecipe(energy, tool.toolShovel, ingot, 1);
				addRecycleRecipe(energy, tool.toolHoe, ingot, 1);
				addRecycleRecipe(energy, tool.toolBow, ingot, 1);
				addRecycleRecipe(energy, tool.toolFishingRod, ingot, 1);
				addRecycleRecipe(energy, tool.toolShears, ingot, 1);
				addRecycleRecipe(energy, tool.toolSickle, ingot, 1);
				addRecycleRecipe(energy, tool.toolHammer, ingot, 2);
				addRecycleRecipe(energy, tool.toolShield, ingot, 3);
			}
			for (ArmorSet armor : ArmorSet.values()) {
				ingot = ItemHelper.getOre(armor.ingot);

				addRecycleRecipe(energy, armor.armorHelmet, ingot, 2);
				addRecycleRecipe(energy, armor.armorChestplate, ingot, 4);
				addRecycleRecipe(energy, armor.armorLegs, ingot, 3);
				addRecycleRecipe(energy, armor.armorBoots, ingot, 2);
			}
		}

		/* GENERAL SCAN */
		{
			String[] oreNames = OreDictionary.getOreNames();
			String oreType;

			for (String oreName : oreNames) {
				if (oreName.startsWith("ore")) {
					oreType = oreName.substring(3, oreName.length());
					if (isStandardOre(oreName)) {
						addDefaultOreDictionaryRecipe(oreType);
					}
				} else if (oreName.startsWith("dust")) {
					oreType = oreName.substring(4, oreName.length());
					if (isStandardOre(oreName)) {
						addDefaultOreDictionaryRecipe(oreType);
					}
				}
			}
		}
	}

	public static void refresh() {

		Map<List<ComparableItemStackValidated>, SmelterRecipe> tempMap = new THashMap<>(recipeMap.size());
		Set<ComparableItemStackValidated> tempSet = new THashSet<>();
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

		Set<ComparableItemStackValidated> tempSet2 = new THashSet<>();
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

		return recipeMap.remove(asList(convertInput(primaryInput), convertInput(secondaryInput)));
	}

	/* HELPERS */
	public static ComparableItemStackValidated convertInput(ItemStack stack) {

		return new ComparableItemStackValidated(stack, oreValidator);
	}

	private static void addFlux(ItemStack flux) {

		lockSet.add(convertInput(flux));
	}

	private static void addDefaultOreDictionaryRecipe(String oreName, String dustName, ItemStack ingot, ItemStack ingotRelated, int richSlagChance, int slagOreChance, int slagDustChance) {

		if (ingot.isEmpty()) {
			return;
		}
		if (oreName != null) {
			addOreToIngotRecipe(DEFAULT_ENERGY, oreName, ItemHelper.cloneStack(ingot, ORE_MULTIPLIER), ItemHelper.cloneStack(ingot, ORE_MULTIPLIER_SPECIAL), ItemHelper.cloneStack(ingotRelated, 1), richSlagChance, slagOreChance);
		}
		if (dustName != null) {
			addDustToIngotRecipe(DEFAULT_ENERGY / 8, dustName, ItemHelper.cloneStack(ingot, 1), slagDustChance);
		}
	}

	private static void addDefaultOreDictionaryRecipe(String oreType) {

		addDefaultOreDictionaryRecipe(oreType, "");
	}

	private static void addDefaultOreDictionaryRecipe(String oreType, String relatedType) {

		if (oreType == null || oreType.isEmpty()) {
			return;
		}
		String oreName = "ore" + StringHelper.titleCase(oreType);
		String dustName = "dust" + StringHelper.titleCase(oreType);
		String ingotName = "ingot" + StringHelper.titleCase(oreType);
		String relatedName;

		List<ItemStack> registeredOre = OreDictionary.getOres(oreName, false);
		List<ItemStack> registeredDust = OreDictionary.getOres(dustName, false);
		List<ItemStack> registeredIngot = OreDictionary.getOres(ingotName, false);
		List<ItemStack> registeredRelated = new ArrayList<>();

		if (!relatedType.isEmpty()) {
			relatedName = "ingot" + StringHelper.titleCase(relatedType);
			registeredRelated = OreDictionary.getOres(relatedName, false);
		}
		if (registeredIngot.isEmpty()) {
			return;
		}
		ItemStack ingot = ItemStack.EMPTY;
		if (!ingot.isEmpty() && !OreDictionaryArbiter.getAllOreNames(ingot).contains(ingotName)) {
			ingot = ItemStack.EMPTY;
		}
		if (ingot.isEmpty()) {
			ingot = registeredIngot.get(0);
		}
		if (registeredOre.isEmpty()) {
			oreName = null;
		}
		if (registeredDust.isEmpty()) {
			dustName = null;
		}
		ItemStack related = ItemStack.EMPTY;
		if (related.isEmpty() && !registeredRelated.isEmpty()) {
			related = registeredRelated.get(0);
		}
		addDefaultOreDictionaryRecipe(oreName, dustName, ingot, related, 5, 75, 25);
	}

	private static void addDefaultOreDictionaryRecipe(String oreName, String dustName, ItemStack ingot) {

		addDefaultOreDictionaryRecipe(oreName, dustName, ingot, ItemStack.EMPTY, 5, 75, 25);
	}

	private static void addDefaultOreDictionaryRecipe(String oreName, String dustName, ItemStack ingot, ItemStack ingotRelated) {

		addDefaultOreDictionaryRecipe(oreName, dustName, ingot, ingotRelated, 5, 75, 25);
	}

	private static void addOreToIngotRecipe(int energy, String oreName, ItemStack ingot2, ItemStack ingot3, ItemStack ingotSecondary, int richSlagChance, int slagOreChance) {

		List<ItemStack> registeredOres = OreDictionary.getOres(oreName, false);

		if (registeredOres.size() > 0) {
			ItemStack ore = registeredOres.get(0);
			addRecipe(energy, ore, BLOCK_SAND, ingot2, ItemMaterial.crystalSlagRich, richSlagChance);
			addRecipe(energy, ore, ItemMaterial.crystalSlagRich, ingot3, ItemMaterial.crystalSlag, slagOreChance);

			if (!ingotSecondary.isEmpty()) {
				addRecipe(energy, ore, ItemMaterial.crystalCinnabar, ingot3, ingotSecondary, 100);
			} else {
				addRecipe(energy, ore, ItemMaterial.crystalCinnabar, ingot3, ItemMaterial.crystalSlagRich, 75);
			}
		}
	}

	private static void addDustToIngotRecipe(int energy, String dustName, ItemStack ingot, int slagDustChance) {

		List<ItemStack> registeredOres = OreDictionary.getOres(dustName, false);

		if (registeredOres.size() > 0) {
			addRecipe(energy, ItemHelper.cloneStack(registeredOres.get(0), 1), BLOCK_SAND, ingot, ItemMaterial.crystalSlag, slagDustChance);
		}
	}

	public static void addAlloyRecipe(int energy, String primaryOreName, int primaryAmount, String secondaryOreName, int secondaryAmount, ItemStack primaryOutput) {

		List<ItemStack> primaryOreList = OreDictionary.getOres(primaryOreName, false);
		List<ItemStack> secondaryOreList = OreDictionary.getOres(secondaryOreName, false);

		if (primaryOreList.size() > 0 && secondaryOreList.size() > 0) {
			addAlloyRecipe(energy, ItemHelper.cloneStack(primaryOreList.get(0), primaryAmount), ItemHelper.cloneStack(secondaryOreList.get(0), secondaryAmount), primaryOutput);
		}
	}

	public static void addAlloyRecipe(int energy, ItemStack primaryInput, ItemStack secondaryInput, ItemStack primaryOutput) {

		addRecipe(energy, primaryInput, secondaryInput, primaryOutput, ItemStack.EMPTY, 0);
	}

	public static void addRecycleRecipe(int energy, ItemStack input, ItemStack output, int outputSize) {

		addRecycleRecipe(energy, input, output, outputSize, true);
	}

	public static void addRecycleRecipe(int energy, ItemStack input, ItemStack output, int outputSize, boolean wildcard) {

		ItemStack recycleInput = wildcard ? input.copy() : new ItemStack(input.getItem(), 1, OreDictionary.WILDCARD_VALUE);
		addRecipe(energy, recycleInput, BLOCK_SAND, ItemHelper.cloneStack(output, outputSize), ItemMaterial.crystalSlag, Math.min(50, outputSize * 5 + 5));
	}

	private static boolean isStandardOre(String oreName) {

		return ItemHelper.oreNameExists(oreName) && FurnaceManager.recipeExists(OreDictionary.getOres(oreName, false).get(0), false);
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
