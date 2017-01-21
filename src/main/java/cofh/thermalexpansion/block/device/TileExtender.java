package cofh.thermalexpansion.block.device;

import cofh.api.energy.IEnergyReceiver;
import cofh.core.CoFHProps;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.ServerHelper;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class TileExtender extends TileDeviceBase implements IFluidHandler {

	private static final int TYPE = BlockDevice.Type.EXTENDER.getMetadata();

	public static void initialize() {

		defaultSideConfig[TYPE] = new SideConfig();
		defaultSideConfig[TYPE].numConfig = 2;
		defaultSideConfig[TYPE].slotGroups = new int[][] { {}, {} };
		defaultSideConfig[TYPE].allowInsertionSide = new boolean[] { false, false };
		defaultSideConfig[TYPE].allowExtractionSide = new boolean[] { false, false };
		defaultSideConfig[TYPE].allowInsertionSlot = new boolean[] {};
		defaultSideConfig[TYPE].allowExtractionSlot = new boolean[] {};
		defaultSideConfig[TYPE].sideTex = new int[] { 0, 7 };
		defaultSideConfig[TYPE].defaultSides = new byte[] { 0, 0, 0, 0, 0, 0 };

		GameRegistry.registerTileEntity(TileExtender.class, "thermalexpansion:extender");
	}

	static final int MAX_CACHE_LEVEL = 128;
	static int[][] slotsCache;

	static {
		slotsCache = new int[MAX_CACHE_LEVEL][];
		slotsCache[0] = CoFHProps.EMPTY_INVENTORY;
	}

	public static int[] getNonSidedSlots(int n) {

		if (n < MAX_CACHE_LEVEL && slotsCache[n] != null) {
			return slotsCache[n];
		}
		int[] slots = new int[n];
		for (int j = 0; j < n; j++) {
			slots[j] = j;
		}
		if (n < MAX_CACHE_LEVEL) {
			slotsCache[n] = slots;
		}
		return slots;
	}

	ISidedInventory targetInventorySided;
	IInventory targetInventory;
	IEnergyReceiver targetReceiver;
	IFluidHandler targetHandler;

	TileEntity targetTile;
	boolean cached = false;
	boolean polling = false;

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	public void onNeighborBlockChange() {

		super.onNeighborBlockChange();
		updateHandlers();
	}

	@Override
	public void onNeighborTileChange(BlockPos pos) {

		super.onNeighborTileChange(pos);
		updateHandlers();
	}

	@Override
	public void markDirty() {

		super.markDirty();

		if (checkPollingAndNeighbor()) {
			return;
		}
		polling = true;
		if (targetTile != null) {
			targetTile.markDirty();
		}
		polling = false;
	}

	protected void updateHandlers() {

		cached = true;

		if (ServerHelper.isClientWorld(worldObj)) {
			return;
		}
		targetTile = BlockHelper.getAdjacentTileEntity(this, facing);

		if (targetTile != null) {
			if (targetTile.isInvalid()) {
				targetTile = null;
			} else if (targetTile instanceof TileExtender) {
				targetTile = null;
			}
		}
		if (targetTile instanceof ISidedInventory) {
			targetInventorySided = (ISidedInventory) targetTile;
		} else {
			targetInventorySided = null;
		}
		if (targetTile instanceof IInventory) {
			targetInventory = (IInventory) targetTile;
		} else {
			targetInventory = null;
		}
		if (targetTile instanceof IEnergyReceiver) {
			targetReceiver = (IEnergyReceiver) targetTile;
		} else {
			targetReceiver = null;
		}
		if (targetTile instanceof IFluidHandler) {
			targetHandler = (IFluidHandler) targetTile;
		} else {
			targetHandler = null;
		}
	}

	public boolean checkPollingAndNeighbor() {

		if (polling || ServerHelper.isClientWorld(worldObj)) {
			return true;
		}
		if (!cached || (targetTile != null && targetTile.isInvalid())) {
			updateHandlers();
		}
		return targetTile != null && !targetTile.isInvalid();
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
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {

		if (checkPollingAndNeighbor()) {
			return 0;
		}
		polling = true;
		int energy = targetReceiver != null ? targetReceiver.receiveEnergy(EnumFacing.VALUES[facing ^ 1], maxReceive, simulate) : 0;
		polling = false;
		return energy;
	}

	@Override
	public int getEnergyStored(EnumFacing from) {

		if (checkPollingAndNeighbor()) {
			return 0;
		}
		polling = true;
		int energy = targetReceiver != null ? targetReceiver.getEnergyStored(EnumFacing.VALUES[facing ^ 1]) : 0;
		polling = false;
		return energy;
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {

		if (checkPollingAndNeighbor()) {
			return 0;
		}
		polling = true;
		int maxEnergy = targetReceiver != null ? targetReceiver.getMaxEnergyStored(EnumFacing.VALUES[facing ^ 1]) : 0;
		polling = false;
		return maxEnergy;
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {

		if (checkPollingAndNeighbor()) {
			return false;
		}
		polling = true;
		boolean canConnect = targetReceiver != null && targetReceiver.canConnectEnergy(EnumFacing.VALUES[facing ^ 1]);
		polling = false;
		return canConnect;
	}

	/* IFluidHandler */
	@Override
	public int fill(EnumFacing from, FluidStack resource, boolean doFill) {

		if (from.ordinal() == facing) {
			return 0;
		}
		if (checkPollingAndNeighbor()) {
			return 0;
		}
		polling = true;
		int amount = targetHandler != null ? targetHandler.fill(EnumFacing.VALUES[facing ^ 1], resource, doFill) : 0;
		polling = false;
		return amount;
	}

	@Override
	public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {

		if (from.ordinal() == facing) {
			return null;
		}
		if (checkPollingAndNeighbor()) {
			return null;
		}
		polling = true;
		FluidStack drain = targetHandler != null ? targetHandler.drain(EnumFacing.VALUES[facing ^ 1], resource, doDrain) : null;
		polling = false;
		return drain;
	}

	@Override
	public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {

		if (from.ordinal() == facing) {
			return null;
		}
		if (checkPollingAndNeighbor()) {
			return null;
		}
		polling = true;
		FluidStack drain = targetHandler != null ? targetHandler.drain(EnumFacing.VALUES[facing ^ 1], maxDrain, doDrain) : null;
		polling = false;
		return drain;
	}

	@Override
	public boolean canFill(EnumFacing from, Fluid fluid) {

		if (from.ordinal() == facing) {
			return false;
		}
		if (checkPollingAndNeighbor()) {
			return false;
		}
		polling = true;
		boolean canFill = targetHandler != null && targetHandler.canFill(EnumFacing.VALUES[facing ^ 1], fluid);
		polling = false;
		return canFill;
	}

	@Override
	public boolean canDrain(EnumFacing from, Fluid fluid) {

		if (from.ordinal() == facing) {
			return false;
		}
		if (checkPollingAndNeighbor()) {
			return false;
		}
		polling = true;
		boolean canDrain = targetHandler != null && targetHandler.canDrain(EnumFacing.VALUES[facing ^ 1], fluid);
		polling = false;
		return canDrain;
	}

	@Override
	public FluidTankInfo[] getTankInfo(EnumFacing from) {

		if (from.ordinal() == facing) {
			return CoFHProps.EMPTY_TANK_INFO;
		}
		if (checkPollingAndNeighbor()) {
			return CoFHProps.EMPTY_TANK_INFO;
		}
		polling = true;
		FluidTankInfo[] tankInfo = targetHandler != null ? targetHandler.getTankInfo(EnumFacing.VALUES[facing ^ 1]) : CoFHProps.EMPTY_TANK_INFO;
		polling = false;
		return tankInfo;
	}

	/* IInventory */
	@Override
	public int getSizeInventory() {

		if (ServerHelper.isClientWorld(worldObj)) {
			return 1;
		}
		if (checkPollingAndNeighbor()) {
			return 0;
		}
		polling = true;
		int size = targetInventory != null ? targetInventory.getSizeInventory() : 0;
		polling = false;
		return size;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {

		if (checkPollingAndNeighbor()) {
			return null;
		}
		polling = true;
		ItemStack stack = targetInventory != null ? targetInventory.getStackInSlot(slot) : null;
		polling = false;
		return stack;
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {

		if (checkPollingAndNeighbor()) {
			return null;
		}
		polling = true;
		ItemStack stack = targetInventory != null ? targetInventory.decrStackSize(slot, amount) : null;
		polling = false;
		return stack;
	}

	@Override
	public ItemStack removeStackFromSlot(int slot) {

		if (checkPollingAndNeighbor()) {
			return null;
		}
		polling = true;
		ItemStack stack = targetInventory != null ? targetInventory.removeStackFromSlot(slot) : null;
		polling = false;
		return stack;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {

		if (checkPollingAndNeighbor()) {
			return;
		}
		polling = true;
		if (targetInventory != null) {
			targetInventory.setInventorySlotContents(slot, stack);
		}
		polling = false;
	}

	@Override
	public int getInventoryStackLimit() {

		if (checkPollingAndNeighbor()) {
			return 64;
		}
		polling = true;
		int num = targetInventory != null ? targetInventory.getInventoryStackLimit() : 64;
		polling = false;
		return num;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		if (checkPollingAndNeighbor()) {
			return false;
		}
		polling = true;
		boolean valid = targetInventory != null && targetInventory.isItemValidForSlot(slot, stack);
		polling = false;
		return valid;
	}

	/* ISidedInventory */
	@Override
	public int[] getSlotsForFace(EnumFacing side) {

		if (side.ordinal() == facing) {
			return CoFHProps.EMPTY_INVENTORY;
		}
		if (checkPollingAndNeighbor()) {
			return CoFHProps.EMPTY_INVENTORY;
		}
		polling = true;
		int[] slots;
		if (targetInventorySided != null) {
			slots = targetInventorySided.getSlotsForFace(side);
		} else if (targetInventory != null) {
			slots = getNonSidedSlots(targetInventory.getSizeInventory());
		} else {
			slots = CoFHProps.EMPTY_INVENTORY;
		}
		polling = false;
		return slots;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, EnumFacing side) {

		if (side.ordinal() == facing) {
			return false;
		}
		if (checkPollingAndNeighbor()) {
			return false;
		}
		polling = true;
		boolean canInsert = targetInventorySided != null ? targetInventorySided.canInsertItem(slot, stack, side) : targetInventory != null;
		polling = false;
		return canInsert;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, EnumFacing side) {

		if (side.ordinal() == facing) {
			return false;
		}
		if (checkPollingAndNeighbor()) {
			return false;
		}
		polling = true;
		boolean canExtract = targetInventorySided != null ? targetInventorySided.canExtractItem(slot, stack, side) : targetInventory != null;
		polling = false;
		return canExtract;
	}

}
