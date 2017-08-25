package cofh.thermalexpansion.plugins;

import cofh.core.util.ModPlugin;
import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.util.managers.TapperManager;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager.Type;
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
			ItemStack sandWhite = getBlockStack("white_sand", 1, 0);

			ItemStack logYellowAutumn = new ItemStack(Blocks.LOG, 1, 2);
			ItemStack logOrangeAutumn = new ItemStack(Blocks.LOG2, 1, 1);
			ItemStack logBamboo = getBlockStack("bamboo", 1, 0);
			ItemStack logMagic = getBlockStack("log_1", 1, 5);
			ItemStack logUmbral = getBlockStack("log_0", 1, 6);
			// Dead
			ItemStack logFir = getBlockStack("log_0", 1, 7);
			// Ethereal

			ItemStack logOrigin = new ItemStack(Blocks.LOG, 1, 0);
			ItemStack logCherry = getBlockStack("log_0", 1, 5);
			// White Cherry
			ItemStack logMaple = new ItemStack(Blocks.LOG, 1, 0);
			ItemStack logHellbark = getBlockStack("log_2", 1, 7);
			ItemStack logFloweringOak = new ItemStack(Blocks.LOG, 1, 0);
			ItemStack logJacaranda = getBlockStack("log_3", 1, 4);
			ItemStack logSacredOak = getBlockStack("log_0", 1, 4);

			ItemStack logMangrove = getBlockStack("log_1", 1, 6);
			ItemStack logPalm = getBlockStack("log_1", 1, 7);
			ItemStack logRedwood = getBlockStack("log_2", 1, 4);
			ItemStack logWillow = getBlockStack("log_2", 1, 5);
			ItemStack logPine = getBlockStack("log_2", 1, 6);
			ItemStack logMahogany = getBlockStack("log_3", 1, 5);
			ItemStack logEbony = getBlockStack("log_3", 1, 6);
			ItemStack logEucalyptus = getBlockStack("log_3", 1, 7);

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
			Block log0 = getBlock("log_0");
			Block log1 = getBlock("log_1");
			Block log2 = getBlock("log_2");
			Block log3 = getBlock("log_3");

			Block leaves0 = getBlock("leaves_0");
			Block leaves1 = getBlock("leaves_1");
			Block leaves2 = getBlock("leaves_2");
			Block leaves3 = getBlock("leaves_3");
			Block leaves4 = getBlock("leaves_4");
			Block leaves5 = getBlock("leaves_5");

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
				InsolatorManager.addDefaultTreeRecipe(saplingYellowAutumn, ItemHelper.cloneStack(logYellowAutumn, 4), saplingYellowAutumn, 50, false, Type.TREE);
				InsolatorManager.addDefaultTreeRecipe(saplingOrangeAutumn, ItemHelper.cloneStack(logOrangeAutumn, 4), saplingOrangeAutumn, 50, false, Type.TREE);
				InsolatorManager.addDefaultTreeRecipe(saplingBamboo, ItemHelper.cloneStack(logBamboo, 4), saplingBamboo, 50, false, Type.TREE);
				InsolatorManager.addDefaultTreeRecipe(saplingMagic, ItemHelper.cloneStack(logMagic, 4), saplingMagic, 50, false, Type.TREE);
				InsolatorManager.addDefaultTreeRecipe(saplingUmbran, ItemHelper.cloneStack(logUmbral, 4), saplingUmbran, 50, false, Type.TREE);
				// Dead
				InsolatorManager.addDefaultTreeRecipe(saplingFir, ItemHelper.cloneStack(logFir, 4), saplingFir, 50, false, Type.TREE);
				// Ethereal

				InsolatorManager.addDefaultTreeRecipe(saplingOrigin, ItemHelper.cloneStack(logOrigin, 4), saplingOrigin, 50, false, Type.TREE);
				InsolatorManager.addDefaultTreeRecipe(saplingPinkCherry, ItemHelper.cloneStack(logCherry, 4), saplingPinkCherry, 50, false, Type.TREE);
				InsolatorManager.addDefaultTreeRecipe(saplingWhiteCherry, ItemHelper.cloneStack(logCherry, 4), saplingWhiteCherry, 50, false, Type.TREE);
				InsolatorManager.addDefaultTreeRecipe(saplingMaple, ItemHelper.cloneStack(logMaple, 4), saplingMaple, 50, false, Type.TREE);
				InsolatorManager.addDefaultTreeRecipe(saplingHellback, ItemHelper.cloneStack(logHellbark, 2), saplingHellback, 50, false, Type.NETHER_TREE);
				InsolatorManager.addDefaultTreeRecipe(saplingFloweringOak, ItemHelper.cloneStack(logFloweringOak, 4), saplingFloweringOak, 50, false, Type.TREE);
				InsolatorManager.addDefaultTreeRecipe(saplingJacaranda, ItemHelper.cloneStack(logJacaranda, 4), saplingJacaranda, 50, false, Type.TREE);
				// Sacred Oak InsolatorManager.addDefaultTreeRecipe(saplingSacredOak, ItemHelper.cloneStack(logSacredOak, 4), saplingSacredOak, 50, false, Type.TREE);

				InsolatorManager.addDefaultTreeRecipe(saplingMangrove, ItemHelper.cloneStack(logMangrove, 4), saplingMangrove, 50, false, Type.TREE);
				InsolatorManager.addDefaultTreeRecipe(saplingPalm, ItemHelper.cloneStack(logPalm, 4), saplingPalm, 50, false, Type.TREE);
				// InsolatorManager.addDefaultTreeRecipe(saplingRedwood, ItemHelper.cloneStack(logRedwood, 4), saplingRedwood, 50, false, Type.TREE);
				InsolatorManager.addDefaultTreeRecipe(saplingWillow, ItemHelper.cloneStack(logWillow, 4), saplingWillow, 50, false, Type.TREE);
				InsolatorManager.addDefaultTreeRecipe(saplingPine, ItemHelper.cloneStack(logPine, 4), saplingPine, 50, false, Type.TREE);
				InsolatorManager.addDefaultTreeRecipe(saplingMahogany, ItemHelper.cloneStack(logMahogany, 4), saplingMahogany, 50, false, Type.TREE);
				InsolatorManager.addDefaultTreeRecipe(saplingEbony, ItemHelper.cloneStack(logEbony, 4), saplingEbony, 50, false, Type.TREE);
				InsolatorManager.addDefaultTreeRecipe(saplingEucalyptus, ItemHelper.cloneStack(logEucalyptus, 4), saplingEucalyptus, 50, false, Type.TREE);
			}

			/* TAPPER */
			{
				// Yellow Autumn
				// Orange Autumn
				TapperManager.addMapping(logBamboo, new FluidStack(FluidRegistry.WATER, 25));
				TapperManager.addMapping(logMagic, new FluidStack(TFFluids.fluidResin, 25));    // TODO: Mana
				TapperManager.addMapping(logUmbral, new FluidStack(TFFluids.fluidResin, 50));
				// Dead
				TapperManager.addMapping(logFir, new FluidStack(TFFluids.fluidResin, 50));
				// Ethereal

				// Origin
				TapperManager.addMapping(logCherry, new FluidStack(TFFluids.fluidSap, 50));
				// White Cherry
				// Maple
				// TapperManager.addMapping(logHellbark, new FluidStack(TFFluids.fluidSap, 50));
				// Flowering Oak
				TapperManager.addMapping(logJacaranda, new FluidStack(TFFluids.fluidSap, 50));
				// TapperManager.addMapping(logSacredOak, new FluidStack(TFFluids.fluidSap, 50));       // TODO: Allow?

				TapperManager.addMapping(logMangrove, new FluidStack(TFFluids.fluidResin, 50));
				// TapperManager.addMapping(logPalm, new FluidStack(TFFluids.fluidSap, 25));            // TODO: Allow?
				// TapperManager.addMapping(logRedwood, new FluidStack(TFFluids.fluidResin, 50));       // TODO: Allow?
				TapperManager.addMapping(logWillow, new FluidStack(TFFluids.fluidResin, 50));
				TapperManager.addMapping(logPine, new FluidStack(TFFluids.fluidResin, 100));
				TapperManager.addMapping(logMahogany, new FluidStack(TFFluids.fluidResin, 25));
				TapperManager.addMapping(logEbony, new FluidStack(TFFluids.fluidResin, 25));
				TapperManager.addMapping(logEucalyptus, new FluidStack(TFFluids.fluidResin, 50));

				addLeafMapping(Blocks.LOG, 2, leaves0, 8);
				addLeafMapping(Blocks.LOG2, 1, leaves0, 9);
				addLeafMapping(bamboo, 0, leaves0, 10);
				addLeafMapping(log1, 5, leaves0, 11);
				addLeafMapping(log0, 6, leaves1, 0);
				// Dead
				addLeafMapping(log0, 7, leaves1, 10);
				// Ethereal

				addLeafMapping(Blocks.LOG, 0, leaves2, 8);
				addLeafMapping(log0, 5, leaves2, 9);
				addLeafMapping(log0, 5, leaves2, 10);
				addLeafMapping(Blocks.LOG, 0, leaves2, 11);
				// Hellbark addLeafMapping(log2, 7, leaves3, 8);
				addLeafMapping(Blocks.LOG, 0, leaves3, 9);
				addLeafMapping(log3, 4, leaves3, 10);
				// Sacred Oak addLeafMapping(log0, 4, leaves3, 11);

				addLeafMapping(log1, 6, leaves4, 8);
				// Palm addLeafMapping(log1, 7, leaves4, 9);
				// Redwood addLeafMapping(log2, 4, leaves4, 10);
				addLeafMapping(log2, 5, leaves4, 11);
				addLeafMapping(log2, 6, leaves5, 8);
				addLeafMapping(log3, 5, leaves5, 9);
				addLeafMapping(log3, 6, leaves5, 10);
				addLeafMapping(log3, 7, leaves5, 11);
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
			TapperManager.addLeafMappingDirect(logState, leafState);
		}
	}

}
