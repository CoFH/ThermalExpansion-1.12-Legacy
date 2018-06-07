package cofh.thermalexpansion.plugins.jei.machine.transposer;

import cofh.thermalexpansion.block.machine.TileTransposer;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.util.managers.machine.TransposerManager;
import cofh.thermalexpansion.util.managers.machine.TransposerManager.TransposerRecipe;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawableAnimated.StartDirection;
import mezz.jei.api.gui.IDrawableStatic;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.singletonList;

public class TransposerRecipeWrapperMulti extends TransposerRecipeWrapper {

	public TransposerRecipeWrapperMulti(IGuiHelper guiHelper, List<TransposerRecipe> recipes, String uIdIn) {

		uId = uIdIn;

		List<ItemStack> recipeInputs = new ArrayList<>();
		List<ItemStack> recipeOutputs = new ArrayList<>();
		List<FluidStack> recipeFluids = new ArrayList<>();

		for (TransposerRecipe recipe : recipes) {
			recipeInputs.add(recipe.getInput());
			recipeOutputs.add(recipe.getOutput());
			recipeFluids.add(recipe.getFluid());
		}
		inputs = singletonList(recipeInputs);
		outputs = singletonList(recipeOutputs);

		if (uId.equals(RecipeUidsTE.TRANSPOSER_FILL)) {
			inputFluids = singletonList(recipeFluids);
			outputFluids = Collections.emptyList();
		} else {
			inputFluids = Collections.emptyList();
			outputFluids = singletonList(recipeFluids);
		}
		energy = TransposerManager.DEFAULT_ENERGY;

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

			fluid = guiHelper.createAnimatedDrawable(fluidDrawable, energy / TileTransposer.basePower, StartDirection.LEFT, true);
			progress = guiHelper.createAnimatedDrawable(progressDrawable, energy / TileTransposer.basePower, StartDirection.LEFT, false);
		}
		IDrawableStatic speedDrawable = Drawables.getDrawables(guiHelper).getScaleFill(Drawables.SCALE_BUBBLE);
		IDrawableStatic energyDrawable = Drawables.getDrawables(guiHelper).getEnergyFill();

		speed = guiHelper.createAnimatedDrawable(speedDrawable, 1000, StartDirection.TOP, true);
		energyMeter = guiHelper.createAnimatedDrawable(energyDrawable, 1000, StartDirection.TOP, true);
	}

}
