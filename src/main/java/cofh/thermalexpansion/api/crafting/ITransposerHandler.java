package cofh.thermalexpansion.api.crafting;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public interface ITransposerHandler {

	public boolean addFillRecipe(int energy, ItemStack input, ItemStack output, FluidStack fluid, boolean reversible, boolean overwrite);

	boolean addExtractionRecipe(int energy, ItemStack input, ItemStack output, FluidStack fluid, int chance, boolean reversible, boolean overwrite);

	public boolean removeFillRecipe(ItemStack input, FluidStack fluid);

	public boolean removeExtractionRecipe(ItemStack input);

}
