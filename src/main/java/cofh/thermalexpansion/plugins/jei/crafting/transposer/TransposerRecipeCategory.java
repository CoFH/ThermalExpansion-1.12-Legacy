package cofh.thermalexpansion.plugins.jei.crafting.transposer;

import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.gui.client.machine.GuiTransposer;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.util.managers.machine.TransposerManager;
import cofh.thermalexpansion.util.managers.machine.TransposerManager.RecipeTransposer;
import com.google.common.collect.ImmutableList;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.api.recipe.BlankRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public abstract class TransposerRecipeCategory extends BlankRecipeCategory<TransposerRecipeWrapper> {

	public static boolean enable = true;

	public static void initialize(IModRegistry registry) {

		if (!enable) {
			return;
		}
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		TransposerRecipeCategoryFill.initialize(registry);
		TransposerRecipeCategoryExtract.initialize(registry);
		registry.addRecipes(getRecipes(guiHelper, registry.getIngredientRegistry()));
		registry.addRecipeClickArea(GuiTransposer.class, 112, 19, 24, 16, RecipeUidsTE.TRANSPOSER_FILL, RecipeUidsTE.TRANSPOSER_EXTRACT);
		registry.addRecipeHandlers(new TransposerRecipeHandler());
	}

	public static List<TransposerRecipeWrapper> getRecipes(IGuiHelper guiHelper, IIngredientRegistry ingredientRegistry) {

		List<TransposerRecipeWrapper> recipes = new ArrayList<>();

		List<ItemStack> ingredients = ingredientRegistry.getIngredients(ItemStack.class);

		for (ItemStack ingredient : ingredients) {
			if (ingredient.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)) {
				FluidStack drain;
				ItemStack emptyStack = ingredient.copy();
				IFluidHandler emptyCapability = emptyStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
				drain = emptyCapability.drain(Fluid.BUCKET_VOLUME, true);

				if (drain != null) {
					if (emptyStack.getCount() == 0) {
						emptyStack = ItemStack.EMPTY;
					}
					RecipeTransposer recipe = new RecipeTransposer(ingredient, emptyStack, drain, TransposerManager.DEFAULT_ENERGY, emptyStack.isEmpty() ? 0 : 100);
					recipes.add(new TransposerRecipeWrapper(guiHelper, recipe, RecipeUidsTE.TRANSPOSER_EXTRACT));
					addFillRecipe(ingredient, drain.getFluid(), recipes, guiHelper);
				} else {
					for (Fluid fluid : FluidRegistry.getRegisteredFluids().values()) {
						addFillRecipe(ingredient, fluid, recipes, guiHelper);
					}
				}
			}
		}
		return recipes;
	}

	private static void addFillRecipe(ItemStack ingredient, Fluid fluid, List<TransposerRecipeWrapper> recipes, IGuiHelper guiHelper) {

		ItemStack fillStack = ingredient.copy();
		IFluidHandler fillCapability = fillStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
		int fill = fillCapability.fill(new FluidStack(fluid, Fluid.BUCKET_VOLUME), true);

		if (fill > 0) {
			FluidStack filledFluid = new FluidStack(fluid, fill);
			RecipeTransposer recipe = new RecipeTransposer(ingredient, fillStack, filledFluid, TransposerManager.DEFAULT_ENERGY, 100);
			recipes.add(new TransposerRecipeWrapper(guiHelper, recipe, RecipeUidsTE.TRANSPOSER_FILL));
		}
	}

	IDrawableStatic background;
	IDrawableStatic energyMeter;
	IDrawableStatic icon;
	IDrawableStatic bubble;
	IDrawableStatic tankOverlay;
	String localizedName;

	public TransposerRecipeCategory(IGuiHelper guiHelper) {

		background = guiHelper.createDrawable(GuiTransposer.TEXTURE, 73, 8, 96, 62, 0, 0, 24, 44);
		energyMeter = Drawables.getDrawables(guiHelper).getEnergyEmpty();
		bubble = Drawables.getDrawables(guiHelper).getScale(Drawables.SCALE_BUBBLE);
		tankOverlay = Drawables.getDrawables(guiHelper).getTankSmallOverlay(0);
		localizedName = StringHelper.localize("tile.thermalexpansion.machine.transposer.name");
	}

	@Nonnull
	@Override
	public String getTitle() {

		return localizedName;
	}

	@Nonnull
	@Override
	public IDrawable getBackground() {

		return background;
	}

	@Override
	public IDrawable getIcon() {

		return icon;
	}

	@Override
	public void drawExtras(@Nonnull Minecraft minecraft) {

		bubble.draw(minecraft, 68, 41);
		energyMeter.draw(minecraft, 2, 8);
	}

}
