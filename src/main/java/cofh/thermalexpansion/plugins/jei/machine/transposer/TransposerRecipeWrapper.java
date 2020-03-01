package cofh.thermalexpansion.plugins.jei.machine.transposer;

import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.block.machine.TileTransposer;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.JEIPluginTE;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.plugins.jei.machine.BaseRecipeWrapper;
import cofh.thermalexpansion.util.managers.machine.TransposerManager;
import cofh.thermalexpansion.util.managers.machine.TransposerManager.TransposerRecipe;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableAnimated.StartDirection;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;

public class TransposerRecipeWrapper extends BaseRecipeWrapper {

	/* Recipe */
	protected List<List<ItemStack>> inputs;
	protected List<List<FluidStack>> inputFluids;
	protected List<List<ItemStack>> outputs;
	protected List<List<FluidStack>> outputFluids;

	protected int chance;

	/* Animation */
	protected IDrawableStatic progressBack;
	protected IDrawableAnimated fluid;
	protected IDrawableAnimated progress;
	protected IDrawableAnimated speed;

	protected Map<Integer, ? extends IGuiIngredient<FluidStack>> guiFluids;

	public TransposerRecipeWrapper() {

	}

	public TransposerRecipeWrapper(IGuiHelper guiHelper, TransposerRecipe recipe, String uIdIn) {

		uId = uIdIn;

		List<ItemStack> recipeInputs = new ArrayList<>();

		int oreID = TransposerManager.convertInput(recipe.getInput()).oreID;
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

		inputs = singletonList(recipeInputs);
		outputs = singletonList(recipeOutputs);

		if (uId.equals(RecipeUidsTE.TRANSPOSER_FILL)) {
			inputFluids = singletonList(recipeFluids);
			outputFluids = Collections.emptyList();
		} else {
			inputFluids = Collections.emptyList();
			outputFluids = singletonList(recipeFluids);
		}
		energy = recipe.getEnergy();
		chance = recipe.getChance();

		int basePower = TileTransposer.basePower;

		if (uId.equals(RecipeUidsTE.TRANSPOSER_FILL)) {
			progressBack = Drawables.getDrawables(guiHelper).getProgressLeft(Drawables.PROGRESS_DROP);

			IDrawableStatic fluidDrawable = Drawables.getDrawables(guiHelper).getProgressLeft(Drawables.PROGRESS_DROP);
			IDrawableStatic progressDrawable = Drawables.getDrawables(guiHelper).getProgressLeftFill(Drawables.PROGRESS_DROP);

			fluid = guiHelper.createAnimatedDrawable(fluidDrawable, energy / TileTransposer.basePower, StartDirection.RIGHT, true);
			progress = guiHelper.createAnimatedDrawable(progressDrawable, energy / TileTransposer.basePower, StartDirection.RIGHT, false);
		} else {
			progressBack = Drawables.getDrawables(guiHelper).getProgress(Drawables.PROGRESS_DROP);

			IDrawableStatic fluidDrawable = Drawables.getDrawables(guiHelper).getProgress(Drawables.PROGRESS_DROP);
			IDrawableStatic progressDrawable = Drawables.getDrawables(guiHelper).getProgressFill(Drawables.PROGRESS_DROP);

			fluid = guiHelper.createAnimatedDrawable(fluidDrawable, Math.max(10, energy / basePower), StartDirection.LEFT, true);
			progress = guiHelper.createAnimatedDrawable(progressDrawable, Math.max(10, energy / basePower), StartDirection.LEFT, false);
		}
		IDrawableStatic speedDrawable = Drawables.getDrawables(guiHelper).getScaleFill(Drawables.SCALE_BUBBLE);
		IDrawableStatic energyDrawable = Drawables.getDrawables(guiHelper).getEnergyFill();

		speed = guiHelper.createAnimatedDrawable(speedDrawable, 1000, StartDirection.TOP, true);
		energyMeter = guiHelper.createAnimatedDrawable(energyDrawable, 1000, StartDirection.TOP, true);
	}

	public void setGuiFluids(Map<Integer, ? extends IGuiIngredient<FluidStack>> fluids) {

		guiFluids = fluids;
	}

	@Override
	public void getIngredients(IIngredients ingredients) {

		ingredients.setInputLists(ItemStack.class, inputs);
		ingredients.setOutputLists(ItemStack.class, outputs);

		if (uId.equals(RecipeUidsTE.TRANSPOSER_FILL)) {
			ingredients.setInputLists(FluidStack.class, inputFluids);
		} else {
			ingredients.setOutputLists(FluidStack.class, outputFluids);
		}
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

		if (guiFluids == null) {
			return;
		}
		progressBack.draw(minecraft, 63, 11);

		JEIPluginTE.drawFluid(63, 11, guiFluids.get(0).getDisplayedIngredient(), 24, 16);

		fluid.draw(minecraft, 63, 11);
		progress.draw(minecraft, 63, 11);
		speed.draw(minecraft, 68, 41);
		energyMeter.draw(minecraft, 2, 8);
	}

}
