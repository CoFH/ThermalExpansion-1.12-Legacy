package cofh.thermalexpansion.plugins.jei.crafting;

import cofh.lib.util.helpers.StringHelper;
import mezz.jei.api.recipe.BlankRecipeWrapper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseRecipeWrapper extends BlankRecipeWrapper {

	protected int energy;
	protected String uId;

	public String getUid() {

		return uId;
	}

	@Nullable
	public List<String> getTooltipStrings(int mouseX, int mouseY) {

		List<String> tooltip = new ArrayList<>();

		if (mouseX > 2 && mouseX < 15 && mouseY > 8 && mouseY < 49) {
			tooltip.add(StringHelper.localize("info.cofh.energy") + ": " + StringHelper.formatNumber(energy) + " RF");
		}
		return tooltip;
	}

}
