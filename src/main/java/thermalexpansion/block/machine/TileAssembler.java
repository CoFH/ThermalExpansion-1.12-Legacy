package thermalexpansion.block.machine;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import thermalexpansion.ThermalExpansion;
import thermalexpansion.core.TEProps;
import thermalexpansion.item.SchematicHelper;
import cofh.util.InventoryHelper;
import cofh.util.ItemHelper;
import cofh.util.ServerHelper;
import cofh.util.inventory.InventoryCraftingFalse;
import cpw.mods.fml.common.registry.GameRegistry;

public class TileAssembler extends TileMachineEnergized implements IFluidHandler {

	public static final int TYPE = BlockMachine.Types.ASSEMBLER.ordinal();

	public static void initialize() {

		sideData[TYPE] = new SideConfig();
		sideData[TYPE].numGroup = 3;
		sideData[TYPE].slotGroups = new int[][] { {}, { 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20 }, { 1 } };
		sideData[TYPE].allowInsertion = new boolean[] { false, true, false };
		sideData[TYPE].allowExtraction = new boolean[] { false, false, true };
		sideData[TYPE].sideTex = new int[] { 0, 1, 4 };

		energyData[TYPE] = new EnergyConfig();
		energyData[TYPE].setEnergyParams(20);

		guiIds[TYPE] = ThermalExpansion.proxy.registerGui("Assembler", "machine", true);
		GameRegistry.registerTileEntity(TileAssembler.class, "cofh.thermalexpansion.Assembler");
	}

	public static final int PROCESS_ENERGY = 2;

	FluidTank tank = new FluidTank(MAX_FLUID_LARGE);

	public boolean needsCraft = false;
	private boolean needsCache = true;
	ItemStack recipeOutput;
	int outputTracker;

	InventoryCrafting tempCraft = new InventoryCraftingFalse(3, 3);

	public TileAssembler() {

		super();

		sideCache = new byte[] { 1, 1, 2, 2, 2, 2 };
		inventory = new ItemStack[1 + 1 + 1 + 18];
	}

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	public int getChargeSlot() {

		return 2;
	}

	public boolean canCreate(ItemStack recipe) {

		return recipe != null
				&& (inventory[1] == null || recipe.isItemEqual(inventory[1]) && inventory[1].stackSize + recipe.stackSize <= recipe.getMaxStackSize());
	}

	ItemStack[] recipeSlot = new ItemStack[9];
	String[] recipeOre = new String[9];
	FluidStack[] filledContainer = new FluidStack[9];

	public void updateOutput() {

		if (inventory[0] != null) {
			if (needsCache) {
				recipeOutput = SchematicHelper.getOutput(inventory[0], worldObj);
				for (int i = 0; i < 9; i++) {
					recipeSlot[i] = SchematicHelper.getSchematicSlot(inventory[0], i);
					filledContainer[i] = FluidContainerRegistry.getFluidForFilledItem(recipeSlot[i]);
					recipeOre[i] = SchematicHelper.getSchematicOreSlot(inventory[0], i);
				}
				needsCache = false;
			}
			if (recipeOutput == null) {
				isActive = false;
				return;
			}

			if (canCreate(recipeOutput)) {
				if (createItem()) {
					recipeOutput = ItemHelper.findMatchingRecipe(tempCraft, worldObj);
					if (recipeOutput != null) {
						if (inventory[1] == null) {
							inventory[1] = recipeOutput.copy();
						} else {
							inventory[1].stackSize += recipeOutput.stackSize;
						}
						isActive = true;
					}
				} else {
					if (energyStorage.getEnergyStored() >= PROCESS_ENERGY) {
						needsCraft = false;
					}
					wasActive = true;
					isActive = false;
					return;
				}
			} else {
				if (isActive) {
					wasActive = true;
				}
				isActive = false;
			}
		}
	}

