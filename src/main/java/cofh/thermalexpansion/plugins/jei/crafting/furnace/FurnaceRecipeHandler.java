package cofh.thermalexpansion.plugins.jei.crafting.furnace;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

import javax.annotation.Nonnull;

public class FurnaceRecipeHandler implements IRecipeHandler<FurnaceRecipeWrapper> {

	@Nonnull
	@Override
	public Class<FurnaceRecipeWrapper> getRecipeClass() {

		return FurnaceRecipeWrapper.class;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid(@Nonnull FurnaceRecipeWrapper recipe) {

		return recipe.getUid();
	}

	@Nonnull
	@Override
	public IRecipeWrapper getRecipeWrapper(@Nonnull FurnaceRecipeWrapper recipe) {

		return recipe;
	}

	@Override
	public boolean isRecipeValid(@Nonnull FurnaceRecipeWrapper recipe) {

		return true;
	}

}
