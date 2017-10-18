package cofh.thermalexpansion.util.managers;

import cofh.core.inventory.ComparableItemStack;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class DiffuserManager {

	private static TObjectIntHashMap<ComparableItemStack> reagentAmpMap = new TObjectIntHashMap<>();
	private static TObjectIntHashMap<ComparableItemStack> reagentDurMap = new TObjectIntHashMap<>();

	public static boolean isValidReagent(ItemStack stack) {

		return reagentAmpMap.containsKey(new ComparableItemStack(stack));
	}

	public static int getReagentAmplifier(ItemStack stack) {

		if (stack.isEmpty()) {
			return 0;
		}
		return reagentAmpMap.get(new ComparableItemStack(stack));
	}

	public static int getReagentDuration(ItemStack stack) {

		if (stack.isEmpty()) {
			return 0;
		}
		return reagentDurMap.get(new ComparableItemStack(stack));
	}

	public static void initialize() {

		/* CATALYSTS */
		{
			addReagent(new ItemStack(Items.REDSTONE), 0, 1);
			addReagent(new ItemStack(Items.GLOWSTONE_DUST), 1, 0);
		}
	}

	public static void refresh() {

		TObjectIntHashMap<ComparableItemStack> tempAmpMap = new TObjectIntHashMap<>(reagentAmpMap.size());
		TObjectIntHashMap<ComparableItemStack> tempDurMap = new TObjectIntHashMap<>(reagentDurMap.size());

		for (TObjectIntIterator<ComparableItemStack> it = reagentAmpMap.iterator(); it.hasNext(); ) {
			it.advance();
			tempAmpMap.put(new ComparableItemStack(it.key().toItemStack()), it.value());
		}
		for (TObjectIntIterator<ComparableItemStack> it = reagentDurMap.iterator(); it.hasNext(); ) {
			it.advance();
			tempDurMap.put(new ComparableItemStack(it.key().toItemStack()), it.value());
		}
		reagentAmpMap.clear();
		reagentDurMap.clear();

		reagentAmpMap = tempAmpMap;
		reagentDurMap = tempDurMap;
	}

	/* HELPERS */
	private static void addReagent(ItemStack reagent, int amp, int dur) {

		reagentAmpMap.put(new ComparableItemStack(reagent), amp);
		reagentDurMap.put(new ComparableItemStack(reagent), dur);
	}

	private static void removeReagent(ItemStack reagent) {

		reagentAmpMap.remove(new ComparableItemStack((reagent)));
		reagentDurMap.remove(new ComparableItemStack((reagent)));
	}

}
