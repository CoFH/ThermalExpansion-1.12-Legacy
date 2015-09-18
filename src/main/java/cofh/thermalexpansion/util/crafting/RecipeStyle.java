package cofh.thermalexpansion.util.crafting;

import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalexpansion.block.light.ItemBlockLight;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.world.World;

public class RecipeStyle extends ShapedRecipes {

	private static ItemStack[] copy(int size, ItemStack d, int style) {

		d = ItemBlockLight.setDefaultTag(d.copy(), style);
		ItemStack[] r = new ItemStack[size];
		for (int i = 0; i < size; ++i) {
			r[i] = d;
		}
		return r;
	}

	protected ItemStack input;
	protected byte style;

	public RecipeStyle(int width, int height, ItemStack input, int style, ItemStack output) {

		super(width, height, copy(width * height, input, style), output.copy());

		this.style = (byte) style;
		this.input = input;
	}

	@Override
	public boolean matches(InventoryCrafting inv, World world) {

		for (int x = 0; x <= 3 - this.recipeWidth; ++x) {
			for (int y = 0; y <= 3 - this.recipeHeight; ++y) {
				if (this.checkMatch(inv, x, y)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean checkMatch(InventoryCrafting inv, int x, int y) {

		for (int left = 0; left < 3; ++left) {
			for (int top = 0; top < 3; ++top) {
				int posX = left - x;
				int posY = top - y;
				ItemStack itemstack = null;

				if ((posX | posY) >= 0 && posX < this.recipeWidth && posY < this.recipeHeight) {
					itemstack = input;
				}

				ItemStack itemstack1 = inv.getStackInRowAndColumn(left, top);
				if (itemstack != itemstack1 && !ItemHelper.itemsEqualForCrafting(itemstack, itemstack1)) {
					return false;
				} else if (itemstack1 != null && itemstack1.stackTagCompound != null) {
					if (itemstack1.stackTagCompound.getByte("Style") != style) {
						return false;
					}
				}
			}
		}
		return true;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {

		return this.getRecipeOutput().copy();
	}

}
