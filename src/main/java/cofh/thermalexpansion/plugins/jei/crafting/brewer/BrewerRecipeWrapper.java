package cofh.thermalexpansion.plugins.jei.crafting.brewer;

import cofh.thermalexpansion.block.machine.TileBrewer;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.plugins.jei.crafting.BaseRecipeWrapper;
import cofh.thermalexpansion.util.managers.machine.BrewerManager.BrewerRecipe;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableAnimated.StartDirection;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BrewerRecipeWrapper extends BaseRecipeWrapper {

	/* Recipe */
	final List<List<ItemStack>> inputs;
	final List<ItemStack> outputs;

	/* Animation */
	final IDrawableStatic progressBack;
	final IDrawableStatic speedBack;
	final IDrawableAnimated progress;
	final IDrawableAnimated speed;

	public BrewerRecipeWrapper(IGuiHelper guiHelper, BrewerRecipe recipe) {

		this(guiHelper, recipe, RecipeUidsTE.BREWER);
	}

	public BrewerRecipeWrapper(IGuiHelper guiHelper, BrewerRecipe recipe, String uIdIn) {

		uId = uIdIn;

		List<List<ItemStack>> recipeInputs = new ArrayList<>();
		List<ItemStack> recipeInputsPrimary = new ArrayList<>();
		List<ItemStack> recipeInputsSecondary = new ArrayList<>();

		recipeInputsPrimary.add(recipe.getPrimaryInput());
		recipeInputsSecondary.add(recipe.getSecondaryInput());

		recipeInputs.add(recipeInputsPrimary);
		recipeInputs.add(recipeInputsSecondary);

		List<ItemStack> recipeOutputs = new ArrayList<>();
		recipeOutputs.add(recipe.getOutput());

		inputs = recipeInputs;
		outputs = recipeOutputs;

		energy = recipe.getEnergy();

		progressBack = Drawables.getDrawables(guiHelper).getProgress(Drawables.PROGRESS_ARROW);
		speedBack = Drawables.getDrawables(guiHelper).getScale(Drawables.SCALE_FLAME);

		IDrawableStatic progressDrawable = Drawables.getDrawables(guiHelper).getProgressFill(Drawables.PROGRESS_ARROW_FLUID);
		IDrawableStatic speedDrawable = Drawables.getDrawables(guiHelper).getScaleFill(Drawables.SCALE_FLAME);
		IDrawableStatic energyDrawable = Drawables.getDrawables(guiHelper).getEnergyFill();

		progress = guiHelper.createAnimatedDrawable(progressDrawable, energy / TileBrewer.basePower, StartDirection.LEFT, false);
		speed = guiHelper.createAnimatedDrawable(speedDrawable, 1000, StartDirection.TOP, true);
		energyMeter = guiHelper.createAnimatedDrawable(energyDrawable, 1000, StartDirection.TOP, true);
	}

	@Override
	public void getIngredients(IIngredients ingredients) {

		ingredients.setInputLists(ItemStack.class, inputs);
		ingredients.setOutputs(ItemStack.class, outputs);
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

		progress.draw(minecraft, 69, 23);
		speed.draw(minecraft, 34, 33);
		energyMeter.draw(minecraft, 2, 8);
	}

}
