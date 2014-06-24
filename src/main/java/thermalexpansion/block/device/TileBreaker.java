package thermalexpansion.block.device;

import cofh.core.CoFHProps;
import cofh.entity.PlayerFake;
import cofh.render.IconRegistry;
import cofh.util.BlockHelper;
import cofh.util.FluidHelper;
import cofh.util.InventoryHelper;
import cofh.util.ServerHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

import java.util.LinkedList;

import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.block.TileReconfigurableInventory;
import thermalexpansion.core.TEProps;
import thermalexpansion.gui.GuiHandler;
import thermalexpansion.gui.client.device.GuiBreaker;
import thermalexpansion.gui.container.ContainerTEBase;

public class TileBreaker extends TileReconfigurableInventory implements IFluidHandler {

	public static void initialize() {

		GameRegistry.registerTileEntity(TileBreaker.class, "thermalexpansion.Breaker");
	}

	public static final int[] SIDE_TEX = new int[] { 0, 4 };

	public LinkedList<ItemStack> stuffedItems = new LinkedList<ItemStack>();

	PlayerFake myFakePlayer;
	boolean needsWorld = true;

	@Override
	public String getName() {

		return "tile.thermalexpansion.device." + BlockDevice.NAMES[getType()] + ".name";
	}

	@Override
	public int getType() {

		return BlockDevice.Types.BREAKER.ordinal();
	}

	@Override
	public boolean openGui(EntityPlayer player) {

		player.openGui(ThermalExpansion.instance, GuiHandler.TILE_ID, worldObj, xCoord, yCoord, zCoord);
		return true;
	}

	@Override
	public boolean sendRedstoneUpdates() {

		return true;
	}

	@Override
	public void updateEntity() {

		super.updateEntity();
		if (ServerHelper.isServerWorld(worldObj) && worldObj.getTotalWorldTime() % CoFHProps.TIME_CONSTANT_HALF == 0 && redstoneControlOrDisable()) {
			if (!isEmpty()) {
				outputBuffer();
			}
			if (isEmpty()) {
				updateFakePlayer();
				breakBlock();
			}
		}
	}

	public void breakBlock() {

		int coords[] = BlockHelper.getAdjacentCoordinatesForSide(xCoord, yCoord, zCoord, facing);
		Block block = worldObj.getBlock(coords[0], coords[1], coords[2]);
		FluidStack theStack = FluidHelper.getFluidFromWorld(worldObj, coords[0], coords[1], coords[2]);
		if (theStack != null) {
			for (int i = 0; i < 6 && theStack.amount > 0; i++) {
				if (sideCache[i] == 1) {
					theStack.amount -= FluidHelper.insertFluidIntoAdjacentFluidHandler(this, i, theStack, true);
				}
			}
			worldObj.setBlockToAir(coords[0], coords[1], coords[2]);
		} else if (PlayerFake.isBlockBreakable(myFakePlayer, worldObj, coords[0], coords[1], coords[2])) {
			stuffedItems.addAll(BlockHelper.breakBlock(worldObj, coords[0], coords[1], coords[2], block, 0, true, false));
		}
	}

	public boolean isEmpty() {

		return stuffedItems.size() == 0;
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

	public void updateFakePlayer() {

		if (needsWorld) {
			myFakePlayer = new PlayerFake((WorldServer) worldObj);
			needsWorld = false;
		}
	}

	/* GUI METHODS */
	@Override
	public GuiContainer getGuiClient(InventoryPlayer inventory) {

		return new GuiBreaker(inventory, this);
	}

	@Override
	public Container getGuiServer(InventoryPlayer inventory) {

		return new ContainerTEBase(inventory, this);
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		NBTTagList list = nbt.getTagList("StuffedInv", 9);
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

		return TEProps.EMPTY_TANK_INFO;
	}

	/* IReconfigurableFacing */
	@Override
	public boolean allowYAxisFacing() {

		return true;
	}

	@Override
	public boolean setFacing(int side) {

		if (side < 0 || side > 5) {
			return false;
		}
		facing = (byte) side;
		sideCache[facing ^ 1] = 1;
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
		sendUpdatePacket(Side.CLIENT);
		return true;
	}

	/* IReconfigurableSides */
	@Override
	public int getNumConfig(int side) {

		return 2;
	}

	/* ISidedTexture */
	@Override
	public IIcon getTexture(int side, int pass) {

		if (pass == 0) {
			return side != facing ? IconRegistry.getIcon("DeviceSide") : redstoneControlOrDisable() ? IconRegistry.getIcon("DeviceActive", getType())
					: IconRegistry.getIcon("DeviceFace", getType());
		} else if (side < 6) {
			return IconRegistry.getIcon(TEProps.textureSelection, SIDE_TEX[sideCache[side]]);
		}
		return IconRegistry.getIcon("DeviceSide");
	}
}
