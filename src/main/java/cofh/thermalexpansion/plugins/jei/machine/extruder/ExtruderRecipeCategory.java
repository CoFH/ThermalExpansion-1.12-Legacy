package cofh.thermalexpansion.plugins.jei.machine.extruder;

import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.gui.client.machine.GuiExtruder;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.plugins.jei.machine.BaseRecipeCategory;
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
		registry.addRecipeCategories(new ExtruderRecipeCategorySedimentary(guiHelper));
	}

	public static void initialize(IModRegistry registry) {

		if (!enable) {
			return;
		}
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipes(getRecipes(guiHelper), RecipeUidsTE.EXTRUDER);
		registry.addRecipeClickArea(GuiExtruder.class, 85, 26, 24, 16, RecipeUidsTE.EXTRUDER, RecipeUidsTE.EXTRUDER_SEDIMENTARY);
		registry.addRecipeCatalyst(BlockMachine.machineExtruder, RecipeUidsTE.EXTRUDER);

		ExtruderRecipeCategorySedimentary.initialize(registry);
	}

	public static List<ExtruderRecipeWrapper> getRecipes(IGuiHelper guiHelper) {

		List<ExtruderRecipeWrapper> recipes = new ArrayList<>();

		for (ExtruderRecipe recipe : ExtruderManager.getRecipeList(false)) {
			recipes.add(new ExtruderRecipeWrapper(guiHelper, recipe));
		}
		return recipes;
	}

	protected IDrawableStatic progress;
	protected IDrawableStatic speed;
	protected IDrawableStatic slot;
	protected IDrawableStatic tankHotOverlay;
	protected IDrawableStatic tankColdOverlay;

	public ExtruderRecipeCategory(IGuiHelper guiHelper) {

		background = guiHelper.createDrawable(GuiExtruder.TEXTURE, 26, 11, 52, 40, 0, 22, 16, 96);
		energyMeter = Drawables.getDrawables(guiHelper).getEnergyEmpty();
		localizedName = StringHelper.localize("tile.thermalexpansion.machine.extruder.name");

		progress = Drawables.getDrawables(guiHelper).getProgress(Drawables.PROGRESS_DROP);
		speed = Drawables.getDrawables(guiHelper).getScale(Drawables.SCALE_COMPACT);
		slot = Drawables.getDrawables(guiHelper).getSlot(Drawables.SLOT_OUTPUT);
		tankHotOverlay = Drawables.getDrawables(guiHelper).getTankSmallOverlay(Drawables.TANK_SHORT);
		tankColdOverlay = Drawables.getDrawables(guiHelper).getTankSmallOverlay(Drawables.TANK_SHORT);
	}

	@Nonnull
	@Override
	public String getUid() {

		return RecipeUidsTE.EXTRUDER;
	}

	@Override
	public void drawExtras(@Nonnull Minecraft minecraft) {

		progress.draw(minecraft, 69, 23);
		speed.draw(minecraft, 34, 40);
		slot.draw(minecraft, 101, 19);
		energyMeter.draw(minecraft, 2, 8);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, ExtruderRecipeWrapper recipeWrapper, IIngredients ingredients) {

		List<List<FluidStack>> inputFluids = ingredients.getInputs(FluidStack.class);
		List<List<ItemStack>> outputItems = ingredients.getOutputs(ItemStack.class);

		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();

		guiItemStacks.init(0, false, 105, 23);
		guiFluidStacks.init(0, true, 22, 8, 16, 30, TEProps.MAX_FLUID_SMALL, false, tankHotOverlay);
		guiFluidStacks.init(1, true, 46, 8, 16, 30, TEProps.MAX_FLUID_SMALL, false, tankColdOverlay);

		guiItemStacks.set(0, outputItems.get(0));
		guiFluidStacks.set(0, inputFluids.get(0));
		guiFluidStacks.set(1, inputFluids.get(1));

	}

}
