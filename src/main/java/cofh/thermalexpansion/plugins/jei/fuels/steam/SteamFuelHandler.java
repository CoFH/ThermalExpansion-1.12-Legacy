package cofh.thermalexpansion.plugins.jei.fuels.steam;

import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

import javax.annotation.Nonnull;

public class SteamFuelHandler implements IRecipeHandler<SteamFuelWrapper> {

	@Nonnull
	@Override
	public Class<SteamFuelWrapper> getRecipeClass() {

		return SteamFuelWrapper.class;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid() {

		return RecipeUidsTE.DYNAMO_STEAM;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid(@Nonnull SteamFuelWrapper recipe) {

		return getRecipeCategoryUid();
	}

	@Nonnull
	@Override
	public IRecipeWrapper getRecipeWrapper(@Nonnull SteamFuelWrapper recipe) {

		return recipe;
	}

	@Override
	public boolean isRecipeValid(@Nonnull SteamFuelWrapper recipe) {

		return true;
	}

}
