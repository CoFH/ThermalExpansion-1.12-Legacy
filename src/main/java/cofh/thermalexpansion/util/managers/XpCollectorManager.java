package cofh.thermalexpansion.util.managers;

import cofh.core.inventory.ComparableItemStack;
import cofh.thermalfoundation.item.ItemMaterial;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class XpCollectorManager {

	private static TObjectIntHashMap<ComparableItemStack> catalystMap = new TObjectIntHashMap<>();
	private static TObjectIntHashMap<ComparableItemStack> catalystFactorMap = new TObjectIntHashMap<>();

	public static final int XP_CONVERSION = 20;

	public static int getCatalystXp(ItemStack stack) {

		if (stack.isEmpty()) {
			return 0;
		}
		return catalystMap.get(new ComparableItemStack(stack));
	}

	public static int getCatalystFactor(ItemStack stack) {

		if (stack.isEmpty()) {
			return 0;
		}
		return catalystFactorMap.get(new ComparableItemStack(stack));
	}

	public static void initialize() {

		/* CATALYSTS */
		{
			addCatalyst(new ItemStack(Blocks.SOUL_SAND), 200, 50);
			addCatalyst(new ItemStack(Items.DYE, 1, 4), 400, 100);
			addCatalyst(ItemMaterial.dustMana, 800, 150);
		}
	}

	public static void refresh() {

		TObjectIntHashMap<ComparableItemStack> tempCatalystMap = new TObjectIntHashMap<>(catalystMap.size());
		TObjectIntHashMap<ComparableItemStack> tempCatalystFactorMap = new TObjectIntHashMap<>(catalystFactorMap.size());

		for (TObjectIntIterator<ComparableItemStack> it = catalystMap.iterator(); it.hasNext(); ) {
			it.advance();
			tempCatalystMap.put(new ComparableItemStack(it.key().toItemStack()), it.value());
		}
		for (TObjectIntIterator<ComparableItemStack> it = catalystFactorMap.iterator(); it.hasNext(); ) {
			it.advance();
			tempCatalystFactorMap.put(new ComparableItemStack(it.key().toItemStack()), it.value());
		}
		catalystMap.clear();
		catalystFactorMap.clear();

		catalystMap = tempCatalystMap;
		catalystFactorMap = tempCatalystFactorMap;
	}

	/* HELPERS */
	private static void addCatalyst(ItemStack catalyst, int xp, int factor) {

		catalystMap.put(new ComparableItemStack(catalyst), xp);
		catalystFactorMap.put(new ComparableItemStack(catalyst), factor);
	}

	private static void removeCatalyst(ItemStack catalyst) {

		catalystMap.remove(new ComparableItemStack((catalyst)));
		catalystFactorMap.remove(new ComparableItemStack((catalyst)));
	}

}
