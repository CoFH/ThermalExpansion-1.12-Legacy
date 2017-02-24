package cofh.thermalexpansion.plugins.jei.sawmill;

import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.gui.client.machine.GuiSawmill;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.util.crafting.SawmillManager;
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

public class SawmillRecipeCategory extends BlankRecipeCategory<SawmillRecipeWrapper> {

	public static void initialize(IModRegistry registry) {

		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipeCategories(new SawmillRecipeCategory(guiHelper));
		registry.addRecipeHandlers(new SawmillRecipeHandler());
		registry.addRecipes(getRecipes());
		registry.addRecipeClickArea(GuiSawmill.class, 79, 34, 24, 16, RecipeUidsTE.SAWMILL);
		registry.addRecipeCategoryCraftingItem(BlockMachine.machineSawmill, RecipeUidsTE.SAWMILL);
	}

	public static List<SawmillRecipeWrapper> getRecipes() {

		List<SawmillRecipeWrapper> recipes = new ArrayList<SawmillRecipeWrapper>();

		for (SawmillManager.RecipeSawmill recipe : SawmillManager.getRecipeList()) {
			recipes.add(new SawmillRecipeWrapper(recipe));
		}
		return recipes;
	}

	IDrawableStatic background;
	String localizedName;

	public SawmillRecipeCategory(IGuiHelper guiHelper) {

		background = guiHelper.createDrawable(GuiSawmill.TEXTURE, 26, 11, 124, 60);
		localizedName = StringHelper.localize("tile.thermalexpansion.machine.sawmill.name");
	}

	@Nonnull
	@Override
	public String getUid() {

		return RecipeUidsTE.SAWMILL;
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
	public void setRecipe(IRecipeLayout recipeLayout, SawmillRecipeWrapper recipeWrapper, IIngredients ingredients) {

		List<List<ItemStack>> inputs = ingredients.getInputs(ItemStack.class);
		List<ItemStack> outputs = ingredients.getOutputs(ItemStack.class);

		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

		guiItemStacks.init(0, true, 26, 14);
		guiItemStacks.init(1, false, 89, 14);

		guiItemStacks.set(0, inputs.get(0));
		guiItemStacks.set(1, outputs.get(0));

		if (outputs.size() > 1) {
			guiItemStacks.init(2, false, 89, 41);
			guiItemStacks.set(2, outputs.get(1));

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
