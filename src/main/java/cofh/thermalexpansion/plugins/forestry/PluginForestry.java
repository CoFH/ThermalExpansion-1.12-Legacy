package cofh.thermalexpansion.plugins.forestry;

import cofh.core.util.ModPlugin;
import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.util.managers.machine.CentrifugeManager;
import cofh.thermalexpansion.util.managers.machine.FurnaceManager;
import cofh.thermalexpansion.util.managers.machine.RefineryManager;
import cofh.thermalexpansion.util.managers.machine.TransposerManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;

import static java.util.Arrays.asList;

public class PluginForestry extends ModPlugin {

	public static final String MOD_ID = "forestry";
	public static final String MOD_NAME = "Forestry";

	public PluginForestry() {

		super(MOD_ID, MOD_NAME);
	}

	/* IInitializer */
	@Override
	public boolean initialize() {

		String category = "Plugins";
		String comment = "If TRUE, support for " + MOD_NAME + " is enabled.";
		enable = Loader.isModLoaded(MOD_ID) && ThermalExpansion.CONFIG.getConfiguration().getBoolean(MOD_NAME, category, true, comment);

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
			ItemStack woodPile = getItemStack("wood_pile", 1);

			ItemStack combHoney = getItemStack("bee_combs", 1, 0);
			ItemStack combCocoa = getItemStack("bee_combs", 1, 1);
			ItemStack combSimmering = getItemStack("bee_combs", 1, 2);
			ItemStack combStringy = getItemStack("bee_combs", 1, 3);
			ItemStack combFrozen = getItemStack("bee_combs", 1, 4);
			ItemStack combDripping = getItemStack("bee_combs", 1, 5);
			ItemStack combSilky = getItemStack("bee_combs", 1, 6);
			ItemStack combParched = getItemStack("bee_combs", 1, 7);
			ItemStack combMysterious = getItemStack("bee_combs", 1, 8);
			ItemStack combIrradiated = getItemStack("bee_combs", 1, 9);
			ItemStack combPowdery = getItemStack("bee_combs", 1, 10);
			ItemStack combReddened = getItemStack("bee_combs", 1, 11);
			ItemStack combDarkened = getItemStack("bee_combs", 1, 12);
			ItemStack combOmega = getItemStack("bee_combs", 1, 13);
			ItemStack combWheat = getItemStack("bee_combs", 1, 14);
			ItemStack combMossy = getItemStack("bee_combs", 1, 15);
			ItemStack combMellow = getItemStack("bee_combs", 1, 16);

			ItemStack honeydew = getItemStack("honeydew");
			ItemStack dropHoney = getItemStack("honey_drop", 1, 0);
			ItemStack dropHoneyCharged = getItemStack("honey_drop", 1, 1);
			ItemStack dropHoneyOmega = getItemStack("honey_drop", 1, 2);

			ItemStack pollenCrystalline = getItemStack("pollen", 1, 1);

			ItemStack propolis = getItemStack("propolis", 1, 0);
			ItemStack propolisSticky = getItemStack("propolis", 1, 1);
			ItemStack propolisPulsating = getItemStack("propolis", 1, 2);
			ItemStack propolisSilky = getItemStack("propolis", 1, 3);

			ItemStack capsule = getItemStack("capsule", 1, 1);
			ItemStack capsuleRefactory = getItemStack("refractory", 1, 1);

			ItemStack wax = getItemStack("beeswax");
			ItemStack waxRefractory = getItemStack("refractory_wax");

			ItemStack silkWisp = getItemStack("crafting_material", 1, 2);

			ItemStack mulch = getItemStack("mulch");
			ItemStack phosphor = getItemStack("phosphor");

			Fluid biomass = FluidRegistry.getFluid("biomass");
			Fluid ethanol = FluidRegistry.getFluid("bio.ethanol");
			Fluid honey = FluidRegistry.getFluid("for.honey");
			Fluid juice = FluidRegistry.getFluid("juice");
			Fluid seed_oil = FluidRegistry.getFluid("seed.oil");

			/* FURNACE */
			{
				FurnaceManager.addRecipePyrolysis(4000, woodPile, new ItemStack(Items.COAL, 6, 1), 600);
			}

			/* REFINERY */
			{
				if (biomass != null && ethanol != null) {
					RefineryManager.addRecipe(3000, new FluidStack(biomass, 100), new FluidStack(ethanol, 30));
				}
			}

			/* TRANSPOSER */
			{
				int energy = 2400;

				if (honey != null) {
					TransposerManager.addExtractRecipe(energy, honeydew, ItemStack.EMPTY, new FluidStack(honey, 100), 0, false);
					TransposerManager.addExtractRecipe(energy, dropHoney, propolis, new FluidStack(honey, 100), 5, false);
				}

				if (juice != null) {
					TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(Items.APPLE, 1), mulch, new FluidStack(juice, 200), 20, false);
					TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(Items.CARROT, 1), mulch, new FluidStack(juice, 200), 20, false);
					TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(ItemHelper.getOre("cropDate"), 1), mulch, new FluidStack(juice, 50), 20, false);
					TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(ItemHelper.getOre("cropLemon"), 1), mulch, new FluidStack(juice, 400), 10, false);
					TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(ItemHelper.getOre("cropPapaya"), 1), mulch, new FluidStack(juice, 600), 10, false);
					TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(ItemHelper.getOre("cropPlum"), 1), mulch, new FluidStack(juice, 100), 60, false);
				}

				if (seed_oil != null) {
					TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(Items.WHEAT_SEEDS, 1), ItemStack.EMPTY, new FluidStack(seed_oil, 10), 0, false);
					TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(Items.BEETROOT_SEEDS, 1), ItemStack.EMPTY, new FluidStack(seed_oil, 10), 0, false);
					TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(Items.PUMPKIN_SEEDS, 1), ItemStack.EMPTY, new FluidStack(seed_oil, 10), 0, false);
					TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(Items.MELON_SEEDS, 1), ItemStack.EMPTY, new FluidStack(seed_oil, 10), 0, false);
					TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(ItemHelper.getOre("cropCherry"), 1), mulch, new FluidStack(seed_oil, 50), 5, false);
					TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(ItemHelper.getOre("cropChestnut"), 1), mulch, new FluidStack(seed_oil, 220), 2, false);
					TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(ItemHelper.getOre("cropWalnut"), 1), mulch, new FluidStack(seed_oil, 180), 5, false);

					addSeedOilRecipes();
				}
				TransposerManager.addContainerOverride(capsule, wax, 10);
				TransposerManager.addContainerOverride(capsuleRefactory, waxRefractory, 10);
			}

			/* CENTRIFUGE */
			{
				int energy = CentrifugeManager.DEFAULT_ENERGY;

				CentrifugeManager.addRecipe(energy, combHoney, asList(wax, dropHoney), asList(100, 40), null);
				CentrifugeManager.addRecipe(energy, combCocoa, asList(wax, new ItemStack(Items.DYE, 1, 3)), asList(100, 50), null);
				CentrifugeManager.addRecipe(energy, combSimmering, asList(waxRefractory, ItemHelper.cloneStack(phosphor, 2)), asList(100, 70), null);
				CentrifugeManager.addRecipe(energy, combStringy, asList(propolis, dropHoney), asList(100, 40), null);
				CentrifugeManager.addRecipe(energy, combFrozen, asList(wax, dropHoney, new ItemStack(Items.SNOWBALL), pollenCrystalline), asList(80, 70, 40, 20), null);
				CentrifugeManager.addRecipe(energy, combDripping, asList(honeydew, dropHoney), asList(100, 40), null);
				CentrifugeManager.addRecipe(energy, combSilky, asList(dropHoney, propolisSilky), asList(100, 80), null);
				CentrifugeManager.addRecipe(energy, combParched, asList(wax, dropHoney), asList(100, 90), null);
				CentrifugeManager.addRecipe(energy, combMysterious, asList(propolisPulsating, dropHoney), asList(100, 40), null);
				CentrifugeManager.addRecipe(energy, combPowdery, asList(new ItemStack(Items.GUNPOWDER), wax, dropHoney), asList(90, 20, 20), null);
				CentrifugeManager.addRecipe(energy, combWheat, asList(new ItemStack(Items.WHEAT), wax, dropHoney), asList(80, 20, 20), null);
				CentrifugeManager.addRecipe(energy, combMossy, asList(wax, dropHoney), asList(100, 90), null);
				CentrifugeManager.addRecipe(energy, combMellow, asList(honeydew, new ItemStack(Items.QUARTZ), wax), asList(60, 30, 20), null);

				CentrifugeManager.addRecipe(energy, propolisSilky, asList(silkWisp, propolis), asList(60, 10), null);
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

	/* HELPERS */
	public static void addSeedOilRecipes() {

		Fluid seed_oil = FluidRegistry.getFluid("seed.oil");
		String[] oreNameList = OreDictionary.getOreNames();

		for (String name : oreNameList) {
			if (name.startsWith("seed")) {
				List<ItemStack> seed = OreDictionary.getOres(name, false);

				if (seed.isEmpty()) {
					continue;
				}
				TransposerManager.addExtractRecipe(2400, ItemHelper.cloneStack(seed.get(0), 1), ItemStack.EMPTY, new FluidStack(seed_oil, 10), 0, false);
			}
		}
	}

}
