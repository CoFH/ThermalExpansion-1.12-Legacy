package cofh.thermalexpansion.plugins.jei.machine.crucible;

import cofh.core.util.helpers.FluidHelper;
import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.gui.client.machine.GuiCrucible;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.plugins.jei.machine.BaseRecipeCategory;
import cofh.thermalexpansion.util.managers.machine.CrucibleManager;
import cofh.thermalexpansion.util.managers.machine.CrucibleManager.CrucibleRecipe;
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
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class CrucibleRecipeCategory extends BaseRecipeCategory<CrucibleRecipeWrapper> {

	public static boolean enable = true;

	public static void register(IRecipeCategoryRegistration registry) {

		String category = "Plugins.JEI";
		enable = ThermalExpansion.CONFIG_CLIENT.get(category, "Machine.Crucible", enable);

		if (!enable) {
			return;
		}
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipeCategories(new CrucibleRecipeCategory(guiHelper));
		registry.addRecipeCategories(new CrucibleRecipeCategoryLava(guiHelper));
	}

	public static void initialize(IModRegistry registry) {

		if (!enable) {
			return;
		}
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipes(getRecipes(guiHelper), RecipeUidsTE.CRUCIBLE);
		registry.addRecipeClickArea(GuiCrucible.class, 103, 34, 24, 16, RecipeUidsTE.CRUCIBLE, RecipeUidsTE.CRUCIBLE_LAVA);
		registry.addRecipeCatalyst(BlockMachine.machineCrucible, RecipeUidsTE.CRUCIBLE);

		CrucibleRecipeCategoryLava.initialize(registry);
	}

	public static List<CrucibleRecipeWrapper> getRecipes(IGuiHelper guiHelper) {

		List<CrucibleRecipeWrapper> recipes = new ArrayList<>();

		for (CrucibleRecipe recipe : CrucibleManager.getRecipeList()) {
			recipes.add(new CrucibleRecipeWrapper(guiHelper, recipe));
		}
		return recipes;
	}

	protected IDrawableStatic progress;
	protected IDrawableStatic speed;
	protected IDrawableStatic tank;
	protected IDrawableStatic tankOverlay;

	public CrucibleRecipeCategory(IGuiHelper guiHelper) {

		background = guiHelper.createDrawable(GuiCrucible.TEXTURE, 26, 11, 72, 62, 0, 0, 16, 76);
		energyMeter = Drawables.getDrawables(guiHelper).getEnergyEmpty();
		localizedName = StringHelper.localize("tile.thermalexpansion.machine.crucible.name");

		progress = Drawables.getDrawables(guiHelper).getProgress(2);
		speed = Drawables.getDrawables(guiHelper).getScale(Drawables.SCALE_FLAME);
		tank = Drawables.getDrawables(guiHelper).getTank(Drawables.TANK);
		tankOverlay = Drawables.getDrawables(guiHelper).getTankLargeOverlay(Drawables.TANK);
	}

	@Nonnull
	@Override
	public String getUid() {

		return RecipeUidsTE.CRUCIBLE;
	}

	@Override
	public void drawExtras(@Nonnull Minecraft minecraft) {

		progress.draw(minecraft, 69, 23);
		speed.draw(minecraft, 43, 33);
		tank.draw(minecraft, 105, 0);
		energyMeter.draw(minecraft, 2, 8);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, CrucibleRecipeWrapper recipeWrapper, IIngredients ingredients) {

		List<List<ItemStack>> inputs = ingredients.getInputs(ItemStack.class);
		List<List<FluidStack>> outputs = ingredients.getOutputs(FluidStack.class);

		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();

		guiItemStacks.init(0, true, 42, 14);
		guiFluidStacks.init(0, false, 106, 1, 16, 60, TEProps.MAX_FLUID_LARGE, false, tankOverlay);

		guiItemStacks.set(0, inputs.get(0));
		guiFluidStacks.set(0, outputs.get(0));

		guiFluidStacks.addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {

			if (FluidHelper.isPotionFluid(ingredient)) {
				FluidHelper.addPotionTooltip(ingredient, tooltip);
			}
		});
	}

}
