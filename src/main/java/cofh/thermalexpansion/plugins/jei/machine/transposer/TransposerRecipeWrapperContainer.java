package cofh.thermalexpansion.plugins.jei.machine.transposer;

import cofh.thermalexpansion.block.machine.TileTransposer;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.util.managers.machine.TransposerManager;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawableAnimated.StartDirection;
import mezz.jei.api.gui.IDrawableStatic;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TransposerRecipeWrapperContainer extends TransposerRecipeWrapper {

	public TransposerRecipeWrapperContainer(IGuiHelper guiHelper, ItemStack container, String uIdIn) {

		uId = uIdIn;

		List<ItemStack> recipeInputs = new ArrayList<>();
		List<ItemStack> recipeOutputs = new ArrayList<>();
		List<FluidStack> recipeFluids = new ArrayList<>();

		if (uId.equals(RecipeUidsTE.TRANSPOSER_FILL)) {
			ItemStack filledStack = container.copy();
			IFluidHandlerItem handler = filledStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
			for (Fluid fluid : FluidRegistry.getRegisteredFluids().values()) {
				int fill = handler.fill(new FluidStack(fluid, Fluid.BUCKET_VOLUME), true);
				if (fill > 0) {
					FluidStack filledFluid = new FluidStack(fluid, fill);
					filledStack = handler.getContainer();
					recipeInputs.add(container);
					recipeOutputs.add(filledStack);
					recipeFluids.add(filledFluid);
				}
			}
		} else {
			ItemStack filledStack = container.copy();
			IFluidHandlerItem handler = filledStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
			for (Fluid fluid : FluidRegistry.getRegisteredFluids().values()) {
				int fill = handler.fill(new FluidStack(fluid, Fluid.BUCKET_VOLUME), true);
				if (fill > 0) {
					filledStack = handler.getContainer().copy();
					FluidStack drainedFluid = handler.drain(Fluid.BUCKET_VOLUME, true);
					if (drainedFluid != null) {
						ItemStack drainedStack = handler.getContainer();
						recipeInputs.add(filledStack);
						recipeOutputs.add(drainedStack);
						recipeFluids.add(drainedFluid);
					}
				}
			}
		}
		inputs = Collections.singletonList(recipeInputs);
		outputs = recipeOutputs;

		if (uId.equals(RecipeUidsTE.TRANSPOSER_FILL)) {
			inputFluids = recipeFluids;
			outputFluids = Collections.emptyList();
		} else {
			inputFluids = Collections.emptyList();
			outputFluids = recipeFluids;
		}
		energy = TransposerManager.DEFAULT_ENERGY;

		if (uId.equals(RecipeUidsTE.TRANSPOSER_FILL)) {
			progressBack = Drawables.getDrawables(guiHelper).getProgressLeft(Drawables.PROGRESS_DROP);

			IDrawableStatic fluidDrawable = Drawables.getDrawables(guiHelper).getProgressLeft(Drawables.PROGRESS_DROP);
			IDrawableStatic progressDrawable = Drawables.getDrawables(guiHelper).getProgressLeftFill(Drawables.PROGRESS_DROP);

			fluid = guiHelper.createAnimatedDrawable(fluidDrawable, energy / TileTransposer.basePower, StartDirection.RIGHT, true);
			progress = guiHelper.createAnimatedDrawable(progressDrawable, energy / TileTransposer.basePower, StartDirection.RIGHT, false);
		} else {
			progressBack = Drawables.getDrawables(guiHelper).getProgress(Drawables.PROGRESS_DROP);

			IDrawableStatic fluidDrawable = Drawables.getDrawables(guiHelper).getProgress(Drawables.PROGRESS_DROP);
			IDrawableStatic progressDrawable = Drawables.getDrawables(guiHelper).getProgressFill(Drawables.PROGRESS_DROP);

			fluid = guiHelper.createAnimatedDrawable(fluidDrawable, energy / TileTransposer.basePower, StartDirection.LEFT, true);
			progress = guiHelper.createAnimatedDrawable(progressDrawable, energy / TileTransposer.basePower, StartDirection.LEFT, false);
		}
		IDrawableStatic speedDrawable = Drawables.getDrawables(guiHelper).getScaleFill(Drawables.SCALE_BUBBLE);
		IDrawableStatic energyDrawable = Drawables.getDrawables(guiHelper).getEnergyFill();

		speed = guiHelper.createAnimatedDrawable(speedDrawable, 1000, StartDirection.TOP, true);
		energyMeter = guiHelper.createAnimatedDrawable(energyDrawable, 1000, StartDirection.TOP, true);
	}

}
