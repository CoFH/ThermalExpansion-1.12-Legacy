package cofh.thermalexpansion.plugins.jei.crafting.transposer;

import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.gui.client.machine.GuiTransposer;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.plugins.jei.crafting.BaseRecipeCategory;
import cofh.thermalexpansion.plugins.jei.crafting.pulverizer.PulverizerRecipeCategory;
import cofh.thermalexpansion.plugins.jei.crafting.pulverizer.PulverizerRecipeCategoryPetrotheum;
import cofh.thermalexpansion.util.managers.machine.TransposerManager;
import cofh.thermalexpansion.util.managers.machine.TransposerManager.TransposerRecipe;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
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

public abstract class TransposerRecipeCategory extends BaseRecipeCategory<TransposerRecipeWrapper> {

	public static boolean enable = true;

	public static void register(IRecipeCategoryRegistration registry) {

		if (!enable) {
			return;
		}
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipeCategories(new TransposerRecipeCategoryFill(guiHelper));
		registry.addRecipeCategories(new TransposerRecipeCategoryExtract(guiHelper));
	}

	public static void initialize(IModRegistry registry) {

		if (!enable) {
			return;
		}
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		TransposerRecipeCategoryFill.initialize(registry);
		TransposerRecipeCategoryExtract.initialize(registry);
		registry.addRecipeClickArea(GuiTransposer.class, 112, 19, 24, 16, RecipeUidsTE.TRANSPOSER_FILL, RecipeUidsTE.TRANSPOSER_EXTRACT);
	}

	IDrawableStatic bubble;
	IDrawableStatic tankOverlay;

	public TransposerRecipeCategory(IGuiHelper guiHelper) {

		background = guiHelper.createDrawable(GuiTransposer.TEXTURE, 73, 8, 96, 62, 0, 0, 24, 44);
		energyMeter = Drawables.getDrawables(guiHelper).getEnergyEmpty();
		bubble = Drawables.getDrawables(guiHelper).getScale(Drawables.SCALE_BUBBLE);
		tankOverlay = Drawables.getDrawables(guiHelper).getTankSmallOverlay(0);
		localizedName = StringHelper.localize("tile.thermalexpansion.machine.transposer.name");
	}

	@Override
	public void drawExtras(@Nonnull Minecraft minecraft) {

		bubble.draw(minecraft, 68, 41);
		energyMeter.draw(minecraft, 2, 8);
	}

}
