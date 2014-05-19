package thermalexpansion.block.machine;

import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.util.crafting.PulverizerManager;
import thermalexpansion.util.crafting.PulverizerManager.RecipePulverizer;

public class TilePulverizer extends TileMachineEnergized {

	public static final int TYPE = BlockMachine.Types.PULVERIZER.ordinal();

	public static void initialize() {

		sideData[TYPE] = new SideConfig();
		sideData[TYPE].numGroup = 5;
		sideData[TYPE].slotGroups = new int[][] { {}, { 0 }, { 1, 2 }, { 3 }, { 1, 2, 3 } };
		sideData[TYPE].allowInsertion = new boolean[] { false, true, false, false, false };
		sideData[TYPE].allowExtraction = new boolean[] { false, false, true, true, true };
		sideData[TYPE].sideTex = new int[] { 0, 1, 2, 3, 4 };

		energyData[TYPE] = new EnergyConfig();
		energyData[TYPE].setEnergyParams(40);

		guiIds[TYPE] = ThermalExpansion.proxy.registerGui("Pulverizer", "machine", true);
		GameRegistry.registerTileEntity(TilePulverizer.class, "thermalexpansion.Pulverizer");
	}

	int outputTrackerPrimary;
	int outputTrackerSecondary;

	public TilePulverizer() {

		super();

		sideCache = new byte[] { 3, 3, 2, 2, 2, 2 };
		inventory = new ItemStack[1 + 2 + 1 + 1];
	}

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	public boolean canStart() {

		if (inventory[0] == null) {
			return false;
		}
		RecipePulverizer recipe = PulverizerManager.getRecipe(inventory[0]);

		if (recipe == null || energyStorage.getEnergyStored() < recipe.getEnergy()) {
			return false;
		}
		if (inventory[0].stackSize < recipe.getInput().stackSize) {
			return false;
		}
		ItemStack primaryItem = recipe.getPrimaryOutput();
		ItemStack secondaryItem = recipe.getSecondaryOutput();

		if (secondaryItem != null && inventory[3] != null) {
			if (!inventory[3].isItemEqual(secondaryItem)) {
				return false;
			}
			if (inventory[3].stackSize + secondaryItem.stackSize > secondaryItem.getMaxStackSize()) {
				return false;
			}
		}
		if (inventory[1] == null || inventory[2] == null) {
			return true;
		}
		if (!inventory[1].isItemEqual(primaryItem) && !inventory[2].isItemEqual(primaryItem)) {
			return false;
		}
		if (!inventory[1].isItemEqual(primaryItem)) {
			return inventory[2].stackSize + primaryItem.stackSize <= primaryItem.getMaxStackSize();
		}
		if (!inventory[2].isItemEqual(primaryItem)) {
			return inventory[1].stackSize + primaryItem.stackSize <= primaryItem.getMaxStackSize();
		}
		return inventory[1].stackSize + inventory[2].stackSize + primaryItem.stackSize <= primaryItem.getMaxStackSize() * 2;
	}

	@Override
	protected boolean hasValidInput() {

		RecipePulverizer recipe = PulverizerManager.getRecipe(inventory[0]);
		return recipe == null ? false : recipe.getInput().stackSize <= inventory[0].stackSize;
	}

	@Override
	protected void processStart() {

		processMax = PulverizerManager.getRecipe(inventory[0]).getEnergy();
		processRem = processMax;
	}

	@Override
	protected void processFinish() {

		RecipePulverizer recipe = PulverizerManager.getRecipe(inventory[0]);
		ItemStack primaryItem = recipe.getPrimaryOutput();
		ItemStack secondaryItem = recipe.getSecondaryOutput();

		if (inventory[1] == null) {
			inventory[1] = primaryItem;
		} else if (inventory[1].isItemEqual(primaryItem)) {
			int result = inventory[1].stackSize + primaryItem.stackSize;

			if (result <= primaryItem.getMaxStackSize()) {
				inventory[1].stackSize += primaryItem.stackSize;
			} else {
				int overflow = primaryItem.getMaxStackSize() - inventory[1].stackSize;
				inventory[1].stackSize += overflow;

				if (inventory[2] == null) {
					inventory[2] = primaryItem;
					inventory[2].stackSize = primaryItem.stackSize - overflow;
				} else {
					inventory[2].stackSize += primaryItem.stackSize - overflow;
				}
			}
		} else {
			if (inventory[2] == null) {
				inventory[2] = primaryItem;
			} else {
				inventory[2].stackSize += primaryItem.stackSize;
			}
		}
		if (secondaryItem != null) {
			if (worldObj.rand.nextInt(CHANCE) < recipe.getSecondaryOutputChance()) {
				if (inventory[3] == null) {
					inventory[3] = secondaryItem;
				} else {
					inventory[3].stackSize += secondaryItem.stackSize;
				}
			}
		}
		inventory[0].stackSize--;

		if (inventory[0].stackSize <= 0) {
			inventory[0] = null;
		}
	}

	@Override
	protected void transferProducts() {

		int side;

		if (inventory[1] == null && inventory[2] == null) {

		} else {
			for (int i = outputTrackerPrimary + 1; i <= outputTrackerPrimary + 6; ++i) {
				side = i % 6;

				if (sideCache[side] == 2 || sideCache[side] == 4) {
					if (transferItem(1, 4, side)) {
						if (!transferItem(2, 4, side)) {
							transferItem(1, 4, side);
						}
						outputTrackerPrimary = side;
						break;
					} else if (transferItem(2, 8, side)) {
						outputTrackerPrimary = side;
						break;
					}
				}
			}
		}
		if (inventory[3] == null) {
			return;
		}
		for (int i = outputTrackerSecondary + 1; i <= outputTrackerSecondary + 6; ++i) {
			side = i % 6;

			if (sideCache[side] == 3 || sideCache[side] == 4) {
				if (transferItem(3, 4, side)) {
					outputTrackerSecondary = side;
					break;
				}
			}
		}
	}

	@Override
	public boolean canAcceptItem(ItemStack stack, int slot, int side) {

		return slot == 0 ? PulverizerManager.recipeExists(stack) : true;
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

}
