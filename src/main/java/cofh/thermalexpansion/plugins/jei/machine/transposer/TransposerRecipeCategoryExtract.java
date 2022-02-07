package cofh.thermalexpansion.plugins.jei.machine.transposer;

import cofh.core.util.helpers.FluidHelper;
import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.gui.client.machine.GuiTransposer;
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
import mezz.jei.api.recipe.IFocus;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static mezz.jei.api.recipe.IFocus.Mode.INPUT;
import static mezz.jei.api.recipe.IFocus.Mode.OUTPUT;

public class TransposerRecipeCategoryExtract extends TransposerRecipeCategory {

	public static void initialize(IModRegistry registry) {

		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipes(getRecipes(guiHelper, registry.getIngredientRegistry()), RecipeUidsTE.TRANSPOSER_EXTRACT);
		registry.addRecipeCatalyst(BlockMachine.machineTransposer, RecipeUidsTE.TRANSPOSER_EXTRACT);
	}

	public static List<TransposerRecipeWrapper> getRecipes(IGuiHelper guiHelper, IIngredientRegistry ingredientRegistry) {

		List<TransposerRecipeWrapper> recipes = new ArrayList<>();

		List<TransposerRecipe> potionRecipes = new ArrayList<>();
		List<TransposerRecipe> splashPotionRecipes = new ArrayList<>();
		List<TransposerRecipe> lingeringPotionRecipes = new ArrayList<>();

		for (TransposerRecipe recipe : TransposerManager.getExtractRecipeList()) {
			if (TFFluids.isPotion(recipe.getFluid())) {
				potionRecipes.add(recipe);
				continue;
			}
			if (TFFluids.isSplashPotion(recipe.getFluid())) {
				splashPotionRecipes.add(recipe);
				continue;
			}
			if (TFFluids.isLingeringPotion(recipe.getFluid())) {
				lingeringPotionRecipes.add(recipe);
				continue;
			}
			recipes.add(new TransposerRecipeWrapper(guiHelper, recipe, RecipeUidsTE.TRANSPOSER_EXTRACT));
		}
		recipes.add(new TransposerRecipeWrapperMulti(guiHelper, potionRecipes, RecipeUidsTE.TRANSPOSER_EXTRACT));
		recipes.add(new TransposerRecipeWrapperMulti(guiHelper, splashPotionRecipes, RecipeUidsTE.TRANSPOSER_EXTRACT));
		recipes.add(new TransposerRecipeWrapperMulti(guiHelper, lingeringPotionRecipes, RecipeUidsTE.TRANSPOSER_EXTRACT));

		List<ItemStack> ingredients = ingredientRegistry.getIngredients(ItemStack.class);
		for (ItemStack ingredient : ingredients) {
			if (ingredient.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
				TransposerRecipeWrapperContainer wrapper = new TransposerRecipeWrapperContainer(guiHelper, ingredient, RecipeUidsTE.TRANSPOSER_EXTRACT);
				if (wrapper.inputs.get(0).isEmpty() || wrapper.outputFluids.get(0).isEmpty()) {
					continue;
				}
				recipes.add(wrapper);
			}
		}
		return recipes;
	}

	public TransposerRecipeCategoryExtract(IGuiHelper guiHelper) {

		super(guiHelper);

		localizedName += " - " + StringHelper.localize("gui.thermalexpansion.jei.transposer.modeEmpty");
		icon = guiHelper.createDrawable(GuiTransposer.TEXTURE, 192, 48, 16, 16);
	}

	@Nonnull
	@Override
	public String getUid() {

		return RecipeUidsTE.TRANSPOSER_EXTRACT;
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, TransposerRecipeWrapper recipeWrapper, IIngredients ingredients) {

		List<List<ItemStack>> inputs = ingredients.getInputs(ItemStack.class);
		List<List<ItemStack>> outputs = ingredients.getOutputs(ItemStack.class);
		List<List<FluidStack>> fluids = ingredients.getOutputs(FluidStack.class);

		IFocus<?> focus = recipeLayout.getFocus();
		if (focus != null) {
			if (focus.getMode() == INPUT && focus.getValue() instanceof ItemStack) {
				List<FluidStack> focusFluids = new ArrayList<>();
				ItemStack input = (ItemStack) focus.getValue();
				FluidStack contained = FluidHelper.getFluidStackFromHandler(input);
				if (contained != null) {
					for (FluidStack fluid : fluids.get(0)) {
						if (FluidHelper.isFluidEqual(contained, fluid)) {
							focusFluids.add(fluid);
						}
					}
					if (focusFluids.size() != fluids.get(0).size()) {
						fluids = singletonList(focusFluids);
					}
				}
			} else if (focus.getMode() == OUTPUT && focus.getValue() instanceof FluidStack) {
				List<ItemStack> focusInputs = new ArrayList<>();
				FluidStack fluid = (FluidStack) focus.getValue();
				for (ItemStack stack : inputs.get(0)) {
					FluidStack contained = FluidHelper.getFluidStackFromHandler(stack);
					if (contained == null || FluidHelper.isFluidEqual(fluid, contained)) {
						focusInputs.add(stack);
					}
				}
				if (focusInputs.size() != inputs.get(0).size()) {
					inputs = singletonList(focusInputs);
				}
			}
		}

		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();

		Map<Integer, ? extends IGuiIngredient<FluidStack>> fluidIngredients = guiFluidStacks.getGuiIngredients();
		recipeWrapper.setGuiFluids(fluidIngredients);

		guiItemStacks.init(0, true, 30, 10);
		guiItemStacks.init(1, false, 30, 41);
		guiFluidStacks.init(0, false, 103, 1, 16, 60, Fluid.BUCKET_VOLUME, false, tankOverlay);

		guiItemStacks.set(0, inputs.get(0));
		guiItemStacks.set(1, outputs.isEmpty() ? null : outputs.get(0));
		guiFluidStacks.set(0, fluids.get(0));

		guiItemStacks.addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {

			if (slotIndex == 1 && recipeWrapper.chance < 100) {
				tooltip.add(StringHelper.localize("info.cofh.chance") + StringHelper.localize("info.thermalexpansion.semicolon") + recipeWrapper.chance + "%");
			}
		});

		guiFluidStacks.addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {

			if (FluidHelper.isPotionFluid(ingredient)) {
				FluidHelper.addPotionTooltip(ingredient, tooltip);
			}
		});
	}

}
