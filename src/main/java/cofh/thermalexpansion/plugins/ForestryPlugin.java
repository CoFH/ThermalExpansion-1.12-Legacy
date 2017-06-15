package cofh.thermalexpansion.plugins;

import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.util.managers.machine.TransposerManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class ForestryPlugin {

	private ForestryPlugin() {

	}

	public static final String MOD_ID = "forestry";
	public static final String MOD_NAME = "Forestry";

	public static void initialize() {

		String category = "Plugins";
		String comment = "If TRUE, support for " + MOD_NAME + " is enabled.";

		boolean enable = ThermalExpansion.CONFIG.getConfiguration().getBoolean(MOD_NAME, category, true, comment);

		if (!enable || !Loader.isModLoaded(MOD_ID)) {
			return;
		}
		ItemStack honeydew = ItemHelper.cloneStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(MOD_ID + ":honeydew")), 1);
		ItemStack honeyDrop = ItemHelper.cloneStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(MOD_ID + ":honeyDrop")), 1);
		ItemStack propolis = ItemHelper.cloneStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(MOD_ID + ":propolis")), 1);
		ItemStack mulch = ItemHelper.cloneStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(MOD_ID + ":mulch")), 1);

		Fluid honey = FluidRegistry.getFluid("for.honey");
		Fluid juice = FluidRegistry.getFluid("juice");
		Fluid seed_oil = FluidRegistry.getFluid("seed.oil");

		if (honey != null) {
			TransposerManager.addExtractRecipe(4800, honeydew, null, new FluidStack(honey, 100), 0, false);
			TransposerManager.addExtractRecipe(4800, honeyDrop, propolis, new FluidStack(honey, 100), 5, false);
		}

		if (juice != null) {
			TransposerManager.addExtractRecipe(2400, ItemHelper.cloneStack(Items.APPLE, 1), mulch, new FluidStack(juice, 200), 20, false);
			TransposerManager.addExtractRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropDate"), 1), mulch, new FluidStack(juice, 50), 20, false);
			TransposerManager.addExtractRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropLemon"), 1), mulch, new FluidStack(juice, 400), 10, false);
			TransposerManager.addExtractRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropPapaya"), 1), mulch, new FluidStack(juice, 600), 10, false);
			TransposerManager.addExtractRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropPlum"), 1), mulch, new FluidStack(juice, 100), 60, false);
		}

		if (seed_oil != null) {
			TransposerManager.addExtractRecipe(2400, ItemHelper.cloneStack(Items.WHEAT_SEEDS, 1), null, new FluidStack(seed_oil, 10), 0, false);
			TransposerManager.addExtractRecipe(2400, ItemHelper.cloneStack(Items.PUMPKIN_SEEDS, 1), null, new FluidStack(seed_oil, 10), 0, false);
			TransposerManager.addExtractRecipe(2400, ItemHelper.cloneStack(Items.MELON_SEEDS, 1), null, new FluidStack(seed_oil, 10), 0, false);
			TransposerManager.addExtractRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropCherry"), 1), mulch, new FluidStack(seed_oil, 50), 5, false);
			TransposerManager.addExtractRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropChestnut"), 1), mulch, new FluidStack(seed_oil, 220), 2, false);
			TransposerManager.addExtractRecipe(2400, ItemHelper.cloneStack(ItemHelper.getOre("cropWalnut"), 1), mulch, new FluidStack(seed_oil, 180), 5, false);
		}

		ThermalExpansion.LOG.info("Thermal Expansion: " + MOD_NAME + " Plugin Enabled.");
	}

}
