package cofh.thermalexpansion.plugins.jei.machine.precipitator;

import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.gui.client.machine.GuiPrecipitator;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.plugins.jei.machine.BaseRecipeCategory;
import cofh.thermalexpansion.util.managers.machine.PrecipitatorManager;
import cofh.thermalexpansion.util.managers.machine.PrecipitatorManager.PrecipitatorRecipe;
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

public class PrecipitatorRecipeCategory extends BaseRecipeCategory<PrecipitatorRecipeWrapper> {

	public static boolean enable = true;

	public static void register(IRecipeCategoryRegistration registry) {

		if (!enable) {
			return;
		}
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipeCategories(new PrecipitatorRecipeCategory(guiHelper));
	}

	public static void initialize(IModRegistry registry) {

		if (!enable) {
			return;
		}
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipes(getRecipes(guiHelper), RecipeUidsTE.PRECIPITATOR);
		registry.addRecipeClickArea(GuiPrecipitator.class, 85, 26, 24, 16, RecipeUidsTE.PRECIPITATOR);
		registry.addRecipeCatalyst(BlockMachine.machinePrecipitator, RecipeUidsTE.PRECIPITATOR);
	}

	public static List<PrecipitatorRecipeWrapper> getRecipes(IGuiHelper guiHelper) {

		List<PrecipitatorRecipeWrapper> recipes = new ArrayList<>();

		for (PrecipitatorRecipe recipe : PrecipitatorManager.getRecipeList()) {
			recipes.add(new PrecipitatorRecipeWrapper(guiHelper, recipe));
		}
		return recipes;
	}

	protected IDrawableStatic progress;
	protected IDrawableStatic speed;
	protected IDrawableStatic slot;
	protected IDrawableStatic tankOverlay;

	public PrecipitatorRecipeCategory(IGuiHelper guiHelper) {

		background = guiHelper.createDrawable(GuiPrecipitator.TEXTURE, 26, 11, 52, 40, 0, 22, 16, 96);
		energyMeter = Drawables.getDrawables(guiHelper).getEnergyEmpty();
		localizedName = StringHelper.localize("tile.thermalexpansion.machine.precipitator.name");

		progress = Drawables.getDrawables(guiHelper).getProgress(Drawables.PROGRESS_DROP);
		speed = Drawables.getDrawables(guiHelper).getScale(Drawables.SCALE_SNOWFLAKE);
		slot = Drawables.getDrawables(guiHelper).getSlot(Drawables.SLOT_OUTPUT);
		tankOverlay = Drawables.getDrawables(guiHelper).getTankSmallOverlay(Drawables.TANK_SHORT);
	}

	@Nonnull
	@Override
	public String getUid() {

		return RecipeUidsTE.PRECIPITATOR;
	}

	@Override
	public void drawExtras(@Nonnull Minecraft minecraft) {

		progress.draw(minecraft, 69, 23);
		speed.draw(minecraft, 34, 40);
		slot.draw(minecraft, 101, 19);
		energyMeter.draw(minecraft, 2, 8);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, PrecipitatorRecipeWrapper recipeWrapper, IIngredients ingredients) {

		List<List<FluidStack>> inputFluids = ingredients.getInputs(FluidStack.class);
		List<List<ItemStack>> outputItems = ingredients.getOutputs(ItemStack.class);

		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();

		guiItemStacks.init(0, false, 105, 23);
		guiFluidStacks.init(0, true, 34, 8, 16, 30, TEProps.MAX_FLUID_MEDIUM, false, tankOverlay);

		guiItemStacks.set(0, outputItems.get(0));
		guiFluidStacks.set(0, inputFluids.get(0));

	}

}
