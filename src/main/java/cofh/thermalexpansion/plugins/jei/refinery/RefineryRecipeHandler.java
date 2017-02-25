package cofh.thermalexpansion.plugins.jei.refinery;

import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

import javax.annotation.Nonnull;

public class RefineryRecipeHandler implements IRecipeHandler<RefineryRecipeWrapper> {

	@Nonnull
	@Override
	public Class<RefineryRecipeWrapper> getRecipeClass() {

		return RefineryRecipeWrapper.class;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid() {

		return RecipeUidsTE.REFINERY;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid(@Nonnull RefineryRecipeWrapper recipe) {

		return getRecipeCategoryUid();
	}

	@Nonnull
	@Override
	public IRecipeWrapper getRecipeWrapper(@Nonnull RefineryRecipeWrapper recipe) {

		return recipe;
	}

	@Override
	public boolean isRecipeValid(@Nonnull RefineryRecipeWrapper recipe) {

		return true;
	}

}
