package cofh.thermalexpansion.plugins.jei.crafting.smelter;

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
	public String getRecipeCategoryUid(@Nonnull SmelterRecipeWrapper recipe) {

		return recipe.getUid();
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
