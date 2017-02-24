package cofh.thermalexpansion.plugins.jei.insolator;

import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

import javax.annotation.Nonnull;

public class InsolatorRecipeHandler implements IRecipeHandler<InsolatorRecipeWrapper> {

	@Nonnull
	@Override
	public Class<InsolatorRecipeWrapper> getRecipeClass() {

		return InsolatorRecipeWrapper.class;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid() {

		return RecipeUidsTE.INSOLATOR;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid(@Nonnull InsolatorRecipeWrapper recipe) {

		return getRecipeCategoryUid();
	}

	@Nonnull
	@Override
	public IRecipeWrapper getRecipeWrapper(@Nonnull InsolatorRecipeWrapper recipe) {

		return recipe;
	}

	@Override
	public boolean isRecipeValid(@Nonnull InsolatorRecipeWrapper recipe) {

		return true;
	}

}
