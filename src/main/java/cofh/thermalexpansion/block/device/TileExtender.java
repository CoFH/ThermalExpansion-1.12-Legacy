package cofh.thermalexpansion.block.device;

import cofh.api.energy.IEnergyReceiver;
import cofh.core.CoFHProps;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.block.device.BlockDevice.Types;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileExtender extends TileDeviceBase implements IFluidHandler {

	public static void initialize() {

		int type = BlockDevice.Types.EXTENDER.ordinal();

		defaultSideConfig[type] = new SideConfig();
		defaultSideConfig[type].numConfig = 2;
		defaultSideConfig[type].slotGroups = new int[][] { {}, {} };
		defaultSideConfig[type].allowInsertionSide = new boolean[] { false, false };
		defaultSideConfig[type].allowExtractionSide = new boolean[] { false, false };
		defaultSideConfig[type].allowInsertionSlot = new boolean[] {};
		defaultSideConfig[type].allowExtractionSlot = new boolean[] {};
		defaultSideConfig[type].sideTex = new int[] { 0, 7 };
		defaultSideConfig[type].defaultSides = new byte[] { 0, 0, 0, 0, 0, 0 };

		GameRegistry.registerTileEntity(TileExtender.class, "thermalexpansion.Extender");
	}

	ISidedInventory targetInventorySided;
	IInventory targetInventory;
	IEnergyReceiver targetReceiver;
	IFluidHandler targetHandler;

	public TileExtender() {

		super(Types.EXTENDER);
	}

	@Override
	public void onNeighborBlockChange() {

		super.onNeighborBlockChange();
		updateHandlers();
	}

	@Override
	public void onNeighborTileChange(int tileX, int tileY, int tileZ) {

		super.onNeighborTileChange(tileX, tileY, tileZ);
		updateHandlers();
	}

	protected void updateHandlers() {

		if (ServerHelper.isClientWorld(worldObj)) {
			return;
		}
		TileEntity tile = BlockHelper.getAdjacentTileEntity(this, facing);

		if (tile instanceof ISidedInventory) {
			targetInventorySided = (ISidedInventory) tile;
		} else {
			targetInventorySided = null;
		}
		if (tile instanceof IInventory) {
			targetInventory = (IInventory) tile;
		} else {
			targetInventory = null;
		}
		if (tile instanceof IEnergyReceiver) {
			targetReceiver = (IEnergyReceiver) tile;
		} else {
			targetReceiver = null;
		}
		if (tile instanceof IFluidHandler) {
			targetHandler = (IFluidHandler) tile;
		} else {
			targetHandler = null;
		}
	}

	/* IReconfigurableFacing */
	@Override
	public boolean rotateBlock() {

		super.rotateBlock();
		updateHandlers();
		return true;
	}

	/* IEnergyReceiver */
	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {

		return targetReceiver != null ? targetReceiver.receiveEnergy(ForgeDirection.VALID_DIRECTIONS[facing ^ 1], maxReceive, simulate) : 0;
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {

		return targetReceiver != null ? targetReceiver.getEnergyStored(ForgeDirection.VALID_DIRECTIONS[facing ^ 1]) : 0;
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {

		return targetReceiver != null ? targetReceiver.getMaxEnergyStored(ForgeDirection.VALID_DIRECTIONS[facing ^ 1]) : 0;
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {

		return targetReceiver != null ? targetReceiver.canConnectEnergy(ForgeDirection.VALID_DIRECTIONS[facing ^ 1]) : false;
	}

	/* IFluidHandler */
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {

		if (from.ordinal() == facing) {
			return 0;
		}
		return targetHandler != null ? targetHandler.fill(ForgeDirection.VALID_DIRECTIONS[facing ^ 1], resource, doFill) : 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {

		if (from.ordinal() == facing) {
			return null;
		}
		return targetHandler != null ? targetHandler.drain(ForgeDirection.VALID_DIRECTIONS[facing ^ 1], resource, doDrain) : null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {

		if (from.ordinal() == facing) {
			return null;
		}
		return targetHandler != null ? targetHandler.drain(ForgeDirection.VALID_DIRECTIONS[facing ^ 1], maxDrain, doDrain) : null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {

		if (from.ordinal() == facing) {
			return false;
		}
		return targetHandler != null ? targetHandler.canFill(ForgeDirection.VALID_DIRECTIONS[facing ^ 1], fluid) : false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {

		if (from.ordinal() == facing) {
			return false;
		}
		return targetHandler != null ? targetHandler.canDrain(ForgeDirection.VALID_DIRECTIONS[facing ^ 1], fluid) : false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {

		if (from.ordinal() == facing) {
			return CoFHProps.EMPTY_TANK_INFO;
		}
		return targetHandler != null ? targetHandler.getTankInfo(ForgeDirection.VALID_DIRECTIONS[facing ^ 1]) : CoFHProps.EMPTY_TANK_INFO;
	}

	/* IInventory */
	@Override
	public int getSizeInventory() {

		return targetInventory != null ? targetInventory.getSizeInventory() : 0;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {

		return targetInventory != null ? targetInventory.getStackInSlot(slot) : null;
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {

		return targetInventory != null ? targetInventory.decrStackSize(slot, amount) : null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {

		return targetInventory != null ? targetInventory.getStackInSlotOnClosing(slot) : null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {

		if (targetInventory != null) {
			targetInventory.setInventorySlotContents(slot, stack);
		}
	}

	/* ISidedInventory */
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {

		if (side == facing) {
			return CoFHProps.EMPTY_INVENTORY;
		}
		return targetInventorySided != null ? targetInventorySided.getAccessibleSlotsFromSide(side) : CoFHProps.EMPTY_INVENTORY;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int side) {

		if (side == facing) {
			return false;
		}
		return targetInventorySided != null ? targetInventorySided.canInsertItem(slot, stack, side) : false;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int side) {

		if (side == facing) {
			return false;
		}
		return targetInventorySided != null ? targetInventorySided.canExtractItem(slot, stack, side) : false;
	}

}
