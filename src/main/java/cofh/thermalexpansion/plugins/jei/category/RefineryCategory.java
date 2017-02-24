package cofh.thermalexpansion.plugins.jei.category;

import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.gui.client.machine.GuiRefinery;
import cofh.thermalexpansion.plugins.jei.JeiPluginTE;
import cofh.thermalexpansion.plugins.jei.MachineRecipeWrapper;
import cofh.thermalexpansion.plugins.jei.gui.*;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class RefineryCategory extends CategoryBase {

	private int cycleTicks = 0;
	private IGuiResource powerBar;
	private InfusionProgress progress;
	private IProgressable working;

	public RefineryCategory() {

		super("thermalexpansion.refinery", "refining");
		powerBar = new PowerBar(8, 6);
		progress = new InfusionProgress(75, 32, false);
		working = Progressable.burning(47, 51, false);
	}

	@Override
	public ItemStack getCraftingItem() {

		return BlockMachine.machineRefinery;
	}

	@Override
	public void registerClickToShowCategoryAreas(IModRegistry registry) {

		registry.addRecipeClickArea(GuiRefinery.class, 75, 30, 25, 25, uId);
	}

	@Override
	public void drawExtras(Minecraft minecraft) {

		if (cycleTicks++ / 120 >= 6) {
			cycleTicks = 0;
		}

		//draws charging slot.
		ResourceUtils.drawScaledTexturedRectFromModel(7, 50, 132, 96, 18, 18, JeiPluginTE.JEI_HANDLER_LOCATION);
		//draws input fluid tank container tank.
		ResourceUtils.drawScaledTexturedRectFromModel(45, 17, 96, 160, 18, 31, JeiPluginTE.JEI_HANDLER_LOCATION);
		ResourceUtils.drawScaledTexturedRectFromModel(45, 17, 128, 161, 16, 29, JeiPluginTE.JEI_HANDLER_LOCATION);
		//draw the output slot
		ResourceUtils.drawScaledTexturedRectFromModel(111, 26, 150, 96, 26, 26, JeiPluginTE.JEI_HANDLER_LOCATION);
		//draws the output fluid tank container.
		ResourceUtils.drawScaledTexturedRectFromModel(150, 8, 32, 96, 18, 62, JeiPluginTE.JEI_HANDLER_LOCATION);
		ResourceUtils.drawScaledTexturedRectFromModel(151, 8, 64, 96, 16, 61, JeiPluginTE.JEI_HANDLER_LOCATION);

		//draws animations.
		powerBar.draw(cycleTicks / 120);
		working.draw(cycleTicks / 6);
		progress.draw(cycleTicks % 120);

	}

	@Override
	public List<String> getTooltips(int mouseX, int mouseY) {

		if (powerBar.inBounds(mouseX, mouseY)) {
			return powerBar.getTooltip(20);
		}

		if (progress.inBounds(mouseX, mouseY)) {
			return progress.getTooltip();
		}

		if (working.inBounds(mouseX, mouseY)) {
			return working.getTooltip();
		}

		return null;
	}

	@ParametersAreNonnullByDefault
	@Override
	public void setRecipe(IRecipeLayout recipeLayout, MachineRecipeWrapper recipeWrapper, IIngredients ingredients) {

		powerBar.setRequiredResource(recipeWrapper.getEnergyRequired());
		working.setDuration(recipeWrapper.getDuration());
		progress.setDuration(recipeWrapper.getDuration());
		progress.setFluid(ingredients.getOutputs(FluidStack.class).get(0));

		IGuiFluidStackGroup fluidStackGroup = recipeLayout.getFluidStacks();
		fluidStackGroup.init(0, false, 46, 18, 16, 29, 10000, true, null);
		fluidStackGroup.init(1, false, 151, 9, 16, 60, 10000, true, null);
		fluidStackGroup.set(0, ingredients.getInputs(FluidStack.class).get(0));
		fluidStackGroup.set(1, ingredients.getOutputs(FluidStack.class));

		IGuiItemStackGroup itemStackGroup = recipeLayout.getItemStacks();
		itemStackGroup.init(0, false, 115, 30);
		itemStackGroup.set(0, ingredients.getOutputs(ItemStack.class).get(0));
	}
}
