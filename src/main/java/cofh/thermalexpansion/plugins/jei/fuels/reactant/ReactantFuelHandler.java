package cofh.thermalexpansion.plugins.jei.fuels.reactant;

import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

import javax.annotation.Nonnull;

public class ReactantFuelHandler implements IRecipeHandler<ReactantFuelWrapper> {

	@Nonnull
	@Override
	public Class<ReactantFuelWrapper> getRecipeClass() {

		return ReactantFuelWrapper.class;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid(@Nonnull ReactantFuelWrapper recipe) {

		return  RecipeUidsTE.DYNAMO_REACTANT;
	}

	@Nonnull
	@Override
	public IRecipeWrapper getRecipeWrapper(@Nonnull ReactantFuelWrapper recipe) {

		return recipe;
	}

	@Override
	public boolean isRecipeValid(@Nonnull ReactantFuelWrapper recipe) {

		return true;
	}

}
