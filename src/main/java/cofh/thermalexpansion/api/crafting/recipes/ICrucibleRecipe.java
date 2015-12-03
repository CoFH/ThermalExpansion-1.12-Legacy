package cofh.thermalexpansion.api.crafting.recipes;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public interface ICrucibleRecipe {

	ItemStack getInput();

	FluidStack getOutput();

	int getEnergy();

}
