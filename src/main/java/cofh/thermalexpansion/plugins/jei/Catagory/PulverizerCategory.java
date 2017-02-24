package cofh.thermalexpansion.plugins.jei.Catagory;

import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.gui.client.machine.GuiPulverizer;
import cofh.thermalexpansion.plugins.jei.JeiPlugin;
import cofh.thermalexpansion.plugins.jei.MachineRecipeWrapper;
import cofh.thermalexpansion.plugins.jei.gui.*;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ITooltipCallback;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class PulverizerCategory extends CategoryBase {
    private IGuiResource powerBar;
    private IPrograssable progress;
    private int cycleTicks = 0;
    private IPrograssable working;
    private int secondChance;

    public PulverizerCategory() {
        //TODO:localization
        super("thermalexpansion.pulverizer", "Pulverizing");
        powerBar = new PowerBar(8, 6);
        progress = Progressable.arrow(70, 32, false);
        working = Progressable.crushing(47, 43, false);
    }

    //crafting ItemStack associated with this category.
    @Override
    public ItemStack getCraftingItem() {
        return BlockMachine.machinePulverizer;
    }

    //process to jei category code.
    @Override
    public void registerClickToShowCategoryAreas(IModRegistry registry) {
        registry.addRecipeClickArea(GuiPulverizer.class, 75, 30, 25, 25, uId);
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
        //draws the first half of the output slot.
        ResourceUtils.drawScaledTexturedRectFromModel(105, 20, 150, 96, 24, 26, JeiPlugin.JEI_HANDLER_LOCATION);
        //draws the second half of the output slot.
        ResourceUtils.drawScaledTexturedRectFromModel(129, 20, 154, 96, 24, 26, JeiPlugin.JEI_HANDLER_LOCATION);
        //draws the secondary output slot.
        ResourceUtils.drawScaledTexturedRectFromModel(110, 50, 132, 96, 18, 18, JeiPlugin.JEI_HANDLER_LOCATION);

        //draws animations.
        powerBar.draw(cycleTicks / 120);
        working.draw(cycleTicks / 6);
        progress.draw(cycleTicks % 120);
    }

    @Override
    //tooltips patch till migrated to 1.11.2 jei which gives tooltips to the category.
    public List<String> getTooltips(int mouseX, int mouseY) {
        if (powerBar.inBounds(mouseX, mouseY))
            return powerBar.getTooltips(20);

        if (progress.inBounds(mouseX, mouseY))
            return progress.getTooltips();

        if (working.inBounds(mouseX, mouseY))
            return working.getTooltips();
        //check for secondary output tooltips
        return null;
    }

    //sets the category for displaying a recipe.
    @ParametersAreNonnullByDefault
    @Override
    public void setRecipe(IRecipeLayout recipeLayout, MachineRecipeWrapper recipeWrapper, IIngredients ingredients) {
        powerBar.setRequiredResource(recipeWrapper.getEnergyRequired());
        progress.setDuration(recipeWrapper.getDuration());
        working.setDuration(recipeWrapper.getDuration());
        this.secondChance = recipeWrapper.getChance();

        List<List<ItemStack>> inputs = ingredients.getInputs(ItemStack.class);
        List<ItemStack> outputs = ingredients.getOutputs(ItemStack.class);
        IGuiItemStackGroup stackGroup = recipeLayout.getItemStacks();

        stackGroup.init(0, true, 45, 22);
        stackGroup.init(1, false, 110, 24);
        stackGroup.set(0, inputs.get(0));
        stackGroup.set(1, outputs.get(0));

        if (outputs.size() > 1) {
            stackGroup.init(2, false, 110, 50);
            stackGroup.set(2, outputs.get(1));
            stackGroup.addTooltipCallback(new ITooltipCallback<ItemStack>() {
                @Override
                public void onTooltip(int slotIndex, boolean input, ItemStack ingredient, List<String> tooltip) {
                    if (slotIndex == 2) tooltip.add("chance: " + secondChance + "%");
                }
            });
        }
    }
}
