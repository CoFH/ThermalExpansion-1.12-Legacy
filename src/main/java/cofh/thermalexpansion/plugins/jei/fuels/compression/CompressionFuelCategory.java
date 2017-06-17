package cofh.thermalexpansion.plugins.jei.fuels.compression;

import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.block.dynamo.BlockDynamo;
import cofh.thermalexpansion.gui.client.dynamo.GuiDynamoCompression;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.plugins.jei.fuels.BaseFuelCategory;
import cofh.thermalexpansion.util.managers.dynamo.CompressionManager;
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
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class CompressionFuelCategory extends BaseFuelCategory<CompressionFuelWrapper> {

	public static boolean enable = true;

	public static void initialize(IModRegistry registry) {

		if (!enable) {
			return;
		}
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		((IRecipeCategoryRegistration) registry).addRecipeCategories(new CompressionFuelCategory(guiHelper));
		registry.addRecipes(getRecipes(registry, guiHelper), RecipeUidsTE.DYNAMO_COMPRESSION);
		registry.addRecipeClickArea(GuiDynamoCompression.class, 115, 35, 16, 16, RecipeUidsTE.DYNAMO_COMPRESSION);
		registry.addRecipeCatalyst(BlockDynamo.dynamoCompression, RecipeUidsTE.DYNAMO_COMPRESSION);
	}

	public static List<CompressionFuelWrapper> getRecipes(IModRegistry registry, IGuiHelper guiHelper) {

		List<CompressionFuelWrapper> recipes = new ArrayList<>();

		for (Fluid fuel : CompressionManager.getFuels()) {
			FluidStack fuelStack = new FluidStack(fuel, Fluid.BUCKET_VOLUME);
			recipes.add(new CompressionFuelWrapper(guiHelper, fuelStack, CompressionManager.getFuelEnergy(fuelStack)));
		}
		return recipes;
	}

	IDrawableStatic tank;

	public CompressionFuelCategory(IGuiHelper guiHelper) {

		background = guiHelper.createDrawable(GuiDynamoCompression.TEXTURE, 26, 11, 70, 62, 0, 0, 16, 78);
		energyMeter = Drawables.getDrawables(guiHelper).getEnergyEmpty();
		durationEmpty = Drawables.getDrawables(guiHelper).getScale(Drawables.SCALE_FLAME);
		localizedName = StringHelper.localize("tile.thermalexpansion.dynamo.compression.name");

		tank = Drawables.getDrawables(guiHelper).getTank(Drawables.TANK_SHORT);
	}

	@Nonnull
	@Override
	public String getUid() {

		return RecipeUidsTE.DYNAMO_COMPRESSION;
	}

	@Override
	public void drawExtras(@Nonnull Minecraft minecraft) {

		super.drawExtras(minecraft);

		tank.draw(minecraft, 33, 7);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, CompressionFuelWrapper recipeWrapper, IIngredients ingredients) {

		List<List<FluidStack>> inputs = ingredients.getInputs(FluidStack.class);

		IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();

		guiFluidStacks.init(0, true, 34, 8, 16, 30, 1000, false, null);

		guiFluidStacks.set(0, inputs.get(0));
	}

}
