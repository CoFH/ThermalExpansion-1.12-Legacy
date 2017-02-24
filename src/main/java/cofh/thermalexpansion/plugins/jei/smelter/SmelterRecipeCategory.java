package cofh.thermalexpansion.plugins.jei.smelter;

import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.gui.client.machine.GuiSmelter;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.util.crafting.SmelterManager;
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

public class SmelterRecipeCategory extends BlankRecipeCategory<SmelterRecipeWrapper> {

	public static void initialize(IModRegistry registry) {

		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipeCategories(new SmelterRecipeCategory(guiHelper));
		registry.addRecipeHandlers(new SmelterRecipeHandler());
		registry.addRecipes(getRecipes());
		registry.addRecipeClickArea(GuiSmelter.class, 79, 34, 24, 16, RecipeUidsTE.SMELTER);
		registry.addRecipeCategoryCraftingItem(BlockMachine.machineSmelter, RecipeUidsTE.SMELTER);
	}

	public static List<SmelterRecipeWrapper> getRecipes() {

		List<SmelterRecipeWrapper> recipes = new ArrayList<>();

		for (SmelterManager.RecipeSmelter recipe : SmelterManager.getRecipeList()) {
			recipes.add(new SmelterRecipeWrapper(recipe));
		}
		return recipes;
	}

	IDrawableStatic background;
	String localizedName;

	public SmelterRecipeCategory(IGuiHelper guiHelper) {

		background = guiHelper.createDrawable(GuiSmelter.TEXTURE, 26, 11, 124, 60);
		localizedName = StringHelper.localize("tile.thermalexpansion.machine.smelter.name");
	}

	@Nonnull
	@Override
	public String getUid() {

		return RecipeUidsTE.SMELTER;
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
	public void setRecipe(IRecipeLayout recipeLayout, SmelterRecipeWrapper recipeWrapper, IIngredients ingredients) {

		List<List<ItemStack>> inputs = ingredients.getInputs(ItemStack.class);
		List<ItemStack> outputs = ingredients.getOutputs(ItemStack.class);

		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

		guiItemStacks.init(0, true, 5, 14);
		guiItemStacks.init(1, true, 29, 14);
		guiItemStacks.init(2, false, 89, 14);

		guiItemStacks.set(0, inputs.get(0));
		guiItemStacks.set(1, inputs.get(1));
		guiItemStacks.set(2, outputs.get(0));

		if (outputs.size() > 1) {
			guiItemStacks.init(3, false, 89, 41);
			guiItemStacks.set(3, outputs.get(1));

			//			guiItemStacks.addTooltipCallback(new ITooltipCallback<ItemStack>() {
			//
			//				@Override
			//				public void onTooltip(int slotIndex, boolean input, ItemStack ingredient, List<String> tooltip) {
			//
			//					if (slotIndex == 2) {
			//						tooltip.add("chance: " + secondChance + "%");
			//					}
			//				}
			//			});
		}
	}

}
