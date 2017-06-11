package cofh.thermalexpansion.plugins.jei.crafting.compactor;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

import javax.annotation.Nonnull;

public class CompactorRecipeHandler implements IRecipeHandler<CompactorRecipeWrapper> {

	@Nonnull
	@Override
	public Class<CompactorRecipeWrapper> getRecipeClass() {

		return CompactorRecipeWrapper.class;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid() {

		return null;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid(@Nonnull CompactorRecipeWrapper recipe) {

		return recipe.getUid();
	}

	@Nonnull
	@Override
	public IRecipeWrapper getRecipeWrapper(@Nonnull CompactorRecipeWrapper recipe) {

		return recipe;
	}

	@Override
	public boolean isRecipeValid(@Nonnull CompactorRecipeWrapper recipe) {

		return true;
	}

}
