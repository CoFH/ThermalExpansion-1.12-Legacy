package thermalexpansion.plugins.nei.handlers;

import static codechicken.lib.gui.GuiDraw.*;

import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;

import net.minecraft.item.ItemStack;

import thermalexpansion.gui.client.machine.GuiSawmill;
import thermalexpansion.util.crafting.SawmillManager;
import thermalexpansion.util.crafting.SawmillManager.ComparableItemStackSawmill;
import thermalexpansion.util.crafting.SawmillManager.RecipeSawmill;

public class RecipeHandlerSawmill extends RecipeHandlerBase {

	public static RecipeHandlerSawmill instance = new RecipeHandlerSawmill();

	public RecipeHandlerSawmill() {

		super();
		this.maxEnergy = 20 * 1200;
	}

	@Override
	public void initialize() {

		this.trCoords = new int[] { 74, 23, 24, 18 };
		this.recipeName = "sawmill";
		this.containerClass = GuiSawmill.class;
	}

	@Override
	public void drawBackgroundExtras(int recipe) {

		drawTexturedModalRect(50, 17, 176, 96, 18, 18);
		drawTexturedModalRect(106, 13, 224, 96, 26, 26);
		drawTexturedModalRect(110, 44, 176, 96, 18, 18);

		drawTexturedModalRect(51, 36, 224, 48, 16, 16);
		drawProgressBar(51, 36, 240, 48, 16, 16, 100, 7);

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
			RecipeSawmill[] recipes = SawmillManager.getRecipeList();
			for (RecipeSawmill recipe : recipes) {
				arecipes.add(new NEIRecipeSawmill(recipe));
			}
		} else {
			super.loadCraftingRecipes(outputId, results);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void loadCraftingRecipes(ItemStack result) {

		RecipeSawmill[] recipes = SawmillManager.getRecipeList();
		for (RecipeSawmill recipe : recipes) {
			if (NEIServerUtils.areStacksSameType(recipe.getPrimaryOutput(), result) || recipe.getSecondaryOutput() != null
					&& NEIServerUtils.areStacksSameType(recipe.getSecondaryOutput(), result)) {
				arecipes.add(new NEIRecipeSawmill(recipe));
			}
		}
	}

	@Override
	public void loadUsageRecipes(String inputId, Object... ingredients) {

		if (inputId.equals("fuel") && getClass() == RecipeHandlerSawmill.class) {
			loadCraftingRecipes(getOverlayIdentifier());
		} else {
			super.loadUsageRecipes(inputId, ingredients);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void loadUsageRecipes(ItemStack ingredient) {

		RecipeSawmill[] recipes = SawmillManager.getRecipeList();
		for (RecipeSawmill recipe : recipes) {
			if (NEIServerUtils.areStacksSameType(recipe.getInput(), ingredient)) {
				arecipes.add(new NEIRecipeSawmill(recipe));
			}
		}
	}

	/* RECIPE CLASS */
	class NEIRecipeSawmill extends NEIRecipeBase {

		public NEIRecipeSawmill(RecipeSawmill recipe) {

			input = new PositionedStack(recipe.getInput(), 51, 18);
			output = new PositionedStack(recipe.getPrimaryOutput(), 111, 18);
			energy = recipe.getEnergy();

			if (recipe.getSecondaryOutput() != null) {
				secondaryOutput = new PositionedStack(recipe.getSecondaryOutput(), 111, 45);
				secondaryOutputChance = recipe.getSecondaryOutputChance();
			}
			setOres();
			cycleInput = ComparableItemStackSawmill.getOreID(inputOreName) != -1;
			cycleSecondary = ComparableItemStackSawmill.getOreID(secondaryInputOreName) != -1;
		}
	}

}
