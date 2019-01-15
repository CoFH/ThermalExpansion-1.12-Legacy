package cofh.thermalexpansion.util.managers.machine;

import cofh.core.inventory.ComparableItemStack;
import cofh.core.inventory.ComparableItemStackValidatedNBT;
import cofh.core.inventory.OreValidator;
import cofh.thermalfoundation.item.ItemMaterial;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static java.util.Arrays.asList;

public class EnchanterManager {

	private static Map<List<ComparableItemStackValidatedNBT>, EnchanterRecipe> recipeMap = new Object2ObjectOpenHashMap<>();
	private static Set<ComparableItemStackValidatedNBT> validationSet = new ObjectOpenHashSet<>();
	private static Set<ComparableItemStackValidatedNBT> lockSet = new ObjectOpenHashSet<>();
	private static OreValidator oreValidator = new OreValidator();

	static {
		oreValidator.addPrefix(ComparableItemStack.INGOT);
		oreValidator.addPrefix(ComparableItemStack.NUGGET);
		oreValidator.addPrefix(ComparableItemStack.DUST);
		oreValidator.addPrefix(ComparableItemStack.GEM);
	}

	public static final ItemStack ITEM_BOOK = new ItemStack(Items.BOOK);

	public static final int DEFAULT_ENERGY[] = { 4000, 12000, 24000, 40000, 60000 };
	public static final int DEFAULT_EXPERIENCE[] = { 500, 1500, 3000, 5000, 7500 };

	public static boolean isRecipeReversed(ItemStack primaryInput, ItemStack secondaryInput) {

		if (primaryInput.isEmpty() || secondaryInput.isEmpty()) {
			return false;
		}
		ComparableItemStackValidatedNBT query = convertInput(primaryInput);
		ComparableItemStackValidatedNBT querySecondary = convertInput(secondaryInput);

		EnchanterRecipe recipe = recipeMap.get(asList(query, querySecondary));
		return recipe == null && recipeMap.get(asList(querySecondary, query)) != null;
	}

