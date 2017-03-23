package cofh.thermalexpansion.plugins.jei.fuels.numismatic;

import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

import javax.annotation.Nonnull;

public class NumismaticFuelHandler implements IRecipeHandler<NumismaticFuelWrapper> {

	@Nonnull
	@Override
	public Class<NumismaticFuelWrapper> getRecipeClass() {

		return NumismaticFuelWrapper.class;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid() {

		return RecipeUidsTE.DYNAMO_NUMISMATIC;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid(@Nonnull NumismaticFuelWrapper recipe) {

		return getRecipeCategoryUid();
	}

	@Nonnull
	@Override
	public IRecipeWrapper getRecipeWrapper(@Nonnull NumismaticFuelWrapper recipe) {

		return recipe;
	}

	@Override
	public boolean isRecipeValid(@Nonnull NumismaticFuelWrapper recipe) {

		return true;
	}

}
