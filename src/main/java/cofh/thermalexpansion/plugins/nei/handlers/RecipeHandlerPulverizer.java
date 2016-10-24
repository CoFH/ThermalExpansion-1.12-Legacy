package cofh.thermalexpansion.plugins.nei.handlers;

import static codechicken.lib.gui.GuiDraw.*;

import codechicken.nei.api.stack.PositionedStack;
import codechicken.nei.util.NEIServerUtils;
import cofh.thermalexpansion.gui.client.machine.GuiPulverizer;
import cofh.thermalexpansion.util.crafting.PulverizerManager;
import cofh.thermalexpansion.util.crafting.PulverizerManager.ComparableItemStackPulverizer;
import cofh.thermalexpansion.util.crafting.PulverizerManager.RecipePulverizer;

import net.minecraft.item.ItemStack;

public class RecipeHandlerPulverizer extends RecipeHandlerBase {

	public static RecipeHandlerPulverizer instance = new RecipeHandlerPulverizer();

	public RecipeHandlerPulverizer() {

		super();
		this.maxEnergy = 40 * 1200;
	}

	@Override
	public void initialize() {

		this.trCoords = new int[] { 74, 23, 24, 18 };
		this.recipeName = "pulverizer";
		this.containerClass = GuiPulverizer.class;
	}

	@Override
	public void drawBackgroundExtras(int recipe) {

		drawTexturedModalRect(50, 17, 132, 96, 18, 18);
		drawTexturedModalRect(106, 13, 150, 96, 26, 26);
		drawTexturedModalRect(110, 44, 132, 96, 18, 18);

		drawTexturedModalRect(51, 36, 224, 16, 16, 16);
		drawProgressBar(51, 36, 240, 16, 16, 16, 100, 7);

		drawTexturedModalRect(74, 24, 176, 16, 24, 16);
		drawProgressBar(74, 24, 200, 16, 24, 16, 20, 0);
	}

	@Override
	public void drawExtras(int recipe) {

		drawEnergy(recipe);

		int energy = ((NEIRecipeBase) arecipes.get(recipe)).energy;

		if (energy < 1000) {
			drawString(energy + "RF", 46, 54, 0x939393, false);
		} else {
			drawString(energy + "RF", 40, 54, 0x939393, false);
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

	@SuppressWarnings("unchecked")
	@Override
	public void loadCraftingRecipes(String outputId, Object... results) {

		if (outputId.equals(getOverlayIdentifier())) {
			RecipePulverizer[] recipes = PulverizerManager.getRecipeList();
			for (RecipePulverizer recipe : recipes) {
				arecipes.add(new NEIRecipePulverizer(recipe));
			}
		} else {
			super.loadCraftingRecipes(outputId, results);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void loadCraftingRecipes(ItemStack result) {

		RecipePulverizer[] recipes = PulverizerManager.getRecipeList();
		for (RecipePulverizer recipe : recipes) {
			if (NEIServerUtils.areStacksSameType(recipe.getPrimaryOutput(), result) || recipe.getSecondaryOutput() != null
					&& NEIServerUtils.areStacksSameType(recipe.getSecondaryOutput(), result)) {
				arecipes.add(new NEIRecipePulverizer(recipe));
			}
		}
	}

	@Override
	public void loadUsageRecipes(String inputId, Object... ingredients) {

		if (inputId.equals("fuel") && getClass() == RecipeHandlerPulverizer.class) {
			loadCraftingRecipes(getOverlayIdentifier());
		} else {
			super.loadUsageRecipes(inputId, ingredients);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void loadUsageRecipes(ItemStack ingredient) {

		RecipePulverizer[] recipes = PulverizerManager.getRecipeList();
		for (RecipePulverizer recipe : recipes) {
			if (NEIServerUtils.areStacksSameType(recipe.getInput(), ingredient)) {
				arecipes.add(new NEIRecipePulverizer(recipe));
			}
		}
	}

	/* RECIPE CLASS */
	class NEIRecipePulverizer extends NEIRecipeBase {

		public NEIRecipePulverizer(RecipePulverizer recipe) {

			input = new PositionedStack(recipe.getInput(), 51, 18);
			output = new PositionedStack(recipe.getPrimaryOutput(), 111, 18);
			energy = recipe.getEnergy();

			if (recipe.getSecondaryOutput() != null) {
				secondaryOutput = new PositionedStack(recipe.getSecondaryOutput(), 111, 45);
				secondaryOutputChance = recipe.getSecondaryOutputChance();
			}
			setOres();
			cycleInput = ComparableItemStackPulverizer.getOreID(inputOreName) != -1;
			cycleSecondary = ComparableItemStackPulverizer.getOreID(secondaryInputOreName) != -1;
		}
	}

}
