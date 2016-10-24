package cofh.thermalexpansion.plugins.nei.handlers;

import static codechicken.lib.gui.GuiDraw.*;

import codechicken.nei.api.stack.PositionedStack;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.util.NEIServerUtils;
import cofh.lib.util.helpers.FluidHelper;
import cofh.thermalexpansion.gui.client.machine.GuiInsolator;
import cofh.thermalexpansion.util.crafting.InsolatorManager;
import cofh.thermalexpansion.util.crafting.InsolatorManager.RecipeInsolator;

import java.awt.Point;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class RecipeHandlerInsolator extends RecipeHandlerBase {

	public static RecipeHandlerInsolator instance = new RecipeHandlerInsolator();

	public RecipeHandlerInsolator() {

		super();
		this.maxEnergy = 20 * 1200;
	}

	@Override
	public void initialize() {

		this.trCoords = new int[] { 74, 23, 24, 18 };
		this.recipeName = "insolator";
		this.containerClass = GuiInsolator.class;
	}

	@Override
	public void drawBackgroundExtras(int recipe) {

		drawTexturedModalRect(26, 17, 132, 96, 18, 18);
		drawTexturedModalRect(50, 17, 132, 96, 18, 18);
		drawTexturedModalRect(106, 13, 150, 96, 26, 26);
		drawTexturedModalRect(110, 44, 132, 96, 18, 18);

		drawTexturedModalRect(39, 37, 224, 96, 16, 16);
		drawProgressBar(39, 37, 240, 96, 16, 16, 100, 7);

		drawFluid(recipe, false);

		drawTexturedModalRect(74, 24, 176, 16, 24, 16);
		drawProgressBar(74, 24, 200, 16, 24, 16, 20, 0);
	}

	@Override
	public void drawExtras(int recipe) {

		drawEnergy(recipe);

		int energy = ((NEIRecipeBase) arecipes.get(recipe)).energy;

		if (energy < 1000) {
			drawString(energy + "RF", 36, 54, 0x939393, false);
		} else {
			drawString(energy + "RF", 30, 54, 0x939393, false);
		}
		if (((NEIRecipeBase) arecipes.get(recipe)).secondaryOutput != null) {
			int secondChance = ((NEIRecipeBase) arecipes.get(recipe)).secondaryOutputChance;

			if (secondChance < 10) {
				drawString(secondChance + "%", 96, 54, 0x939393, false);
			} else if (secondChance >= 100) {
				drawString(secondChance + "%", 84, 54, 0x939393, false);
			} else {
				drawString(secondChance + "%", 90, 54, 0x939393, false);
			}
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
			FluidStack fluid = FluidHelper.WATER;
			currenttip.add(fluid.getFluid().getLocalizedName(fluid));
		} else if (mousepos.x >= minX1 + gui.guiLeft && mousepos.x < maxX1 + gui.guiLeft && mousepos.y >= minY1 + gui.guiTop + yOffset
				&& mousepos.y < maxY1 + gui.guiTop + yOffset && arecipe[1] == recipe) {
			FluidStack fluid = FluidHelper.WATER;
			currenttip.add(fluid.getFluid().getLocalizedName(fluid));
		}
		return super.handleTooltip(gui, currenttip, recipe);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void loadCraftingRecipes(String outputId, Object... results) {

		if (outputId.equals(getOverlayIdentifier())) {
			RecipeInsolator[] recipes = InsolatorManager.getRecipeList();
			for (RecipeInsolator recipe : recipes) {
				arecipes.add(new NEIRecipeInsolator(recipe));
			}
		} else {
			super.loadCraftingRecipes(outputId, results);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void loadCraftingRecipes(ItemStack result) {

		RecipeInsolator[] recipes = InsolatorManager.getRecipeList();
		for (RecipeInsolator recipe : recipes) {
			if (NEIServerUtils.areStacksSameType(recipe.getPrimaryOutput(), result) || recipe.getSecondaryOutput() != null
					&& NEIServerUtils.areStacksSameType(recipe.getSecondaryOutput(), result)) {
				arecipes.add(new NEIRecipeInsolator(recipe));
			}
		}
	}

	@Override
	public void loadUsageRecipes(String inputId, Object... ingredients) {

		if (inputId.equals("fuel") && getClass() == RecipeHandlerInsolator.class) {
			loadCraftingRecipes(getOverlayIdentifier());
		} else {
			super.loadUsageRecipes(inputId, ingredients);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void loadUsageRecipes(ItemStack ingredient) {

		RecipeInsolator[] recipes = InsolatorManager.getRecipeList();
		for (RecipeInsolator recipe : recipes) {
			if (NEIServerUtils.areStacksSameType(recipe.getPrimaryInput(), ingredient)
					|| NEIServerUtils.areStacksSameType(recipe.getSecondaryInput(), ingredient)) {
				arecipes.add(new NEIRecipeInsolator(recipe));
			}
		}
	}

	/* RECIPE CLASS */
	class NEIRecipeInsolator extends NEIRecipeBase {

		public NEIRecipeInsolator(RecipeInsolator recipe) {

			this.input = new PositionedStack(recipe.getPrimaryInput(), 51, 18);
			this.output = new PositionedStack(recipe.getPrimaryOutput(), 111, 18);
			this.energy = recipe.getEnergy();
			this.fluid = new FluidStack(FluidRegistry.WATER, this.energy / 10);

			if (recipe.getSecondaryInput() != null) {
				secondaryInput = new PositionedStack(recipe.getSecondaryInput(), 27, 18);
			}
			if (recipe.getSecondaryOutput() != null) {
				secondaryOutput = new PositionedStack(recipe.getSecondaryOutput(), 111, 45);
				secondaryOutputChance = recipe.getSecondaryOutputChance();
			}
			setOres();
		}
	}

}
