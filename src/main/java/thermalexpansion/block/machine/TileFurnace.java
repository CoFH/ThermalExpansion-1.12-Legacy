package thermalexpansion.block.machine;

import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.util.crafting.FurnaceManager;
import thermalexpansion.util.crafting.FurnaceManager.RecipeFurnace;

public class TileFurnace extends TileMachineEnergized {

	public static final int TYPE = BlockMachine.Types.FURNACE.ordinal();

	public static void initialize() {

		sideData[TYPE] = new SideConfig();
		sideData[TYPE].numGroup = 3;
		sideData[TYPE].slotGroups = new int[][] { {}, { 0 }, { 1 } };
		sideData[TYPE].allowInsertion = new boolean[] { false, true, false };
		sideData[TYPE].allowExtraction = new boolean[] { false, false, true };
		sideData[TYPE].sideTex = new int[] { 0, 1, 4 };

		energyData[TYPE] = new EnergyConfig();
		energyData[TYPE].setEnergyParams(20);

		guiIds[TYPE] = ThermalExpansion.proxy.registerGui("Furnace", "machine", true);
		GameRegistry.registerTileEntity(TileFurnace.class, "thermalexpansion.Furnace");
	}

	int outputTracker;

	public TileFurnace() {

		super();

		sideCache = new byte[] { 1, 1, 2, 2, 2, 2 };
		inventory = new ItemStack[1 + 1 + 1];
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
		RecipeFurnace recipe = FurnaceManager.getRecipe(inventory[0]);

		if (recipe == null || energyStorage.getEnergyStored() < recipe.getEnergy()) {
			return false;
		}
		ItemStack output = recipe.getOutput();

		if (output == null) {
			return false;
		}
		if (inventory[1] == null) {
			return true;
		}
		if (!inventory[1].isItemEqual(output)) {
			return false;
		}
		return inventory[1].stackSize + output.stackSize <= output.getMaxStackSize();
	}

	@Override
	protected boolean hasValidInput() {

		RecipeFurnace recipe = FurnaceManager.getRecipe(inventory[0]);
		return recipe == null ? false : recipe.getInput().stackSize <= inventory[0].stackSize;
	}

	@Override
	protected void processStart() {

		processMax = FurnaceManager.getRecipe(inventory[0]).getEnergy();
		processRem = processMax;
	}

	@Override
	protected void processFinish() {

		ItemStack output = FurnaceManager.getRecipe(inventory[0]).getOutput();
		if (inventory[1] == null) {
			inventory[1] = output;
		} else {
			inventory[1].stackSize += output.stackSize;
		}
		inventory[0].stackSize--;

		if (inventory[0].stackSize <= 0) {
			inventory[0] = null;
		}
	}

	@Override
	protected void transferProducts() {

		if (inventory[1] == null) {
			return;
		}
		int side;
		for (int i = outputTracker + 1; i <= outputTracker + 6; i++) {
			side = i % 6;

			if (sideCache[side] == 2) {
				if (transferItem(1, 4, side)) {
					outputTracker = side;
					break;
				}
			}
		}
	}

	@Override
	public int getChargeSlot() {

		return 2;
	}

	@Override
	public boolean canAcceptItem(ItemStack stack, int slot, int side) {

		return slot == 0 ? FurnaceManager.recipeExists(stack) : true;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		outputTracker = nbt.getInteger("Tracker");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("Tracker", outputTracker);
	}

}
