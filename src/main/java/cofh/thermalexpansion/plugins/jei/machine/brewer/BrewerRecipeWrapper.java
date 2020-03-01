package cofh.thermalexpansion.plugins.jei.machine.brewer;

import cofh.thermalexpansion.block.machine.TileBrewer;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.JEIPluginTE;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.plugins.jei.machine.BaseRecipeWrapper;
import cofh.thermalexpansion.util.managers.machine.BrewerManager.BrewerRecipe;
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

public class BrewerRecipeWrapper extends BaseRecipeWrapper {

	/* Recipe */
	protected List<ItemStack> inputItems;
	protected List<FluidStack> inputFluids;
	protected List<FluidStack> outputFluids;

	/* Animation */
	protected IDrawableAnimated fluid;
	protected IDrawableAnimated progress;
	protected IDrawableAnimated speed;

	public BrewerRecipeWrapper(IGuiHelper guiHelper, BrewerRecipe recipe) {

		this(guiHelper, recipe, RecipeUidsTE.BREWER);
	}

	public BrewerRecipeWrapper(IGuiHelper guiHelper, BrewerRecipe recipe, String uIdIn) {

		uId = uIdIn;

		List<ItemStack> recipeInputs = new ArrayList<>();
		recipeInputs.add(recipe.getInput());

		List<FluidStack> recipeInputFluids = new ArrayList<>();
		recipeInputFluids.add(recipe.getInputFluid());

		List<FluidStack> recipeOutputFluids = new ArrayList<>();
		recipeOutputFluids.add(recipe.getOutputFluid());

		inputItems = recipeInputs;
		inputFluids = recipeInputFluids;
		outputFluids = recipeOutputFluids;

		energy = recipe.getEnergy();

		IDrawableStatic fluidDrawable = Drawables.getDrawables(guiHelper).getProgress(Drawables.PROGRESS_DROP);
		IDrawableStatic progressDrawable = Drawables.getDrawables(guiHelper).getProgressFill(Drawables.PROGRESS_DROP);
		IDrawableStatic speedDrawable = Drawables.getDrawables(guiHelper).getScaleFill(Drawables.SCALE_ALCHEMY);
		IDrawableStatic energyDrawable = Drawables.getDrawables(guiHelper).getEnergyFill();

		int basePower = TileBrewer.basePower;

		fluid = guiHelper.createAnimatedDrawable(fluidDrawable, Math.max(10, energy / basePower), StartDirection.LEFT, true);
		progress = guiHelper.createAnimatedDrawable(progressDrawable, Math.max(10, energy / basePower), StartDirection.LEFT, false);
		speed = guiHelper.createAnimatedDrawable(speedDrawable, 1000, StartDirection.TOP, true);
		energyMeter = guiHelper.createAnimatedDrawable(energyDrawable, 1000, StartDirection.TOP, true);
	}

	@Override
	public void getIngredients(IIngredients ingredients) {

		ingredients.setInputs(ItemStack.class, inputItems);
		ingredients.setInputs(FluidStack.class, inputFluids);
		ingredients.setOutputs(FluidStack.class, outputFluids);
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

		JEIPluginTE.drawFluid(94, 23, outputFluids.get(0), 24, 16);

		fluid.draw(minecraft, 94, 23);
		progress.draw(minecraft, 94, 23);
		speed.draw(minecraft, 46, 23);
		energyMeter.draw(minecraft, 2, 8);
	}

}
