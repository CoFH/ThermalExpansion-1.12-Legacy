package cofh.thermalexpansion.plugins.jei.crafting.refinery;

import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.gui.client.machine.GuiRefinery;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.plugins.jei.crafting.BaseRecipeCategory;
import cofh.thermalexpansion.util.managers.machine.RefineryManager;
import cofh.thermalexpansion.util.managers.machine.RefineryManager.RefineryRecipe;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class RefineryRecipeCategory extends BaseRecipeCategory<RefineryRecipeWrapper> {

	public static boolean enable = true;

	public static void register(IRecipeCategoryRegistration registry) {

		if (!enable) {
			return;
		}
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipeCategories(new RefineryRecipeCategory(guiHelper));
	}

	public static void initialize(IModRegistry registry) {

		if (!enable) {
			return;
		}
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipes(getRecipes(guiHelper), RecipeUidsTE.REFINERY);
		registry.addRecipeClickArea(GuiRefinery.class, 76, 34, 24, 16, RecipeUidsTE.REFINERY);
		registry.addRecipeCatalyst(BlockMachine.machineRefinery, RecipeUidsTE.REFINERY);
	}

	public static List<RefineryRecipeWrapper> getRecipes(IGuiHelper guiHelper) {

		List<RefineryRecipeWrapper> recipes = new ArrayList<>();

		for (RefineryRecipe recipe : RefineryManager.getRecipeList()) {
			recipes.add(new RefineryRecipeWrapper(guiHelper, recipe));
		}
		return recipes;
	}

	IDrawableStatic drop;
	IDrawableStatic slot;
	IDrawableStatic tank;
	IDrawableStatic tankOverlayInput;
	IDrawableStatic tankOverlayOutput;

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
		List<List<ItemStack>> outputItems = ingredients.getOutputs(ItemStack.class);
		List<List<FluidStack>> outputFluids = ingredients.getOutputs(FluidStack.class);

		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();

		guiFluidStacks.init(0, true, 22, 8, 16, 30, 1000, false, tankOverlayInput);
		guiItemStacks.init(0, false, 81, 23);
		guiFluidStacks.init(1, false, 117, 1, 16, 60, 1000, false, tankOverlayOutput);

		guiFluidStacks.set(0, inputs.get(0));
		guiItemStacks.set(0, outputItems.get(0));
		guiFluidStacks.set(1, outputFluids.get(0));
	}

}
