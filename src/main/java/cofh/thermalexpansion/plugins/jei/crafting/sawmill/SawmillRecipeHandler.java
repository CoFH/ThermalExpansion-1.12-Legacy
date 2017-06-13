package cofh.thermalexpansion.plugins.jei.crafting.sawmill;

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
	public String getRecipeCategoryUid(@Nonnull SawmillRecipeWrapper recipe) {

		return recipe.getUid();
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
