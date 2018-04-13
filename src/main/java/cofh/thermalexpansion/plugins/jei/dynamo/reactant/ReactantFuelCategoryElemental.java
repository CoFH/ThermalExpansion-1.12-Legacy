package cofh.thermalexpansion.plugins.jei.dynamo.reactant;

import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.block.dynamo.BlockDynamo;
import cofh.thermalexpansion.item.ItemAugment;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.util.managers.dynamo.ReactantManager;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ReactantFuelCategoryElemental extends ReactantFuelCategory {

	public static void initialize(IModRegistry registry) {

		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipes(getRecipes(guiHelper), RecipeUidsTE.DYNAMO_REACTANT_ELEMENTAL);
		registry.addRecipeCatalyst(ItemAugment.dynamoReactantElemental, RecipeUidsTE.DYNAMO_REACTANT_ELEMENTAL);
		registry.addRecipeCatalyst(BlockDynamo.dynamoReactant, RecipeUidsTE.DYNAMO_REACTANT_ELEMENTAL);
	}

	public static List<ReactantFuelWrapper> getRecipes(IGuiHelper guiHelper) {

		List<ReactantFuelWrapper> recipes = new ArrayList<>();

		for (ReactantManager.Reaction reaction : ReactantManager.getReactionList()) {
			if (ReactantManager.reactionExistsElemental(reaction.getReactant(), reaction.getFluid())) {
				recipes.add(new ReactantFuelWrapper(guiHelper, reaction, RecipeUidsTE.DYNAMO_REACTANT_ELEMENTAL));
			}
		}
		return recipes;
	}

	public ReactantFuelCategoryElemental(IGuiHelper guiHelper) {

		super(guiHelper);

		localizedName = StringHelper.localize("item.thermalexpansion.augment.dynamoReactantElemental.name");
	}

	@Nonnull
	@Override
	public String getUid() {

		return RecipeUidsTE.DYNAMO_REACTANT_ELEMENTAL;
	}

}
