package cofh.thermalexpansion.api.crafting;

import net.minecraft.item.ItemStack;

public interface IFurnaceHandler {

	boolean addRecipe(int energy, ItemStack input, ItemStack output, boolean overwrite);

	boolean removeRecipe(ItemStack input);

}
