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

public class CompactorRecipeCategoryCoin extends CompactorRecipeCategory {

	public static void initialize(IModRegistry registry) {

		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipes(getRecipes(guiHelper), RecipeUidsTE.COMPACTOR_COIN);
		registry.addRecipeCatalyst(ItemAugment.machineCompactorCoin, RecipeUidsTE.COMPACTOR_COIN);
		registry.addRecipeCatalyst(BlockMachine.machineCompactor, RecipeUidsTE.COMPACTOR_COIN);
	}

	public static List<CompactorRecipeWrapper> getRecipes(IGuiHelper guiHelper) {

		List<CompactorRecipeWrapper> recipes = new ArrayList<>();

		for (CompactorRecipe recipe : CompactorManager.getRecipeList(Mode.COIN)) {
			recipes.add(new CompactorRecipeWrapper(guiHelper, recipe, RecipeUidsTE.COMPACTOR_COIN));
		}
		return recipes;
	}

	public CompactorRecipeCategoryCoin(IGuiHelper guiHelper) {

		super(guiHelper);

		localizedName = StringHelper.localize("item.thermalexpansion.augment.machineCompactorCoin.name");
	}

	@Nonnull
	@Override
	public String getUid() {

		return RecipeUidsTE.COMPACTOR_COIN;
	}

}
