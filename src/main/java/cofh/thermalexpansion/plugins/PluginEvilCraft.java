package cofh.thermalexpansion.plugins;

import cofh.core.util.ModPlugin;
import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.util.managers.device.TapperManager;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager;
import cofh.thermalexpansion.util.managers.machine.TransposerManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;

public class PluginEvilCraft extends ModPlugin {

	public static final String MOD_ID = "evilcraft";
	public static final String MOD_NAME = "EvilCraft";

	public PluginEvilCraft() {

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
			ItemStack logUndead = getItemStack("undead_log", 1, 0);

			ItemStack saplingUndead = getItemStack("undead_sapling", 1, 0);

			Block blockLogUndead = getBlock("undead_log");
			Block blockLeavesUndead = getBlock("undead_leaves");

			Fluid fluidBlood = FluidRegistry.getFluid("evilcraftblood");

			/* INSOLATOR */
			{
				int energy = InsolatorManager.DEFAULT_ENERGY;

				InsolatorManager.addDefaultTreeRecipe(energy * 2, saplingUndead, ItemHelper.cloneStack(logUndead, 6), new ItemStack(Blocks.DEADBUSH), 100);
			}

			/* TRANSPOSER */
			{
				int energy = 2400;

				if (fluidBlood != null) {
					TransposerManager.addExtractRecipe(energy, logUndead, getItemStack("hardened_blood_shard"), new FluidStack(fluidBlood, 100), 25, false);
				}
			}

			/* TAPPER */
			{
				if (fluidBlood != null) {
					TapperManager.addItemMapping(logUndead, new FluidStack(fluidBlood, 10));

					TapperManager.addBlockStateMapping(new ItemStack(blockLogUndead, 1, 1), new FluidStack(fluidBlood, 50));

					addLeafMapping(blockLogUndead, 1, blockLeavesUndead, 0);
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
