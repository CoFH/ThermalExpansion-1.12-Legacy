package cofh.thermalexpansion.plugins;

import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.util.managers.machine.RefineryManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;

public class ActuallyAdditionsPlugin {

	private ActuallyAdditionsPlugin() {

	}

	public static final String MOD_ID = "actuallyadditions";
	public static final String MOD_NAME = "Actually Additions";

	public static void initialize() {

		String category = "Plugins";
		String comment = "If TRUE, support for " + MOD_NAME + " is enabled.";

		boolean enable = ThermalExpansion.CONFIG.getConfiguration().getBoolean(MOD_NAME, category, true, comment);

		if (!enable || !Loader.isModLoaded(MOD_ID)) {
			return;
		}
		try {

			Fluid canola_oil = FluidRegistry.getFluid("canola_oil");
			Fluid refined_canola_oil = FluidRegistry.getFluid("oil");

			/* REFINERY */
			{
				if (canola_oil != null && refined_canola_oil != null) {
					RefineryManager.addRecipe(1000, new FluidStack(canola_oil, 100), new FluidStack(refined_canola_oil, 100), ItemStack.EMPTY);
				}
			}

			ThermalExpansion.LOG.info("Thermal Expansion: " + MOD_NAME + " Plugin Enabled.");
		} catch (Throwable t) {
			ThermalExpansion.LOG.error("Thermal Expansion: " + MOD_NAME + " Plugin encountered an error:", t);
		}
	}

}
