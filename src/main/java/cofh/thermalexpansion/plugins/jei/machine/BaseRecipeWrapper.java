package cofh.thermalexpansion.plugins.jei.machine;

import cofh.core.util.helpers.StringHelper;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.recipe.BlankRecipeWrapper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseRecipeWrapper extends BlankRecipeWrapper {

	protected int energy;
	protected String uId;

	protected IDrawableAnimated energyMeter;

	public String getUid() {

		return uId;
	}

	@Nullable
	public List<String> getTooltipStrings(int mouseX, int mouseY) {

		List<String> tooltip = new ArrayList<>();

		if (energyMeter != null && mouseX > 2 && mouseX < 15 && mouseY > 8 && mouseY < 49) {
			tooltip.add(StringHelper.localize("info.cofh.energy") + ": " + StringHelper.formatNumber(energy) + " RF");
		}
		return tooltip;
	}

}
