package cofh.thermalexpansion.plugins;

import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.util.managers.device.TapperManager;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager;
import cofh.thermalexpansion.util.managers.machine.SawmillManager;
import cofh.thermalfoundation.init.TFFluids;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class PluginTraverse extends PluginTEBase {

	public static final String MOD_ID = "traverse";
	public static final String MOD_NAME = "Traverse";

	public PluginTraverse() {

		super(MOD_ID, MOD_NAME);
	}

	@Override
	public void initializeDelegate() {

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
			SawmillManager.addDoorRecipe(getItemStack("fir_door"), planksFir);
			SawmillManager.addFenceRecipe(getItemStack("fir_fence"), planksFir);
			SawmillManager.addFenceGateRecipe(getItemStack("fir_fence_gate"), planksFir);
			SawmillManager.addStairsRecipe(getItemStack("fir_stairs"), planksFir);
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
	}

}
