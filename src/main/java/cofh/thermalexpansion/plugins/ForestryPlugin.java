package cofh.thermalexpansion.plugins;

import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.util.managers.machine.FurnaceManager;
import cofh.thermalexpansion.util.managers.machine.RefineryManager;
import cofh.thermalexpansion.util.managers.machine.TransposerManager;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;

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
		try {
			ItemStack woodPile = getBlockStack("wood_pile", 1);

			ItemStack honeydew = ItemHelper.cloneStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(MOD_ID + ":honeydew")), 1);
			ItemStack honeyDrop = ItemHelper.cloneStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(MOD_ID + ":honeyDrop")), 1);
			ItemStack propolis = ItemHelper.cloneStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(MOD_ID + ":propolis")), 1);
			ItemStack mulch = ItemHelper.cloneStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(MOD_ID + ":mulch")), 1);

			Fluid biomass = FluidRegistry.getFluid("biomass");
			Fluid ethanol = FluidRegistry.getFluid("bio.ethanol");
			Fluid honey = FluidRegistry.getFluid("for.honey");
			Fluid juice = FluidRegistry.getFluid("juice");
			Fluid seed_oil = FluidRegistry.getFluid("seed.oil");

			/* FURNACE */
			{
				FurnaceManager.addRecipePyrolysis(8000, woodPile, new ItemStack(Items.COAL, 6, 1), 400);
			}

			/* REFINERY */
			{
				if (biomass != null && ethanol != null) {
					RefineryManager.addRecipe(3000, new FluidStack(biomass, 100), new FluidStack(ethanol, 30), null);
				}
			}

			/* TRANSPOSER */
			{
				int energy = 4800;

				if (honey != null) {
					TransposerManager.addExtractRecipe(energy, honeydew, null, new FluidStack(honey, 100), 0, false);
					TransposerManager.addExtractRecipe(energy, honeyDrop, propolis, new FluidStack(honey, 100), 5, false);
				}
				energy = 2400;

				if (juice != null) {
					TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(Items.APPLE, 1), mulch, new FluidStack(juice, 200), 20, false);
					TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(ItemHelper.getOre("cropDate"), 1), mulch, new FluidStack(juice, 50), 20, false);
					TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(ItemHelper.getOre("cropLemon"), 1), mulch, new FluidStack(juice, 400), 10, false);
					TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(ItemHelper.getOre("cropPapaya"), 1), mulch, new FluidStack(juice, 600), 10, false);
					TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(ItemHelper.getOre("cropPlum"), 1), mulch, new FluidStack(juice, 100), 60, false);
				}

				if (seed_oil != null) {
					TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(Items.WHEAT_SEEDS, 1), null, new FluidStack(seed_oil, 10), 0, false);
					TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(Items.PUMPKIN_SEEDS, 1), null, new FluidStack(seed_oil, 10), 0, false);
					TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(Items.MELON_SEEDS, 1), null, new FluidStack(seed_oil, 10), 0, false);
					TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(ItemHelper.getOre("cropCherry"), 1), mulch, new FluidStack(seed_oil, 50), 5, false);
					TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(ItemHelper.getOre("cropChestnut"), 1), mulch, new FluidStack(seed_oil, 220), 2, false);
					TransposerManager.addExtractRecipe(energy, ItemHelper.cloneStack(ItemHelper.getOre("cropWalnut"), 1), mulch, new FluidStack(seed_oil, 180), 5, false);
				}
			}

			ThermalExpansion.LOG.info("Thermal Expansion: " + MOD_NAME + " Plugin Enabled.");
		} catch (Throwable t) {
			ThermalExpansion.LOG.error("Thermal Expansion: " + MOD_NAME + " Plugin encountered an error:", t);
		}
	}

	public static void postInit() {

		try {
			addSeedOilRecipes();
		} catch (Throwable t) {
			ThermalExpansion.LOG.error("Thermal Expansion: " + MOD_NAME + " Plugin encountered an error:", t);
		}
	}

	/* HELPERS */
	public static void addSeedOilRecipes() {

		Fluid seed_oil = FluidRegistry.getFluid("seed.oil");

		if (seed_oil == null) {
			return;
		}
		String[] oreNameList = OreDictionary.getOreNames();
		for (String name : oreNameList) {
			if (name.startsWith("seed")) {
				List<ItemStack> seed = OreDictionary.getOres(name, false);

				if (seed.isEmpty()) {
					continue;
				}
				TransposerManager.addExtractRecipe(2400, ItemHelper.cloneStack(seed.get(0), 1), null, new FluidStack(seed_oil, 10), 0, false);
			}
		}

	}

	private static ItemStack getBlockStack(String name, int amount, int meta) {

		Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(MOD_ID + ":" + name));
		return block != null ? new ItemStack(block, amount, meta) : null;
	}

	private static ItemStack getBlockStack(String name, int amount) {

		return getBlockStack(name, amount, 0);
	}

	private static ItemStack getItem(String name, int amount, int meta) {

		Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MOD_ID + ":" + name));
		return item != null ? new ItemStack(item, amount, meta) : null;
	}

	private static ItemStack getItem(String name) {

		return getItem(name, 1, 0);
	}

}
