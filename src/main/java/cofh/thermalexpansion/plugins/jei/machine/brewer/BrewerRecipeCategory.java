package cofh.thermalexpansion.plugins.jei.machine.brewer;

import cofh.core.util.helpers.FluidHelper;
import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.gui.client.machine.GuiBrewer;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.plugins.jei.machine.BaseRecipeCategory;
import cofh.thermalexpansion.util.managers.machine.BrewerManager;
import cofh.thermalexpansion.util.managers.machine.BrewerManager.BrewerRecipe;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class BrewerRecipeCategory extends BaseRecipeCategory<BrewerRecipeWrapper> {

	public static boolean enable = true;

	public static void register(IRecipeCategoryRegistration registry) {

		String category = "Plugins.JEI";
		enable = ThermalExpansion.CONFIG_CLIENT.get(category, "Machine.Brewer", enable);

		if (!enable) {
			return;
		}
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipeCategories(new BrewerRecipeCategory(guiHelper));
	}

	public static void initialize(IModRegistry registry) {

		if (!enable) {
			return;
		}
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipes(getRecipes(guiHelper), RecipeUidsTE.BREWER);
		registry.addRecipeClickArea(GuiBrewer.class, 112, 34, 24, 16, RecipeUidsTE.BREWER);
		registry.addRecipeCatalyst(BlockMachine.machineBrewer, RecipeUidsTE.BREWER);
	}

	public static List<BrewerRecipeWrapper> getRecipes(IGuiHelper guiHelper) {

		List<BrewerRecipeWrapper> recipes = new ArrayList<>();

		for (BrewerRecipe recipe : BrewerManager.getRecipeList()) {
			recipes.add(new BrewerRecipeWrapper(guiHelper, recipe));
		}
		return recipes;
	}

	protected IDrawableStatic progress;
	protected IDrawableStatic speed;
	protected IDrawableStatic tank;
	protected IDrawableStatic tankOverlayInput;
	protected IDrawableStatic tankOverlayOutput;

	public BrewerRecipeCategory(IGuiHelper guiHelper) {

		background = guiHelper.createDrawable(GuiBrewer.TEXTURE, 26, 11, 72, 62, 0, 0, 16, 76);
		energyMeter = Drawables.getDrawables(guiHelper).getEnergyEmpty();
		localizedName = StringHelper.localize("tile.thermalexpansion.machine.brewer.name");

		speed = Drawables.getDrawables(guiHelper).getScale(Drawables.SCALE_ALCHEMY);
		progress = Drawables.getDrawables(guiHelper).getProgress(Drawables.PROGRESS_DROP);
		tank = Drawables.getDrawables(guiHelper).getTank(Drawables.TANK);
		tankOverlayInput = Drawables.getDrawables(guiHelper).getTankSmallOverlay(Drawables.TANK_SHORT);
		tankOverlayOutput = Drawables.getDrawables(guiHelper).getTankSmallOverlay(Drawables.TANK);

	}

	@Nonnull
	@Override
	public String getUid() {

		return RecipeUidsTE.BREWER;
	}

	@Override
	public void drawExtras(@Nonnull Minecraft minecraft) {

		progress.draw(minecraft, 94, 23);
		speed.draw(minecraft, 46, 23);
		tank.draw(minecraft, 125, 0);
		energyMeter.draw(minecraft, 2, 8);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, BrewerRecipeWrapper recipeWrapper, IIngredients ingredients) {

		List<List<ItemStack>> inputItems = ingredients.getInputs(ItemStack.class);
		List<List<FluidStack>> inputFluids = ingredients.getInputs(FluidStack.class);
		List<List<FluidStack>> outputFluids = ingredients.getOutputs(FluidStack.class);

		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();

		guiItemStacks.init(0, true, 69, 22);
		guiFluidStacks.init(0, true, 22, 16, 16, 30, Fluid.BUCKET_VOLUME, false, null);
		guiFluidStacks.init(1, false, 126, 1, 16, 60, Fluid.BUCKET_VOLUME, false, null);

		guiItemStacks.set(0, inputItems.get(0));
		guiFluidStacks.set(0, inputFluids.get(0));
		guiFluidStacks.set(1, outputFluids.get(0));

		guiFluidStacks.addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {

			if (FluidHelper.isPotionFluid(ingredient)) {
				FluidHelper.addPotionTooltip(ingredient, tooltip);
			}
		});
	}

}
