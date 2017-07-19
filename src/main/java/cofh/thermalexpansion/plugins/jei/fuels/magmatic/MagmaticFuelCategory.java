package cofh.thermalexpansion.plugins.jei.fuels.magmatic;

import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.block.dynamo.BlockDynamo;
import cofh.thermalexpansion.gui.client.dynamo.GuiDynamoMagmatic;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.plugins.jei.fuels.BaseFuelCategory;
import cofh.thermalexpansion.util.managers.dynamo.MagmaticManager;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class MagmaticFuelCategory extends BaseFuelCategory<MagmaticFuelWrapper> {

	public static boolean enable = true;

	public static void register(IRecipeCategoryRegistration registry) {

		if (!enable) {
			return;
		}
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipeCategories(new MagmaticFuelCategory(guiHelper));
	}

	public static void initialize(IModRegistry registry) {

		if (!enable) {
			return;
		}
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipes(getRecipes(registry, guiHelper), RecipeUidsTE.DYNAMO_MAGMATIC);
		registry.addRecipeClickArea(GuiDynamoMagmatic.class, 115, 35, 16, 16, RecipeUidsTE.DYNAMO_MAGMATIC);
		registry.addRecipeCatalyst(BlockDynamo.dynamoMagmatic, RecipeUidsTE.DYNAMO_MAGMATIC);
	}

	public static List<MagmaticFuelWrapper> getRecipes(IModRegistry registry, IGuiHelper guiHelper) {

		List<MagmaticFuelWrapper> recipes = new ArrayList<>();

		for (String fluidName : MagmaticManager.getFuels()) {
			FluidStack fuelStack = new FluidStack(FluidRegistry.getFluid(fluidName), Fluid.BUCKET_VOLUME);
			recipes.add(new MagmaticFuelWrapper(guiHelper, fuelStack, MagmaticManager.getFuelEnergy(fuelStack)));
		}
		return recipes;
	}

	IDrawableStatic tank;

	public MagmaticFuelCategory(IGuiHelper guiHelper) {

		background = guiHelper.createDrawable(GuiDynamoMagmatic.TEXTURE, 26, 11, 70, 62, 0, 0, 16, 78);
		energyMeter = Drawables.getDrawables(guiHelper).getEnergyEmpty();
		durationEmpty = Drawables.getDrawables(guiHelper).getScale(Drawables.SCALE_FLAME);
		localizedName = StringHelper.localize("tile.thermalexpansion.dynamo.magmatic.name");

		tank = Drawables.getDrawables(guiHelper).getTank(Drawables.TANK_SHORT);
	}

	@Nonnull
	@Override
	public String getUid() {

		return RecipeUidsTE.DYNAMO_MAGMATIC;
	}

	@Override
	public void drawExtras(@Nonnull Minecraft minecraft) {

		super.drawExtras(minecraft);

		tank.draw(minecraft, 33, 7);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, MagmaticFuelWrapper recipeWrapper, IIngredients ingredients) {

		List<List<FluidStack>> inputs = ingredients.getInputs(FluidStack.class);

		IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();

		guiFluidStacks.init(0, true, 34, 8, 16, 30, 1000, false, null);

		guiFluidStacks.set(0, inputs.get(0));
	}

}
