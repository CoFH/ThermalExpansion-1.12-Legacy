package thermalexpansion.plugins.nei.handlers;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

// Simple wrapper class to hide recipes from NEI
public class NEIRecipeWrapper implements IRecipe {
    private final IRecipe recipe;

    public static void addRecipe(IRecipe recipe) {
        GameRegistry.addRecipe(wrap(recipe));
    }

    public static IRecipe wrap(IRecipe recipe) {
        if (Loader.isModLoaded("NotEnoughItems"))
            return new NEIRecipeWrapper(recipe);
        else
            return recipe;
    }


    private NEIRecipeWrapper(IRecipe recipe) {
        this.recipe = recipe;
    }

    public IRecipe getWrappedRecipe() {
        return recipe;
    }

    @Override
    public boolean matches(InventoryCrafting p_77569_1_, World p_77569_2_) {
        return recipe.matches(p_77569_1_, p_77569_2_);
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting p_77572_1_) {
        return recipe.getCraftingResult(p_77572_1_);
    }

    @Override
    public int getRecipeSize() {
        return recipe.getRecipeSize();
    }

    @Override
    public ItemStack getRecipeOutput() {
        return recipe.getRecipeOutput();
    }
}
