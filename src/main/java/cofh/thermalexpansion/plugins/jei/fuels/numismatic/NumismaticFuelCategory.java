package cofh.thermalexpansion.plugins.jei.fuels.numismatic;

import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.block.dynamo.BlockDynamo;
import cofh.thermalexpansion.gui.client.dynamo.GuiDynamoNumismatic;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.util.fuels.NumismaticManager;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class NumismaticFuelCategory extends BlankRecipeCategory<NumismaticFuelWrapper> {

	public static boolean enable = true;

	public static void initialize(IModRegistry registry) {

		if (!enable) {
			return;
		}
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipeCategories(new NumismaticFuelCategory(guiHelper));
		registry.addRecipeHandlers(new NumismaticFuelHandler());
		registry.addRecipes(getRecipes(guiHelper));
		registry.addRecipeClickArea(GuiDynamoNumismatic.class, 115, 35, 16, 16, RecipeUidsTE.DYNAMO_NUMISMATIC);
		registry.addRecipeCategoryCraftingItem(BlockDynamo.dynamoNumismatic, RecipeUidsTE.DYNAMO_NUMISMATIC);
	}

	public static List<NumismaticFuelWrapper> getRecipes(IGuiHelper guiHelper) {

		List<NumismaticFuelWrapper> recipes = new ArrayList<>();

		for (ItemStack fuel : NumismaticManager.getFuels()) {
			recipes.add(new NumismaticFuelWrapper(guiHelper, fuel, NumismaticManager.getFuelEnergy(fuel)));
		}
		return recipes;
	}

	IDrawableStatic background;
	IDrawableStatic energyMeter;
	IDrawableStatic burnEmpty;
	String localizedName;

	public NumismaticFuelCategory(IGuiHelper guiHelper) {

		background = guiHelper.createDrawable(GuiDynamoNumismatic.TEXTURE, 26, 11, 70, 62, 0, 0, 16, 78);
		energyMeter = Drawables.getDrawables(guiHelper).getEnergyEmpty();
		burnEmpty = Drawables.getDrawables(guiHelper).getSpeed(8);
		localizedName = StringHelper.localize("tile.thermalexpansion.dynamo.numismatic.name");
	}

	@Nonnull
	@Override
	public String getUid() {

		return RecipeUidsTE.DYNAMO_NUMISMATIC;
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

		energyMeter.draw(minecraft, 71, 7);
		burnEmpty.draw(minecraft, 34, 43);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, NumismaticFuelWrapper recipeWrapper, IIngredients ingredients) {

		List<List<ItemStack>> inputs = ingredients.getInputs(ItemStack.class);
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

		guiItemStacks.init(0, true, 33, 23);

		guiItemStacks.set(0, inputs.get(0));
	}

}
