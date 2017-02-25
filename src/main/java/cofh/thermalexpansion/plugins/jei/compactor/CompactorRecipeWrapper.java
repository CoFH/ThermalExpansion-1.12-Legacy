package cofh.thermalexpansion.plugins.jei.compactor;

import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.util.crafting.CompactorManager.ComparableItemStackCompactor;
import cofh.thermalexpansion.util.crafting.CompactorManager.Mode;
import cofh.thermalexpansion.util.crafting.CompactorManager.RecipeCompactor;
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

public class CompactorRecipeWrapper extends BlankRecipeWrapper {

	/* Recipe */
	final List<List<ItemStack>> inputs;
	final List<ItemStack> outputs;

	final int energy;
	final Mode mode;

	/* Animation */
	final IDrawableAnimated progress;
	final IDrawableAnimated energyMeter;

	public CompactorRecipeWrapper(IGuiHelper guiHelper, RecipeCompactor recipe, Mode mode) {

		List<ItemStack> recipeInputs = new ArrayList<>();

		if (ComparableItemStackCompactor.getOreID(recipe.getInput()) != -1) {
			for (ItemStack ore : OreDictionary.getOres(ItemHelper.getOreName(recipe.getInput()))) {
				recipeInputs.add(ItemHelper.cloneStack(ore, recipe.getInput().stackSize));
			}
		} else {
			recipeInputs.add(recipe.getInput());
		}
		List<ItemStack> recipeOutputs = new ArrayList<>();
		recipeOutputs.add(recipe.getOutput());

		inputs = Collections.singletonList(recipeInputs);
		outputs = recipeOutputs;

		energy = recipe.getEnergy();

		this.mode = mode;

		IDrawableStatic progressDrawable = Drawables.getDrawables(guiHelper).getProgressFill(0);
		IDrawableStatic energyDrawable = Drawables.getDrawables(guiHelper).getEnergyFill();
		this.progress = guiHelper.createAnimatedDrawable(progressDrawable, energy / 20, StartDirection.LEFT, false);
		this.energyMeter = guiHelper.createAnimatedDrawable(energyDrawable, 1000, StartDirection.TOP, true);
	}

	@Override
	public void getIngredients(IIngredients ingredients) {

		ingredients.setInputLists(ItemStack.class, inputs);
		ingredients.setOutputs(ItemStack.class, outputs);
	}

	public String getUid() {

		switch (mode) {
			case STORAGE:
				return RecipeUidsTE.COMPACTOR_STORAGE;
			case MINT:
				return RecipeUidsTE.COMPACTOR_MINT;
			default:
				return RecipeUidsTE.COMPACTOR_PRESS;
		}
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

		progress.draw(minecraft, 69, 23);
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
