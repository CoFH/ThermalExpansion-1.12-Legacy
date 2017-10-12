package cofh.thermalexpansion.util.managers.machine;

import cofh.core.inventory.ComparableItemStack;
import cofh.core.util.helpers.ItemHelper;
import cofh.core.util.oredict.OreDictionaryArbiter;
import cofh.thermalfoundation.item.ItemTome;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;

import java.util.*;
import java.util.Map.Entry;

public class EnchanterManager {

	private static Map<List<ComparableItemStackEnchanter>, EnchanterRecipe> recipeMap = new THashMap<>();
	private static Map<List<ComparableItemStackEnchanter>, EnchanterRecipe> recipeMapEmpowered = new THashMap<>();
	private static Set<ComparableItemStackEnchanter> validationSet = new THashSet<>();
	private static Set<ComparableItemStackEnchanter> lockSet = new THashSet<>();

	public static final ItemStack ITEM_BOOK = new ItemStack(Items.BOOK);
	public static final ItemStack ITEM_BOOK2 = ItemTome.tomeLexicon;

	public static final int DEFAULT_ENERGY[] = { 4000, 8000, 12000, 16000, 20000 };
	public static final int DEFAULT_EXPERIENCE[] = { 500, 1500, 3000, 5000, 7500 };

	public static boolean isRecipeReversed(ItemStack primaryInput, ItemStack secondaryInput) {

		if (primaryInput.isEmpty() || secondaryInput.isEmpty()) {
			return false;
		}
		ComparableItemStackEnchanter query = new ComparableItemStackEnchanter(primaryInput);
		ComparableItemStackEnchanter querySecondary = new ComparableItemStackEnchanter(secondaryInput);

		EnchanterRecipe recipe = recipeMap.get(Arrays.asList(query, querySecondary));
		return recipe == null && recipeMap.get(Arrays.asList(querySecondary, query)) != null;
	}

	public static EnchanterRecipe getRecipe(ItemStack primaryInput, ItemStack secondaryInput) {

		if (primaryInput.isEmpty() || secondaryInput.isEmpty()) {
			return null;
		}
		ComparableItemStackEnchanter query = new ComparableItemStackEnchanter(primaryInput);
		ComparableItemStackEnchanter querySecondary = new ComparableItemStackEnchanter(secondaryInput);

		EnchanterRecipe recipe = recipeMap.get(Arrays.asList(query, querySecondary));

		if (recipe == null) {
			recipe = recipeMap.get(Arrays.asList(querySecondary, query));
		}
		if (recipe == null) {
			return null;
		}
		return recipe;
	}

	public static boolean recipeExists(ItemStack primaryInput, ItemStack secondaryInput) {

		return getRecipe(primaryInput, secondaryInput) != null;
	}

	public static EnchanterRecipe[] getRecipeList() {

		return recipeMap.values().toArray(new EnchanterRecipe[recipeMap.size()]);
	}

	public static boolean isItemValid(ItemStack input) {

		return !input.isEmpty() && validationSet.contains(new ComparableItemStackEnchanter(input));
	}

	public static boolean isItemArcana(ItemStack input) {

		return !input.isEmpty() && lockSet.contains(new ComparableItemStackEnchanter(input));
	}

