package cofh.thermalexpansion.plugins.jei.pulverizer;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

import javax.annotation.Nonnull;

public class PulverizerRecipeHandler implements IRecipeHandler<PulverizerRecipeWrapper> {

	@Nonnull
	@Override
	public Class<PulverizerRecipeWrapper> getRecipeClass() {

		return PulverizerRecipeWrapper.class;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid() {

		return null;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid(@Nonnull PulverizerRecipeWrapper recipe) {

		return recipe.getUid();
	}

	@Nonnull
	@Override
	public IRecipeWrapper getRecipeWrapper(@Nonnull PulverizerRecipeWrapper recipe) {

		return recipe;
	}

	@Override
	public boolean isRecipeValid(@Nonnull PulverizerRecipeWrapper recipe) {

		return true;
	}

}
