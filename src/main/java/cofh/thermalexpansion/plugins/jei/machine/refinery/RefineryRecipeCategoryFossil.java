package cofh.thermalexpansion.plugins.jei.machine.refinery;

import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.item.ItemAugment;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.util.managers.machine.RefineryManager;
import cofh.thermalexpansion.util.managers.machine.RefineryManager.RefineryRecipe;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class RefineryRecipeCategoryFossil extends RefineryRecipeCategory {

	public static void initialize(IModRegistry registry) {

		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipes(getRecipes(guiHelper), RecipeUidsTE.REFINERY_FOSSIL);
		registry.addRecipeCatalyst(ItemAugment.machineRefineryFossil, RecipeUidsTE.REFINERY_FOSSIL);
		registry.addRecipeCatalyst(BlockMachine.machineRefinery, RecipeUidsTE.REFINERY_FOSSIL);
	}

	public static List<RefineryRecipeWrapper> getRecipes(IGuiHelper guiHelper) {

		List<RefineryRecipeWrapper> recipes = new ArrayList<>();

		for (RefineryRecipe recipe : RefineryManager.getRecipeList()) {
			if (RefineryManager.isFossilFuel(recipe.getInput())) {
				recipes.add(new RefineryRecipeWrapper(guiHelper, recipe, RecipeUidsTE.REFINERY_FOSSIL));
			}
		}
		return recipes;
	}

	public RefineryRecipeCategoryFossil(IGuiHelper guiHelper) {

		super(guiHelper);

		localizedName = StringHelper.localize("item.thermalexpansion.augment.machineRefineryFossil.name");
	}

	@Nonnull
	@Override
	public String getUid() {

		return RecipeUidsTE.REFINERY_FOSSIL;
	}

}
