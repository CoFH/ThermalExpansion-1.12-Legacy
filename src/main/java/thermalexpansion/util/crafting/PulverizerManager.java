package thermalexpansion.util.crafting;

import cofh.util.ItemHelper;
import cofh.util.MathHelper;
import cofh.util.StringHelper;
import cofh.util.inventory.ComparableItemStack;

import gnu.trove.map.hash.THashMap;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.item.TEItems;
import thermalfoundation.item.TFItems;

public class PulverizerManager {

	private static Map<ComparableItemStackPulverizer, RecipePulverizer> recipeMap = new THashMap();
	private static ComparableItemStackPulverizer query = new ComparableItemStackPulverizer(new ItemStack(Blocks.stone));
	private static boolean allowOverwrite = false;

	static {
		allowOverwrite = ThermalExpansion.config.get("tweak.crafting", "Pulverizer.AllowRecipeOverwrite", false);
	}

	public static RecipePulverizer getRecipe(ItemStack input) {

		return input == null ? null : recipeMap.get(query.set(input));
	}

	public static boolean recipeExists(ItemStack input) {

		return getRecipe(input) != null;
	}

	public static RecipePulverizer[] getRecipeList() {

		return recipeMap.values().toArray(new RecipePulverizer[0]);
	}

	public static void addDefaultRecipes() {

		String category = "tweak.crafting";

		boolean recipeSandstone = ThermalExpansion.config.get(category, "Pulverizer.Sandstone", true);
		boolean recipeNetherrack = ThermalExpansion.config.get(category, "Pulverizer.Netherrack", true);
		boolean recipeCloth = ThermalExpansion.config.get(category, "Pulverizer.Cloth", true);
		boolean recipeReed = ThermalExpansion.config.get(category, "Pulverizer.Reed", true);
		boolean recipeBone = ThermalExpansion.config.get(category, "Pulverizer.Bone", true);
		boolean recipeBlazeRod = ThermalExpansion.config.get(category, "Pulverizer.BlazeRod", true);
		boolean recipeBlizzRod = ThermalExpansion.config.get(category, "Pulverizer.BlizzRod", true);

		int chanceCinnabar = (int) MathHelper.clip(ThermalExpansion.config.get(category, "Pulverizer.Cinnabar.Chance", 25), 1, 100);
		int woolDyeChance = (int) MathHelper.clip(ThermalExpansion.config.get(category, "Pulverizer.Wool.Dye.Chance", 5), 0, 100);

		addRecipe(3200, new ItemStack(Blocks.stone), new ItemStack(Blocks.cobblestone));
		addRecipe(3200, new ItemStack(Blocks.cobblestone), new ItemStack(Blocks.sand), new ItemStack(Blocks.gravel), 10);
		addRecipe(3200, new ItemStack(Blocks.gravel), new ItemStack(Items.flint));
		addRecipe(3200, new ItemStack(Blocks.glass), new ItemStack(Blocks.sand));
		addRecipe(800, new ItemStack(Blocks.stonebrick), new ItemStack(Blocks.stonebrick, 1, 2));

		if (recipeSandstone) {
			addTERecipe(3200, new ItemStack(Blocks.sandstone), new ItemStack(Blocks.sand, 2), TFItems.dustNiter, 15);
		}
		addRecipe(2400, new ItemStack(Items.coal, 1, 0), TFItems.dustCoal, TFItems.dustSulfur, 10);
		addRecipe(2400, new ItemStack(Items.coal, 1, 1), TFItems.dustCharcoal);
		addRecipe(4000, new ItemStack(Blocks.obsidian), ItemHelper.cloneStack(TFItems.dustObsidian, 4));

		if (recipeNetherrack) {
			addTERecipe(3200, new ItemStack(Blocks.netherrack), new ItemStack(Blocks.gravel), TFItems.dustSulfur, 10);
		}
		addRecipe(2400, new ItemStack(Blocks.coal_ore), new ItemStack(Items.coal, 2, 0));
		addRecipe(2400, new ItemStack(Blocks.diamond_ore), new ItemStack(Items.diamond, 2, 0));
		addRecipe(2400, new ItemStack(Blocks.emerald_ore), new ItemStack(Items.emerald, 2, 0));
		addRecipe(2400, new ItemStack(Blocks.glowstone), new ItemStack(Items.glowstone_dust, 4));
		addRecipe(2400, new ItemStack(Blocks.lapis_ore), new ItemStack(Items.dye, 8, 4));
		addTERecipe(3200, new ItemStack(Blocks.redstone_ore), new ItemStack(Items.redstone, 6), TFItems.crystalCinnabar, chanceCinnabar);
		addRecipe(2400, new ItemStack(Blocks.quartz_ore), new ItemStack(Items.quartz, 2), TFItems.dustSulfur, 10);

		for (int i = 0; i < 3; i++) {
			addRecipe(2400, new ItemStack(Blocks.quartz_block, 1, i), new ItemStack(Items.quartz, 4));
		}
		addRecipe(2400, new ItemStack(Blocks.quartz_stairs), new ItemStack(Items.quartz, 6));
		addRecipe(1600, new ItemStack(Blocks.log), ItemHelper.cloneStack(TEItems.sawdust, 2));
		// addRecipe(1600, new ItemStack(Blocks.log2), ItemHelper.cloneStack(TEItems.sawdust, 2));

		addRecipe(1600, new ItemStack(Blocks.red_flower), new ItemStack(Items.dye, 4, 1));
		addRecipe(1600, new ItemStack(Blocks.yellow_flower), new ItemStack(Items.dye, 4, 11));

		if (recipeCloth) {
			ItemStack silkStack = new ItemStack(Items.string, 4);
			if (woolDyeChance > 0) {
				for (int i = 1; i < 16; i++) {
					addTERecipe(1600, new ItemStack(Blocks.wool, 1, i), silkStack, new ItemStack(Items.dye, 1, 15 - i), woolDyeChance);
				}
				addTERecipe(1600, new ItemStack(Blocks.wool, 1, 0), silkStack);
			} else {
				for (int i = 0; i < 16; i++) {
					addTERecipe(1600, new ItemStack(Blocks.wool, 1, i), silkStack);
				}
			}
		}
		if (recipeReed) {
			addTERecipe(800, new ItemStack(Items.reeds), new ItemStack(Items.sugar, 2));
		}
		if (recipeBone) {
			addTERecipe(1600, new ItemStack(Items.bone), new ItemStack(Items.dye, 6, 15));
		}
		if (recipeBlazeRod) {
			addTERecipe(1600, new ItemStack(Items.blaze_rod), new ItemStack(Items.blaze_powder, 4), TFItems.dustSulfur, 50);
		}
		if (recipeBlizzRod) {
			addTERecipe(1600, TFItems.rodBlizz, ItemHelper.cloneStack(TFItems.dustBlizz, 4), new ItemStack(Items.snowball), 50);
		}

		int energy = 4000;

		addOreNameToDustRecipe(energy, "oreIron", TFItems.dustIron, TFItems.dustNickel, 10);
		addOreNameToDustRecipe(energy, "oreGold", TFItems.dustGold, null, 0);
		addOreNameToDustRecipe(energy, "oreCopper", TFItems.dustCopper, TFItems.dustGold, 10);
		addOreNameToDustRecipe(energy, "oreTin", TFItems.dustTin, TFItems.dustIron, 10);
		addOreNameToDustRecipe(energy, "oreSilver", TFItems.dustSilver, TFItems.dustLead, 10);
		addOreNameToDustRecipe(energy, "oreLead", TFItems.dustLead, TFItems.dustSilver, 10);
		addOreNameToDustRecipe(energy, "oreNickel", TFItems.dustNickel, TFItems.dustPlatinum, 10);
		addOreNameToDustRecipe(energy, "orePlatinum", TFItems.dustPlatinum, null, 0);

		energy = 2400;

		addIngotNameToDustRecipe(energy, "ingotIron", TFItems.dustIron);
		addIngotNameToDustRecipe(energy, "ingotGold", TFItems.dustGold);
		addIngotNameToDustRecipe(energy, "ingotCopper", TFItems.dustCopper);
		addIngotNameToDustRecipe(energy, "ingotTin", TFItems.dustTin);
		addIngotNameToDustRecipe(energy, "ingotSilver", TFItems.dustSilver);
		addIngotNameToDustRecipe(energy, "ingotLead", TFItems.dustLead);
		addIngotNameToDustRecipe(energy, "ingotNickel", TFItems.dustNickel);
		addIngotNameToDustRecipe(energy, "ingotPlatinum", TFItems.dustPlatinum);
		addIngotNameToDustRecipe(energy, "ingotElectrum", TFItems.dustElectrum);
		addIngotNameToDustRecipe(energy, "ingotInvar", TFItems.dustInvar);
		addIngotNameToDustRecipe(energy, "ingotBronze", TFItems.dustBronze);
	}

