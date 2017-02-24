package cofh.thermalexpansion.plugins.jei.compactor;

import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.util.crafting.CompactorManager;
import cofh.thermalexpansion.util.crafting.CompactorManager.Mode;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class CompactorRecipeCategoryStorage extends CompactorRecipeCategory {

	public static void initialize(IModRegistry registry) {

		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipeCategories(new CompactorRecipeCategoryStorage(guiHelper));
		registry.addRecipes(getRecipes());
		registry.addRecipeCategoryCraftingItem(BlockMachine.machineCompactor, RecipeUidsTE.COMPACTOR_STORAGE);
	}

	public static List<CompactorRecipeWrapper> getRecipes() {

		List<CompactorRecipeWrapper> recipes = new ArrayList<>();

		for (CompactorManager.RecipeCompactor recipe : CompactorManager.getRecipeList(Mode.STORAGE)) {
			recipes.add(new CompactorRecipeWrapper(recipe, Mode.STORAGE));
		}
		return recipes;
	}

	public CompactorRecipeCategoryStorage(IGuiHelper guiHelper) {

		super(guiHelper);

		localizedName += " - " + StringHelper.localize("gui.thermalexpansion.machine.compactor.modeStorage");
	}

	@Nonnull
	@Override
	public String getUid() {

		return RecipeUidsTE.COMPACTOR_STORAGE;
	}

}
