package cofh.thermalexpansion.util.managers.dynamo;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.Set;

public class CompressionManager {

	private static Object2IntOpenHashMap<String> fuelMap = new Object2IntOpenHashMap<>();

	public static int DEFAULT_ENERGY = 100000;

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

	public static void refresh() {

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
