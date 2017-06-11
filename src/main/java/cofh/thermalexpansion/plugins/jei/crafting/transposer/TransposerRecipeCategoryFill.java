package cofh.thermalexpansion.plugins.jei.crafting.transposer;

import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.gui.client.machine.GuiTransposer;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.util.managers.machine.TransposerManager;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class TransposerRecipeCategoryFill extends TransposerRecipeCategory {

	public static void initialize(IModRegistry registry) {

		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipeCategories(new TransposerRecipeCategoryFill(guiHelper));
		registry.addRecipes(getRecipes(guiHelper, registry.getIngredientRegistry()));
		registry.addRecipeCategoryCraftingItem(BlockMachine.machineTransposer, RecipeUidsTE.TRANSPOSER_FILL);
	}

	public static List<TransposerRecipeWrapper> getRecipes(IGuiHelper guiHelper, IIngredientRegistry ingredientRegistry) {

		List<TransposerRecipeWrapper> recipes = new ArrayList<>();

		for (TransposerManager.RecipeTransposer recipe : TransposerManager.getFillRecipeList()) {
			recipes.add(new TransposerRecipeWrapper(guiHelper, recipe, RecipeUidsTE.TRANSPOSER_FILL));
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
		List<ItemStack> outputs = ingredients.getOutputs(ItemStack.class);
		List<List<FluidStack>> fluids = ingredients.getInputs(FluidStack.class);

		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();

		guiItemStacks.init(0, true, 30, 10);
		guiItemStacks.init(1, false, 30, 41);
		guiFluidStacks.init(0, true, 103, 1, 16, 60, 2000, false, tankOverlay);

		guiItemStacks.set(0, inputs.get(0));
		guiItemStacks.set(1, outputs.get(0));
		guiFluidStacks.set(0, fluids.get(0));
	}

}
