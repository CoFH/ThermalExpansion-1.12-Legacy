package cofh.thermalexpansion.plugins.nei.handlers;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

// Simple wrapper class to hide recipes from NEI
public class NEIRecipeWrapper implements IRecipe {

	public enum RecipeType {
		MACHINE, UPGRADE, SECURE
	};

	private final IRecipe recipe;

	public final RecipeType type;

	public static void addMachineRecipe(IRecipe recipe) {

		GameRegistry.addRecipe(wrap(recipe, RecipeType.MACHINE));
	}

	public static void addUpgradeRecipe(IRecipe recipe) {

		GameRegistry.addRecipe(wrap(recipe, RecipeType.UPGRADE));
	}

	public static void addSecureRecipe(IRecipe recipe) {

		GameRegistry.addRecipe(wrap(recipe, RecipeType.SECURE));
	}

	public final static List<IRecipe> originalRecipeList = new ArrayList<IRecipe>();

	public static IRecipe wrap(IRecipe recipe, RecipeType type) {

		if (Loader.isModLoaded("NotEnoughItems")) {
			originalRecipeList.add(recipe);
			return new NEIRecipeWrapper(recipe, type);
		} else {
			return recipe;
		}
	}

	private NEIRecipeWrapper(IRecipe recipe, RecipeType type) {

		this.recipe = recipe;
		this.type = type;
	}

	public RecipeType getRecipeType() {

		return type;
	}

	public IRecipe getWrappedRecipe() {

		return recipe;
	}

	@Override
	public boolean matches(InventoryCrafting inv, World world) {

		return recipe.matches(inv, world);
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {

		return recipe.getCraftingResult(inv);
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
