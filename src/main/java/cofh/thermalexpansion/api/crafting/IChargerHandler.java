package cofh.thermalexpansion.api.crafting;

import net.minecraft.item.ItemStack;

public interface IChargerHandler {

	boolean addRecipe(int energy, ItemStack input, ItemStack output, boolean overwrite);

	boolean removeRecipe(ItemStack input);

}
