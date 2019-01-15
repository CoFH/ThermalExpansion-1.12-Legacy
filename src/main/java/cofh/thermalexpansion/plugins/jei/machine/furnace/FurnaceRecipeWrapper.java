package cofh.thermalexpansion.plugins.jei.machine.furnace;

import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.block.machine.TileFurnace;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.JEIPluginTE;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.plugins.jei.machine.BaseRecipeWrapper;
import cofh.thermalexpansion.util.managers.machine.FurnaceManager;
import cofh.thermalexpansion.util.managers.machine.FurnaceManager.FurnaceRecipe;
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

import static java.util.Collections.singletonList;

public class FurnaceRecipeWrapper extends BaseRecipeWrapper {

	/* Recipe */
	protected List<List<ItemStack>> inputs;
	protected List<ItemStack> outputs;
	protected List<FluidStack> outputFluids;

	/* Animation */
	protected IDrawableAnimated fluid;
	protected IDrawableAnimated progress;
	protected IDrawableAnimated speed;

	public FurnaceRecipeWrapper(IGuiHelper guiHelper, FurnaceRecipe recipe) {

		this(guiHelper, recipe, RecipeUidsTE.FURNACE);
	}

	public FurnaceRecipeWrapper(IGuiHelper guiHelper, FurnaceRecipe recipe, String uIdIn) {

		uId = uIdIn;

		List<ItemStack> recipeInputs = new ArrayList<>();
		List<ItemStack> recipeOutputs = new ArrayList<>();
		List<FluidStack> recipeOutputFluids = new ArrayList<>();

		int oreID = FurnaceManager.convertInput(recipe.getInput()).oreID;
		if (oreID != -1) {
			for (ItemStack ore : OreDictionary.getOres(ItemHelper.oreProxy.getOreName(oreID), false)) {
				recipeInputs.add(ItemHelper.cloneStack(ore, recipe.getInput().getCount()));
			}
		} else {
			recipeInputs.add(recipe.getInput());
		}
		switch (uId) {
			case RecipeUidsTE.FURNACE_FOOD:
			case RecipeUidsTE.FURNACE_ORE:
				recipeOutputs.add(ItemHelper.cloneStack(recipe.getOutput(), recipe.getOutput().getCount() + Math.max(1, recipe.getOutput().getCount() / 2)));
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
		inputs = singletonList(recipeInputs);
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
