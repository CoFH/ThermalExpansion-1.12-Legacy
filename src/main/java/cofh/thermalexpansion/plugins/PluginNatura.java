package cofh.thermalexpansion.plugins;

import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.util.managers.device.TapperManager;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager;
import cofh.thermalexpansion.util.managers.machine.TransposerManager;
import cofh.thermalfoundation.init.TFFluids;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class PluginNatura extends PluginTEBase {

	public static final String MOD_ID = "natura";
	public static final String MOD_NAME = "Natura";

	public PluginNatura() {

		super(MOD_ID, MOD_NAME);
	}

	@Override
	public void registerDelegate() {

		ItemStack seedBarley = getItemStack("overworld_seeds", 1, 0);
		ItemStack seedCotton = getItemStack("overworld_seeds", 1, 1);

		ItemStack cropRaspberry = getItemStack("edibles", 1, 2);
		ItemStack cropBlueberry = getItemStack("edibles", 1, 3);
		ItemStack cropBlackberry = getItemStack("edibles", 1, 4);
		ItemStack cropMaloberry = getItemStack("edibles", 1, 5);

		ItemStack cropBlightberry = getItemStack("edibles", 1, 6);
		ItemStack cropDuskberry = getItemStack("edibles", 1, 7);
		ItemStack cropSkyberry = getItemStack("edibles", 1, 8);
		ItemStack cropStingberry = getItemStack("edibles", 1, 9);

		ItemStack bushRaspberry = getItemStack("overworld_berrybush_raspberry", 1, 0);
		ItemStack bushBlueberry = getItemStack("overworld_berrybush_blueberry", 1, 0);
		ItemStack bushBlackberry = getItemStack("overworld_berrybush_blackberry", 1, 0);
		ItemStack bushMaloberry = getItemStack("overworld_berrybush_maloberry", 1, 0);

		ItemStack bushBlightberry = getItemStack("nether_berrybush_blightberry", 1, 0);
		ItemStack bushDuskberry = getItemStack("nether_berrybush_duskberry", 1, 0);
		ItemStack bushSkyberry = getItemStack("nether_berrybush_skyberry", 1, 0);
		ItemStack bushStingberry = getItemStack("nether_berrybush_stingberry", 1, 0);

		ItemStack saguaroFruit = getItemStack("saguaro_fruit_item");
		ItemStack saguaroCactus = getItemStack("saguaro", 1, 0);
		ItemStack saguaroCactusBaby = getItemStack("saguaro_baby", 1, 0);

		ItemStack plantThornvine = getItemStack("nether_thorn_vines", 1, 0);

		ItemStack glowshroomGreen = getItemStack("nether_glowshroom", 1, 0);
		ItemStack glowshroomBlue = getItemStack("nether_glowshroom", 1, 1);
		ItemStack glowshroomPurple = getItemStack("nether_glowshroom", 1, 2);

		ItemStack logMaple = getItemStack("overworld_logs", 1, 0);
		ItemStack logSilverbell = getItemStack("overworld_logs", 1, 1);
		ItemStack logAmaranth = getItemStack("overworld_logs", 1, 2);
		ItemStack logTigerwood = getItemStack("overworld_logs", 1, 3);

		ItemStack logWillow = getItemStack("overworld_logs2", 1, 0);
		ItemStack logEucalyptus = getItemStack("overworld_logs2", 1, 1);
		ItemStack logHopseed = getItemStack("overworld_logs2", 1, 2);
		ItemStack logSakura = getItemStack("overworld_logs2", 1, 3);

		ItemStack saplingMaple = getItemStack("overworld_sapling", 1, 0);
		ItemStack saplingSilverbell = getItemStack("overworld_sapling", 1, 1);
		ItemStack saplingAmaranth = getItemStack("overworld_sapling", 1, 2);
		ItemStack saplingTigerwood = getItemStack("overworld_sapling", 1, 3);

		ItemStack saplingWillow = getItemStack("overworld_sapling2", 1, 0);
		ItemStack saplingEucalyptus = getItemStack("overworld_sapling2", 1, 1);
		ItemStack saplingHopseed = getItemStack("overworld_sapling2", 1, 2);
		ItemStack saplingSakura = getItemStack("overworld_sapling2", 1, 3);

		ItemStack saplingGhostwood = getItemStack("nether_sapling", 1, 0);
		ItemStack saplingFusewood = getItemStack("nether_sapling", 1, 1);
		ItemStack saplingDarkwood = getItemStack("nether_sapling", 1, 2);
		ItemStack saplingBloodwood = getItemStack("nether_sapling2", 1, 0);

		ItemStack logGhostwood = getItemStack("nether_logs", 1, 0);
		ItemStack logFusewood = getItemStack("nether_logs", 1, 2);
		ItemStack logDarkwood = getItemStack("nether_logs", 1, 1);
		ItemStack logBloodwood = getItemStack("nether_logs2", 1, 0);

		Block blockLog = getBlock("overworld_logs");
		Block blockLog2 = getBlock("overworld_logs2");
		Block blockLogNether = getBlock("nether_logs");

		Block blockLeaves = getBlock("overworld_leaves");
		Block blockLeaves2 = getBlock("overworld_leaves2");
		Block blockLeavesNether = getBlock("nether_leaves");
		Block blockLeavesNether2 = getBlock("nether_leaves2");

		Fluid seed_oil = FluidRegistry.getFluid("seed.oil");

		/* INSOLATOR */
		{
			InsolatorManager.addDefaultRecipe(bushRaspberry, ItemHelper.cloneStack(cropRaspberry, 2), bushRaspberry, 100);
			InsolatorManager.addDefaultRecipe(bushBlueberry, ItemHelper.cloneStack(cropBlueberry, 2), bushBlueberry, 100);
			InsolatorManager.addDefaultRecipe(bushBlackberry, ItemHelper.cloneStack(cropBlackberry, 2), bushBlackberry, 100);
			InsolatorManager.addDefaultRecipe(bushMaloberry, ItemHelper.cloneStack(cropMaloberry, 2), bushMaloberry, 100);

			InsolatorManager.addDefaultRecipe(bushBlightberry, ItemHelper.cloneStack(cropBlightberry, 2), bushBlightberry, 100);
			InsolatorManager.addDefaultRecipe(bushDuskberry, ItemHelper.cloneStack(cropDuskberry, 2), bushDuskberry, 100);
			InsolatorManager.addDefaultRecipe(bushSkyberry, ItemHelper.cloneStack(cropSkyberry, 2), bushSkyberry, 100);
			InsolatorManager.addDefaultRecipe(bushStingberry, ItemHelper.cloneStack(cropStingberry, 2), bushStingberry, 100);

			InsolatorManager.addDefaultRecipe(saguaroFruit, saguaroCactusBaby, ItemStack.EMPTY, 0);
			InsolatorManager.addDefaultRecipe(saguaroCactusBaby, ItemHelper.cloneStack(saguaroFruit, 2), saguaroCactus, 0);

			InsolatorManager.addDefaultRecipe(plantThornvine, ItemHelper.cloneStack(plantThornvine, 2), ItemStack.EMPTY, 0);

			InsolatorManager.addDefaultRecipe(glowshroomGreen, ItemHelper.cloneStack(glowshroomGreen, 2), ItemStack.EMPTY, 0);
			InsolatorManager.addDefaultRecipe(glowshroomBlue, ItemHelper.cloneStack(glowshroomBlue, 2), ItemStack.EMPTY, 0);
			InsolatorManager.addDefaultRecipe(glowshroomPurple, ItemHelper.cloneStack(glowshroomPurple, 2), ItemStack.EMPTY, 0);

			InsolatorManager.addDefaultTreeRecipe(saplingMaple, ItemHelper.cloneStack(logMaple, 6), saplingMaple);
			InsolatorManager.addDefaultTreeRecipe(saplingSilverbell, ItemHelper.cloneStack(logSilverbell, 6), saplingSilverbell);
			InsolatorManager.addDefaultTreeRecipe(saplingAmaranth, ItemHelper.cloneStack(logAmaranth, 6), saplingAmaranth);
			InsolatorManager.addDefaultTreeRecipe(saplingTigerwood, ItemHelper.cloneStack(logTigerwood, 6), saplingTigerwood);

			InsolatorManager.addDefaultTreeRecipe(saplingWillow, ItemHelper.cloneStack(logWillow, 6), saplingWillow);
			InsolatorManager.addDefaultTreeRecipe(saplingEucalyptus, ItemHelper.cloneStack(logEucalyptus, 6), saplingEucalyptus);
			InsolatorManager.addDefaultTreeRecipe(saplingHopseed, ItemHelper.cloneStack(logHopseed, 6), saplingHopseed);
			InsolatorManager.addDefaultTreeRecipe(saplingSakura, ItemHelper.cloneStack(logSakura, 6), saplingSakura);

			InsolatorManager.addDefaultTreeRecipe(saplingGhostwood, ItemHelper.cloneStack(logGhostwood, 6), saplingGhostwood);
			InsolatorManager.addDefaultTreeRecipe(saplingFusewood, ItemHelper.cloneStack(logFusewood, 6), saplingFusewood);
			InsolatorManager.addDefaultTreeRecipe(saplingDarkwood, ItemHelper.cloneStack(logDarkwood, 6), saplingDarkwood);
			InsolatorManager.addDefaultTreeRecipe(saplingBloodwood, ItemHelper.cloneStack(logBloodwood, 6), saplingBloodwood);
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
			TapperManager.addStandardMapping(logMaple, new FluidStack(TFFluids.fluidSap, 100));
			TapperManager.addStandardMapping(logSilverbell, new FluidStack(TFFluids.fluidResin, 50));
			TapperManager.addStandardMapping(logAmaranth, new FluidStack(TFFluids.fluidResin, 50));
			TapperManager.addStandardMapping(logTigerwood, new FluidStack(TFFluids.fluidResin, 50));

			TapperManager.addStandardMapping(logWillow, new FluidStack(TFFluids.fluidResin, 50));
			TapperManager.addStandardMapping(logEucalyptus, new FluidStack(TFFluids.fluidResin, 50));
			TapperManager.addStandardMapping(logHopseed, new FluidStack(TFFluids.fluidResin, 50));
			TapperManager.addStandardMapping(logSakura, new FluidStack(TFFluids.fluidSap, 50));

			TapperManager.addStandardMapping(logGhostwood, new FluidStack(FluidRegistry.LAVA, 25));
			TapperManager.addStandardMapping(logFusewood, new FluidStack(FluidRegistry.LAVA, 25));
			TapperManager.addStandardMapping(logDarkwood, new FluidStack(FluidRegistry.LAVA, 25));

			addLeafMapping(blockLog, 0, blockLeaves, 0);
			addLeafMapping(blockLog, 1, blockLeaves, 1);
			addLeafMapping(blockLog, 2, blockLeaves, 2);
			addLeafMapping(blockLog, 3, blockLeaves, 3);

			addLeafMapping(blockLog2, 0, blockLeaves2, 0);
			addLeafMapping(blockLog2, 1, blockLeaves2, 1);
			addLeafMapping(blockLog2, 2, blockLeaves2, 2);
			addLeafMapping(blockLog2, 3, blockLeaves2, 3);

			addLeafMapping(blockLogNether, 0, blockLeavesNether, 0);
			addLeafMapping(blockLogNether, 2, blockLeavesNether, 2);
			addLeafMapping(blockLogNether, 1, blockLeavesNether2, 0);
		}
	}

}
