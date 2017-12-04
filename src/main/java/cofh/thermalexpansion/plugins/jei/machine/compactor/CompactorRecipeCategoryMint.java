package cofh.thermalexpansion.plugins.jei.machine.compactor;

import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.item.ItemAugment;
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

public class CompactorRecipeCategoryMint extends CompactorRecipeCategory {

	public static void initialize(IModRegistry registry) {

		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipes(getRecipes(guiHelper), RecipeUidsTE.COMPACTOR_MINT);
		registry.addRecipeCatalyst(ItemAugment.machineCompactorMint, RecipeUidsTE.COMPACTOR_MINT);
		registry.addRecipeCatalyst(BlockMachine.machineCompactor, RecipeUidsTE.COMPACTOR_MINT);
	}

	public static List<CompactorRecipeWrapper> getRecipes(IGuiHelper guiHelper) {

		List<CompactorRecipeWrapper> recipes = new ArrayList<>();

		for (CompactorRecipe recipe : CompactorManager.getRecipeList(Mode.MINT)) {
			recipes.add(new CompactorRecipeWrapper(guiHelper, recipe, RecipeUidsTE.COMPACTOR_MINT));
		}
		return recipes;
	}

	public CompactorRecipeCategoryMint(IGuiHelper guiHelper) {

		super(guiHelper);

		localizedName += " - " + StringHelper.localize("gui.thermalexpansion.jei.compactor.modeMint");
	}

	@Nonnull
	@Override
	public String getUid() {

		return RecipeUidsTE.COMPACTOR_MINT;
	}

}
