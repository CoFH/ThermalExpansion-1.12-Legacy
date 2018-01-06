package cofh.thermalexpansion.plugins.jei.machine.centrifuge;

import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.item.ItemAugment;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.util.managers.machine.CentrifugeManager;
import cofh.thermalexpansion.util.managers.machine.CentrifugeManager.CentrifugeRecipe;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class CentrifugeRecipeCategoryMobs extends CentrifugeRecipeCategory {

	public static void initialize(IModRegistry registry) {

		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipes(getRecipes(guiHelper), RecipeUidsTE.CENTRIFUGE_MOBS);
		registry.addRecipeCatalyst(ItemAugment.machineCentrifugeMobs, RecipeUidsTE.CENTRIFUGE_MOBS);
		registry.addRecipeCatalyst(BlockMachine.machineCentrifuge, RecipeUidsTE.CENTRIFUGE_MOBS);
	}

	public static List<CentrifugeRecipeWrapper> getRecipes(IGuiHelper guiHelper) {

		List<CentrifugeRecipeWrapper> recipes = new ArrayList<>();

		for (CentrifugeRecipe recipe : CentrifugeManager.getRecipeListMobs()) {
			recipes.add(new CentrifugeRecipeWrapper(guiHelper, recipe, RecipeUidsTE.CENTRIFUGE_MOBS));
		}
		return recipes;
	}

	public CentrifugeRecipeCategoryMobs(IGuiHelper guiHelper) {

		super(guiHelper);

		localizedName = StringHelper.localize("item.thermalexpansion.augment.machineCentrifugeMobs.name");
	}

	@Nonnull
	@Override
	public String getUid() {

		return RecipeUidsTE.CENTRIFUGE_MOBS;
	}

}
