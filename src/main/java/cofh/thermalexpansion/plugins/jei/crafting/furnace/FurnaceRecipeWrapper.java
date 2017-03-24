package cofh.thermalexpansion.plugins.jei.crafting.furnace;

import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalexpansion.block.machine.TileFurnace;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.plugins.jei.crafting.BaseRecipeWrapper;
import cofh.thermalexpansion.util.crafting.FurnaceManager.ComparableItemStackFurnace;
import cofh.thermalexpansion.util.crafting.FurnaceManager.RecipeFurnace;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableAnimated.StartDirection;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FurnaceRecipeWrapper extends BaseRecipeWrapper {

	/* Recipe */
	final List<List<ItemStack>> inputs;
	final List<ItemStack> outputs;

	/* Animation */
	final IDrawableAnimated progress;
	final IDrawableAnimated speed;

	public FurnaceRecipeWrapper(IGuiHelper guiHelper, RecipeFurnace recipe) {

		this(guiHelper, recipe, RecipeUidsTE.FURNACE);
	}

	public FurnaceRecipeWrapper(IGuiHelper guiHelper, RecipeFurnace recipe, String uIdIn) {

		uId = uIdIn;

		List<ItemStack> recipeInputs = new ArrayList<>();
		List<ItemStack> recipeOutputs = new ArrayList<>();

		if (ComparableItemStackFurnace.getOreID(recipe.getInput()) != -1) {
			for (ItemStack ore : OreDictionary.getOres(ItemHelper.getOreName(recipe.getInput()))) {
				recipeInputs.add(ItemHelper.cloneStack(ore, recipe.getInput().stackSize));
			}
		} else {
			recipeInputs.add(recipe.getInput());
		}
		switch (uId) {
			case RecipeUidsTE.FURNACE_FOOD:
			case RecipeUidsTE.FURNACE_ORE:
				recipeOutputs.add(ItemHelper.cloneStack(recipe.getOutput(), recipe.getOutput().stackSize + 1));
				energy = recipe.getEnergy() * 3 / 2;
				break;
			default:
				recipeOutputs.add(recipe.getOutput());
				energy = recipe.getEnergy();
				break;
		}
		inputs = Collections.singletonList(recipeInputs);
		outputs = recipeOutputs;

		IDrawableStatic progressDrawable = Drawables.getDrawables(guiHelper).getProgressFill(Drawables.PROGRESS_ARROW);
		IDrawableStatic speedDrawable = Drawables.getDrawables(guiHelper).getScaleFill(Drawables.SCALE_FLAME);
		IDrawableStatic energyDrawable = Drawables.getDrawables(guiHelper).getEnergyFill();

		progress = guiHelper.createAnimatedDrawable(progressDrawable, energy / TileFurnace.basePower, StartDirection.LEFT, false);
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
		speed.draw(minecraft, 43, 33);
		energyMeter.draw(minecraft, 2, 8);
	}

}
