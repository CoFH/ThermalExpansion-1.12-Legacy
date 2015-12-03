package cofh.thermalexpansion.api.crafting;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public interface ICrucibleHandler {

	public boolean addRecipe(int energy, ItemStack input, FluidStack output, boolean overwrite);

	public boolean removeRecipe(ItemStack input);

}
