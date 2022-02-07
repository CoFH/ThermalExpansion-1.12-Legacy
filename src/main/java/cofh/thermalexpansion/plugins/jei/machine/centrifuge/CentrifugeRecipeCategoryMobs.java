package cofh.thermalexpansion.plugins.jei.machine.centrifuge;

import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.item.ItemAugment;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.util.managers.machine.CentrifugeManager;
import cofh.thermalexpansion.util.managers.machine.CentrifugeManager.CentrifugeRecipe;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class CentrifugeRecipeCategoryMobs extends CentrifugeRecipeCategory {

	public static void initialize(IModRegistry registry) {

		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipes(getRecipes(guiHelper), RecipeUidsTE.CENTRIFUGE_MOBS);
		registry.addRecipeCatalyst(ItemAugment.machineCentrifugeMobs, RecipeUidsTE.CENTRIFUGE_MOBS);
		registry.addRecipeCatalyst(BlockMachine.machineCentrifuge, RecipeUidsTE.CENTRIFUGE_MOBS);
	}

	public static List<CentrifugeRecipeWrapper> getRecipes(IGuiHelper guiHelper) {

		List<CentrifugeRecipeWrapper> recipes = new ArrayList<>();

		for (CentrifugeRecipe recipe : CentrifugeManager.getRecipeListMobs()) {
			recipes.add(new CentrifugeRecipeWrapper(guiHelper, recipe, RecipeUidsTE.CENTRIFUGE_MOBS));
		}
		return recipes;
	}

	public CentrifugeRecipeCategoryMobs(IGuiHelper guiHelper) {

		super(guiHelper);

		localizedName = StringHelper.localize("item.thermalexpansion.augment.machineCentrifugeMobs.name");
	}

	@Nonnull
	@Override
	public String getUid() {

		return RecipeUidsTE.CENTRIFUGE_MOBS;
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, CentrifugeRecipeWrapper recipeWrapper, IIngredients ingredients) {

		List<List<ItemStack>> inputs = ingredients.getInputs(ItemStack.class);
		List<List<ItemStack>> outputs = ingredients.getOutputs(ItemStack.class);
		List<List<FluidStack>> outputFluids = ingredients.getOutputs(FluidStack.class);

		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();

		guiItemStacks.init(0, true, 33, 14);
		guiItemStacks.init(1, false, 96, 14);
		guiItemStacks.init(2, false, 114, 14);
		guiItemStacks.init(3, false, 96, 32);
		guiItemStacks.init(4, false, 114, 32);

		guiFluidStacks.init(0, false, 141, 1, 16, 60, TEProps.MAX_FLUID_SMALL, false, tankOverlay);

		guiItemStacks.set(0, inputs.get(0));

		for (int i = 0; i < outputs.size(); i++) {
			guiItemStacks.set(i + 1, outputs.get(i));
		}
		guiFluidStacks.set(0, outputFluids.get(0));

		guiItemStacks.addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {

			if (!recipeWrapper.chance.isEmpty() && slotIndex >= 1 && slotIndex <= 4) {
				if (recipeWrapper.chance.get(slotIndex - 1) < 100) {
					if (recipeWrapper.outputs.get(slotIndex - 1).getCount() > 1) {
						tooltip.add(StringHelper.localize("gui.thermalexpansion.jei.centrifuge.mobNotice"));
					}
					tooltip.add(StringHelper.localize("gui.thermalexpansion.jei.centrifuge.mobChance") + StringHelper.localize("info.thermalexpansion.semicolon")ngHelper.localize("info.thermalexpansion.semicolon")ngHelper.localize("info.thermalexpansion.semicolon") + recipeWrapper.chance.get(slotIndex - 1) + "%");
				}
			}
		});
	}

}
