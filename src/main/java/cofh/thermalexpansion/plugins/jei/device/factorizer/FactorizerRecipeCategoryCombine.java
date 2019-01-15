package cofh.thermalexpansion.plugins.jei.device.factorizer;

import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.block.device.BlockDevice;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.util.managers.device.FactorizerManager;
import cofh.thermalexpansion.util.managers.device.FactorizerManager.FactorizerRecipe;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class FactorizerRecipeCategoryCombine extends FactorizerRecipeCategory {

	public static void initialize(IModRegistry registry) {

		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipes(getRecipes(guiHelper), RecipeUidsTE.FACTORIZER_COMBINE);
		registry.addRecipeCatalyst(BlockDevice.deviceFactorizer, RecipeUidsTE.FACTORIZER_COMBINE);
	}

	public static List<FactorizerRecipeWrapper> getRecipes(IGuiHelper guiHelper) {

		List<FactorizerRecipeWrapper> recipes = new ArrayList<>();

		for (FactorizerRecipe recipe : FactorizerManager.getRecipeList(false)) {
			recipes.add(new FactorizerRecipeWrapper(guiHelper, recipe, RecipeUidsTE.FACTORIZER_COMBINE));
		}
		return recipes;
	}

	public FactorizerRecipeCategoryCombine(IGuiHelper guiHelper) {

		super(guiHelper);

		localizedName += " - " + StringHelper.localize("gui.thermalexpansion.jei.factorizer.modeCombine");
	}

	@Nonnull
	@Override
	public String getUid() {

		return RecipeUidsTE.FACTORIZER_COMBINE;
	}

}
