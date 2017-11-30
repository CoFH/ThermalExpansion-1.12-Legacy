package cofh.thermalexpansion.plugins.jei.crafting.extruder;

import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.gui.client.machine.GuiExtruder;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.plugins.jei.crafting.BaseRecipeCategory;
import cofh.thermalexpansion.util.managers.machine.ExtruderManager;
import cofh.thermalexpansion.util.managers.machine.ExtruderManager.ExtruderRecipe;
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

public class ExtruderRecipeCategory extends BaseRecipeCategory<ExtruderRecipeWrapper> {

	public static boolean enable = true;

	public static void register(IRecipeCategoryRegistration registry) {

		if (!enable) {
			return;
		}
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipeCategories(new ExtruderRecipeCategory(guiHelper));
	}

	public static void initialize(IModRegistry registry) {

		if (!enable) {
			return;
		}
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipes(getRecipes(guiHelper), RecipeUidsTE.EXTRUDER);
		registry.addRecipeClickArea(GuiExtruder.class, 112, 49, 24, 16, RecipeUidsTE.EXTRUDER);
		registry.addRecipeCatalyst(BlockMachine.machineExtruder, RecipeUidsTE.EXTRUDER);
	}

	public static List<ExtruderRecipeWrapper> getRecipes(IGuiHelper guiHelper) {

		List<ExtruderRecipeWrapper> recipes = new ArrayList<>();

		for (ExtruderRecipe recipe : ExtruderManager.getRecipeList()) {
			recipes.add(new ExtruderRecipeWrapper(guiHelper, recipe));
		}
		return recipes;
	}

	final IDrawableStatic progress;
	final IDrawableStatic slot;
	final IDrawableStatic tankHot;
	final IDrawableStatic tankCold;
	final IDrawableStatic tankHotOverlay;
	final IDrawableStatic tankColdOverlay;

	public ExtruderRecipeCategory(IGuiHelper guiHelper) {

		background = guiHelper.createDrawable(GuiExtruder.TEXTURE, 38, 11, 24, 62, 0, 0, 16, 124);
		energyMeter = Drawables.getDrawables(guiHelper).getEnergyEmpty();
		localizedName = StringHelper.localize("tile.thermalexpansion.machine.extruder.name");

		progress = Drawables.getDrawables(guiHelper).getProgressLeft(Drawables.PROGRESS_DROP);
		slot = Drawables.getDrawables(guiHelper).getSlot(Drawables.SLOT_OUTPUT);
		tankHot = Drawables.getDrawables(guiHelper).getTank(Drawables.TANK_THIN);
		tankCold = Drawables.getDrawables(guiHelper).getTank(Drawables.TANK_THIN);
		tankHotOverlay = Drawables.getDrawables(guiHelper).getTankSmallOverlay(Drawables.TANK_THIN);
		tankColdOverlay = Drawables.getDrawables(guiHelper).getTankSmallOverlay(Drawables.TANK_THIN);
	}

	@Nonnull
	@Override
	public String getUid() {

		return RecipeUidsTE.EXTRUDER;
	}

	@Override
	public void drawExtras(@Nonnull Minecraft minecraft) {

		progress.draw(minecraft, 82, 23);
		slot.draw(minecraft, 46, 19);
		tankHot.draw(minecraft, 116, 0);
		tankCold.draw(minecraft, 125, 0);
		energyMeter.draw(minecraft, 2, 8);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, ExtruderRecipeWrapper recipeWrapper, IIngredients ingredients) {

		List<List<FluidStack>> inputFluids = ingredients.getInputs(FluidStack.class);
		List<List<ItemStack>> outputItems = ingredients.getOutputs(ItemStack.class);

		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();

		guiItemStacks.init(0, false, 50, 23);
		guiFluidStacks.init(0, true, 117, 1, 7, 60, 1000, false, tankHotOverlay);
		guiFluidStacks.init(1, true, 126, 1, 7, 60, 1000, false, tankColdOverlay);

		guiItemStacks.set(0, outputItems.get(0));
		guiFluidStacks.set(0, inputFluids.get(0));
		guiFluidStacks.set(1, inputFluids.get(1));

	}

}
