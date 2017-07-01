package cofh.thermalexpansion.util.managers.dynamo;

import cofh.core.inventory.ComparableItemStack;
import cofh.core.util.helpers.EnergyHelper;
import cofh.redstoneflux.api.IEnergyContainerItem;
import com.google.common.collect.ImmutableSet;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import java.util.Set;

public class EnervationManager {

	private static TObjectIntHashMap<ComparableItemStack> fuelMap = new TObjectIntHashMap<>();

	public static int DEFAULT_ENERGY = 32000;

	public static Set<ComparableItemStack> getFuels() {

		return ImmutableSet.copyOf(fuelMap.keySet());
	}

	public static int getFuelEnergy(ItemStack stack) {

		if (stack.isEmpty()) {
			return 0;
		}
		int energy = fuelMap.get(new ComparableItemStack(stack));

		if (energy > 0) {
			return energy;
		}
		if (EnergyHelper.isEnergyContainerItem(stack)) {
			IEnergyContainerItem container = (IEnergyContainerItem) stack.getItem();
			return container.extractEnergy(stack, container.getEnergyStored(stack), true);
		}
		return 0;
	}

	public static void initialize() {

		addFuel(new ItemStack(Items.REDSTONE), 64000);
		addFuel(new ItemStack(Blocks.REDSTONE_BLOCK), 64000 * 10);

		loadFuels();
	}

	public static void loadFuels() {

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

		if (stack.isEmpty() || energy < 1000 || energy > 200000000) {
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
