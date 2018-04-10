package cofh.thermalexpansion.plugins;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import cofh.thermalexpansion.util.managers.machine.ExtruderManager;

public class PluginChisel extends PluginTEBase {

	public static final String MOD_ID = "chisel";
	public static final String MOD_NAME = "Chisel";

	public PluginChisel() {

		super(MOD_ID, MOD_NAME);
	}

	@Override
	public void registerDelegate() {

		ItemStack basalt = getItemStack("basalt2",1,7);	//"raw" basalt
		ItemStack limeStone = getItemStack("limestone2",1,7); //"raw" limestone

		/* EXTRUDER */
		{
			ExtruderManager.addRecipeIgneous(ExtruderManager.DEFAULT_ENERGY * 2, basalt, new FluidStack(FluidRegistry.LAVA, 0), new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME));
			ExtruderManager.addRecipeSedimentary(ExtruderManager.DEFAULT_ENERGY * 2, limeStone, new FluidStack(FluidRegistry.LAVA, 0), new FluidStack(FluidRegistry.WATER, 1500));
		}
	}
}
