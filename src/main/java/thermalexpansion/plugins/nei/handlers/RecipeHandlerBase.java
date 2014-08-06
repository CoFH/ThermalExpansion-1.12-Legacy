package thermalexpansion.plugins.nei.handlers;

import static codechicken.lib.gui.GuiDraw.changeTexture;
import static codechicken.lib.gui.GuiDraw.drawTexturedModalRect;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIClientConfig;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.GuiUsageRecipe;
import codechicken.nei.recipe.TemplateRecipeHandler;
import cofh.lib.render.RenderHelper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.StringHelper;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import org.lwjgl.opengl.GL11;

import thermalexpansion.core.TEProps;

public abstract class RecipeHandlerBase extends TemplateRecipeHandler {

	Class containerClass;
	String recipeName;
	static final String TEXTURE = TEProps.PATH_GUI + "NEIHandler.png";
	int[] trCoords = new int[4];

	int maxEnergy = 24000;
	int scaleEnergy = 42;

	int maxFluid = 10000;
	int scaleFluid = 60;

	int energyAmount[] = new int[2];
	int fluidAmount[] = new int[2];
	int lastCycle[] = new int[2];
	int arecipe[] = { -1, -1 };

	@Override
	public void loadTransferRects() {

		initialize();
		transferRects.add(new RecipeTransferRect(new Rectangle(trCoords[0], trCoords[1], trCoords[2], trCoords[3]), getOverlayIdentifier()));
	}

	@Override
	public Class<? extends GuiContainer> getGuiClass() {

		return containerClass;
	}

	@Override
	public String getRecipeName() {

		return StringHelper.localize("tile.thermalexpansion.machine." + recipeName + ".name");
	}

	@Override
	public String getGuiTexture() {

		return TEXTURE;
	}

	@Override
	public String getOverlayIdentifier() {

		return "thermalexpansion." + recipeName;
	}

	public abstract void initialize();

	@Override
	public void drawBackground(int recipe) {

		GL11.glColor4f(1, 1, 1, 1);
		changeTexture(getGuiTexture());
		drawTexturedModalRect(0, 0, 5, 11, 166, 65);

		drawBackgroundExtras(recipe);
	}

	public void drawBackgroundExtras(int recipe) {

	}

	@Override
	public boolean keyTyped(GuiRecipe gui, char keyChar, int keyCode, int recipe) {

		if (keyCode == NEIClientConfig.getKeyBinding("gui.recipe")) {
			if (transferFluidTank(gui, recipe, false)) {
				return true;
			}
		} else if (keyCode == NEIClientConfig.getKeyBinding("gui.usage")) {
			if (transferFluidTank(gui, recipe, true)) {
				return true;
			}
		}
		return super.keyTyped(gui, keyChar, keyCode, recipe);
	}

	@Override
	public boolean mouseClicked(GuiRecipe gui, int button, int recipe) {

		if (button == 0) {
			if (transferFluidTank(gui, recipe, false)) {
				return true;
			}
		} else if (button == 1) {
			if (transferFluidTank(gui, recipe, true)) {
				return true;
			}
		}
		return super.mouseClicked(gui, button, recipe);
	}

	protected boolean transferFluidTank(GuiRecipe gui, int recipe, boolean usage) {

		int minX1 = 153;
		int maxX1 = 169;
		int minY1 = 19;
		int maxY1 = 79;
		int yOffset = 65;
		Point mousepos = GuiDraw.getMousePosition();
		FluidStack fluid = null;

		if ((mousepos.x >= minX1 + gui.guiLeft) && (mousepos.x < maxX1 + gui.guiLeft) && (mousepos.y >= minY1 + gui.guiTop)
				&& (mousepos.y < maxY1 + gui.guiTop) && (this.arecipe[0] == recipe)) {
			fluid = ((RecipeHandlerBase.NEIRecipeBase) this.arecipes.get(recipe)).fluid;
		} else if ((mousepos.x >= minX1 + gui.guiLeft) && (mousepos.x < maxX1 + gui.guiLeft) && (mousepos.y >= minY1 + gui.guiTop + yOffset)
				&& (mousepos.y < maxY1 + gui.guiTop + yOffset) && (this.arecipe[1] == recipe)) {
			fluid = ((RecipeHandlerBase.NEIRecipeBase) this.arecipes.get(recipe)).fluid;
		}

		if ((fluid != null) && (fluid.amount > 0) && (usage ? GuiUsageRecipe.openRecipeGui("liquid", fluid) : GuiCraftingRecipe.openRecipeGui("liquid", fluid))) {
			return true;
		}
		return false;
	}

