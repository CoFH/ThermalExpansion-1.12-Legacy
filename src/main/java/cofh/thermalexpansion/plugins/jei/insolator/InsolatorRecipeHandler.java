package cofh.thermalexpansion.plugins.jei.insolator;

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

		return null;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid(@Nonnull InsolatorRecipeWrapper recipe) {

		return recipe.getUid();
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
