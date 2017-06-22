package cofh.thermalexpansion.plugins.jei.crafting.centrifuge;

import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.gui.client.machine.GuiCentrifuge;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.plugins.jei.crafting.BaseRecipeCategory;
import cofh.thermalexpansion.util.managers.machine.CentrifugeManager;
import cofh.thermalexpansion.util.managers.machine.CentrifugeManager.CentrifugeRecipe;
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

public class CentrifugeRecipeCategory extends BaseRecipeCategory<CentrifugeRecipeWrapper> {

	public static boolean enable = true;

	public static void register(IRecipeCategoryRegistration registry) {

		if (!enable) {
			return;
		}
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipeCategories(new CentrifugeRecipeCategory(guiHelper));
	}

	public static void initialize(IModRegistry registry) {

		if (!enable) {
			return;
		}
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipes(getRecipes(guiHelper), RecipeUidsTE.CENTRIFUGE);
		registry.addRecipeClickArea(GuiCentrifuge.class, 71, 34, 24, 16, RecipeUidsTE.CENTRIFUGE);
		registry.addRecipeCatalyst(BlockMachine.machineCentrifuge, RecipeUidsTE.CENTRIFUGE);
	}

	public static List<CentrifugeRecipeWrapper> getRecipes(IGuiHelper guiHelper) {

		List<CentrifugeRecipeWrapper> recipes = new ArrayList<>();

		for (CentrifugeRecipe recipe : CentrifugeManager.getRecipeList()) {
			recipes.add(new CentrifugeRecipeWrapper(guiHelper, recipe));
		}
		return recipes;
	}

	IDrawableStatic tank;
	IDrawableStatic tankOverlay;

	public CentrifugeRecipeCategory(IGuiHelper guiHelper) {

		background = guiHelper.createDrawable(GuiCentrifuge.TEXTURE, 26, 11, 124, 62, 0, 0, 16, 24);

		tank = Drawables.getDrawables(guiHelper).getTank(Drawables.TANK);
		tankOverlay = Drawables.getDrawables(guiHelper).getTankSmallOverlay(Drawables.TANK);
		energyMeter = Drawables.getDrawables(guiHelper).getEnergyEmpty();
		localizedName = StringHelper.localize("tile.thermalexpansion.machine.centrifuge.name");
	}

	@Nonnull
	@Override
	public String getUid() {

		return RecipeUidsTE.CENTRIFUGE;
	}

	@Override
	public void drawExtras(@Nonnull Minecraft minecraft) {

		tank.draw(minecraft, 140, 0);
		energyMeter.draw(minecraft, 2, 8);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, CentrifugeRecipeWrapper recipeWrapper, IIngredients ingredients) {

		List<List<ItemStack>> inputs = ingredients.getInputs(ItemStack.class);
		List<List<ItemStack>> outputs = ingredients.getOutputs(ItemStack.class);
		List<List<FluidStack>> outputFluids = ingredients.getOutputs(FluidStack.class);

		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();

		guiItemStacks.init(0, true, 33, 14);
		guiItemStacks.init(1, false, 95, 14);
		guiItemStacks.init(2, false, 113, 14);
		guiItemStacks.init(3, false, 95, 32);
		guiItemStacks.init(4, false, 113, 32);

		guiFluidStacks.init(0, false, 141, 1, 16, 60, 2000, false, tankOverlay);

		guiItemStacks.set(0, inputs.get(0));

		for (int i = 0; i < outputs.size(); i++) {
			guiItemStacks.set(i + 1, outputs.get(i));
		}
		guiFluidStacks.set(0, outputFluids.get(0));
	}

}
