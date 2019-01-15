package cofh.thermalexpansion.util.managers.machine;

import cofh.core.inventory.ComparableItemStack;
import cofh.core.inventory.ComparableItemStackValidated;
import cofh.core.inventory.OreValidator;
import cofh.core.util.helpers.ItemHelper;
import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalfoundation.init.TFEquipment.ToolSetVanilla;
import cofh.thermalfoundation.item.ItemMaterial;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Map;
import java.util.Map.Entry;

public class PulverizerManager {

	private static Map<ComparableItemStackValidated, PulverizerRecipe> recipeMap = new Object2ObjectOpenHashMap<>();
	private static OreValidator oreValidator = new OreValidator();

	static {
		oreValidator.addPrefix(ComparableItemStack.ORE);
		oreValidator.addPrefix(ComparableItemStack.INGOT);
		oreValidator.addPrefix(ComparableItemStack.NUGGET);
		oreValidator.addPrefix("log");
		oreValidator.addPrefix("plank");
		oreValidator.addExact("sand");
		// oreValidator.addBlacklist("oreCertusQuartz");

		oreValidator.addExact("treeSapling");
		oreValidator.addExact("treeLeaves");
	}

	static int oreMultiplier = 2;
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

		return recipeMap.values().toArray(new PulverizerRecipe[0]);
	}

	public static void preInit() {

		String category = "Machine.Pulverizer";
		String comment = "Adjust this value to change the default Ore -> Dust Multiplier for this machine.";
		oreMultiplier = ThermalExpansion.CONFIG.getConfiguration().getInt("Ore -> Dust Multiplier", category, oreMultiplier, 1, 8, comment);
	}

	public static void initialize() {

		/* RECYCLING */
		{
			// Output is 1/2, round down, minimum of 1.

			/* WOODEN TOOLS / ARMOR */
			int energy = DEFAULT_ENERGY * 3 / 4;
			ItemStack output = ItemMaterial.dustWood;

			addRecycleRecipe(energy, new ItemStack(Items.WOODEN_SWORD), output, 2);
			addRecycleRecipe(energy, new ItemStack(Items.WOODEN_PICKAXE), output, 2);
			addRecycleRecipe(energy, new ItemStack(Items.WOODEN_AXE), output, 2);
			addRecycleRecipe(energy, new ItemStack(Items.WOODEN_SHOVEL), output, 2);
			addRecycleRecipe(energy, new ItemStack(Items.WOODEN_HOE), output, 2);

			addRecycleRecipe(energy, ToolSetVanilla.WOOD.toolBow, output, 2);
			addRecycleRecipe(energy, ToolSetVanilla.WOOD.toolFishingRod, output, 2);
			addRecycleRecipe(energy, ToolSetVanilla.WOOD.toolShears, output, 2);
			addRecycleRecipe(energy, ToolSetVanilla.WOOD.toolSickle, output, 2);
			addRecycleRecipe(energy, ToolSetVanilla.WOOD.toolHammer, output, 4);
			addRecycleRecipe(energy, ToolSetVanilla.WOOD.toolExcavator, output, 2);
			addRecycleRecipe(energy, ToolSetVanilla.WOOD.toolShield, output, 6);

			/* DIAMOND TOOLS / ARMOR */
			energy = DEFAULT_ENERGY * 3 / 2;
			output = new ItemStack(Items.DIAMOND);

			addRecycleRecipe(energy, new ItemStack(Items.DIAMOND_SWORD), output, 1);
			addRecycleRecipe(energy, new ItemStack(Items.DIAMOND_PICKAXE), output, 1);
			addRecycleRecipe(energy, new ItemStack(Items.DIAMOND_AXE), output, 1);
			addRecycleRecipe(energy, new ItemStack(Items.DIAMOND_SHOVEL), output, 1);
			addRecycleRecipe(energy, new ItemStack(Items.DIAMOND_HOE), output, 1);

			addRecycleRecipe(energy, new ItemStack(Items.DIAMOND_HELMET), output, 2);
			addRecycleRecipe(energy, new ItemStack(Items.DIAMOND_CHESTPLATE), output, 4);
			addRecycleRecipe(energy, new ItemStack(Items.DIAMOND_LEGGINGS), output, 3);
			addRecycleRecipe(energy, new ItemStack(Items.DIAMOND_BOOTS), output, 2);

			addRecycleRecipe(energy, new ItemStack(Items.DIAMOND_HORSE_ARMOR), output, 2);

			addRecycleRecipe(energy, ToolSetVanilla.DIAMOND.toolBow, output, 1);
			addRecycleRecipe(energy, ToolSetVanilla.DIAMOND.toolFishingRod, output, 1);
			addRecycleRecipe(energy, ToolSetVanilla.DIAMOND.toolShears, output, 1);
			addRecycleRecipe(energy, ToolSetVanilla.DIAMOND.toolSickle, output, 1);
			addRecycleRecipe(energy, ToolSetVanilla.DIAMOND.toolHammer, output, 2);
			addRecycleRecipe(energy, ToolSetVanilla.DIAMOND.toolExcavator, output, 1);
			addRecycleRecipe(energy, ToolSetVanilla.DIAMOND.toolShield, output, 3);
		}

		/* GENERAL SCAN */
		{
			String oreType;
			for (String oreName : OreDictionary.getOreNames()) {
				if (oreName.startsWith("ore") || oreName.startsWith("gem")) {
					oreType = oreName.substring(3, oreName.length());
					addDefaultRecipes(oreType, "");
				} else if (oreName.startsWith("dust")) {
					oreType = oreName.substring(4, oreName.length());
					addDefaultRecipes(oreType, "");
				}
			}
		}
	}

	public static void refresh() {

		Map<ComparableItemStackValidated, PulverizerRecipe> tempMap = new Object2ObjectOpenHashMap<>(recipeMap.size());
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

	private static void addDefaultRecipes(String oreType, String relatedOre) {

		if (oreType == null || oreType.isEmpty()) {
			return;
		}
		String suffix = StringHelper.titleCase(oreType);

		String oreName = "ore" + suffix;
		String gemName = "gem" + suffix;
		String dustName = "dust" + suffix;
		String ingotName = "ingot" + suffix;

		String oreNetherName = "oreNether" + suffix;
		String oreEndName = "oreEnd" + suffix;

		ItemStack ore = ItemHelper.getOre(oreName);
		ItemStack gem = ItemHelper.getOre(gemName);
		ItemStack dust = ItemHelper.getOre(dustName);
		ItemStack ingot = ItemHelper.getOre(ingotName);

		ItemStack oreNether = ItemHelper.getOre(oreNetherName);
		ItemStack oreEnd = ItemHelper.getOre(oreEndName);

		ItemStack related = relatedOre.isEmpty() ? ItemStack.EMPTY : ItemHelper.getOre(relatedOre);

		int energy = DEFAULT_ENERGY;

		if (!gem.isEmpty()) {
			addRecipe(energy, ore, ItemHelper.cloneStack(gem, oreMultiplier), related, related.isEmpty() ? 0 : 10);
			addRecipe(energy, oreNether, ItemHelper.cloneStack(gem, oreMultiplier * 2), related, related.isEmpty() ? 0 : 20);
			addRecipe(energy, oreEnd, ItemHelper.cloneStack(gem, oreMultiplier * 2), related, related.isEmpty() ? 0 : 20);
			addRecipe(energy / 2, gem, ItemHelper.cloneStack(dust, 1));
		} else {
			addRecipe(energy, ore, ItemHelper.cloneStack(dust, oreMultiplier), related, related.isEmpty() ? 0 : 10);
			addRecipe(energy, oreNether, ItemHelper.cloneStack(dust, oreMultiplier * 2), related, related.isEmpty() ? 0 : 20);
			addRecipe(energy, oreEnd, ItemHelper.cloneStack(dust, oreMultiplier * 2), related, related.isEmpty() ? 0 : 20);
			addRecipe(energy / 2, ingot, ItemHelper.cloneStack(dust, 1));
		}
	}

	public static void addRecycleRecipe(int energy, ItemStack input, ItemStack output, int outputSize) {

		addRecycleRecipe(energy, input, output, outputSize, true);
	}

	public static void addRecycleRecipe(int energy, ItemStack input, ItemStack output, int outputSize, boolean wildcard) {

		ItemStack recycleInput = wildcard ? new ItemStack(input.getItem(), 1, OreDictionary.WILDCARD_VALUE) : input.copy();
		addRecipe(energy, recycleInput, ItemHelper.cloneStack(output, outputSize));
	}

	public static boolean isOre(ItemStack stack) {

		return ItemHelper.isOre(stack) || ItemHelper.isCluster(stack);
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
