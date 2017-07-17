package cofh.thermalexpansion.plugins.jei.crafting.crucible;

import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.item.ItemAugment;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.util.managers.machine.CrucibleManager;
import cofh.thermalexpansion.util.managers.machine.CrucibleManager.CrucibleRecipe;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class CrucibleRecipeCategoryLava extends CrucibleRecipeCategory {

	public static void initialize(IModRegistry registry) {

		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipes(getRecipes(guiHelper), RecipeUidsTE.CRUCIBLE_LAVA);
		registry.addRecipeCatalyst(ItemAugment.machineCrucibleLava, RecipeUidsTE.CRUCIBLE_LAVA);
		registry.addRecipeCatalyst(BlockMachine.machineCrucible, RecipeUidsTE.CRUCIBLE_LAVA);
	}

	public static List<CrucibleRecipeWrapper> getRecipes(IGuiHelper guiHelper) {

		List<CrucibleRecipeWrapper> recipes = new ArrayList<>();

		for (CrucibleRecipe recipe : CrucibleManager.getRecipeList()) {
			if (CrucibleManager.isLava(recipe.getInput())) {
				recipes.add(new CrucibleRecipeWrapper(guiHelper, recipe, RecipeUidsTE.CRUCIBLE_LAVA));
			}
		}
		return recipes;
	}

	public CrucibleRecipeCategoryLava(IGuiHelper guiHelper) {

		super(guiHelper);

		localizedName = StringHelper.localize("item.thermalexpansion.augment.machineCrucibleLava.name");
	}

	@Nonnull
	@Override
	public String getUid() {

		return RecipeUidsTE.CRUCIBLE_LAVA;
	}

}
