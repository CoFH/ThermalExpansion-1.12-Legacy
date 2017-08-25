package cofh.thermalexpansion.plugins.forestry;

import cofh.core.util.ModPlugin;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.util.managers.machine.CentrifugeManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;

import java.util.Arrays;

public class PluginMagicBees extends ModPlugin {

	public static final String PARENT_ID = PluginForestry.MOD_ID;
	public static final String MOD_ID = "magicbees";
	public static final String MOD_NAME = "Magic Bees";

	public PluginMagicBees() {

		super(MOD_ID, MOD_NAME);
	}

	/* IInitializer */
	@Override
	public boolean initialize() {

		String category = "Plugins";
		String comment = "If TRUE, support for " + MOD_NAME + " is enabled.";
		enable = Loader.isModLoaded(PARENT_ID) && Loader.isModLoaded(MOD_ID) && ThermalExpansion.CONFIG.getConfiguration().getBoolean(MOD_NAME, category, true, comment);

		if (!enable) {
			return false;
		}
		return !error;
	}

	@Override
	public boolean register() {

		if (!enable) {
			return false;
		}
		try {
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

				CentrifugeManager.addRecipe(energy, combMundane, Arrays.asList(wax, dropHoney, waxMagic), Arrays.asList(90, 60, 10), null);
				CentrifugeManager.addRecipe(energy, combMolten, Arrays.asList(waxRefractory, dropHoney), Arrays.asList(86, 8), null);
				CentrifugeManager.addRecipe(energy, combOccult, Arrays.asList(waxMagic, dropHoney), Arrays.asList(100, 60), null);
				CentrifugeManager.addRecipe(energy, combOtherworldy, Arrays.asList(wax, dropHoney, waxMagic), Arrays.asList(50, 100, 20), null);
				CentrifugeManager.addRecipe(energy, combTransmuting, Arrays.asList(wax, propolisUnstable, waxMagic), Arrays.asList(80, 15, 80), null);
				CentrifugeManager.addRecipe(energy, combPapery, Arrays.asList(wax, new ItemStack(Items.PAPER), waxMagic), Arrays.asList(80, 5, 20), null);
				CentrifugeManager.addRecipe(energy, combSoul, Arrays.asList(waxSoul, honeydew), Arrays.asList(95, 26), null);
				CentrifugeManager.addRecipe(energy, combFurtive, Arrays.asList(wax, honeydew, propolis), Arrays.asList(90, 35, 20), null);
				CentrifugeManager.addRecipe(energy, combMemory, Arrays.asList(waxMagic, honeydew, dropIntellect), Arrays.asList(90, 40, 10), null);
				CentrifugeManager.addRecipe(energy, combTemporal, Arrays.asList(waxMagic, honeydew, pollenPhased), Arrays.asList(100, 60, 5), null);
				CentrifugeManager.addRecipe(energy, combForgotten, Arrays.asList(waxAmnesic, dropHoney, propolisPulsating), Arrays.asList(50, 40, 50), null);
				CentrifugeManager.addRecipe(energy, combWindy, Arrays.asList(waxMagic, new ItemStack(Items.FEATHER)), Arrays.asList(100, 60), null);
				CentrifugeManager.addRecipe(energy, combFirey, Arrays.asList(waxMagic, new ItemStack(Items.BLAZE_POWDER)), Arrays.asList(100, 60), null);
				CentrifugeManager.addRecipe(energy, combWatery, Arrays.asList(waxMagic, new ItemStack(Items.DYE, 1, 0)), Arrays.asList(100, 60), null);
				CentrifugeManager.addRecipe(energy, combEarthy, Arrays.asList(waxMagic, new ItemStack(Items.CLAY_BALL)), Arrays.asList(100, 60), null);
			}
		} catch (Throwable t) {
			ThermalExpansion.LOG.error("Thermal Expansion: " + MOD_NAME + " Plugin encountered an error:", t);
			error = true;
		}
		if (!error) {
			ThermalExpansion.LOG.info("Thermal Expansion: " + MOD_NAME + " Plugin Enabled.");
		}
		return !error;
	}

}
