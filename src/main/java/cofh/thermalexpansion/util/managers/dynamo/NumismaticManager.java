package cofh.thermalexpansion.util.managers.dynamo;

import cofh.core.inventory.ComparableItemStack;
import com.google.common.collect.ImmutableSet;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraft.item.ItemStack;

import java.util.Set;

public class NumismaticManager {

	private static TObjectIntHashMap<ComparableItemStack> fuelMap = new TObjectIntHashMap<>();
	private static TObjectIntHashMap<ComparableItemStack> gemFuelMap = new TObjectIntHashMap<>();

	public static int DEFAULT_ENERGY = 30000;

	public static Set<ComparableItemStack> getFuels() {

		return ImmutableSet.copyOf(fuelMap.keySet());
	}

	public static Set<ComparableItemStack> getGemFuels() {

		return ImmutableSet.copyOf(gemFuelMap.keySet());
	}

	public static int getFuelEnergy(ItemStack stack) {

		if (stack.isEmpty()) {
			return 0;
		}
		return fuelMap.get(new ComparableItemStack(stack));
	}

	public static int getGemFuelEnergy(ItemStack stack) {

		if (stack.isEmpty()) {
			return 0;
		}
		return gemFuelMap.get(new ComparableItemStack(stack));
	}

	public static void refresh() {

		TObjectIntHashMap<ComparableItemStack> tempMap = new TObjectIntHashMap<>(fuelMap.size());
		TObjectIntHashMap<ComparableItemStack> tempMap2 = new TObjectIntHashMap<>(gemFuelMap.size());

		for (TObjectIntIterator<ComparableItemStack> it = fuelMap.iterator(); it.hasNext(); ) {
			it.advance();
			tempMap.put(new ComparableItemStack(it.key().toItemStack()), it.value());
		}
		for (TObjectIntIterator<ComparableItemStack> it = gemFuelMap.iterator(); it.hasNext(); ) {
			it.advance();
			tempMap2.put(new ComparableItemStack(it.key().toItemStack()), it.value());
		}
		fuelMap.clear();
		fuelMap = tempMap;

		gemFuelMap.clear();
		gemFuelMap = tempMap2;
	}

	/* ADD FUELS */
	public static boolean addFuel(ItemStack stack, int energy) {

		if (stack.isEmpty() || energy < 2000 || energy > 200000000) {
			return false;
		}
		fuelMap.put(new ComparableItemStack(stack), energy);
		return true;
	}

	public static boolean addGemFuel(ItemStack stack, int energy) {

		if (stack.isEmpty() || energy < 2000 || energy > 200000000) {
			return false;
		}
		gemFuelMap.put(new ComparableItemStack(stack), energy);
		return true;
	}

	/* REMOVE FUELS */
	public static boolean removeFuel(ItemStack stack) {

		fuelMap.remove(new ComparableItemStack(stack));
		return true;
	}

	public static boolean removeGemFuel(ItemStack stack) {

		gemFuelMap.remove(new ComparableItemStack(stack));
		return true;
	}

}
