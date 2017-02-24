package cofh.thermalexpansion.plugins.jei.compactor;

import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.gui.client.machine.GuiCompactor;
import mezz.jei.api.IGuiHelper;
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
import java.util.List;

public abstract class CompactorRecipeCategory extends BlankRecipeCategory<CompactorRecipeWrapper> {

	public static void initialize(IModRegistry registry) {

		CompactorRecipeCategoryPress.initialize(registry);
		//CompactorRecipeCategoryStorage.initialize(registry);
		//CompactorRecipeCategoryMint.initialize(registry);
		//registry.addRecipeClickArea(GuiCompactor.class, 79, 34, 24, 16, RecipeUidsTE.COMPACTOR_PRESS, RecipeUidsTE.COMPACTOR_STORAGE, RecipeUidsTE.COMPACTOR_MINT);
		registry.addRecipeHandlers(new CompactorRecipeHandler());
	}

	IDrawableStatic background;
	String localizedName;

	public CompactorRecipeCategory(IGuiHelper guiHelper) {

		background = guiHelper.createDrawable(GuiCompactor.TEXTURE, 26, 11, 124, 60);
		localizedName = StringHelper.localize("tile.thermalexpansion.machine.compactor.name");
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
	public void setRecipe(IRecipeLayout recipeLayout, CompactorRecipeWrapper recipeWrapper, IIngredients ingredients) {

		List<List<ItemStack>> inputs = ingredients.getInputs(ItemStack.class);
		List<ItemStack> outputs = ingredients.getOutputs(ItemStack.class);

		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

		guiItemStacks.init(0, true, 26, 14);
		guiItemStacks.init(1, false, 89, 23);

		guiItemStacks.set(0, inputs.get(0));
		guiItemStacks.set(1, outputs.get(0));
	}

}
