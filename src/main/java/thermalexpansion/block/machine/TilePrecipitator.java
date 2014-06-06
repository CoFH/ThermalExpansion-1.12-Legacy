package thermalexpansion.block.machine;

import cofh.network.CoFHPacket;
import cofh.util.ServerHelper;
import cofh.util.fluid.FluidTankAdv;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.core.TEProps;

public class TilePrecipitator extends TileMachineEnergized implements IFluidHandler {

	public static final int TYPE = BlockMachine.Types.PRECIPITATOR.ordinal();

	public static void initialize() {

		processItems[0] = new ItemStack(Items.snowball, 4, 0);
		processItems[1] = new ItemStack(Blocks.snow);
		processItems[2] = new ItemStack(Blocks.ice);

		sideData[TYPE] = new SideConfig();
		sideData[TYPE].numGroup = 3;
		sideData[TYPE].slotGroups = new int[][] { {}, {}, { 0 } };
		sideData[TYPE].allowInsertion = new boolean[] { false, true, false };
		sideData[TYPE].allowExtraction = new boolean[] { false, false, true };
		sideData[TYPE].sideTex = new int[] { 0, 1, 4 };

		energyData[TYPE] = new EnergyConfig();
		energyData[TYPE].setParamsPower(20);

		guiIds[TYPE] = ThermalExpansion.proxy.registerGui("Precipitator", "machine", true);
		GameRegistry.registerTileEntity(TilePrecipitator.class, "thermalexpansion.Precipitator");
	}

	static int[] processWater = { 500, 500, 1000 };
	static int[] processEnergy = { 800, 800, 1600 };

	static ItemStack[] processItems = new ItemStack[3];

	FluidStack renderFluid = new FluidStack(FluidRegistry.WATER, 0);
	FluidTankAdv tank = new FluidTankAdv(MAX_FLUID_SMALL);

	byte curSelection;
	byte prevSelection;

	int outputTracker;

	public TilePrecipitator() {

		super();

		sideCache = new byte[] { 2, 2, 1, 1, 1, 1 };
		inventory = new ItemStack[1 + 1 + 3];

		inventory[2] = processItems[0];
		inventory[3] = processItems[1];
		inventory[4] = processItems[2];
	}

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	public int getChargeSlot() {

		return 1;
	}

	@Override
	public boolean canStart() {

		if (energyStorage.getEnergyStored() < processEnergy[curSelection] || tank.getFluidAmount() < processWater[curSelection]) {
			return false;
		}
		if (inventory[0] == null) {
			return true;
		}
		if (!inventory[0].isItemEqual(processItems[curSelection])) {
			return false;
		}
		return inventory[0].stackSize + processItems[curSelection].stackSize <= processItems[prevSelection].getMaxStackSize();
	}

	@Override
	public boolean canFinish() {

		return processRem <= 0;
	}

	@Override
	protected void processStart() {

		processMax = processEnergy[curSelection];
		processRem = processMax;
		prevSelection = curSelection;
	}

	@Override
	protected void processFinish() {

		if (inventory[0] == null) {
			inventory[0] = processItems[prevSelection].copy();
		} else {
			inventory[0].stackSize += processItems[prevSelection].stackSize;
		}
		tank.drain(processWater[prevSelection], true);
		prevSelection = curSelection;
	}

	@Override
	protected void transferProducts() {

		if (!upgradeAutoTransfer) {
			return;
		}
		if (inventory[0] == null) {
			return;
		}
		int side;
		for (int i = outputTracker + 1; i <= outputTracker + 6; i++) {
			side = i % 6;

			if (sideCache[side] == 2) {
				if (transferItem(0, 4, side)) {
					outputTracker = side;
					break;
				}
			}
		}
	}

	/* NETWORK METHODS */
	@Override
	public CoFHPacket getPacket() {

		CoFHPacket payload = super.getPacket();

		payload.addFluidStack(renderFluid);
		return payload;
	}

	@Override
	public CoFHPacket getGuiCoFHPacket() {

		CoFHPacket payload = super.getGuiCoFHPacket();

		payload.addByte(curSelection);
		payload.addByte(prevSelection);

		if (tank.getFluid() == null) {
			payload.addFluidStack(renderFluid);
		} else {
			payload.addFluidStack(tank.getFluid());
		}
		return payload;
	}

	@Override
	public CoFHPacket getFluidCoFHPacket() {

		CoFHPacket payload = super.getFluidCoFHPacket();

		payload.addFluidStack(renderFluid);

		return payload;
	}

	@Override
	public CoFHPacket getModeCoFHPacket() {

		CoFHPacket payload = super.getModeCoFHPacket();

		payload.addByte(curSelection);

		return payload;
	}

	public void setMode(int i) {

		byte lastSelection = curSelection;
		curSelection = (byte) i;

		if (ServerHelper.isClientWorld(worldObj)) {
			sendModePacket();
		}
		curSelection = lastSelection;
	}

	/* ITilePacketHandler */
	@Override
	public void handleTilePacket(CoFHPacket payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		if (ServerHelper.isClientWorld(worldObj)) {
			renderFluid = payload.getFluidStack();
		} else {
			payload.getFluidStack();
		}
	}

	/* ITileInfoPacketHandler */
	@Override
	public void handleTileInfoPacket(CoFHPacket payload, boolean isServer, EntityPlayer thePlayer) {

		switch (TEProps.PacketID.values()[payload.getByte()]) {
		case GUI:
			isActive = payload.getBool();
			processMax = payload.getInt();
			processRem = payload.getInt();
			energyStorage.setEnergyStored(payload.getInt());
			curSelection = payload.getByte();
			prevSelection = payload.getByte();
			tank.setFluid(payload.getFluidStack());
			return;
		case FLUID:
			renderFluid = payload.getFluidStack();
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			return;
		case MODE:
			curSelection = payload.getByte();

			if (!isActive) {
				prevSelection = curSelection;
			}
			worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
			return;
		default:
		}
	}

	/* GUI METHODS */
	public FluidTankAdv getTank() {

		return tank;
	}

	public FluidStack getTankFluid() {

		return tank.getFluid();
	}

	public int getCurSelection() {

		return curSelection;
	}

	public int getPrevSelection() {

		return prevSelection;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		outputTracker = nbt.getInteger("Tracker");
		prevSelection = nbt.getByte("Prev");
		curSelection = nbt.getByte("Sel");
		tank.readFromNBT(nbt);

		if (tank.getFluid() != null) {
			renderFluid = tank.getFluid();
		}
		inventory[2] = processItems[0];
		inventory[3] = processItems[1];
		inventory[4] = processItems[2];
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("Tracker", outputTracker);
		nbt.setByte("Prev", prevSelection);
		nbt.setByte("Sel", curSelection);
		tank.writeToNBT(nbt);
	}

	/* IInventory */
	@Override
	public int getSizeInventory() {

		return inventory.length - 3;
	}

	/* IFluidHandler */
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {

		if (from != ForgeDirection.UNKNOWN && sideCache[from.ordinal()] != 1) {
			return 0;
		}
		if (resource.getFluid() != FluidRegistry.WATER) {
			return 0;
		}
		return tank.fill(resource, doFill);
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

		return true;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {

		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {

		return new FluidTankInfo[] { tank.getInfo() };
	}

}
