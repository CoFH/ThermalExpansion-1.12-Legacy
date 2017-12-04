package cofh.thermalexpansion.plugins.jei.machine.transposer;

import cofh.core.util.helpers.FluidHelper;
import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.gui.client.machine.GuiTransposer;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.util.managers.machine.TransposerManager;
import cofh.thermalexpansion.util.managers.machine.TransposerManager.TransposerRecipe;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class TransposerRecipeCategoryExtract extends TransposerRecipeCategory {

	public static void initialize(IModRegistry registry) {

		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipes(getRecipes(guiHelper, registry.getIngredientRegistry()), RecipeUidsTE.TRANSPOSER_EXTRACT);
		registry.addRecipeCatalyst(BlockMachine.machineTransposer, RecipeUidsTE.TRANSPOSER_EXTRACT);
	}

	public static List<TransposerRecipeWrapper> getRecipes(IGuiHelper guiHelper, IIngredientRegistry ingredientRegistry) {

		List<TransposerRecipeWrapper> recipes = new ArrayList<>();

		for (TransposerRecipe recipe : TransposerManager.getExtractRecipeList()) {
			recipes.add(new TransposerRecipeWrapper(guiHelper, recipe, RecipeUidsTE.TRANSPOSER_EXTRACT));
		}
		List<ItemStack> ingredients = ingredientRegistry.getIngredients(ItemStack.class);

		for (ItemStack ingredient : ingredients) {
			if (ingredient.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
				for (Fluid fluid : FluidRegistry.getRegisteredFluids().values()) {
					addExtractRecipe(ingredient, fluid, recipes, guiHelper);
				}
				//				TransposerRecipeWrapperContainer wrapper = new TransposerRecipeWrapperContainer(guiHelper, ingredient, RecipeUidsTE.TRANSPOSER_EXTRACT);
				//				if (!wrapper.inputs.isEmpty() && !wrapper.outputFluids.isEmpty()) {
				//					recipes.add(wrapper);
				//				}
			}
		}
		return recipes;
	}

	private static void addExtractRecipe(ItemStack baseStack, Fluid fluid, List<TransposerRecipeWrapper> recipes, IGuiHelper guiHelper) {

		ItemStack filledStack = baseStack.copy();
		IFluidHandlerItem handler = filledStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
		int fill = handler.fill(new FluidStack(fluid, Fluid.BUCKET_VOLUME), true);

		if (fill > 0) {
			filledStack = handler.getContainer().copy();
			FluidStack drainedFluid = handler.drain(Fluid.BUCKET_VOLUME, true);

			if (drainedFluid != null) {
				ItemStack drainedStack = handler.getContainer();
				TransposerRecipe recipe = new TransposerRecipe(filledStack, drainedStack, drainedFluid, TransposerManager.DEFAULT_ENERGY, drainedStack.getCount() <= 0 ? 0 : 100);
				recipes.add(new TransposerRecipeWrapper(guiHelper, recipe, RecipeUidsTE.TRANSPOSER_EXTRACT));
			}
		}
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

		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();

		guiItemStacks.init(0, true, 30, 10);
		guiItemStacks.init(1, false, 30, 41);
		guiFluidStacks.init(0, false, 103, 1, 16, 60, TEProps.MAX_FLUID_LARGE, false, tankOverlay);

		guiItemStacks.set(0, inputs.get(0));
		guiItemStacks.set(1, outputs.isEmpty() ? null : outputs.get(0));
		guiFluidStacks.set(0, fluids.get(0));

		guiItemStacks.addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {

			if (slotIndex == 1 && recipeWrapper.chance < 100) {
				tooltip.add(StringHelper.localize("info.cofh.chance") + ": " + recipeWrapper.chance + "%");
			}
		});

		guiFluidStacks.addTooltipCallback((i, b, fluidStack, list) -> {

			if (FluidHelper.isPotionFluid(fluidStack)) {
				FluidHelper.addPotionTooltip(fluidStack, list);
			}
		});
	}

}
