package cofh.thermalexpansion.api.crafting;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public interface ICrucibleHandler {

	boolean addRecipe(int energy, ItemStack input, FluidStack output, boolean overwrite);

	boolean removeRecipe(ItemStack input);

}
