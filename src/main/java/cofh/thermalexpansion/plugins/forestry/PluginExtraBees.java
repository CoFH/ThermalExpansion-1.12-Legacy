package cofh.thermalexpansion.plugins.forestry;

import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.plugins.PluginTEBase;
import cofh.thermalexpansion.util.managers.machine.CentrifugeManager;
import cofh.thermalexpansion.util.managers.machine.TransposerManager;
import cofh.thermalfoundation.item.ItemMaterial;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import static java.util.Arrays.asList;

public class PluginExtraBees extends PluginTEBase {

	public static final String PARENT_ID = PluginForestry.MOD_ID;
	public static final String MOD_ID = "extrabees";
	public static final String MOD_NAME = "Extra Bees";

	public PluginExtraBees() {

		super(MOD_ID, MOD_NAME);
	}

	@Override
	public void initializeDelegate() {

		ItemStack dropHoney = getItemStack(PARENT_ID, "honey_drop", 1, 0);
		ItemStack wax = getItemStack(PARENT_ID, "beeswax", 1, 0);
		ItemStack compost = getItemStack(PARENT_ID, "fertilizer_bio", 1, 0);

		ItemStack combBarren = getItemStack("honey_comb", 1, 0);
		ItemStack combRotten = getItemStack("honey_comb", 1, 1);
		ItemStack combBone = getItemStack("honey_comb", 1, 2);
		ItemStack combOil = getItemStack("honey_comb", 1, 3);
		ItemStack combCoal = getItemStack("honey_comb", 1, 4);
		ItemStack combFuel = getItemStack("honey_comb", 1, 5);
		ItemStack combWater = getItemStack("honey_comb", 1, 6);
		ItemStack combMilk = getItemStack("honey_comb", 1, 7);
		ItemStack combFruit = getItemStack("honey_comb", 1, 8);
		ItemStack combSeed = getItemStack("honey_comb", 1, 9);
		ItemStack combAlcohol = getItemStack("honey_comb", 1, 10);
		ItemStack combStone = getItemStack("honey_comb", 1, 11);
		ItemStack combRedstone = getItemStack("honey_comb", 1, 12);
		ItemStack combResin = getItemStack("honey_comb", 1, 13);        // IC2
		ItemStack combIC2Energy = getItemStack("honey_comb", 1, 14);
		ItemStack combIron = getItemStack("honey_comb", 1, 15);
		ItemStack combGold = getItemStack("honey_comb", 1, 16);
		ItemStack combCopper = getItemStack("honey_comb", 1, 17);
		ItemStack combTin = getItemStack("honey_comb", 1, 18);
		ItemStack combSilver = getItemStack("honey_comb", 1, 19);
		ItemStack combBronze;
		ItemStack combUranium = getItemStack("honey_comb", 1, 21);
		ItemStack combClay = getItemStack("honey_comb", 1, 22);
		ItemStack combOld = getItemStack("honey_comb", 1, 23);
		ItemStack combFungal = getItemStack("honey_comb", 1, 24);
		ItemStack combCreosote = getItemStack("honey_comb", 1, 25);
		ItemStack combLatex = getItemStack("honey_comb", 1, 26);        // Rubber
		ItemStack combAcidic = getItemStack("honey_comb", 1, 27);       // Sulfur
		ItemStack combVenomous = getItemStack("honey_comb", 1, 28);
		ItemStack combSlime = getItemStack("honey_comb", 1, 29);
		ItemStack combBlaze = getItemStack("honey_comb", 1, 30);
		ItemStack combCoffee = getItemStack("honey_comb", 1, 31);       // IC2
		ItemStack combGlacial = getItemStack("honey_comb", 1, 32);
		ItemStack combMint;
		ItemStack combCitrus;
		ItemStack combPeat;
		ItemStack combShadow = getItemStack("honey_comb", 1, 36);       // Obsidian
		ItemStack combLead = getItemStack("honey_comb", 1, 37);
		ItemStack combBrass;
		ItemStack combElectrum;
		ItemStack combZinc = getItemStack("honey_comb", 1, 40);
		ItemStack combTitanium = getItemStack("honey_comb", 1, 41);
		ItemStack combTungsten = getItemStack("honey_comb", 1, 42);
		ItemStack combSteel;
		ItemStack combIridium;
		ItemStack combPlatinum = getItemStack("honey_comb", 1, 45);
		ItemStack combLapis = getItemStack("honey_comb", 1, 46);
		ItemStack combSodalite = getItemStack("honey_comb", 1, 47);
		ItemStack combPyrite = getItemStack("honey_comb", 1, 48);
		ItemStack combBauxite = getItemStack("honey_comb", 1, 49);
		ItemStack combCinnabar = getItemStack("honey_comb", 1, 50);
		ItemStack combSphalerite = getItemStack("honey_comb", 1, 51);
		ItemStack combEmerald = getItemStack("honey_comb", 1, 52);
		ItemStack combRuby = getItemStack("honey_comb", 1, 53);
		ItemStack combSapphire = getItemStack("honey_comb", 1, 54);
		ItemStack combOlivine;
		ItemStack combDiamond = getItemStack("honey_comb", 1, 56);
		// Dyes
		ItemStack combNickel = getItemStack("honey_comb", 1, 73);
		ItemStack combInvar;
		ItemStack combGlowstone = getItemStack("honey_comb", 1, 75);
		ItemStack combSaltpeter = getItemStack("honey_comb", 1, 76);
		ItemStack combPulp;
		ItemStack combMulch;
		ItemStack combCompost = getItemStack("honey_comb", 1, 79);
		ItemStack combSawdust = getItemStack("honey_comb", 1, 80);
		ItemStack combCertus = getItemStack("honey_comb", 1, 81);
		ItemStack combEnderPearl = getItemStack("honey_comb", 1, 82);
		ItemStack combYellorium = getItemStack("honey_comb", 1, 83);
		ItemStack combCyanite = getItemStack("honey_comb", 1, 84);
		ItemStack combBlutonium = getItemStack("honey_comb", 1, 85);

		ItemStack dropEnergy = getItemStack("honey_drop", 1, 0);
		ItemStack dropAcid = getItemStack("honey_drop", 1, 1);
		ItemStack dropPoison = getItemStack("honey_drop", 1, 2);
		ItemStack dropApple = getItemStack("honey_drop", 1, 3);
		ItemStack dropIce = getItemStack("honey_drop", 1, 5);
		ItemStack dropMilk = getItemStack("honey_drop", 1, 6);
		ItemStack dropSeed = getItemStack("honey_drop", 1, 7);
		ItemStack dropAlcohol = getItemStack("honey_drop", 1, 8);

		ItemStack dustRedstone = new ItemStack(Items.REDSTONE);
		ItemStack dustGlowstone = new ItemStack(Items.GLOWSTONE_DUST);

		ItemStack gemLapis = new ItemStack(Items.DYE, 6, 4);
		ItemStack gemQuartz = new ItemStack(Items.QUARTZ);

		ItemStack grainsIron = getItemStack("misc", 1, 6);
		ItemStack grainsGold = getItemStack("misc", 1, 7);
		ItemStack grainsSilver = getItemStack("misc", 1, 8);
		ItemStack grainsPlatinum = getItemStack("misc", 1, 9);
		ItemStack grainsCopper = getItemStack("misc", 1, 10);
		ItemStack grainsTin = getItemStack("misc", 1, 11);
		ItemStack grainsNickel = getItemStack("misc", 1, 12);
		ItemStack grainsLead = getItemStack("misc", 1, 13);
		ItemStack grainsZinc = getItemStack("misc", 1, 14);
		ItemStack grainsTitanium = getItemStack("misc", 1, 15);
		ItemStack grainsTungsten = getItemStack("misc", 1, 16);
		ItemStack grainsCoal = getItemStack("misc", 1, 18);
		ItemStack grainsYellorium = getItemStack("misc", 1, 27);
		ItemStack grainsCyanite = getItemStack("misc", 1, 28);
		ItemStack grainsBlutonium = getItemStack("misc", 1, 29);

		ItemStack propolisWater = getItemStack("propolis", 1, 0);
		ItemStack propolisOil = getItemStack("propolis", 1, 1);
		ItemStack propolisFuel = getItemStack("propolis", 1, 2);
		ItemStack propolisCreosote = getItemStack("propolis", 1, 7);

		ItemStack shardDiamond = getItemStack("misc", 1, 1);
		ItemStack shardEmerald = getItemStack("misc", 1, 2);
		ItemStack shardRuby = getItemStack("misc", 1, 3);
		ItemStack shardSapphire = getItemStack("misc", 1, 4);

		ItemStack[] tintedCombs = new ItemStack[16];
		ItemStack[] tintedDrops = new ItemStack[16];
		ItemStack[] dye = new ItemStack[16];

		int tintedCombStart = 57;
		int tintedDropStart = 13;

		for (int i = 0; i < 16; i++) {
			tintedCombs[i] = getItemStack("honey_comb", 1, i + tintedCombStart);
			tintedDrops[i] = getItemStack("honey_drop", 1, i + tintedDropStart);
		}
		dye[0] = getItemStack("misc", 1, 19);
		dye[1] = getItemStack("misc", 1, 20);
		dye[2] = getItemStack("misc", 1, 21);
		dye[3] = getItemStack("misc", 1, 22);
		dye[4] = getItemStack("misc", 1, 24);
		dye[5] = getItemStack("misc", 1, 23);
		dye[6] = getItemStack("misc", 1, 25);
		dye[7] = new ItemStack(Items.DYE, 1, 14);
		dye[8] = new ItemStack(Items.DYE, 1, 6);
		dye[9] = new ItemStack(Items.DYE, 1, 5);
		dye[10] = new ItemStack(Items.DYE, 1, 8);
		dye[11] = new ItemStack(Items.DYE, 1, 12);
		dye[12] = new ItemStack(Items.DYE, 1, 9);
		dye[13] = new ItemStack(Items.DYE, 1, 10);
		dye[14] = new ItemStack(Items.DYE, 1, 13);
		dye[15] = new ItemStack(Items.DYE, 1, 7);

		Fluid honey = FluidRegistry.getFluid("for.honey");

		/* TRANSPOSER */
		{
			int energy = 4000;

			if (honey != null) {
				for (int i = 0; i < 16; i++) {
					TransposerManager.addExtractRecipe(energy, tintedDrops[i], dye[i], new FluidStack(honey, 200), 100, false);
				}
			}
		}

		/* CENTRIFUGE */
		{
			int energy = CentrifugeManager.DEFAULT_ENERGY;

			CentrifugeManager.addRecipe(energy, combBarren, asList(dropHoney, wax), asList(50, 100), null);
			CentrifugeManager.addRecipe(energy, combRotten, asList(new ItemStack(Items.ROTTEN_FLESH), dropHoney, wax), asList(80, 20, 20), null);
			CentrifugeManager.addRecipe(energy, combBone, asList(new ItemStack(Items.DYE, 1, 15), dropHoney, wax), asList(80, 20, 20), null);
			CentrifugeManager.addRecipe(energy, combOil, asList(propolisOil, dropHoney), asList(100, 90), null);
			CentrifugeManager.addRecipe(energy, combCoal, asList(grainsCoal, dropHoney, wax), asList(100, 75, 80), null);
			CentrifugeManager.addRecipe(energy, combFuel, asList(propolisFuel, dropHoney), asList(100, 90), null);
			CentrifugeManager.addRecipe(energy, combWater, asList(propolisWater, dropHoney), asList(100, 90), null);
			CentrifugeManager.addRecipe(energy, combMilk, asList(dropMilk, dropHoney), asList(100, 90), null);
			CentrifugeManager.addRecipe(energy, combFruit, asList(dropApple, dropHoney), asList(100, 90), null);
			CentrifugeManager.addRecipe(energy, combSeed, asList(dropSeed, dropHoney), asList(100, 90), null);
			CentrifugeManager.addRecipe(energy, combAlcohol, asList(dropAlcohol, dropHoney), asList(100, 90), null);
			CentrifugeManager.addRecipe(energy, combStone, asList(dropHoney, wax), asList(25, 50), null);
			CentrifugeManager.addRecipe(energy, combRedstone, asList(dustRedstone, dropHoney, wax), asList(100, 50, 80), null);
			// CentrifugeManager.addRecipe(energy, combResin, asList(propolisWater, dropHoney), asList(100, 90), null);
			CentrifugeManager.addRecipe(energy, combIC2Energy, asList(dropEnergy, dustRedstone, wax), asList(100, 75, 80), null);
			CentrifugeManager.addRecipe(energy, combIron, asList(grainsIron, dropHoney, wax), asList(100, 25, 50), null);
			CentrifugeManager.addRecipe(energy, combGold, asList(grainsGold, dropHoney, wax), asList(100, 25, 50), null);
			CentrifugeManager.addRecipe(energy, combCopper, asList(grainsCopper, dropHoney, wax), asList(100, 25, 50), null);
			CentrifugeManager.addRecipe(energy, combTin, asList(grainsTin, dropHoney, wax), asList(100, 25, 50), null);
			CentrifugeManager.addRecipe(energy, combSilver, asList(grainsSilver, dropHoney, wax), asList(100, 25, 50), null);
			// Bronze
			CentrifugeManager.addRecipe(energy, combUranium, asList(ItemHelper.getOre("crushedUranium"), dropHoney, wax), asList(50, 25, 50), null);
			CentrifugeManager.addRecipe(energy, combClay, asList(new ItemStack(Items.CLAY_BALL), dropHoney, wax), asList(80, 80, 25), null);
			CentrifugeManager.addRecipe(energy, combOld, asList(dropHoney, wax), asList(90, 100), null);
			CentrifugeManager.addRecipe(energy, combFungal, asList(new ItemStack(Blocks.BROWN_MUSHROOM_BLOCK), new ItemStack(Blocks.RED_MUSHROOM_BLOCK), wax), asList(100, 75, 90), null);
			CentrifugeManager.addRecipe(energy, combCreosote, asList(propolisCreosote, dropHoney), asList(70, 50), null);
			CentrifugeManager.addRecipe(energy, combLatex, asList(ItemHelper.getOre("itemRubber"), dropHoney, wax), asList(100, 50, 85), null);
			CentrifugeManager.addRecipe(energy, combAcidic, asList(ItemMaterial.dustSulfur, dropAcid, wax), asList(75, 50, 80), null);
			CentrifugeManager.addRecipe(energy, combVenomous, asList(dropPoison, dropHoney), asList(80, 80), null);
			CentrifugeManager.addRecipe(energy, combSlime, asList(new ItemStack(Items.SLIME_BALL), dropHoney, wax), asList(75, 75, 100), null);
			CentrifugeManager.addRecipe(energy, combBlaze, asList(new ItemStack(Items.BLAZE_POWDER), wax), asList(100, 75), null);
			// CentrifugeManager.addRecipe(energy, combCoffee, asList(propolisWater, dropHoney), asList(100, 90), null);
			CentrifugeManager.addRecipe(energy, combGlacial, asList(dropIce, dropHoney), asList(80, 75), null);
			// Mint
			// Citrus
			// Peat
			CentrifugeManager.addRecipe(energy, combShadow, asList(ItemMaterial.dustObsidian, dropHoney), asList(75, 50), null);
			CentrifugeManager.addRecipe(energy, combLead, asList(grainsLead, dropHoney, wax), asList(100, 25, 50), null);
			// Brass
			// Electrum
			CentrifugeManager.addRecipe(energy, combZinc, asList(grainsZinc, dropHoney, wax), asList(100, 25, 50), null);
			CentrifugeManager.addRecipe(energy, combTitanium, asList(grainsTitanium, dropHoney, wax), asList(100, 25, 50), null);
			CentrifugeManager.addRecipe(energy, combTungsten, asList(grainsTungsten, dropHoney, wax), asList(100, 25, 50), null);
			// Steel
			// Iridium
			CentrifugeManager.addRecipe(energy, combPlatinum, asList(grainsPlatinum, dropHoney, wax), asList(100, 25, 50), null);
			CentrifugeManager.addRecipe(energy, combLapis, asList(gemLapis, dropHoney, wax), asList(100, 25, 50), null);
			CentrifugeManager.addRecipe(energy, combSodalite, asList(ItemHelper.getOre("dustSmallSodalite"), ItemHelper.getOre("dustSmallAluminum"), dropHoney, wax), asList(100, 100, 25, 50), null);
			CentrifugeManager.addRecipe(energy, combPyrite, asList(ItemHelper.getOre("dustSmallPyrite"), ItemHelper.getOre("dustSmallIron"), dropHoney, wax), asList(100, 100, 25, 50), null);
			CentrifugeManager.addRecipe(energy, combBauxite, asList(ItemHelper.getOre("dustSmallBauxite"), ItemHelper.getOre("dustSmallAluminum"), dropHoney, wax), asList(100, 100, 25, 50), null);
			CentrifugeManager.addRecipe(energy, combCinnabar, asList(ItemHelper.getOre("dustSmallCinnabar"), dustRedstone, dropHoney, wax), asList(100, 5, 25, 50), null);
			CentrifugeManager.addRecipe(energy, combSphalerite, asList(ItemHelper.getOre("dustSmallSphalerite"), ItemHelper.getOre("dustSmallZinc"), dropHoney, wax), asList(100, 100, 25, 50), null);
			CentrifugeManager.addRecipe(energy, combEmerald, asList(shardEmerald, dropHoney, wax), asList(100, 25, 50), null);
			CentrifugeManager.addRecipe(energy, combRuby, asList(shardRuby, dropHoney, wax), asList(100, 25, 50), null);
			CentrifugeManager.addRecipe(energy, combSapphire, asList(shardSapphire, dropHoney, wax), asList(100, 25, 50), null);
			CentrifugeManager.addRecipe(energy, combDiamond, asList(shardDiamond, dropHoney, wax), asList(100, 25, 50), null);
			CentrifugeManager.addRecipe(energy, combNickel, asList(grainsNickel, dropHoney, wax), asList(100, 25, 50), null);
			// Invar
			CentrifugeManager.addRecipe(energy, combGlowstone, asList(dustGlowstone, dropHoney), asList(100, 25), null);
			CentrifugeManager.addRecipe(energy, combSaltpeter, asList(ItemMaterial.dustNiter, dropHoney), asList(100, 25), null);
			// Pulp
			// Mulch
			CentrifugeManager.addRecipe(energy, combCompost, asList(compost, dropHoney), asList(100, 25), null);
			CentrifugeManager.addRecipe(energy, combSawdust, asList(ItemMaterial.dustWood, dropHoney), asList(100, 25), null);
			CentrifugeManager.addRecipe(energy, combCertus, asList(ItemHelper.getOre("dustCertusQuartz"), gemQuartz, dropHoney), asList(20, 25, 25), null);
			CentrifugeManager.addRecipe(energy, combEnderPearl, asList(ItemHelper.getOre("dustEnderPearl"), dropHoney), asList(25, 25), null);
			CentrifugeManager.addRecipe(energy, combYellorium, asList(grainsYellorium, dropHoney, wax), asList(100, 25, 50), null);
			CentrifugeManager.addRecipe(energy, combCyanite, asList(grainsCyanite, dropHoney, wax), asList(100, 25, 50), null);
			CentrifugeManager.addRecipe(energy, combBlutonium, asList(grainsBlutonium, dropHoney, wax), asList(100, 25, 50), null);

			for (int i = 0; i < 16; i++) {
				CentrifugeManager.addRecipe(energy, tintedCombs[i], asList(tintedDrops[i], dropHoney, wax), asList(100, 80, 80), null);
			}
		}
	}

}
