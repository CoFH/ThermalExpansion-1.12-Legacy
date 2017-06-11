package cofh.thermalexpansion.plugins.jei.crafting.pulverizer;

import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.item.ItemAugment;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.util.managers.machine.PulverizerManager;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class PulverizerRecipeCategoryPetrotheum extends PulverizerRecipeCategory {

	public static void initialize(IModRegistry registry) {

		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipeCategories(new PulverizerRecipeCategoryPetrotheum(guiHelper));
		registry.addRecipes(getRecipes(guiHelper));
		registry.addRecipeCategoryCraftingItem(ItemAugment.machinePulverizerPetrotheum, RecipeUidsTE.PULVERIZER_PETROTHEUM);
		registry.addRecipeCategoryCraftingItem(BlockMachine.machinePulverizer, RecipeUidsTE.PULVERIZER_PETROTHEUM);
	}

	public static List<PulverizerRecipeWrapper> getRecipes(IGuiHelper guiHelper) {

		List<PulverizerRecipeWrapper> recipes = new ArrayList<>();

		for (PulverizerManager.RecipePulverizer recipe : PulverizerManager.getRecipeList()) {
			if (ItemHelper.isOre(recipe.getInput())) {
				recipes.add(new PulverizerRecipeWrapper(guiHelper, recipe, RecipeUidsTE.PULVERIZER_PETROTHEUM));
			}
		}
		return recipes;
	}

	IDrawableStatic tank;
	IDrawableStatic tankOverlay;

	public PulverizerRecipeCategoryPetrotheum(IGuiHelper guiHelper) {

		super(guiHelper);

		tank = Drawables.getDrawables(guiHelper).getTank(Drawables.TANK);
		tankOverlay = Drawables.getDrawables(guiHelper).getTankSmallOverlay(Drawables.TANK);

		localizedName = StringHelper.localize("item.thermalexpansion.augment.machinePulverizerPetrotheum.name");
	}

	@Nonnull
	@Override
	public String getUid() {

		return RecipeUidsTE.PULVERIZER_PETROTHEUM;
	}

	@Override
	public void drawExtras(@Nonnull Minecraft minecraft) {

		super.drawExtras(minecraft);

		tank.draw(minecraft, 140, 0);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, PulverizerRecipeWrapper recipeWrapper, IIngredients ingredients) {

		super.setRecipe(recipeLayout, recipeWrapper, ingredients);

		List<List<FluidStack>> inputFluids = ingredients.getInputs(FluidStack.class);
		IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();

		guiFluidStacks.init(0, true, 141, 1, 16, 60, 1000, false, tankOverlay);
		guiFluidStacks.set(0, inputFluids.get(0));
	}

}