	public static void initialize() {

		ItemStack book = new ItemStack(Items.BOOK);

		/* ARCANA */
		{
			addArcana(book);
		}

		/* GENERAL */
		{
			addDefaultEnchantmentRecipe(new ItemStack(Blocks.OBSIDIAN), "unbreaking", 1);
			addDefaultEnchantmentRecipe(new ItemStack(Items.NETHER_STAR), "mending", 3);
		}

		/* ARMOR */
		{
			addDefaultEnchantmentRecipe(new ItemStack(Items.IRON_INGOT), "protection", 0);
			addDefaultEnchantmentRecipe(new ItemStack(Items.MAGMA_CREAM), "fire_protection", 1);
			addDefaultEnchantmentRecipe(new ItemStack(Items.FEATHER), "feather_falling", 1);
			addDefaultEnchantmentRecipe(new ItemStack(Items.GUNPOWDER), "blast_protection", 1);
			addDefaultEnchantmentRecipe(new ItemStack(Items.SHIELD), "projectile_protection", 1);
			addDefaultEnchantmentRecipe(new ItemStack(Items.FISH, 1, 3), "respiration", 2);
			addDefaultEnchantmentRecipe(new ItemStack(Items.PRISMARINE_CRYSTALS), "aqua_affinity", 2);
			addDefaultEnchantmentRecipe(new ItemStack(Items.PRISMARINE_SHARD), "depth_strider", 2);
			addDefaultEnchantmentRecipe(new ItemStack(Blocks.DOUBLE_PLANT, 1, 4), "thorns", 3);
			addDefaultEnchantmentRecipe(new ItemStack(Blocks.ICE), "frost_walker", 3);
		}

		/* SWORDS */
		{
			addDefaultEnchantmentRecipe(new ItemStack(Items.QUARTZ), "sharpness", 0);
			addDefaultEnchantmentRecipe(new ItemStack(Items.ROTTEN_FLESH), "smite", 1);
			addDefaultEnchantmentRecipe(new ItemStack(Items.SPIDER_EYE), "bane_of_arthropods", 1);
			addDefaultEnchantmentRecipe(new ItemStack(Blocks.PISTON), "knockback", 1);
			addDefaultEnchantmentRecipe(new ItemStack(Items.BLAZE_ROD), "fire_aspect", 2);
			addDefaultEnchantmentRecipe(new ItemStack(Items.GOLD_INGOT), "looting", 2);
			addDefaultEnchantmentRecipe(new ItemStack(Items.REEDS), "sweeping", 2);
		}

		/* TOOLS */
		{
			addDefaultEnchantmentRecipe(new ItemStack(Items.REDSTONE), "efficiency", 0);
			addDefaultEnchantmentRecipe(new ItemStack(Items.EMERALD), "fortune", 2);
			addDefaultEnchantmentRecipe(new ItemStack(Items.GLOWSTONE_DUST), "silk_touch", 3);
		}

		/* BOWS */
		{
			addDefaultEnchantmentRecipe(new ItemStack(Items.FLINT), "power", 0);
			addDefaultEnchantmentRecipe(new ItemStack(Items.STRING), "punch", 2);
			addDefaultEnchantmentRecipe(new ItemStack(Items.BLAZE_POWDER), "flame", 2);
			addDefaultEnchantmentRecipe(new ItemStack(Items.ENDER_EYE), "infinity", 3);
		}

		/* FISHING RODS */
		{
			addDefaultEnchantmentRecipe(new ItemStack(Items.FISH, 1, 2), "luck_of_the_sea", 2);
			addDefaultEnchantmentRecipe(new ItemStack(Items.CARROT_ON_A_STICK), "lure", 2);
		}

		/* COFH ENCHANTS */
		{
			addDefaultEnchantmentRecipe(new ItemStack(Blocks.CHEST), "cofhcore:holding", 0);
			addDefaultEnchantmentRecipe(new ItemStack(Items.EXPERIENCE_BOTTLE), "cofhcore:insight", 1);
			addDefaultEnchantmentRecipe(new ItemStack(Blocks.SOUL_SAND), "cofhcore:leech", 1);
			addDefaultEnchantmentRecipe(new ItemStack(Items.ARROW), "cofhcore:multishot", 2);
			// addDefaultEnchantmentRecipe(new ItemStack(Items.SKULL), "cofhcore:vorpal", 3);
		}

		/* LOAD RECIPES */
		loadRecipes();
	}

	public static void loadRecipes() {

	}

	public static void refresh() {

		Map<List<ComparableItemStackEnchanter>, EnchanterRecipe> tempMap = new THashMap<>(recipeMap.size());
		Set<ComparableItemStackEnchanter> tempSet = new THashSet<>();
		EnchanterRecipe tempRecipe;

		for (Entry<List<ComparableItemStackEnchanter>, EnchanterRecipe> entry : recipeMap.entrySet()) {
			tempRecipe = entry.getValue();
			ComparableItemStackEnchanter primary = new ComparableItemStackEnchanter(tempRecipe.primaryInput);
			ComparableItemStackEnchanter secondary = new ComparableItemStackEnchanter(tempRecipe.secondaryInput);

			tempMap.put(Arrays.asList(primary, secondary), tempRecipe);
			tempSet.add(primary);
			tempSet.add(secondary);
		}
		recipeMap.clear();
		recipeMap = tempMap;
		validationSet.clear();
		validationSet = tempSet;

		Set<ComparableItemStackEnchanter> tempSet2 = new THashSet<>();
		for (ComparableItemStackEnchanter entry : lockSet) {
			ComparableItemStackEnchanter lock = new ComparableItemStackEnchanter(new ItemStack(entry.item, entry.stackSize, entry.metadata));
			tempSet2.add(lock);
		}
		lockSet.clear();
		lockSet = tempSet2;
	}

