package cofh.thermalexpansion.api.crafting;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public interface ITransposerHandler {

	boolean addFillRecipe(int energy, ItemStack input, ItemStack output, FluidStack fluid, boolean reversible);

	boolean addExtractionRecipe(int energy, ItemStack input, ItemStack output, FluidStack fluid, int chance, boolean reversible);

	boolean removeFillRecipe(ItemStack input, FluidStack fluid);

	boolean removeExtractionRecipe(ItemStack input);

}
