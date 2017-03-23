package cofh.thermalexpansion.plugins.jei.crafting.furnace;

import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.item.ItemAugment;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.util.crafting.FurnaceManager;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class FurnaceRecipeCategoryFood extends FurnaceRecipeCategory {

	public static void initialize(IModRegistry registry) {

		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipeCategories(new FurnaceRecipeCategoryFood(guiHelper));
		registry.addRecipes(getRecipes(guiHelper));
		registry.addRecipeCategoryCraftingItem(ItemAugment.machineFurnaceFood, RecipeUidsTE.FURNACE_FOOD);
		registry.addRecipeCategoryCraftingItem(BlockMachine.machineFurnace, RecipeUidsTE.FURNACE_FOOD);
	}

	public static List<FurnaceRecipeWrapper> getRecipes(IGuiHelper guiHelper) {

		List<FurnaceRecipeWrapper> recipes = new ArrayList<>();

		for (FurnaceManager.RecipeFurnace recipe : FurnaceManager.getRecipeList()) {
			if (FurnaceManager.isFood(recipe.getInput())) {
				recipes.add(new FurnaceRecipeWrapper(guiHelper, recipe, RecipeUidsTE.FURNACE_FOOD));
			}
		}
		return recipes;
	}

	public FurnaceRecipeCategoryFood(IGuiHelper guiHelper) {

		super(guiHelper);

		localizedName = StringHelper.localize("item.thermalexpansion.augment.machineFurnaceFood.name");
	}

	@Nonnull
	@Override
	public String getUid() {

		return RecipeUidsTE.FURNACE_FOOD;
	}

}
