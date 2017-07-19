package cofh.thermalexpansion.util.managers;

import com.google.common.collect.ImmutableSet;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.Set;

public class CoolantManager {

	/**
	 * Coolant is stored as RF effectiveness per bucket.
	 */
	private static TObjectIntHashMap<String> coolantMap = new TObjectIntHashMap<>();
	private static TObjectIntHashMap<String> coolantFactorMap = new TObjectIntHashMap<>();

	private static final int WATER_RF = 250000;
	private static final int WATER_FACTOR = 6;

	public static boolean isValidCoolant(Fluid fluid) {

		return fluid != null && coolantMap.containsKey(fluid);
	}

	public static boolean isValidCoolant(FluidStack stack) {

		return stack != null && coolantMap.containsKey(stack.getFluid());
	}

	public static Set<String> getCoolantFluids() {

		return ImmutableSet.copyOf(coolantMap.keySet());
	}

	/**
	 * This is for the full bucket.
	 */
	public static int getCoolantRF(Fluid fluid) {

		return coolantMap.get(fluid.getName());
	}

	public static int getCoolantRF(FluidStack stack) {

		return getCoolantRF(stack.getFluid());
	}

	/**
	 * This is for a 100 mB amount.
	 */
	public static int getCoolantRF100mB(Fluid fluid) {

		return getCoolantRF(fluid) / 10;
	}

	public static int getCoolantRF100mB(FluidStack stack) {

		return getCoolantRF100mB(stack.getFluid());
	}

	/**
	 * This is a rough breakpoint factor - it's a measure of the "power" of a coolant relative to the Water baseline - lower is *better*.
	 */
	public static int getCoolantFactor(Fluid fluid) {

		return coolantFactorMap.get(fluid);
	}

	public static int getCoolantFactor(FluidStack stack) {

		return getCoolantFactor(stack.getFluid());
	}

	public static void initialize() {

		addCoolant("water", WATER_RF, WATER_FACTOR);
		addCoolant("cryotheum", 4000000, 2);

		loadMappings();
	}

	public static void loadMappings() {

		addCoolant("ice", 1500000, 3);
	}

	/* ADD */
	public static boolean addCoolant(String fluidName, int coolantRF, int coolantFactor) {

		if (!FluidRegistry.isFluidRegistered(fluidName) || coolantRF < 0 || coolantFactor < 1 || coolantFactor > 100) {
			return false;
		}
		coolantMap.put(fluidName, coolantRF);
		coolantFactorMap.put(fluidName, coolantFactor);
		return true;
	}

	/* REMOVE */
	public static boolean removeCoolant(String fluidName) {

		if (!FluidRegistry.isFluidRegistered(fluidName)) {
			return false;
		}
		coolantMap.remove(fluidName);
		coolantFactorMap.remove(fluidName);
		return true;
	}

}
