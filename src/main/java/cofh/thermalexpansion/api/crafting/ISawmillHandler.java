package cofh.thermalexpansion.api.crafting;

import net.minecraft.item.ItemStack;

public interface ISawmillHandler {

	public boolean addRecipe(int energy, ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance, boolean overwrite);

	public boolean removeRecipe(ItemStack input);

}
