package cofh.thermalexpansion.plugins.forestry;

import cofh.thermalexpansion.plugins.PluginTEBase;
import cofh.thermalexpansion.util.managers.machine.CentrifugeManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import static java.util.Arrays.asList;

public class PluginMagicBees extends PluginTEBase {

	public static final String PARENT_ID = PluginForestry.MOD_ID;
	public static final String MOD_ID = "magicbees";
	public static final String MOD_NAME = "Magic Bees";

	public PluginMagicBees() {

		super(MOD_ID, MOD_NAME);
	}

	@Override
	public void registerDelegate() {

		ItemStack honeydew = getItemStack(PARENT_ID, "honeydew", 1, 0);
		ItemStack dropHoney = getItemStack(PARENT_ID, "honey_drop", 1, 0);
		ItemStack propolis = getItemStack(PARENT_ID, "propolis", 1, 0);
		ItemStack propolisPulsating = getItemStack(PARENT_ID, "propolis", 1, 2);
		ItemStack wax = getItemStack(PARENT_ID, "beeswax", 1, 0);
		ItemStack waxRefractory = getItemStack(PARENT_ID, "refractory_wax", 1, 0);

		ItemStack combMundane = getItemStack("beecomb", 1, 0);
		ItemStack combMolten = getItemStack("beecomb", 1, 1);
		ItemStack combOccult = getItemStack("beecomb", 1, 2);
		ItemStack combOtherworldy = getItemStack("beecomb", 1, 3);
		ItemStack combTransmuting = getItemStack("beecomb", 1, 4);
		ItemStack combPapery = getItemStack("beecomb", 1, 5);
		ItemStack combSoul = getItemStack("beecomb", 1, 6);
		ItemStack combFurtive = getItemStack("beecomb", 1, 7);
		ItemStack combMemory = getItemStack("beecomb", 1, 8);
		ItemStack combTemporal = getItemStack("beecomb", 1, 9);
		ItemStack combForgotten = getItemStack("beecomb", 1, 10);
		ItemStack combWindy = getItemStack("beecomb", 1, 11);
		ItemStack combFirey = getItemStack("beecomb", 1, 12);
		ItemStack combWatery = getItemStack("beecomb", 1, 13);
		ItemStack combEarthy = getItemStack("beecomb", 1, 14);

		ItemStack dropIntellect = getItemStack("drop", 1, 1);

		ItemStack pollenPhased = getItemStack("pollen", 1, 1);

		ItemStack propolisUnstable = getItemStack("propolis", 1, 0);

		ItemStack waxMagic = getItemStack("wax", 1, 0);
		ItemStack waxSoul = getItemStack("wax", 1, 1);
		ItemStack waxAmnesic = getItemStack("wax", 1, 2);

		/* CENTRIFUGE */
		{
			int energy = CentrifugeManager.DEFAULT_ENERGY;

			CentrifugeManager.addRecipe(energy, combMundane, asList(wax, dropHoney, waxMagic), asList(90, 60, 10), null);
			CentrifugeManager.addRecipe(energy, combMolten, asList(waxRefractory, dropHoney), asList(86, 8), null);
			CentrifugeManager.addRecipe(energy, combOccult, asList(waxMagic, dropHoney), asList(100, 60), null);
			CentrifugeManager.addRecipe(energy, combOtherworldy, asList(wax, dropHoney, waxMagic), asList(50, 100, 20), null);
			CentrifugeManager.addRecipe(energy, combTransmuting, asList(wax, propolisUnstable, waxMagic), asList(80, 15, 80), null);
			CentrifugeManager.addRecipe(energy, combPapery, asList(wax, new ItemStack(Items.PAPER), waxMagic), asList(80, 5, 20), null);
			CentrifugeManager.addRecipe(energy, combSoul, asList(waxSoul, honeydew), asList(95, 26), null);
			CentrifugeManager.addRecipe(energy, combFurtive, asList(wax, honeydew, propolis), asList(90, 35, 20), null);
			CentrifugeManager.addRecipe(energy, combMemory, asList(waxMagic, honeydew, dropIntellect), asList(90, 40, 10), null);
			CentrifugeManager.addRecipe(energy, combTemporal, asList(waxMagic, honeydew, pollenPhased), asList(100, 60, 5), null);
			CentrifugeManager.addRecipe(energy, combForgotten, asList(waxAmnesic, dropHoney, propolisPulsating), asList(50, 40, 50), null);
			CentrifugeManager.addRecipe(energy, combWindy, asList(waxMagic, new ItemStack(Items.FEATHER)), asList(100, 60), null);
			CentrifugeManager.addRecipe(energy, combFirey, asList(waxMagic, new ItemStack(Items.BLAZE_POWDER)), asList(100, 60), null);
			CentrifugeManager.addRecipe(energy, combWatery, asList(waxMagic, new ItemStack(Items.DYE, 1, 0)), asList(100, 60), null);
			CentrifugeManager.addRecipe(energy, combEarthy, asList(waxMagic, new ItemStack(Items.CLAY_BALL)), asList(100, 60), null);
		}
	}

}
