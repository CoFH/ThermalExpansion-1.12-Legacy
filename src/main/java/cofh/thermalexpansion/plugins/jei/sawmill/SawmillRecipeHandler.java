package cofh.thermalexpansion.plugins.jei.sawmill;

import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

import javax.annotation.Nonnull;

public class SawmillRecipeHandler implements IRecipeHandler<SawmillRecipeWrapper> {

	@Nonnull
	@Override
	public Class<SawmillRecipeWrapper> getRecipeClass() {

		return SawmillRecipeWrapper.class;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid() {

		return RecipeUidsTE.SAWMILL;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid(@Nonnull SawmillRecipeWrapper recipe) {

		return getRecipeCategoryUid();
	}

	@Nonnull
	@Override
	public IRecipeWrapper getRecipeWrapper(@Nonnull SawmillRecipeWrapper recipe) {

		return recipe;
	}

	@Override
	public boolean isRecipeValid(@Nonnull SawmillRecipeWrapper recipe) {

		return true;
	}

}
