package thermalexpansion.util;

import cofh.util.ItemHelper;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.ShapedOreRecipe;

import thermalexpansion.block.simple.BlockFrame;

public class RecipeMachine extends ShapedOreRecipe {

	int targetSlot = 4;

	public RecipeMachine(ItemStack result, Object[] recipe) {

		super(result, recipe);
	}

	public RecipeMachine(int upgradeSlot, ItemStack result, Object[] recipe) {

		super(result, recipe);
		this.targetSlot = upgradeSlot;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting craftMatrix) {

		if (craftMatrix.getStackInSlot(targetSlot) == null) {
			return super.getCraftingResult(craftMatrix);
		}
		ItemStack retStack = getRecipeOutput().copy();

		if (retStack.stackTagCompound == null) {
			retStack.setTagCompound(new NBTTagCompound());
		}
		retStack.stackTagCompound.setByte("Level", getLevel(craftMatrix.getStackInSlot(targetSlot)));

		return retStack;
	}

	private byte getLevel(ItemStack stack) {

		if (ItemHelper.itemsEqualWithMetadata(stack, BlockFrame.frameMachineBasic)) {
			return 0;
		}
		if (ItemHelper.itemsEqualWithMetadata(stack, BlockFrame.frameMachineHardened)) {
			return 1;
		}
		if (ItemHelper.itemsEqualWithMetadata(stack, BlockFrame.frameMachineReinforced)) {
			return 2;
		}
		if (ItemHelper.itemsEqualWithMetadata(stack, BlockFrame.frameMachineEnderium)) {
			return 3;
		}
		return 0;
	}

}
