package cofh.thermalexpansion.plugins.jei.crafting.brewer;

import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.gui.client.machine.GuiBrewer;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.plugins.jei.crafting.BaseRecipeCategory;
import cofh.thermalexpansion.util.managers.machine.BrewerManager;
import cofh.thermalexpansion.util.managers.machine.BrewerManager.BrewerRecipe;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class BrewerRecipeCategory extends BaseRecipeCategory<BrewerRecipeWrapper> {

	public static boolean enable = true;

	public static void register(IRecipeCategoryRegistration registry) {

		if (!enable) {
			return;
		}
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipeCategories(new BrewerRecipeCategory(guiHelper));
	}

	public static void initialize(IModRegistry registry) {

		if (!enable) {
			return;
		}
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipes(getRecipes(guiHelper), RecipeUidsTE.BREWER);
		registry.addRecipeClickArea(GuiBrewer.class, 79, 34, 24, 16, RecipeUidsTE.BREWER);
		registry.addRecipeCatalyst(BlockMachine.machineBrewer, RecipeUidsTE.BREWER);
	}

	public static List<BrewerRecipeWrapper> getRecipes(IGuiHelper guiHelper) {

		List<BrewerRecipeWrapper> recipes = new ArrayList<>();

		for (BrewerRecipe recipe : BrewerManager.getRecipeList()) {
			recipes.add(new BrewerRecipeWrapper(guiHelper, recipe));
		}
		return recipes;
	}

	public BrewerRecipeCategory(IGuiHelper guiHelper) {

		background = guiHelper.createDrawable(GuiBrewer.TEXTURE, 26, 11, 124, 62, 0, 0, 16, 24);
		energyMeter = Drawables.getDrawables(guiHelper).getEnergyEmpty();
		localizedName = StringHelper.localize("tile.thermalexpansion.machine.brewer.name");
	}

	@Nonnull
	@Override
	public String getUid() {

		return RecipeUidsTE.BREWER;
	}

	@Override
	public void drawExtras(@Nonnull Minecraft minecraft) {

		energyMeter.draw(minecraft, 2, 8);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, BrewerRecipeWrapper recipeWrapper, IIngredients ingredients) {

		List<List<ItemStack>> inputs = ingredients.getInputs(ItemStack.class);
		List<List<ItemStack>> outputs = ingredients.getOutputs(ItemStack.class);

		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

		guiItemStacks.init(0, true, 21, 14);
		guiItemStacks.init(1, true, 45, 14);
		guiItemStacks.init(2, false, 105, 23);

		guiItemStacks.set(0, inputs.get(0));
		guiItemStacks.set(1, inputs.get(1));
		guiItemStacks.set(2, outputs.get(0));
	}

}
