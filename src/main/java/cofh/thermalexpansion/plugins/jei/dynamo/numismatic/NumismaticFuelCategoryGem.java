package cofh.thermalexpansion.plugins.jei.dynamo.numismatic;

import cofh.core.inventory.ComparableItemStack;
import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.block.dynamo.BlockDynamo;
import cofh.thermalexpansion.item.ItemAugment;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.util.managers.dynamo.NumismaticManager;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class NumismaticFuelCategoryGem extends NumismaticFuelCategory {

	public static void initialize(IModRegistry registry) {

		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipes(getRecipes(guiHelper), RecipeUidsTE.DYNAMO_NUMISMATIC_GEM);
		registry.addRecipeCatalyst(ItemAugment.dynamoNumismaticGem, RecipeUidsTE.DYNAMO_NUMISMATIC_GEM);
		registry.addRecipeCatalyst(BlockDynamo.dynamoNumismatic, RecipeUidsTE.DYNAMO_NUMISMATIC_GEM);
	}

	public static List<NumismaticFuelWrapper> getRecipes(IGuiHelper guiHelper) {

		List<NumismaticFuelWrapper> recipes = new ArrayList<>();

		for (ComparableItemStack fuel : NumismaticManager.getGemFuels()) {
			ItemStack fuelStack = fuel.toItemStack();
			recipes.add(new NumismaticFuelWrapper(guiHelper, fuelStack, NumismaticManager.getGemFuelEnergy(fuelStack), RecipeUidsTE.DYNAMO_NUMISMATIC_GEM));
		}
		return recipes;
	}

	public NumismaticFuelCategoryGem(IGuiHelper guiHelper) {

		super(guiHelper);

		localizedName = StringHelper.localize("item.thermalexpansion.augment.dynamoNumismaticGem.name");
	}

	@Nonnull
	@Override
	public String getUid() {

		return RecipeUidsTE.DYNAMO_NUMISMATIC_GEM;
	}

}
