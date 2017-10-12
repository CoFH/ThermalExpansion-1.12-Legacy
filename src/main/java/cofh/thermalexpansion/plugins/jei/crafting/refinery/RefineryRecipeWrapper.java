package cofh.thermalexpansion.plugins.jei.crafting.refinery;

import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.block.machine.TileRefinery;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.JEIPluginTE;
import cofh.thermalexpansion.plugins.jei.crafting.BaseRecipeWrapper;
import cofh.thermalexpansion.util.managers.machine.RefineryManager.RefineryRecipe;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableAnimated.StartDirection;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RefineryRecipeWrapper extends BaseRecipeWrapper {

	/* Recipe */
	final List<List<FluidStack>> inputFluids;
	final List<ItemStack> outputs;
	final List<FluidStack> outputFluids;

	final int chance;

	/* Animation */
	final IDrawableStatic progressBack;
	final IDrawableStatic speedBack;
	final IDrawableAnimated fluid;
	final IDrawableAnimated progress;
	final IDrawableAnimated speed;

	public RefineryRecipeWrapper(IGuiHelper guiHelper, RefineryRecipe recipe) {

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
		chance = recipe.getChance();

		progressBack = Drawables.getDrawables(guiHelper).getProgress(Drawables.PROGRESS_DROP);
		speedBack = Drawables.getDrawables(guiHelper).getScale(Drawables.SCALE_FLAME);

		IDrawableStatic fluidDrawable = Drawables.getDrawables(guiHelper).getProgress(Drawables.PROGRESS_DROP);
		IDrawableStatic progressDrawable = Drawables.getDrawables(guiHelper).getProgressFill(Drawables.PROGRESS_DROP);
		IDrawableStatic speedDrawable = Drawables.getDrawables(guiHelper).getScaleFill(Drawables.SCALE_FLAME);
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

		progressBack.draw(minecraft, 46, 23);
		speedBack.draw(minecraft, 22, 40);

		JEIPluginTE.drawFluid(46, 23, inputFluids.get(0).get(0), 24, 16);

		fluid.draw(minecraft, 46, 23);
		progress.draw(minecraft, 46, 23);
		speed.draw(minecraft, 22, 40);
		energyMeter.draw(minecraft, 2, 8);

		if (chance > 0) {
			String dispChance = StringHelper.formatNumber(chance) + "%";
			minecraft.fontRenderer.drawString(dispChance, 102 - 6 * dispChance.length(), 48, 0x808080);
		}
	}

}
