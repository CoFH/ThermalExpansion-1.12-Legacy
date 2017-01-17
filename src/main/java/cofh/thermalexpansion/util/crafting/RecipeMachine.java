package cofh.thermalexpansion.util.crafting;

import cofh.lib.util.helpers.AugmentHelper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalexpansion.block.simple.BlockFrame;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class RecipeMachine extends ShapedOreRecipe {

	int targetSlot = 4;
	ItemStack[] augments;

	public RecipeMachine(ItemStack result, ItemStack[] augments, Object[] recipe) {

		super(result, recipe);
		this.augments = augments;
	}

	public RecipeMachine(int targetSlot, ItemStack result, ItemStack[] augments, Object[] recipe) {

		super(result, recipe);
		this.targetSlot = targetSlot;
		this.augments = augments;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting craftMatrix) {

		if (craftMatrix.getStackInSlot(targetSlot) == null) {
			return super.getCraftingResult(craftMatrix);
		}
		ItemStack retStack = getRecipeOutput().copy();
		AugmentHelper.writeAugments(retStack, augments);
		retStack.getTagCompound().setByte("Level", getLevel(craftMatrix.getStackInSlot(targetSlot)));

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
		if (ItemHelper.itemsEqualWithMetadata(stack, BlockFrame.frameMachineResonant)) {
			return 3;
		}
		return 0;
	}

}
