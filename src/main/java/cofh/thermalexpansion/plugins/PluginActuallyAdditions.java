package cofh.thermalexpansion.plugins;

import cofh.core.init.CoreProps;
import cofh.thermalexpansion.util.managers.machine.CrucibleManager;
import cofh.thermalexpansion.util.managers.machine.PulverizerManager;
import cofh.thermalexpansion.util.managers.machine.RefineryManager;
import cofh.thermalfoundation.init.TFFluids;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class PluginActuallyAdditions extends PluginTEBase {

	public static final String MOD_ID = "actuallyadditions";
	public static final String MOD_NAME = "Actually Additions";

	public PluginActuallyAdditions() {

		super(MOD_ID, MOD_NAME);
	}

	@Override
	public void initializeDelegate() {

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
	}

}
