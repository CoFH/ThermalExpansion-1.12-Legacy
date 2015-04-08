package cofh.thermalexpansion.plugins.nei.handlers;

import static codechicken.lib.gui.GuiDraw.*;

import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import cofh.thermalexpansion.gui.client.machine.GuiCharger;
import cofh.thermalexpansion.util.crafting.ChargerManager;
import cofh.thermalexpansion.util.crafting.ChargerManager.RecipeCharger;

import net.minecraft.item.ItemStack;

public class RecipeHandlerCharger extends RecipeHandlerBase {

	public static RecipeHandlerCharger instance = new RecipeHandlerCharger();

	public RecipeHandlerCharger() {

		super();
		this.maxEnergy = 400 * 1000;
	}

	@Override
	public void initialize() {

		this.trCoords = new int[] { 79, 43, 18, 16 };
		this.recipeName = "charger";
		this.containerClass = GuiCharger.class;
	}

	@Override
	public void drawBackgroundExtras(int recipe) {

		drawTexturedModalRect(79, 24, 132, 96, 18, 18);
		drawTexturedModalRect(116, 20, 150, 96, 26, 26);

		drawTexturedModalRect(79, 44, 224, 80, 16, 16);
		drawProgressBar(79, 44, 240, 80, 16, 16, 20, 7);
	}

	@Override
	public void drawExtras(int recipe) {

		drawEnergy(recipe);

		int energy = ((NEIRecipeBase) arecipes.get(recipe)).energy;

		if (energy < 1000) {
			drawString(energy + "RF", 44, 48, 0x939393, false);
		} else if (energy < 10000) {
			drawString(energy + "RF", 38, 48, 0x939393, false);
		} else if (energy < 100000) {
			drawString(energy + "RF", 32, 48, 0x939393, false);
		} else {
			drawString(energy + "RF", 26, 48, 0x939393, false);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void loadCraftingRecipes(String outputId, Object... results) {

		if (outputId.equals(getOverlayIdentifier())) {
			RecipeCharger[] recipes = ChargerManager.getRecipeList();
			for (RecipeCharger recipe : recipes) {
				arecipes.add(new NEIRecipeCharger(recipe));
			}
		} else {
			super.loadCraftingRecipes(outputId, results);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void loadCraftingRecipes(ItemStack result) {

		RecipeCharger[] recipes = ChargerManager.getRecipeList();
		for (ChargerManager.RecipeCharger recipe : recipes) {
			if (NEIServerUtils.areStacksSameType(recipe.getOutput(), result)) {
				arecipes.add(new NEIRecipeCharger(recipe));
			}
		}
	}

	@Override
	public void loadUsageRecipes(String inputId, Object... ingredients) {

		if (inputId.equals("fuel") && getClass() == RecipeHandlerCharger.class) {
			loadCraftingRecipes(getOverlayIdentifier());
		} else {
			super.loadUsageRecipes(inputId, ingredients);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void loadUsageRecipes(ItemStack ingredient) {

		RecipeCharger[] recipes = ChargerManager.getRecipeList();
		for (RecipeCharger recipe : recipes) {
			if (NEIServerUtils.areStacksSameType(recipe.getInput(), ingredient)) {
				arecipes.add(new NEIRecipeCharger(recipe));
			}
		}
	}

	/* RECIPE CLASS */
	class NEIRecipeCharger extends NEIRecipeBase {

		public NEIRecipeCharger(RecipeCharger recipe) {

			input = new PositionedStack(recipe.getInput(), 80, 25);
			output = new PositionedStack(recipe.getOutput(), 121, 25);
			energy = recipe.getEnergy();

			setOres();
		}
	}

}
