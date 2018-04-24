package cofh.thermalexpansion.plugins;

import cofh.thermalexpansion.util.managers.machine.ExtruderManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class PluginChisel extends PluginTEBase {

	public static final String MOD_ID = "chisel";
	public static final String MOD_NAME = "Chisel";

	public PluginChisel() {

		super(MOD_ID, MOD_NAME);
	}

	@Override
	public void registerDelegate() {

		ItemStack basalt = getItemStack("basalt2", 1, 7);           //"raw" basalt
		ItemStack limestone = getItemStack("limestone2", 1, 7);     //"raw" limestone

		/* EXTRUDER */
		{
			int energy = ExtruderManager.DEFAULT_ENERGY * 2;

			ExtruderManager.addRecipeIgneous(energy, basalt, new FluidStack(FluidRegistry.LAVA, 250), new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME));
			ExtruderManager.addRecipeSedimentary(energy, limestone, new FluidStack(FluidRegistry.LAVA, 0), new FluidStack(FluidRegistry.WATER, 1500));
		}
	}

}
