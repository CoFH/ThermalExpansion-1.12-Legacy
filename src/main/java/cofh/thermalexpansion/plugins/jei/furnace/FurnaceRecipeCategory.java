package cofh.thermalexpansion.plugins.jei.furnace;

import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.gui.client.machine.GuiFurnace;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.util.crafting.FurnaceManager;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class FurnaceRecipeCategory extends BlankRecipeCategory<FurnaceRecipeWrapper> {

	public static void initialize(IModRegistry registry) {

		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipeCategories(new FurnaceRecipeCategory(guiHelper));
		registry.addRecipeHandlers(new FurnaceRecipeHandler());
		registry.addRecipes(getRecipes());
		registry.addRecipeClickArea(GuiFurnace.class, 79, 34, 24, 16, RecipeUidsTE.FURNACE);
		registry.addRecipeCategoryCraftingItem(BlockMachine.machineFurnace, RecipeUidsTE.FURNACE);
	}

	public static List<FurnaceRecipeWrapper> getRecipes() {

		List<FurnaceRecipeWrapper> recipes = new ArrayList<>();

		for (FurnaceManager.RecipeFurnace recipe : FurnaceManager.getRecipeList()) {
			recipes.add(new FurnaceRecipeWrapper(recipe));
		}
		return recipes;
	}

	IDrawableStatic background;
	String localizedName;

	public FurnaceRecipeCategory(IGuiHelper guiHelper) {

		background = guiHelper.createDrawable(GuiFurnace.TEXTURE, 26, 11, 124, 60);
		localizedName = StringHelper.localize("tile.thermalexpansion.machine.furnace.name");
	}

	@Nonnull
	@Override
	public String getUid() {

		return RecipeUidsTE.FURNACE;
	}

	@Nonnull
	@Override
	public String getTitle() {

		return localizedName;
	}

	@Nonnull
	@Override
	public IDrawable getBackground() {

		return background;
	}

	@Override
	public void drawExtras(@Nonnull Minecraft minecraft) {

	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, FurnaceRecipeWrapper recipeWrapper, IIngredients ingredients) {

		List<List<ItemStack>> inputs = ingredients.getInputs(ItemStack.class);
		List<ItemStack> outputs = ingredients.getOutputs(ItemStack.class);

		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

		guiItemStacks.init(0, true, 26, 14);
		guiItemStacks.init(1, false, 89, 23);

		guiItemStacks.set(0, inputs.get(0));
		guiItemStacks.set(1, outputs.get(0));
	}

}
