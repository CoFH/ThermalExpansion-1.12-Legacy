package cofh.thermalexpansion.plugins.jei.crafting.charger;

import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

import javax.annotation.Nonnull;

public class ChargerRecipeHandler implements IRecipeHandler<ChargerRecipeWrapper> {

	@Nonnull
	@Override
	public Class<ChargerRecipeWrapper> getRecipeClass() {

		return ChargerRecipeWrapper.class;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid() {

		return RecipeUidsTE.CHARGER;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid(@Nonnull ChargerRecipeWrapper recipe) {

		return getRecipeCategoryUid();
	}

	@Nonnull
	@Override
	public IRecipeWrapper getRecipeWrapper(@Nonnull ChargerRecipeWrapper recipe) {

		return recipe;
	}

	@Override
	public boolean isRecipeValid(@Nonnull ChargerRecipeWrapper recipe) {

		return true;
	}

}
