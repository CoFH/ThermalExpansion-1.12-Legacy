package cofh.thermalexpansion.plugins.jei.fuels.coolant;

import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.block.dynamo.TileDynamoCompression;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.fuels.BaseFuelWrapper;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableAnimated.StartDirection;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CoolantWrapper extends BaseFuelWrapper {

	private static DecimalFormat decimalFormat = new DecimalFormat("##0.00");

	final List<List<FluidStack>> inputs;

	protected int factor;
	protected double overclock;

	protected IDrawableAnimated durationFill;

	public CoolantWrapper(IGuiHelper guiHelper, FluidStack fuel, int energy, int factor) {

		List<FluidStack> recipeInputs = new ArrayList<>();
		recipeInputs.add(fuel);

		this.inputs = Collections.singletonList(recipeInputs);
		this.energy = energy;
		this.factor = factor;

		overclock = 100D / factor;

		IDrawableStatic progressDrawable = Drawables.getDrawables(guiHelper).getScaleFill(Drawables.SCALE_SNOWFLAKE);
		IDrawableStatic energyDrawable = Drawables.getDrawables(guiHelper).getCoolantFill();

		durationFill = guiHelper.createAnimatedDrawable(progressDrawable, energy / (TileDynamoCompression.basePower * 10), StartDirection.TOP, true);
		energyMeter = guiHelper.createAnimatedDrawable(energyDrawable, energy / (TileDynamoCompression.basePower * 100), StartDirection.TOP, true);
	}

	@Override
	public void getIngredients(IIngredients ingredients) {

		ingredients.setInputLists(FluidStack.class, inputs);
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

		durationFill.draw(minecraft, 34, 43);
		energyMeter.draw(minecraft, 71, 7);

		minecraft.fontRendererObj.drawString(StringHelper.formatNumber(energy) + " TC", 96, (recipeHeight - 9) / 2, 0x0a76d0);
		minecraft.fontRendererObj.drawString(decimalFormat.format(overclock) + "%", 96, 10 + (recipeHeight - 9) / 2, 0xd0650b);
	}

	@Nullable
	public List<String> getTooltipStrings(int mouseX, int mouseY) {

		return null;
		//		List<String> tooltip = new ArrayList<>();
		//
		//		if (mouseX > 71 && mouseX < 84 && mouseY > 7 && mouseY < 48) {
		//			tooltip.add(StringHelper.localize("info.cofh.energy") + ": " + StringHelper.formatNumber(energy) + " TC");
		//		}
		//		return tooltip;
	}

}
