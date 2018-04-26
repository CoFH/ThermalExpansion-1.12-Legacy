package cofh.thermalexpansion.plugins.jei.device.factorizer;

import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.gui.client.device.GuiFactorizer;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.plugins.jei.machine.BaseRecipeCategory;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;

import java.util.List;

public abstract class FactorizerRecipeCategory extends BaseRecipeCategory<FactorizerRecipeWrapper> {

	public static boolean enable = true;

	public static void register(IRecipeCategoryRegistration registry) {

		if (!enable) {
			return;
		}
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipeCategories(new FactorizerRecipeCategoryCombine(guiHelper));
		registry.addRecipeCategories(new FactorizerRecipeCategorySplit(guiHelper));
	}

	public static void initialize(IModRegistry registry) {

		if (!enable) {
			return;
		}
		FactorizerRecipeCategoryCombine.initialize(registry);
		FactorizerRecipeCategorySplit.initialize(registry);
		registry.addRecipeClickArea(GuiFactorizer.class, 79, 25, 24, 16, RecipeUidsTE.FACTORIZER_COMBINE, RecipeUidsTE.FACTORIZER_SPLIT);
	}

	public FactorizerRecipeCategory(IGuiHelper guiHelper) {

		background = guiHelper.createDrawable(GuiFactorizer.TEXTURE, 26, 11, 124, 62, 0, 0, 16, 24);
		localizedName = StringHelper.localize("tile.thermalexpansion.device.factorizer.name");
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, FactorizerRecipeWrapper recipeWrapper, IIngredients ingredients) {

		List<List<ItemStack>> inputs = ingredients.getInputs(ItemStack.class);
		List<List<ItemStack>> outputs = ingredients.getOutputs(ItemStack.class);

		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

		guiItemStacks.init(0, true, 33, 14);
		guiItemStacks.init(1, false, 105, 14);

		guiItemStacks.set(0, inputs.get(0));
		guiItemStacks.set(1, outputs.get(0));
	}

}
