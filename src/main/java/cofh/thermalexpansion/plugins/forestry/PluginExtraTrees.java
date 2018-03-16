package cofh.thermalexpansion.plugins.forestry;

import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.plugins.PluginTEBase;
import cofh.thermalexpansion.util.managers.machine.CentrifugeManager;
import cofh.thermalexpansion.util.managers.machine.RefineryManager;
import cofh.thermalexpansion.util.managers.machine.TransposerManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;

import static java.util.Collections.singletonList;

public class PluginExtraTrees extends PluginTEBase {

	public static final String PARENT_ID = PluginForestry.MOD_ID;
	public static final String MOD_ID = "extratrees";
	public static final String MOD_NAME = "Extra Trees";

	public PluginExtraTrees() {

		super(MOD_ID, MOD_NAME);
	}

	@Override
	public void registerDelegate() {

		ItemStack mulch = getItemStack(PARENT_ID, "mulch", 1, 0);

		Fluid juice = FluidRegistry.getFluid("juice");
		Fluid seed_oil = FluidRegistry.getFluid("seed.oil");

		Fluid juiceApple = FluidRegistry.getFluid("binnie.juice.apple");
		Fluid juiceApricot = FluidRegistry.getFluid("binnie.juice.apricot");
		Fluid juiceBanana = FluidRegistry.getFluid("binnie.juice.banana");
		Fluid juiceCarrot = FluidRegistry.getFluid("binnie.juice.carrot");
		Fluid juiceCherry = FluidRegistry.getFluid("binnie.juice.cherry");
		Fluid juiceCranberry = FluidRegistry.getFluid("binnie.juice.cranberry");
		Fluid juiceElderberry = FluidRegistry.getFluid("binnie.juice.elderberry");
		Fluid juiceGrapefruit = FluidRegistry.getFluid("binnie.juice.grapefruit");
		Fluid juiceLemon = FluidRegistry.getFluid("binnie.juice.lemon");
		Fluid juiceLime = FluidRegistry.getFluid("binnie.juice.lime");
		Fluid juiceOlive = FluidRegistry.getFluid("binnie.juice.olive");
		Fluid juiceOrange = FluidRegistry.getFluid("binnie.juice.orange");
		Fluid juicePeach = FluidRegistry.getFluid("binnie.juice.peach");
		Fluid juicePear = FluidRegistry.getFluid("binnie.juice.pear");
		Fluid juicePineapple = FluidRegistry.getFluid("binnie.juice.pineapple");
		Fluid juicePlum = FluidRegistry.getFluid("binnie.juice.plum");
		Fluid juiceRedGrape = FluidRegistry.getFluid("binnie.juice.red.grape");
		Fluid juiceTomato = FluidRegistry.getFluid("binnie.juice.tomato");
		Fluid juiceWhiteGrape = FluidRegistry.getFluid("binnie.juice.white.grape");

		Fluid ciderApple = FluidRegistry.getFluid("binnie.cider.apple");
		Fluid ciderPeach = FluidRegistry.getFluid("binnie.cider.peach");
		Fluid ciderPear = FluidRegistry.getFluid("binnie.cider.pear");

		if (ciderPear == null) {
			ciderPear = FluidRegistry.getFluid("binnie.ciderpear");         // Mod has a typo.
		}

		Fluid wineApricot = FluidRegistry.getFluid("binnie.wine.apricot");
		Fluid wineBanana = FluidRegistry.getFluid("binnie.wine.banana");
		Fluid wineCarrot = FluidRegistry.getFluid("binnie.wine.carrot");
		Fluid wineCherry = FluidRegistry.getFluid("binnie.wine.cherry");
		Fluid wineCitrus = FluidRegistry.getFluid("binnie.wine.citrus");
		Fluid wineCranberry = FluidRegistry.getFluid("binnie.wine.cranberry");
		Fluid wineElderberry = FluidRegistry.getFluid("binnie.wine.elderberry");
		Fluid winePlum = FluidRegistry.getFluid("binnie.wine.plum");

		Fluid brandyApple = FluidRegistry.getFluid("binnie.brandy.apple");
		Fluid brandyApricot = FluidRegistry.getFluid("binnie.brandy.apricot");
		Fluid brandyCherry = FluidRegistry.getFluid("binnie.brandy.cherry");
		Fluid brandyCitrus = FluidRegistry.getFluid("binnie.brandy.citrus");
		Fluid brandyElderberry = FluidRegistry.getFluid("binnie.brandy.elderberry");
		Fluid brandyFruit = FluidRegistry.getFluid("binnie.brandy.fruit");
		Fluid brandyGrape = FluidRegistry.getFluid("binnie.brandy.grape");
		Fluid brandyPear = FluidRegistry.getFluid("binnie.brandy.pear");
		Fluid brandyPlum = FluidRegistry.getFluid("binnie.brandy.plum");

		Fluid liquorApple = FluidRegistry.getFluid("binnie.liquor.apple");
		Fluid liquorApricot = FluidRegistry.getFluid("binnie.liquor.apricot");
		Fluid liquorCherry = FluidRegistry.getFluid("binnie.liquor.cherry");
		Fluid liquorElderberry = FluidRegistry.getFluid("binnie.liquor.elderberry");
		Fluid liquorFruit = FluidRegistry.getFluid("binnie.liquor.fruit");
		Fluid liquorPear = FluidRegistry.getFluid("binnie.liquor.pear");

		Fluid spiritNeutral = FluidRegistry.getFluid("binnie.spirit.neutral");

		/* TRANSPOSER */
		{
			int energy = 2400;

			if (juice != null) {
				// TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(ItemHelper.getOre("cropCrabapple"), 1), mulch, new FluidStack(juice, 150), 10, false);
				TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(ItemHelper.getOre("cropOrange"), 1), mulch, new FluidStack(juice, 400), 15, false);
				TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(ItemHelper.getOre("cropKumquat"), 1), mulch, new FluidStack(juice, 300), 10, false);
				TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(ItemHelper.getOre("cropLime"), 1), mulch, new FluidStack(juice, 300), 10, false);
				TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(ItemHelper.getOre("cropBlackthorn"), 1), mulch, new FluidStack(juice, 50), 5, false);
				// TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(ItemHelper.getOre("cropCherryPlum"), 1), mulch, new FluidStack(juice, 100), 60, false);
				TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(ItemHelper.getOre("cropApricot"), 1), mulch, new FluidStack(juice, 150), 40, false);
				TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(ItemHelper.getOre("cropGrapefruit"), 1), mulch, new FluidStack(juice, 500), 15, false);
				TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(ItemHelper.getOre("cropPeach"), 1), mulch, new FluidStack(juice, 150), 40, false);
				// TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(ItemHelper.getOre("cropSatsuma"), 1), mulch, new FluidStack(juice, 300), 10, false);
				// TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(ItemHelper.getOre("cropBuddhaHand"), 1), mulch, new FluidStack(juice, 400), 15, false);
				TransposerManager.addExtractRecipe(energy, getItemStack("food", 1, 15), mulch, new FluidStack(juice, 400), 15, false); // Citron
				// TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(ItemHelper.getOre("cropFingerLime"), 1), mulch, new FluidStack(juice, 300), 10, false);
				// TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(ItemHelper.getOre("cropKeyLime"), 1), mulch, new FluidStack(juice, 300), 10, false);
				// TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(ItemHelper.getOre("cropManderin"), 1), mulch, new FluidStack(juice, 400), 10, false);
				// TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(ItemHelper.getOre("cropNectarine"), 1), mulch, new FluidStack(juice, 150), 40, false);
				TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(ItemHelper.getOre("cropPomelo"), 1), mulch, new FluidStack(juice, 300), 10, false);
				// TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(ItemHelper.getOre("cropTangerine"), 1), mulch, new FluidStack(juice, 300), 10, false);
				TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(ItemHelper.getOre("cropPear"), 1), mulch, new FluidStack(juice, 300), 20, false);
				TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(ItemHelper.getOre("cropSandPear"), 1), mulch, new FluidStack(juice, 200), 10, false);
				TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(ItemHelper.getOre("cropPeach"), 1), mulch, new FluidStack(juice, 150), 40, false);
				TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(ItemHelper.getOre("cropBanana"), 1), mulch, new FluidStack(juice, 100), 30, false);
				// TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(ItemHelper.getOre("cropRedBanana"), 1), mulch, new FluidStack(juice, 100), 30, false);
				TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(ItemHelper.getOre("cropPlantain"), 1), mulch, new FluidStack(juice, 100), 40, false);
				TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(ItemHelper.getOre("cropElderberry"), 1), mulch, new FluidStack(juice, 100), 5, false);
				TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(ItemHelper.getOre("cropOsangeOrange"), 1), mulch, new FluidStack(juice, 300), 15, false);
			}

			if (seed_oil != null) {
				// TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(ItemHelper.getOre("cropWildCherry"), 1), mulch, new FluidStack(seed_oil, 50), 5, false);
				// TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(ItemHelper.getOre("cropSourCherry"), 1), mulch, new FluidStack(seed_oil, 50), 3, false);
				// TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(ItemHelper.getOre("cropBlackCherry"), 1), mulch, new FluidStack(seed_oil, 50), 5, false);
				TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(ItemHelper.getOre("cropAlmond"), 1), mulch, new FluidStack(seed_oil, 80), 5, false);
				TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(ItemHelper.getOre("cropHazelnut"), 1), mulch, new FluidStack(seed_oil, 150), 5, false);
				// TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(ItemHelper.getOre("cropButternut"), 1), mulch, new FluidStack(seed_oil, 180), 5, false);
				TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(ItemHelper.getOre("cropBeechnut"), 1), mulch, new FluidStack(seed_oil, 100), 4, false);
				TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(ItemHelper.getOre("cropPecan"), 1), mulch, new FluidStack(seed_oil, 50), 2, false);
				TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(ItemHelper.getOre("cropBrazilNut"), 1), mulch, new FluidStack(seed_oil, 20), 2, false);
				TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(ItemHelper.getOre("cropFig"), 1), mulch, new FluidStack(seed_oil, 50), 3, false);
				TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(ItemHelper.getOre("cropAcorn"), 1), mulch, new FluidStack(seed_oil, 50), 3, false);
				TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(ItemHelper.getOre("cropOlive"), 1), mulch, new FluidStack(seed_oil, 50), 3, false);
				TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(ItemHelper.getOre("cropGingkoNut"), 1), mulch, new FluidStack(seed_oil, 50), 5, false);
				TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(ItemHelper.getOre("cropCoffee"), 1), mulch, new FluidStack(seed_oil, 20), 2, false);
				TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(ItemHelper.getOre("cropClove"), 1), mulch, new FluidStack(seed_oil, 25), 2, false);
			}
		}

		/* REFINERY */
		{
			int energy = RefineryManager.DEFAULT_ENERGY;

			/* NEUTRAL SPIRITS */
			FluidStack spiritNeutralStack = new FluidStack(spiritNeutral, 200);

			RefineryManager.addRecipe(energy, new FluidStack(liquorApple, 500), spiritNeutralStack);
			RefineryManager.addRecipe(energy, new FluidStack(liquorApricot, 500), spiritNeutralStack);
			RefineryManager.addRecipe(energy, new FluidStack(liquorCherry, 500), spiritNeutralStack);
			RefineryManager.addRecipe(energy, new FluidStack(liquorElderberry, 500), spiritNeutralStack);
			RefineryManager.addRecipe(energy, new FluidStack(liquorFruit, 500), spiritNeutralStack);
			RefineryManager.addRecipe(energy, new FluidStack(liquorPear, 500), spiritNeutralStack);

			/* LIQUOR */
			FluidStack liquorFruitStack = new FluidStack(liquorFruit, 200);

			RefineryManager.addRecipe(energy, new FluidStack(brandyApple, 500), new FluidStack(liquorApple, 200));
			RefineryManager.addRecipe(energy, new FluidStack(brandyApricot, 500), new FluidStack(liquorApricot, 200));
			RefineryManager.addRecipe(energy, new FluidStack(brandyCherry, 500), new FluidStack(liquorCherry, 200));
			RefineryManager.addRecipe(energy, new FluidStack(brandyCitrus, 500), liquorFruitStack);
			RefineryManager.addRecipe(energy, new FluidStack(brandyElderberry, 500), new FluidStack(liquorElderberry, 200));
			RefineryManager.addRecipe(energy, new FluidStack(brandyFruit, 500), liquorFruitStack);
			RefineryManager.addRecipe(energy, new FluidStack(brandyPear, 500), new FluidStack(liquorPear, 200));
			RefineryManager.addRecipe(energy, new FluidStack(brandyPlum, 500), liquorFruitStack);

			/* BRANDY */
			FluidStack brandyFruitStack = new FluidStack(brandyFruit, 200);

			RefineryManager.addRecipe(energy, new FluidStack(ciderApple, 500), new FluidStack(brandyApple, 200));
			RefineryManager.addRecipe(energy, new FluidStack(ciderPeach, 500), brandyFruitStack);
			RefineryManager.addRecipe(energy, new FluidStack(ciderPear, 500), new FluidStack(brandyPear, 200));

			RefineryManager.addRecipe(energy, new FluidStack(wineApricot, 500), new FluidStack(brandyApricot, 200));
			RefineryManager.addRecipe(energy, new FluidStack(wineBanana, 500), brandyFruitStack);
			RefineryManager.addRecipe(energy, new FluidStack(wineCarrot, 500), brandyFruitStack);
			RefineryManager.addRecipe(energy, new FluidStack(wineCherry, 500), new FluidStack(brandyCherry, 200));
			RefineryManager.addRecipe(energy, new FluidStack(wineCitrus, 500), new FluidStack(brandyCitrus, 200));
			RefineryManager.addRecipe(energy, new FluidStack(wineCranberry, 500), brandyFruitStack);
			RefineryManager.addRecipe(energy, new FluidStack(wineElderberry, 500), new FluidStack(brandyElderberry, 200));
			RefineryManager.addRecipe(energy, new FluidStack(winePlum, 500), new FluidStack(brandyPlum, 200));
		}

		/* CENTRIFUGE */
		{
			int energy = CentrifugeManager.DEFAULT_ENERGY;

			if (juiceApple != null) {
				CentrifugeManager.addRecipe(energy, new ItemStack(Items.APPLE), new ArrayList<>(), new FluidStack(juiceApple, 200));
				CentrifugeManager.addRecipe(energy, getItemStack("food", 1, 0), new ArrayList<>(), new FluidStack(juiceApple, 150));
			}
			if (juiceApricot != null) {
				CentrifugeManager.addRecipe(energy, getItemStack("food", 1, 10), new ArrayList<>(), new FluidStack(juiceApricot, 150));
			}
			if (juiceBanana != null) {
				CentrifugeManager.addRecipe(energy, getItemStack("food", 1, 28), new ArrayList<>(), new FluidStack(juiceBanana, 100));
				CentrifugeManager.addRecipe(energy, getItemStack("food", 1, 29), new ArrayList<>(), new FluidStack(juiceBanana, 100));
			}
			if (juiceCherry != null) {
				CentrifugeManager.addRecipe(energy, getItemStack(PARENT_ID, "fruits", 1, 0), new ArrayList<>(), new FluidStack(juiceCherry, 50));
				CentrifugeManager.addRecipe(energy, getItemStack("food", 1, 4), new ArrayList<>(), new FluidStack(juiceCherry, 50));
				CentrifugeManager.addRecipe(energy, getItemStack("food", 1, 5), new ArrayList<>(), new FluidStack(juiceCherry, 50));
				CentrifugeManager.addRecipe(energy, getItemStack("food", 1, 6), new ArrayList<>(), new FluidStack(juiceCherry, 50));
			}
			if (juiceElderberry != null) {
				CentrifugeManager.addRecipe(energy, getItemStack("food", 1, 34), new ArrayList<>(), new FluidStack(juiceElderberry, 100));
			}
			if (juiceLemon != null) {
				CentrifugeManager.addRecipe(energy, getItemStack(PARENT_ID, "fruits", 1, 3), new ArrayList<>(), new FluidStack(juiceLemon, 400));
			}
			if (juiceLime != null) {
				CentrifugeManager.addRecipe(energy, getItemStack("food", 1, 3), new ArrayList<>(), new FluidStack(juiceLime, 300));
				CentrifugeManager.addRecipe(energy, getItemStack("food", 1, 16), new ArrayList<>(), new FluidStack(juiceLime, 300));
				CentrifugeManager.addRecipe(energy, getItemStack("food", 1, 17), new ArrayList<>(), new FluidStack(juiceLime, 300));
			}
			if (juiceOrange != null) {
				CentrifugeManager.addRecipe(energy, getItemStack("food", 1, 1), new ArrayList<>(), new FluidStack(juiceOrange, 400));
				CentrifugeManager.addRecipe(energy, getItemStack("food", 1, 13), new ArrayList<>(), new FluidStack(juiceOrange, 300));
				CentrifugeManager.addRecipe(energy, getItemStack("food", 1, 18), new ArrayList<>(), new FluidStack(juiceOrange, 400));
				CentrifugeManager.addRecipe(energy, getItemStack("food", 1, 21), new ArrayList<>(), new FluidStack(juiceOrange, 300));
			}
			if (juicePeach != null) {
				CentrifugeManager.addRecipe(energy, getItemStack("food", 1, 12), new ArrayList<>(), new FluidStack(juicePeach, 150));
				CentrifugeManager.addRecipe(energy, getItemStack("food", 1, 19), new ArrayList<>(), new FluidStack(juicePeach, 150));
			}
			if (juicePlum != null) {
				CentrifugeManager.addRecipe(energy, getItemStack(PARENT_ID, "fruits", 1, 4), new ArrayList<>(), new FluidStack(juicePlum, 100));
				CentrifugeManager.addRecipe(energy, getItemStack("food", 1, 8), new ArrayList<>(), new FluidStack(juicePlum, 100));
			}
			if (juiceCarrot != null) {
				CentrifugeManager.addRecipe(energy, new ItemStack(Items.CARROT), new ArrayList<>(), new FluidStack(juiceCarrot, 200));
				CentrifugeManager.addRecipe(energy, new ItemStack(Items.CARROT_ON_A_STICK), singletonList(new ItemStack(Items.FISHING_ROD)), new FluidStack(juiceCarrot, 200));
			}
			if (juiceGrapefruit != null) {
				CentrifugeManager.addRecipe(energy, getItemStack("food", 1, 11), new ArrayList<>(), new FluidStack(juiceGrapefruit, 500));
			}
			if (juicePear != null) {
				CentrifugeManager.addRecipe(energy, getItemStack("food", 1, 22), new ArrayList<>(), new FluidStack(juiceGrapefruit, 300));
			}
		}
	}

}
