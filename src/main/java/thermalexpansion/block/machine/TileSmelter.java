package thermalexpansion.block.machine;

import cofh.util.BlockHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.util.crafting.SmelterManager;
import thermalexpansion.util.crafting.SmelterManager.RecipeSmelter;

public class TileSmelter extends TileMachineEnergized {

	public static final int TYPE = BlockMachine.Types.SMELTER.ordinal();

	public static void initialize() {

		sideData[TYPE] = new SideConfig();
		sideData[TYPE].numGroup = 7;
		sideData[TYPE].slotGroups = new int[][] { {}, { 0, 1 }, { 2, 3 }, { 4 }, { 2, 3, 4 }, { 0 }, { 1 } };
		sideData[TYPE].allowInsertion = new boolean[] { false, true, false, false, false, true, true };
		sideData[TYPE].allowExtraction = new boolean[] { false, false, true, true, true, false, false };
		sideData[TYPE].sideTex = new int[] { 0, 1, 2, 3, 4, 5, 6 };

		energyData[TYPE] = new EnergyConfig();
		energyData[TYPE].setEnergyParams(40);

		guiIds[TYPE] = ThermalExpansion.proxy.registerGui("Smelter", "machine", true);
		GameRegistry.registerTileEntity(TileSmelter.class, "thermalexpansion.Smelter");
	}

	int outputTrackerPrimary;
	int outputTrackerSecondary;

	public TileSmelter() {

		super();

		sideCache = new byte[] { 3, 3, 2, 2, 2, 2 };
		inventory = new ItemStack[2 + 2 + 1 + 1];
	}

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	public int getMaxInputSlot() {

		return 1;
	}

	@Override
	public boolean canStart() {

		if (inventory[0] == null && inventory[1] == null) {
			return false;
		}
		RecipeSmelter recipe = SmelterManager.getRecipe(inventory[0], inventory[1]);

		if (recipe == null || energyStorage.getEnergyStored() < recipe.getEnergy()) {
			return false;
		}
		if (SmelterManager.isRecipeReversed(inventory[0], inventory[1])) {
			if (recipe.getPrimaryInput().stackSize > inventory[1].stackSize || recipe.getSecondaryInput().stackSize > inventory[0].stackSize) {
				return false;
			}
		} else {
			if (recipe.getPrimaryInput().stackSize > inventory[0].stackSize || recipe.getSecondaryInput().stackSize > inventory[1].stackSize) {
				return false;
			}
		}
		ItemStack primaryItem = recipe.getPrimaryOutput();
		ItemStack secondaryItem = recipe.getSecondaryOutput();

		if (secondaryItem != null && inventory[4] != null) {
			if (!inventory[4].isItemEqual(secondaryItem)) {
				return false;
			}
			if (inventory[4].stackSize + secondaryItem.stackSize > secondaryItem.getMaxStackSize()) {
				return false;
			}
		}
		if (inventory[2] == null || inventory[3] == null) {
			return true;
		}
		if (!inventory[2].isItemEqual(primaryItem) && !inventory[3].isItemEqual(primaryItem)) {
			return false;
		}
		if (!inventory[2].isItemEqual(primaryItem)) {
			return inventory[3].stackSize + primaryItem.stackSize <= primaryItem.getMaxStackSize();
		}
		if (!inventory[3].isItemEqual(primaryItem)) {
			return inventory[2].stackSize + primaryItem.stackSize <= primaryItem.getMaxStackSize();
		}
		return inventory[2].stackSize + inventory[3].stackSize + primaryItem.stackSize <= primaryItem.getMaxStackSize() * 2;
	}

	@Override
	protected boolean hasValidInput() {

		RecipeSmelter recipe = SmelterManager.getRecipe(inventory[0], inventory[1]);

		if (recipe == null) {
			return false;
		}
		if (SmelterManager.isRecipeReversed(inventory[0], inventory[1])) {
			if (recipe.getPrimaryInput().stackSize > inventory[1].stackSize || recipe.getSecondaryInput().stackSize > inventory[0].stackSize) {
				return false;
			}
		} else {
			if (recipe.getPrimaryInput().stackSize > inventory[0].stackSize || recipe.getSecondaryInput().stackSize > inventory[1].stackSize) {
				return false;
			}
		}
		return true;
	}

