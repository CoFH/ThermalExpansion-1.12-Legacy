package cofh.thermalexpansion.plugins.pam;

import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.plugins.PluginTEBase;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class PluginRedbudTree extends PluginTEBase {

	public static final String MOD_ID = "redbudtree";
	public static final String MOD_NAME = "Pam's Redbud Tree";

	public PluginRedbudTree() {

		super(MOD_ID, MOD_NAME);
	}

	@Override
	public void preInitDelegate() {

	}

	@Override
	public void initializeDelegate() {

		ItemStack sapling = getItemStack("redbudtree_sapling");

		Block blockLeaves = getBlock("redbudtree_leaves");

		/* INSOLATOR */
		{
			InsolatorManager.addDefaultTreeRecipe(sapling, ItemHelper.cloneStack(Blocks.LOG, 6), sapling);
		}

		/* TAPPER */
		{
			addLeafMapping(Blocks.LOG, 0, blockLeaves, 0);
		}
	}

}
