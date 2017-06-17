package cofh.thermalexpansion.plugins.jei.crafting.compactor;

import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.gui.client.machine.GuiCompactor;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.util.managers.machine.CompactorManager;
import cofh.thermalexpansion.util.managers.machine.CompactorManager.CompactorRecipe;
import cofh.thermalexpansion.util.managers.machine.CompactorManager.Mode;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class CompactorRecipeCategoryPress extends CompactorRecipeCategory {

	public static void initialize(IModRegistry registry) {

		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipes(getRecipes(guiHelper), RecipeUidsTE.COMPACTOR_PRESS);
		registry.addRecipeCatalyst(BlockMachine.machineCompactor, RecipeUidsTE.COMPACTOR_PRESS);
	}

	public static List<CompactorRecipeWrapper> getRecipes(IGuiHelper guiHelper) {

		List<CompactorRecipeWrapper> recipes = new ArrayList<>();

		for (CompactorRecipe recipe : CompactorManager.getRecipeList(Mode.PRESS)) {
			recipes.add(new CompactorRecipeWrapper(guiHelper, recipe, RecipeUidsTE.COMPACTOR_PRESS));
		}
		return recipes;
	}

	public CompactorRecipeCategoryPress(IGuiHelper guiHelper) {

		super(guiHelper);

		localizedName += " - " + StringHelper.localize("gui.thermalexpansion.jei.compactor.modePress");

		icon = guiHelper.createDrawable(GuiCompactor.TEXTURE, 176, 48, 16, 16);
	}

	@Nonnull
	@Override
	public String getUid() {

		return RecipeUidsTE.COMPACTOR_PRESS;
	}

}
