package thermalexpansion.block.machine;

import cofh.util.MathHelper;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.gui.client.machine.GuiPulverizer;
import thermalexpansion.gui.container.machine.ContainerPulverizer;
import thermalexpansion.util.crafting.PulverizerManager;
import thermalexpansion.util.crafting.PulverizerManager.RecipePulverizer;

public class TilePulverizer extends TileMachineBase {

	static final int TYPE = BlockMachine.Types.PULVERIZER.ordinal();

	public static void initialize() {

		defaultSideConfig[TYPE] = new SideConfig();
		defaultSideConfig[TYPE].numGroup = 5;
		defaultSideConfig[TYPE].slotGroups = new int[][] { {}, { 0 }, { 1, 2 }, { 3 }, { 1, 2, 3 } };
		defaultSideConfig[TYPE].allowInsertion = new boolean[] { false, true, false, false, false };
		defaultSideConfig[TYPE].allowExtraction = new boolean[] { false, true, true, true, true };
		defaultSideConfig[TYPE].sideTex = new int[] { 0, 1, 2, 3, 4 };
		defaultSideConfig[TYPE].defaultSides = new byte[] { 3, 1, 2, 2, 2, 2 };

		int maxPower = MathHelper.clampI(ThermalExpansion.config.get("block.tweak", "Machine.Pulverizer.BasePower", 40), 10, 500);
		ThermalExpansion.config.set("block.tweak", "Machine.Pulverizer.BasePower", maxPower);
		defaultEnergyConfig[TYPE] = new EnergyConfig();
		defaultEnergyConfig[TYPE].setParamsPower(maxPower);

		GameRegistry.registerTileEntity(TilePulverizer.class, "thermalexpansion.Pulverizer");
	}

	int outputTrackerPrimary;
	int outputTrackerSecondary;

	public TilePulverizer() {

		super();

		inventory = new ItemStack[1 + 2 + 1 + 1];
	}

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	protected boolean canStart() {

		if (inventory[0] == null) {
			return false;
		}
		RecipePulverizer recipe = PulverizerManager.getRecipe(inventory[0]);

		if (recipe == null || energyStorage.getEnergyStored() < recipe.getEnergy() * energyMod / processMod) {
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
			if (worldObj.rand.nextInt(secondaryChance) < recipe.getSecondaryOutputChance()) {
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

		if (!augmentAutoTransfer) {
			return;
		}
		int side;
		if (inventory[1] != null || inventory[2] != null) {
			for (int i = outputTrackerPrimary + 1; i <= outputTrackerPrimary + 6; i++) {
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
		for (int i = outputTrackerSecondary + 1; i <= outputTrackerSecondary + 6; i++) {
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
	public boolean isItemValid(ItemStack stack, int slot, int side) {

		return slot == 0 ? PulverizerManager.recipeExists(stack) : true;
	}

	/* GUI METHODS */
	@Override
	public GuiContainer getGuiClient(InventoryPlayer inventory) {

		return new GuiPulverizer(inventory, this);
	}

	@Override
	public Container getGuiServer(InventoryPlayer inventory) {

		return new ContainerPulverizer(inventory, this);
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
