package cofh.thermalexpansion.api.crafting.recipes;

import net.minecraft.item.ItemStack;

public interface IFurnaceRecipe {

	ItemStack getInput();

	ItemStack getOutput();

	int getEnergy();

	boolean isOutputFood();

}
