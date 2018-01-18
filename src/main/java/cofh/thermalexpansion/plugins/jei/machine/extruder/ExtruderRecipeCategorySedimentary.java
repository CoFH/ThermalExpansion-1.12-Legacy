package cofh.thermalexpansion.plugins.jei.machine.extruder;

import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.item.ItemAugment;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.util.managers.machine.ExtruderManager;
import cofh.thermalexpansion.util.managers.machine.ExtruderManager.ExtruderRecipe;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ExtruderRecipeCategorySedimentary extends ExtruderRecipeCategory {

	public static void initialize(IModRegistry registry) {

		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipes(getRecipes(guiHelper), RecipeUidsTE.EXTRUDER_SEDIMENTARY);
		registry.addRecipeCatalyst(ItemAugment.machineExtruderSedimentary, RecipeUidsTE.EXTRUDER_SEDIMENTARY);
		registry.addRecipeCatalyst(BlockMachine.machineExtruder, RecipeUidsTE.EXTRUDER_SEDIMENTARY);
	}

	public static List<ExtruderRecipeWrapper> getRecipes(IGuiHelper guiHelper) {

		List<ExtruderRecipeWrapper> recipes = new ArrayList<>();

		for (ExtruderRecipe recipe : ExtruderManager.getRecipeList(true)) {
			recipes.add(new ExtruderRecipeWrapper(guiHelper, recipe, RecipeUidsTE.EXTRUDER_SEDIMENTARY));
		}
		return recipes;
	}

	public ExtruderRecipeCategorySedimentary(IGuiHelper guiHelper) {

		super(guiHelper);

		localizedName = StringHelper.localize("item.thermalexpansion.augment.machineExtruderSedimentary.name");
	}

	@Nonnull
	@Override
	public String getUid() {

		return RecipeUidsTE.EXTRUDER_SEDIMENTARY;
	}

}
