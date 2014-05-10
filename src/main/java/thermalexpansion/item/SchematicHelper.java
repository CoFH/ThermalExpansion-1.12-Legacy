package thermalexpansion.item;

import cofh.util.ItemHelper;
import cofh.util.StringHelper;
import cofh.util.inventory.InventoryCraftingFalse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class SchematicHelper {

	/* MUST BE PASSED AN INVENTORY WITH 9 SLOTS! */
	public static NBTTagCompound getNBTForSchematic(IInventory craftSlots, ItemStack output) {

		NBTTagCompound theComp = new NBTTagCompound();
		for (int i = 0; i < 9 && i < craftSlots.getSizeInventory(); i++) {
			if (craftSlots.getStackInSlot(i) != null) {
				theComp.setInteger("Slot" + i + "Id", craftSlots.getStackInSlot(i).itemID);
				theComp.setInteger("Slot" + i + "Meta", craftSlots.getStackInSlot(i).getItemDamage());
				theComp.setString("Slot" + i + "Name", craftSlots.getStackInSlot(i).getDisplayName());
				String OreName = ItemHelper.getOreName(craftSlots.getStackInSlot(i));

				if (!OreName.equals("Unknown") && !ItemHelper.isBlacklist(output)) {
					theComp.setString("Slot" + i + "Ore", OreName);
				}
			} else {
				theComp.setInteger("Slot" + i + "Id", -1);
				theComp.setInteger("Slot" + i + "Meta", -1);
				theComp.setString("Slot" + i + "Name", "");
			}
		}
		theComp.setString("OutputName", output.stackSize + "x " + output.getDisplayName());
		return theComp;
	}

	public static ItemStack getSchematic(NBTTagCompound nbt) {

		ItemStack returnStack = TEItems.diagramSchematic.copy();
		returnStack.stackTagCompound = nbt;
		return returnStack;
	}

	public static String getOutputName(ItemStack stack) {

		if (stack.stackTagCompound != null && stack.stackTagCompound.hasKey("OutputName")) {
			return ": " + stack.stackTagCompound.getString("OutputName");
		}
		return "";
	}

	public static ItemStack getOutput(ItemStack schematic, World world) {

		InventoryCrafting tempCraft = new InventoryCraftingFalse(3, 3);
		for (int i = 0; i < 9; i++) {
			tempCraft.setInventorySlotContents(i, getSchematicSlot(schematic, i));
		}
		return ItemHelper.findMatchingRecipe(tempCraft, world);
	}

	public static ItemStack getSchematicSlot(ItemStack schematic, int slot) {

		if (schematic == null) {
			return null;
		}
		if (schematic.stackTagCompound != null && schematic.stackTagCompound.hasKey("Slot" + slot + "Id") && schematic.stackTagCompound.getInteger("Slot" + slot + "Id") > -1) {
			return new ItemStack(schematic.stackTagCompound.getInteger("Slot" + slot + "Id"), 1, schematic.stackTagCompound.getInteger("Slot" + slot + "Meta"));
		}
		return null;
	}

	public static String getSchematicOreSlot(ItemStack schematic, int slot) {

		if (schematic.stackTagCompound != null && schematic.stackTagCompound.hasKey("Slot" + slot + "Ore")) {
			return schematic.stackTagCompound.getString("Slot" + slot + "Ore");
		}
		return null;
	}

	public static boolean isSchematic(ItemStack stack) {

		return stack == null ? false : stack.itemID == TEItems.diagramSchematic.itemID && stack.getItemDamage() == TEItems.SCHEMATIC_ID;
	}

	/**
	 * 
	 * @param list
	 * @param schematic
	 *            :WARNING: Validity not checked
	 */
	public static void addSchematicInformation(List list, ItemStack schematic) {

		if (schematic.stackTagCompound == null) {
			list.add(StringHelper.getInfoText("info.cofh.blank"));
			return;
		}
		boolean hasOre = false;
		Map<String, Integer> aMap = new HashMap<String, Integer>();
		String curName;
		for (int i = 0; i < 9; i++) {
			if (schematic.stackTagCompound.hasKey("Slot" + i + "Name") && !schematic.stackTagCompound.getString("Slot" + i + "Name").equalsIgnoreCase("")) {
				if (schematic.stackTagCompound.hasKey("Slot" + i + "Ore")) {
					hasOre = true;

					if (StringHelper.isShiftKeyDown()) {
						curName = schematic.stackTagCompound.getString("Slot" + i + "Ore");

						if (aMap.containsKey(curName)) {
							aMap.put(curName, aMap.get(curName) + 1);
						} else {
							aMap.put(curName, 1);
						}
						continue;
					}
				}
				curName = schematic.stackTagCompound.getString("Slot" + i + "Name");

				if (aMap.containsKey(curName)) {
					aMap.put(curName, aMap.get(curName) + 1);
				} else {
					aMap.put(curName, 1);
				}
			}
		}
		for (Map.Entry<String, Integer> entry : aMap.entrySet()) {
			list.add(StringHelper.LIGHT_GRAY + entry.getValue() + "x " + entry.getKey());
		}
		if (hasOre && StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
			list.add(StringHelper.shiftForInfo);
		}
	}

}
