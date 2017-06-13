package cofh.thermalexpansion.plugins.jei.crafting.crucible;

import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

import javax.annotation.Nonnull;

public class CrucibleRecipeHandler implements IRecipeHandler<CrucibleRecipeWrapper> {

	@Nonnull
	@Override
	public Class<CrucibleRecipeWrapper> getRecipeClass() {

		return CrucibleRecipeWrapper.class;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid(@Nonnull CrucibleRecipeWrapper recipe) {

		return RecipeUidsTE.CRUCIBLE;
	}

	@Nonnull
	@Override
	public IRecipeWrapper getRecipeWrapper(@Nonnull CrucibleRecipeWrapper recipe) {

		return recipe;
	}

	@Override
	public boolean isRecipeValid(@Nonnull CrucibleRecipeWrapper recipe) {

		return true;
	}

}
