package cofh.thermalexpansion.plugins;

import cofh.core.util.ModPlugin;
import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.util.managers.device.TapperManager;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager;
import cofh.thermalexpansion.util.managers.machine.SawmillManager;
import cofh.thermalfoundation.init.TFFluids;
import cofh.thermalfoundation.item.ItemMaterial;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;

public class PluginTraverse extends ModPlugin {

	public static final String MOD_ID = "traverse";
	public static final String MOD_NAME = "Traverse";

	public PluginTraverse() {

		super(MOD_ID, MOD_NAME);
	}

	/* IInitializer */
	@Override
	public boolean initialize() {

		String category = "Plugins";
		String comment = "If TRUE, support for " + MOD_NAME + " is enabled.";
		enable = ThermalExpansion.CONFIG.getConfiguration().getBoolean(MOD_NAME, category, true, comment) && Loader.isModLoaded(MOD_ID);

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
			ItemStack saplingRed = getItemStack("red_autumnal_sapling");
			ItemStack saplingBrown = getItemStack("brown_autumnal_sapling");
			ItemStack saplingOrange = getItemStack("orange_autumnal_sapling");
			ItemStack saplingYellow = getItemStack("yellow_autumnal_sapling");
			ItemStack saplingFir = getItemStack("fir_sapling");

			ItemStack logFir = getItemStack("fir_log");
			ItemStack planksFir = getItemStack("fir_planks");

			Block blockLeavesRed = getBlock("red_autumnal_leaves");
			Block blockLeavesBrown = getBlock("brown_autumnal_leaves");
			Block blockLeavesOrange = getBlock("orange_autumnal_leaves");
			Block blockLeavesYellow = getBlock("yellow_autumnal_leaves");
			Block blockLeavesFir = getBlock("fir_leaves");

			/* SAWMILL */
			{
				int energy = SawmillManager.DEFAULT_ENERGY * 3 / 2;

				SawmillManager.addRecipe(energy, getItemStack("fir_door"), planksFir, ItemMaterial.dustWood, 50);
				SawmillManager.addRecipe(energy, getItemStack("fir_fence"), planksFir, ItemMaterial.dustWood, 25);
				SawmillManager.addRecipe(energy, getItemStack("fir_fence_gate"), planksFir, ItemMaterial.dustWood, 150);
				SawmillManager.addRecipe(energy, getItemStack("fir_stairs", 2), planksFir, ItemMaterial.dustWood, 50);
			}

			/* INSOLATOR */
			{
				InsolatorManager.addDefaultTreeRecipe(saplingRed, new ItemStack(Blocks.LOG, 6), saplingRed);
				InsolatorManager.addDefaultTreeRecipe(saplingBrown, new ItemStack(Blocks.LOG, 6), saplingBrown);
				InsolatorManager.addDefaultTreeRecipe(saplingOrange, new ItemStack(Blocks.LOG, 6), saplingOrange);
				InsolatorManager.addDefaultTreeRecipe(saplingYellow, new ItemStack(Blocks.LOG, 6), saplingYellow);
				InsolatorManager.addDefaultTreeRecipe(saplingFir, ItemHelper.cloneStack(logFir, 6), saplingFir);
			}

			/* TAPPER */
			{
				TapperManager.addItemMapping(logFir, new FluidStack(TFFluids.fluidResin, 20));
				TapperManager.addBlockStateMapping(getItemStack("fir_log", 1, 1), new FluidStack(TFFluids.fluidResin, 100));

				addLeafMapping(Blocks.LOG, 0, blockLeavesRed, 0);
				addLeafMapping(Blocks.LOG, 0, blockLeavesBrown, 0);
				addLeafMapping(Blocks.LOG, 0, blockLeavesOrange, 0);
				addLeafMapping(Blocks.LOG, 0, blockLeavesYellow, 0);

				addLeafMapping(getBlock("fir_log"), 1, blockLeavesFir, 0);
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
