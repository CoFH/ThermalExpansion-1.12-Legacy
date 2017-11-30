package cofh.thermalexpansion.plugins.jei.crafting.precipitator;

import cofh.thermalexpansion.block.machine.TilePrecipitator;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.JEIPluginTE;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.plugins.jei.crafting.BaseRecipeWrapper;
import cofh.thermalexpansion.util.managers.machine.PrecipitatorManager.PrecipitatorRecipe;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableAnimated.StartDirection;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class PrecipitatorRecipeWrapper extends BaseRecipeWrapper {

	/* Recipe */
	final List<FluidStack> inputFluids;
	final List<ItemStack> outputItems;

	/* Animation */
	final IDrawableAnimated fluid;
	final IDrawableAnimated progress;

	public PrecipitatorRecipeWrapper(IGuiHelper guiHelper, PrecipitatorRecipe recipe) {

		this(guiHelper, recipe, RecipeUidsTE.PRECIPITATOR);
	}

	public PrecipitatorRecipeWrapper(IGuiHelper guiHelper, PrecipitatorRecipe recipe, String uIdIn) {

		uId = uIdIn;

		List<FluidStack> recipeInputFluids = new ArrayList<>();
		List<ItemStack> recipeOutputItems = new ArrayList<>();

		recipeInputFluids.add(recipe.getInput());
		recipeOutputItems.add(recipe.getOutput());

		inputFluids = recipeInputFluids;
		outputItems = recipeOutputItems;

		energy = recipe.getEnergy();

		IDrawableStatic fluidDrawable = Drawables.getDrawables(guiHelper).getProgressLeft(Drawables.PROGRESS_DROP);
		IDrawableStatic progressDrawable = Drawables.getDrawables(guiHelper).getProgressLeftFill(Drawables.PROGRESS_DROP);
		IDrawableStatic energyDrawable = Drawables.getDrawables(guiHelper).getEnergyFill();

		fluid = guiHelper.createAnimatedDrawable(fluidDrawable, energy / TilePrecipitator.basePower, StartDirection.RIGHT, true);
		progress = guiHelper.createAnimatedDrawable(progressDrawable, energy / TilePrecipitator.basePower, StartDirection.RIGHT, false);
		energyMeter = guiHelper.createAnimatedDrawable(energyDrawable, 1000, StartDirection.TOP, true);
	}

	@Override
	public void getIngredients(IIngredients ingredients) {

		ingredients.setInputs(FluidStack.class, inputFluids);
		ingredients.setOutputs(ItemStack.class, outputItems);
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

		JEIPluginTE.drawFluid(82, 23, inputFluids.get(0), 24, 16);

		fluid.draw(minecraft, 82, 23);
		progress.draw(minecraft, 82, 23);
		energyMeter.draw(minecraft, 2, 8);
	}

}
