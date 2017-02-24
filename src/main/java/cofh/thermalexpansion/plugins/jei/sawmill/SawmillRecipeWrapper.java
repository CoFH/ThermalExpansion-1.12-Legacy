package cofh.thermalexpansion.plugins.jei.sawmill;

import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalexpansion.util.crafting.SawmillManager.ComparableItemStackSawmill;
import cofh.thermalexpansion.util.crafting.SawmillManager.RecipeSawmill;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SawmillRecipeWrapper extends BlankRecipeWrapper {

	final List<List<ItemStack>> inputs;
	final List<ItemStack> outputs;

	final int energy;
	final int chance;

	public SawmillRecipeWrapper(RecipeSawmill recipe) {

		List<ItemStack> recipeInputs = new ArrayList<>();

		if (ComparableItemStackSawmill.getOreID(recipe.getInput()) != -1) {
			recipeInputs.addAll(OreDictionary.getOres(ItemHelper.getOreName(recipe.getInput())));
		} else {
			recipeInputs.add(recipe.getInput());
		}
		List<ItemStack> recipeOutputs = new ArrayList<>();
		recipeOutputs.add(recipe.getPrimaryOutput());

		if (recipe.getSecondaryOutput() != null) {
			recipeOutputs.add(recipe.getSecondaryOutput());
		}
		inputs = Collections.singletonList(recipeInputs);
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
