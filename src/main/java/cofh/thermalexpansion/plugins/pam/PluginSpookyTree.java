package cofh.thermalexpansion.plugins.pam;

import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.plugins.PluginTEBase;
import cofh.thermalexpansion.util.managers.device.TapperManager;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager;
import cofh.thermalexpansion.util.managers.machine.SawmillManager;
import cofh.thermalfoundation.init.TFFluids;
import cofh.thermalfoundation.item.ItemMaterial;
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
	public void initializeDelegate() {

	}

	@Override
	public void registerDelegate() {

		ItemStack sapling = getItemStack("spookytree_sapling");
		ItemStack log = getItemStack("spookytree_log");
		ItemStack planks = getItemStack("spookytree_planks");
		// ItemStack halfslab = getItemStack("spookytree_halfslab");
		// ItemStack doubleslab = getItemStack("spookytree_doubleslab");
		ItemStack button = getItemStack("spookytree_button");
		ItemStack pressureplate = getItemStack("spookytree_pressureplate");
		ItemStack trapdoor = getItemStack("spookytree_trapdoor");
		ItemStack fence = getItemStack("spookytree_fence");
		ItemStack fencegate = getItemStack("spookytree_fencegate");
		ItemStack stairs = getItemStack("spookytree_stairs");

		Block blockLog = getBlock("spookytree_log");
		Block blockLeaves = getBlock("spookytree_leaves");

		/* SAWMILL */
		{
			int energy = SawmillManager.DEFAULT_ENERGY * 3 / 2;

			SawmillManager.addRecipe(energy / 2, ItemHelper.cloneStack(button, 2), planks, ItemMaterial.dustWood, 25);
			SawmillManager.addRecipe(energy, pressureplate, planks, ItemMaterial.dustWood, 50);
			SawmillManager.addRecipe(energy, ItemHelper.cloneStack(trapdoor, 2), planks, ItemMaterial.dustWood, 75);
			SawmillManager.addRecipe(energy, fence, planks, ItemMaterial.dustWood, 25);
			SawmillManager.addRecipe(energy, fencegate, planks, ItemMaterial.dustWood, 150);
			SawmillManager.addRecipe(energy, ItemHelper.cloneStack(stairs, 2), planks, ItemMaterial.dustWood, 25);
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
