package cofh.thermalexpansion.plugins.jei.insolator;

import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.gui.client.machine.GuiInsolator;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.util.crafting.InsolatorManager;
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

public class InsolatorRecipeCategory extends BlankRecipeCategory<InsolatorRecipeWrapper> {

	public static boolean enable = true;

	public static void initialize(IModRegistry registry) {

		if (!enable) {
			return;
		}
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipeCategories(new InsolatorRecipeCategory(guiHelper));
		registry.addRecipeHandlers(new InsolatorRecipeHandler());
		registry.addRecipes(getRecipes(guiHelper));
		registry.addRecipeClickArea(GuiInsolator.class, 79, 34, 24, 16, RecipeUidsTE.INSOLATOR);
		registry.addRecipeCategoryCraftingItem(BlockMachine.machineInsolator, RecipeUidsTE.INSOLATOR);
	}

	public static List<InsolatorRecipeWrapper> getRecipes(IGuiHelper guiHelper) {

		List<InsolatorRecipeWrapper> recipes = new ArrayList<>();

		for (InsolatorManager.RecipeInsolator recipe : InsolatorManager.getRecipeList()) {
			recipes.add(new InsolatorRecipeWrapper(guiHelper, recipe));
		}
		return recipes;
	}

	IDrawableStatic background;
	IDrawableStatic tank;
	IDrawableStatic tankOverlay;
	IDrawableStatic energyMeter;
	String localizedName;

	public InsolatorRecipeCategory(IGuiHelper guiHelper) {

		background = guiHelper.createDrawable(GuiInsolator.TEXTURE, 26, 11, 124, 62, 0, 0, 16, 24);
		tank = Drawables.getDrawables(guiHelper).getTank(0);
		tankOverlay = Drawables.getDrawables(guiHelper).getTankSmallOverlay(0);
		energyMeter = Drawables.getDrawables(guiHelper).getEnergyEmpty();
		localizedName = StringHelper.localize("tile.thermalexpansion.machine.insolator.name");
	}

	@Nonnull
	@Override
	public String getUid() {

		return RecipeUidsTE.INSOLATOR;
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

		tank.draw(minecraft, 140, 0);
		energyMeter.draw(minecraft, 2, 8);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, InsolatorRecipeWrapper recipeWrapper, IIngredients ingredients) {

		List<List<ItemStack>> inputs = ingredients.getInputs(ItemStack.class);
		List<List<FluidStack>> inputFluids = ingredients.getInputs(FluidStack.class);
		List<ItemStack> outputs = ingredients.getOutputs(ItemStack.class);

		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();

		guiItemStacks.init(0, true, 21, 14);
		guiItemStacks.init(1, true, 45, 14);
		guiItemStacks.init(2, false, 105, 14);

		guiFluidStacks.init(0, true, 141, 1, 16, 60, 2000, false, tankOverlay);

		guiItemStacks.set(0, inputs.get(0));
		guiItemStacks.set(1, inputs.get(1));
		guiItemStacks.set(2, outputs.get(0));

		guiFluidStacks.set(0, inputFluids.get(0));

		if (outputs.size() > 1) {
			guiItemStacks.init(3, false, 105, 41);
			guiItemStacks.set(3, outputs.get(1));

			guiItemStacks.addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {

				if (slotIndex == 3) {
					tooltip.add(StringHelper.localize("info.cofh.chance") + ": " + recipeWrapper.chance + "%");
				}
			});
		}
	}

}
