package cofh.thermalexpansion.plugins.jei.dynamo.magmatic;

import cofh.thermalexpansion.block.dynamo.TileDynamoMagmatic;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.dynamo.BaseFuelWrapper;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawableAnimated.StartDirection;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class MagmaticFuelWrapper extends BaseFuelWrapper {

	protected List<FluidStack> inputs;

	public MagmaticFuelWrapper(IGuiHelper guiHelper, FluidStack fuel, int energy) {

		List<FluidStack> recipeInputs = new ArrayList<>();
		recipeInputs.add(fuel);

		this.inputs = recipeInputs;
		this.energy = energy;

		IDrawableStatic progressDrawable = Drawables.getDrawables(guiHelper).getScaleFill(Drawables.SCALE_FLAME);
		IDrawableStatic energyDrawable = Drawables.getDrawables(guiHelper).getEnergyFill();

		durationFill = guiHelper.createAnimatedDrawable(progressDrawable, Math.max(10, energy / TileDynamoMagmatic.basePower), StartDirection.TOP, true);
		energyMeter = guiHelper.createAnimatedDrawable(energyDrawable, 1000, StartDirection.BOTTOM, false);
	}

	@Override
	public void getIngredients(IIngredients ingredients) {

		ingredients.setInputs(FluidStack.class, inputs);
	}

}
