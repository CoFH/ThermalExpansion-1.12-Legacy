package cofh.thermalexpansion.plugins.jei.dynamo.compression;

import cofh.thermalexpansion.block.dynamo.TileDynamoCompression;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.dynamo.BaseFuelWrapper;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawableAnimated.StartDirection;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class CompressionFuelWrapper extends BaseFuelWrapper {

	protected List<FluidStack> inputs;

	public CompressionFuelWrapper(IGuiHelper guiHelper, FluidStack fuel, int energy) {

		List<FluidStack> recipeInputs = new ArrayList<>();
		recipeInputs.add(fuel);

		this.inputs = recipeInputs;
		this.energy = energy;

		IDrawableStatic progressDrawable = Drawables.getDrawables(guiHelper).getScaleFill(Drawables.SCALE_FLAME);
		IDrawableStatic energyDrawable = Drawables.getDrawables(guiHelper).getEnergyFill();

		durationFill = guiHelper.createAnimatedDrawable(progressDrawable, Math.max(1, energy / TileDynamoCompression.basePower), StartDirection.TOP, true);
		energyMeter = guiHelper.createAnimatedDrawable(energyDrawable, 1000, StartDirection.BOTTOM, false);
	}

	@Override
	public void getIngredients(IIngredients ingredients) {

		ingredients.setInputs(FluidStack.class, inputs);
	}

}
