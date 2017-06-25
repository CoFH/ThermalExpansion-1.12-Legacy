package cofh.thermalexpansion.plugins;

import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.util.managers.TapperManager;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager.Type;
import cofh.thermalexpansion.util.managers.machine.TransposerManager;
import cofh.thermalfoundation.init.TFFluids;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class NaturaPlugin {

	private NaturaPlugin() {

	}

	public static final String MOD_ID = "natura";
	public static final String MOD_NAME = "Natura";

	public static void initialize() {

		String category = "Plugins";
		String comment = "If TRUE, support for " + MOD_NAME + " is enabled.";

		boolean enable = ThermalExpansion.CONFIG.getConfiguration().getBoolean(MOD_NAME, category, true, comment);

		if (!enable || !Loader.isModLoaded(MOD_ID)) {
			return;
		}

		ItemStack seedBarley = getItem("overworld_seeds", 1, 0);
		ItemStack seedCotton = getItem("overworld_seeds", 1, 1);

		ItemStack raspberry = getItem("edibles", 1, 2);
		ItemStack blueberry = getItem("edibles", 1, 3);
		ItemStack blackberry = getItem("edibles", 1, 4);
		ItemStack maloberry = getItem("edibles", 1, 5);

		ItemStack blightberry = getItem("edibles", 1, 6);
		ItemStack duskberry = getItem("edibles", 1, 7);
		ItemStack skyberry = getItem("edibles", 1, 8);
		ItemStack stingberry = getItem("edibles", 1, 9);

		ItemStack bushRaspberry = getBlockStack("overworld_berrybush_raspberry", 1, 0);
		ItemStack bushBlueberry = getBlockStack("overworld_berrybush_raspberry", 1, 0);
		ItemStack bushBlackberry = getBlockStack("overworld_berrybush_raspberry", 1, 0);
		ItemStack bushMaloberry = getBlockStack("overworld_berrybush_raspberry", 1, 0);

		ItemStack bushBlightberry = getBlockStack("nether_berrybush_blightberry", 1, 0);
		ItemStack bushDuskberry = getBlockStack("nether_berrybush_duskberry", 1, 0);
		ItemStack bushSkyberry = getBlockStack("nether_berrybush_skyberry", 1, 0);
		ItemStack bushStingberry = getBlockStack("nether_berrybush_stingberry", 1, 0);

		ItemStack glowshroomGreen = getBlockStack("nether_glowshroom", 1, 0);
		ItemStack glowshroomBlue = getBlockStack("nether_glowshroom", 1, 1);
		ItemStack glowshroomPurple = getBlockStack("nether_glowshroom", 1, 2);

		ItemStack logMaple = getBlockStack("overworld_logs", 1, 0);
		ItemStack logSilverbell = getBlockStack("overworld_logs", 1, 1);
		ItemStack logAmaranth = getBlockStack("overworld_logs", 1, 2);
		ItemStack logTigerwood = getBlockStack("overworld_logs", 1, 3);

		ItemStack logWillow = getBlockStack("overworld_logs2", 1, 0);
		ItemStack logEucalyptus = getBlockStack("overworld_logs2", 1, 1);
		ItemStack logHopseed = getBlockStack("overworld_logs2", 1, 2);
		ItemStack logSakura = getBlockStack("overworld_logs2", 1, 3);

		ItemStack saplingMaple = getItem("overworld_sapling", 1, 0);
		ItemStack saplingSilverbell = getItem("overworld_sapling", 1, 1);
		ItemStack saplingAmaranth = getItem("overworld_sapling", 1, 2);
		ItemStack saplingTigerwood = getItem("overworld_sapling", 1, 3);

		ItemStack saplingWillow = getItem("overworld_sapling2", 1, 0);
		ItemStack saplingEucalyptus = getItem("overworld_sapling2", 1, 1);
		ItemStack saplingHopseed = getItem("overworld_sapling2", 1, 2);
		ItemStack saplingSakura = getItem("overworld_sapling2", 1, 3);

		ItemStack saplingGhostwood = getItem("nether_sapling", 1, 0);
		ItemStack saplingFusewood = getItem("nether_sapling", 1, 1);
		ItemStack saplingDarkwood = getItem("nether_sapling", 1, 2);
		ItemStack saplingBloodwood = getItem("nether_sapling2", 1, 0);

		ItemStack logGhostwood = getBlockStack("nether_logs", 1, 0);
		ItemStack logDarkwood = getBlockStack("nether_logs", 1, 1);
		ItemStack logFusewood = getBlockStack("nether_logs", 1, 2);
		ItemStack logBloodwood = getBlockStack("nether_logs2", 1, 0);

		Block log = getBlock("overworld_logs");
		Block log2 = getBlock("overworld_logs2");

		Block leaves = getBlock("overworld_leaves");
		Block leaves2 = getBlock("overworld_leaves2");

		Fluid seed_oil = FluidRegistry.getFluid("seed.oil");

		/* INSOLATOR */
		{
			InsolatorManager.addDefaultRecipe(bushRaspberry, ItemHelper.cloneStack(raspberry, 2), bushRaspberry, 100);
			InsolatorManager.addDefaultRecipe(bushBlueberry, ItemHelper.cloneStack(blueberry, 2), bushBlueberry, 100);
			InsolatorManager.addDefaultRecipe(bushBlackberry, ItemHelper.cloneStack(blackberry, 2), bushBlackberry, 100);
			InsolatorManager.addDefaultRecipe(bushMaloberry, ItemHelper.cloneStack(maloberry, 2), bushMaloberry, 100);

			InsolatorManager.addDefaultRecipe(bushBlightberry, ItemHelper.cloneStack(blightberry, 2), bushBlightberry, 100, false, Type.NETHER);
			InsolatorManager.addDefaultRecipe(bushDuskberry, ItemHelper.cloneStack(duskberry, 2), bushDuskberry, 100, false, Type.NETHER);
			InsolatorManager.addDefaultRecipe(bushSkyberry, ItemHelper.cloneStack(skyberry, 2), bushSkyberry, 100, false, Type.NETHER);
			InsolatorManager.addDefaultRecipe(bushStingberry, ItemHelper.cloneStack(stingberry, 2), bushStingberry, 100, false, Type.NETHER);

			InsolatorManager.addDefaultRecipe(glowshroomGreen, ItemHelper.cloneStack(glowshroomGreen, 2), ItemStack.EMPTY, 0, false, Type.NETHER);
			InsolatorManager.addDefaultRecipe(glowshroomBlue, ItemHelper.cloneStack(glowshroomBlue, 2), ItemStack.EMPTY, 0, false, Type.NETHER);
			InsolatorManager.addDefaultRecipe(glowshroomPurple, ItemHelper.cloneStack(glowshroomPurple, 2), ItemStack.EMPTY, 0, false, Type.NETHER);

			InsolatorManager.addDefaultTreeRecipe(saplingMaple, ItemHelper.cloneStack(logMaple, 4), saplingMaple, 50, false, Type.TREE);
			InsolatorManager.addDefaultTreeRecipe(saplingSilverbell, ItemHelper.cloneStack(logSilverbell, 4), saplingSilverbell, 50, false, Type.TREE);
			InsolatorManager.addDefaultTreeRecipe(saplingAmaranth, ItemHelper.cloneStack(logAmaranth, 4), saplingAmaranth, 50, false, Type.TREE);
			InsolatorManager.addDefaultTreeRecipe(saplingTigerwood, ItemHelper.cloneStack(logTigerwood, 4), saplingTigerwood, 50, false, Type.TREE);

			InsolatorManager.addDefaultTreeRecipe(saplingWillow, ItemHelper.cloneStack(logWillow, 4), saplingWillow, 50, false, Type.TREE);
			InsolatorManager.addDefaultTreeRecipe(saplingEucalyptus, ItemHelper.cloneStack(logEucalyptus, 4), saplingEucalyptus, 50, false, Type.TREE);
			InsolatorManager.addDefaultTreeRecipe(saplingHopseed, ItemHelper.cloneStack(logHopseed, 4), saplingHopseed, 50, false, Type.TREE);
			InsolatorManager.addDefaultTreeRecipe(saplingSakura, ItemHelper.cloneStack(logSakura, 4), saplingSakura, 50, false, Type.TREE);

			InsolatorManager.addDefaultTreeRecipe(saplingGhostwood, ItemHelper.cloneStack(logGhostwood, 4), saplingGhostwood, 50, false, Type.NETHER_TREE);
			InsolatorManager.addDefaultTreeRecipe(saplingFusewood, ItemHelper.cloneStack(logDarkwood, 4), saplingFusewood, 50, false, Type.NETHER_TREE);
			InsolatorManager.addDefaultTreeRecipe(saplingDarkwood, ItemHelper.cloneStack(logFusewood, 4), saplingDarkwood, 50, false, Type.NETHER_TREE);
			InsolatorManager.addDefaultTreeRecipe(saplingBloodwood, ItemHelper.cloneStack(logBloodwood, 4), saplingBloodwood, 50, false, Type.NETHER_TREE);
		}

		/* TRANSPOSER */
		{
			if (seed_oil != null) {
				TransposerManager.addExtractRecipe(2400, seedBarley, ItemStack.EMPTY, new FluidStack(seed_oil, 10), 0, false);
				TransposerManager.addExtractRecipe(2400, seedCotton, ItemStack.EMPTY, new FluidStack(seed_oil, 10), 0, false);
			}
		}

		/* TAPPER */
		{
			TapperManager.addMapping(logMaple, new FluidStack(TFFluids.fluidSap, 100));
			TapperManager.addMapping(logSilverbell, new FluidStack(TFFluids.fluidResin, 50));
			TapperManager.addMapping(logAmaranth, new FluidStack(TFFluids.fluidResin, 50));
			TapperManager.addMapping(logTigerwood, new FluidStack(TFFluids.fluidResin, 50));

			TapperManager.addMapping(logWillow, new FluidStack(TFFluids.fluidResin, 50));
			TapperManager.addMapping(logEucalyptus, new FluidStack(TFFluids.fluidResin, 50));
			TapperManager.addMapping(logHopseed, new FluidStack(TFFluids.fluidResin, 50));
			TapperManager.addMapping(logSakura, new FluidStack(TFFluids.fluidSap, 50));

			addLeafMapping(log, leaves, 0);
			addLeafMapping(log, leaves, 1);
			addLeafMapping(log, leaves, 2);
			addLeafMapping(log, leaves, 3);

			addLeafMapping(log2, leaves2, 0);
			addLeafMapping(log2, leaves2, 1);
			addLeafMapping(log2, leaves2, 2);
			addLeafMapping(log2, leaves2, 3);
		}

		ThermalExpansion.LOG.info("Thermal Expansion: " + MOD_NAME + " Plugin Enabled.");
	}

	/* HELPERS */
	private static ItemStack getBlockStack(String name, int amount, int meta) {

		Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(MOD_ID + ":" + name));
		return block != null ? new ItemStack(block, amount, meta) : ItemStack.EMPTY;
	}

	private static ItemStack getBlockStack(String name, int amount) {

		return getBlockStack(name, amount, 0);
	}

	private static Block getBlock(String name) {

		return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(MOD_ID + ":" + name));
	}

	private static ItemStack getItem(String name, int amount, int meta) {

		Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MOD_ID + ":" + name));
		return item != null ? new ItemStack(item, amount, meta) : ItemStack.EMPTY;
	}

	private static ItemStack getItem(String name) {

		return getItem(name, 1, 0);
	}

	private static void addLeafMapping(Block logBlock, Block leafBlock, int metadata) {

		IBlockState logState = logBlock.getStateFromMeta(metadata);

		for (Boolean check_decay : BlockLeaves.CHECK_DECAY.getAllowedValues()) {
			IBlockState leafState = leafBlock.getStateFromMeta(metadata).withProperty(BlockLeaves.DECAYABLE, Boolean.TRUE).withProperty(BlockLeaves.CHECK_DECAY, check_decay);
			TapperManager.addLeafMappingDirect(logState, leafState);
		}
	}

}
