package cofh.thermalexpansion.plugins;

import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.util.managers.TapperManager;
import cofh.thermalexpansion.util.managers.machine.CompactorManager;
import cofh.thermalexpansion.util.managers.machine.CompactorManager.Mode;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager.Type;
import cofh.thermalfoundation.item.ItemMaterial;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class IndustrialCraftPlugin {

	private IndustrialCraftPlugin() {

	}

	public static final String MOD_ID = "ic2";
	public static final String MOD_NAME = "IndustrialCraft 2";

	public static void initialize() {

		String category = "Plugins";
		String comment = "If TRUE, support for " + MOD_NAME + " is enabled.";

		boolean enable = ThermalExpansion.CONFIG.getConfiguration().getBoolean(MOD_NAME, category, true, comment);

		if (!enable || !Loader.isModLoaded(MOD_ID)) {
			return;
		}
		try {
			/* INSOLATOR */
			{
				ItemStack logRubber = getBlockStack("rubber_wood", 1, 0);
				ItemStack saplingRubber = getItem("sapling", 1, 0);

				InsolatorManager.addDefaultTreeRecipe(saplingRubber, ItemHelper.cloneStack(logRubber, 4), saplingRubber, 50, false, Type.TREE);
			}

			/* COMPACTOR */
			{
				ItemStack coalBall = getItem("crafting", 1, 16);
				ItemStack coalBallCompressed = getItem("crafting", 1, 17);
				ItemStack coalChunk = getItem("crafting", 1, 18);

				int energy = CompactorManager.DEFAULT_ENERGY;

				CompactorManager.addRecipe(energy, coalBall, coalBallCompressed, Mode.PRESS);
				CompactorManager.addRecipe(energy, coalBallCompressed, coalChunk, Mode.PRESS);
				CompactorManager.addRecipe(energy, coalChunk, ItemMaterial.gemDiamond, Mode.PRESS);
			}

			ThermalExpansion.LOG.info("Thermal Expansion: " + MOD_NAME + " Plugin Enabled.");
		} catch (Throwable t) {
			ThermalExpansion.LOG.error("Thermal Expansion: " + MOD_NAME + " Plugin encountered an error:", t);
		}
	}

	/* HELPERS */
	private static ItemStack getBlockStack(String name, int amount, int meta) {

		Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(MOD_ID + ":" + name));
		return block != null ? new ItemStack(block, amount, meta) : ItemStack.EMPTY;
	}

	private static ItemStack getBlockStack(String name, int amount) {

		return getBlockStack(name, amount, 0);
	}

	private static Block getBlock(String name) {

		return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(MOD_ID + ":" + name));
	}

	private static ItemStack getItem(String name, int amount, int meta) {

		Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MOD_ID + ":" + name));
		return item != null ? new ItemStack(item, amount, meta) : ItemStack.EMPTY;
	}

	private static ItemStack getItem(String name) {

		return getItem(name, 1, 0);
	}

	private static void addLeafMapping(Block logBlock, Block leafBlock, int metadata) {

		IBlockState logState = logBlock.getStateFromMeta(metadata);

		for (Boolean check_decay : BlockLeaves.CHECK_DECAY.getAllowedValues()) {
			IBlockState leafState = leafBlock.getStateFromMeta(metadata).withProperty(BlockLeaves.DECAYABLE, Boolean.TRUE).withProperty(BlockLeaves.CHECK_DECAY, check_decay);
			TapperManager.addLeafMappingDirect(logState, leafState);
		}
	}

}
