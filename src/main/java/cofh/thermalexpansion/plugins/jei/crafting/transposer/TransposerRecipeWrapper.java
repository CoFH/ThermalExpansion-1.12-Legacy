package cofh.thermalexpansion.plugins.jei.crafting.transposer;

import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.block.machine.TileTransposer;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.JEIPluginTE;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.plugins.jei.crafting.BaseRecipeWrapper;
import cofh.thermalexpansion.util.managers.machine.TransposerManager.ComparableItemStackTransposer;
import cofh.thermalexpansion.util.managers.machine.TransposerManager.TransposerRecipe;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableAnimated.StartDirection;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TransposerRecipeWrapper extends BaseRecipeWrapper {

	/* Recipe */
	final List<List<ItemStack>> inputs;
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

	public TransposerRecipeWrapper(IGuiHelper guiHelper, TransposerRecipe recipe, String uIdIn) {

		uId = uIdIn;

		List<ItemStack> recipeInputs = new ArrayList<>();

		int oreID = ComparableItemStackTransposer.getOreID(recipe.getInput());
		if (oreID != -1) {
			for (ItemStack ore : OreDictionary.getOres(ItemHelper.oreProxy.getOreName(oreID), false)) {
				recipeInputs.add(ItemHelper.cloneStack(ore, recipe.getInput().getCount()));
			}
		} else {
			recipeInputs.add(recipe.getInput());
		}
		List<ItemStack> recipeOutputs = new ArrayList<>();
		recipeOutputs.add(recipe.getOutput());

		List<FluidStack> recipeFluids = new ArrayList<>();
		recipeFluids.add(recipe.getFluid());

		inputs = Collections.singletonList(recipeInputs);
		outputs = recipeOutputs;

		if (uId.equals(RecipeUidsTE.TRANSPOSER_FILL)) {
			inputFluids = Collections.singletonList(recipeFluids);
			outputFluids = Collections.emptyList();
		} else {
			inputFluids = Collections.emptyList();
			outputFluids = recipeFluids;
		}
		energy = recipe.getEnergy();
		chance = recipe.getChance();

		if (uId.equals(RecipeUidsTE.TRANSPOSER_FILL)) {
			progressBack = Drawables.getDrawables(guiHelper).getProgressLeft(Drawables.PROGRESS_DROP);
			speedBack = Drawables.getDrawables(guiHelper).getScale(Drawables.SCALE_BUBBLE);

			IDrawableStatic fluidDrawable = Drawables.getDrawables(guiHelper).getProgressLeft(Drawables.PROGRESS_DROP);
			IDrawableStatic progressDrawable = Drawables.getDrawables(guiHelper).getProgressLeftFill(Drawables.PROGRESS_DROP);

			fluid = guiHelper.createAnimatedDrawable(fluidDrawable, energy / TileTransposer.basePower, StartDirection.RIGHT, true);
			progress = guiHelper.createAnimatedDrawable(progressDrawable, energy / TileTransposer.basePower, StartDirection.RIGHT, false);
		} else {
			progressBack = Drawables.getDrawables(guiHelper).getProgress(Drawables.PROGRESS_DROP);
			speedBack = Drawables.getDrawables(guiHelper).getScale(Drawables.SCALE_BUBBLE);

			IDrawableStatic fluidDrawable = Drawables.getDrawables(guiHelper).getProgress(Drawables.PROGRESS_DROP);
			IDrawableStatic progressDrawable = Drawables.getDrawables(guiHelper).getProgressFill(Drawables.PROGRESS_DROP);

			fluid = guiHelper.createAnimatedDrawable(fluidDrawable, energy / TileTransposer.basePower, StartDirection.LEFT, true);
			progress = guiHelper.createAnimatedDrawable(progressDrawable, energy / TileTransposer.basePower, StartDirection.LEFT, false);
		}
		IDrawableStatic speedDrawable = Drawables.getDrawables(guiHelper).getScaleFill(Drawables.SCALE_BUBBLE);
		IDrawableStatic energyDrawable = Drawables.getDrawables(guiHelper).getEnergyFill();

		speed = guiHelper.createAnimatedDrawable(speedDrawable, 1000, StartDirection.TOP, true);
		energyMeter = guiHelper.createAnimatedDrawable(energyDrawable, 1000, StartDirection.TOP, true);
	}

	@Override
	public void getIngredients(IIngredients ingredients) {

		ingredients.setInputLists(ItemStack.class, inputs);
		ingredients.setOutputs(ItemStack.class, outputs);

		if (uId.equals(RecipeUidsTE.TRANSPOSER_FILL)) {
			ingredients.setInputLists(FluidStack.class, inputFluids);
		} else {
			ingredients.setOutputs(FluidStack.class, outputFluids);
		}
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

		progressBack.draw(minecraft, 63, 11);
		speedBack.draw(minecraft, 68, 41);

		if (uId.equals(RecipeUidsTE.TRANSPOSER_FILL)) {
			JEIPluginTE.drawFluid(63, 11, inputFluids.get(0).get(0), 24, 16);
		} else {
			JEIPluginTE.drawFluid(63, 11, outputFluids.get(0), 24, 16);
		}
		fluid.draw(minecraft, 63, 11);
		progress.draw(minecraft, 63, 11);
		speed.draw(minecraft, 68, 41);
		energyMeter.draw(minecraft, 2, 8);
	}

}
