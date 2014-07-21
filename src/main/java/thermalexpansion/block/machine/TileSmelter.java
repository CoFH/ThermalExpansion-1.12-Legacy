package thermalexpansion.block.machine;

import cofh.util.MathHelper;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.gui.client.machine.GuiSmelter;
import thermalexpansion.gui.container.machine.ContainerSmelter;
import thermalexpansion.util.crafting.SmelterManager;
import thermalexpansion.util.crafting.SmelterManager.RecipeSmelter;

public class TileSmelter extends TileMachineBase {

	static final int TYPE = BlockMachine.Types.SMELTER.ordinal();

	public static void initialize() {

		defaultSideConfig[TYPE] = new SideConfig();
		defaultSideConfig[TYPE].numGroup = 7;
		defaultSideConfig[TYPE].slotGroups = new int[][] { {}, { 0, 1 }, { 2, 3 }, { 4 }, { 2, 3, 4 }, { 0 }, { 1 } };
		defaultSideConfig[TYPE].allowInsertion = new boolean[] { false, true, false, false, false, true, true };
		defaultSideConfig[TYPE].allowExtraction = new boolean[] { false, true, true, true, true, true, true };
		defaultSideConfig[TYPE].sideTex = new int[] { 0, 1, 2, 3, 4, 5, 6 };
		defaultSideConfig[TYPE].defaultSides = new byte[] { 3, 1, 2, 2, 2, 2 };

		int maxPower = MathHelper.clampI(ThermalExpansion.config.get("block.tweak", "Machine.Smelter.BasePower", 40), 10, 500);
		ThermalExpansion.config.set("block.tweak", "Machine.Smelter.BasePower", maxPower);
		defaultEnergyConfig[TYPE] = new EnergyConfig();
		defaultEnergyConfig[TYPE].setParamsPower(maxPower);

		GameRegistry.registerTileEntity(TileSmelter.class, "thermalexpansion.Smelter");
	}

	int outputTrackerPrimary;
	int outputTrackerSecondary;

	public TileSmelter() {

		super();

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
	protected boolean canStart() {

		if (inventory[0] == null && inventory[1] == null) {
			return false;
		}
		RecipeSmelter recipe = SmelterManager.getRecipe(inventory[0], inventory[1]);

		if (recipe == null || energyStorage.getEnergyStored() < recipe.getEnergy() * energyMod / processMod) {
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
			if (worldObj.rand.nextInt(secondaryChance) < recipe.getSecondaryOutputChance()) {
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

		if (!augmentAutoTransfer) {
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
	public boolean isItemValid(ItemStack stack, int slot, int side) {

		return slot == 0 ? SmelterManager.isItemValid(stack) : true;
	}

	/* GUI METHODS */
	@Override
	public GuiContainer getGuiClient(InventoryPlayer inventory) {

		return new GuiSmelter(inventory, this);
	}

	@Override
	public Container getGuiServer(InventoryPlayer inventory) {

		return new ContainerSmelter(inventory, this);
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
