package cofh.thermalexpansion.plugins.jei.crafting.furnace;

import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalexpansion.block.machine.TileFurnace;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.JEIPluginTE;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.plugins.jei.crafting.BaseRecipeWrapper;
import cofh.thermalexpansion.util.managers.machine.FurnaceManager.ComparableItemStackFurnace;
import cofh.thermalexpansion.util.managers.machine.FurnaceManager.RecipeFurnace;
import cofh.thermalfoundation.init.TFFluids;
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

public class FurnaceRecipeWrapper extends BaseRecipeWrapper {

	/* Recipe */
	final List<List<ItemStack>> inputs;
	final List<ItemStack> outputs;
	final List<FluidStack> outputFluids;

	/* Animation */
	final IDrawableAnimated fluid;
	final IDrawableAnimated progress;
	final IDrawableAnimated speed;

	public FurnaceRecipeWrapper(IGuiHelper guiHelper, RecipeFurnace recipe) {

		this(guiHelper, recipe, RecipeUidsTE.FURNACE);
	}

	public FurnaceRecipeWrapper(IGuiHelper guiHelper, RecipeFurnace recipe, String uIdIn) {

		uId = uIdIn;

		List<ItemStack> recipeInputs = new ArrayList<>();
		List<ItemStack> recipeOutputs = new ArrayList<>();
		List<FluidStack> recipeOutputFluids = new ArrayList<>();

		if (ComparableItemStackFurnace.getOreID(recipe.getInput()) != -1) {
			for (ItemStack ore : OreDictionary.getOres(ItemHelper.getOreName(recipe.getInput()), false)) {
				recipeInputs.add(ItemHelper.cloneStack(ore, recipe.getInput().stackSize));
			}
		} else {
			recipeInputs.add(recipe.getInput());
		}
		switch (uId) {
			case RecipeUidsTE.FURNACE_FOOD:
			case RecipeUidsTE.FURNACE_ORE:
				recipeOutputs.add(ItemHelper.cloneStack(recipe.getOutput(), recipe.getOutput().stackSize + 1));
				outputFluids = Collections.emptyList();
				energy = recipe.getEnergy() * 3 / 2;
				break;
			case RecipeUidsTE.FURNACE_PYROLYSIS:
				recipeOutputs.add(recipe.getOutput());
				recipeOutputFluids.add(new FluidStack(TFFluids.fluidCreosote, recipe.getCreosote()));
				outputFluids = recipeOutputFluids;
				energy = recipe.getEnergy() * 3 / 2;
				break;
			default:
				recipeOutputs.add(recipe.getOutput());
				outputFluids = Collections.emptyList();
				energy = recipe.getEnergy();
				break;
		}
		inputs = Collections.singletonList(recipeInputs);
		outputs = recipeOutputs;

		IDrawableStatic fluidDrawable = Drawables.getDrawables(guiHelper).getProgress(Drawables.PROGRESS_ARROW_FLUID);
		IDrawableStatic progressDrawable = Drawables.getDrawables(guiHelper).getProgressFill(uId.equals(RecipeUidsTE.FURNACE_PYROLYSIS) ? Drawables.PROGRESS_ARROW_FLUID : Drawables.PROGRESS_ARROW);
		IDrawableStatic speedDrawable = Drawables.getDrawables(guiHelper).getScaleFill(Drawables.SCALE_FLAME);
		IDrawableStatic energyDrawable = Drawables.getDrawables(guiHelper).getEnergyFill();

		fluid = guiHelper.createAnimatedDrawable(fluidDrawable, energy / TileFurnace.basePower, StartDirection.LEFT, true);
		progress = guiHelper.createAnimatedDrawable(progressDrawable, energy / TileFurnace.basePower, StartDirection.LEFT, false);
		speed = guiHelper.createAnimatedDrawable(speedDrawable, 1000, StartDirection.TOP, true);
		energyMeter = guiHelper.createAnimatedDrawable(energyDrawable, 1000, StartDirection.TOP, true);
	}

	@Override
	public void getIngredients(IIngredients ingredients) {

		ingredients.setInputLists(ItemStack.class, inputs);
		ingredients.setOutputs(ItemStack.class, outputs);
		ingredients.setOutputs(FluidStack.class, outputFluids);
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

		if (uId.equals(RecipeUidsTE.FURNACE_PYROLYSIS)) {
			JEIPluginTE.drawFluid(69, 23, outputFluids.get(0), 24, 16);
			fluid.draw(minecraft, 69, 23);
		}
		progress.draw(minecraft, 69, 23);
		speed.draw(minecraft, 43, 33);
		energyMeter.draw(minecraft, 2, 8);
	}

}
