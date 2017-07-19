package cofh.thermalexpansion.util.managers.dynamo;

import cofh.core.init.CoreProps;
import com.google.common.collect.ImmutableSet;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.Set;

public class MagmaticManager {

	private static TObjectIntHashMap<String> fuelMap = new TObjectIntHashMap<>();

	public static int DEFAULT_ENERGY = 32000;

	public static Set<String> getFuels() {

		return ImmutableSet.copyOf(fuelMap.keySet());
	}

	public static boolean isValidFuel(FluidStack fluid) {

		return fluid != null && fuelMap.containsKey(fluid.getFluid().getName());
	}

	public static int getFuelEnergy(FluidStack fluid) {

		return fluid == null ? 0 : fuelMap.get(fluid.getFluid().getName());
	}

	public static int getFuelEnergy100mB(FluidStack fluid) {

		return fluid == null ? 0 : fuelMap.get(fluid.getFluid().getName()) / 10;
	}

	public static void initialize() {

		addFuel("lava", CoreProps.LAVA_RF * 9 / 10);
		addFuel("pyrotheum", 2000000);

		loadFuels();
	}

	public static void loadFuels() {

	}

	public static void refresh() {

		//		TObjectIntHashMap<Fluid> tempMap = new TObjectIntHashMap<>(fuelMap.size());
		//
		//		for (TObjectIntIterator<Fluid> it = fuelMap.iterator(); it.hasNext(); ) {
		//			it.advance();
		//			tempMap.put(FluidRegistry.getFluid(it.key().getName()), it.value());
		//		}
		//		fuelMap.clear();
		//		fuelMap = tempMap;
	}

	/* ADD FUELS */
	public static boolean addFuel(String fluidName, int energy) {

		if (!FluidRegistry.isFluidRegistered(fluidName) || energy < 10000 || energy > 200000000) {
			return false;
		}
		fuelMap.put(fluidName, energy);
		return true;
	}

	/* REMOVE FUELS */
	public static boolean removeFuel(String fluidName) {

		if (!FluidRegistry.isFluidRegistered(fluidName)) {
			return false;
		}
		fuelMap.remove(fluidName);
		return true;
	}

}
