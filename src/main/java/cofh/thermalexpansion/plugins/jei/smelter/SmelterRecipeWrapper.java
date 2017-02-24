package cofh.thermalexpansion.plugins.jei.smelter;

import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalexpansion.util.crafting.SmelterManager.ComparableItemStackSmelter;
import cofh.thermalexpansion.util.crafting.SmelterManager.RecipeSmelter;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

public class SmelterRecipeWrapper extends BlankRecipeWrapper {

	final List<List<ItemStack>> inputs;
	final List<ItemStack> outputs;

	final int energy;
	final int chance;

	public SmelterRecipeWrapper(RecipeSmelter recipe) {

		List<List<ItemStack>> recipeInputs = new ArrayList<>();
		List<ItemStack> recipeInputsPrimary = new ArrayList<>();
		List<ItemStack> recipeInputsSecondary = new ArrayList<>();

		if (ComparableItemStackSmelter.getOreID(recipe.getPrimaryInput()) != -1) {
			recipeInputsPrimary.addAll(OreDictionary.getOres(ItemHelper.getOreName(recipe.getPrimaryInput())));
		} else {
			recipeInputsPrimary.add(recipe.getPrimaryInput());
		}
		if (ComparableItemStackSmelter.getOreID(recipe.getSecondaryInput()) != -1) {
			recipeInputsSecondary.addAll(OreDictionary.getOres(ItemHelper.getOreName(recipe.getSecondaryInput())));
		} else {
			recipeInputsSecondary.add(recipe.getSecondaryInput());
		}
		recipeInputs.add(recipeInputsPrimary);
		recipeInputs.add(recipeInputsSecondary);

		List<ItemStack> recipeOutputs = new ArrayList<>();
		recipeOutputs.add(recipe.getPrimaryOutput());

		if (recipe.getSecondaryOutput() != null) {
			recipeOutputs.add(recipe.getSecondaryOutput());
		}
		inputs = recipeInputs;
		outputs = recipeOutputs;

		energy = recipe.getEnergy();
		chance = recipe.getSecondaryOutputChance();
	}

	@Override
	public void getIngredients(IIngredients ingredients) {

		ingredients.setInputLists(ItemStack.class, inputs);
		ingredients.setOutputs(ItemStack.class, outputs);
	}

}
