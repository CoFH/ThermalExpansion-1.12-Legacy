package cofh.thermalexpansion.util.fuels;

import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class CoolantManager {

	/**
	 * Coolant is stored as RF effectiveness per bucket.
	 */
	private static TObjectIntHashMap<Fluid> coolantMap = new TObjectIntHashMap<>();

	private static final int WATER_RF = 500000;

	public static boolean isValidCoolant(Fluid fluid) {

		return fluid != null && coolantMap.containsKey(fluid);
	}

	public static boolean isValidCoolant(FluidStack stack) {

		return stack != null && coolantMap.containsKey(stack.getFluid());
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
	 * This is for a 50 mB amount.
	 */
	public static int getCoolantRF100mB(Fluid fluid) {

		return getCoolantRF(fluid) / 10;
	}

	public static int getCoolantRF100mB(FluidStack stack) {

		return getCoolantRF100mB(stack.getFluid());
	}

	/**
	 * This is a rough breakpoint factor - it's a measure of the "power" of a coolant relative to the Water baseline.
	 */
	public static int getCoolantFactor(Fluid fluid) {

		return getCoolantRF(fluid) / WATER_RF / 4;
	}

	public static int getCoolantFactor(FluidStack stack) {

		return getCoolantFactor(stack.getFluid());
	}

	public static void addDefaultMappings() {

		addCoolant("water", 500000);
		addCoolant("cryotheum", 4000000);

		addCoolant("ice", 2000000);
	}

	public static void loadMappings() {

	}

	/* ADD */
	public static boolean addCoolant(Fluid fluid, int coolantRF) {

		if (fluid == null) {
			return false;
		}
		coolantMap.put(fluid, coolantRF);
		return true;
	}

	public static boolean addCoolant(String fluidName, int coolantRF) {

		if (!FluidRegistry.isFluidRegistered(fluidName)) {
			return false;
		}
		return coolantMap.put(FluidRegistry.getFluid(fluidName), coolantRF) != 0;
	}

	/* REMOVE */
	public static boolean removeCoolant(Fluid fluid) {

		return coolantMap.remove(fluid) != 0;
	}

	public static boolean removeCoolant(String fluidName) {

		if (!FluidRegistry.isFluidRegistered(fluidName)) {
			return false;
		}
		return coolantMap.remove(FluidRegistry.getFluid(fluidName)) != 0;
	}

}
