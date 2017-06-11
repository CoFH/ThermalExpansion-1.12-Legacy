package cofh.thermalexpansion.plugins.jei.crafting.transposer;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

import javax.annotation.Nonnull;

public class TransposerRecipeHandler implements IRecipeHandler<TransposerRecipeWrapper> {

	@Nonnull
	@Override
	public Class<TransposerRecipeWrapper> getRecipeClass() {

		return TransposerRecipeWrapper.class;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid() {

		return null;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid(@Nonnull TransposerRecipeWrapper recipe) {

		return recipe.getUid();
	}

	@Nonnull
	@Override
	public IRecipeWrapper getRecipeWrapper(@Nonnull TransposerRecipeWrapper recipe) {

		return recipe;
	}

	@Override
	public boolean isRecipeValid(@Nonnull TransposerRecipeWrapper recipe) {

		return true;
	}

}
