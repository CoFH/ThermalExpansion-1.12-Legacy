package cofh.thermalexpansion.util.managers.dynamo;

import cofh.core.init.CoreProps;
import cofh.core.inventory.ComparableItemStack;
import com.google.common.collect.ImmutableSet;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

import java.util.Set;

public class SteamManager {

	private static TObjectIntHashMap<ComparableItemStack> fuelMap = new TObjectIntHashMap<>();

	public static int DEFAULT_ENERGY = 16000;

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
		energy = TileEntityFurnace.getItemBurnTime(stack) * CoreProps.RF_PER_MJ;

		if (energy >= 300 * CoreProps.RF_PER_MJ) {
			return energy;
		}
		return 0;
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
		if (fuelMap.containsKey(new ComparableItemStack(stack))) {
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