	public static void loadRecipes() {

		if (ItemHelper.oreNameExists("dustEnderPearl")) {
			addRecipe(1600, new ItemStack(Items.ender_pearl), ItemHelper.cloneStack(OreDictionary.getOres("dustEnderPearl").get(0), 1));
		}
		if (ItemHelper.oreNameExists("oreSaltpeter")) {
			addRecipe(2400, OreDictionary.getOres("oreSaltpeter").get(0), ItemHelper.cloneStack(TFItems.dustNiter, 4));
		}
		if (ItemHelper.oreNameExists("oreSulfur")) {
			addRecipe(2400, OreDictionary.getOres("oreSulfur").get(0), ItemHelper.cloneStack(TFItems.dustSulfur, 6));
		}
		if (ItemHelper.oreNameExists("oreCertusQuartz") && ItemHelper.oreNameExists("dustCertusQuartz") && ItemHelper.oreNameExists("crystalCertusQuartz")) {
			addRecipe(2400, OreDictionary.getOres("oreCertusQuartz").get(0), ItemHelper.cloneStack(OreDictionary.getOres("crystalCertusQuartz").get(0), 2),
					OreDictionary.getOres("dustCertusQuartz").get(0), 10);
			addRecipe(1600, OreDictionary.getOres("crystalCertusQuartz").get(0), OreDictionary.getOres("dustCertusQuartz").get(0));
		}
		if (ItemHelper.oreNameExists("dustNetherQuartz")) {
			addRecipe(1600, new ItemStack(Items.quartz, 1), ItemHelper.cloneStack(OreDictionary.getOres("dustNetherQuartz").get(0), 1));
		}

		String[] oreNameList = OreDictionary.getOreNames();
		String oreName = "";

		for (int i = 0; i < oreNameList.length; i++) {
			if (oreNameList[i].startsWith("ore")) {
				oreName = oreNameList[i].substring(3, oreNameList[i].length());
				addDefaultOreDictionaryRecipe(oreName);
			} else if (oreNameList[i].startsWith("dust")) {
				oreName = oreNameList[i].substring(4, oreNameList[i].length());
				addDefaultOreDictionaryRecipe(oreName);
			}
		}
	}

