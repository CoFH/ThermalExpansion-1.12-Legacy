package cofh.thermalexpansion.util.managers;

import cofh.lib.inventory.ComparableItemStack;
import cofh.thermalfoundation.item.ItemFertilizer;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class FisherManager {

	private static TObjectIntHashMap<ComparableItemStack> baitMap = new TObjectIntHashMap<>();

	public static boolean isValidBait(ItemStack stack) {

		return !stack.isEmpty() && baitMap.contains(new ComparableItemStack(stack));
	}

	public static ItemStack getFish() {

		return new ItemStack(Items.FISH, 1, 0);
	}

	public static int getBaitMultiplier(ItemStack stack) {

		if (stack.isEmpty()) {
			return 0;
		}
		return baitMap.get(new ComparableItemStack(stack));
	}

	public static void initialize() {

		/* BAIT */
		{
			addBait(ItemFertilizer.fertilizerBasic, 2);
			addBait(ItemFertilizer.fertilizerRich, 4);
			addBait(ItemFertilizer.fertilizerFlux, 5);
		}

		/* LOAD MAPPINGS */
		loadMappings();
	}

	public static void loadMappings() {

	}

	/* ADD MAPPING */

	public static void refresh() {

		TObjectIntHashMap<ComparableItemStack> tempBaitMap = new TObjectIntHashMap<>(baitMap.size());

		for (TObjectIntIterator<ComparableItemStack> it = baitMap.iterator(); it.hasNext(); ) {
			it.advance();
			tempBaitMap.put(new ComparableItemStack(it.key().toItemStack()), it.value());
		}
		baitMap.clear();

		baitMap = tempBaitMap;
	}

	/* HELPERS */
	private static void addBait(ItemStack bait, int multiplier) {

		baitMap.put(new ComparableItemStack(bait), multiplier);
	}

}
