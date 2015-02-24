package cofh.thermalexpansion.plugins.nei.handlers;

import codechicken.core.ReflectionManager;
import codechicken.nei.NEIClientConfig;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.ShapedRecipeHandler;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.plugins.nei.handlers.NEIRecipeWrapper.RecipeType;
import cofh.thermalexpansion.util.crafting.RecipeMachine;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class RecipeHandlerCraftingMachine extends ShapedRecipeHandler {

	public static RecipeHandlerCraftingMachine instance = new RecipeHandlerCraftingMachine();

	@SuppressWarnings("unchecked")
	@Override
	public void loadCraftingRecipes(String outputId, Object... results) {

		if (outputId.equals("crafting")) {
			for (IRecipe r : (List<IRecipe>) CraftingManager.getInstance().getRecipeList()) {
				if (r.getClass() != NEIRecipeWrapper.class) {
					continue;
				}
				RecipeType type = ((NEIRecipeWrapper) r).getRecipeType();

				if (type != RecipeType.MACHINE) {
					continue;
				}
				IRecipe irecipe = ((NEIRecipeWrapper) r).getWrappedRecipe();

				CachedShapedRecipe recipe = null;
				if (irecipe instanceof RecipeMachine) {
					recipe = machineShapedRecipe((RecipeMachine) irecipe);
				} else if (irecipe instanceof ShapedRecipes) {
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

			if (type != RecipeType.MACHINE) {
				continue;
			}
			IRecipe irecipe = ((NEIRecipeWrapper) r).getWrappedRecipe();

			if (NEIServerUtils.areStacksSameTypeCrafting(irecipe.getRecipeOutput(), result)) {
				CachedShapedRecipe recipe = null;
				if (irecipe instanceof RecipeMachine) {
					recipe = machineShapedRecipe((RecipeMachine) irecipe);
				} else if (irecipe instanceof ShapedRecipes) {
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

			if (type != RecipeType.MACHINE) {
				continue;
			}
			IRecipe irecipe = ((NEIRecipeWrapper) r).getWrappedRecipe();

			CachedShapedRecipe recipe = null;
			if (irecipe instanceof RecipeMachine) {
				recipe = machineShapedRecipe((RecipeMachine) irecipe);
			} else if (irecipe instanceof ShapedRecipes) {
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

		return StringHelper.localize("recipe.thermalexpansion.machine");
	}

	public CachedMachineRecipe machineShapedRecipe(ShapedOreRecipe recipe) {

		int width;
		int height;
		try {
			width = ReflectionManager.getField(ShapedOreRecipe.class, Integer.class, recipe, 4);
			height = ReflectionManager.getField(ShapedOreRecipe.class, Integer.class, recipe, 5);
		} catch (Exception e) {
			NEIClientConfig.logger.error("Error loading recipe", e);
			return null;
		}

		Object[] items = recipe.getInput();
		for (Object item : items)
			if (item instanceof List && ((List<?>) item).isEmpty())// ore handler, no ores
				return null;

		return new CachedMachineRecipe(width, height, items, recipe.getRecipeOutput());
	}

	/* RECIPE CLASS */
	public class CachedMachineRecipe extends CachedShapedRecipe {

		public CachedMachineRecipe(int width, int height, Object[] items, ItemStack out) {

			super(3, 3, items, out);
		}

		public CachedMachineRecipe(RecipeMachine recipe) {

			super(3, 3, recipe.getInput(), recipe.getRecipeOutput());
		}

		@Override
		public List<PositionedStack> getIngredients() {

			for (int itemIndex = 0; itemIndex < ingredients.size(); itemIndex++) {
				if (itemIndex == 3) {
					ingredients.get(3).setPermutationToRender((cycleticks / 50) % 4);
				} else {
					randomRenderPermutation(ingredients.get(itemIndex), cycleticks / 20 + itemIndex);
				}
			}
			return ingredients;
		}

		@Override
		public PositionedStack getResult() {

			return result;
		}

		@Override
		public void computeVisuals() {

			for (PositionedStack p : ingredients) {
				p.generatePermutations();
			}
		}
	}

}
