package cofh.thermalexpansion.plugins.jei.machine.transposer;

import cofh.core.util.helpers.FluidHelper;
import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.gui.client.machine.GuiTransposer;
import cofh.thermalexpansion.item.ItemFlorb;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.util.managers.machine.TransposerManager;
import cofh.thermalexpansion.util.managers.machine.TransposerManager.TransposerRecipe;
import cofh.thermalfoundation.init.TFFluids;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TransposerRecipeCategoryFill extends TransposerRecipeCategory {

	public static void initialize(IModRegistry registry) {

		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipes(getRecipes(guiHelper, registry.getIngredientRegistry()), RecipeUidsTE.TRANSPOSER_FILL);
		registry.addRecipeCatalyst(BlockMachine.machineTransposer, RecipeUidsTE.TRANSPOSER_FILL);
	}

	public static List<TransposerRecipeWrapper> getRecipes(IGuiHelper guiHelper, IIngredientRegistry ingredientRegistry) {

		List<TransposerRecipeWrapper> recipes = new ArrayList<>();

		for (TransposerRecipe recipe : TransposerManager.getFillRecipeList()) {
			if (TFFluids.isPotion(recipe.getFluid()) || TFFluids.isSplashPotion(recipe.getFluid()) || TFFluids.isLingeringPotion(recipe.getFluid())) {
				// Ignore Potions
				continue;
			}
			if (ItemFlorb.florbStandard.equals(recipe.getInput()) || ItemFlorb.florbMagmatic.equals(recipe.getInput())) {
				// Ignore Florbs
				continue;
			}
			recipes.add(new TransposerRecipeWrapper(guiHelper, recipe, RecipeUidsTE.TRANSPOSER_FILL));
		}
		List<ItemStack> ingredients = ingredientRegistry.getIngredients(ItemStack.class);
		for (ItemStack ingredient : ingredients) {
			if (ingredient.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
				TransposerRecipeWrapperContainer wrapper = new TransposerRecipeWrapperContainer(guiHelper, ingredient, RecipeUidsTE.TRANSPOSER_FILL);
				if (wrapper.inputs.get(0).isEmpty() || wrapper.outputs.get(0).isEmpty() || wrapper.inputFluids.get(0).isEmpty()) {
					continue;
				}
				recipes.add(wrapper);
			}
		}
		return recipes;
	}

	public TransposerRecipeCategoryFill(IGuiHelper guiHelper) {

		super(guiHelper);

		localizedName += " - " + StringHelper.localize("gui.thermalexpansion.jei.transposer.modeFill");
		icon = guiHelper.createDrawable(GuiTransposer.TEXTURE, 176, 48, 16, 16);
	}

	@Nonnull
	@Override
	public String getUid() {

		return RecipeUidsTE.TRANSPOSER_FILL;
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, TransposerRecipeWrapper recipeWrapper, IIngredients ingredients) {

		List<List<ItemStack>> inputs = ingredients.getInputs(ItemStack.class);
		List<List<ItemStack>> outputs = ingredients.getOutputs(ItemStack.class);
		List<List<FluidStack>> fluids = ingredients.getInputs(FluidStack.class);

		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();

		Map<Integer, ? extends IGuiIngredient<FluidStack>> fluidIngredients = guiFluidStacks.getGuiIngredients();
		recipeWrapper.setGuiFluids(fluidIngredients);

		guiItemStacks.init(0, true, 30, 10);
		guiItemStacks.init(1, false, 30, 41);
		guiFluidStacks.init(0, true, 103, 1, 16, 60, Fluid.BUCKET_VOLUME, false, tankOverlay);

		guiItemStacks.set(0, inputs.get(0));
		guiItemStacks.set(1, outputs.get(0));
		guiFluidStacks.set(0, fluids.get(0));

		guiFluidStacks.addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {

			if (FluidHelper.isPotionFluid(ingredient)) {
				FluidHelper.addPotionTooltip(ingredient, tooltip);
			}
		});
	}

}
