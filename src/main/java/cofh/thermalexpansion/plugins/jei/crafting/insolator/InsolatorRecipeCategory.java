package cofh.thermalexpansion.plugins.jei.crafting.insolator;

import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.gui.client.machine.GuiInsolator;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.plugins.jei.crafting.BaseRecipeCategory;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager.Type;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class InsolatorRecipeCategory extends BaseRecipeCategory<InsolatorRecipeWrapper> {

	public static boolean enable = true;

	public static void initialize(IModRegistry registry) {

		if (!enable) {
			return;
		}
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipeCategories(new InsolatorRecipeCategory(guiHelper));
		registry.addRecipeHandlers(new InsolatorRecipeHandler());
		registry.addRecipes(getRecipes(guiHelper));
		registry.addRecipeClickArea(GuiInsolator.class, 79, 34, 24, 16, RecipeUidsTE.INSOLATOR, RecipeUidsTE.INSOLATOR_MYCELIUM, RecipeUidsTE.INSOLATOR_NETHER, RecipeUidsTE.INSOLATOR_END, RecipeUidsTE.INSOLATOR_TREE);
		registry.addRecipeCategoryCraftingItem(BlockMachine.machineInsolator, RecipeUidsTE.INSOLATOR);

		InsolatorRecipeCategoryMycelium.initialize(registry);
		InsolatorRecipeCategoryNether.initialize(registry);
		InsolatorRecipeCategoryEnd.initialize(registry);
		InsolatorRecipeCategoryTree.initialize(registry);
	}

	public static List<InsolatorRecipeWrapper> getRecipes(IGuiHelper guiHelper) {

		List<InsolatorRecipeWrapper> recipes = new ArrayList<>();

		for (InsolatorManager.RecipeInsolator recipe : InsolatorManager.getRecipeList()) {

			if (recipe.getType() == Type.STANDARD) {
				recipes.add(new InsolatorRecipeWrapper(guiHelper, recipe));
			}
		}
		return recipes;
	}

	IDrawableStatic tank;
	IDrawableStatic tankOverlay;

	public InsolatorRecipeCategory(IGuiHelper guiHelper) {

		background = guiHelper.createDrawable(GuiInsolator.TEXTURE, 26, 11, 124, 62, 0, 0, 16, 24);
		tank = Drawables.getDrawables(guiHelper).getTank(Drawables.TANK);
		tankOverlay = Drawables.getDrawables(guiHelper).getTankSmallOverlay(Drawables.TANK);
		energyMeter = Drawables.getDrawables(guiHelper).getEnergyEmpty();
		localizedName = StringHelper.localize("tile.thermalexpansion.machine.insolator.name");
	}

	@Nonnull
	@Override
	public String getUid() {

		return RecipeUidsTE.INSOLATOR;
	}

	@Override
	public void drawExtras(@Nonnull Minecraft minecraft) {

		tank.draw(minecraft, 140, 0);
		energyMeter.draw(minecraft, 2, 8);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, InsolatorRecipeWrapper recipeWrapper, IIngredients ingredients) {

		List<List<ItemStack>> inputs = ingredients.getInputs(ItemStack.class);
		List<List<FluidStack>> inputFluids = ingredients.getInputs(FluidStack.class);
		List<List<ItemStack>> outputs = ingredients.getOutputs(ItemStack.class);

		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();

		guiItemStacks.init(0, true, 21, 14);
		guiItemStacks.init(1, true, 45, 14);
		guiItemStacks.init(2, false, 105, 14);

		guiFluidStacks.init(0, true, 141, 1, 16, 60, 2000, false, tankOverlay);

		guiItemStacks.set(0, inputs.get(0));
		guiItemStacks.set(1, inputs.get(1));
		guiItemStacks.set(2, outputs.get(0));

		guiFluidStacks.set(0, inputFluids.get(0));

		if (outputs.size() > 1) {
			guiItemStacks.init(3, false, 105, 41);
			guiItemStacks.set(3, outputs.get(1));

			guiItemStacks.addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {

				if (slotIndex == 3) {
					tooltip.add(StringHelper.localize("info.cofh.chance") + ": " + recipeWrapper.chance + "%");
				}
			});
		}
	}

}
