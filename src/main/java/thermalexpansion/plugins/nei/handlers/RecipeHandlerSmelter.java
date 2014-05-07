package thermalexpansion.plugins.nei.handlers;

import net.minecraft.item.ItemStack;
import thermalexpansion.gui.client.machine.GuiSmelter;
import thermalexpansion.util.crafting.SmelterManager;
import thermalexpansion.util.crafting.SmelterManager.RecipeSmelter;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;

public class RecipeHandlerSmelter extends RecipeHandlerBase {

	public static RecipeHandlerSmelter instance = new RecipeHandlerSmelter();

	public RecipeHandlerSmelter() {

		super();
		this.maxEnergy = 40 * 1200;
	}

	@Override
	public void initialize() {

		this.trCoords = new int[] { 74, 23, 24, 18 };
		this.recipeName = "smelter";
		this.containerClass = GuiSmelter.class;
	}

	@Override
	public void drawBackgroundExtras(int recipe) {

		drawTexturedModalRect(26, 17, 176, 96, 18, 18);
		drawTexturedModalRect(50, 17, 176, 96, 18, 18);
		drawTexturedModalRect(106, 13, 224, 96, 26, 26);
		drawTexturedModalRect(110, 44, 176, 96, 18, 18);

		drawTexturedModalRect(37, 36, 224, 32, 16, 16);
		drawProgressBar(37, 36, 240, 32, 16, 16, 100, 7);

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

	@SuppressWarnings("unchecked")
	@Override
	public void loadCraftingRecipes(String outputId, Object... results) {

		if (outputId.equals(getOverlayIdentifier())) {
			RecipeSmelter[] recipes = SmelterManager.getRecipeList();
			for (RecipeSmelter recipe : recipes) {
				arecipes.add(new NEIRecipeSmelter(recipe));
			}
		} else {
			super.loadCraftingRecipes(outputId, results);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void loadCraftingRecipes(ItemStack result) {

		RecipeSmelter[] recipes = SmelterManager.getRecipeList();
		for (RecipeSmelter recipe : recipes) {
			if (NEIServerUtils.areStacksSameType(recipe.getPrimaryOutput(), result) || recipe.getSecondaryOutput() != null
					&& NEIServerUtils.areStacksSameType(recipe.getSecondaryOutput(), result)) {
				arecipes.add(new NEIRecipeSmelter(recipe));
			}
		}
	}

	@Override
	public void loadUsageRecipes(String inputId, Object... ingredients) {

		if (inputId.equals("fuel") && getClass() == RecipeHandlerSmelter.class) {
			loadCraftingRecipes(getOverlayIdentifier());
		} else {
			super.loadUsageRecipes(inputId, ingredients);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void loadUsageRecipes(ItemStack ingredient) {

		RecipeSmelter[] recipes = SmelterManager.getRecipeList();
		for (RecipeSmelter recipe : recipes) {
			if (NEIServerUtils.areStacksSameType(recipe.getPrimaryInput(), ingredient)
					|| NEIServerUtils.areStacksSameType(recipe.getSecondaryInput(), ingredient)) {
				arecipes.add(new NEIRecipeSmelter(recipe));
			}
		}
	}

	/* RECIPE CLASS */
	class NEIRecipeSmelter extends NEIRecipeBase {

		public NEIRecipeSmelter(RecipeSmelter recipe) {

			this.input = new PositionedStack(recipe.getPrimaryInput(), 51, 18);
			this.output = new PositionedStack(recipe.getPrimaryOutput(), 111, 18);
			this.energy = recipe.getEnergy();

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
