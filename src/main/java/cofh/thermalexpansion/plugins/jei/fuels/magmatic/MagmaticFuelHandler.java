package cofh.thermalexpansion.plugins.jei.fuels.magmatic;

import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

import javax.annotation.Nonnull;

public class MagmaticFuelHandler implements IRecipeHandler<MagmaticFuelWrapper> {

	@Nonnull
	@Override
	public Class<MagmaticFuelWrapper> getRecipeClass() {

		return MagmaticFuelWrapper.class;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid() {

		return RecipeUidsTE.DYNAMO_MAGMATIC;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid(@Nonnull MagmaticFuelWrapper recipe) {

		return getRecipeCategoryUid();
	}

	@Nonnull
	@Override
	public IRecipeWrapper getRecipeWrapper(@Nonnull MagmaticFuelWrapper recipe) {

		return recipe;
	}

	@Override
	public boolean isRecipeValid(@Nonnull MagmaticFuelWrapper recipe) {

		return true;
	}

}
