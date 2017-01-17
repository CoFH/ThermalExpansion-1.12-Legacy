package cofh.thermalexpansion.util.crafting;

import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalexpansion.block.machine.ItemBlockMachine;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraftforge.oredict.ShapedOreRecipe;

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
		byte machineLevel = ItemBlockMachine.getLevel(machine);

		if (level != machineLevel + 1) {
			return null;
		}
		ItemStack newMachine = ItemHelper.copyTag(getRecipeOutput().copy(), machine);
		newMachine.setItemDamage(ItemHelper.getItemDamage(machine));
		newMachine.getTagCompound().setByte("Level", level);

		return newMachine;
	}

	public static ItemStack getMachineLevel(ItemStack stack, int level) {

		ItemStack newMachine = stack.copy();
		newMachine.setTagInfo("Level", new NBTTagByte((byte) level));
		return newMachine;
	}

}
