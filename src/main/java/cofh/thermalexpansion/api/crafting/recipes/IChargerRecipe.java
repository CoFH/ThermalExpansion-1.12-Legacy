package cofh.thermalexpansion.api.crafting.recipes;

import net.minecraft.item.ItemStack;

public interface IChargerRecipe {

	ItemStack getInput();

	ItemStack getOutput();

	int getEnergy();

}
