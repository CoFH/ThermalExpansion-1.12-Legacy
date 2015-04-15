package cofh.thermalexpansion.block.device;

import cofh.api.energy.IEnergyReceiver;
import cofh.core.CoFHProps;
import cofh.core.render.IconRegistry;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.block.TileAugmentable;
import cofh.thermalexpansion.core.TEProps;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileExtension extends TileAugmentable implements IFluidHandler {

	static final int TYPE = 0;
	static SideConfig defaultSideConfig = new SideConfig();

	ISidedInventory targetInventorySided;
	IInventory targetInventory;
	IEnergyReceiver targetReceiver;
	IFluidHandler targetHandler;

	@Override
	public String getName() {

		// return "tile.thermalexpansion.device." + 0 + ".name";
		return "";
	}

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

	/* IEnergyReceiver */
	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {

		return targetReceiver != null ? targetReceiver.receiveEnergy(from, maxReceive, simulate) : 0;
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {

		return targetReceiver != null ? targetReceiver.getEnergyStored(from) : 0;
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {

		return targetReceiver != null ? targetReceiver.getMaxEnergyStored(from) : 0;
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {

		return targetReceiver != null ? targetReceiver.canConnectEnergy(from) : false;
	}

	/* IFluidHandler */
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {

		if (from.ordinal() == facing) {
			return 0;
		}
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {

		if (from.ordinal() == facing) {
			return null;
		}
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {

		if (from.ordinal() == facing) {
			return null;
		}
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {

		if (from.ordinal() == facing) {
			return false;
		}
		return targetHandler != null ? targetHandler.canFill(from, fluid) : false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {

		if (from.ordinal() == facing) {
			return false;
		}
		return targetHandler != null ? targetHandler.canDrain(from, fluid) : false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {

		if (from.ordinal() == facing) {
			return CoFHProps.EMPTY_TANK_INFO;
		}
		return targetHandler != null ? targetHandler.getTankInfo(from) : CoFHProps.EMPTY_TANK_INFO;
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

	/* ISidedTexture */
	@Override
	public IIcon getTexture(int side, int pass) {

		if (pass == 0) {
			return side != facing ? IconRegistry.getIcon("DeviceSide") : redstoneControlOrDisable() ? IconRegistry.getIcon("DeviceActive", getType())
					: IconRegistry.getIcon("DeviceFace", getType());
		} else if (side < 6) {
			return IconRegistry.getIcon(TEProps.textureSelection, sideConfig.sideTex[sideCache[side]]);
		}
		return IconRegistry.getIcon("DeviceSide");
	}

}
