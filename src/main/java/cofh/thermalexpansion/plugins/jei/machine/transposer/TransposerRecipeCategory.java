package cofh.thermalexpansion.plugins.jei.machine.transposer;

import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.gui.client.machine.GuiTransposer;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.plugins.jei.machine.BaseRecipeCategory;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.client.Minecraft;

import javax.annotation.Nonnull;

public abstract class TransposerRecipeCategory extends BaseRecipeCategory<TransposerRecipeWrapper> {

	public static boolean enable = true;

	public static void register(IRecipeCategoryRegistration registry) {

		String category = "Plugins.JEI";
		enable = ThermalExpansion.CONFIG_CLIENT.get(category, "Machine.Transposer", enable);

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
		try {
			TransposerRecipeCategoryFill.initialize(registry);
			TransposerRecipeCategoryExtract.initialize(registry);
			registry.addRecipeClickArea(GuiTransposer.class, 112, 19, 24, 16, RecipeUidsTE.TRANSPOSER_FILL, RecipeUidsTE.TRANSPOSER_EXTRACT);
		} catch (Throwable t) {
			ThermalExpansion.LOG.error("Bad/null recipe!", t);
		}
	}

	protected IDrawableStatic speed;
	protected IDrawableStatic tankOverlay;

	public TransposerRecipeCategory(IGuiHelper guiHelper) {

		background = guiHelper.createDrawable(GuiTransposer.TEXTURE, 73, 8, 96, 62, 0, 0, 24, 44);
		energyMeter = Drawables.getDrawables(guiHelper).getEnergyEmpty();
		speed = Drawables.getDrawables(guiHelper).getScale(Drawables.SCALE_BUBBLE);
		tankOverlay = Drawables.getDrawables(guiHelper).getTankLargeOverlay(0);
		localizedName = StringHelper.localize("tile.thermalexpansion.machine.transposer.name");
	}

	@Override
	public void drawExtras(@Nonnull Minecraft minecraft) {

		speed.draw(minecraft, 68, 41);
		energyMeter.draw(minecraft, 2, 8);
	}

}