	public static void refreshRecipes() {

		Map<ComparableItemStackPulverizer, RecipePulverizer> tempMap = new THashMap(recipeMap.size());
		RecipePulverizer tempRecipe;

		for (Entry<ComparableItemStackPulverizer, RecipePulverizer> entry : recipeMap.entrySet()) {
			tempRecipe = entry.getValue();
			tempMap.put(new ComparableItemStackPulverizer(tempRecipe.input), tempRecipe);
		}
		recipeMap.clear();
		recipeMap = tempMap;
	}

	/* ADD RECIPES */
	protected static boolean addTERecipe(int energy, ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance) {

		if (input == null || primaryOutput == null || energy <= 0) {
			return false;
		}
		RecipePulverizer recipe = new RecipePulverizer(input, primaryOutput, secondaryOutput, secondaryChance, energy);
		recipeMap.put(new ComparableItemStackPulverizer(input), recipe);
		return true;
	}

	public static boolean addRecipe(int energy, ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance, boolean overwrite) {

		if (input == null || primaryOutput == null || energy <= 0 || !(allowOverwrite & overwrite) && recipeExists(input)) {
			return false;
		}
		RecipePulverizer recipe = new RecipePulverizer(input, primaryOutput, secondaryOutput, secondaryChance, energy);
		recipeMap.put(new ComparableItemStackPulverizer(input), recipe);
		return true;
	}

