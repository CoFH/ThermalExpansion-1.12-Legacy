package cofh.thermalexpansion.util.crafting;

import cofh.lib.util.BlockWrapper;
import cofh.lib.util.ItemWrapper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalfoundation.init.TFFluids;
import gnu.trove.map.hash.THashMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.Map;
import java.util.Map.Entry;

public class TapperManager {

	private static Map<BlockWrapper, FluidStack> blockMap = new THashMap<>();
	private static Map<ItemWrapper, FluidStack> itemMap = new THashMap<>();
	private static Map<BlockWrapper, BlockWrapper> leafMap = new THashMap<>();

	public static FluidStack getFluid(IBlockState state) {

		return blockMap.get(new BlockWrapper(state.getBlock(), state.getBlock().getMetaFromState(state)));
	}

	public static boolean mappingExists(IBlockState state) {

		return getFluid(state) != null;
	}

	public static FluidStack getFluid(ItemStack stack) {

		return stack == null ? null : itemMap.get(new ItemWrapper(stack.getItem(), ItemHelper.getItemDamage(stack)));
	}

	public static boolean mappingExists(ItemStack stack) {

		return getFluid(stack) != null;
	}

	public static BlockWrapper getLeaf(IBlockState state) {

		return leafMap.get(new BlockWrapper(state.getBlock(), state.getBlock().getMetaFromState(state)));
	}

	public static void addDefaultMappings() {

		FluidStack resin = new FluidStack(TFFluids.fluidResin, 25);

		addMapping(new ItemStack(Blocks.LOG, 1, 0), new FluidStack(resin, 25));
		addMapping(new ItemStack(Blocks.LOG, 1, 1), new FluidStack(resin, 50));
		addMapping(new ItemStack(Blocks.LOG, 1, 2), new FluidStack(resin, 25));     // syrup
		addMapping(new ItemStack(Blocks.LOG, 1, 3), new FluidStack(resin, 25));     // rubber
		addMapping(new ItemStack(Blocks.LOG2, 1, 0), new FluidStack(resin, 25));    // rubber
		addMapping(new ItemStack(Blocks.LOG2, 1, 1), new FluidStack(resin, 25));

		addLeafMapping(new ItemStack(Blocks.LOG, 1, 0), new ItemStack(Blocks.LEAVES, 1, 0));
		addLeafMapping(new ItemStack(Blocks.LOG, 1, 1), new ItemStack(Blocks.LEAVES, 1, 1));
		addLeafMapping(new ItemStack(Blocks.LOG, 1, 2), new ItemStack(Blocks.LEAVES, 1, 2));
		addLeafMapping(new ItemStack(Blocks.LOG, 1, 3), new ItemStack(Blocks.LEAVES, 1, 3));
		addLeafMapping(new ItemStack(Blocks.LOG2, 1, 0), new ItemStack(Blocks.LEAVES2, 1, 0));
		addLeafMapping(new ItemStack(Blocks.LOG2, 1, 1), new ItemStack(Blocks.LEAVES2, 1, 1));
	}

	public static void loadMappings() {

	}

	/* ADD MAPPING */
	public static boolean addMapping(ItemStack item, FluidStack fluid) {

		if (item == null || fluid == null) {
			return false;
		}
		if (item.getItem() instanceof ItemBlock) {
			blockMap.put(new BlockWrapper(((ItemBlock) item.getItem()).getBlock(), ItemHelper.getItemDamage(item)), fluid.copy());
		}
		itemMap.put(new ItemWrapper(item.getItem(), ItemHelper.getItemDamage(item)), fluid.copy());
		return true;
	}

	public static boolean addLeafMapping(ItemStack log, ItemStack leaf) {

		if (log == null || leaf == null) {
			return false;
		}
		leafMap.put(new BlockWrapper(((ItemBlock) log.getItem()).getBlock(), ItemHelper.getItemDamage(log)), new BlockWrapper(((ItemBlock) leaf.getItem()).getBlock(), ItemHelper.getItemDamage(leaf)));
		return true;
	}

	public static void refreshMappings() {

		Map<BlockWrapper, FluidStack> tempBlockMap = new THashMap<>(blockMap.size());
		Map<ItemWrapper, FluidStack> tempItemMap = new THashMap<>(itemMap.size());
		Map<BlockWrapper, BlockWrapper> tempLeafMap = new THashMap<>(leafMap.size());

		for (Entry<BlockWrapper, FluidStack> entry : blockMap.entrySet()) {
			BlockWrapper tempBlock = new BlockWrapper(entry.getKey().block, entry.getKey().metadata);
			tempBlockMap.put(tempBlock, entry.getValue());
		}
		for (Entry<ItemWrapper, FluidStack> entry : itemMap.entrySet()) {
			ItemWrapper tempItem = new ItemWrapper(entry.getKey().item, entry.getKey().metadata);
			tempItemMap.put(tempItem, entry.getValue());
		}
		for (Entry<BlockWrapper, BlockWrapper> entry : leafMap.entrySet()) {
			BlockWrapper tempLeaf = new BlockWrapper(entry.getKey().block, entry.getKey().metadata);
			tempLeafMap.put(tempLeaf, entry.getValue());
		}
		blockMap.clear();
		itemMap.clear();
		leafMap.clear();

		blockMap = tempBlockMap;
		itemMap = tempItemMap;
		leafMap = tempLeafMap;
	}

}
