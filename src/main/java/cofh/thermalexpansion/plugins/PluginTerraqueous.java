package cofh.thermalexpansion.plugins;

import cofh.core.util.ModPlugin;
import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.util.managers.TapperManager;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager;
import cofh.thermalexpansion.util.managers.machine.SawmillManager;
import cofh.thermalfoundation.init.TFFluids;
import cofh.thermalfoundation.item.ItemMaterial;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;

public class PluginTerraqueous extends ModPlugin {

	public static final String MOD_ID = "terraqueous";
	public static final String MOD_NAME = "Terraqueous";

	public PluginTerraqueous() {

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
			ItemStack[] plank = new ItemStack[10];
			ItemStack[] sapling = new ItemStack[10];
			ItemStack[] door = new ItemStack[10];
			ItemStack[] trapdoor = new ItemStack[10];
			ItemStack[] gate = new ItemStack[10];
			ItemStack[] fence = new ItemStack[10];
			ItemStack[] stairs = new ItemStack[10];
			ItemStack[] slab = new ItemStack[10];

			ItemStack[] flowersCluster = new ItemStack[16];
			ItemStack[] flowersSingle = new ItemStack[16];

			ItemStack[] log = new ItemStack[10];
			ItemStack[] fruit = new ItemStack[10];

			Block blockLog1 = getBlock("trunk1");
			Block blockLog2 = getBlock("trunk2");

			Block blockLeaves1 = getBlock("foliage1");
			Block blockLeaves2 = getBlock("foliage2");
			Block blockLeaves3 = getBlock("foliage3");
			Block blockLeaves4 = getBlock("foliage4");
			Block blockLeaves5 = getBlock("foliage5");

			for (int i = 0; i < plank.length; i++) {
				plank[i] = getItemStack("planks", 1, i);
				sapling[i] = getItemStack("sapling", 1, i);

				door[i] = getItemStack("type_earth", 1, i);
				trapdoor[i] = getItemStack("type_earth", 1, i + 10);
				gate[i] = getItemStack("type_earth", 1, i + 20);
				fence[i] = getItemStack("type_earth", 1, i + 30);
				stairs[i] = getItemStack("type_earth", 1, i + 40);
				slab[i] = getItemStack("type_earth", 1, i + 50);
			}
			for (int i = 0; i < flowersCluster.length; i++) {
				flowersCluster[i] = getItemStack("flowers_cluster", 1, i);
				flowersSingle[i] = getItemStack("flowers_single", 1, i);
			}
			for (int i = 0; i < 5; i++) {
				log[i] = getItemStack("trunk1", i);
				log[i + 5] = getItemStack("trunk2", i);
			}
			for (int i = 1; i < fruit.length; i++) {
				fruit[i] = getItemStack("multifood", i - 1);
			}
			fruit[0] = new ItemStack(Items.APPLE);

			/* SAWMILL */
			{
				int energy = SawmillManager.DEFAULT_ENERGY * 3 / 2;

				for (int i = 0; i < plank.length; i++) {
					SawmillManager.addRecipe(energy, door[i], plank[i], ItemMaterial.dustWood, 50);
					SawmillManager.addRecipe(energy, ItemHelper.cloneStack(trapdoor[i], 2), plank[i], ItemMaterial.dustWood, 75);
					SawmillManager.addRecipe(energy, gate[i], plank[i], ItemMaterial.dustWood, 150);
					SawmillManager.addRecipe(energy, fence[i], plank[i], ItemMaterial.dustWood, 25);
					SawmillManager.addRecipe(energy, ItemHelper.cloneStack(stairs[i], 2), plank[i], ItemMaterial.dustWood, 25);
				}
			}

			/* INSOLATOR */
			{
				for (int i = 0; i < sapling.length; i++) {
					InsolatorManager.addDefaultTreeRecipe(sapling[i], ItemHelper.cloneStack(log[i], 4), sapling[i]);
				}
				for (int i = 0; i < flowersCluster.length; i++) {
					InsolatorManager.addDefaultRecipe(flowersCluster[i], ItemHelper.cloneStack(flowersSingle[i], 6), ItemStack.EMPTY, 0);
					InsolatorManager.addDefaultRecipe(flowersSingle[i], flowersCluster[i], ItemStack.EMPTY, 0);
				}
				InsolatorManager.addDefaultRecipe(getItemStack("multifood", 1, 9), ItemHelper.cloneStack(getItemStack("multifood", 1, 9), 3), ItemStack.EMPTY, 0);
				InsolatorManager.addDefaultRecipe(getItemStack("plants", 1, 3), ItemHelper.cloneStack(getItemStack("multifood", 1, 10), 2), ItemStack.EMPTY, 0);
			}

			/* TAPPER */
			{
				TapperManager.addItemMapping(log[0], new FluidStack(TFFluids.fluidResin, 10));
				TapperManager.addItemMapping(log[1], new FluidStack(TFFluids.fluidSap, 10));
				TapperManager.addItemMapping(log[2], new FluidStack(TFFluids.fluidResin, 10));
				TapperManager.addItemMapping(log[3], new FluidStack(TFFluids.fluidResin, 10));
				TapperManager.addItemMapping(log[4], new FluidStack(TFFluids.fluidResin, 10));
				TapperManager.addItemMapping(log[5], new FluidStack(TFFluids.fluidResin, 10));
				TapperManager.addItemMapping(log[6], new FluidStack(TFFluids.fluidResin, 10));
				TapperManager.addItemMapping(log[7], new FluidStack(TFFluids.fluidResin, 10));
				TapperManager.addItemMapping(log[8], new FluidStack(TFFluids.fluidResin, 10));
				TapperManager.addItemMapping(log[9], new FluidStack(TFFluids.fluidResin, 10));

				TapperManager.addBlockStateMapping(new ItemStack(blockLog1, 1, 0), new FluidStack(TFFluids.fluidSap, 50));
				TapperManager.addBlockStateMapping(new ItemStack(blockLog1, 1, 3), new FluidStack(TFFluids.fluidSap, 50));
				TapperManager.addBlockStateMapping(new ItemStack(blockLog1, 1, 6), new FluidStack(TFFluids.fluidSap, 50));
				TapperManager.addBlockStateMapping(new ItemStack(blockLog1, 1, 9), new FluidStack(TFFluids.fluidSap, 50));
				TapperManager.addBlockStateMapping(new ItemStack(blockLog1, 1, 12), new FluidStack(TFFluids.fluidSap, 50));
				TapperManager.addBlockStateMapping(new ItemStack(blockLog2, 1, 0), new FluidStack(TFFluids.fluidSap, 50));
				TapperManager.addBlockStateMapping(new ItemStack(blockLog2, 1, 3), new FluidStack(TFFluids.fluidSap, 50));
				TapperManager.addBlockStateMapping(new ItemStack(blockLog2, 1, 6), new FluidStack(TFFluids.fluidSap, 50));
				TapperManager.addBlockStateMapping(new ItemStack(blockLog2, 1, 9), new FluidStack(TFFluids.fluidSap, 50));
				TapperManager.addBlockStateMapping(new ItemStack(blockLog2, 1, 12), new FluidStack(TFFluids.fluidSap, 50));

				addLeafMapping(blockLog1, 0, blockLeaves1, 0);
				addLeafMapping(blockLog1, 0, blockLeaves1, 1);
				addLeafMapping(blockLog1, 0, blockLeaves1, 2);

				addLeafMapping(blockLog1, 3, blockLeaves1, 4);
				addLeafMapping(blockLog1, 3, blockLeaves1, 5);
				addLeafMapping(blockLog1, 3, blockLeaves1, 6);

				addLeafMapping(blockLog1, 6, blockLeaves2, 0);
				addLeafMapping(blockLog1, 6, blockLeaves2, 1);
				addLeafMapping(blockLog1, 6, blockLeaves2, 2);

				addLeafMapping(blockLog1, 9, blockLeaves2, 4);
				addLeafMapping(blockLog1, 9, blockLeaves2, 5);
				addLeafMapping(blockLog1, 9, blockLeaves2, 6);

				addLeafMapping(blockLog1, 12, blockLeaves3, 0);
				addLeafMapping(blockLog1, 12, blockLeaves3, 1);
				addLeafMapping(blockLog1, 12, blockLeaves3, 2);

				addLeafMapping(blockLog2, 0, blockLeaves3, 4);
				addLeafMapping(blockLog2, 0, blockLeaves3, 5);
				addLeafMapping(blockLog2, 0, blockLeaves3, 6);

				addLeafMapping(blockLog2, 3, blockLeaves4, 0);
				addLeafMapping(blockLog2, 3, blockLeaves4, 1);
				addLeafMapping(blockLog2, 3, blockLeaves4, 2);

				addLeafMapping(blockLog2, 6, blockLeaves4, 4);
				addLeafMapping(blockLog2, 6, blockLeaves4, 5);
				addLeafMapping(blockLog2, 6, blockLeaves4, 6);

				addLeafMapping(blockLog2, 9, blockLeaves5, 0);
				addLeafMapping(blockLog2, 9, blockLeaves5, 1);
				addLeafMapping(blockLog2, 9, blockLeaves5, 2);

				addLeafMapping(blockLog2, 12, blockLeaves5, 4);
				addLeafMapping(blockLog2, 12, blockLeaves5, 5);
				addLeafMapping(blockLog2, 12, blockLeaves5, 6);
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
