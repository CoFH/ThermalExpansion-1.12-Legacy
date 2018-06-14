package cofh.thermalexpansion.plugins;

import cofh.thermalexpansion.util.managers.machine.SawmillManager;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class PluginPrimalCore extends PluginTEBase {

	public static final String MOD_ID = "primal";
	public static final String MOD_NAME = "Primal Core";

	public PluginPrimalCore() {

		super(MOD_ID, MOD_NAME);
	}

	@Override
	public void initializeDelegate() {

		ItemStack plankIronwood = getItemStack("planks", 1, 0);
		ItemStack plankYew = getItemStack("planks", 1, 1);
		ItemStack plankLacquer = getItemStack("planks", 1, 2);
		ItemStack plankCorphyry = getItemStack("planks", 1, 3);

		/* SAWMILL */
		{
			SawmillManager.addBoatRecipe(getItemStack("boat_ironwood"), plankIronwood);
			SawmillManager.addBoatRecipe(getItemStack("boat_yew"), plankYew);
			SawmillManager.addBoatRecipe(getItemStack("boat_lacquer"), plankLacquer);
			SawmillManager.addBoatRecipe(getItemStack("boat_corypha"), plankCorphyry);

			SawmillManager.addDoorRecipe(getItemStack("door_ironwood"), plankIronwood);
			SawmillManager.addDoorRecipe(getItemStack("door_yew"), plankYew);
			// SawmillManager.addDoorRecipe(getItemStack("door_lacquer"), plankLacquer);
			SawmillManager.addDoorRecipe(getItemStack("door_corypha"), plankCorphyry);

			SawmillManager.addFenceRecipe(getItemStack("fence", 1, 0), plankIronwood);
			SawmillManager.addFenceRecipe(getItemStack("fence", 1, 1), plankYew);
			SawmillManager.addFenceRecipe(getItemStack("fence", 1, 2), plankLacquer);
			SawmillManager.addFenceRecipe(getItemStack("fence", 1, 3), plankCorphyry);

			SawmillManager.addFenceGateRecipe(getItemStack("gate_ironwood"), plankIronwood);
			SawmillManager.addFenceGateRecipe(getItemStack("gate_yew"), plankYew);
			// SawmillManager.addFenceGateRecipe(getItemStack("gate_lacquer"), plankLacquer);
			SawmillManager.addFenceGateRecipe(getItemStack("gate_corypha"), plankCorphyry);

			SawmillManager.addLogRecipe(new ItemStack(Blocks.LOG, 1, 0), new ItemStack(Blocks.PLANKS, 1, 0));
			SawmillManager.addLogRecipe(new ItemStack(Blocks.LOG, 1, 1), new ItemStack(Blocks.PLANKS, 1, 1));
			SawmillManager.addLogRecipe(new ItemStack(Blocks.LOG, 1, 2), new ItemStack(Blocks.PLANKS, 1, 2));
			SawmillManager.addLogRecipe(new ItemStack(Blocks.LOG, 1, 3), new ItemStack(Blocks.PLANKS, 1, 3));
			SawmillManager.addLogRecipe(new ItemStack(Blocks.LOG2, 1, 0), new ItemStack(Blocks.PLANKS, 1, 4));
			SawmillManager.addLogRecipe(new ItemStack(Blocks.LOG2, 1, 1), new ItemStack(Blocks.PLANKS, 1, 5));

			SawmillManager.addLogRecipe(getItemStack("logs_stripped", 1, 0), new ItemStack(Blocks.PLANKS, 1, 0));
			SawmillManager.addLogRecipe(getItemStack("logs_stripped", 1, 1), new ItemStack(Blocks.PLANKS, 1, 1));
			SawmillManager.addLogRecipe(getItemStack("logs_stripped", 1, 2), new ItemStack(Blocks.PLANKS, 1, 2));
			SawmillManager.addLogRecipe(getItemStack("logs_stripped", 1, 3), new ItemStack(Blocks.PLANKS, 1, 3));
			SawmillManager.addLogRecipe(getItemStack("logs_stripped", 1, 4), new ItemStack(Blocks.PLANKS, 1, 4));
			SawmillManager.addLogRecipe(getItemStack("logs_stripped", 1, 5), new ItemStack(Blocks.PLANKS, 1, 5));
			SawmillManager.addLogRecipe(getItemStack("logs_stripped", 1, 6), plankIronwood);
			SawmillManager.addLogRecipe(getItemStack("logs_stripped", 1, 7), plankYew);

			SawmillManager.addStairsRecipe(getItemStack("stairs_ironwood"), plankIronwood);
			// SawmillManager.addStairsRecipe(getItemStack("stairs_yew"), plankYew);
			SawmillManager.addStairsRecipe(getItemStack("stairs_lacquer"), plankLacquer);
			SawmillManager.addStairsRecipe(getItemStack("stairs_corypha"), plankCorphyry);

			SawmillManager.addWorkbenchRecipe(getItemStack("worktable_shelf", 1, 0), new ItemStack(Blocks.PLANKS, 1, 0));
			SawmillManager.addWorkbenchRecipe(getItemStack("worktable_shelf", 1, 1), new ItemStack(Blocks.PLANKS, 1, 1));
			SawmillManager.addWorkbenchRecipe(getItemStack("worktable_shelf", 1, 2), new ItemStack(Blocks.PLANKS, 1, 2));
			SawmillManager.addWorkbenchRecipe(getItemStack("worktable_shelf", 1, 3), new ItemStack(Blocks.PLANKS, 1, 3));
			SawmillManager.addWorkbenchRecipe(getItemStack("worktable_shelf", 1, 4), new ItemStack(Blocks.PLANKS, 1, 4));
			SawmillManager.addWorkbenchRecipe(getItemStack("worktable_shelf", 1, 5), new ItemStack(Blocks.PLANKS, 1, 5));
			SawmillManager.addWorkbenchRecipe(getItemStack("worktable_shelf", 1, 6), plankIronwood);
			SawmillManager.addWorkbenchRecipe(getItemStack("worktable_shelf", 1, 7), plankYew);
			SawmillManager.addWorkbenchRecipe(getItemStack("worktable_shelf", 1, 9), plankCorphyry);
		}

		/* FISHER */
		{

		}
	}

}
