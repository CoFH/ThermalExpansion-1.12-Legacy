package cofh.thermalexpansion.plugins.ee3;

import cofh.asm.relauncher.Strippable;
import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalexpansion.item.TEAugments;
import cofh.thermalexpansion.plugins.nei.handlers.NEIRecipeWrapper;
import cofh.thermalexpansion.util.crafting.ChargerManager;
import cofh.thermalexpansion.util.crafting.CrucibleManager;
import cofh.thermalexpansion.util.crafting.FurnaceManager;
import cofh.thermalexpansion.util.crafting.InsolatorManager;
import cofh.thermalexpansion.util.crafting.PulverizerManager;
import cofh.thermalexpansion.util.crafting.SawmillManager;
import cofh.thermalexpansion.util.crafting.SmelterManager;
import cofh.thermalexpansion.util.crafting.TransposerManager;
import cofh.thermalfoundation.plugins.ee3.EE3Helper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

@SuppressWarnings("UnusedDeclaration")
public class EE3Plugin {
    public static void preInit() {

    }

    public static void initialize() {

    }

    public static void postInit() {

    }


    private static int MAX_SECONDARD_MOD;

    static {
        MAX_SECONDARD_MOD = 100;
        for (int i : TEAugments.MACHINE_SECONDARY_MOD) {
            MAX_SECONDARD_MOD -= i;
        }
    }

    @Strippable("mod:EE3")
    public static void loadComplete() throws Throwable {
        for (ChargerManager.RecipeCharger recipe : ChargerManager.getRecipeList()) {
            EE3Helper.addRecipe(recipe.getOutput(), recipe.getInput());
        }

        for (CrucibleManager.RecipeCrucible recipe : CrucibleManager.getRecipeList()) {
            EE3Helper.addRecipe(recipe.getOutput(), recipe.getInput());
        }

        for (FurnaceManager.RecipeFurnace recipe : FurnaceManager.getRecipeList()) {
            EE3Helper.addRecipe(recipe.getOutput(), recipe.getInput());
        }

        for (InsolatorManager.RecipeInsolator recipe : InsolatorManager.getRecipeList()) {
            addTE3Machine(recipe.getPrimaryOutput(), recipe.getSecondaryOutput(), recipe.getSecondaryOutputChance(), recipe.getPrimaryInput(), recipe.getSecondaryInput());
        }

        for (PulverizerManager.RecipePulverizer recipe : PulverizerManager.getRecipeList()) {
            addTE3Machine(recipe.getPrimaryOutput(), recipe.getSecondaryOutput(), recipe.getSecondaryOutputChance(), recipe.getInput());
        }


        for (SawmillManager.RecipeSawmill recipe : SawmillManager.getRecipeList()) {
            addTE3Machine(recipe.getPrimaryOutput(), recipe.getSecondaryOutput(), recipe.getSecondaryOutputChance(), recipe.getInput());
        }

        for (SmelterManager.RecipeSmelter recipe : SmelterManager.getRecipeList()) {
            addTE3Machine(recipe.getPrimaryOutput(), recipe.getSecondaryOutput(), recipe.getSecondaryOutputChance(), recipe.getPrimaryInput(), recipe.getSecondaryInput());
        }

        // TODO: Add support for 'chance' parameter
        for (TransposerManager.RecipeTransposer recipe : TransposerManager.getFillRecipeList()) {
            EE3Helper.addRecipe(recipe.getOutput(), recipe.getInput(), recipe.getFluid());
        }

        for (TransposerManager.RecipeTransposer recipe : TransposerManager.getExtractionRecipeList()) {
            EE3Helper.addRecipe(recipe.getFluid(), recipe.getInput());
            EE3Helper.addRecipe(recipe.getOutput(), recipe.getInput());
        }

        // Add the recipes we hide from NEI
        for (IRecipe recipe : NEIRecipeWrapper.originalRecipeList) {
            ItemStack output = recipe.getRecipeOutput();
            if (output == null) continue;

            Object[] inputs;

            if (recipe instanceof ShapedRecipes) {
                ShapedRecipes shapedRecipe = (ShapedRecipes) recipe;
                inputs = shapedRecipe.recipeItems;
            } else if (recipe instanceof ShapelessRecipes) {
                ShapelessRecipes shapelessRecipe = (ShapelessRecipes) recipe;
                inputs = shapelessRecipe.recipeItems.toArray();
            } else if (recipe instanceof ShapedOreRecipe) {
                ShapedOreRecipe shapedOreRecipe = (ShapedOreRecipe) recipe;
                inputs = shapedOreRecipe.getInput();
            } else if (recipe instanceof ShapelessOreRecipe) {
                ShapelessOreRecipe shapelessOreRecipe = (ShapelessOreRecipe) recipe;
                inputs = shapelessOreRecipe.getInput().toArray();
            } else
                continue;

            EE3Helper.addRecipe(output, inputs);
        }
    }


    public static void addTE3Machine(ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance, ItemStack... inputs) throws Throwable {
        if (secondaryOutput == null) {
            EE3Helper.addRecipe(primaryOutput, inputs);
        } else if (ItemHelper.itemsEqualWithMetadata(primaryOutput, secondaryOutput)) {
            double prob = primaryOutput.stackSize + secondaryOutput.stackSize * getTE3Prob(secondaryChance);
            EE3Helper.addProbabilisticRecipe(ItemHelper.cloneStack(primaryOutput, 1), prob, inputs);
        } else {
            EE3Helper.addRecipe(primaryOutput, inputs);
            EE3Helper.addProbabilisticRecipe(secondaryOutput, getTE3Prob(secondaryChance), inputs);
        }
    }

    // Calculates probability of getting the output with the max augment
    public static double getTE3Prob(int recipeChance) {
        return recipeChance > MAX_SECONDARD_MOD ? 1.0 : (double) recipeChance / MAX_SECONDARD_MOD;
    }

}
