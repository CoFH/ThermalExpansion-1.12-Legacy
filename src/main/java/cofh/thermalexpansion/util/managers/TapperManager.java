package cofh.thermalexpansion.util.managers;

import cofh.lib.inventory.ComparableItemStack;
import cofh.lib.util.BlockWrapper;
import cofh.lib.util.ItemWrapper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalfoundation.init.TFFluids;
import cofh.thermalfoundation.item.ItemFertilizer;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraft.block.*;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class TapperManager {

	private static Map<BlockWrapper, FluidStack> blockMap = new THashMap<>();
	private static Map<ItemWrapper, FluidStack> itemMap = new THashMap<>();
	private static SetMultimap<BlockWrapper, BlockWrapper> leafMap = HashMultimap.create();
	private static TObjectIntHashMap<ComparableItemStack> fertilizerMap = new TObjectIntHashMap<>();

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

	public static Set<BlockWrapper> getLeaf(IBlockState state) {

		return leafMap.get(new BlockWrapper(state.getBlock(), state.getBlock().getMetaFromState(state)));
	}

	public static int getFertilizerMultiplier(ItemStack stack) {

		if (stack == null) {
			return 0;
		}
		return fertilizerMap.get(new ComparableItemStack(stack));
	}

	public static void addDefaultMappings() {

		FluidStack resin = new FluidStack(TFFluids.fluidResin, 25);

		/* FERTILIZER */
		{
			addFertilizer(ItemFertilizer.fertilizerBasic, 2);
			addFertilizer(ItemFertilizer.fertilizerRich, 4);
			addFertilizer(ItemFertilizer.fertilizerFlux, 6);
		}

		/* FLUIDS */
		{
			addMapping(new ItemStack(Blocks.LOG, 1, 0), new FluidStack(resin, 50));     // syrup
			addMapping(new ItemStack(Blocks.LOG, 1, 1), new FluidStack(resin, 100));
			addMapping(new ItemStack(Blocks.LOG, 1, 2), new FluidStack(resin, 50));
			addMapping(new ItemStack(Blocks.LOG, 1, 3), new FluidStack(resin, 50));     // rubber
			addMapping(new ItemStack(Blocks.LOG2, 1, 0), new FluidStack(resin, 50));    // rubber
			addMapping(new ItemStack(Blocks.LOG2, 1, 1), new FluidStack(resin, 50));    // syrup
		}

		/* LEAVES */
		{
			addVanillaLeafMappings(Blocks.LOG, BlockOldLog.VARIANT, Blocks.LEAVES, BlockOldLeaf.VARIANT);
			addVanillaLeafMappings(Blocks.LOG2, BlockNewLog.VARIANT, Blocks.LEAVES2, BlockNewLeaf.VARIANT);
		}
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
		HashMultimap<BlockWrapper, BlockWrapper> tempLeafMap = HashMultimap.create(leafMap.keySet().size(), leafMap.size() / leafMap.keySet().size());
		TObjectIntHashMap<ComparableItemStack> tempFertilizerMap = new TObjectIntHashMap<>(fertilizerMap.size());

		for (Entry<BlockWrapper, FluidStack> entry : blockMap.entrySet()) {
			BlockWrapper tempBlock = new BlockWrapper(entry.getKey().block, entry.getKey().metadata);
			tempBlockMap.put(tempBlock, entry.getValue());
		}
		for (Entry<ItemWrapper, FluidStack> entry : itemMap.entrySet()) {
			ItemWrapper tempItem = new ItemWrapper(entry.getKey().item, entry.getKey().metadata);
			tempItemMap.put(tempItem, entry.getValue());
		}
		for (Entry<BlockWrapper, BlockWrapper> entry : leafMap.entries()) {
			BlockWrapper tempLeaf = new BlockWrapper(entry.getKey().block, entry.getKey().metadata);
			tempLeafMap.put(tempLeaf, entry.getValue());
		}
		for (TObjectIntIterator<ComparableItemStack> it = fertilizerMap.iterator(); it.hasNext(); ) {
			it.advance();
			tempFertilizerMap.put(new ComparableItemStack(it.key().toItemStack()), it.value());
		}
		blockMap.clear();
		itemMap.clear();
		leafMap.clear();
		fertilizerMap.clear();

		blockMap = tempBlockMap;
		itemMap = tempItemMap;
		leafMap = tempLeafMap;
		fertilizerMap = tempFertilizerMap;
	}

	/* HELPERS */
	private static void addVanillaLeafMappings(Block logBlock, PropertyEnum<BlockPlanks.EnumType> logVariantProperty, Block leavesBlock, PropertyEnum<BlockPlanks.EnumType> leafVariantProperty) {

		for (BlockPlanks.EnumType variant : logVariantProperty.getAllowedValues()) {
			IBlockState logState = logBlock.getDefaultState().withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.Y).withProperty(logVariantProperty, variant);

			for (Boolean check_decay : BlockLeaves.CHECK_DECAY.getAllowedValues()) {
				IBlockState leafState = leavesBlock.getDefaultState().withProperty(leafVariantProperty, variant).withProperty(BlockLeaves.DECAYABLE, Boolean.TRUE).withProperty(BlockLeaves.CHECK_DECAY, check_decay);
				leafMap.put(new BlockWrapper(logState), new BlockWrapper(leafState));
			}
		}
	}

	private static void addFertilizer(ItemStack fertilizer, int multiplier) {

		fertilizerMap.put(new ComparableItemStack(fertilizer), multiplier);
	}

}
