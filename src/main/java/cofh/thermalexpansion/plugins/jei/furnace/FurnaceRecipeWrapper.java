package cofh.thermalexpansion.plugins.jei.furnace;

import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalexpansion.util.crafting.FurnaceManager.ComparableItemStackFurnace;
import cofh.thermalexpansion.util.crafting.FurnaceManager.RecipeFurnace;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FurnaceRecipeWrapper extends BlankRecipeWrapper {

	final List<List<ItemStack>> inputs;
	final List<ItemStack> outputs;

	final int energy;

	public FurnaceRecipeWrapper(RecipeFurnace recipe) {

		List<ItemStack> recipeInputs = new ArrayList<>();

		if (ComparableItemStackFurnace.getOreID(recipe.getInput()) != -1) {
			recipeInputs.addAll(OreDictionary.getOres(ItemHelper.getOreName(recipe.getInput())));
		} else {
			recipeInputs.add(recipe.getInput());
		}
		List<ItemStack> recipeOutputs = new ArrayList<>();
		recipeOutputs.add(recipe.getOutput());

		inputs = Collections.singletonList(recipeInputs);
		outputs = recipeOutputs;

		energy = recipe.getEnergy();
	}

	@Override
	public void getIngredients(IIngredients ingredients) {

		ingredients.setInputLists(ItemStack.class, inputs);
		ingredients.setOutputs(ItemStack.class, outputs);
	}

}
