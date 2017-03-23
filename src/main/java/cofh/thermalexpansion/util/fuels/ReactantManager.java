package cofh.thermalexpansion.util.fuels;

import cofh.core.init.CoreProps;
import cofh.lib.inventory.ComparableItemStack;
import com.google.common.collect.ImmutableSet;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

import java.util.Set;

public class ReactantManager {

	private static TObjectIntHashMap<ComparableItemStack> fuelMap = new TObjectIntHashMap<>();

	public static int DEFAULT_ENERGY = 32000;

	public static Set<ComparableItemStack> getFuels() {

		return ImmutableSet.copyOf(fuelMap.keySet());
	}

	public static int getEnergyValue(ItemStack stack) {

		if (stack == null) {
			return 0;
		}
		if (stack.getItem().hasContainerItem(stack)) {
			return 0;
		}
		int energy = fuelMap.get(new ComparableItemStack(stack));

		return energy > 0 ? energy : TileEntityFurnace.getItemBurnTime(stack) * CoreProps.RF_PER_MJ;
	}

	public static void addDefaultFuels() {

	}

	public static void loadFuels() {

	}

	/* ADD FUELS */
	public static boolean addFuel(ItemStack stack, int energy) {

		if (stack == null || energy < 1000 || energy > 200000000) {
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
