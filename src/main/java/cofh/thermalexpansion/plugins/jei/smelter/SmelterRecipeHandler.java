package cofh.thermalexpansion.plugins.jei.smelter;

import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

import javax.annotation.Nonnull;

public class SmelterRecipeHandler implements IRecipeHandler<SmelterRecipeWrapper> {

	@Nonnull
	@Override
	public Class<SmelterRecipeWrapper> getRecipeClass() {

		return SmelterRecipeWrapper.class;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid() {

		return RecipeUidsTE.SMELTER;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid(@Nonnull SmelterRecipeWrapper recipe) {

		return getRecipeCategoryUid();
	}

	@Nonnull
	@Override
	public IRecipeWrapper getRecipeWrapper(@Nonnull SmelterRecipeWrapper recipe) {

		return recipe;
	}

	@Override
	public boolean isRecipeValid(@Nonnull SmelterRecipeWrapper recipe) {

		return true;
	}

}
