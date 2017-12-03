package cofh.thermalexpansion.plugins;

import cofh.core.util.ModPlugin;
import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.util.managers.TapperManager;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager;
import cofh.thermalexpansion.util.managers.machine.PulverizerManager;
import cofh.thermalfoundation.init.TFFluids;
import cofh.thermalfoundation.item.ItemMaterial;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;

public class PluginBiomesOPlenty extends ModPlugin {

	public static final String MOD_ID = "biomesoplenty";
	public static final String MOD_NAME = "Biomes O' Plenty";

	public PluginBiomesOPlenty() {

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
			ItemStack sandWhite = getItemStack("white_sand", 1, 0);

			ItemStack plantFlowerVine = getItemStack("flower_vine", 1, 0);

			ItemStack logYellowAutumn = new ItemStack(Blocks.LOG, 1, 2);
			ItemStack logOrangeAutumn = new ItemStack(Blocks.LOG2, 1, 1);
			ItemStack logBamboo = getItemStack("bamboo", 1, 0);
			ItemStack logMagic = getItemStack("log_1", 1, 5);
			ItemStack logUmbral = getItemStack("log_0", 1, 6);
			// Dead
			ItemStack logFir = getItemStack("log_0", 1, 7);
			// Ethereal

			ItemStack logOrigin = new ItemStack(Blocks.LOG, 1, 0);
			ItemStack logCherry = getItemStack("log_0", 1, 5);
			// White Cherry
			ItemStack logMaple = new ItemStack(Blocks.LOG, 1, 0);
			ItemStack logHellbark = getItemStack("log_2", 1, 7);
			ItemStack logFloweringOak = new ItemStack(Blocks.LOG, 1, 0);
			ItemStack logJacaranda = getItemStack("log_3", 1, 4);
			ItemStack logSacredOak = getItemStack("log_0", 1, 4);

			ItemStack logMangrove = getItemStack("log_1", 1, 6);
			ItemStack logPalm = getItemStack("log_1", 1, 7);
			ItemStack logRedwood = getItemStack("log_2", 1, 4);
			ItemStack logWillow = getItemStack("log_2", 1, 5);
			ItemStack logPine = getItemStack("log_2", 1, 6);
			ItemStack logMahogany = getItemStack("log_3", 1, 5);
			ItemStack logEbony = getItemStack("log_3", 1, 6);
			ItemStack logEucalyptus = getItemStack("log_3", 1, 7);

			ItemStack saplingYellowAutumn = getItemStack("sapling_0", 1, 0);
			ItemStack saplingOrangeAutumn = getItemStack("sapling_0", 1, 1);
			ItemStack saplingBamboo = getItemStack("sapling_0", 1, 2);
			ItemStack saplingMagic = getItemStack("sapling_0", 1, 3);
			ItemStack saplingUmbran = getItemStack("sapling_0", 1, 4);
			ItemStack saplingDead = getItemStack("sapling_0", 1, 5);
			ItemStack saplingFir = getItemStack("sapling_0", 1, 6);
			ItemStack saplingEthereal = getItemStack("sapling_0", 1, 7);

			ItemStack saplingOrigin = getItemStack("sapling_1", 1, 0);
			ItemStack saplingPinkCherry = getItemStack("sapling_1", 1, 1);
			ItemStack saplingWhiteCherry = getItemStack("sapling_1", 1, 2);
			ItemStack saplingMaple = getItemStack("sapling_1", 1, 3);
			ItemStack saplingHellback = getItemStack("sapling_1", 1, 4);
			ItemStack saplingFloweringOak = getItemStack("sapling_1", 1, 5);
			ItemStack saplingJacaranda = getItemStack("sapling_1", 1, 6);
			ItemStack saplingSacredOak = getItemStack("sapling_1", 1, 7);

			ItemStack saplingMangrove = getItemStack("sapling_2", 1, 0);
			ItemStack saplingPalm = getItemStack("sapling_2", 1, 1);
			ItemStack saplingRedwood = getItemStack("sapling_2", 1, 2);
			ItemStack saplingWillow = getItemStack("sapling_2", 1, 3);
			ItemStack saplingPine = getItemStack("sapling_2", 1, 4);
			ItemStack saplingMahogany = getItemStack("sapling_2", 1, 5);
			ItemStack saplingEbony = getItemStack("sapling_2", 1, 6);
			ItemStack saplingEucalyptus = getItemStack("sapling_2", 1, 7);

			Block bamboo = getBlock("bamboo");
			Block blockLog0 = getBlock("log_0");
			Block blockLog1 = getBlock("log_1");
			Block blockLog2 = getBlock("log_2");
			Block blockLog3 = getBlock("log_3");

			Block blockLeaves0 = getBlock("leaves_0");
			Block blockLeaves1 = getBlock("leaves_1");
			Block blockLeaves2 = getBlock("leaves_2");
			Block blockLeaves3 = getBlock("leaves_3");
			Block blockLeaves4 = getBlock("leaves_4");
			Block blockLeaves5 = getBlock("leaves_5");

			/* PULVERIZER */
			{
				int energy = PulverizerManager.DEFAULT_ENERGY * 3 / 4;

				for (int i = 0; i < 3; i++) {
					PulverizerManager.addRecipe(energy, new ItemStack(getBlock("white_sandstone"), 1, i), ItemHelper.cloneStack(sandWhite, 2), ItemMaterial.dustNiter, 40);
				}
				PulverizerManager.addRecipe(energy, new ItemStack(getBlock("white_sandstone_stairs")), ItemHelper.cloneStack(sandWhite, 2), ItemMaterial.dustNiter, 20);
				PulverizerManager.addRecipe(energy, new ItemStack(getBlock("other_slab"), 1, 1), ItemHelper.cloneStack(sandWhite, 1), ItemMaterial.dustNiter, 20);
			}

			/* INSOLATOR */
			{
				InsolatorManager.addDefaultRecipe(plantFlowerVine, ItemHelper.cloneStack(plantFlowerVine, 2), ItemStack.EMPTY, 0);

				InsolatorManager.addDefaultTreeRecipe(saplingYellowAutumn, ItemHelper.cloneStack(logYellowAutumn, 6), saplingYellowAutumn);
				InsolatorManager.addDefaultTreeRecipe(saplingOrangeAutumn, ItemHelper.cloneStack(logOrangeAutumn, 6), saplingOrangeAutumn);
				InsolatorManager.addDefaultTreeRecipe(saplingBamboo, ItemHelper.cloneStack(logBamboo, 6), saplingBamboo);
				InsolatorManager.addDefaultTreeRecipe(saplingMagic, ItemHelper.cloneStack(logMagic, 6), saplingMagic);
				InsolatorManager.addDefaultTreeRecipe(saplingUmbran, ItemHelper.cloneStack(logUmbral, 6), saplingUmbran);
				// Dead
				InsolatorManager.addDefaultTreeRecipe(saplingFir, ItemHelper.cloneStack(logFir, 6), saplingFir);
				// Ethereal

				InsolatorManager.addDefaultTreeRecipe(saplingOrigin, ItemHelper.cloneStack(logOrigin, 6), saplingOrigin);
				InsolatorManager.addDefaultTreeRecipe(saplingPinkCherry, ItemHelper.cloneStack(logCherry, 6), saplingPinkCherry);
				InsolatorManager.addDefaultTreeRecipe(saplingWhiteCherry, ItemHelper.cloneStack(logCherry, 6), saplingWhiteCherry);
				InsolatorManager.addDefaultTreeRecipe(saplingMaple, ItemHelper.cloneStack(logMaple, 6), saplingMaple);
				InsolatorManager.addDefaultTreeRecipe(saplingHellback, ItemHelper.cloneStack(logHellbark, 2), saplingHellback);
				InsolatorManager.addDefaultTreeRecipe(saplingFloweringOak, ItemHelper.cloneStack(logFloweringOak, 6), saplingFloweringOak);
				InsolatorManager.addDefaultTreeRecipe(saplingJacaranda, ItemHelper.cloneStack(logJacaranda, 6), saplingJacaranda);
				// Sacred Oak InsolatorManager.addDefaultTreeRecipe(saplingSacredOak, ItemHelper.cloneStack(logSacredOak, 6), saplingSacredOak);

				InsolatorManager.addDefaultTreeRecipe(saplingMangrove, ItemHelper.cloneStack(logMangrove, 6), saplingMangrove);
				InsolatorManager.addDefaultTreeRecipe(saplingPalm, ItemHelper.cloneStack(logPalm, 6), saplingPalm);
				// InsolatorManager.addDefaultTreeRecipe(saplingRedwood, ItemHelper.cloneStack(logRedwood, 6), saplingRedwood);
				InsolatorManager.addDefaultTreeRecipe(saplingWillow, ItemHelper.cloneStack(logWillow, 6), saplingWillow);
				InsolatorManager.addDefaultTreeRecipe(saplingPine, ItemHelper.cloneStack(logPine, 6), saplingPine);
				InsolatorManager.addDefaultTreeRecipe(saplingMahogany, ItemHelper.cloneStack(logMahogany, 6), saplingMahogany);
				InsolatorManager.addDefaultTreeRecipe(saplingEbony, ItemHelper.cloneStack(logEbony, 6), saplingEbony);
				InsolatorManager.addDefaultTreeRecipe(saplingEucalyptus, ItemHelper.cloneStack(logEucalyptus, 6), saplingEucalyptus);
			}

			/* TAPPER */
			{
				// Yellow Autumn
				// Orange Autumn
				TapperManager.addStandardMapping(logBamboo, new FluidStack(FluidRegistry.WATER, 25));
				TapperManager.addStandardMapping(logMagic, new FluidStack(TFFluids.fluidResin, 25));    // TODO: Mana
				TapperManager.addStandardMapping(logUmbral, new FluidStack(TFFluids.fluidResin, 50));
				// Dead
				TapperManager.addStandardMapping(logFir, new FluidStack(TFFluids.fluidResin, 50));
				// Ethereal

				// Origin
				TapperManager.addStandardMapping(logCherry, new FluidStack(TFFluids.fluidSap, 50));
				// White Cherry
				// Maple
				// TapperManager.addStandardMapping(logHellbark, new FluidStack(TFFluids.fluidSap, 50));
				// Flowering Oak
				TapperManager.addStandardMapping(logJacaranda, new FluidStack(TFFluids.fluidSap, 50));
				// TapperManager.addStandardMapping(logSacredOak, new FluidStack(TFFluids.fluidSap, 50));       // TODO: Allow?

				TapperManager.addStandardMapping(logMangrove, new FluidStack(TFFluids.fluidResin, 50));
				// TapperManager.addStandardMapping(logPalm, new FluidStack(TFFluids.fluidSap, 25));            // TODO: Allow?
				// TapperManager.addStandardMapping(logRedwood, new FluidStack(TFFluids.fluidResin, 50));       // TODO: Allow?
				TapperManager.addStandardMapping(logWillow, new FluidStack(TFFluids.fluidResin, 50));
				TapperManager.addStandardMapping(logPine, new FluidStack(TFFluids.fluidResin, 100));
				TapperManager.addStandardMapping(logMahogany, new FluidStack(TFFluids.fluidResin, 25));
				TapperManager.addStandardMapping(logEbony, new FluidStack(TFFluids.fluidResin, 25));
				TapperManager.addStandardMapping(logEucalyptus, new FluidStack(TFFluids.fluidResin, 50));

				addLeafMapping(Blocks.LOG, 2, blockLeaves0, 8);
				addLeafMapping(Blocks.LOG2, 1, blockLeaves0, 9);
				addLeafMapping(bamboo, 0, blockLeaves0, 10);
				addLeafMapping(blockLog1, 5, blockLeaves0, 11);
				addLeafMapping(blockLog0, 6, blockLeaves1, 0);
				// Dead
				addLeafMapping(blockLog0, 7, blockLeaves1, 10);
				// Ethereal

				addLeafMapping(Blocks.LOG, 0, blockLeaves2, 8);
				addLeafMapping(blockLog0, 5, blockLeaves2, 9);
				addLeafMapping(blockLog0, 5, blockLeaves2, 10);
				addLeafMapping(Blocks.LOG, 0, blockLeaves2, 11);
				// Hellbark addLeafMapping(blockLog2, 7, blockLeaves3, 8);
				addLeafMapping(Blocks.LOG, 0, blockLeaves3, 9);
				addLeafMapping(blockLog3, 4, blockLeaves3, 10);
				// Sacred Oak addLeafMapping(blockLog0, 4, blockLeaves3, 11);

				addLeafMapping(blockLog1, 6, blockLeaves4, 8);
				// Palm addLeafMapping(blockLog1, 7, blockLeaves4, 9);
				// Redwood addLeafMapping(blockLog2, 4, blockLeaves4, 10);
				addLeafMapping(blockLog2, 5, blockLeaves4, 11);
				addLeafMapping(blockLog2, 6, blockLeaves5, 8);
				addLeafMapping(blockLog3, 5, blockLeaves5, 9);
				addLeafMapping(blockLog3, 6, blockLeaves5, 10);
				addLeafMapping(blockLog3, 7, blockLeaves5, 11);
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
	private void addLeafMapping(Block logBlock, int logMetadata, Block leafBlock, int leafMetadata) {

		IBlockState logState = logBlock.getStateFromMeta(logMetadata);

		for (Boolean check_decay : BlockLeaves.CHECK_DECAY.getAllowedValues()) {
			IBlockState leafState = leafBlock.getStateFromMeta(leafMetadata).withProperty(BlockLeaves.DECAYABLE, Boolean.TRUE).withProperty(BlockLeaves.CHECK_DECAY, check_decay);
			TapperManager.addLeafMapping(logState, leafState);
		}
	}

}
