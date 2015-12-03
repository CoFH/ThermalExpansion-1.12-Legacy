package cofh.thermalexpansion.api.crafting.recipes;

import net.minecraft.item.ItemStack;

public interface ISawmillRecipe {

	ItemStack getInput();

	ItemStack getPrimaryOutput();

	ItemStack getSecondaryOutput();

	int getEnergy();

	int getSecondaryOutputChance();

}
