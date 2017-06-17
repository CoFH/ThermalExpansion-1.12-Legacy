package cofh.thermalexpansion.plugins.jei.crafting.furnace;

import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.gui.client.machine.GuiFurnace;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.plugins.jei.crafting.BaseRecipeCategory;
import cofh.thermalexpansion.util.managers.machine.FurnaceManager;
import cofh.thermalexpansion.util.managers.machine.FurnaceManager.FurnaceRecipe;
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

public class FurnaceRecipeCategory extends BaseRecipeCategory<FurnaceRecipeWrapper> {

	public static boolean enable = true;

	public static void initialize(IModRegistry registry) {

		if (!enable) {
			return;
		}
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		((IRecipeCategoryRegistration) registry).addRecipeCategories(new FurnaceRecipeCategory(guiHelper));
		registry.addRecipes(getRecipes(guiHelper), RecipeUidsTE.FURNACE);
		registry.addRecipeClickArea(GuiFurnace.class, 79, 34, 24, 16, RecipeUidsTE.FURNACE, RecipeUidsTE.FURNACE_FOOD, RecipeUidsTE.FURNACE_ORE, RecipeUidsTE.FURNACE_PYROLYSIS);
		registry.addRecipeCatalyst(BlockMachine.machineFurnace, RecipeUidsTE.FURNACE);

		FurnaceRecipeCategoryFood.initialize(registry);
		FurnaceRecipeCategoryOre.initialize(registry);
		FurnaceRecipeCategoryPyrolysis.initialize(registry);
	}

	public static List<FurnaceRecipeWrapper> getRecipes(IGuiHelper guiHelper) {

		List<FurnaceRecipeWrapper> recipes = new ArrayList<>();

		for (FurnaceRecipe recipe : FurnaceManager.getRecipeList()) {
			recipes.add(new FurnaceRecipeWrapper(guiHelper, recipe));
		}
		return recipes;
	}

	public FurnaceRecipeCategory(IGuiHelper guiHelper) {

		background = guiHelper.createDrawable(GuiFurnace.TEXTURE, 26, 11, 124, 62, 0, 0, 16, 24);
		energyMeter = Drawables.getDrawables(guiHelper).getEnergyEmpty();
		localizedName = StringHelper.localize("tile.thermalexpansion.machine.furnace.name");
	}

	@Nonnull
	@Override
	public String getUid() {

		return RecipeUidsTE.FURNACE;
	}

	@Override
	public void drawExtras(@Nonnull Minecraft minecraft) {

		energyMeter.draw(minecraft, 2, 8);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, FurnaceRecipeWrapper recipeWrapper, IIngredients ingredients) {

		List<List<ItemStack>> inputs = ingredients.getInputs(ItemStack.class);
		List<List<ItemStack>> outputs = ingredients.getOutputs(ItemStack.class);

		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

		guiItemStacks.init(0, true, 42, 14);
		guiItemStacks.init(1, false, 105, 23);

		guiItemStacks.set(0, inputs.get(0));
		guiItemStacks.set(1, outputs.get(0));
	}

}
