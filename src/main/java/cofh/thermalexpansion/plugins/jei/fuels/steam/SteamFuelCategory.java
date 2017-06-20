package cofh.thermalexpansion.plugins.jei.fuels.steam;

import cofh.lib.inventory.ComparableItemStack;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.block.dynamo.BlockDynamo;
import cofh.thermalexpansion.gui.client.dynamo.GuiDynamoSteam;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.plugins.jei.fuels.BaseFuelCategory;
import cofh.thermalexpansion.util.managers.dynamo.SteamManager;
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
import java.util.Set;

public class SteamFuelCategory extends BaseFuelCategory<SteamFuelWrapper> {

	public static boolean enable = true;

	public static void register(IRecipeCategoryRegistration registry) {

		if (!enable) {
			return;
		}
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipeCategories(new SteamFuelCategory(guiHelper));
	}

	public static void initialize(IModRegistry registry) {

		if (!enable) {
			return;
		}
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipes(getRecipes(registry, guiHelper), RecipeUidsTE.DYNAMO_STEAM);
		registry.addRecipeClickArea(GuiDynamoSteam.class, 115, 35, 16, 16, RecipeUidsTE.DYNAMO_STEAM);
		registry.addRecipeCatalyst(BlockDynamo.dynamoSteam, RecipeUidsTE.DYNAMO_STEAM);
	}

	public static List<SteamFuelWrapper> getRecipes(IModRegistry registry, IGuiHelper guiHelper) {

		List<SteamFuelWrapper> recipes = new ArrayList<>();

		for (ComparableItemStack fuel : SteamManager.getFuels()) {
			ItemStack fuelStack = fuel.toItemStack();
			recipes.add(new SteamFuelWrapper(guiHelper, fuelStack, SteamManager.getFuelEnergy(fuelStack)));
		}
		Set specificFuels = SteamManager.getFuels();

		for (ItemStack fuel : registry.getIngredientRegistry().getFuels()) {
			if (specificFuels.contains(new ComparableItemStack(fuel))) {
				continue;
			}
			int energy = SteamManager.getFuelEnergy(fuel);
			if (energy > 0) {
				recipes.add(new SteamFuelWrapper(guiHelper, fuel, energy));
			}
		}
		return recipes;
	}

	public SteamFuelCategory(IGuiHelper guiHelper) {

		background = guiHelper.createDrawable(GuiDynamoSteam.TEXTURE, 26, 11, 70, 62, 0, 0, 16, 78);
		energyMeter = Drawables.getDrawables(guiHelper).getEnergyEmpty();
		durationEmpty = Drawables.getDrawables(guiHelper).getScale(Drawables.SCALE_FLAME);
		localizedName = StringHelper.localize("tile.thermalexpansion.dynamo.steam.name");
	}

	@Nonnull
	@Override
	public String getUid() {

		return RecipeUidsTE.DYNAMO_STEAM;
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, SteamFuelWrapper recipeWrapper, IIngredients ingredients) {

		List<List<ItemStack>> inputs = ingredients.getInputs(ItemStack.class);

		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

		guiItemStacks.init(0, true, 33, 23);

		guiItemStacks.set(0, inputs.get(0));
	}

}
