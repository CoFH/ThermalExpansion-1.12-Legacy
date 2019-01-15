package cofh.thermalexpansion.plugins.jei.machine.smelter;

import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.item.ItemAugment;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.util.managers.machine.SmelterManager;
import cofh.thermalexpansion.util.managers.machine.SmelterManager.SmelterRecipe;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class SmelterRecipeCategoryPyrotheum extends SmelterRecipeCategory {

	public static void initialize(IModRegistry registry) {

		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipes(getRecipes(guiHelper), RecipeUidsTE.SMELTER_PYROTHEUM);
		registry.addRecipeCatalyst(ItemAugment.machineSmelterPyrotheum, RecipeUidsTE.SMELTER_PYROTHEUM);
		registry.addRecipeCatalyst(BlockMachine.machineSmelter, RecipeUidsTE.SMELTER_PYROTHEUM);
	}

	public static List<SmelterRecipeWrapper> getRecipes(IGuiHelper guiHelper) {

		List<SmelterRecipeWrapper> recipes = new ArrayList<>();

		for (SmelterRecipe recipe : SmelterManager.getRecipeList()) {
			if (SmelterManager.isOre(recipe.getPrimaryInput())) {
				recipes.add(new SmelterRecipeWrapper(guiHelper, recipe, RecipeUidsTE.SMELTER_PYROTHEUM));
			}
		}
		return recipes;
	}

	IDrawableStatic tank;
	IDrawableStatic tankOverlay;

	public SmelterRecipeCategoryPyrotheum(IGuiHelper guiHelper) {

		super(guiHelper);

		tank = Drawables.getDrawables(guiHelper).getTank(Drawables.TANK);
		tankOverlay = Drawables.getDrawables(guiHelper).getTankSmallOverlay(Drawables.TANK);

		localizedName = StringHelper.localize("item.thermalexpansion.augment.machineSmelterPyrotheum.name");
	}

	@Nonnull
	@Override
	public String getUid() {

		return RecipeUidsTE.SMELTER_PYROTHEUM;
	}

	@Override
	public void drawExtras(@Nonnull Minecraft minecraft) {

		super.drawExtras(minecraft);

		tank.draw(minecraft, 140, 0);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, SmelterRecipeWrapper recipeWrapper, IIngredients ingredients) {

		super.setRecipe(recipeLayout, recipeWrapper, ingredients);

		List<List<FluidStack>> inputFluids = ingredients.getInputs(FluidStack.class);
		IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();

		guiFluidStacks.init(0, true, 141, 1, 16, 60, Fluid.BUCKET_VOLUME, false, null);
		guiFluidStacks.set(0, inputFluids.get(0));

		guiFluidStacks.addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {

			tooltip.add(StringHelper.LIGHT_BLUE + StringHelper.localize("info.cofh.input"));
		});
	}

}
