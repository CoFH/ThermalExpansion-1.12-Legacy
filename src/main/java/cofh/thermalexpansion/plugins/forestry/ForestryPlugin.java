package cofh.thermalexpansion.plugins.forestry;

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

	public static final String MOD_NAME = "forestry";

	public static void initialize() {

		if (!Loader.isModLoaded(MOD_NAME)) {
			return;
		}
		ItemStack honeydew = ItemHelper.cloneStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(MOD_NAME + ":honeydew")), 1);
		ItemStack honeyDrop = ItemHelper.cloneStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(MOD_NAME + ":honeyDrop")), 1);
		ItemStack propolis = ItemHelper.cloneStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(MOD_NAME + ":propolis")), 1);
		ItemStack mulch = ItemHelper.cloneStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(MOD_NAME + ":mulch")), 1);

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

		ThermalExpansion.LOG.info("Thermal Expansion: Forestry Plugin Enabled.");
	}

}
