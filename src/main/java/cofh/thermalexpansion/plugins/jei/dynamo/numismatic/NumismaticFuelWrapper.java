package cofh.thermalexpansion.plugins.jei.dynamo.numismatic;

import cofh.thermalexpansion.block.dynamo.TileDynamoNumismatic;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.plugins.jei.dynamo.BaseFuelWrapper;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawableAnimated.StartDirection;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class NumismaticFuelWrapper extends BaseFuelWrapper {

	protected List<ItemStack> inputs;

	public NumismaticFuelWrapper(IGuiHelper guiHelper, ItemStack fuel, int energy) {

		this(guiHelper, fuel, energy, RecipeUidsTE.DYNAMO_NUMISMATIC);
	}

	public NumismaticFuelWrapper(IGuiHelper guiHelper, ItemStack fuel, int energy, String uIdIn) {

		uId = uIdIn;

		List<ItemStack> recipeInputs = new ArrayList<>();
		recipeInputs.add(fuel);

		this.inputs = recipeInputs;
		this.energy = energy;

		IDrawableStatic progressDrawable = Drawables.getDrawables(guiHelper).getScaleFill(Drawables.SCALE_ALCHEMY);
		IDrawableStatic energyDrawable = Drawables.getDrawables(guiHelper).getEnergyFill();

		durationFill = guiHelper.createAnimatedDrawable(progressDrawable, energy / TileDynamoNumismatic.basePower, StartDirection.TOP, true);
		energyMeter = guiHelper.createAnimatedDrawable(energyDrawable, 1000, StartDirection.BOTTOM, false);
	}

	@Override
	public void getIngredients(IIngredients ingredients) {

		ingredients.setInputs(ItemStack.class, inputs);
	}

}