	/* ADD RECIPES */
	public static EnchanterRecipe addRecipe(int energy, ItemStack primaryInput, ItemStack secondaryInput, ItemStack output, int experience, Type type) {

		if (primaryInput.isEmpty() || secondaryInput.isEmpty() || energy <= 0 || recipeExists(primaryInput, secondaryInput)) {
			return null;
		}
		EnchanterRecipe recipe = new EnchanterRecipe(primaryInput, secondaryInput, output, experience, energy, type);
		recipeMap.put(Arrays.asList(new ComparableItemStackEnchanter(primaryInput), new ComparableItemStackEnchanter(secondaryInput)), recipe);
		validationSet.add(new ComparableItemStackEnchanter(primaryInput));
		validationSet.add(new ComparableItemStackEnchanter(secondaryInput));
		return recipe;
	}

	public static void addDefaultEnchantmentRecipe(ItemStack input, String enchantName, int tier) {

		if (tier < 0 || tier > 4) {
			return;
		}
		Enchantment enchant = Enchantment.getEnchantmentByLocation(enchantName);

		if (enchant == null) {
			return;
		}
		if (enchant.isTreasureEnchantment()) {
			addRecipe(DEFAULT_ENERGY[tier], ITEM_BOOK, input, ItemEnchantedBook.getEnchantedItemStack(new EnchantmentData(enchant, 1)), DEFAULT_EXPERIENCE[tier], Type.TREASURE);
			if (enchant.getMaxLevel() > 1) {
				addRecipe(DEFAULT_ENERGY[Math.min(tier + 1, 4)], ITEM_BOOK2, input, ItemEnchantedBook.getEnchantedItemStack(new EnchantmentData(enchant, enchant.getMaxLevel())), DEFAULT_EXPERIENCE[Math.min(tier + 1, 4)], Type.TREASURE_EMPOWERED);
			}
		} else {
			addRecipe(DEFAULT_ENERGY[tier], ITEM_BOOK, input, ItemEnchantedBook.getEnchantedItemStack(new EnchantmentData(enchant, 1)), DEFAULT_EXPERIENCE[tier], Type.STANDARD);
			if (enchant.getMaxLevel() > 1) {
				addRecipe(DEFAULT_ENERGY[Math.min(tier + 1, 4)], ITEM_BOOK2, input, ItemEnchantedBook.getEnchantedItemStack(new EnchantmentData(enchant, enchant.getMaxLevel())), DEFAULT_EXPERIENCE[Math.min(tier + 1, 4)], Type.EMPOWERED);
			}
		}
	}

	/* REMOVE RECIPES */
	public static EnchanterRecipe removeRecipe(ItemStack primaryInput, ItemStack secondaryInput) {

		return recipeMap.remove(Arrays.asList(new ComparableItemStackEnchanter(primaryInput), new ComparableItemStackEnchanter(secondaryInput)));
	}

	/* HELPERS */
	private static void addArcana(ItemStack arcana) {

		lockSet.add(new ComparableItemStackEnchanter(arcana));
	}

	/* RECIPE CLASS */
	public static class EnchanterRecipe {

		final ItemStack primaryInput;
		final ItemStack secondaryInput;
		final ItemStack output;
		final int experience;
		final int energy;
		final Type type;

		EnchanterRecipe(ItemStack primaryInput, ItemStack secondaryInput, ItemStack output, int experience, int energy, Type type) {

			this.primaryInput = primaryInput;
			this.secondaryInput = secondaryInput;
			this.output = output;
			this.experience = experience;
			this.energy = energy;
			this.type = type;
		}

		public ItemStack getPrimaryInput() {

			return primaryInput;
		}

		public ItemStack getSecondaryInput() {

			return secondaryInput;
		}

		public ItemStack getOutput() {

			return output;
		}

		public int getEnergy() {

			return energy;
		}

		public int getExperience() {

			return experience;
		}

		public Type getType() {

			return type;
		}
	}

	/* TYPE ENUM */
	public enum Type {
		STANDARD, TREASURE, EMPOWERED, TREASURE_EMPOWERED
	}

	/* ITEMSTACK CLASS */
	public static class ComparableItemStackEnchanter extends ComparableItemStack {

		public static final String INGOT = "ingot";
		public static final String NUGGET = "nugget";

		public static boolean safeOreType(String oreName) {

			return oreName.startsWith(INGOT) || oreName.startsWith(NUGGET);
		}

		public static int getOreID(ItemStack stack) {

			ArrayList<Integer> ids = OreDictionaryArbiter.getAllOreIDs(stack);

			if (ids != null) {
				for (Integer id : ids) {
					if (id != -1 && safeOreType(ItemHelper.oreProxy.getOreName(id))) {
						return id;
					}
				}
			}
			return -1;
		}

		public ComparableItemStackEnchanter(ItemStack stack) {

			super(stack);
			oreID = getOreID(stack);
		}
	}

}
