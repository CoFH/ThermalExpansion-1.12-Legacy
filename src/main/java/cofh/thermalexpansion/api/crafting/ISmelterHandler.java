package cofh.thermalexpansion.api.crafting;

import net.minecraft.item.ItemStack;

public interface ISmelterHandler {

	public boolean addRecipe(int energy, ItemStack primaryInput, ItemStack secondaryInput, ItemStack primaryOutput, ItemStack secondaryOutput,
			int secondaryChance, boolean overwrite);

	public boolean removeRecipe(ItemStack primaryInput, ItemStack secondaryInput);

}
