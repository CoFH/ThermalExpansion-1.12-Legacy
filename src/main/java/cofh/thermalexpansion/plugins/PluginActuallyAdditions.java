package cofh.thermalexpansion.plugins;

import cofh.core.init.CoreProps;
import cofh.core.util.ModPlugin;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.util.managers.machine.CrucibleManager;
import cofh.thermalexpansion.util.managers.machine.PulverizerManager;
import cofh.thermalexpansion.util.managers.machine.RefineryManager;
import cofh.thermalfoundation.init.TFFluids;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;

public class PluginActuallyAdditions extends ModPlugin {

	public static final String MOD_ID = "actuallyadditions";
	public static final String MOD_NAME = "Actually Additions";

	public PluginActuallyAdditions() {

		super(MOD_ID, MOD_NAME);
	}

	/* IInitializer */
	@Override
	public boolean initialize() {

		String category = "Plugins";
		String comment = "If TRUE, support for " + MOD_NAME + " is enabled.";
		enable = ThermalExpansion.CONFIG.getConfiguration().getBoolean(MOD_NAME, category, true, comment) && Loader.isModLoaded(MOD_ID);

		if (!enable) {
			return false;
		}
		return !error;
	}

	@Override
	public boolean register() {

		if (!enable) {
			return false;
		}
		try {
			Fluid canola_oil = FluidRegistry.getFluid("canolaoil");
			Fluid refined_canola_oil = FluidRegistry.getFluid("refinedcanolaoil");

			/* PULVERIZER */
			{
				int energy = PulverizerManager.DEFAULT_ENERGY / 2;

				PulverizerManager.addRecipe(energy, getItemStack("block_black_lotus"), getItemStack("item_misc", 4, 17));
			}

			/* CRUCIBLE */
			{
				int energy = CrucibleManager.DEFAULT_ENERGY;

				CrucibleManager.addRecipe(energy / 4, getItemStack("item_solidified_experience"), new FluidStack(TFFluids.fluidExperience, 8 * CoreProps.MB_PER_XP));
			}

			/* REFINERY */
			{
				int energy = RefineryManager.DEFAULT_ENERGY;

				if (canola_oil != null && refined_canola_oil != null) {
					RefineryManager.addRecipe(energy / 5, new FluidStack(canola_oil, 100), new FluidStack(refined_canola_oil, 100), ItemStack.EMPTY);
				}
			}
		} catch (Throwable t) {
			ThermalExpansion.LOG.error("Thermal Expansion: " + MOD_NAME + " Plugin encountered an error:", t);
			error = true;
		}
		if (!error) {
			ThermalExpansion.LOG.info("Thermal Expansion: " + MOD_NAME + " Plugin Enabled.");
		}
		return !error;
	}

}
