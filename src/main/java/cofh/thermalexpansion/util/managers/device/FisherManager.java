package cofh.thermalexpansion.util.managers.device;

import cofh.core.inventory.ComparableItemStack;
import cofh.core.util.helpers.ItemHelper;
import cofh.core.util.helpers.MathHelper;
import cofh.thermalfoundation.item.ItemBait;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class FisherManager {

	private static List<ItemStack> fishList = new ArrayList<>();
	private static List<Integer> weightList = new ArrayList<>();
	private static int totalWeight;

	private static TObjectIntHashMap<ComparableItemStack> baitMap = new TObjectIntHashMap<>();

	public static boolean isValidBait(ItemStack stack) {

		return !stack.isEmpty() && baitMap.contains(new ComparableItemStack(stack));
	}

	public static ItemStack getFish() {

		int roll = MathHelper.RANDOM.nextInt(totalWeight);

		for (int i = 0; i < weightList.size(); i++) {
			roll -= weightList.get(i);

			if (roll < 0) {
				return fishList.get(i);
			}
		}
		return ItemStack.EMPTY;
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
			addBait(ItemBait.baitBasic, 2);
			addBait(ItemBait.baitRich, 3);
			addBait(ItemBait.baitFlux, 4);
		}

		/* FISH */
		{
			addFish(new ItemStack(Items.FISH, 1, 0), 2 * 60);
			addFish(new ItemStack(Items.FISH, 1, 1), 2 * 25);
			addFish(new ItemStack(Items.FISH, 1, 2), 2 * 2);
			addFish(new ItemStack(Items.FISH, 1, 3), 2 * 13);
		}

		/* LOAD MAPPINGS */
		loadMappings();
	}

	public static void loadMappings() {

	}

	/* ADD MAPPING */
	public static boolean addFish(ItemStack fish, int weight) {

		if (fish.isEmpty() || weight <= 0) {
			return false;
		}
		fishList.add(ItemHelper.cloneStack(fish, 1));
		weightList.add(weight);

		totalWeight += weight;

		return true;
	}

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
