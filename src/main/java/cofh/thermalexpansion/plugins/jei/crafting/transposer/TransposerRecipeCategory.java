package cofh.thermalexpansion.plugins.jei.crafting.transposer;

import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.gui.client.machine.GuiTransposer;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.recipe.BlankRecipeCategory;
import net.minecraft.client.Minecraft;

import javax.annotation.Nonnull;

public abstract class TransposerRecipeCategory extends BlankRecipeCategory<TransposerRecipeWrapper> {

	public static boolean enable = true;

	public static void initialize(IModRegistry registry) {

		if (!enable) {
			return;
		}
		TransposerRecipeCategoryFill.initialize(registry);
		TransposerRecipeCategoryExtract.initialize(registry);
		registry.addRecipeClickArea(GuiTransposer.class, 112, 19, 24, 16, RecipeUidsTE.TRANSPOSER_FILL, RecipeUidsTE.TRANSPOSER_EXTRACT);
		registry.addRecipeHandlers(new TransposerRecipeHandler());
	}

	IDrawableStatic background;
	IDrawableStatic energyMeter;
	IDrawableStatic icon;
	IDrawableStatic bubble;
	IDrawableStatic tankOverlay;
	String localizedName;

	public TransposerRecipeCategory(IGuiHelper guiHelper) {

		background = guiHelper.createDrawable(GuiTransposer.TEXTURE, 73, 8, 96, 62, 0, 0, 24, 44);
		energyMeter = Drawables.getDrawables(guiHelper).getEnergyEmpty();
		bubble = Drawables.getDrawables(guiHelper).getSpeed(0);
		tankOverlay = Drawables.getDrawables(guiHelper).getTankSmallOverlay(0);
		localizedName = StringHelper.localize("tile.thermalexpansion.machine.transposer.name");
	}

	@Nonnull
	@Override
	public String getTitle() {

		return localizedName;
	}

	@Nonnull
	@Override
	public IDrawable getBackground() {

		return background;
	}

	@Override
	public IDrawable getIcon() {

		return icon;
	}

	@Override
	public void drawExtras(@Nonnull Minecraft minecraft) {

		bubble.draw(minecraft, 68, 41);
		energyMeter.draw(minecraft, 2, 8);
	}

}
