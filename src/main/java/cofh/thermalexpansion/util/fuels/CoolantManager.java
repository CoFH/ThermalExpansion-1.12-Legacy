package cofh.thermalexpansion.util.fuels;

import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class CoolantManager {

	private static TObjectIntHashMap<Fluid> coolantMap = new TObjectIntHashMap<Fluid>();

	public static final int WATER_FACTOR = 1000;

	/**
	 * This is derived from Lava, as 1 bucket of water fully cools one bucket of Lava.
	 */
	public static final int WATER_RF = 200000;
	public static final int WATER_RF_50 = WATER_RF / 20;

	/**
	 * Coolant factor is an approximate measure, relative to Water at 1000.
	 */
	public static boolean addCoolant(Fluid fluid, int coolantFactor) {

		if (fluid == null) {
			return false;
		}
		coolantMap.put(fluid, coolantFactor);
		return true;
	}

	public static boolean removeCoolant(Fluid fluid) {

		coolantMap.remove(fluid);
		return true;
	}

	public static int getCoolantFactor(Fluid fluid) {

		return coolantMap.get(fluid);
	}

	public static int getCoolantRF(Fluid fluid) {

		return WATER_RF * getCoolantFactor(fluid) / WATER_FACTOR;
	}

	public static int getCoolantRF(FluidStack stack) {

		return getCoolantRF(stack.getFluid());
	}

	/**
	 * This is for a 50 mB amount.
	 */
	public static int getCoolantRF50mB(Fluid fluid) {

		return WATER_RF_50 * getCoolantFactor(fluid) / WATER_FACTOR;
	}

	public static int getCoolantRF50mB(FluidStack stack) {

		return getCoolantRF50mB(stack.getFluid());
	}

}
