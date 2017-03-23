package cofh.thermalexpansion.util.fuels;

import cofh.api.energy.IEnergyContainerItem;
import cofh.lib.util.helpers.EnergyHelper;
import com.google.common.collect.ImmutableSet;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import java.util.Set;

public class EnervationManager {

	private static TObjectIntHashMap<ItemStack> fuelMap = new TObjectIntHashMap<>();

	public static int DEFAULT_ENERGY = 32000;

	public static Set<ItemStack> getFuels() {

		return ImmutableSet.copyOf(fuelMap.keySet());
	}

	public static int getFuelEnergy(ItemStack stack) {

		if (stack == null) {
			return 0;
		}
		int energy = fuelMap.get(stack);

		if (energy > 0) {
			return energy;
		}
		if (EnergyHelper.isEnergyContainerItem(stack)) {
			IEnergyContainerItem container = (IEnergyContainerItem) stack.getItem();
			return container.extractEnergy(stack, container.getEnergyStored(stack), true);
		}
		return 0;
	}

	public static void addDefaultFuels() {

		addFuel(new ItemStack(Items.REDSTONE), 64000);
		addFuel(new ItemStack(Blocks.REDSTONE_BLOCK), 64000 * 10);
	}

	public static void loadFuels() {

	}

	/* ADD FUELS */
	public static boolean addFuel(ItemStack stack, int energy) {

		if (stack == null || energy < 1000 || energy > 200000000) {
			return false;
		}
		fuelMap.put(stack, energy);
		return true;
	}

	/* REMOVE FUELS */
	public static boolean removeFuel(ItemStack stack) {

		fuelMap.remove(stack);
		return true;
	}

}
