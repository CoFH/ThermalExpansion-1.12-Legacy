package thermalexpansion.util;

import cofh.util.ItemHelper;
import cofh.util.StringHelper;
import cofh.util.inventory.InventoryCraftingFalse;
import cofh.util.oredict.OreDictionaryArbiter;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;

import java.util.List;
import java.util.Map;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import thermalexpansion.item.ItemDiagram;
import thermalexpansion.item.TEItems;

public class SchematicHelper {

	private SchematicHelper() {

	}

	/* MUST BE PASSED AN INVENTORY WITH 9 SLOTS! */
	public static NBTTagCompound getNBTForSchematic(IInventory craftSlots, ItemStack output) {

		NBTTagCompound nbt = new NBTTagCompound();
		for (int i = 0; i < 9 && i < craftSlots.getSizeInventory(); i++) {
			if (craftSlots.getStackInSlot(i) == null) {
				nbt.removeTag("Slot" + i);
				nbt.removeTag("Name" + i);
				nbt.removeTag("Ore" + i);
			} else {
				NBTTagCompound itemTag = new NBTTagCompound();
				craftSlots.getStackInSlot(i).writeToNBT(itemTag);
				nbt.setTag("Slot" + i, itemTag);
				nbt.setString("Name" + i, craftSlots.getStackInSlot(i).getDisplayName());
				String oreName = ItemHelper.getOreName(craftSlots.getStackInSlot(i));

				if (!oreName.equals(OreDictionaryArbiter.UNKNOWN) && !ItemHelper.isBlacklist(output)) {
					nbt.setString("Ore" + i, oreName);
				}
			}
		}
		nbt.setString("Output", output.stackSize + "x " + output.getDisplayName());
		return nbt;
	}

	public static ItemStack writeNBTToSchematic(ItemStack schematic, NBTTagCompound nbt) {

		ItemStack returnStack = schematic.copy();
		returnStack.setTagCompound(new NBTTagCompound());
		returnStack.stackTagCompound = nbt;
		return returnStack;
	}

	public static String getOutputName(ItemStack stack) {

		if (stack.stackTagCompound != null && stack.stackTagCompound.hasKey("Output")) {
			return ": " + stack.stackTagCompound.getString("Output");
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
		if (schematic.stackTagCompound != null && schematic.stackTagCompound.hasKey("Slot" + slot)) {
			return ItemStack.loadItemStackFromNBT(schematic.stackTagCompound.getCompoundTag("Slot" + slot));
		}
		return null;
	}

	public static String getSchematicOreSlot(ItemStack schematic, int slot) {

		if (schematic.stackTagCompound != null && schematic.stackTagCompound.hasKey("Ore" + slot)) {
			return schematic.stackTagCompound.getString("Ore" + slot);
		}
		return null;
	}

	public static boolean isSchematic(ItemStack stack) {

		return stack == null ? false : stack.getUnlocalizedName().contentEquals(TEItems.diagramSchematic.getUnlocalizedName())
				&& ItemHelper.getItemDamage(stack) == ItemDiagram.Types.SCHEMATIC.ordinal();
	}

	/**
	 * Add schematic information. Validity not checked.
	 */
	public static void addSchematicInformation(ItemStack stack, List<String> list) {

		if (stack.stackTagCompound == null) {
			list.add(StringHelper.getInfoText("info.cofh.blank"));
			return;
		}
		list.add(StringHelper.getDeactivationText("info.thermalexpansion.diagram.0"));
		boolean hasOre = false;
		TMap<String, Integer> aMap = new THashMap<String, Integer>();
		String curName;

		for (int i = 0; i < 9; i++) {
			if (stack.stackTagCompound.hasKey("Name" + i)) {
				if (stack.stackTagCompound.hasKey("Ore" + i)) {
					hasOre = true;
					if (StringHelper.isShiftKeyDown()) {
						curName = stack.stackTagCompound.getString("Ore" + i);
						if (aMap.containsKey(curName)) {
							aMap.put(curName, aMap.get(curName) + 1);
						} else {
							aMap.put(curName, 1);
						}
					}
				} else {
					curName = stack.stackTagCompound.getString("Name" + i);
					if (aMap.containsKey(curName)) {
						aMap.put(curName, aMap.get(curName) + 1);
					} else {
						aMap.put(curName, 1);
					}
				}
			}
		}
		for (Map.Entry<String, Integer> entry : aMap.entrySet()) {
			list.add(StringHelper.LIGHT_GRAY + entry.getValue() + "x " + entry.getKey());
		}
		if (hasOre && StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
			list.add(StringHelper.shiftForDetails());
		}
	}

}
