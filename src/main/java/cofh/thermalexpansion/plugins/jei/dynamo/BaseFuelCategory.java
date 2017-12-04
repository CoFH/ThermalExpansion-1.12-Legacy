package cofh.thermalexpansion.plugins.jei.dynamo;

import cofh.thermalexpansion.ThermalExpansion;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class BaseFuelCategory<T extends IRecipeWrapper> extends BlankRecipeCategory<T> implements IRecipeCategory<T> {

	protected IDrawableStatic background;
	protected IDrawableStatic energyMeter;
	protected IDrawableStatic durationEmpty;
	protected IDrawableStatic icon;
	protected String localizedName;

	@Nonnull
	@Override
	public String getTitle() {

		return localizedName;
	}

	@Override
	public String getModName() {

		return ThermalExpansion.MOD_NAME;
	}

	@Nonnull
	@Override
	public IDrawable getBackground() {

		return background;
	}

	@Nullable
	@Override
	public IDrawable getIcon() {

		return icon;
	}

	@Override
	public void drawExtras(@Nonnull Minecraft minecraft) {

		energyMeter.draw(minecraft, 71, 7);
		durationEmpty.draw(minecraft, 34, 43);
	}

}
