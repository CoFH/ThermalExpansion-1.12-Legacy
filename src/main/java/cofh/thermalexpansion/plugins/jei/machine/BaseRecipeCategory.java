package cofh.thermalexpansion.plugins.jei.machine;

import cofh.thermalexpansion.ThermalExpansion;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public abstract class BaseRecipeCategory<T extends IRecipeWrapper> implements IRecipeCategory<T> {

	protected IDrawableStatic background;
	protected IDrawableStatic energyMeter;
	protected IDrawableStatic icon;
	protected String localizedName;

	public BaseRecipeCategory() {

	}

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
	public List<String> getTooltipStrings(int mouseX, int mouseY) {

		return Collections.emptyList();
	}

}
