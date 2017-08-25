package cofh.thermalexpansion.plugins.forestry;

import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.util.managers.machine.CentrifugeManager;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.Arrays;

public class MagicBeesPlugin {

	private MagicBeesPlugin() {

	}

	public static final String MOD_ID = "magicbees";
	public static final String MOD_NAME = "Magic Bees";

	public static void initialize() {

		String category = "Plugins";
		String comment = "If TRUE, support for " + MOD_NAME + " is enabled.";

		boolean enable = ThermalExpansion.CONFIG.getConfiguration().getBoolean(MOD_NAME, category, true, comment);

		if (!enable || !Loader.isModLoaded(MOD_ID)) {
			return;
		}
		try {
			ItemStack honeydew = getItem(ForestryPlugin.MOD_ID, "honeydew", 1, 0);
			ItemStack dropHoney = getItem(ForestryPlugin.MOD_ID, "honey_drop", 1, 0);
			ItemStack propolis = getItem(ForestryPlugin.MOD_ID, "propolis", 1, 0);
			ItemStack propolisPulsating = getItem(ForestryPlugin.MOD_ID, "propolis", 1, 2);
			ItemStack wax = getItem(ForestryPlugin.MOD_ID, "beeswax", 1, 0);
			ItemStack waxRefractory = getItem(ForestryPlugin.MOD_ID, "refractory_wax", 1, 0);

			ItemStack combMundane = getItem("beecomb", 1, 0);
			ItemStack combMolten = getItem("beecomb", 1, 1);
			ItemStack combOccult = getItem("beecomb", 1, 2);
			ItemStack combOtherworldy = getItem("beecomb", 1, 3);
			ItemStack combTransmuting = getItem("beecomb", 1, 4);
			ItemStack combPapery = getItem("beecomb", 1, 5);
			ItemStack combSoul = getItem("beecomb", 1, 6);
			ItemStack combFurtive = getItem("beecomb", 1, 7);
			ItemStack combMemory = getItem("beecomb", 1, 8);
			ItemStack combTemporal = getItem("beecomb", 1, 9);
			ItemStack combForgotten = getItem("beecomb", 1, 10);
			ItemStack combWindy = getItem("beecomb", 1, 11);
			ItemStack combFirey = getItem("beecomb", 1, 12);
			ItemStack combWatery = getItem("beecomb", 1, 13);
			ItemStack combEarthy = getItem("beecomb", 1, 14);

			ItemStack dropIntellect = getItem("drop", 1, 1);

			ItemStack pollenPhased = getItem("pollen", 1, 1);

			ItemStack propolisUnstable = getItem("propolis", 1, 0);

			ItemStack waxMagic = getItem("wax", 1, 0);
			ItemStack waxSoul = getItem("wax", 1, 1);
			ItemStack waxAmnesic = getItem("wax", 1, 2);

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

			ThermalExpansion.LOG.info("Thermal Expansion: " + MOD_NAME + " Plugin Enabled.");
		} catch (Throwable t) {
			ThermalExpansion.LOG.error("Thermal Expansion: " + MOD_NAME + " Plugin encountered an error:", t);
		}
	}

	/* HELPERS */
	private static ItemStack getBlockStack(String name, int amount, int meta) {

		Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(MOD_ID + ":" + name));
		return block != null ? new ItemStack(block, amount, meta) : ItemStack.EMPTY;
	}

	private static ItemStack getBlockStack(String name, int amount) {

		return getBlockStack(name, amount, 0);
	}

	private static ItemStack getItem(String modid, String name, int amount, int meta) {

		Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(modid + ":" + name));
		return item != null ? new ItemStack(item, amount, meta) : ItemStack.EMPTY;
	}

	private static ItemStack getItem(String name, int amount, int meta) {

		return getItem(MOD_ID, name, amount, meta);
	}

	private static ItemStack getItem(String name) {

		return getItem(name, 1, 0);
	}

}
