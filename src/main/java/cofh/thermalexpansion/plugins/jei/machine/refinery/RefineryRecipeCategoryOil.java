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

public class RefineryRecipeCategoryOil extends RefineryRecipeCategory {

	public static void initialize(IModRegistry registry) {

		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipes(getRecipes(guiHelper), RecipeUidsTE.REFINERY_OIL);
		registry.addRecipeCatalyst(ItemAugment.machineRefineryOil, RecipeUidsTE.REFINERY_OIL);
		registry.addRecipeCatalyst(BlockMachine.machineRefinery, RecipeUidsTE.REFINERY_OIL);
	}

	public static List<RefineryRecipeWrapper> getRecipes(IGuiHelper guiHelper) {

		List<RefineryRecipeWrapper> recipes = new ArrayList<>();

		for (RefineryRecipe recipe : RefineryManager.getRecipeList()) {
			if (RefineryManager.isFossilFuel(recipe.getInput())) {
				recipes.add(new RefineryRecipeWrapper(guiHelper, recipe, RecipeUidsTE.REFINERY_OIL));
			}
		}
		return recipes;
	}

	public RefineryRecipeCategoryOil(IGuiHelper guiHelper) {

		super(guiHelper);

		localizedName = StringHelper.localize("item.thermalexpansion.augment.machineRefineryOil.name");
	}

	@Nonnull
	@Override
	public String getUid() {

		return RecipeUidsTE.REFINERY_OIL;
	}

}
