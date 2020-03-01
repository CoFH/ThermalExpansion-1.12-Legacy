package cofh.thermalexpansion.plugins.jei.machine.extruder;

import cofh.thermalexpansion.block.machine.TileBrewer;
import cofh.thermalexpansion.block.machine.TileExtruder;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.JEIPluginTE;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.plugins.jei.machine.BaseRecipeWrapper;
import cofh.thermalexpansion.util.managers.machine.ExtruderManager.ExtruderRecipe;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableAnimated.StartDirection;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class ExtruderRecipeWrapper extends BaseRecipeWrapper {

	/* Recipe */
	protected List<FluidStack> inputFluids;
	protected List<ItemStack> outputItems;

	/* Animation */
	protected IDrawableAnimated fluid;
	protected IDrawableAnimated progress;
	protected IDrawableAnimated speed;

	public ExtruderRecipeWrapper(IGuiHelper guiHelper, ExtruderRecipe recipe) {

		this(guiHelper, recipe, RecipeUidsTE.EXTRUDER);
	}

	public ExtruderRecipeWrapper(IGuiHelper guiHelper, ExtruderRecipe recipe, String uIdIn) {

		uId = uIdIn;

		List<FluidStack> recipeInputFluids = new ArrayList<>();
		List<ItemStack> recipeOutputItems = new ArrayList<>();

		recipeInputFluids.add(recipe.getInputHot());
		recipeInputFluids.add(recipe.getInputCold());

		recipeOutputItems.add(recipe.getOutput());

		inputFluids = recipeInputFluids;
		outputItems = recipeOutputItems;

		energy = recipe.getEnergy();

		IDrawableStatic fluidDrawable = Drawables.getDrawables(guiHelper).getProgress(Drawables.PROGRESS_DROP);
		IDrawableStatic progressDrawable = Drawables.getDrawables(guiHelper).getProgressFill(Drawables.PROGRESS_DROP);
		IDrawableStatic speedDrawable = Drawables.getDrawables(guiHelper).getScaleFill(Drawables.SCALE_COMPACT);
		IDrawableStatic energyDrawable = Drawables.getDrawables(guiHelper).getEnergyFill();

		int basePower = TileExtruder.basePower;

		fluid = guiHelper.createAnimatedDrawable(fluidDrawable, Math.max(10, energy / basePower), StartDirection.LEFT, true);
		progress = guiHelper.createAnimatedDrawable(progressDrawable, Math.max(10, energy / basePower), StartDirection.LEFT, false);
		speed = guiHelper.createAnimatedDrawable(speedDrawable, 1000, StartDirection.TOP, true);
		energyMeter = guiHelper.createAnimatedDrawable(energyDrawable, 1000, StartDirection.TOP, true);
	}

	@Override
	public void getIngredients(IIngredients ingredients) {

		ingredients.setInputs(FluidStack.class, inputFluids);
		ingredients.setOutputs(ItemStack.class, outputItems);
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

		JEIPluginTE.drawFluid(69, 23, inputFluids.get(0), 24, 8);
		JEIPluginTE.drawFluid(69, 31, inputFluids.get(1), 24, 8);

		if (inputFluids.get(0).amount < Fluid.BUCKET_VOLUME) {
			JEIPluginTE.drawFluid(22, 8 + 23, inputFluids.get(0), 16, 7);
		}
		if (inputFluids.get(1).amount < Fluid.BUCKET_VOLUME) {
			JEIPluginTE.drawFluid(46, 8 + 23, inputFluids.get(1), 16, 7);
		}
		fluid.draw(minecraft, 69, 23);
		progress.draw(minecraft, 69, 23);
		speed.draw(minecraft, 34, 40);
		energyMeter.draw(minecraft, 2, 8);
	}

}
