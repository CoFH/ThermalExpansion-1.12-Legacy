package cofh.thermalexpansion.plugins.jei.fuels.reactant;

import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.block.dynamo.TileDynamoReactant;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.fuels.BaseFuelWrapper;
import cofh.thermalexpansion.util.fuels.ReactantManager.Reaction;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawableAnimated.StartDirection;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReactantFuelWrapper extends BaseFuelWrapper {

	final List<List<ItemStack>> inputs;
	final List<List<FluidStack>> inputFluids;

	public ReactantFuelWrapper(IGuiHelper guiHelper, Reaction reaction) {

		List<ItemStack> recipeInputs = new ArrayList<>();
		recipeInputs.add(reaction.getReactant());

		List<FluidStack> recipeFluids = new ArrayList<>();
		recipeFluids.add(new FluidStack(reaction.getFluid(), TileDynamoReactant.fluidAmount));

		this.inputs = Collections.singletonList(recipeInputs);
		this.inputFluids = Collections.singletonList(recipeFluids);
		this.energy = reaction.getEnergy();

		IDrawableStatic progressDrawable = Drawables.getDrawables(guiHelper).getScaleFill(Drawables.SCALE_FLAME_GREEN);
		IDrawableStatic energyDrawable = Drawables.getDrawables(guiHelper).getEnergyFill();

		durationFill = guiHelper.createAnimatedDrawable(progressDrawable, energy / TileDynamoReactant.basePower, StartDirection.TOP, true);
		energyMeter = guiHelper.createAnimatedDrawable(energyDrawable, 1000, StartDirection.BOTTOM, false);
	}

	@Override
	public void getIngredients(IIngredients ingredients) {

		ingredients.setInputLists(ItemStack.class, inputs);
		ingredients.setInputLists(FluidStack.class, inputFluids);
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

		durationFill.draw(minecraft, 22, 43);
		energyMeter.draw(minecraft, 71, 7);

		minecraft.fontRendererObj.drawString(StringHelper.formatNumber(energy) + " RF", 96, (recipeHeight - 9) / 2, 0x808080);
		//		minecraft.fontRendererObj.drawString(StringHelper.formatNumber(energy * 10) + " RF", 96, 10 + (recipeHeight - 9) / 2, 0x089e4c);
	}

}
