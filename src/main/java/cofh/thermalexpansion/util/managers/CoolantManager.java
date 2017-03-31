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
	private static TObjectIntHashMap<Fluid> coolantMap = new TObjectIntHashMap<>();
	private static TObjectIntHashMap<Fluid> coolantFactorMap = new TObjectIntHashMap<>();

	private static final int WATER_RF = 500000;

	public static boolean isValidCoolant(Fluid fluid) {

		return fluid != null && coolantMap.containsKey(fluid);
	}

	public static boolean isValidCoolant(FluidStack stack) {

		return stack != null && coolantMap.containsKey(stack.getFluid());
	}

	public static Set<Fluid> getCoolantFluids() {

		return ImmutableSet.copyOf(coolantMap.keySet());
	}

	/**
	 * This is for the full bucket.
	 */
	public static int getCoolantRF(Fluid fluid) {

		return coolantMap.get(fluid);
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

		addCoolant("water", 500000, 6);
		addCoolant("cryotheum", 4000000, 2);
		addCoolant("ice", 1500000, 3);

		loadMappings();
	}

	public static void loadMappings() {

	}

	/* ADD */
	public static boolean addCoolant(Fluid fluid, int coolantRF, int coolantFactor) {

		if (fluid == null || coolantRF < 0 || coolantFactor < 1 || coolantFactor > 100) {
			return false;
		}
		coolantMap.put(fluid, coolantRF);
		coolantFactorMap.put(fluid, coolantFactor);
		return true;
	}

	public static boolean addCoolant(String fluidName, int coolantRF, int coolantFactor) {

		return addCoolant(FluidRegistry.getFluid(fluidName), coolantRF, coolantFactor);
	}

	/* REMOVE */
	public static boolean removeCoolant(Fluid fluid) {

		if (!coolantMap.contains(fluid)) {
			return false;
		}
		coolantMap.remove(fluid);
		coolantFactorMap.remove(fluid);
		return true;
	}

	public static boolean removeCoolant(String fluidName) {

		if (!FluidRegistry.isFluidRegistered(fluidName)) {
			return false;
		}
		coolantMap.remove(FluidRegistry.getFluid(fluidName));
		coolantFactorMap.remove(FluidRegistry.getFluid(fluidName));
		return true;
	}

}
