package cofh.thermalexpansion.plugins.jei.crafting.enchanter;

import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.item.ItemAugment;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.util.managers.machine.EnchanterManager;
import cofh.thermalexpansion.util.managers.machine.EnchanterManager.EnchanterRecipe;
import cofh.thermalexpansion.util.managers.machine.EnchanterManager.Type;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class EnchanterRecipeCategoryEmpowered extends EnchanterRecipeCategory {

	public static void initialize(IModRegistry registry) {

		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipes(getRecipes(guiHelper), RecipeUidsTE.ENCHANTER_EMPOWERED);
		registry.addRecipeCatalyst(ItemAugment.machineEnchanterEmpowered, RecipeUidsTE.ENCHANTER_EMPOWERED);
		registry.addRecipeCatalyst(BlockMachine.machineEnchanter, RecipeUidsTE.ENCHANTER_EMPOWERED);
	}

	public static List<EnchanterRecipeWrapper> getRecipes(IGuiHelper guiHelper) {

		List<EnchanterRecipeWrapper> recipes = new ArrayList<>();

		for (EnchanterRecipe recipe : EnchanterManager.getRecipeList()) {
			if (recipe.getType() == Type.EMPOWERED) {
				recipes.add(new EnchanterRecipeWrapper(guiHelper, recipe, RecipeUidsTE.ENCHANTER_EMPOWERED));
			}
		}
		return recipes;
	}

	public EnchanterRecipeCategoryEmpowered(IGuiHelper guiHelper) {

		super(guiHelper);

		localizedName = StringHelper.localize("item.thermalexpansion.augment.machineEnchanterEmpowered.name");
	}

	@Nonnull
	@Override
	public String getUid() {

		return RecipeUidsTE.ENCHANTER_EMPOWERED;
	}

}
