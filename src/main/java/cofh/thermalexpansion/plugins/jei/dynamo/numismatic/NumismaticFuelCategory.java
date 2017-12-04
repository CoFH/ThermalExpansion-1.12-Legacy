package cofh.thermalexpansion.plugins.jei.dynamo.numismatic;

import cofh.core.inventory.ComparableItemStack;
import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.block.dynamo.BlockDynamo;
import cofh.thermalexpansion.gui.client.dynamo.GuiDynamoNumismatic;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.plugins.jei.dynamo.BaseFuelCategory;
import cofh.thermalexpansion.util.managers.dynamo.NumismaticManager;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class NumismaticFuelCategory extends BaseFuelCategory<NumismaticFuelWrapper> {

	public static boolean enable = true;

	public static void register(IRecipeCategoryRegistration registry) {

		if (!enable) {
			return;
		}
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipeCategories(new NumismaticFuelCategory(guiHelper));
		registry.addRecipeCategories(new NumismaticFuelCategoryGem(guiHelper));
	}

	public static void initialize(IModRegistry registry) {

		if (!enable) {
			return;
		}
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipes(getRecipes(guiHelper), RecipeUidsTE.DYNAMO_NUMISMATIC);
		registry.addRecipeClickArea(GuiDynamoNumismatic.class, 115, 35, 16, 16, RecipeUidsTE.DYNAMO_NUMISMATIC, RecipeUidsTE.DYNAMO_NUMISMATIC_GEM);
		registry.addRecipeCatalyst(BlockDynamo.dynamoNumismatic, RecipeUidsTE.DYNAMO_NUMISMATIC);

		NumismaticFuelCategoryGem.initialize(registry);
	}

	public static List<NumismaticFuelWrapper> getRecipes(IGuiHelper guiHelper) {

		List<NumismaticFuelWrapper> recipes = new ArrayList<>();

		for (ComparableItemStack fuel : NumismaticManager.getFuels()) {
			ItemStack fuelStack = fuel.toItemStack();
			recipes.add(new NumismaticFuelWrapper(guiHelper, fuelStack, NumismaticManager.getFuelEnergy(fuelStack)));
		}
		return recipes;
	}

	public NumismaticFuelCategory(IGuiHelper guiHelper) {

		background = guiHelper.createDrawable(GuiDynamoNumismatic.TEXTURE, 26, 11, 70, 62, 0, 0, 16, 78);
		energyMeter = Drawables.getDrawables(guiHelper).getEnergyEmpty();
		durationEmpty = Drawables.getDrawables(guiHelper).getScale(Drawables.SCALE_ALCHEMY);
		localizedName = StringHelper.localize("tile.thermalexpansion.dynamo.numismatic.name");
	}

	@Nonnull
	@Override
	public String getUid() {

		return RecipeUidsTE.DYNAMO_NUMISMATIC;
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, NumismaticFuelWrapper recipeWrapper, IIngredients ingredients) {

		List<List<ItemStack>> inputs = ingredients.getInputs(ItemStack.class);

		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

		guiItemStacks.init(0, true, 33, 23);

		guiItemStacks.set(0, inputs.get(0));
	}

}
