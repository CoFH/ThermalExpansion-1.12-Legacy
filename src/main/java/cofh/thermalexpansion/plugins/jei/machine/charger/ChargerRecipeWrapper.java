package cofh.thermalexpansion.plugins.jei.machine.charger;

import cofh.thermalexpansion.block.machine.TileCharger;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.machine.BaseRecipeWrapper;
import cofh.thermalexpansion.util.managers.machine.ChargerManager.ChargerRecipe;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableAnimated.StartDirection;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ChargerRecipeWrapper extends BaseRecipeWrapper {

	/* Recipe */
	protected List<ItemStack> inputs;
	protected List<ItemStack> outputs;

	/* Animation */
	protected IDrawableAnimated progress;

	public ChargerRecipeWrapper(IGuiHelper guiHelper, ChargerRecipe recipe) {

		List<ItemStack> recipeInputs = new ArrayList<>();
		recipeInputs.add(recipe.getInput());

		List<ItemStack> recipeOutputs = new ArrayList<>();
		recipeOutputs.add(recipe.getOutput());

		inputs = recipeInputs;
		outputs = recipeOutputs;

		energy = recipe.getEnergy();

		IDrawableStatic progressDrawable = Drawables.getDrawables(guiHelper).getScaleFill(Drawables.SCALE_FLUX);
		IDrawableStatic energyDrawable = Drawables.getDrawables(guiHelper).getEnergyFill();

		progress = guiHelper.createAnimatedDrawable(progressDrawable, Math.max(10, energy / TileCharger.basePower), StartDirection.BOTTOM, false);
		energyMeter = guiHelper.createAnimatedDrawable(energyDrawable, 1000, StartDirection.TOP, true);
	}

	@Override
	public void getIngredients(IIngredients ingredients) {

		ingredients.setInputs(ItemStack.class, inputs);
		ingredients.setOutputs(ItemStack.class, outputs);
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

		progress.draw(minecraft, 34, 43);
		energyMeter.draw(minecraft, 2, 8);
	}

}