	private void resetCounters() {

		arecipe[0] = -1;
		arecipe[1] = -1;
		energyAmount[0] = 0;
		energyAmount[1] = 0;
		fluidAmount[0] = 0;
		fluidAmount[1] = 0;
		lastCycle[0] = 0;
		lastCycle[1] = 0;
	}

	public void drawEnergy(int recipe) {

		int recipeIndex = 0;

		if (arecipe[0] == -1) {
			arecipe[0] = recipe;
		} else if (arecipe[1] == -1 && arecipe[0] != recipe) {
			arecipe[1] = recipe;
		}
		if (arecipe[0] != recipe && arecipe[1] != recipe) {
			resetCounters();
			drawEnergy(recipe);
			return;
		}
		if (arecipe[1] == recipe) {
			recipeIndex = 1;
		}
		drawTexturedModalRect(4, 2, 0, 96, 16, scaleEnergy);

		int energy = getScaledEnergy(energyAmount[recipeIndex]);
		drawTexturedModalRect(4, 2 + energy, 16, 96 + energy, 16, scaleEnergy - energy);

		if (cycleticks % 20 == 0 && cycleticks != lastCycle[recipeIndex]) {
			if (energyAmount[recipeIndex] == maxEnergy) {
				energyAmount[recipeIndex] = 0;
			}
			energyAmount[recipeIndex] += ((NEIRecipeBase) arecipes.get(recipe)).energy;

			if (energyAmount[recipeIndex] > maxEnergy) {
				energyAmount[recipeIndex] = maxEnergy;
			}
			lastCycle[recipeIndex] = cycleticks;
		}
	}

	public void drawFluid(int recipe, boolean increase) {

		int recipeIndex = 0;

		if (arecipe[0] == -1) {
			arecipe[0] = recipe;
		} else if (arecipe[1] == -1 && arecipe[0] != recipe) {
			arecipe[1] = recipe;
		}
		if (arecipe[0] != recipe && arecipe[1] != recipe) {
			resetCounters();
			drawFluid(recipe, increase);
			return;
		}
		if (arecipe[1] == recipe) {
			recipeIndex = 1;
		}
		drawTexturedModalRect(147, 2, 32, 96, 18, scaleFluid + 2);

		int fluid = getScaledFluid(fluidAmount[recipeIndex]);

		if (increase) {
			drawFluidRect(148, 3 + scaleFluid - fluid, ((NEIRecipeBase) arecipes.get(recipe)).fluid, 16, fluid);
		} else {
			drawFluidRect(148, 3 + fluid, ((NEIRecipeBase) arecipes.get(recipe)).fluid, 16, scaleFluid - fluid);
		}

		if (cycleticks % 20 == 0 && cycleticks != lastCycle[recipeIndex]) {
			if (fluidAmount[recipeIndex] == maxFluid) {
				fluidAmount[recipeIndex] = 0;
			}
			fluidAmount[recipeIndex] += ((NEIRecipeBase) arecipes.get(recipe)).fluid.amount;

			if (fluidAmount[recipeIndex] > maxFluid) {
				fluidAmount[recipeIndex] = maxFluid;
			}
		}
		drawTexturedModalRect(148, 2, 80, 96, 18, scaleFluid + 2);
	}

	public int getScaledEnergy(int amount) {

		return amount * scaleEnergy / maxEnergy;
	}

	public int getScaledFluid(int amount) {

		return amount * scaleFluid / maxFluid;
	}

	protected void drawFluidRect(int j, int k, FluidStack fluid, int width, int height) {

		if (height > scaleFluid) {
			height = scaleFluid;
		}
		int drawHeight = 0;
		int drawWidth = 0;

		RenderHelper.setBlockTextureSheet();
		RenderHelper.setColor3ub(fluid.getFluid().getColor(fluid));

		for (int x = 0; x < width; x += 16) {
			for (int y = 0; y < height; y += 16) {
				drawWidth = Math.min(width - x, 16);
				drawHeight = Math.min(height - y, 16);
				drawScaledTexturedModelRectFromIcon(j + x, k + y, fluid.getFluid().getIcon(), drawWidth, drawHeight);
			}
		}
		GL11.glColor4f(1, 1, 1, 1);
		changeTexture(getGuiTexture());
	}