	public static boolean removeRecipe(ItemStack input) {

		return recipeMap.remove(new ComparableItemStackPulverizer(input)) != null;
	}

	/* HELPER FUNCTIONS */
	public static void addDefaultOreDictionaryRecipe(String oreType) {

		addDefaultOreDictionaryRecipe(oreType, "");
	}

	public static void addDefaultOreDictionaryRecipe(String oreType, String relatedType) {

		String oreName = "ore" + StringHelper.titleCase(oreType);
		String dustName = "dust" + StringHelper.titleCase(oreType);
		String ingotName = "ingot" + StringHelper.titleCase(oreType);

		ArrayList<ItemStack> registeredOre = OreDictionary.getOres(oreName);
		ArrayList<ItemStack> registeredDust = OreDictionary.getOres(dustName);
		ArrayList<ItemStack> registeredIngot = OreDictionary.getOres(ingotName);
		ArrayList<ItemStack> registeredRelated = new ArrayList<ItemStack>();

		String clusterName = "cluster" + StringHelper.titleCase(oreType);
		ArrayList<ItemStack> registeredCluster = OreDictionary.getOres(clusterName);

		if (relatedType != "") {
			String relatedName = "dust" + StringHelper.titleCase(relatedType);
			registeredRelated = OreDictionary.getOres(relatedName);
		}
		if (registeredDust.isEmpty()) {
			return;
		}
		if (registeredIngot.isEmpty()) {
			ingotName = null;
		}
		if (registeredOre.isEmpty()) {
			oreName = null;
		}
		if (registeredCluster.isEmpty()) {
			clusterName = null;
		}
		if (!registeredRelated.isEmpty()) {
			addOreNameToDustRecipe(4000, oreName, ItemHelper.cloneStack(registeredDust.get(0), 2), ItemHelper.cloneStack(registeredRelated.get(0), 1), 5);
			addOreNameToDustRecipe(4800, clusterName, ItemHelper.cloneStack(registeredDust.get(0), 2), ItemHelper.cloneStack(registeredRelated.get(0), 1), 5);
		} else {
			addOreNameToDustRecipe(4000, oreName, ItemHelper.cloneStack(registeredDust.get(0), 2), null, 0);
			addOreNameToDustRecipe(4800, clusterName, ItemHelper.cloneStack(registeredDust.get(0), 2), null, 0);
		}
		addIngotNameToDustRecipe(2400, ingotName, ItemHelper.cloneStack(registeredDust.get(0), 1));
	}

	public static void addOreNameToDustRecipe(int energy, String oreName, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance) {

		if (primaryOutput == null || oreName == null) {
			return;
		}
		ArrayList<ItemStack> registeredOres = OreDictionary.getOres(oreName);

		if (!registeredOres.isEmpty()) {
			addRecipe(energy, ItemHelper.cloneStack(registeredOres.get(0), 1), ItemHelper.cloneStack(primaryOutput, 2), secondaryOutput, secondaryChance);
		}
	}

	public static void addOreToDustRecipe(int energy, ItemStack ore, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance) {

		if (primaryOutput == null) {
			return;
		}
		ItemStack dust = ItemHelper.cloneStack(primaryOutput, 2);
		addRecipe(energy, ore, dust, secondaryOutput, secondaryChance);
	}

