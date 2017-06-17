package cofh.thermalexpansion.plugins.jei.fuels.reactant;

import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.block.dynamo.BlockDynamo;
import cofh.thermalexpansion.gui.client.dynamo.GuiDynamoReactant;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.plugins.jei.crafting.charger.ChargerRecipeCategory;
import cofh.thermalexpansion.plugins.jei.fuels.BaseFuelCategory;
import cofh.thermalexpansion.util.managers.dynamo.ReactantManager;
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

public class ReactantFuelCategory extends BaseFuelCategory<ReactantFuelWrapper> {

	public static boolean enable = true;

	public static void register(IRecipeCategoryRegistration registry) {

		if (!enable) {
			return;
		}
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipeCategories(new ReactantFuelCategory(guiHelper));
	}

	public static void initialize(IModRegistry registry) {

		if (!enable) {
			return;
		}
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipes(getRecipes(guiHelper), RecipeUidsTE.DYNAMO_REACTANT);
		registry.addRecipeClickArea(GuiDynamoReactant.class, 115, 35, 16, 16, RecipeUidsTE.DYNAMO_REACTANT);
		registry.addRecipeCatalyst(BlockDynamo.dynamoReactant, RecipeUidsTE.DYNAMO_REACTANT);
	}

	public static List<ReactantFuelWrapper> getRecipes(IGuiHelper guiHelper) {

		List<ReactantFuelWrapper> recipes = new ArrayList<>();

		for (ReactantManager.Reaction reaction : ReactantManager.getReactionList()) {
			recipes.add(new ReactantFuelWrapper(guiHelper, reaction));
		}
		return recipes;
	}

	IDrawableStatic tank;
	IDrawableStatic tankOverlayInput;

	public ReactantFuelCategory(IGuiHelper guiHelper) {

		background = guiHelper.createDrawable(GuiDynamoReactant.TEXTURE, 26, 11, 70, 62, 0, 0, 16, 78);
		energyMeter = Drawables.getDrawables(guiHelper).getEnergyEmpty();
		durationEmpty = Drawables.getDrawables(guiHelper).getScale(Drawables.SCALE_FLAME_GREEN);
		localizedName = StringHelper.localize("tile.thermalexpansion.dynamo.reactant.name");

		tank = Drawables.getDrawables(guiHelper).getTank(Drawables.TANK_SHORT);
		tankOverlayInput = Drawables.getDrawables(guiHelper).getTankSmallOverlay(Drawables.TANK_SHORT);
	}

	@Nonnull
	@Override
	public String getUid() {

		return RecipeUidsTE.DYNAMO_REACTANT;
	}

	@Override
	public void drawExtras(@Nonnull Minecraft minecraft) {

		energyMeter.draw(minecraft, 71, 7);
		durationEmpty.draw(minecraft, 22, 43);

		tank.draw(minecraft, 9, 9);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, ReactantFuelWrapper recipeWrapper, IIngredients ingredients) {

		List<List<ItemStack>> inputs = ingredients.getInputs(ItemStack.class);
		List<List<FluidStack>> inputFluids = ingredients.getInputs(FluidStack.class);

		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();

		guiItemStacks.init(0, true, 33, 23);
		guiFluidStacks.init(0, true, 10, 10, 16, 30, 1000, false, tankOverlayInput);

		guiItemStacks.set(0, inputs.get(0));
		guiFluidStacks.set(0, inputFluids.get(0));
	}

}
