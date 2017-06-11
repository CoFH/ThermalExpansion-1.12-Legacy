package cofh.thermalexpansion.util.managers.dynamo;

import com.google.common.collect.ImmutableSet;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.Set;

public class CompressionManager {

	private static TObjectIntHashMap<Fluid> fuelMap = new TObjectIntHashMap<>();

	public static int DEFAULT_ENERGY = 32000;

	public static Set<Fluid> getFuels() {

		return ImmutableSet.copyOf(fuelMap.keySet());
	}

	public static boolean isValidFuel(FluidStack stack) {

		return stack != null && fuelMap.containsKey(stack.getFluid());
	}

	public static int getFuelEnergy(FluidStack stack) {

		return stack == null ? 0 : fuelMap.get(stack.getFluid());
	}

	public static int getFuelEnergy100mB(FluidStack stack) {

		return stack == null ? 0 : fuelMap.get(stack.getFluid()) / 10;
	}

	public static void initialize() {

		addFuel("creosote", 100000);
		addFuel("coal", 500000);
		addFuel("tree_oil", 1000000);
		addFuel("refined_oil", 1250000);
		addFuel("fuel", 2000000);

		loadFuels();
	}

	public static void loadFuels() {

	}

	/* ADD FUELS */
	public static boolean addFuel(Fluid fluid, int energy) {

		if (fluid == null || energy < 10000 || energy > 200000000) {
			return false;
		}
		fuelMap.put(fluid, energy);
		return true;
	}

	public static boolean addFuel(String name, int energy) {

		return FluidRegistry.isFluidRegistered(name) && addFuel(FluidRegistry.getFluid(name), energy);
	}

	/* REMOVE FUELS */
	public static boolean removeFuel(Fluid fluid) {

		fuelMap.remove(fluid);
		return true;
	}

	public static boolean removeFuel(String name) {

		return FluidRegistry.isFluidRegistered(name) && removeFuel(FluidRegistry.getFluid(name));
	}

}
