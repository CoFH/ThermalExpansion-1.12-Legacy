package cofh.thermalexpansion.util.managers.machine;

import cofh.core.inventory.ComparableItemStack;
import cofh.core.inventory.ComparableItemStackValidated;
import cofh.core.inventory.OreValidator;
import cofh.core.util.helpers.ItemHelper;
import cofh.core.util.helpers.StringHelper;
import cofh.thermalfoundation.init.TFEquipment;
import gnu.trove.map.hash.THashMap;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class PulverizerManager {

	private static Map<ComparableItemStackValidated, PulverizerRecipe> recipeMap = new THashMap<>();
	private static OreValidator oreValidator = new OreValidator();

	static {
		oreValidator.addPrefix(ComparableItemStack.ORE);
		oreValidator.addPrefix(ComparableItemStack.INGOT);
		oreValidator.addPrefix(ComparableItemStack.NUGGET);
		oreValidator.addPrefix("log");
		oreValidator.addPrefix("plank");
		oreValidator.addExact("sand");
	}

	static final int ORE_MULTIPLIER = 2;
	public static final int DEFAULT_ENERGY = 4000;

	public static PulverizerRecipe getRecipe(ItemStack input) {

		if (input.isEmpty()) {
			return null;
		}
		ComparableItemStackValidated query = convertInput(input);

		PulverizerRecipe recipe = recipeMap.get(query);

		if (recipe == null) {
			query.metadata = OreDictionary.WILDCARD_VALUE;
			recipe = recipeMap.get(query);
		}
		return recipe;
	}

	public static boolean recipeExists(ItemStack input) {

		return getRecipe(input) != null;
	}

	public static PulverizerRecipe[] getRecipeList() {

		return recipeMap.values().toArray(new PulverizerRecipe[recipeMap.size()]);
	}

	public static void initialize() {

		/* RECYCLING */
		{
			int energy = DEFAULT_ENERGY * 3 / 2;
			// Output is 1/2, round down, minimum of 1.

			/* DIAMOND TOOLS / ARMOR */
			ItemStack diamond = new ItemStack(Items.DIAMOND);

			addRecycleRecipe(energy, new ItemStack(Items.DIAMOND_SWORD), diamond, 1);
			addRecycleRecipe(energy, new ItemStack(Items.DIAMOND_PICKAXE), diamond, 1);
			addRecycleRecipe(energy, new ItemStack(Items.DIAMOND_AXE), diamond, 1);
			addRecycleRecipe(energy, new ItemStack(Items.DIAMOND_SHOVEL), diamond, 1);
			addRecycleRecipe(energy, new ItemStack(Items.DIAMOND_HOE), diamond, 1);

			addRecycleRecipe(energy, new ItemStack(Items.DIAMOND_HELMET), diamond, 2);
			addRecycleRecipe(energy, new ItemStack(Items.DIAMOND_CHESTPLATE), diamond, 4);
			addRecycleRecipe(energy, new ItemStack(Items.DIAMOND_LEGGINGS), diamond, 3);
			addRecycleRecipe(energy, new ItemStack(Items.DIAMOND_BOOTS), diamond, 2);

			addRecycleRecipe(energy, new ItemStack(Items.DIAMOND_HORSE_ARMOR), diamond, 2);

			addRecycleRecipe(energy, TFEquipment.ToolSetVanilla.DIAMOND.toolBow, diamond, 1);
			addRecycleRecipe(energy, TFEquipment.ToolSetVanilla.DIAMOND.toolFishingRod, diamond, 1);
			addRecycleRecipe(energy, TFEquipment.ToolSetVanilla.DIAMOND.toolShears, diamond, 1);
			addRecycleRecipe(energy, TFEquipment.ToolSetVanilla.DIAMOND.toolSickle, diamond, 1);
			addRecycleRecipe(energy, TFEquipment.ToolSetVanilla.DIAMOND.toolHammer, diamond, 2);
			addRecycleRecipe(energy, TFEquipment.ToolSetVanilla.DIAMOND.toolShield, diamond, 3);
		}

		/* GENERAL SCAN */
		{
			String[] oreNames = OreDictionary.getOreNames();
			String oreType;

			for (String oreName : oreNames) {
				if (oreName.startsWith("ore")) {
					oreType = oreName.substring(3, oreName.length());
					addDefaultOreDictionaryRecipe(oreType);
				} else if (oreName.startsWith("dust")) {
					oreType = oreName.substring(4, oreName.length());
					addDefaultOreDictionaryRecipe(oreType);
				} else if (oreName.startsWith("gem")) {
					oreType = oreName.substring(3, oreName.length());
					addDefaultOreDictionaryRecipeGem(oreType);
				}
			}
		}
	}

	public static void refresh() {

		Map<ComparableItemStackValidated, PulverizerRecipe> tempMap = new THashMap<>(recipeMap.size());
		PulverizerRecipe tempRecipe;

		for (Entry<ComparableItemStackValidated, PulverizerRecipe> entry : recipeMap.entrySet()) {
			tempRecipe = entry.getValue();
			tempMap.put(convertInput(tempRecipe.input), tempRecipe);
		}
		recipeMap.clear();
		recipeMap = tempMap;
	}

	/* ADD RECIPES */
	public static PulverizerRecipe addRecipe(int energy, ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance) {

		if (input.isEmpty() || primaryOutput.isEmpty() || energy <= 0 || recipeExists(input)) {
			return null;
		}
		PulverizerRecipe recipe = new PulverizerRecipe(input, primaryOutput, secondaryOutput, secondaryOutput.isEmpty() ? 0 : secondaryChance, energy);
		recipeMap.put(convertInput(input), recipe);
		return recipe;
	}

	public static PulverizerRecipe addRecipe(int energy, ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput) {

		return addRecipe(energy, input, primaryOutput, secondaryOutput, 100);
	}

	public static PulverizerRecipe addRecipe(int energy, ItemStack input, ItemStack primaryOutput) {

		return addRecipe(energy, input, primaryOutput, ItemStack.EMPTY, 0);
	}

	/* REMOVE RECIPES */
	public static PulverizerRecipe removeRecipe(ItemStack input) {

		return recipeMap.remove(convertInput(input));
	}

	/* HELPERS */
	public static ComparableItemStackValidated convertInput(ItemStack stack) {

		return new ComparableItemStackValidated(stack, oreValidator);
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
		String clusterName = "cluster" + StringHelper.titleCase(oreType);
		String relatedName;

		List<ItemStack> registeredOre = OreDictionary.getOres(oreName, false);
		List<ItemStack> registeredDust = OreDictionary.getOres(dustName, false);
		List<ItemStack> registeredIngot = OreDictionary.getOres(ingotName, false);
		List<ItemStack> registeredCluster = OreDictionary.getOres(clusterName, false);
		List<ItemStack> registeredRelated = new ArrayList<>();

		if (!relatedType.isEmpty()) {
			relatedName = "dust" + StringHelper.titleCase(relatedType);
			registeredRelated = OreDictionary.getOres(relatedName, false);
		}
		if (registeredDust.isEmpty()) {
			return;
		}
		ItemStack dust = registeredDust.get(0);

		if (registeredIngot.isEmpty()) {
			ingotName = null;
		}
		if (registeredOre.isEmpty()) {
			oreName = null;
		}
		if (registeredCluster.isEmpty()) {
			clusterName = null;
		}
		ItemStack related = ItemStack.EMPTY;
		if (related.isEmpty() && !registeredRelated.isEmpty()) {
			related = registeredRelated.get(0);
		}
		int energy = DEFAULT_ENERGY;

		addOreToDustRecipe(energy, oreName, ItemHelper.cloneStack(dust, ORE_MULTIPLIER), related, related.isEmpty() ? 0 : 5);
		addOreToDustRecipe(energy * 3 / 4, clusterName, ItemHelper.cloneStack(dust, ORE_MULTIPLIER), related, related.isEmpty() ? 0 : 5);
		addIngotToDustRecipe(energy / 2, ingotName, ItemHelper.cloneStack(dust, 1));
	}

	private static void addDefaultOreDictionaryRecipeGem(String oreType) {

		addDefaultOreDictionaryRecipeGem(oreType, "");
	}

	private static void addDefaultOreDictionaryRecipeGem(String oreType, String relatedType) {

		if (oreType == null || oreType.isEmpty()) {
			return;
		}
		String oreName = "ore" + StringHelper.titleCase(oreType);
		String dustName = "dust" + StringHelper.titleCase(oreType);
		String gemName = "gem" + StringHelper.titleCase(oreType);
		String relatedName;

		List<ItemStack> registeredOre = OreDictionary.getOres(oreName, false);
		List<ItemStack> registeredDust = OreDictionary.getOres(dustName, false);
		List<ItemStack> registeredGem = OreDictionary.getOres(gemName, false);
		List<ItemStack> registeredRelated = new ArrayList<>();

		String clusterName = "cluster" + StringHelper.titleCase(oreType);
		List<ItemStack> registeredCluster = OreDictionary.getOres(clusterName, false);

		if (!relatedType.isEmpty()) {
			relatedName = "dust" + StringHelper.titleCase(relatedType);
			registeredRelated = OreDictionary.getOres(relatedName, false);
		}
		if (registeredGem.isEmpty()) {
			return;
		}
		ItemStack gem = registeredGem.get(0);
		ItemStack dust = ItemStack.EMPTY;

		if (!registeredDust.isEmpty()) {
			dust = registeredDust.get(0);
		}
		if (registeredOre.isEmpty()) {
			oreName = null;
		}
		if (registeredCluster.isEmpty()) {
			clusterName = null;
		}
		ItemStack related = ItemStack.EMPTY;
		if (related.isEmpty() && !registeredRelated.isEmpty()) {
			related = registeredRelated.get(0);
		}
		addOreToDustRecipe(DEFAULT_ENERGY, oreName, ItemHelper.cloneStack(gem, ORE_MULTIPLIER), related, related.isEmpty() ? 0 : 5);
		addOreToDustRecipe(DEFAULT_ENERGY * 5 / 4, clusterName, ItemHelper.cloneStack(gem, ORE_MULTIPLIER), related, related.isEmpty() ? 0 : 5);
		addIngotToDustRecipe(DEFAULT_ENERGY * 3 / 4, gemName, ItemHelper.cloneStack(dust, 1));
	}

	private static void addOreToDustRecipe(int energy, String oreName, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance) {

		if (primaryOutput.isEmpty() || oreName == null) {
			return;
		}
		List<ItemStack> registeredOres = OreDictionary.getOres(oreName, false);

		if (!registeredOres.isEmpty() && !recipeExists(OreDictionary.getOres(oreName, false).get(0))) {
			addRecipe(energy, ItemHelper.cloneStack(registeredOres.get(0), 1), ItemHelper.cloneStack(primaryOutput, ORE_MULTIPLIER), secondaryOutput, secondaryChance);
		}
	}

	private static void addIngotToDustRecipe(int energy, String ingotName, ItemStack dust) {

		if (dust.isEmpty() || ingotName == null) {
			return;
		}
		List<ItemStack> registeredOres = OreDictionary.getOres(ingotName, false);

		if (!registeredOres.isEmpty() && !recipeExists(OreDictionary.getOres(ingotName, false).get(0))) {
			addRecipe(energy, ItemHelper.cloneStack(registeredOres.get(0), 1), dust, ItemStack.EMPTY, 0);
		}
	}

	public static void addRecycleRecipe(int energy, ItemStack input, ItemStack output, int outputSize) {

		addRecycleRecipe(energy, input, output, outputSize, true);
	}

	public static void addRecycleRecipe(int energy, ItemStack input, ItemStack output, int outputSize, boolean wildcard) {

		ItemStack recycleInput = wildcard ? input.copy() : new ItemStack(input.getItem(), 1, OreDictionary.WILDCARD_VALUE);
		addRecipe(energy, recycleInput, ItemHelper.cloneStack(output, outputSize));
	}

	/* RECIPE CLASS */
	public static class PulverizerRecipe {

		final ItemStack input;
		final ItemStack primaryOutput;
		final ItemStack secondaryOutput;
		final int secondaryChance;
		final int energy;

		PulverizerRecipe(ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance, int energy) {

			this.input = input;
			this.primaryOutput = primaryOutput;
			this.secondaryOutput = secondaryOutput;
			this.secondaryChance = secondaryChance;
			this.energy = energy;
		}

		public ItemStack getInput() {

			return input;
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

	}

}
