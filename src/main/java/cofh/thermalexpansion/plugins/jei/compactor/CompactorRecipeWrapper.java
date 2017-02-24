package cofh.thermalexpansion.plugins.jei.compactor;

import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.util.crafting.CompactorManager.ComparableItemStackCompactor;
import cofh.thermalexpansion.util.crafting.CompactorManager.Mode;
import cofh.thermalexpansion.util.crafting.CompactorManager.RecipeCompactor;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompactorRecipeWrapper extends BlankRecipeWrapper {

	private final List<List<ItemStack>> inputs;
	private final List<ItemStack> outputs;

	private final int energy;

	private final Mode mode;

	public CompactorRecipeWrapper(RecipeCompactor recipe, Mode mode) {

		List<ItemStack> recipeInputs = new ArrayList<>();

		if (ComparableItemStackCompactor.getOreID(recipe.getInput()) != -1) {
			recipeInputs.addAll(OreDictionary.getOres(ItemHelper.getOreName(recipe.getInput())));
		} else {
			recipeInputs.add(recipe.getInput());
		}
		List<ItemStack> recipeOutputs = new ArrayList<>();
		recipeOutputs.add(recipe.getOutput());

		inputs = Collections.singletonList(recipeInputs);
		outputs = recipeOutputs;

		energy = recipe.getEnergy();

		this.mode = mode;
	}

	@Override
	public void getIngredients(IIngredients ingredients) {

		ingredients.setInputLists(ItemStack.class, inputs);
		ingredients.setOutputs(ItemStack.class, outputs);
	}

	public String getUid() {

		switch (mode) {
			case STORAGE:
				return RecipeUidsTE.COMPACTOR_STORAGE;
			case MINT:
				return RecipeUidsTE.COMPACTOR_MINT;
			default:
				return RecipeUidsTE.COMPACTOR_PRESS;
		}
	}

}
