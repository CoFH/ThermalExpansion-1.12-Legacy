package cofh.thermalexpansion.plugins.jei;

import cofh.thermalexpansion.plugins.jei.Catagory.Categories;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

import javax.annotation.Nonnull;

public class RecipeHandler implements IRecipeHandler<MachineRecipeWrapper> {

    @Nonnull
    @Override
    public Class<MachineRecipeWrapper> getRecipeClass() {
        return MachineRecipeWrapper.class;
    }

    @Nonnull
    @Override
    public String getRecipeCategoryUid() {
        return Categories.getDefualt();
    }

    @Nonnull
    @Override
    public String getRecipeCategoryUid(MachineRecipeWrapper recipe) {
        return recipe.getType().getCategory().getUid();
    }

    @Nonnull
    @Override
    public IRecipeWrapper getRecipeWrapper(MachineRecipeWrapper recipe) {
        return recipe;
    }

    @Nonnull
    @Override
    public boolean isRecipeValid(MachineRecipeWrapper recipe) {
        return recipe.getType() != null;
    }
}