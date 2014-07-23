package thermalexpansion.plugins.nei.handlers;

import static codechicken.lib.gui.GuiDraw.drawString;
import static codechicken.lib.gui.GuiDraw.drawTexturedModalRect;
import static codechicken.lib.gui.GuiDraw.getMousePosition;

import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.GuiRecipe;

import java.awt.Point;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import thermalexpansion.gui.client.machine.GuiCrucible;
import thermalexpansion.util.crafting.CrucibleManager;
import thermalexpansion.util.crafting.CrucibleManager.RecipeCrucible;

public class RecipeHandlerCrucible extends RecipeHandlerBase {

	public static RecipeHandlerCrucible instance = new RecipeHandlerCrucible();

	public RecipeHandlerCrucible() {

		super();
		this.maxEnergy = 400000;
	}

	@Override
	public void initialize() {

		this.trCoords = new int[] { 98, 23, 24, 18 };
		this.recipeName = "crucible";
		this.containerClass = GuiCrucible.class;
	}

	@Override
	public void drawBackgroundExtras(int recipe) {

		drawTexturedModalRect(50, 17, 176, 96, 18, 18);
		drawTexturedModalRect(51, 36, 224, 32, 16, 16);
		drawProgressBar(51, 36, 240, 32, 16, 16, 100, 7);

		drawFluid(recipe, true);
		drawTexturedModalRect(98, 24, 176, 48, 24, 16);
		drawFluidRect(98, 24, ((NEIRecipeBase) arecipes.get(recipe)).fluid, (int) (cycleticks % 20 / (float) 20 * 24), 16);
		drawProgressBar(98, 24, 200, 48, 24, 16, 20, 0);
	}

	@Override
	public void drawExtras(int recipe) {

		drawEnergy(recipe);

		int energy = ((NEIRecipeBase) arecipes.get(recipe)).energy;

		if (energy < 1000) {
			drawString(energy + "RF", 56, 54, 0x939393, false);
		} else if (energy < 10000) {
			drawString(energy + "RF", 50, 54, 0x939393, false);
		} else if (energy < 100000) {
			drawString(energy + "RF", 44, 54, 0x939393, false);
		} else {
			drawString(energy + "RF", 38, 54, 0x939393, false);
		}
		int fluid = ((NEIRecipeBase) arecipes.get(recipe)).fluid.amount;

		if (fluid < 100) {
			drawString(fluid + "mB", 120, 54, 0x939393, false);
		} else if (fluid < 1000) {
			drawString(fluid + "mB", 114, 54, 0x939393, false);
		} else if (fluid < 10000) {
			drawString(fluid + "mB", 108, 54, 0x939393, false);
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
			RecipeCrucible[] recipes = CrucibleManager.getRecipeList();
			for (RecipeCrucible recipe : recipes) {
				arecipes.add(new NEIRecipeCrucible(recipe));
			}
		} else if ((outputId.equals("liquid")) && (results.length == 1) && ((results[0] instanceof FluidStack))) {
			RecipeCrucible[] recipes = CrucibleManager.getRecipeList();
			for (RecipeCrucible recipe : recipes) {
				if (recipe.getOutput().isFluidEqual((FluidStack) results[0])) {
					this.arecipes.add(new NEIRecipeCrucible(recipe));
				}
			}
		} else {
			super.loadCraftingRecipes(outputId, results);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void loadCraftingRecipes(ItemStack result) {

		RecipeCrucible[] recipes = CrucibleManager.getRecipeList();
		for (RecipeCrucible recipe : recipes) {
			if (recipe.getOutput().isFluidEqual(result)) {
				arecipes.add(new NEIRecipeCrucible(recipe));
			}
		}
	}

	@Override
	public void loadUsageRecipes(String inputId, Object... ingredients) {

		if (inputId.equals("fuel") && getClass() == RecipeHandlerCrucible.class) {
			loadCraftingRecipes(getOverlayIdentifier());
		} else {
			super.loadUsageRecipes(inputId, ingredients);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void loadUsageRecipes(ItemStack ingredient) {

		RecipeCrucible[] recipes = CrucibleManager.getRecipeList();
		for (RecipeCrucible recipe : recipes) {
			if (NEIServerUtils.areStacksSameType(recipe.getInput(), ingredient)) {
				arecipes.add(new NEIRecipeCrucible(recipe));
			}
		}
	}

	/* RECIPE CLASS */
	class NEIRecipeCrucible extends NEIRecipeBase {

		public NEIRecipeCrucible(RecipeCrucible recipe) {

			this.input = new PositionedStack(recipe.getInput(), 51, 18);
			this.fluid = recipe.getOutput();
			this.energy = recipe.getEnergy();

			setOres();
		}
	}

}
