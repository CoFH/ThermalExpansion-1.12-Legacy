package cofh.thermalexpansion.block.device;

import cofh.api.inventory.IInventoryConnection;
import cofh.core.CoFHProps;
import cofh.core.entity.CoFHFakePlayer;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.FluidHelper;
import cofh.lib.util.helpers.InventoryHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.block.device.BlockDevice.Types;
import cofh.thermalexpansion.gui.client.device.GuiBreaker;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import cpw.mods.fml.common.registry.GameRegistry;

import java.util.LinkedList;

import net.minecraft.block.Block;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileBreaker extends TileDeviceBase implements IFluidHandler, IInventoryConnection {

	public static void initialize() {

		int type = BlockDevice.Types.BREAKER.ordinal();

		defaultSideConfig[type] = new SideConfig();
		defaultSideConfig[type].numConfig = 2;
		defaultSideConfig[type].slotGroups = new int[][] { {}, {} };
		defaultSideConfig[type].allowInsertionSide = new boolean[] { false, false };
		defaultSideConfig[type].allowExtractionSide = new boolean[] { false, false };
		defaultSideConfig[type].allowInsertionSlot = new boolean[] {};
		defaultSideConfig[type].allowExtractionSlot = new boolean[] {};
		defaultSideConfig[type].sideTex = new int[] { 0, 4 };
		defaultSideConfig[type].defaultSides = new byte[] { 0, 0, 0, 0, 0, 0 };

		GameRegistry.registerTileEntity(TileBreaker.class, "thermalexpansion.Breaker");
	}

	CoFHFakePlayer myFakePlayer;
	LinkedList<ItemStack> stuffedItems = new LinkedList<ItemStack>();

	public TileBreaker() {

		super(Types.BREAKER);
	}

	@Override
	public void cofh_validate() {

		if (ServerHelper.isServerWorld(worldObj)) {
			myFakePlayer = new CoFHFakePlayer((WorldServer) worldObj);
		}
		super.cofh_validate();
	}

	@Override
	public void setDefaultSides() {

		sideCache = getDefaultSides();
		sideCache[facing ^ 1] = 1;
	}

	@Override
	public void updateEntity() {

		if (ServerHelper.isClientWorld(worldObj)) {
			return;
		}
		if (worldObj.getTotalWorldTime() % CoFHProps.TIME_CONSTANT_HALF == 0 && redstoneControlOrDisable()) {
			if (!isEmpty()) {
				outputBuffer();
			}
			if (isEmpty()) {
				breakBlock();
			}
		}
	}

	public boolean isEmpty() {

		return stuffedItems.size() == 0;
	}

	public void breakBlock() {

		int coords[] = BlockHelper.getAdjacentCoordinatesForSide(xCoord, yCoord, zCoord, facing);
		Block block = worldObj.getBlock(coords[0], coords[1], coords[2]);
		FluidStack theStack = FluidHelper.getFluidFromWorld(worldObj, coords[0], coords[1], coords[2], true);
		if (theStack != null) {
			for (int i = 0; i < 6 && theStack.amount > 0; i++) {
				if (sideCache[i] == 1) {
					theStack.amount -= FluidHelper.insertFluidIntoAdjacentFluidHandler(this, i, theStack, true);
				}
			}
			worldObj.setBlockToAir(coords[0], coords[1], coords[2]);
		} else if (CoFHFakePlayer.isBlockBreakable(myFakePlayer, worldObj, coords[0], coords[1], coords[2])) {
			stuffedItems.addAll(BlockHelper.breakBlock(worldObj, myFakePlayer, coords[0], coords[1], coords[2], block, 0, true, false));
		}
	}

	public void outputBuffer() {

		for (int i = 0; i < 6; i++) {
			if (i != facing && sideCache[i] == 1) {
				int coords[] = BlockHelper.getAdjacentCoordinatesForSide(xCoord, yCoord, zCoord, i);
				TileEntity theTile = worldObj.getTileEntity(coords[0], coords[1], coords[2]);

				if (InventoryHelper.isInsertion(theTile)) {
					LinkedList<ItemStack> newStuffed = new LinkedList<ItemStack>();
					for (ItemStack curItem : stuffedItems) {
						if (curItem == null || curItem.getItem() == null) {
							curItem = null;
						} else {
							curItem = InventoryHelper.addToInsertion(theTile, i, curItem);
						}
						if (curItem != null) {
							newStuffed.add(curItem);
						}
					}
					stuffedItems = newStuffed;
				}
			}
		}
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiBreaker(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerTEBase(inventory, this);
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		NBTTagList list = nbt.getTagList("StuffedInv", 10);
		stuffedItems.clear();
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound compound = list.getCompoundTagAt(i);
			stuffedItems.add(ItemStack.loadItemStackFromNBT(compound));
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		NBTTagList list = new NBTTagList();
		list = new NBTTagList();
		for (int i = 0; i < stuffedItems.size(); i++) {
			if (stuffedItems.get(i) != null) {
				NBTTagCompound compound = new NBTTagCompound();
				stuffedItems.get(i).writeToNBT(compound);
				list.appendTag(compound);
			}
		}
		nbt.setTag("StuffedInv", list);
	}

	/* IFluidHandler */
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {

		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {

		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {

		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {

		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {

		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {

		return CoFHProps.EMPTY_TANK_INFO;
	}

	/* IInventoryConnection */
	@Override
	public ConnectionType canConnectInventory(ForgeDirection from) {

		if (from != ForgeDirection.UNKNOWN && from.ordinal() != facing && sideCache[from.ordinal()] == 1) {
			return ConnectionType.FORCE;
		} else {
			return ConnectionType.DEFAULT;
		}
	}

}
