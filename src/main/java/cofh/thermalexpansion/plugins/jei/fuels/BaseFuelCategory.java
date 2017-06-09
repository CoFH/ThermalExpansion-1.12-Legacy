package cofh.thermalexpansion.plugins.jei.fuels;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;

import javax.annotation.Nonnull;

public abstract class BaseFuelCategory<T extends IRecipeWrapper> extends BlankRecipeCategory<T> implements IRecipeCategory<T> {

	protected IDrawableStatic background;
	protected IDrawableStatic energyMeter;
	protected IDrawableStatic durationEmpty;
	protected String localizedName;

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
	public void drawExtras(@Nonnull Minecraft minecraft) {

		energyMeter.draw(minecraft, 71, 7);
		durationEmpty.draw(minecraft, 34, 43);
	}

}
