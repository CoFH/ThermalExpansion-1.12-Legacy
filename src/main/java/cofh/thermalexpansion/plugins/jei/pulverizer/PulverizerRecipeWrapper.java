package cofh.thermalexpansion.plugins.jei.pulverizer;

import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.util.crafting.PulverizerManager.ComparableItemStackPulverizer;
import cofh.thermalexpansion.util.crafting.PulverizerManager.RecipePulverizer;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableAnimated.StartDirection;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PulverizerRecipeWrapper extends BlankRecipeWrapper {

	/* Recipe */
	final List<List<ItemStack>> inputs;
	final List<ItemStack> outputs;

	final int energy;
	final int chance;

	/* Animation */
	final IDrawableAnimated progress;
	final IDrawableAnimated speed;
	final IDrawableAnimated energyMeter;

	public PulverizerRecipeWrapper(IGuiHelper guiHelper, RecipePulverizer recipe) {

		List<ItemStack> recipeInputs = new ArrayList<>();

		if (ComparableItemStackPulverizer.getOreID(recipe.getInput()) != -1) {
			for (ItemStack ore : OreDictionary.getOres(ItemHelper.getOreName(recipe.getInput()))) {
				recipeInputs.add(ItemHelper.cloneStack(ore, recipe.getInput().stackSize));
			}
		} else {
			recipeInputs.add(recipe.getInput());
		}
		List<ItemStack> recipeOutputs = new ArrayList<>();
		recipeOutputs.add(recipe.getPrimaryOutput());

		if (recipe.getSecondaryOutput() != null) {
			recipeOutputs.add(recipe.getSecondaryOutput());
		}
		inputs = Collections.singletonList(recipeInputs);
		outputs = recipeOutputs;

		energy = recipe.getEnergy();
		chance = recipe.getSecondaryOutputChance();

		IDrawableStatic progressDrawable = Drawables.getDrawables(guiHelper).getProgressFill(0);
		IDrawableStatic speedDrawable = Drawables.getDrawables(guiHelper).getSpeedFill(1);
		IDrawableStatic energyDrawable = Drawables.getDrawables(guiHelper).getEnergyFill();

		this.progress = guiHelper.createAnimatedDrawable(progressDrawable, energy / 20, StartDirection.LEFT, false);
		this.speed = guiHelper.createAnimatedDrawable(speedDrawable, 1000, StartDirection.TOP, true);
		this.energyMeter = guiHelper.createAnimatedDrawable(energyDrawable, 1000, StartDirection.TOP, true);
	}

	@Override
	public void getIngredients(IIngredients ingredients) {

		ingredients.setInputLists(ItemStack.class, inputs);
		ingredients.setOutputs(ItemStack.class, outputs);
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

		progress.draw(minecraft, 69, 23);
		speed.draw(minecraft, 43, 33);
		energyMeter.draw(minecraft, 2, 8);
	}

	@Nullable
	public List<String> getTooltipStrings(int mouseX, int mouseY) {

		List<String> tooltip = new ArrayList<>();

		if (mouseX > 2 && mouseX < 15 && mouseY > 8 && mouseY < 49) {
			tooltip.add(StringHelper.localize("info.cofh.energy") + ": " + energy + " RF");
		}
		return tooltip;
	}

}
