package cofh.thermalexpansion.plugins.jei.machine.refinery;

import cofh.core.util.helpers.FluidHelper;
import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.gui.client.machine.GuiRefinery;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.plugins.jei.machine.BaseRecipeCategory;
import cofh.thermalexpansion.util.managers.machine.RefineryManager;
import cofh.thermalexpansion.util.managers.machine.RefineryManager.RefineryRecipe;
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

public class RefineryRecipeCategory extends BaseRecipeCategory<RefineryRecipeWrapper> {

	public static boolean enable = true;

	public static void register(IRecipeCategoryRegistration registry) {

		if (!enable) {
			return;
		}
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipeCategories(new RefineryRecipeCategory(guiHelper));
		registry.addRecipeCategories(new RefineryRecipeCategoryFossil(guiHelper));
		registry.addRecipeCategories(new RefineryRecipeCategoryBio(guiHelper));
		registry.addRecipeCategories(new RefineryRecipeCategoryPotion(guiHelper));
	}

	public static void initialize(IModRegistry registry) {

		if (!enable) {
			return;
		}
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipes(getRecipes(guiHelper), RecipeUidsTE.REFINERY);
		registry.addRecipeClickArea(GuiRefinery.class, 76, 34, 24, 16, RecipeUidsTE.REFINERY, RecipeUidsTE.REFINERY_FOSSIL, RecipeUidsTE.REFINERY_BIO, RecipeUidsTE.REFINERY_POTION);
		registry.addRecipeCatalyst(BlockMachine.machineRefinery, RecipeUidsTE.REFINERY);

		RefineryRecipeCategoryFossil.initialize(registry);
		RefineryRecipeCategoryBio.initialize(registry);
		RefineryRecipeCategoryPotion.initialize(registry);
	}

	public static List<RefineryRecipeWrapper> getRecipes(IGuiHelper guiHelper) {

		List<RefineryRecipeWrapper> recipes = new ArrayList<>();

		for (RefineryRecipe recipe : RefineryManager.getRecipeList()) {
			recipes.add(new RefineryRecipeWrapper(guiHelper, recipe));
		}
		return recipes;
	}

	protected IDrawableStatic progress;
	protected IDrawableStatic speed;
	protected IDrawableStatic slot;
	protected IDrawableStatic tank;
	protected IDrawableStatic tankOverlayInput;
	protected IDrawableStatic tankOverlayOutput;

	public RefineryRecipeCategory(IGuiHelper guiHelper) {

		background = guiHelper.createDrawable(GuiRefinery.TEXTURE, 26, 11, 48, 62, 0, 0, 16, 100);
		energyMeter = Drawables.getDrawables(guiHelper).getEnergyEmpty();
		localizedName = StringHelper.localize("tile.thermalexpansion.machine.refinery.name");

		progress = Drawables.getDrawables(guiHelper).getProgress(Drawables.PROGRESS_DROP);
		speed = Drawables.getDrawables(guiHelper).getScale(Drawables.SCALE_FLAME);
		slot = Drawables.getDrawables(guiHelper).getSlot(Drawables.SLOT_OUTPUT);
		tank = Drawables.getDrawables(guiHelper).getTank(Drawables.TANK);
		tankOverlayInput = Drawables.getDrawables(guiHelper).getTankSmallOverlay(Drawables.TANK_SHORT);
		tankOverlayOutput = Drawables.getDrawables(guiHelper).getTankSmallOverlay(Drawables.TANK);
	}

	@Nonnull
	@Override
	public String getUid() {

		return RecipeUidsTE.REFINERY;
	}

	@Override
	public void drawExtras(@Nonnull Minecraft minecraft) {

		progress.draw(minecraft, 69, 23);
		speed.draw(minecraft, 34, 40);
		slot.draw(minecraft, 101, 19);
		tank.draw(minecraft, 140, 0);
		energyMeter.draw(minecraft, 2, 8);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, RefineryRecipeWrapper recipeWrapper, IIngredients ingredients) {

		List<List<FluidStack>> inputFluids = ingredients.getInputs(FluidStack.class);
		List<List<FluidStack>> outputFluids = ingredients.getOutputs(FluidStack.class);
		List<List<ItemStack>> outputItems = ingredients.getOutputs(ItemStack.class);

		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();

		guiItemStacks.init(0, false, 105, 23);
		guiFluidStacks.init(0, true, 34, 8, 16, 30, Fluid.BUCKET_VOLUME / 2, false, tankOverlayInput);
		guiFluidStacks.init(1, false, 141, 1, 16, 60, Fluid.BUCKET_VOLUME / 2, false, tankOverlayOutput);

		guiItemStacks.set(0, outputItems.get(0));
		guiFluidStacks.set(0, inputFluids.get(0));
		guiFluidStacks.set(1, outputFluids.get(0));

		guiItemStacks.addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {

			if (slotIndex == 0 && recipeWrapper.chance < 100) {
				tooltip.add(StringHelper.localize("info.cofh.chance") + ": " + recipeWrapper.chance + "%");
			}
		});

		guiFluidStacks.addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {

			if (FluidHelper.isPotionFluid(ingredient)) {
				FluidHelper.addPotionTooltip(ingredient, tooltip);
			}
		});
	}

}
