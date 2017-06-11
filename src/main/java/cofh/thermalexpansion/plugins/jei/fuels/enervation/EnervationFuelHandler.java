package cofh.thermalexpansion.plugins.jei.fuels.enervation;

import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

import javax.annotation.Nonnull;

public class EnervationFuelHandler implements IRecipeHandler<EnervationFuelWrapper> {

	@Nonnull
	@Override
	public Class<EnervationFuelWrapper> getRecipeClass() {

		return EnervationFuelWrapper.class;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid() {

		return RecipeUidsTE.DYNAMO_ENERVATION;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid(@Nonnull EnervationFuelWrapper recipe) {

		return getRecipeCategoryUid();
	}

	@Nonnull
	@Override
	public IRecipeWrapper getRecipeWrapper(@Nonnull EnervationFuelWrapper recipe) {

		return recipe;
	}

	@Override
	public boolean isRecipeValid(@Nonnull EnervationFuelWrapper recipe) {

		return true;
	}

}
