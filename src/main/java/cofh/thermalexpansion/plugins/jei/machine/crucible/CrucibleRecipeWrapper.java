package cofh.thermalexpansion.plugins.jei.machine.crucible;

import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.block.machine.TileCrucible;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.JEIPluginTE;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.plugins.jei.machine.BaseRecipeWrapper;
import cofh.thermalexpansion.util.managers.machine.CrucibleManager.ComparableItemStackCrucible;
import cofh.thermalexpansion.util.managers.machine.CrucibleManager.CrucibleRecipe;
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

public class CrucibleRecipeWrapper extends BaseRecipeWrapper {

	/* Recipe */
	protected List<List<ItemStack>> inputs;
	protected List<FluidStack> outputFluids;

	/* Animation */
	protected IDrawableAnimated fluid;
	protected IDrawableAnimated progress;
	protected IDrawableAnimated speed;

	public CrucibleRecipeWrapper(IGuiHelper guiHelper, CrucibleRecipe recipe) {

		this(guiHelper, recipe, RecipeUidsTE.CRUCIBLE);
	}

	public CrucibleRecipeWrapper(IGuiHelper guiHelper, CrucibleRecipe recipe, String uIdIn) {

		uId = uIdIn;

		List<ItemStack> recipeInputs = new ArrayList<>();

		int oreID = new ComparableItemStackCrucible(recipe.getInput()).getOreID(recipe.getInput());
		if (oreID != -1) {
			for (ItemStack ore : OreDictionary.getOres(ItemHelper.oreProxy.getOreName(oreID), false)) {
				recipeInputs.add(ItemHelper.cloneStack(ore, recipe.getInput().getCount()));
			}
		} else {
			recipeInputs.add(recipe.getInput());
		}
		List<FluidStack> recipeOutputFluids = new ArrayList<>();
		recipeOutputFluids.add(recipe.getOutput());

		inputs = Collections.singletonList(recipeInputs);
		outputFluids = recipeOutputFluids;

		energy = recipe.getEnergy();

		IDrawableStatic fluidDrawable = Drawables.getDrawables(guiHelper).getProgress(Drawables.PROGRESS_DROP);
		IDrawableStatic progressDrawable = Drawables.getDrawables(guiHelper).getProgressFill(Drawables.PROGRESS_DROP);
		IDrawableStatic speedDrawable = Drawables.getDrawables(guiHelper).getScaleFill(Drawables.SCALE_FLAME);
		IDrawableStatic energyDrawable = Drawables.getDrawables(guiHelper).getEnergyFill();

		int basePower = TileCrucible.basePower;

		if (uId.equals(RecipeUidsTE.CRUCIBLE_LAVA)) {
			basePower *= TileCrucible.LAVA_MULTIPLIER;
			energy *= 100 + TileCrucible.LAVA_ENERGY_MOD;
			energy /= 100;
		}
		fluid = guiHelper.createAnimatedDrawable(fluidDrawable, energy / basePower, StartDirection.LEFT, true);
		progress = guiHelper.createAnimatedDrawable(progressDrawable, energy / basePower, StartDirection.LEFT, false);
		speed = guiHelper.createAnimatedDrawable(speedDrawable, 1000, StartDirection.TOP, true);
		energyMeter = guiHelper.createAnimatedDrawable(energyDrawable, 1000, StartDirection.TOP, true);

	}

	@Override
	public void getIngredients(IIngredients ingredients) {

		ingredients.setInputLists(ItemStack.class, inputs);
		ingredients.setOutputs(FluidStack.class, outputFluids);
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

		JEIPluginTE.drawFluid(69, 23, outputFluids.get(0), 24, 16);

		fluid.draw(minecraft, 69, 23);
		progress.draw(minecraft, 69, 23);
		speed.draw(minecraft, 43, 33);
		energyMeter.draw(minecraft, 2, 8);
	}

}
