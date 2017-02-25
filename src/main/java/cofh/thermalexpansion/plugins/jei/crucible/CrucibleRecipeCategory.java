package cofh.thermalexpansion.plugins.jei.crucible;

import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.gui.client.machine.GuiCrucible;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.util.crafting.CrucibleManager;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.*;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class CrucibleRecipeCategory extends BlankRecipeCategory<CrucibleRecipeWrapper> {

	public static void initialize(IModRegistry registry) {

		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipeCategories(new CrucibleRecipeCategory(guiHelper));
		registry.addRecipeHandlers(new CrucibleRecipeHandler());
		registry.addRecipes(getRecipes(guiHelper));
		registry.addRecipeClickArea(GuiCrucible.class, 103, 34, 24, 16, RecipeUidsTE.CRUCIBLE);
		registry.addRecipeCategoryCraftingItem(BlockMachine.machineCrucible, RecipeUidsTE.CRUCIBLE);
	}

	public static List<CrucibleRecipeWrapper> getRecipes(IGuiHelper guiHelper) {

		List<CrucibleRecipeWrapper> recipes = new ArrayList<>();

		for (CrucibleManager.RecipeCrucible recipe : CrucibleManager.getRecipeList()) {
			recipes.add(new CrucibleRecipeWrapper(guiHelper, recipe));
		}
		return recipes;
	}

	IDrawableStatic background;
	IDrawableStatic energyMeter;
	IDrawableStatic drop;
	IDrawableStatic tank;
	IDrawableStatic tankOverlay;
	String localizedName;

	public CrucibleRecipeCategory(IGuiHelper guiHelper) {

		background = guiHelper.createDrawable(GuiCrucible.TEXTURE, 26, 11, 72, 62, 0, 0, 16, 52);

		drop = Drawables.getDrawables(guiHelper).getProgress(2);
		tank = Drawables.getDrawables(guiHelper).getTank(0);
		tankOverlay = Drawables.getDrawables(guiHelper).getTankSmallOverlay(0);
		energyMeter = Drawables.getDrawables(guiHelper).getEnergyEmpty();
		localizedName = StringHelper.localize("tile.thermalexpansion.machine.crucible.name");
	}

	@Nonnull
	@Override
	public String getUid() {

		return RecipeUidsTE.CRUCIBLE;
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

		drop.draw(minecraft, 69, 23);
		tank.draw(minecraft, 105, 0);
		energyMeter.draw(minecraft, 2, 8);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, CrucibleRecipeWrapper recipeWrapper, IIngredients ingredients) {

		List<List<ItemStack>> inputs = ingredients.getInputs(ItemStack.class);
		List<FluidStack> outputs = ingredients.getOutputs(FluidStack.class);

		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();

		guiItemStacks.init(0, true, 42, 14);
		guiFluidStacks.init(0, false, 106, 1, 16, 60, 2000, false, tankOverlay);

		guiItemStacks.set(0, inputs.get(0));
		guiFluidStacks.set(0, outputs.get(0));
	}

}
