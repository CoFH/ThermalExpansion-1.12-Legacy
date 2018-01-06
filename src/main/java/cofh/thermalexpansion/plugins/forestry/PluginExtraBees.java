package cofh.thermalexpansion.plugins.forestry;

import cofh.core.util.ModPlugin;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.util.managers.machine.CentrifugeManager;
import cofh.thermalexpansion.util.managers.machine.TransposerManager;
import cofh.thermalfoundation.item.ItemMaterial;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;

import static java.util.Arrays.asList;

public class PluginExtraBees extends ModPlugin {

	public static final String PARENT_ID = PluginForestry.MOD_ID;
	public static final String MOD_ID = "extrabees";
	public static final String MOD_NAME = "Extra Bees";

	public PluginExtraBees() {

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
			ItemStack dropHoney = getItemStack(PARENT_ID, "honey_drop", 1, 0);
			ItemStack wax = getItemStack(PARENT_ID, "beeswax", 1, 0);
			ItemStack compost = getItemStack(PARENT_ID, "fertilizer_bio", 1, 0);

			ItemStack combIron = getItemStack("honey_comb", 1, 15);
			ItemStack combGold = getItemStack("honey_comb", 1, 16);
			ItemStack combCopper = getItemStack("honey_comb", 1, 17);
			ItemStack combTin = getItemStack("honey_comb", 1, 18);
			ItemStack combSilver = getItemStack("honey_comb", 1, 19);
			ItemStack combLead = getItemStack("honey_comb", 1, 37);
			ItemStack combNickel = getItemStack("honey_comb", 1, 73);
			ItemStack combPlatinum = getItemStack("honey_comb", 1, 45);

			ItemStack combLapis = getItemStack("honey_comb", 1, 46);
			ItemStack combEmerald = getItemStack("honey_comb", 1, 52);
			ItemStack combDiamond = getItemStack("honey_comb", 1, 56);

			ItemStack combClay = getItemStack("honey_comb", 1, 22);
			ItemStack combRedstone = getItemStack("honey_comb", 1, 12);
			ItemStack combGlowstone = getItemStack("honey_comb", 1, 75);
			ItemStack combCoal = getItemStack("honey_comb", 1, 4);
			ItemStack combObsidian = getItemStack("honey_comb", 1, 36);
			ItemStack combSulfur = getItemStack("honey_comb", 1, 27);
			ItemStack combNiter = getItemStack("honey_comb", 1, 76);

			ItemStack combMushroom = getItemStack("honey_comb", 1, 24);
			ItemStack combSlime = getItemStack("honey_comb", 1, 29);
			ItemStack combBlaze = getItemStack("honey_comb", 1, 30);

			ItemStack combBarren = getItemStack("honey_comb", 1, 0);
			ItemStack combRotten = getItemStack("honey_comb", 1, 1);
			ItemStack combBone = getItemStack("honey_comb", 1, 2);
			ItemStack combWater = getItemStack("honey_comb", 1, 6);
			ItemStack combRocky = getItemStack("honey_comb", 1, 11);
			ItemStack combOld = getItemStack("honey_comb", 1, 23);
			ItemStack combTar = getItemStack("honey_comb", 1, 25);

			ItemStack combCompost = getItemStack("honey_comb", 1, 79);

			ItemStack combUranium = getItemStack("honey_comb", 1, 21);
			ItemStack combRubber = getItemStack("honey_comb", 1, 26);

			ItemStack dropAcid = getItemStack("honey_drop", 1, 1);

			ItemStack propolisWater = getItemStack("propolis", 1, 0);
			ItemStack propolisCreosote = getItemStack("propolis", 1, 7);

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

				CentrifugeManager.addRecipe(energy, combIron, asList(ItemMaterial.dustIron, dropHoney, wax), asList(25, 25, 50), null);
				CentrifugeManager.addRecipe(energy, combGold, asList(ItemMaterial.dustGold, dropHoney, wax), asList(25, 25, 50), null);
				CentrifugeManager.addRecipe(energy, combCopper, asList(ItemMaterial.dustCopper, dropHoney, wax), asList(25, 25, 50), null);
				CentrifugeManager.addRecipe(energy, combTin, asList(ItemMaterial.dustTin, dropHoney, wax), asList(25, 25, 50), null);
				CentrifugeManager.addRecipe(energy, combSilver, asList(ItemMaterial.dustSilver, dropHoney, wax), asList(25, 25, 50), null);
				CentrifugeManager.addRecipe(energy, combLead, asList(ItemMaterial.dustLead, dropHoney, wax), asList(25, 25, 50), null);
				CentrifugeManager.addRecipe(energy, combNickel, asList(ItemMaterial.dustNickel, dropHoney, wax), asList(25, 25, 50), null);
				CentrifugeManager.addRecipe(energy, combPlatinum, asList(ItemMaterial.dustPlatinum, dropHoney, wax), asList(25, 25, 50), null);

				CentrifugeManager.addRecipe(energy, combLapis, asList(new ItemStack(Items.DYE, 6, 4), dropHoney, wax), asList(100, 25, 50), null);
				CentrifugeManager.addRecipe(energy, combEmerald, asList(ItemMaterial.nuggetEmerald, dropHoney, wax), asList(100, 25, 50), null);
				CentrifugeManager.addRecipe(energy, combDiamond, asList(ItemMaterial.nuggetDiamond, dropHoney, wax), asList(100, 25, 50), null);

				CentrifugeManager.addRecipe(energy, combClay, asList(new ItemStack(Items.CLAY_BALL), dropHoney, wax), asList(80, 80, 25), null);
				CentrifugeManager.addRecipe(energy, combRedstone, asList(new ItemStack(Items.REDSTONE), dropHoney, wax), asList(100, 50, 80), null);
				CentrifugeManager.addRecipe(energy, combGlowstone, asList(new ItemStack(Items.GLOWSTONE_DUST), dropHoney), asList(100, 25), null);
				CentrifugeManager.addRecipe(energy, combCoal, asList(ItemMaterial.dustCoal, dropHoney, wax), asList(25, 75, 80), null);
				CentrifugeManager.addRecipe(energy, combObsidian, asList(ItemMaterial.dustObsidian, dropHoney), asList(75, 50), null);
				CentrifugeManager.addRecipe(energy, combSulfur, asList(ItemMaterial.dustSulfur, dropAcid, wax), asList(75, 50, 80), null);
				CentrifugeManager.addRecipe(energy, combNiter, asList(ItemMaterial.dustNiter, dropHoney), asList(100, 25), null);

				CentrifugeManager.addRecipe(energy, combMushroom, asList(new ItemStack(Blocks.BROWN_MUSHROOM_BLOCK), new ItemStack(Blocks.RED_MUSHROOM_BLOCK), wax), asList(100, 75, 90), null);
				CentrifugeManager.addRecipe(energy, combSlime, asList(new ItemStack(Items.SLIME_BALL), dropHoney, wax), asList(75, 75, 100), null);
				CentrifugeManager.addRecipe(energy, combBlaze, asList(new ItemStack(Items.BLAZE_POWDER), wax), asList(100, 75), null);

				CentrifugeManager.addRecipe(energy, combBarren, asList(dropHoney, wax), asList(50, 100), null);
				CentrifugeManager.addRecipe(energy, combRotten, asList(new ItemStack(Items.ROTTEN_FLESH), dropHoney, wax), asList(80, 20, 20), null);
				CentrifugeManager.addRecipe(energy, combBone, asList(new ItemStack(Items.DYE, 1, 15), dropHoney, wax), asList(80, 20, 20), null);
				CentrifugeManager.addRecipe(energy, combWater, asList(propolisWater, dropHoney), asList(100, 90), null);
				CentrifugeManager.addRecipe(energy, combRocky, asList(dropHoney, wax), asList(25, 50), null);
				CentrifugeManager.addRecipe(energy, combOld, asList(dropHoney, wax), asList(90, 100), null);
				CentrifugeManager.addRecipe(energy, combTar, asList(propolisCreosote, dropHoney), asList(70, 50), null);

				CentrifugeManager.addRecipe(energy, combCompost, asList(compost, dropHoney), asList(100, 25), null);

				for (int i = 0; i < 16; i++) {
					CentrifugeManager.addRecipe(energy, tintedCombs[i], asList(tintedDrops[i], dropHoney, wax), asList(100, 80, 80), null);
				}
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
