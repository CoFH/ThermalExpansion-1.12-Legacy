package cofh.thermalexpansion.plugins.jei.crafting.insolator;

import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.item.ItemAugment;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager.InsolatorRecipe;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager.Type;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class InsolatorRecipeCategoryMycelium extends InsolatorRecipeCategory {

	public static void initialize(IModRegistry registry) {

		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipes(getRecipes(guiHelper), RecipeUidsTE.INSOLATOR_MYCELIUM);
		registry.addRecipeCatalyst(ItemAugment.machineInsolatorMycelium, RecipeUidsTE.INSOLATOR_MYCELIUM);
		registry.addRecipeCatalyst(BlockMachine.machineInsolator, RecipeUidsTE.INSOLATOR_MYCELIUM);
	}

	public static List<InsolatorRecipeWrapper> getRecipes(IGuiHelper guiHelper) {

		List<InsolatorRecipeWrapper> recipes = new ArrayList<>();

		for (InsolatorRecipe recipe : InsolatorManager.getRecipeList()) {
			if (recipe.getType() == Type.MYCELIUM || recipe.getType() == Type.MYCELIUM_TREE) {
				recipes.add(new InsolatorRecipeWrapper(guiHelper, recipe, RecipeUidsTE.INSOLATOR_MYCELIUM));
			}
		}
		return recipes;
	}

	public InsolatorRecipeCategoryMycelium(IGuiHelper guiHelper) {

		super(guiHelper);

		localizedName = StringHelper.localize("item.thermalexpansion.augment.machineInsolatorMycelium.name");
	}

	@Nonnull
	@Override
	public String getUid() {

		return RecipeUidsTE.INSOLATOR_MYCELIUM;
	}

}
