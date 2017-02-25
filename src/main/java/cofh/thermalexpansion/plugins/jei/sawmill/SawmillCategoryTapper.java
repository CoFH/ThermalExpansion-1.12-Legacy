package cofh.thermalexpansion.plugins.jei.sawmill;

import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.item.ItemAugment;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.util.crafting.SawmillManager;
import cofh.thermalexpansion.util.crafting.TapperManager;
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

public class SawmillCategoryTapper extends SawmillRecipeCategory {

	public static void initialize(IModRegistry registry) {

		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipeCategories(new SawmillCategoryTapper(guiHelper));
		registry.addRecipes(getRecipes(guiHelper));
		registry.addRecipeCategoryCraftingItem(ItemAugment.machineSawmillTapper, RecipeUidsTE.SAWMILL_TAPPER);
		registry.addRecipeCategoryCraftingItem(BlockMachine.machineSawmill, RecipeUidsTE.SAWMILL_TAPPER);
	}

	public static List<SawmillRecipeWrapper> getRecipes(IGuiHelper guiHelper) {

		List<SawmillRecipeWrapper> recipes = new ArrayList<>();

		for (SawmillManager.RecipeSawmill recipe : SawmillManager.getRecipeList()) {
			if (TapperManager.mappingExists(recipe.getInput())) {
				recipes.add(new SawmillRecipeWrapper(guiHelper, recipe, RecipeUidsTE.SAWMILL_TAPPER));
			}
		}
		return recipes;
	}

	IDrawableStatic tank;
	IDrawableStatic tankOverlay;

	public SawmillCategoryTapper(IGuiHelper guiHelper) {

		super(guiHelper);

		tank = Drawables.getDrawables(guiHelper).getTank(0);
		tankOverlay = Drawables.getDrawables(guiHelper).getTankSmallOverlay(0);

		localizedName = StringHelper.localize("item.thermalexpansion.augment.machineSawmillTapper.name");
	}

	@Nonnull
	@Override
	public String getUid() {

		return RecipeUidsTE.SAWMILL_TAPPER;
	}

	@Override
	public void drawExtras(@Nonnull Minecraft minecraft) {

		super.drawExtras(minecraft);

		tank.draw(minecraft, 140, 0);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, SawmillRecipeWrapper recipeWrapper, IIngredients ingredients) {

		super.setRecipe(recipeLayout, recipeWrapper, ingredients);

		List<FluidStack> outputFluids = ingredients.getOutputs(FluidStack.class);
		IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();

		guiFluidStacks.init(0, true, 141, 1, 16, 60, 100, false, tankOverlay);
		guiFluidStacks.set(0, outputFluids.get(0));
	}

}
