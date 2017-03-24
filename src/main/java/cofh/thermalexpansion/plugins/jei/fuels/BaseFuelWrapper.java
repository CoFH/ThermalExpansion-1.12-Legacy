package cofh.thermalexpansion.plugins.jei.fuels;

import cofh.lib.util.helpers.StringHelper;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.client.Minecraft;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseFuelWrapper extends BlankRecipeWrapper {

	protected int energy;

	protected IDrawableAnimated durationFill;
	protected IDrawableAnimated energyMeter;

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

		durationFill.draw(minecraft, 34, 43);
		energyMeter.draw(minecraft, 71, 7);

		minecraft.fontRendererObj.drawString(StringHelper.formatNumber(energy) + " RF", 96, (recipeHeight - 9) / 2, 0x808080);
	}

	@Nullable
	public List<String> getTooltipStrings(int mouseX, int mouseY) {

		List<String> tooltip = new ArrayList<>();

		if (mouseX > 71 && mouseX < 84 && mouseY > 7 && mouseY < 48) {
			tooltip.add(StringHelper.localize("info.cofh.energy") + ": " + StringHelper.formatNumber(energy) + " RF");
		}
		return tooltip;
	}

}
