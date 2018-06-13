package cofh.thermalexpansion.plugins.pam;

import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.plugins.PluginTEBase;
import cofh.thermalexpansion.util.managers.device.TapperManager;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager;
import cofh.thermalexpansion.util.managers.machine.SawmillManager;
import cofh.thermalfoundation.init.TFFluids;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class PluginSpookyTree extends PluginTEBase {

	public static final String MOD_ID = "spookytree";
	public static final String MOD_NAME = "Pam's Spooky Tree";

	public PluginSpookyTree() {

		super(MOD_ID, MOD_NAME);
	}

	@Override
	public void preInitDelegate() {

	}

	@Override
	public void initializeDelegate() {

		ItemStack sapling = getItemStack("spookytree_sapling");
		ItemStack log = getItemStack("spookytree_log");
		ItemStack planks = getItemStack("spookytree_planks");
		// ItemStack halfslab = getItemStack("spookytree_halfslab");
		// ItemStack doubleslab = getItemStack("spookytree_doubleslab");
		ItemStack button = getItemStack("spookytree_button");
		ItemStack fence = getItemStack("spookytree_fence");
		ItemStack fencegate = getItemStack("spookytree_fencegate");
		ItemStack pressureplate = getItemStack("spookytree_pressureplate");
		ItemStack stairs = getItemStack("spookytree_stairs");
		ItemStack trapdoor = getItemStack("spookytree_trapdoor");

		Block blockLog = getBlock("spookytree_log");
		Block blockLeaves = getBlock("spookytree_leaves");

		/* SAWMILL */
		{
			SawmillManager.addButtonRecipe(button, planks);
			SawmillManager.addFenceRecipe(fence, planks);
			SawmillManager.addFenceGateRecipe(fencegate, planks);
			SawmillManager.addPressurePlateRecipe(pressureplate, planks);
			SawmillManager.addStairsRecipe(stairs, planks);
			SawmillManager.addTrapdoorRecipe(trapdoor, planks);
		}

		/* INSOLATOR */
		{
			InsolatorManager.addDefaultTreeRecipe(sapling, ItemHelper.cloneStack(log, 6), sapling);
		}

		/* TAPPER */
		{
			TapperManager.addStandardMapping(log, new FluidStack(TFFluids.fluidResin, 50));

			addLeafMapping(blockLog, 0, blockLeaves, 0);
		}
	}

}
