package cofh.thermalexpansion.plugins.nei.handlers;

import codechicken.nei.NEIServerUtils;
import codechicken.nei.recipe.ShapedRecipeHandler;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.plugins.nei.handlers.NEIRecipeWrapper.RecipeType;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class RecipeHandlerCraftingUpgrade extends ShapedRecipeHandler {

	public static RecipeHandlerCraftingUpgrade instance = new RecipeHandlerCraftingUpgrade();

	@SuppressWarnings("unchecked")
	@Override
	public void loadCraftingRecipes(String outputId, Object... results) {

		if (outputId.equals("crafting")) {
			for (IRecipe r : (List<IRecipe>) CraftingManager.getInstance().getRecipeList()) {
				if (r.getClass() != NEIRecipeWrapper.class) {
					continue;
				}
				RecipeType type = ((NEIRecipeWrapper) r).getRecipeType();

				if (type != RecipeType.UPGRADE) {
					continue;
				}
				IRecipe irecipe = ((NEIRecipeWrapper) r).getWrappedRecipe();

				CachedShapedRecipe recipe = null;
				if (irecipe instanceof ShapedRecipes) {
					recipe = new CachedShapedRecipe((ShapedRecipes) irecipe);
				} else if (irecipe instanceof ShapedOreRecipe) {
					recipe = forgeShapedRecipe((ShapedOreRecipe) irecipe);
				}
				if (recipe == null) {
					continue;
				}
				recipe.computeVisuals();
				arecipes.add(recipe);
			}
		} else {
			super.loadCraftingRecipes(outputId, results);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void loadCraftingRecipes(ItemStack result) {

		for (IRecipe r : (List<IRecipe>) CraftingManager.getInstance().getRecipeList()) {
			if (r.getClass() != NEIRecipeWrapper.class) {
				continue;
			}
			RecipeType type = ((NEIRecipeWrapper) r).getRecipeType();

			if (type != RecipeType.UPGRADE) {
				continue;
			}
			IRecipe irecipe = ((NEIRecipeWrapper) r).getWrappedRecipe();

			if (NEIServerUtils.areStacksSameTypeCrafting(irecipe.getRecipeOutput(), result)) {
				CachedShapedRecipe recipe = null;
				if (irecipe instanceof ShapedRecipes) {
					recipe = new CachedShapedRecipe((ShapedRecipes) irecipe);
				} else if (irecipe instanceof ShapedOreRecipe) {
					recipe = forgeShapedRecipe((ShapedOreRecipe) irecipe);
				}
				if (recipe == null) {
					continue;
				}
				recipe.computeVisuals();
				arecipes.add(recipe);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void loadUsageRecipes(ItemStack ingredient) {

		for (IRecipe r : (List<IRecipe>) CraftingManager.getInstance().getRecipeList()) {
			if (r.getClass() != NEIRecipeWrapper.class) {
				continue;
			}
			RecipeType type = ((NEIRecipeWrapper) r).getRecipeType();

			if (type != RecipeType.UPGRADE) {
				continue;
			}
			IRecipe irecipe = ((NEIRecipeWrapper) r).getWrappedRecipe();

			CachedShapedRecipe recipe = null;
			if (irecipe instanceof ShapedRecipes) {
				recipe = new CachedShapedRecipe((ShapedRecipes) irecipe);
			} else if (irecipe instanceof ShapedOreRecipe) {
				recipe = forgeShapedRecipe((ShapedOreRecipe) irecipe);
			}
			if (recipe == null || !recipe.contains(recipe.ingredients, ingredient.getItem())) {
				continue;
			}
			recipe.computeVisuals();
			if (recipe.contains(recipe.ingredients, ingredient)) {
				recipe.setIngredientPermutation(recipe.ingredients, ingredient);
				arecipes.add(recipe);
			}
		}
	}

	@Override
	public String getRecipeName() {

		return StringHelper.localize("recipe.thermalexpansion.upgrade");
	}

}
