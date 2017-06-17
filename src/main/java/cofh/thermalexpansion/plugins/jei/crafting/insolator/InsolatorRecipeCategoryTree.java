package cofh.thermalexpansion.plugins.jei.crafting.insolator;

import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.item.ItemAugment;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager.InsolatorRecipe;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager.Type;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class InsolatorRecipeCategoryTree extends InsolatorRecipeCategory {

	public static void initialize(IModRegistry registry) {

		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		((IRecipeCategoryRegistration) registry).addRecipeCategories(new InsolatorRecipeCategoryTree(guiHelper));
		registry.addRecipes(getRecipes(guiHelper), RecipeUidsTE.INSOLATOR_TREE);
		registry.addRecipeCatalyst(ItemAugment.machineInsolatorTree, RecipeUidsTE.INSOLATOR_TREE);
		registry.addRecipeCatalyst(BlockMachine.machineInsolator, RecipeUidsTE.INSOLATOR_TREE);
	}

	public static List<InsolatorRecipeWrapper> getRecipes(IGuiHelper guiHelper) {

		List<InsolatorRecipeWrapper> recipes = new ArrayList<>();

		for (InsolatorRecipe recipe : InsolatorManager.getRecipeList()) {

			if (recipe.getType() == Type.TREE) {
				recipes.add(new InsolatorRecipeWrapper(guiHelper, recipe, RecipeUidsTE.INSOLATOR_TREE));
			}
		}
		return recipes;
	}

	public InsolatorRecipeCategoryTree(IGuiHelper guiHelper) {

		super(guiHelper);

		localizedName = StringHelper.localize("item.thermalexpansion.augment.machineInsolatorTree.name");
	}

	@Nonnull
	@Override
	public String getUid() {

		return RecipeUidsTE.INSOLATOR_TREE;
	}

}
