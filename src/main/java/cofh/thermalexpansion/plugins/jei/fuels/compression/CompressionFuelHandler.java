package cofh.thermalexpansion.plugins.jei.fuels.compression;

import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

import javax.annotation.Nonnull;

public class CompressionFuelHandler implements IRecipeHandler<CompressionFuelWrapper> {

	@Nonnull
	@Override
	public Class<CompressionFuelWrapper> getRecipeClass() {

		return CompressionFuelWrapper.class;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid() {

		return RecipeUidsTE.DYNAMO_COMPRESSION;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid(@Nonnull CompressionFuelWrapper recipe) {

		return getRecipeCategoryUid();
	}

	@Nonnull
	@Override
	public IRecipeWrapper getRecipeWrapper(@Nonnull CompressionFuelWrapper recipe) {

		return recipe;
	}

	@Override
	public boolean isRecipeValid(@Nonnull CompressionFuelWrapper recipe) {

		return true;
	}

}