	public static void drawScaledTexturedModelRectFromIcon(int i, int j, IIcon icon, int x, int y) {

		if (icon == null) {
			return;
		}
		double minU = icon.getMinU();
		double maxU = icon.getMaxU();
		double minV = icon.getMinV();
		double maxV = icon.getMaxV();

		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(i + 0, j + y, GuiDraw.gui.getZLevel(), minU, minV + (maxV - minV) * y / 16F);
		tessellator.addVertexWithUV(i + x, j + y, GuiDraw.gui.getZLevel(), minU + (maxU - minU) * x / 16F, minV + (maxV - minV) * y / 16F);
		tessellator.addVertexWithUV(i + x, j + 0, GuiDraw.gui.getZLevel(), minU + (maxU - minU) * x / 16F, minV);
		tessellator.addVertexWithUV(i + 0, j + 0, GuiDraw.gui.getZLevel(), minU, minV);

		tessellator.draw();
	}

	/* RECIPE CLASS */
	abstract class NEIRecipeBase extends CachedRecipe {

		PositionedStack input = null;
		PositionedStack secondaryInput = null;
		PositionedStack output = null;
		PositionedStack secondaryOutput = null;

		String inputOreName = "Unknown";
		ArrayList<ItemStack> inputList = null;
		String secondaryInputOreName = "Unknown";
		ArrayList<ItemStack> secondaryList = null;

		int inputOrePosition = 0;
		int secondaryOrePosition = 0;

		FluidStack fluid = null;

		int secondaryOutputChance = 0;
		int energy = 0;

		protected void setOres() {

			if (input != null) {
				inputOreName = ItemHelper.getOreName(input.item);

				if (!inputOreName.equals("Unknown")) {
					inputList = OreDictionary.getOres(inputOreName);
				}
			}
			if (secondaryInput != null) {
				secondaryInputOreName = ItemHelper.getOreName(secondaryInput.item);

				if (!secondaryInputOreName.equals("Unknown")) {
					secondaryList = OreDictionary.getOres(secondaryInputOreName);
				}
			}
		}

		protected void incrementPrimary() {

			if (!inputOreName.equals("Unknown")) {
				inputOrePosition++;
				inputOrePosition %= inputList.size();

				int stackSize = input.item.stackSize;
				input.item = inputList.get(inputOrePosition);
				input.item.stackSize = stackSize;
				if (inputList.get(inputOrePosition).getItemDamage() != OreDictionary.WILDCARD_VALUE) {
					input.item.setItemDamage(inputList.get(inputOrePosition).getItemDamage());
				}
			}
		}

		protected void incrementSecondary() {

			if (!secondaryInputOreName.equals("Unknown")) {
				secondaryOrePosition++;
				secondaryOrePosition %= secondaryList.size();

				int stackSize = secondaryInput.item.stackSize;
				secondaryInput.item = secondaryList.get(secondaryOrePosition);
				secondaryInput.item.stackSize = stackSize;
				if (secondaryList.get(secondaryOrePosition).getItemDamage() != OreDictionary.WILDCARD_VALUE) {
					secondaryInput.item.setItemDamage(secondaryList.get(secondaryOrePosition).getItemDamage());
				}
			}
		}

		@Override
		public PositionedStack getIngredient() {

			if (cycleticks % 20 == 0) {
				incrementPrimary();
			}
			return input;
		}

		@Override
		public PositionedStack getResult() {

			return output;
		}

		@Override
		public ArrayList<PositionedStack> getOtherStacks() {

			ArrayList<PositionedStack> stacks = new ArrayList<PositionedStack>();
			if (secondaryOutput != null) {
				stacks.add(secondaryOutput);
			}
			if (secondaryInput != null) {
				if (cycleticks % 20 == 0) {
					incrementSecondary();
				}
				stacks.add(secondaryInput);
			}
			return stacks;
		}
	}

}
