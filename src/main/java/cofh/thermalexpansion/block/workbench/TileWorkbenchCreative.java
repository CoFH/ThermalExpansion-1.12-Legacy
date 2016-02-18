package cofh.thermalexpansion.block.workbench;

import cofh.core.util.oredict.OreDictionaryArbiter;
import cofh.lib.util.helpers.InventoryHelper;
import cofh.lib.util.helpers.ItemHelper;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.item.ItemStack;

public class TileWorkbenchCreative extends TileWorkbench {

	public static void initialize() {

		GameRegistry.registerTileEntity(TileWorkbenchCreative.class, "thermalexpansion.WorkbenchCreative");
	}

	public TileWorkbenchCreative() {

	}

	public TileWorkbenchCreative(int metadata) {

		super(metadata);
	}

	@Override
	public boolean createItem(boolean doCreate, ItemStack output) {

		ItemStack[] invCopy = InventoryHelper.cloneInventory(inventory);
		ItemStack recipeSlot;
		String recipeOreName;
		@SuppressWarnings("unused")
		boolean found = false;

		for (int i = 0; i < 9; i++) {
			recipeSlot = craftingGrid[i];
			recipeOreName = OreDictionaryArbiter.getOreName(recipeSlot);

			if (recipeSlot != null) {
				for (int j = 0; j < getSizeInventory(); j++) {
					if (invCopy[j] != null && ItemHelper.craftingEquivalent(invCopy[j], recipeSlot, recipeOreName, output)) {
						craftingGrid[i] = ItemHelper.cloneStack(invCopy[j], 1);
						invCopy[j].stackSize--;

						if (invCopy[j].getItem().hasContainerItem(invCopy[j])) {
							ItemStack containerStack = invCopy[j].getItem().getContainerItem(invCopy[j]);

							if (containerStack == null) {
								// this is absolutely stupid and nobody should ever make a container item where this gets called
							} else {
								if (containerStack.isItemStackDamageable() && containerStack.getItemDamage() > containerStack.getMaxDamage()) {
									containerStack = null;
								}
								if (containerStack != null
										&& (!invCopy[j].getItem().doesContainerItemLeaveCraftingGrid(invCopy[j]) || !InventoryHelper.addItemStackToInventory(
												invCopy, containerStack, 3))) {
									if (invCopy[j].stackSize <= 0) {
										invCopy[j] = containerStack;
										if (containerStack.stackSize <= 0) {
											invCopy[j].stackSize = 1;
										}
									} else {
										return false;
									}
								}
							}
						}
						if (invCopy[j].stackSize <= 0) {
							invCopy[j] = null;
						}
						found = true;
						break;
					}
				}
			}
		}
		if (doCreate) {
			// Update the inventories since we can make it.
			inventory = invCopy;
		}
		return true;
	}

	@Override
	public boolean createItemClient(boolean doCreate, ItemStack output) {

		ItemStack[] invCopy = InventoryHelper.cloneInventory(inventory);
		ItemStack recipeSlot;
		String recipeOreName;
		boolean found = false;
		@SuppressWarnings("unused")
		boolean masterFound = true;
		missingItem = new boolean[] { false, false, false, false, false, false, false, false, false };

		for (int i = 0; i < 9; i++) {
			recipeSlot = craftingGrid[i];
			recipeOreName = OreDictionaryArbiter.getOreName(recipeSlot);

			if (recipeSlot != null) {
				for (int j = 0; j < getSizeInventory(); j++) {
					if (invCopy[j] != null && ItemHelper.craftingEquivalent(invCopy[j], recipeSlot, recipeOreName, output)) {
						craftingGrid[i] = ItemHelper.cloneStack(invCopy[j], 1);
						invCopy[j].stackSize--;

						if (invCopy[j].getItem().hasContainerItem(invCopy[j])) {
							ItemStack containerStack = invCopy[j].getItem().getContainerItem(invCopy[j]);

							if (containerStack.isItemStackDamageable() && containerStack.getItemDamage() > containerStack.getMaxDamage()) {
								containerStack = null;
							}
							if (containerStack != null
									&& (!invCopy[j].getItem().doesContainerItemLeaveCraftingGrid(invCopy[j]) || !InventoryHelper.addItemStackToInventory(
											invCopy, containerStack, 2))) {
								if (invCopy[j].stackSize <= 0) {
									invCopy[j] = containerStack;
									if (containerStack.stackSize <= 0) {
										invCopy[j].stackSize = 1;
									}
								} else {
									return false;
								}
							}
						}
						if (invCopy[j].stackSize <= 0) {
							invCopy[j] = null;
						}
						found = true;
						break;
					}
				}
				if (!found) {
					masterFound = false;
					missingItem[i] = true;
				}
				found = false;
			}
		}
		if (doCreate) {
			// Update the inventories since we can make it.
			inventory = invCopy;
		}
		return true;
	}

}
