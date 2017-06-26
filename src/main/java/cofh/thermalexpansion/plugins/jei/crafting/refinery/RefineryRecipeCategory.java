package cofh.thermalexpansion.plugins.jei.crafting.refinery;

import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.gui.client.machine.GuiRefinery;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.util.managers.machine.RefineryManager;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.*;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class RefineryRecipeCategory extends BlankRecipeCategory<RefineryRecipeWrapper> {

	public static boolean enable = true;

	public static void initialize(IModRegistry registry) {

		if (!enable) {
			return;
		}
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipeCategories(new RefineryRecipeCategory(guiHelper));
		registry.addRecipeHandlers(new RefineryRecipeHandler());
		registry.addRecipes(getRecipes(guiHelper));
		registry.addRecipeClickArea(GuiRefinery.class, 76, 34, 24, 16, RecipeUidsTE.REFINERY);
		registry.addRecipeCategoryCraftingItem(BlockMachine.machineRefinery, RecipeUidsTE.REFINERY);
	}

	public static List<RefineryRecipeWrapper> getRecipes(IGuiHelper guiHelper) {

		List<RefineryRecipeWrapper> recipes = new ArrayList<>();

		for (RefineryManager.RecipeRefinery recipe : RefineryManager.getRecipeList()) {
			recipes.add(new RefineryRecipeWrapper(guiHelper, recipe));
		}
		return recipes;
	}

	IDrawableStatic background;
	IDrawableStatic energyMeter;
	IDrawableStatic drop;
	IDrawableStatic slot;
	IDrawableStatic tank;
	IDrawableStatic tankOverlayInput;
	IDrawableStatic tankOverlayOutput;
	String localizedName;

	public RefineryRecipeCategory(IGuiHelper guiHelper) {

		background = guiHelper.createDrawable(GuiRefinery.TEXTURE, 38, 11, 24, 62, 0, 0, 16, 124);
		energyMeter = Drawables.getDrawables(guiHelper).getEnergyEmpty();
		drop = Drawables.getDrawables(guiHelper).getProgress(Drawables.PROGRESS_DROP);
		slot = Drawables.getDrawables(guiHelper).getSlot(Drawables.SLOT_OUTPUT);
		tank = Drawables.getDrawables(guiHelper).getTank(Drawables.TANK);
		tankOverlayInput = Drawables.getDrawables(guiHelper).getTankSmallOverlay(Drawables.TANK_SHORT);
		tankOverlayOutput = Drawables.getDrawables(guiHelper).getTankSmallOverlay(Drawables.TANK);
		localizedName = StringHelper.localize("tile.thermalexpansion.machine.refinery.name");
	}

	@Nonnull
	@Override
	public String getUid() {

		return RecipeUidsTE.REFINERY;
	}

	@Nonnull
	@Override
	public String getTitle() {

		return localizedName;
	}

	@Nonnull
	@Override
	public IDrawable getBackground() {

		return background;
	}

	@Override
	public void drawExtras(@Nonnull Minecraft minecraft) {

		drop.draw(minecraft, 46, 23);
		slot.draw(minecraft, 77, 19);
		tank.draw(minecraft, 116, 0);
		energyMeter.draw(minecraft, 2, 8);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, RefineryRecipeWrapper recipeWrapper, IIngredients ingredients) {

		List<List<FluidStack>> inputs = ingredients.getInputs(FluidStack.class);
		List<ItemStack> outputItems = ingredients.getOutputs(ItemStack.class);
		List<FluidStack> outputFluids = ingredients.getOutputs(FluidStack.class);

		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();

		guiFluidStacks.init(0, true, 22, 8, 16, 30, 1000, false, tankOverlayInput);
		guiFluidStacks.init(1, false, 117, 1, 16, 60, 1000, false, tankOverlayOutput);

		guiFluidStacks.set(0, inputs.get(0));

		if (!outputItems.isEmpty()) {
			guiItemStacks.init(0, false, 81, 23);
			guiItemStacks.set(0, outputItems.get(0));
		}
		guiFluidStacks.set(1, outputFluids.get(0));
	}

}
