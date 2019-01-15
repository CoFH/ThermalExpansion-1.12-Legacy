package cofh.thermalexpansion.plugins.jei.dynamo.enervation;

import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.block.dynamo.TileDynamoEnervation;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.dynamo.BaseFuelWrapper;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawableAnimated.StartDirection;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class EnervationFuelWrapper extends BaseFuelWrapper {

	protected List<ItemStack> inputs;
	protected int maxEnergy;

	public EnervationFuelWrapper(IGuiHelper guiHelper, ItemStack fuel, int energy) {

		this(guiHelper, fuel, energy, 0);
	}

	public EnervationFuelWrapper(IGuiHelper guiHelper, ItemStack fuel, int energy, int maxEnergy) {

		List<ItemStack> recipeInputs = new ArrayList<>();
		recipeInputs.add(fuel);

		this.inputs = recipeInputs;
		this.energy = energy;
		this.maxEnergy = maxEnergy;

		IDrawableStatic progressDrawable = Drawables.getDrawables(guiHelper).getScaleFill(Drawables.SCALE_FLUX);
		IDrawableStatic energyDrawable = Drawables.getDrawables(guiHelper).getEnergyFill();

		durationFill = guiHelper.createAnimatedDrawable(progressDrawable, Math.max(1, energy / TileDynamoEnervation.basePower), StartDirection.TOP, true);
		energyMeter = guiHelper.createAnimatedDrawable(energyDrawable, 1000, StartDirection.BOTTOM, false);
	}

	@Override
	public void getIngredients(IIngredients ingredients) {

		ingredients.setInputs(ItemStack.class, inputs);
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

		super.drawInfo(minecraft, recipeWidth, recipeHeight, mouseX, mouseY);

		if (maxEnergy > 0) {
			minecraft.fontRenderer.drawString(StringHelper.formatNumber(maxEnergy) + " RF", 96, 10 + (recipeHeight - 9) / 2, 0xD00000);
		}
	}

}
