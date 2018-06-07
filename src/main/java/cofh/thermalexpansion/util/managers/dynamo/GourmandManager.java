package cofh.thermalexpansion.util.managers.dynamo;

import cofh.core.inventory.ComparableItemStack;
import com.google.common.collect.ImmutableSet;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;

import java.util.Set;

public class GourmandManager {

	private static TObjectIntHashMap<ComparableItemStack> fuelMap = new TObjectIntHashMap<>();

	public static int DEFAULT_ENERGY = 2000;

	public static Set<ComparableItemStack> getFuels() {

		return ImmutableSet.copyOf(fuelMap.keySet());
	}

	public static int getFuelEnergy(ItemStack stack) {

		if (stack.isEmpty()) {
			return 0;
		}
		if (stack.getItem().hasContainerItem(stack)) {
			return 0;
		}
		int energy = fuelMap.get(new ComparableItemStack(stack));

		if (energy > 0) {
			return energy;
		}
		if (stack.getItem() instanceof ItemFood) {
			ItemFood food = (ItemFood) stack.getItem();
			int foodEnergy = food.getHealAmount(stack) * DEFAULT_ENERGY;
			int satEnergy = (int) (food.getSaturationModifier(stack) * foodEnergy * 2);
			return foodEnergy + satEnergy;
		}
		return 0;
	}

	// TODO: Implement.
	public static int getPaleoFuelEnergy(ItemStack stack) {

		return getFuelEnergy(stack);
	}

	public static void refresh() {

		TObjectIntHashMap<ComparableItemStack> tempMap = new TObjectIntHashMap<>(fuelMap.size());

		for (TObjectIntIterator<ComparableItemStack> it = fuelMap.iterator(); it.hasNext(); ) {
			it.advance();
			tempMap.put(new ComparableItemStack(it.key().toItemStack()), it.value());
		}
		fuelMap.clear();
		fuelMap = tempMap;
	}

	/* ADD FUELS */
	public static boolean addFuel(ItemStack stack, int energy) {

		if (stack.isEmpty() || energy < 2000 || energy > 200000000) {
			return false;
		}
		fuelMap.put(new ComparableItemStack(stack), energy);
		return true;
	}

	/* REMOVE FUELS */
	public static boolean removeFuel(ItemStack stack) {

		fuelMap.remove(new ComparableItemStack(stack));
		return true;
	}

}