	public boolean createItem() {

		if (energyStorage.getEnergyStored() < PROCESS_ENERGY) {
			return false;
		}
		ItemStack[] invCopy = InventoryHelper.cloneInventory(inventory);
		FluidStack fluidCopy = null;

		if (tank.getFluid() != null) {
			fluidCopy = tank.getFluid().copy();
		}
		boolean found = false;
		for (int i = 0; i < 9; i++) {
			if (fluidCopy != null) {
				if (fluidCopy.isFluidEqual(filledContainer[i])) {
					if (fluidCopy.amount >= filledContainer[i].amount) {
						fluidCopy.amount -= filledContainer[i].amount;
						tempCraft.setInventorySlotContents(i, recipeSlot[i].copy());
						continue; // Go to the next item in the schematic
					}
				}
			}

			if (recipeSlot[i] != null) {
				for (int j = 2; j < invCopy.length; j++) {
					if (invCopy[j] != null && ItemHelper.craftingEquivalent(invCopy[j], recipeSlot[i], recipeOre[i], recipeOutput)) {
						tempCraft.setInventorySlotContents(i, invCopy[j].copy());
						invCopy[j].stackSize--;

						if (invCopy[j].getItem().hasContainerItem()) {
							ItemStack containerStack = invCopy[j].getItem().getContainerItemStack(invCopy[j]);

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
						if (invCopy[j].stackSize <= 0) {
							invCopy[j] = null;
						}
						found = true;
						break;
					}
				}
				if (!found) {
					return false;
				}

				found = false;
			} else {
				tempCraft.setInventorySlotContents(i, null);
			}
		}
		// Update the inventories since we can make it.
		inventory = invCopy;

		if (fluidCopy == null || fluidCopy.amount <= 0) {
			fluidCopy = null;
		}
		tank.setFluid(fluidCopy);
		energyStorage.modifyEnergyStored(-PROCESS_ENERGY);
		return true;
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
				if (transferItem(1, 64, side)) {
					outputTracker = side;
					break;
				}
			}
		}
	}

	@Override
	public void updateEntity() {

		if (ServerHelper.isClientWorld(worldObj)) {
			return;
		}
		boolean curActive = isActive;

		if (redstoneControlOrDisable()) {
			if (needsCraft) {
				updateOutput();
			}
			if (timeCheck()) {
				transferProducts();
			}
		} else {
			if (isActive) {
				wasActive = true;
			}
			isActive = false;
		}
		updateIfChanged(curActive);
		chargeEnergy();
	}

	/* NETWORK METHODS */
	@Override
	public Payload getGuiPayload() {

		Payload payload = super.getGuiPayload();

		payload.addFluidStack(getTankFluid());

		return payload;
	}

	/* ITileInfoPacketHandler */
	@Override
	public void handleTileInfoPacket(Payload payload, NetHandler handler) {

		switch (TEProps.PacketID.values()[payload.getByte()]) {
		case GUI:
			isActive = payload.getBool();
			processMax = payload.getInt();
			processRem = payload.getInt();
			energyStorage.setEnergyStored(payload.getInt());
			tank.setFluid(payload.getFluidStack());
			return;
		default:
		}
	}

	/* GUI METHODS */
	public FluidTank getTank() {

		return tank;
	}

	public FluidStack getTankFluid() {

		return tank.getFluid();
	}

	@Override
	public void sendGuiNetworkData(Container container, ICrafting iCrafting) {

		super.sendGuiNetworkData(container, iCrafting);
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);
		outputTracker = nbt.getInteger("Output");
		needsCraft = true;
		needsCache = true;
		tank.readFromNBT(nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);
		nbt.setInteger("Output", outputTracker);
		tank.writeToNBT(nbt);
	}

	/* IInventory */
	@Override
	public void markDirty() {

		needsCraft = true;
		// needsCache = true;
		super.markDirty();
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {

		needsCraft = true;
		needsCache = needsCache ? true : slot == 0;
		return super.decrStackSize(slot, amount);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {

		needsCraft = true;
		needsCache = needsCache ? true : slot == 0;

		inventory[slot] = stack;

		if (stack != null && stack.stackSize > getInventoryStackLimit()) {
			stack.stackSize = getInventoryStackLimit();
		}
	}

	/* IFluidHandler */
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {

		if (from == ForgeDirection.UNKNOWN || sideCache[from.ordinal()] != 1) {
			return 0;
		}
		int filled = tank.fill(resource, doFill);

		if (doFill && filled > 0) {
			needsCraft = true;
		}
		return filled;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {

		if (from == ForgeDirection.UNKNOWN || sideCache[from.ordinal()] != 2) {
			return null;
		}
		if (resource == null || !resource.isFluidEqual(tank.getFluid())) {
			return null;
		}
		return tank.drain(resource.amount, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {

		if (from == ForgeDirection.UNKNOWN || sideCache[from.ordinal()] != 2) {
			return null;
		}
		return tank.drain(maxDrain, doDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {

		return true;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {

		return true;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {

		return new FluidTankInfo[] { tank.getInfo() };
	}

}
