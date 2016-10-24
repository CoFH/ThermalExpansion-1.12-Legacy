package cofh.thermalexpansion.api.crafting;

import net.minecraft.item.ItemStack;

public interface IInsolatorHandler {

	boolean addRecipe(int energy, ItemStack primaryInput, ItemStack secondaryInput, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance, boolean overwrite);

	boolean removeRecipe(ItemStack primaryInput, ItemStack secondaryInput);

}
