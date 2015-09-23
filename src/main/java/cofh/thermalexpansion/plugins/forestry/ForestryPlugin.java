package cofh.thermalexpansion.plugins.forestry;

import cofh.asm.relauncher.Strippable;
import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.util.crafting.TransposerManager;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class ForestryPlugin {

	public static void preInit() {

	}

	public static void initialize() {

	}

	public static void postInit() {

	}

	@Strippable("mod:Forestry")
	public static void loadComplete() {

		ItemStack honeydew = ItemHelper.cloneStack(GameRegistry.findItem("Forestry", "honeydew"), 1);
		ItemStack honeyDrop = ItemHelper.cloneStack(GameRegistry.findItem("Forestry", "honeyDrop"), 1);
		ItemStack propolis = ItemHelper.cloneStack(GameRegistry.findItem("Forestry", "propolis"), 1);
		ItemStack mulch = ItemHelper.cloneStack(GameRegistry.findItem("Forestry", "mulch"), 1);

		Fluid honey = FluidRegistry.getFluid("for.honey");
		Fluid juice = FluidRegistry.getFluid("juice");
		Fluid seedoil = FluidRegistry.getFluid("seedoil");

		if (honey != null) {
			TransposerManager.addExtractionRecipe(4800, honeydew, null, new FluidStack(honey, 100), 0, false);
			TransposerManager.addExtractionRecipe(4800, honeyDrop, propolis, new FluidStack(honey, 100), 5, false);
		}
		if (juice != null) {
			/* FORESTRY */
			TransposerManager.addExtractionRecipe(2400, ItemHelper.cloneStack(Items.apple, 1), mulch, new FluidStack(juice, 200), 20, false);
			TransposerManager.addExtractionRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropApple"), 1), mulch, new FluidStack(juice, 200), 20, false);
			TransposerManager.addExtractionRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropDate"), 1), mulch, new FluidStack(juice, 50), 50, false);
			TransposerManager.addExtractionRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropLemon"), 1), mulch, new FluidStack(juice, 400), 10, false);
			TransposerManager
					.addExtractionRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropPapaya"), 1), mulch, new FluidStack(juice, 600), 10, false);
			TransposerManager.addExtractionRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropPlum"), 1), mulch, new FluidStack(juice, 100), 60, false);

			/* BINNIE */
			TransposerManager.addExtractionRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropApricot"), 1), mulch, new FluidStack(juice, 150), 40,
					false);
			TransposerManager
					.addExtractionRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropBanana"), 1), mulch, new FluidStack(juice, 100), 30, false);
			TransposerManager.addExtractionRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropBlackthorn"), 1), mulch, new FluidStack(juice, 50), 5,
					false);
			TransposerManager.addExtractionRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropBuddhaHand"), 1), mulch, new FluidStack(juice, 400), 15,
					false);
			TransposerManager.addExtractionRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropCandlenut"), 1), mulch, new FluidStack(juice, 50), 10,
					false);
			TransposerManager
					.addExtractionRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropCashew"), 1), mulch, new FluidStack(juice, 150), 15, false);
			TransposerManager.addExtractionRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropCherryPlum"), 1), mulch, new FluidStack(juice, 100), 60,
					false);
			TransposerManager
					.addExtractionRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropCitron"), 1), mulch, new FluidStack(juice, 400), 15, false);
			TransposerManager.addExtractionRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropCrabapple"), 1), mulch, new FluidStack(juice, 150), 10,
					false);
			TransposerManager.addExtractionRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropElderberry"), 1), mulch, new FluidStack(juice, 100), 5,
					false);
			TransposerManager.addExtractionRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropFingerLime"), 1), mulch, new FluidStack(juice, 300), 10,
					false);
			TransposerManager.addExtractionRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropGrapefruit"), 1), mulch, new FluidStack(juice, 500), 15,
					false);
			TransposerManager.addExtractionRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropKeyLime"), 1), mulch, new FluidStack(juice, 300), 10,
					false);
			TransposerManager.addExtractionRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropKumquat"), 1), mulch, new FluidStack(juice, 300), 10,
					false);
			TransposerManager.addExtractionRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropLime"), 1), mulch, new FluidStack(juice, 300), 10, false);
			TransposerManager.addExtractionRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropManderin"), 1), mulch, new FluidStack(juice, 400), 10,
					false);
			TransposerManager.addExtractionRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropMango"), 1), mulch, new FluidStack(juice, 400), 20, false);
			TransposerManager.addExtractionRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropNectarine"), 1), mulch, new FluidStack(juice, 150), 40,
					false);
			TransposerManager
					.addExtractionRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropOrange"), 1), mulch, new FluidStack(juice, 400), 15, false);
			TransposerManager.addExtractionRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropOsangeOrange"), 1), mulch, new FluidStack(juice, 300), 15,
					false);
			TransposerManager.addExtractionRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropPeach"), 1), mulch, new FluidStack(juice, 150), 40, false);
			TransposerManager.addExtractionRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropPear"), 1), mulch, new FluidStack(juice, 300), 20, false);
			TransposerManager.addExtractionRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropPlantain"), 1), mulch, new FluidStack(juice, 100), 40,
					false);
			TransposerManager
					.addExtractionRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropPomelo"), 1), mulch, new FluidStack(juice, 300), 10, false);
			TransposerManager.addExtractionRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropRedBanana"), 1), mulch, new FluidStack(juice, 100), 30,
					false);
			TransposerManager.addExtractionRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropSandPear"), 1), mulch, new FluidStack(juice, 200), 10,
					false);
			TransposerManager.addExtractionRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropSatsuma"), 1), mulch, new FluidStack(juice, 300), 10,
					false);
			TransposerManager.addExtractionRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropStarfruit"), 1), mulch, new FluidStack(juice, 300), 10,
					false);
			TransposerManager.addExtractionRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropTangerine"), 1), mulch, new FluidStack(juice, 300), 10,
					false);
		}
		if (seedoil != null) {
			/* FORESTRY */
			TransposerManager.addExtractionRecipe(2400, ItemHelper.cloneStack(Items.wheat_seeds, 1), null, new FluidStack(seedoil, 10), 0, false);
			TransposerManager.addExtractionRecipe(2400, ItemHelper.cloneStack(Items.pumpkin_seeds, 1), null, new FluidStack(seedoil, 10), 0, false);
			TransposerManager.addExtractionRecipe(2400, ItemHelper.cloneStack(Items.melon_seeds, 1), null, new FluidStack(seedoil, 10), 0, false);
			TransposerManager
					.addExtractionRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropCherry"), 1), mulch, new FluidStack(seedoil, 50), 5, false);
			TransposerManager.addExtractionRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropChestnut"), 1), mulch, new FluidStack(seedoil, 220), 2,
					false);
			TransposerManager.addExtractionRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropWalnut"), 1), mulch, new FluidStack(seedoil, 180), 5,
					false);

			/* BINNIE */
			TransposerManager.addExtractionRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropAcorn"), 1), mulch, new FluidStack(seedoil, 50), 3, false);
			TransposerManager
					.addExtractionRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropAlmond"), 1), mulch, new FluidStack(seedoil, 80), 5, false);
			TransposerManager.addExtractionRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropAvocado"), 1), mulch, new FluidStack(seedoil, 300), 15,
					false);
			TransposerManager.addExtractionRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropBlackCherry"), 1), mulch, new FluidStack(seedoil, 50), 5,
					false);
			TransposerManager.addExtractionRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropBeechnut"), 1), mulch, new FluidStack(seedoil, 100), 4,
					false);
			TransposerManager.addExtractionRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropBrazilNut"), 1), mulch, new FluidStack(seedoil, 20), 2,
					false);
			TransposerManager.addExtractionRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropButternut"), 1), mulch, new FluidStack(seedoil, 180), 5,
					false);
			TransposerManager.addExtractionRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropClove"), 1), mulch, new FluidStack(seedoil, 25), 2, false);
			TransposerManager.addExtractionRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropCoconut"), 1), mulch, new FluidStack(seedoil, 300), 25,
					false);
			TransposerManager
					.addExtractionRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropCoffee"), 1), mulch, new FluidStack(seedoil, 20), 2, false);
			TransposerManager.addExtractionRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropFig"), 1), mulch, new FluidStack(seedoil, 50), 3, false);
			TransposerManager.addExtractionRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropGingkoNut"), 1), mulch, new FluidStack(seedoil, 50), 5,
					false);
			TransposerManager.addExtractionRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropHazelnut"), 1), mulch, new FluidStack(seedoil, 150), 5,
					false);
			TransposerManager.addExtractionRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropNutmeg"), 1), mulch, new FluidStack(seedoil, 50), 10,
					false);
			TransposerManager.addExtractionRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropOlive"), 1), mulch, new FluidStack(seedoil, 50), 3, false);
			TransposerManager.addExtractionRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropPecan"), 1), mulch, new FluidStack(seedoil, 50), 2, false);
			TransposerManager.addExtractionRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropSourCherry"), 1), mulch, new FluidStack(seedoil, 50), 3,
					false);
			TransposerManager.addExtractionRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropWildCherry"), 1), mulch, new FluidStack(seedoil, 50), 5,
					false);
		}
		ThermalExpansion.log.info("Thermal Expansion: Forestry Plugin Enabled.");
	}

}
