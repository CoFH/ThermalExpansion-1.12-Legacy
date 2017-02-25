package cofh.thermalexpansion.plugins.jei.refinery;

import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.block.machine.TileRefinery;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.JEIPluginTE;
import cofh.thermalexpansion.util.crafting.RefineryManager.RecipeRefinery;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableAnimated.StartDirection;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RefineryRecipeWrapper extends BlankRecipeWrapper {

	/* Recipe */
	final List<List<FluidStack>> inputFluids;
	final List<ItemStack> outputs;
	final List<FluidStack> outputFluids;

	final int energy;

	/* Animation */
	final IDrawableAnimated fluid;
	final IDrawableAnimated progress;
	final IDrawableAnimated speed;
	final IDrawableAnimated energyMeter;

	public RefineryRecipeWrapper(IGuiHelper guiHelper, RecipeRefinery recipe) {

		List<FluidStack> recipeInputFluids = new ArrayList<>();
		recipeInputFluids.add(recipe.getInput());

		List<ItemStack> recipeOutputs = new ArrayList<>();
		recipeOutputs.add(recipe.getOutputItem());

		List<FluidStack> recipeOutputFluids = new ArrayList<>();
		recipeOutputFluids.add(recipe.getOutputFluid());

		inputFluids = Collections.singletonList(recipeInputFluids);
		outputs = recipeOutputs;
		outputFluids = recipeOutputFluids;

		energy = recipe.getEnergy();

		IDrawableStatic fluidDrawable = Drawables.getDrawables(guiHelper).getProgress(2);
		IDrawableStatic progressDrawable = Drawables.getDrawables(guiHelper).getProgressFill(2);
		IDrawableStatic speedDrawable = Drawables.getDrawables(guiHelper).getSpeedFill(2);
		IDrawableStatic energyDrawable = Drawables.getDrawables(guiHelper).getEnergyFill();

		fluid = guiHelper.createAnimatedDrawable(fluidDrawable, energy / TileRefinery.basePower, StartDirection.LEFT, true);
		progress = guiHelper.createAnimatedDrawable(progressDrawable, energy / TileRefinery.basePower, StartDirection.LEFT, false);
		speed = guiHelper.createAnimatedDrawable(speedDrawable, 1000, StartDirection.TOP, true);
		energyMeter = guiHelper.createAnimatedDrawable(energyDrawable, 1000, StartDirection.TOP, true);
	}

	@Override
	public void getIngredients(IIngredients ingredients) {

		ingredients.setInputLists(FluidStack.class, inputFluids);
		ingredients.setOutputs(ItemStack.class, outputs);
		ingredients.setOutputs(FluidStack.class, outputFluids);
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

		JEIPluginTE.drawFluid(46, 23, inputFluids.get(0).get(0), 24, 16);

		fluid.draw(minecraft, 46, 23);
		progress.draw(minecraft, 46, 23);
		speed.draw(minecraft, 22, 40);
		energyMeter.draw(minecraft, 2, 8);
	}

	@Nullable
	public List<String> getTooltipStrings(int mouseX, int mouseY) {

		List<String> tooltip = new ArrayList<>();

		if (mouseX > 2 && mouseX < 15 && mouseY > 8 && mouseY < 49) {
			tooltip.add(StringHelper.localize("info.cofh.energy") + ": " + energy + " RF");
		}
		return tooltip;
	}

}
