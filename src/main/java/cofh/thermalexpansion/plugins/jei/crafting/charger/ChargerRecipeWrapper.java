package cofh.thermalexpansion.plugins.jei.crafting.charger;

import cofh.thermalexpansion.block.machine.TileCharger;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.crafting.BaseRecipeWrapper;
import cofh.thermalexpansion.util.crafting.ChargerManager.RecipeCharger;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableAnimated.StartDirection;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChargerRecipeWrapper extends BaseRecipeWrapper {

	/* Recipe */
	final List<List<ItemStack>> inputs;
	final List<ItemStack> outputs;

	/* Animation */
	final IDrawableAnimated progress;

	public ChargerRecipeWrapper(IGuiHelper guiHelper, RecipeCharger recipe) {

		List<ItemStack> recipeInputs = new ArrayList<>();
		recipeInputs.add(recipe.getInput());

		List<ItemStack> recipeOutputs = new ArrayList<>();
		recipeOutputs.add(recipe.getOutput());

		inputs = Collections.singletonList(recipeInputs);
		outputs = recipeOutputs;

		energy = recipe.getEnergy();

		IDrawableStatic progressDrawable = Drawables.getDrawables(guiHelper).getScaleFill(Drawables.SCALE_FLUX);
		IDrawableStatic energyDrawable = Drawables.getDrawables(guiHelper).getEnergyFill();

		progress = guiHelper.createAnimatedDrawable(progressDrawable, energy / TileCharger.basePower, StartDirection.BOTTOM, false);
		energyMeter = guiHelper.createAnimatedDrawable(energyDrawable, 1000, StartDirection.TOP, true);
	}

	@Override
	public void getIngredients(IIngredients ingredients) {

		ingredients.setInputLists(ItemStack.class, inputs);
		ingredients.setOutputs(ItemStack.class, outputs);
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

		progress.draw(minecraft, 34, 43);
		energyMeter.draw(minecraft, 2, 8);
	}

}
