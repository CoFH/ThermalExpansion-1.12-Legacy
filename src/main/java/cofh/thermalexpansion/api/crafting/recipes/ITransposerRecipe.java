package cofh.thermalexpansion.api.crafting.recipes;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public interface ITransposerRecipe {

	ItemStack getInput();

	ItemStack getOutput();

	FluidStack getFluid();

	int getEnergy();

	int getChance();

}
