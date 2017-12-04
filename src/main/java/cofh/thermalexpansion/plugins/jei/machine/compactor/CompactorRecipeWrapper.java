package cofh.thermalexpansion.plugins.jei.machine.compactor;

import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.block.machine.TileCompactor;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.machine.BaseRecipeWrapper;
import cofh.thermalexpansion.util.managers.machine.CompactorManager.CompactorRecipe;
import cofh.thermalexpansion.util.managers.machine.CompactorManager.ComparableItemStackCompactor;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableAnimated.StartDirection;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

public class CompactorRecipeWrapper extends BaseRecipeWrapper {

	/* Recipe */
	protected List<ItemStack> inputs;
	protected List<ItemStack> outputs;

	/* Animation */
	protected IDrawableAnimated progress;
	protected IDrawableAnimated speed;

	public CompactorRecipeWrapper(IGuiHelper guiHelper, CompactorRecipe recipe, String uIdIn) {

		uId = uIdIn;

		List<ItemStack> recipeInputs = new ArrayList<>();

		int oreID = ComparableItemStackCompactor.getOreID(recipe.getInput());
		if (oreID != -1) {
			for (ItemStack ore : OreDictionary.getOres(ItemHelper.oreProxy.getOreName(oreID), false)) {
				recipeInputs.add(ItemHelper.cloneStack(ore, recipe.getInput().getCount()));
			}
		} else {
			recipeInputs.add(recipe.getInput());
		}
		List<ItemStack> recipeOutputs = new ArrayList<>();
		recipeOutputs.add(recipe.getOutput());

		inputs = recipeInputs;
		outputs = recipeOutputs;

		energy = recipe.getEnergy();

		IDrawableStatic progressDrawable = Drawables.getDrawables(guiHelper).getProgressFill(Drawables.PROGRESS_ARROW);
		IDrawableStatic speedDrawable = Drawables.getDrawables(guiHelper).getScaleFill(Drawables.SCALE_COMPACT);
		IDrawableStatic energyDrawable = Drawables.getDrawables(guiHelper).getEnergyFill();

		progress = guiHelper.createAnimatedDrawable(progressDrawable, energy / TileCompactor.basePower, StartDirection.LEFT, false);
		speed = guiHelper.createAnimatedDrawable(speedDrawable, 1000, StartDirection.TOP, true);
		energyMeter = guiHelper.createAnimatedDrawable(energyDrawable, 1000, StartDirection.TOP, true);
	}

	@Override
	public void getIngredients(IIngredients ingredients) {

		ingredients.setInputs(ItemStack.class, inputs);
		ingredients.setOutputs(ItemStack.class, outputs);
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

		progress.draw(minecraft, 69, 23);
		speed.draw(minecraft, 43, 33);
		energyMeter.draw(minecraft, 2, 8);
	}

}
