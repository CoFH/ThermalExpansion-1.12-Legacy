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

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class CompactorRecipeCategoryStorage extends CompactorRecipeCategory {

	public static void initialize(IModRegistry registry) {

		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipes(getRecipes(guiHelper), RecipeUidsTE.COMPACTOR_STORAGE);
		registry.addRecipeCatalyst(BlockMachine.machineCompactor, RecipeUidsTE.COMPACTOR_STORAGE);
	}

	public static List<CompactorRecipeWrapper> getRecipes(IGuiHelper guiHelper) {

		List<CompactorRecipeWrapper> recipes = new ArrayList<>();

		for (CompactorRecipe recipe : CompactorManager.getRecipeList(Mode.STORAGE)) {
			recipes.add(new CompactorRecipeWrapper(guiHelper, recipe, RecipeUidsTE.COMPACTOR_STORAGE));
		}
		return recipes;
	}

	public CompactorRecipeCategoryStorage(IGuiHelper guiHelper) {

		super(guiHelper);

		localizedName += " - " + StringHelper.localize("gui.thermalexpansion.jei.compactor.modeStorage");

		icon = guiHelper.createDrawable(GuiCompactor.TEXTURE, 192, 48, 16, 16);
	}

	@Nonnull
	@Override
	public String getUid() {

		return RecipeUidsTE.COMPACTOR_STORAGE;
	}

}