	public static void addIngotNameToDustRecipe(int energy, String ingotName, ItemStack dust) {

		if (dust == null || ingotName == null) {
			return;
		}
		ArrayList<ItemStack> registeredOres = OreDictionary.getOres(ingotName);

		if (!registeredOres.isEmpty()) {
			addRecipe(energy, ItemHelper.cloneStack(registeredOres.get(0), 1), dust, null, 0);
		}
	}

	public static void addIngotNameToDustRecipe(int energy, String oreType) {

	}

	public static boolean addTERecipe(int energy, ItemStack input, ItemStack primaryOutput) {

		return addTERecipe(energy, input, primaryOutput, null, 0);
	}

	public static boolean addRecipe(int energy, ItemStack input, ItemStack primaryOutput) {

		return addRecipe(energy, input, primaryOutput, false);
	}

	public static boolean addRecipe(int energy, ItemStack input, ItemStack primaryOutput, boolean overwrite) {

		return addRecipe(energy, input, primaryOutput, null, 0, overwrite);
	}

	public static boolean addRecipe(int energy, ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput) {

		return addRecipe(energy, input, primaryOutput, secondaryOutput, false);
	}

	public static boolean addRecipe(int energy, ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput, boolean overwrite) {

		return addRecipe(energy, input, primaryOutput, secondaryOutput, 100, overwrite);
	}

	public static boolean addRecipe(int energy, ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance) {

		return addRecipe(energy, input, primaryOutput, secondaryOutput, secondaryChance, false);
	}

	/* RECIPE CLASS */
	public static class RecipePulverizer {

		final ItemStack input;
		final ItemStack primaryOutput;
		final ItemStack secondaryOutput;
		final int secondaryChance;
		final int energy;

		RecipePulverizer(ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance, int energy) {

			this.input = input;
			this.primaryOutput = primaryOutput;
			this.secondaryOutput = secondaryOutput;
			this.secondaryChance = secondaryChance;
			this.energy = energy;
		}

		public ItemStack getInput() {

			return input.copy();
		}

		public ItemStack getPrimaryOutput() {

			return primaryOutput.copy();
		}

		public ItemStack getSecondaryOutput() {

			if (secondaryOutput == null) {
				return null;
			}
			return secondaryOutput.copy();
		}

		public int getSecondaryOutputChance() {

			return secondaryChance;
		}

		public int getEnergy() {

			return energy;
		}

	}

	/* ITEMSTACK CLASS */
	public static class ComparableItemStackPulverizer extends ComparableItemStack {

		static final String BLOCK = "block";
		static final String ORE = "ore";
		static final String DUST = "dust";
		static final String INGOT = "ingot";
		static final String NUGGET = "nugget";
		static final String LOG = "log";

		public static boolean safeOreType(String oreName) {

			return oreName.startsWith(BLOCK) || oreName.startsWith(ORE) || oreName.startsWith(DUST) || oreName.startsWith(INGOT) || oreName.startsWith(NUGGET)
					|| oreName.startsWith(LOG);
		}

		public static int getOreID(ItemStack stack) {

			int id = ItemHelper.oreProxy.getOreID(stack);

			if (id == -1 || !safeOreType(ItemHelper.oreProxy.getOreName(id))) {
				return -1;
			}
			return id;
		}

		public static int getOreID(String oreName) {

			if (!safeOreType(oreName)) {
				return -1;
			}
			return ItemHelper.oreProxy.getOreID(oreName);
		}

		public ComparableItemStackPulverizer(ItemStack stack) {

			super(stack);
			oreID = getOreID(stack);
		}

		public ComparableItemStackPulverizer(Item item, int damage, int stackSize) {

			super(item, damage, stackSize);
			this.oreID = getOreID(this.toItemStack());
		}

		@Override
		public ComparableItemStackPulverizer set(ItemStack stack) {

			super.set(stack);
			oreID = getOreID(stack);

			return this;
		}

	}

}
