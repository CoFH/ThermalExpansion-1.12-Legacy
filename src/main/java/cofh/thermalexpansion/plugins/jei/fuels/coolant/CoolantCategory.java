package cofh.thermalexpansion.plugins.jei.fuels.coolant;

import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.block.device.BlockDevice;
import cofh.thermalexpansion.block.dynamo.BlockDynamo;
import cofh.thermalexpansion.item.ItemAugment;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.plugins.jei.fuels.BaseFuelCategory;
import cofh.thermalexpansion.util.managers.CoolantManager;
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

public class CoolantCategory extends BaseFuelCategory<CoolantWrapper> {

	public static boolean enable = true;

	public static void register(IRecipeCategoryRegistration registry) {

		if (!enable) {
			return;
		}
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipeCategories(new CoolantCategory(guiHelper));
	}

	public static void initialize(IModRegistry registry) {

		if (!enable) {
			return;
		}
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipes(getRecipes(registry, guiHelper), RecipeUidsTE.COOLANT);
		registry.addRecipeCatalyst(BlockDevice.deviceHeatSink, RecipeUidsTE.COOLANT);
		registry.addRecipeCatalyst(BlockDynamo.dynamoCompression, RecipeUidsTE.COOLANT);
		registry.addRecipeCatalyst(ItemAugment.dynamoMagmaticCoolant, RecipeUidsTE.COOLANT);
	}

	public static List<CoolantWrapper> getRecipes(IModRegistry registry, IGuiHelper guiHelper) {

		List<CoolantWrapper> recipes = new ArrayList<>();

		for (String fluidName : CoolantManager.getCoolantFluids()) {
			FluidStack coolantStack = new FluidStack(FluidRegistry.getFluid(fluidName), Fluid.BUCKET_VOLUME);
			recipes.add(new CoolantWrapper(guiHelper, coolantStack, CoolantManager.getCoolantRF(coolantStack), CoolantManager.getCoolantFactor(coolantStack)));
		}
		return recipes;
	}

	IDrawableStatic tank;

	public CoolantCategory(IGuiHelper guiHelper) {

		background = guiHelper.createBlankDrawable(164, 62);
		energyMeter = Drawables.getDrawables(guiHelper).getCoolantEmpty();
		durationEmpty = Drawables.getDrawables(guiHelper).getScale(Drawables.SCALE_SNOWFLAKE);
		localizedName = StringHelper.localize("info.thermalexpansion.coolant");

		tank = Drawables.getDrawables(guiHelper).getTank(Drawables.TANK_SHORT);
	}

	@Nonnull
	@Override
	public String getUid() {

		return RecipeUidsTE.COOLANT;
	}

	@Override
	public void drawExtras(@Nonnull Minecraft minecraft) {

		super.drawExtras(minecraft);

		tank.draw(minecraft, 33, 7);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, CoolantWrapper recipeWrapper, IIngredients ingredients) {

		List<List<FluidStack>> inputs = ingredients.getInputs(FluidStack.class);

		IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();

		guiFluidStacks.init(0, true, 34, 8, 16, 30, 1000, false, null);

		guiFluidStacks.set(0, inputs.get(0));
	}

}
