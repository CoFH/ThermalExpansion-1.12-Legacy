package cofh.thermalexpansion.plugins;

import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.util.managers.machine.TransposerManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class PluginImmersiveEngineering extends PluginTEBase {

	public static final String MOD_ID = "immersiveengineering";
	public static final String MOD_NAME = "Immersive Engineering";

	public PluginImmersiveEngineering() {

		super(MOD_ID, MOD_NAME);
	}

	@Override
	public void initializeDelegate() {

		ItemStack treatedWood = getItemStack("treated_wood", 1, 0);

		Fluid creosote = FluidRegistry.getFluid("creosote");

		/* TRANSPOSER */
		{
			int energy = 400;

			if (creosote != null) {
				TransposerManager.addFillRecipe(energy, ItemHelper.getOre("logWood"), ItemHelper.cloneStack(treatedWood), new FluidStack(creosote, Fluid.BUCKET_VOLUME / 8), false);
			}
		}
	}

}
