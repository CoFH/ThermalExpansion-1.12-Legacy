package cofh.thermalexpansion.plugins.nei.handlers;

import static codechicken.lib.gui.GuiDraw.*;

import codechicken.nei.api.stack.PositionedStack;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.util.NEIServerUtils;
import cofh.thermalexpansion.gui.client.machine.GuiTransposer;
import cofh.thermalexpansion.util.crafting.TransposerManager;
import cofh.thermalexpansion.util.crafting.TransposerManager.RecipeTransposer;

import java.awt.Point;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class RecipeHandlerTransposer extends RecipeHandlerBase {

	public static RecipeHandlerTransposer instance = new RecipeHandlerTransposer();

	public RecipeHandlerTransposer() {

		super();
		this.maxEnergy = 40 * 1200;
	}

	@Override
	public void initialize() {

		this.trCoords = new int[] { 107, 8, 24, 18 };
		this.recipeName = "transposer";
		this.containerClass = GuiTransposer.class;
	}

	@Override
	public void drawBackgroundExtras(int recipe) {

		drawTexturedModalRect(74, 10, 132, 96, 18, 18);
		drawTexturedModalRect(71, 37, 150, 96, 26, 26);

		drawTexturedModalRect(36, 20, 224, 0, 16, 16);
		drawProgressBar(36, 20, 240, 0, 16, 16, 100, 7);

		if (((NEIRecipeTransposer) arecipes.get(recipe)).fillRecipe) {
			drawFluid(recipe, false);
			drawTexturedModalRect(107, 9, 176, 32, 24, 16);
			int fluidSize = (int) (cycleticks % 20 / (float) 20 * 24);
			drawFluidRect(107 + 24 - fluidSize, 9, ((NEIRecipeBase) arecipes.get(recipe)).fluid, fluidSize, 16);
			drawProgressBar(107, 9, 200, 32, 24, 16, 20, 2);
		} else {
			drawFluid(recipe, true);
			drawTexturedModalRect(107, 9, 176, 48, 24, 16);
			drawFluidRect(107, 9, ((NEIRecipeBase) arecipes.get(recipe)).fluid, (int) (cycleticks % 20 / (float) 20 * 24), 16);
			drawProgressBar(107, 9, 200, 48, 24, 16, 20, 0);
		}
	}

	@Override
	public void drawExtras(int recipe) {

		drawEnergy(recipe);

		int energy = ((NEIRecipeBase) arecipes.get(recipe)).energy;

		if (energy < 1000) {
			drawString(energy + "RF", 36, 54, 0x939393, false);
		} else if (energy < 10000) {
			drawString(energy + "RF", 30, 54, 0x939393, false);
		} else {
			drawString(energy + "RF", 24, 54, 0x939393, false);
		}
		int fluid = ((NEIRecipeBase) arecipes.get(recipe)).fluid.amount;

		if (fluid < 100) {
			drawString(fluid + "mB", 120, 54, 0x939393, false);
		} else if (fluid < 1000) {
			drawString(fluid + "mB", 114, 54, 0x939393, false);
		} else if (fluid < 10000) {
			drawString(fluid + "mB", 108, 54, 0x939393, false);
		}
		int secondChance = ((NEIRecipeBase) arecipes.get(recipe)).secondaryOutputChance;

		if (secondChance < 10) {
			drawString(secondChance + "%", 120, 46, 0x939393, false);
		} else if (secondChance < 100) {
			drawString(secondChance + "%", 114, 46, 0x939393, false);
		}
	}

	@Override
	public List<String> handleTooltip(GuiRecipe gui, List<String> currenttip, int recipe) {

		int minX1 = 153;
		int maxX1 = 169;
		int minY1 = 19;
		int maxY1 = 79;
		int yOffset = 65;
		Point mousepos = getMousePosition();

		if (mousepos.x >= minX1 + gui.guiLeft && mousepos.x < maxX1 + gui.guiLeft && mousepos.y >= minY1 + gui.guiTop && mousepos.y < maxY1 + gui.guiTop
				&& arecipe[0] == recipe) {
			FluidStack fluid = ((NEIRecipeBase) arecipes.get(recipe)).fluid;
			currenttip.add(fluid.getFluid().getLocalizedName(fluid));
		} else if (mousepos.x >= minX1 + gui.guiLeft && mousepos.x < maxX1 + gui.guiLeft && mousepos.y >= minY1 + gui.guiTop + yOffset
				&& mousepos.y < maxY1 + gui.guiTop + yOffset && arecipe[1] == recipe) {
			FluidStack fluid = ((NEIRecipeBase) arecipes.get(recipe)).fluid;
			currenttip.add(fluid.getFluid().getLocalizedName(fluid));
		}
		return super.handleTooltip(gui, currenttip, recipe);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void loadCraftingRecipes(String outputId, Object... results) {

		if (outputId.equals(getOverlayIdentifier())) {
			RecipeTransposer[] recipes = TransposerManager.getFillRecipeList();
			for (RecipeTransposer recipe : recipes) {
				arecipes.add(new NEIRecipeTransposer(recipe, true));
			}
			recipes = TransposerManager.getExtractionRecipeList();
			for (RecipeTransposer recipe : recipes) {
				arecipes.add(new NEIRecipeTransposer(recipe, false));
			}
		} else if ((outputId.equals("liquid")) && (results.length == 1) && ((results[0] instanceof FluidStack))) {
			RecipeTransposer[] recipes = TransposerManager.getExtractionRecipeList();
			for (RecipeTransposer recipe : recipes) {
				if (recipe.getFluid().isFluidEqual((FluidStack) results[0])) {
					arecipes.add(new NEIRecipeTransposer(recipe, false));
				}
			}
		} else {
			super.loadCraftingRecipes(outputId, results);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void loadCraftingRecipes(ItemStack result) {

		RecipeTransposer[] recipes = TransposerManager.getFillRecipeList();
		for (RecipeTransposer recipe : recipes) {
			if (NEIServerUtils.areStacksSameType(recipe.getOutput(), result)) {
				arecipes.add(new NEIRecipeTransposer(recipe, true));
			}
		}
		recipes = TransposerManager.getExtractionRecipeList();
		for (RecipeTransposer recipe : recipes) {
			if (NEIServerUtils.areStacksSameType(recipe.getOutput(), result)) {
				arecipes.add(new NEIRecipeTransposer(recipe, false));
			}
		}
	}

	@Override
	public void loadUsageRecipes(String inputId, Object... ingredients) {

		if (inputId.equals("fuel") && getClass() == RecipeHandlerTransposer.class) {
			loadCraftingRecipes(getOverlayIdentifier());
		} else if ((inputId.equals("liquid")) && (ingredients.length == 1) && ((ingredients[0] instanceof FluidStack))) {
			RecipeTransposer[] recipes = TransposerManager.getFillRecipeList();
			for (RecipeTransposer recipe : recipes) {
				if (recipe.getFluid().isFluidEqual((FluidStack) ingredients[0])) {
					this.arecipes.add(new NEIRecipeTransposer(recipe, true));
				}
			}

		} else {
			super.loadUsageRecipes(inputId, ingredients);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void loadUsageRecipes(ItemStack ingredient) {

		RecipeTransposer[] recipes = TransposerManager.getFillRecipeList();
		for (RecipeTransposer recipe : recipes) {
			if (NEIServerUtils.areStacksSameType(recipe.getInput(), ingredient)) {
				arecipes.add(new NEIRecipeTransposer(recipe, true));
			}
		}
		recipes = TransposerManager.getExtractionRecipeList();
		for (RecipeTransposer recipe : recipes) {
			if (NEIServerUtils.areStacksSameType(recipe.getInput(), ingredient)) {
				arecipes.add(new NEIRecipeTransposer(recipe, false));
			}
		}
	}

	/* RECIPE CLASS */
	class NEIRecipeTransposer extends NEIRecipeBase {

		boolean fillRecipe = false;

		public NEIRecipeTransposer(RecipeTransposer recipe, boolean fillRecipe) {

			input = new PositionedStack(recipe.getInput(), 75, 11);

			if (recipe.getOutput() != null) {
				output = new PositionedStack(recipe.getOutput(), 75, 41);
			}
			energy = recipe.getEnergy();
			fluid = recipe.getFluid();
			secondaryOutputChance = recipe.getChance();
			this.fillRecipe = fillRecipe;

			setOres();
		}
	}

}