	public static EnchanterRecipe getRecipe(ItemStack primaryInput, ItemStack secondaryInput) {

		if (primaryInput.isEmpty() || secondaryInput.isEmpty()) {
			return null;
		}
		ComparableItemStackValidatedNBT query = convertInput(primaryInput);
		ComparableItemStackValidatedNBT querySecondary = convertInput(secondaryInput);

		EnchanterRecipe recipe = recipeMap.get(asList(query, querySecondary));

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

	public static EnchanterRecipe[] getRecipeList() {

		return recipeMap.values().toArray(new EnchanterRecipe[0]);
	}

	public static boolean isItemValid(ItemStack input) {

		return !input.isEmpty() && validationSet.contains(convertInput(input));
	}

	public static boolean isItemArcana(ItemStack input) {

		return !input.isEmpty() && lockSet.contains(convertInput(input));
	}

	public static void preInit() {

		/* ARCANA */
		{
			addArcana(new ItemStack(Items.BOOK));
		}
	}

	public static void initialize() {

		addEnchantmentBooks();
	}

	public static void refresh() {

		Map<List<ComparableItemStackValidatedNBT>, EnchanterRecipe> tempMap = new Object2ObjectOpenHashMap<>(recipeMap.size());
		Set<ComparableItemStackValidatedNBT> tempSet = new ObjectOpenHashSet<>();
		EnchanterRecipe tempRecipe;

		for (Entry<List<ComparableItemStackValidatedNBT>, EnchanterRecipe> entry : recipeMap.entrySet()) {
			tempRecipe = entry.getValue();
			ComparableItemStackValidatedNBT primary = convertInput(tempRecipe.primaryInput);
			ComparableItemStackValidatedNBT secondary = convertInput(tempRecipe.secondaryInput);

			if (!tempRecipe.enchantName.isEmpty() && tempRecipe.output.getItem() == Items.ENCHANTED_BOOK) {
				Enchantment enchant = Enchantment.getEnchantmentByLocation(tempRecipe.enchantName);
				if (enchant != null) {
					tempRecipe = new EnchanterRecipe(tempRecipe.primaryInput, tempRecipe.secondaryInput, ItemEnchantedBook.getEnchantedItemStack(new EnchantmentData(enchant, 1)), tempRecipe.experience, tempRecipe.energy, tempRecipe.type);
				}
			}
			tempMap.put(asList(primary, secondary), tempRecipe);
			tempSet.add(primary);
			tempSet.add(secondary);
		}
		recipeMap.clear();
		recipeMap = tempMap;
		validationSet.clear();
		validationSet = tempSet;

		Set<ComparableItemStackValidatedNBT> tempSet2 = new ObjectOpenHashSet<>();
		for (ComparableItemStackValidatedNBT entry : lockSet) {
			ComparableItemStackValidatedNBT lock = convertInput(new ItemStack(entry.item, entry.stackSize, entry.metadata));
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
		recipeMap.put(asList(convertInput(primaryInput), convertInput(secondaryInput)), recipe);
		validationSet.add(convertInput(primaryInput));
		validationSet.add(convertInput(secondaryInput));
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
		addRecipe(DEFAULT_ENERGY[tier], ITEM_BOOK, input, ItemEnchantedBook.getEnchantedItemStack(new EnchantmentData(enchant, 1)), DEFAULT_EXPERIENCE[tier], Type.STANDARD);
		//		if (enchant.getMaxLevel() > 1) {
		//			addRecipe(DEFAULT_ENERGY[Math.min(tier + 1, 4)] * 3, ITEM_BOOK2, input, ItemEnchantedBook.getEnchantedItemStack(new EnchantmentData(enchant, enchant.getMaxLevel())), DEFAULT_EXPERIENCE[Math.min(tier + 1, 4)], Type.EMPOWERED);
		//		}
	}

	/* REMOVE RECIPES */
	public static EnchanterRecipe removeRecipe(ItemStack primaryInput, ItemStack secondaryInput) {

		return recipeMap.remove(asList(convertInput(primaryInput), convertInput(secondaryInput)));
	}

	/* HELPERS */
	public static ComparableItemStackValidatedNBT convertInput(ItemStack stack) {

		return new ComparableItemStackValidatedNBT(stack, oreValidator);
	}

	private static void addArcana(ItemStack arcana) {

		lockSet.add(convertInput(arcana));
	}

	private static void addEnchantmentBooks() {

		/* GENERAL */
		{
			addDefaultEnchantmentRecipe(new ItemStack(Blocks.OBSIDIAN), "unbreaking", 1);
			addDefaultEnchantmentRecipe(new ItemStack(Items.NETHER_STAR), "mending", 3);
			addDefaultEnchantmentRecipe(new ItemStack(Items.CHORUS_FRUIT_POPPED), "binding_curse", 3);
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
			addDefaultEnchantmentRecipe(new ItemStack(Items.GHAST_TEAR), "vanishing_curse", 3);
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
			addDefaultEnchantmentRecipe(new ItemStack(Items.NETHER_WART), "cofhcore:leech", 1);
			addDefaultEnchantmentRecipe(new ItemStack(Items.ARROW), "cofhcore:multishot", 2);
			addDefaultEnchantmentRecipe(ItemMaterial.dustPetrotheum, "cofhcore:smashing", 2);
			addDefaultEnchantmentRecipe(ItemMaterial.dustPyrotheum, "cofhcore:smelting", 2);
			addDefaultEnchantmentRecipe(new ItemStack(Blocks.SOUL_SAND), "cofhcore:soulbound", 1);
			addDefaultEnchantmentRecipe(new ItemStack(Items.SKULL, 1, 1), "cofhcore:vorpal", 3);
		}
	}

	/* RECIPE CLASS */
	public static class EnchanterRecipe {

		final ItemStack primaryInput;
		final ItemStack secondaryInput;
		final ItemStack output;
		final int experience;
		final int energy;
		final String enchantName;
		final Type type;

		EnchanterRecipe(ItemStack primaryInput, ItemStack secondaryInput, ItemStack output, int experience, int energy, Type type) {

			this.primaryInput = primaryInput;
			this.secondaryInput = secondaryInput;
			this.output = output;
			this.experience = experience;
			this.energy = energy;
			this.type = type;

			Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(output);

			if (enchants.isEmpty() || enchants.size() > 1) {
				this.enchantName = "";
			} else {
				this.enchantName = enchants.keySet().stream().findFirst().get().getRegistryName().toString();
			}
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

		public String getEnchantName() {

			return enchantName;
		}

		public Type getType() {

			return type;
		}
	}

	/* TYPE ENUM */
	public enum Type {
		STANDARD, EMPOWERED
	}

}