	@Override
	protected void processStart() {

		processMax = SmelterManager.getRecipe(inventory[0], inventory[1]).getEnergy();
		processRem = processMax;
	}

	@Override
	protected void processFinish() {

		RecipeSmelter recipe = SmelterManager.getRecipe(inventory[0], inventory[1]);
		ItemStack primaryItem = recipe.getPrimaryOutput();
		ItemStack secondaryItem = recipe.getSecondaryOutput();

		if (inventory[2] == null) {
			inventory[2] = primaryItem;
		} else if (inventory[2].isItemEqual(primaryItem)) {
			if (inventory[2].stackSize + primaryItem.stackSize <= primaryItem.getMaxStackSize()) {
				inventory[2].stackSize += primaryItem.stackSize;
			} else {
				int overflow = primaryItem.getMaxStackSize() - inventory[2].stackSize;
				inventory[2].stackSize += overflow;

				if (inventory[3] == null) {
					inventory[3] = primaryItem;
					inventory[3].stackSize = primaryItem.stackSize - overflow;
				} else {
					inventory[3].stackSize += primaryItem.stackSize - overflow;
				}
			}
		} else {
			if (inventory[3] == null) {
				inventory[3] = primaryItem;
			} else {
				inventory[3].stackSize += primaryItem.stackSize;
			}
		}
		if (secondaryItem != null) {
			if (worldObj.rand.nextInt(CHANCE) < recipe.getSecondaryOutputChance()) {
				if (inventory[4] == null) {
					inventory[4] = secondaryItem;
				} else {
					inventory[4].stackSize += secondaryItem.stackSize;
				}
			}
		}
		if (SmelterManager.isRecipeReversed(inventory[0], inventory[1])) {
			inventory[1].stackSize -= recipe.getPrimaryInput().stackSize;
			inventory[0].stackSize -= recipe.getSecondaryInput().stackSize;
		} else {
			inventory[0].stackSize -= recipe.getPrimaryInput().stackSize;
			inventory[1].stackSize -= recipe.getSecondaryInput().stackSize;
		}
		if (inventory[0].stackSize <= 0) {
			inventory[0] = null;
		}
		if (inventory[1].stackSize <= 0) {
			inventory[1] = null;
		}
	}

	@Override
	protected void transferProducts() {

		if (!upgradeAutoTransfer) {
			return;
		}
		int side;
		if (inventory[2] != null && inventory[3] != null) {
			for (int i = outputTrackerPrimary + 1; i <= outputTrackerPrimary + 6; i++) {
				side = i % 6;

				if (sideCache[side] == 2 || sideCache[side] == 4) {
					if (transferItem(2, 4, side)) {
						if (!transferItem(3, 4, side)) {
							transferItem(2, 4, side);
						}
						outputTrackerPrimary = side;
						break;
					} else if (transferItem(3, 8, side)) {
						outputTrackerPrimary = side;
						break;
					}
				}
			}
		}
		if (inventory[4] == null) {
			return;
		}
		for (int i = outputTrackerSecondary + 1; i <= outputTrackerSecondary + 6; i++) {
			side = i % 6;

			if (sideCache[side] == 3 || sideCache[side] == 4) {
				if (transferItem(4, 4, side)) {
					outputTrackerSecondary = side;
					break;
				}
			}
		}
	}

	@Override
	public boolean canAcceptItem(ItemStack stack, int slot, int side) {

		return slot == 0 ? SmelterManager.isItemValid(stack) : true;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		outputTrackerPrimary = nbt.getInteger("Tracker1");
		outputTrackerSecondary = nbt.getInteger("Tracker2");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("Tracker1", outputTrackerPrimary);
		nbt.setInteger("Tracker2", outputTrackerSecondary);
	}

	/* IReconfigurableFacing */
	@Override
	public boolean setFacing(int side) {

		if (side < 0 || side > 5) {
			return false;
		}
		sideCache[side] = 0;
		sideCache[BlockHelper.SIDE_OPPOSITE[side]] = 6;
		sideCache[BlockHelper.SIDE_LEFT[side]] = 5;
		facing = (byte) side;
		sendUpdatePacket(Side.CLIENT);
		return true;
	}

}
