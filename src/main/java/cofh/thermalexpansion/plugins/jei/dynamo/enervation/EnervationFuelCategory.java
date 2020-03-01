package cofh.thermalexpansion.plugins.jei.dynamo.enervation;

import cofh.core.inventory.ComparableItemStack;
import cofh.core.inventory.ComparableItemStackNBT;
import cofh.core.util.helpers.StringHelper;
import cofh.redstoneflux.api.IEnergyContainerItem;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.dynamo.BlockDynamo;
import cofh.thermalexpansion.gui.client.dynamo.GuiDynamoEnervation;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.plugins.jei.dynamo.BaseFuelCategory;
import cofh.thermalexpansion.util.managers.dynamo.EnervationManager;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class EnervationFuelCategory extends BaseFuelCategory<EnervationFuelWrapper> {

	public static boolean enable = true;

	public static void register(IRecipeCategoryRegistration registry) {

		String category = "Plugins.JEI";
		enable = ThermalExpansion.CONFIG_CLIENT.get(category, "Dynamo.Enervation", enable);

		if (!enable) {
			return;
		}
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipeCategories(new EnervationFuelCategory(guiHelper));
	}

	public static void initialize(IModRegistry registry) {

		if (!enable) {
			return;
		}
		try {
			IJeiHelpers jeiHelpers = registry.getJeiHelpers();
			IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

			registry.addRecipes(getRecipes(guiHelper), RecipeUidsTE.DYNAMO_ENERVATION);
			registry.addRecipeClickArea(GuiDynamoEnervation.class, 115, 35, 16, 16, RecipeUidsTE.DYNAMO_ENERVATION);
			registry.addRecipeCatalyst(BlockDynamo.dynamoEnervation, RecipeUidsTE.DYNAMO_ENERVATION);
		} catch (Throwable t) {
			ThermalExpansion.LOG.error("Bad/null fuel!", t);
		}
	}

	public static List<EnervationFuelWrapper> getRecipes(IGuiHelper guiHelper) {

		List<EnervationFuelWrapper> recipes = new ArrayList<>();

		for (ComparableItemStack fuel : EnervationManager.getFuels()) {
			ItemStack fuelStack = fuel.toItemStack();
			recipes.add(new EnervationFuelWrapper(guiHelper, fuelStack, EnervationManager.getFuelEnergy(fuelStack)));
		}
		for (Item item : Item.REGISTRY) {
			if (item instanceof IEnergyContainerItem) {
				try {
					HashSet<ComparableItemStack> processedStacks = new HashSet<>();
					NonNullList<ItemStack> list = NonNullList.create();
					item.getSubItems(item.getCreativeTab(), list);

					for (ItemStack fuel : list) {
						IEnergyContainerItem energyContainerItem = (IEnergyContainerItem) item;
						int maxEnergyStored = energyContainerItem.getMaxEnergyStored(fuel);

						if (maxEnergyStored != 0) {
							energyContainerItem.receiveEnergy(fuel, Integer.MAX_VALUE, false);

							if (!processedStacks.add(new ComparableItemStackNBT(fuel))) {
								continue;
							}
							int energy = EnervationManager.getFuelEnergy(fuel);

							if (energy > 0) {
								recipes.add(new EnervationFuelWrapper(guiHelper, fuel, energy, maxEnergyStored));
							}
						}
					}
				} catch (Exception e) {
					ThermalExpansion.LOG.error("Exception thrown while getting Enervation Dynamo fuels.", e);
				}
			}
		}
		return recipes;
	}

	public EnervationFuelCategory(IGuiHelper guiHelper) {

		background = guiHelper.createDrawable(GuiDynamoEnervation.TEXTURE, 26, 11, 70, 62, 0, 0, 16, 78);
		energyMeter = Drawables.getDrawables(guiHelper).getEnergyEmpty();
		durationEmpty = Drawables.getDrawables(guiHelper).getScale(Drawables.SCALE_FLUX);
		localizedName = StringHelper.localize("tile.thermalexpansion.dynamo.enervation.name");
	}

	@Nonnull
	@Override
	public String getUid() {

		return RecipeUidsTE.DYNAMO_ENERVATION;
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, EnervationFuelWrapper recipeWrapper, IIngredients ingredients) {

		List<List<ItemStack>> inputs = ingredients.getInputs(ItemStack.class);

		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

		guiItemStacks.init(0, true, 33, 23);

		guiItemStacks.set(0, inputs.get(0));
	}

}
