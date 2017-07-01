package cofh.thermalexpansion.plugins.jei.crafting.furnace;

import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.item.ItemAugment;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.util.managers.machine.FurnaceManager;
import cofh.thermalexpansion.util.managers.machine.FurnaceManager.FurnaceRecipe;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class FurnaceRecipeCategoryPyrolysis extends FurnaceRecipeCategory {

	public static void initialize(IModRegistry registry) {

		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipes(getRecipes(guiHelper), RecipeUidsTE.FURNACE_PYROLYSIS);
		registry.addRecipeCatalyst(ItemAugment.machineFurnacePyrolysis, RecipeUidsTE.FURNACE_PYROLYSIS);
		registry.addRecipeCatalyst(BlockMachine.machineFurnace, RecipeUidsTE.FURNACE_PYROLYSIS);
	}

	public static List<FurnaceRecipeWrapper> getRecipes(IGuiHelper guiHelper) {

		List<FurnaceRecipeWrapper> recipes = new ArrayList<>();

		for (FurnaceRecipe recipe : FurnaceManager.getRecipeListPyrolysis()) {
			recipes.add(new FurnaceRecipeWrapper(guiHelper, recipe, RecipeUidsTE.FURNACE_PYROLYSIS));
		}
		return recipes;
	}

	IDrawableStatic tank;
	IDrawableStatic tankOverlay;

	public FurnaceRecipeCategoryPyrolysis(IGuiHelper guiHelper) {

		super(guiHelper);

		tank = Drawables.getDrawables(guiHelper).getTank(Drawables.TANK);
		tankOverlay = Drawables.getDrawables(guiHelper).getTankSmallOverlay(Drawables.TANK);

		localizedName = StringHelper.localize("item.thermalexpansion.augment.machineFurnacePyrolysis.name");
	}

	@Nonnull
	@Override
	public String getUid() {

		return RecipeUidsTE.FURNACE_PYROLYSIS;
	}

	@Override
	public void drawExtras(@Nonnull Minecraft minecraft) {

		super.drawExtras(minecraft);

		tank.draw(minecraft, 140, 0);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, FurnaceRecipeWrapper recipeWrapper, IIngredients ingredients) {

		super.setRecipe(recipeLayout, recipeWrapper, ingredients);

		List<List<FluidStack>> outputFluids = ingredients.getOutputs(FluidStack.class);
		IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();

		guiFluidStacks.init(0, true, 141, 1, 16, 60, 1000, false, tankOverlay);
		guiFluidStacks.set(0, outputFluids.get(0));
	}

}
