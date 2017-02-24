package cofh.thermalexpansion.plugins.jei;

import cofh.thermalexpansion.plugins.jei.Catagory.Categories;
import cofh.thermalexpansion.plugins.jei.Catagory.CategoryBase;
import cofh.thermalexpansion.util.crafting.*;
import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@JEIPlugin
public class JeiPlugin extends BlankModPlugin {
    public static final ResourceLocation JEI_HANDLER_LOCATION = new ResourceLocation("thermalexpansion:textures/gui/jei_handler.png");

    @Override
    public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {
        //TODO: subtypes for cells and capacitors once they're in.
    }

    @Override
    public void register(IModRegistry registry) {
        registry.addRecipeHandlers(new RecipeHandler());

        for (Categories type : Categories.values()) {

            CategoryBase category = type.getCategory();
            registry.addRecipeCategories(category);
            registry.addRecipeCategoryCraftingItem(category.getCraftingItem(), category.getUid());
            category.registerClickToShowCategoryAreas(registry);

        }
        List<MachineRecipeWrapper> recipes = new ArrayList<MachineRecipeWrapper>();

        //pulverizer
        for (PulverizerManager.RecipePulverizer recipe : PulverizerManager.getRecipeList()) {
            int chance = recipe.getSecondaryOutputChance();
            List<ItemStack> input = Collections.singletonList(recipe.getInput());
            List<ItemStack> outputs = chance == 0 ? Collections.singletonList(recipe.getPrimaryOutput()) : Arrays.asList(recipe.getPrimaryOutput(), recipe.getSecondaryOutput());

            int duration = recipe.getEnergy() / 20;

            recipes.add(new MachineRecipeWrapper(input, outputs, Categories.Pulverizer, recipe.getEnergy(), duration, chance));
        }

        //powered.furnace
        for (FurnaceManager.RecipeFurnace recipe : FurnaceManager.getRecipeList()) {
            List<ItemStack> inputs = Collections.singletonList(recipe.getInput());
            List<ItemStack> outputs = Collections.singletonList(recipe.getOutput());
            int duration = recipe.getEnergy() / 20;
            recipes.add(new MachineRecipeWrapper(inputs, outputs, Categories.PoweredFurnace, recipe.getEnergy(), duration, 0));
        }

        //magma.crucible
        for (CrucibleManager.RecipeCrucible recipe : CrucibleManager.getRecipeList()) {
            List<ItemStack> input = Collections.singletonList(recipe.getInput());
            List<FluidStack> output = Collections.singletonList(recipe.getOutput());
            int duration = recipe.getEnergy() / 50;
            recipes.add(new MachineRecipeWrapper(input, output, Categories.Crucible, recipe.getEnergy(), duration, 0));
        }

        //sawmill
        for (SawmillManager.RecipeSawmill recipe : SawmillManager.getRecipeList()) {
            int chance = recipe.getSecondaryOutputChance();
            List<ItemStack> input = Collections.singletonList(recipe.getInput());
            List<ItemStack> outputs = chance == 0 ? Collections.singletonList(recipe.getPrimaryOutput()) : Arrays.asList(recipe.getPrimaryOutput(), recipe.getSecondaryOutput());

            int duration = recipe.getEnergy() / 20;

            recipes.add(new MachineRecipeWrapper(input, outputs, Categories.Sawmill, recipe.getEnergy(), duration, chance));
        }

        //induc.smelter
        for (SmelterManager.RecipeSmelter recipe : SmelterManager.getRecipeList()) {
            int chance = recipe.getSecondaryOutputChance();
            List<ItemStack> input = Arrays.asList(recipe.getPrimaryInput(), recipe.getSecondaryInput());
            List<ItemStack> outputs = chance == 0 ? Collections.singletonList(recipe.getPrimaryOutput()) : Arrays.asList(recipe.getPrimaryOutput(), recipe.getSecondaryOutput());

            int duration = recipe.getEnergy() / 20;

            recipes.add(new MachineRecipeWrapper(input, outputs, Categories.Smelter, recipe.getEnergy(), duration, chance));
        }

        //trasposer
        for (TransposerManager.RecipeTransposer recipe : TransposerManager.getExtractionRecipeList()) {
            int chance = recipe.getChance();
            List<ItemStack> input = Collections.singletonList(recipe.getInput());
            List<ItemStack> outputs = chance == 0 ? Collections.<ItemStack>emptyList() : Collections.singletonList(recipe.getOutput());

            int duration = recipe.getEnergy() / 20;

            recipes.add(new MachineRecipeWrapper(input, outputs, Categories.TransposerExtraction, recipe.getEnergy(), duration, chance));
        }
        for (TransposerManager.RecipeTransposer recipe : TransposerManager.getFillRecipeList()) {
            int chance = recipe.getChance();
            List<Object> input = Arrays.asList(recipe.getInput(), recipe.getFluid());
            List<?> outputs = chance == 0 ? Collections.singletonList(recipe.getFluid()) : Arrays.asList(recipe.getFluid(), recipe.getOutput());

            int duration = recipe.getEnergy() / 20;

            recipes.add(new MachineRecipeWrapper(input, outputs, Categories.TransposerFilling, recipe.getEnergy(), duration, chance));
        }

        //compactor
        for (CompactorManager.RecipeCompactor recipe : CompactorManager.getRecipeList()) {
            List<ItemStack> inputs = Collections.singletonList(recipe.getInput());
            List<ItemStack> outputs = Collections.singletonList(recipe.getOutput());
            int duration = recipe.getEnergy() / 20;
            recipes.add(new MachineRecipeWrapper(inputs, outputs, Categories.Compactor, recipe.getEnergy(), duration, 0));
        }

        //refinery
        for (RefineryManager.RecipeRefinery recipe : RefineryManager.getRecipeList()) {
            List<FluidStack> inputs = Collections.singletonList(recipe.getInput());
            List<Object> outputs = Arrays.asList(recipe.getOutputItem(), recipe.getOutputFluid());
            int duration = recipe.getEnergy() / 20;
            recipes.add(new MachineRecipeWrapper(inputs, outputs, Categories.Refinery, recipe.getEnergy(), duration, 0));
        }

        registry.addRecipes(recipes);
    }
}
