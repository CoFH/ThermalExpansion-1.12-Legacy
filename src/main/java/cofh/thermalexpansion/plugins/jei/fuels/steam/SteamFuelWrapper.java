package cofh.thermalexpansion.plugins.jei.fuels.steam;

import cofh.thermalexpansion.block.dynamo.TileDynamoSteam;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.fuels.BaseFuelWrapper;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawableAnimated.StartDirection;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SteamFuelWrapper extends BaseFuelWrapper {

	final List<List<ItemStack>> inputs;

	public SteamFuelWrapper(IGuiHelper guiHelper, ItemStack fuel, int energy) {

		List<ItemStack> recipeInputs = new ArrayList<>();
		recipeInputs.add(fuel);

		this.inputs = Collections.singletonList(recipeInputs);
		this.energy = energy;

		IDrawableStatic progressDrawable = Drawables.getDrawables(guiHelper).getScaleFill(Drawables.SCALE_FLAME);
		IDrawableStatic energyDrawable = Drawables.getDrawables(guiHelper).getEnergyFill();

		durationFill = guiHelper.createAnimatedDrawable(progressDrawable, energy / TileDynamoSteam.basePower, StartDirection.TOP, true);
		energyMeter = guiHelper.createAnimatedDrawable(energyDrawable, 1000, StartDirection.BOTTOM, false);
	}

	@Override
	public void getIngredients(IIngredients ingredients) {

		ingredients.setInputLists(ItemStack.class, inputs);
	}

}
