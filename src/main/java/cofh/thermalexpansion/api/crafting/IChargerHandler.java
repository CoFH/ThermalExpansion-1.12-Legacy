package cofh.thermalexpansion.api.crafting;

import net.minecraft.item.ItemStack;

public interface IChargerHandler {

	public boolean addRecipe(int energy, ItemStack input, ItemStack output, boolean overwrite);

	public boolean removeRecipe(ItemStack input);

}
