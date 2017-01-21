package cofh.thermalexpansion.api.crafting;

import net.minecraft.item.ItemStack;

public interface ISawmillHandler {

	boolean addRecipe(int energy, ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance);

	boolean removeRecipe(ItemStack input);

}
