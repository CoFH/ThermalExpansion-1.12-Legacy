package cofh.thermalexpansion.util.managers.machine;

import cofh.core.inventory.ComparableItemStack;
import cofh.core.inventory.ComparableItemStackValidated;
import cofh.core.inventory.InventoryCraftingFalse;
import cofh.core.inventory.OreValidator;
import cofh.core.util.helpers.ItemHelper;
import cofh.thermalfoundation.item.ItemMaterial;
import gnu.trove.map.hash.THashMap;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Map;
import java.util.Map.Entry;

public class SawmillManager {

	private static Map<ComparableItemStackValidated, SawmillRecipe> recipeMap = new THashMap<>();
	private static OreValidator oreValidator = new OreValidator();

	static {
		oreValidator.addPrefix(ComparableItemStack.ORE);
		oreValidator.addPrefix(ComparableItemStack.INGOT);
		oreValidator.addPrefix(ComparableItemStack.NUGGET);
	}

	static final float LOG_MULTIPLIER = 1.5F;
	public static final int DEFAULT_ENERGY = 2000;

	public static SawmillRecipe getRecipe(ItemStack input) {

		if (input.isEmpty()) {
			return null;
		}
		ComparableItemStackValidated query = convertInput(input);

		SawmillRecipe recipe = recipeMap.get(query);

		if (recipe == null) {
			query.metadata = OreDictionary.WILDCARD_VALUE;
			recipe = recipeMap.get(query);
		}
		return recipe;
	}

	public static boolean recipeExists(ItemStack input) {

		return getRecipe(input) != null;
	}

	public static SawmillRecipe[] getRecipeList() {

		return recipeMap.values().toArray(new SawmillRecipe[0]);
	}

	public static void initialize() {

		/* GENERAL SCAN */
		{
			InventoryCraftingFalse tempCrafting = new InventoryCraftingFalse(3, 3);

			for (ItemStack logWood : OreDictionary.getOres("logWood", false)) {
				Block logBlock = Block.getBlockFromItem(logWood.getItem());

				if (ItemHelper.getItemDamage(logWood) == OreDictionary.WILDCARD_VALUE) {
					NonNullList<ItemStack> logVariants = NonNullList.create();
					logBlock.getSubBlocks(logBlock.getCreativeTabToDisplayOn(), logVariants);

					for (ItemStack log : logVariants) {
						tempCrafting.setInventorySlotContents(0, log);
						ItemStack resultEntry = ItemHelper.getCraftingResult(tempCrafting, null);

						if (!resultEntry.isEmpty()) {
							ItemStack result = resultEntry.copy();
							result.setCount((int) (result.getCount() * LOG_MULTIPLIER));
							addRecipe(DEFAULT_ENERGY / 2, log, result, ItemMaterial.dustWood);
						}
					}
				} else {
					ItemStack log = ItemHelper.cloneStack(logWood, 1);
					tempCrafting.setInventorySlotContents(0, log);
					ItemStack resultEntry = ItemHelper.getCraftingResult(tempCrafting, null);

					if (!resultEntry.isEmpty()) {
						ItemStack result = resultEntry.copy();
						result.setCount((int) (result.getCount() * LOG_MULTIPLIER));
						addRecipe(DEFAULT_ENERGY / 2, log, result, ItemMaterial.dustWood);
					}
				}
			}
		}
	}

	public static void refresh() {

		Map<ComparableItemStackValidated, SawmillRecipe> tempMap = new THashMap<>(recipeMap.size());
		SawmillRecipe tempRecipe;

		for (Entry<ComparableItemStackValidated, SawmillRecipe> entry : recipeMap.entrySet()) {
			tempRecipe = entry.getValue();
			tempMap.put(convertInput(tempRecipe.input), tempRecipe);
		}
		recipeMap.clear();
		recipeMap = tempMap;
	}

	/* ADD RECIPES */
	public static SawmillRecipe addRecipe(int energy, ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance) {

		if (input.isEmpty() || primaryOutput.isEmpty() || energy <= 0 || recipeExists(input)) {
			return null;
		}
		SawmillRecipe recipe = new SawmillRecipe(input, primaryOutput, secondaryOutput, secondaryOutput.isEmpty() ? 0 : secondaryChance, energy);
		recipeMap.put(convertInput(input), recipe);
		return recipe;
	}

	public static SawmillRecipe addRecipe(int energy, ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput) {

		return addRecipe(energy, input, primaryOutput, secondaryOutput, 100);
	}

	public static SawmillRecipe addRecipe(int energy, ItemStack input, ItemStack primaryOutput) {

		return addRecipe(energy, input, primaryOutput, ItemStack.EMPTY, 0);
	}

	/* REMOVE RECIPES */
	public static SawmillRecipe removeRecipe(ItemStack input) {

		return recipeMap.remove(convertInput(input));
	}

	/* HELPERS */
	public static ComparableItemStackValidated convertInput(ItemStack stack) {

		return new ComparableItemStackValidated(stack, oreValidator);
	}

	/* RECIPE CLASS */
	public static class SawmillRecipe {

		final ItemStack input;
		final ItemStack primaryOutput;
		final ItemStack secondaryOutput;
		final int secondaryChance;
		final int energy;

		SawmillRecipe(ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance, int energy) {

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
