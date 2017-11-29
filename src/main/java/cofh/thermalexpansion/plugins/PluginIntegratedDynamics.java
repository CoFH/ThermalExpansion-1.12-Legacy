package cofh.thermalexpansion.plugins;

import cofh.core.util.ModPlugin;
import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.util.managers.TapperManager;
import cofh.thermalexpansion.util.managers.machine.CrucibleManager;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager;
import cofh.thermalexpansion.util.managers.machine.TransposerManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;

public class PluginIntegratedDynamics extends ModPlugin {

	public static final String MOD_ID = "integrateddynamics";
	public static final String MOD_NAME = "Integrated Dynamics";

	public PluginIntegratedDynamics() {

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

			ItemStack blockMenril = getItemStack("crystalized_menril_block", 1, 0);
			ItemStack blockChorus = getItemStack("crystalized_chorus_block", 1, 0);

			ItemStack logMenril = getItemStack("menril_log", 1, 0);

			ItemStack saplingMenril = getItemStack("menril_sapling", 1, 0);

			Block blockLogMenril = getBlock("menril_log");
			Block blockLogMenrilFilled = getBlock("menril_log_filled");
			Block blockLeavesMenril = getBlock("menril_leaves");

			Fluid fluidMenril = FluidRegistry.getFluid("menrilresin");
			Fluid fluidChorus = FluidRegistry.getFluid("liquidchorus");

			/* INSOLATOR */
			{
				int energy = InsolatorManager.DEFAULT_ENERGY;

				InsolatorManager.addDefaultTreeRecipe(energy * 2, saplingMenril, ItemHelper.cloneStack(logMenril, 6), saplingMenril, 100);
			}

			/* CRUCIBLE */
			{
				int energy = CrucibleManager.DEFAULT_ENERGY;

				if (fluidMenril != null) {
					CrucibleManager.addRecipe(energy / 2, blockMenril, new FluidStack(fluidMenril, Fluid.BUCKET_VOLUME));
				}
				if (fluidChorus != null) {
					CrucibleManager.addRecipe(energy / 2, blockChorus, new FluidStack(fluidChorus, Fluid.BUCKET_VOLUME));
				}
			}

			/* TRANSPOSER */
			{
				int energy = 2400;

				if (fluidMenril != null) {
					TransposerManager.addExtractRecipe(energy, logMenril, ItemStack.EMPTY, new FluidStack(fluidMenril, 1000), 0, false);
				}
				if (fluidChorus != null) {
					TransposerManager.addExtractRecipe(energy, new ItemStack(Items.CHORUS_FRUIT_POPPED), ItemStack.EMPTY, new FluidStack(fluidChorus, 125), 0, false);
				}
			}

			/* TAPPER */
			{
				if (fluidMenril != null) {
					TapperManager.addItemMapping(logMenril, new FluidStack(fluidMenril, 100));

					TapperManager.addBlockStateMapping(new ItemStack(blockLogMenril, 1, 1), new FluidStack(fluidMenril, 100));
					TapperManager.addBlockStateMapping(new ItemStack(blockLogMenrilFilled, 1, 4), new FluidStack(fluidMenril, 100));
					TapperManager.addBlockStateMapping(new ItemStack(blockLogMenrilFilled, 1, 5), new FluidStack(fluidMenril, 100));
					TapperManager.addBlockStateMapping(new ItemStack(blockLogMenrilFilled, 1, 6), new FluidStack(fluidMenril, 100));
					TapperManager.addBlockStateMapping(new ItemStack(blockLogMenrilFilled, 1, 7), new FluidStack(fluidMenril, 100));

					addLeafMapping(blockLogMenril, 1, blockLeavesMenril, 0);
					addLeafMapping(blockLogMenrilFilled, 4, blockLeavesMenril, 0);
					addLeafMapping(blockLogMenrilFilled, 5, blockLeavesMenril, 0);
					addLeafMapping(blockLogMenrilFilled, 6, blockLeavesMenril, 0);
					addLeafMapping(blockLogMenrilFilled, 7, blockLeavesMenril, 0);
				}
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
