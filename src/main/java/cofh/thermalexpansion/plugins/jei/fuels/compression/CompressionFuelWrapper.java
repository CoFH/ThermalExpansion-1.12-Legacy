package cofh.thermalexpansion.plugins.jei.fuels.compression;

import cofh.thermalexpansion.block.dynamo.TileDynamoCompression;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.fuels.BaseFuelWrapper;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawableAnimated.StartDirection;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompressionFuelWrapper extends BaseFuelWrapper {

	final List<List<FluidStack>> inputs;

	public CompressionFuelWrapper(IGuiHelper guiHelper, FluidStack fuel, int energy) {

		List<FluidStack> recipeInputs = new ArrayList<>();
		recipeInputs.add(fuel);

		this.inputs = Collections.singletonList(recipeInputs);
		this.energy = energy;

		IDrawableStatic progressDrawable = Drawables.getDrawables(guiHelper).getScaleFill(Drawables.SCALE_FLAME);
		IDrawableStatic energyDrawable = Drawables.getDrawables(guiHelper).getEnergyFill();

		durationFill = guiHelper.createAnimatedDrawable(progressDrawable, energy / TileDynamoCompression.basePower, StartDirection.TOP, true);
		energyMeter = guiHelper.createAnimatedDrawable(energyDrawable, 1000, StartDirection.BOTTOM, false);
	}

	@Override
	public void getIngredients(IIngredients ingredients) {

		ingredients.setInputLists(FluidStack.class, inputs);
	}

}
