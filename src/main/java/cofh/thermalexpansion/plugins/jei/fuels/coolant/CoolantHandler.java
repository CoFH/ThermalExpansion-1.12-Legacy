package cofh.thermalexpansion.plugins.jei.fuels.coolant;

import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

import javax.annotation.Nonnull;

public class CoolantHandler implements IRecipeHandler<CoolantWrapper> {

	@Nonnull
	@Override
	public Class<CoolantWrapper> getRecipeClass() {

		return CoolantWrapper.class;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid() {

		return RecipeUidsTE.COOLANT;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid(@Nonnull CoolantWrapper recipe) {

		return getRecipeCategoryUid();
	}

	@Nonnull
	@Override
	public IRecipeWrapper getRecipeWrapper(@Nonnull CoolantWrapper recipe) {

		return recipe;
	}

	@Override
	public boolean isRecipeValid(@Nonnull CoolantWrapper recipe) {

		return true;
	}

}
