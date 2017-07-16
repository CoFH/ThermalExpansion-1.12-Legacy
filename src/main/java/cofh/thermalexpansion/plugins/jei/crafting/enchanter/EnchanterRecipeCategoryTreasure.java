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

public class EnchanterRecipeCategoryTreasure extends EnchanterRecipeCategory {

	public static void initialize(IModRegistry registry) {

		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipes(getRecipes(guiHelper), RecipeUidsTE.ENCHANTER_TREASURE);
		registry.addRecipeCatalyst(ItemAugment.machineEnchanterTreasure, RecipeUidsTE.ENCHANTER_TREASURE);
		registry.addRecipeCatalyst(BlockMachine.machineEnchanter, RecipeUidsTE.ENCHANTER_TREASURE);
	}

	public static List<EnchanterRecipeWrapper> getRecipes(IGuiHelper guiHelper) {

		List<EnchanterRecipeWrapper> recipes = new ArrayList<>();

		for (EnchanterRecipe recipe : EnchanterManager.getRecipeList()) {
			if (recipe.getType() == Type.TREASURE || recipe.getType() == Type.TREASURE_EMPOWERED) {
				recipes.add(new EnchanterRecipeWrapper(guiHelper, recipe, RecipeUidsTE.ENCHANTER_TREASURE));
			}
		}
		return recipes;
	}

	public EnchanterRecipeCategoryTreasure(IGuiHelper guiHelper) {

		super(guiHelper);

		localizedName = StringHelper.localize("item.thermalexpansion.augment.machineEnchanterTreasure.name");
	}

	@Nonnull
	@Override
	public String getUid() {

		return RecipeUidsTE.ENCHANTER_TREASURE;
	}

}
