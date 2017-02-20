package cofh.thermalexpansion.plugins.jei.Catagory;

import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.gui.client.machine.GuiCrucible;
import cofh.thermalexpansion.plugins.jei.JeiPlugin;
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

public class CrucibleCategory extends CategoryBase {
    private IGuiResource powerBar;
    private InfusionProgress progress;
    private int cycleTicks = 0;
    private IPrograssable working;

    public CrucibleCategory() {
        super("thermalexpansion.crucible", "Magma Crucible");
        powerBar = new PowerBar(8, 6);
        working = Progressable.burning(47, 40, false);
        progress = new InfusionProgress(95, 32, false);
    }

    @Override
    public ItemStack getCraftingItem() {
        return BlockMachine.machineCrucible;
    }

    @Override
    public void registerClickToShowCategoryAreas(IModRegistry registry) {
        registry.addRecipeClickArea(GuiCrucible.class, 96, 30, 30, 24, uId);
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
        if (cycleTicks++ / 120 >= 6) {
            cycleTicks = 0;
        }

        //draws charging slot.
        ResourceUtils.drawScaledTexturedRectFromModel(7, 50, 132, 96, 18, 18, JeiPlugin.JEI_HANDLER_LOCATION);
        //draws input slot.
        ResourceUtils.drawScaledTexturedRectFromModel(45, 22, 132, 96, 18, 18, JeiPlugin.JEI_HANDLER_LOCATION);
        //draws the fluid tank container.
        ResourceUtils.drawScaledTexturedRectFromModel(150, 8, 32, 96, 18, 62, JeiPlugin.JEI_HANDLER_LOCATION);
        ResourceUtils.drawScaledTexturedRectFromModel(151, 8, 80, 96, 18, 62, JeiPlugin.JEI_HANDLER_LOCATION);

        //draws animations.
        powerBar.draw(cycleTicks / 120);
        working.draw(cycleTicks / 6);
        progress.draw(cycleTicks % 120);
    }

    @Override
    public List<String> getTooltips(int mouseX, int mouseY) {

        if (powerBar.inBounds(mouseX, mouseY))
            return powerBar.getTooltips(50);

        if (working.inBounds(mouseX, mouseY))
            return working.getTooltips();

        if (progress.inBounds(mouseX, mouseY))
            return progress.getTooltips();
        return null;
    }

    @ParametersAreNonnullByDefault
    @Override
    public void setRecipe(IRecipeLayout recipeLayout, MachineRecipeWrapper recipeWrapper, IIngredients ingredients) {
        powerBar.setRequiredResource(recipeWrapper.getEnergyRequired());
        progress.setDuration(recipeWrapper.getDuration());
        working.setDuration(recipeWrapper.getDuration());
        progress.setFluid(ingredients.getOutputs(FluidStack.class).get(0));

        IGuiItemStackGroup itemStackGroup = recipeLayout.getItemStacks();
        itemStackGroup.init(0, true, 45, 22);
        itemStackGroup.set(0, ingredients.getInputs(ItemStack.class).get(0));

        IGuiFluidStackGroup fluidStackGroup = recipeLayout.getFluidStacks();
        fluidStackGroup.init(0, false, 151, 9, 16, 60, 10000, true, null);
        fluidStackGroup.set(0, ingredients.getOutputs(FluidStack.class));
    }
}
