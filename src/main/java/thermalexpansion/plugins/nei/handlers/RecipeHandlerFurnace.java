package thermalexpansion.plugins.nei.handlers;

import net.minecraft.item.ItemStack;
import thermalexpansion.gui.client.machine.GuiFurnace;
import thermalexpansion.util.crafting.FurnaceManager;
import thermalexpansion.util.crafting.FurnaceManager.RecipeFurnace;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;

public class RecipeHandlerFurnace extends RecipeHandlerBase {

	public static RecipeHandlerFurnace instance = new RecipeHandlerFurnace();

	public RecipeHandlerFurnace() {

		super();
		this.maxEnergy = 20 * 1200;
	}

	@Override
	public void initialize() {

		this.trCoords = new int[] { 74, 23, 24, 18 };
		this.recipeName = "furnace";
		this.containerClass = GuiFurnace.class;
	}

	@Override
	public void drawBackgroundExtras(int recipe) {

		drawTexturedModalRect(50, 17, 176, 96, 18, 18);
		drawTexturedModalRect(106, 22, 224, 96, 26, 26);

		drawTexturedModalRect(51, 36, 224, 32, 16, 16);
		drawProgressBar(51, 36, 240, 32, 16, 16, 100, 7);

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
	}

	@SuppressWarnings("unchecked")
	@Override
	public void loadCraftingRecipes(String outputId, Object... results) {

		if (outputId.equals(getOverlayIdentifier())) {
			RecipeFurnace[] recipes = FurnaceManager.getRecipeList();
			for (RecipeFurnace recipe : recipes) {
				arecipes.add(new NEIRecipeFurnace(recipe));
			}
		} else {
			super.loadCraftingRecipes(outputId, results);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void loadCraftingRecipes(ItemStack result) {

		RecipeFurnace[] recipes = FurnaceManager.getRecipeList();
		for (thermalexpansion.util.crafting.FurnaceManager.RecipeFurnace recipe : recipes) {
			if (NEIServerUtils.areStacksSameType(recipe.getOutput(), result)) {
				arecipes.add(new NEIRecipeFurnace(recipe));
			}
		}
	}

	@Override
	public void loadUsageRecipes(String inputId, Object... ingredients) {

		if (inputId.equals("fuel") && getClass() == RecipeHandlerFurnace.class) {
			loadCraftingRecipes(getOverlayIdentifier());
		} else {
			super.loadUsageRecipes(inputId, ingredients);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void loadUsageRecipes(ItemStack ingredient) {

		RecipeFurnace[] recipes = FurnaceManager.getRecipeList();
		for (RecipeFurnace recipe : recipes) {
			if (NEIServerUtils.areStacksSameType(recipe.getInput(), ingredient)) {
				arecipes.add(new NEIRecipeFurnace(recipe));
			}
		}
	}

	/* RECIPE CLASS */
	class NEIRecipeFurnace extends NEIRecipeBase {

		public NEIRecipeFurnace(RecipeFurnace recipe) {

			input = new PositionedStack(recipe.getInput(), 51, 18);
			output = new PositionedStack(recipe.getOutput(), 111, 27);
			energy = recipe.getEnergy();

			setOres();
		}
	}

}
