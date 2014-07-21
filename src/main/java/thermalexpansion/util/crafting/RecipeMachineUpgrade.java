package thermalexpansion.util.crafting;

import cofh.util.ItemHelper;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

import thermalexpansion.block.machine.ItemBlockMachine;

public class RecipeMachineUpgrade extends ShapedOreRecipe {

	byte level = 0;
	byte targetSlot = 4;

	public RecipeMachineUpgrade(int level, ItemStack result, Object[] recipe) {

		super(result, recipe);
		this.level = (byte) level;
	}

	public RecipeMachineUpgrade(int level, int targetSlot, ItemStack result, Object[] recipe) {

		super(result, recipe);
		this.level = (byte) level;
		this.targetSlot = (byte) targetSlot;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting craftMatrix) {

		ItemStack machine = craftMatrix.getStackInSlot(targetSlot);

		if (machine == null) {
			return super.getCraftingResult(craftMatrix);
		}
		ItemStack newMachine = ItemHelper.copyTag(getRecipeOutput().copy(), machine);
		newMachine.setItemDamage(ItemHelper.getItemDamage(machine));

		if (newMachine.stackTagCompound == null) {
			ItemBlockMachine.setDefaultTag(newMachine);
		}
		byte machineLevel = ItemBlockMachine.getLevel(machine);

		if (level <= machineLevel) {
			return null;
		}
		newMachine.stackTagCompound.setByte("Level", level);

		return newMachine;
	}

}
