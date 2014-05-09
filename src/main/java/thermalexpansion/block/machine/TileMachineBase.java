package thermalexpansion.block.machine;

import cofh.network.ITileInfoPacketHandler;
import cofh.network.ITilePacketHandler;
import cofh.render.IconRegistry;
import cofh.util.BlockHelper;
import cofh.util.ServerHelper;
import cofh.util.TimeTracker;
import cpw.mods.fml.relauncher.Side;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.FluidContainerRegistry;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.block.TileReconfigurableInventory;
import thermalexpansion.core.TEProps;

public abstract class TileMachineBase extends TileReconfigurableInventory implements ISidedInventory, ITilePacketHandler, ITileInfoPacketHandler {

	public static class SideConfig {

		public int numGroup;
		public int[][] slotGroups;
		public boolean[] allowInsertion;
		public boolean[] allowExtraction;
		public int[] sideTex;
	}

	protected static final SideConfig[] sideData = new SideConfig[BlockMachine.Types.values().length];
	protected static final int[] guiIds = new int[BlockMachine.Types.values().length];
	protected static final int[] LIGHT_VALUE = { 14, 0, 0, 15, 15, 0, 0, 14, 0, 0, 7 };

	protected static final int CHANCE = 100;
	protected static final int RATE = 25;
	protected static final int MAX_FLUID_SMALL = FluidContainerRegistry.BUCKET_VOLUME * 4;
	protected static final int MAX_FLUID_LARGE = FluidContainerRegistry.BUCKET_VOLUME * 10;

	TimeTracker tracker = new TimeTracker();
	boolean wasActive;

	int processMax;
	int processRem;

	public int getMaxInputSlot() {

		return 0;
	}

	public void updateIfChanged(boolean curActive) {

		if (curActive != isActive && isActive == true) {
			sendUpdatePacket(Side.CLIENT);
		} else if (tracker.hasDelayPassed(worldObj, 200) && wasActive) {
			wasActive = false;
			sendUpdatePacket(Side.CLIENT);
		}
	}

	@Override
	public int getLightValue() {

		return isActive ? LIGHT_VALUE[getType()] : 0;
	}

	@Override
	public String getName() {

		return "tile.thermalexpansion.machine." + BlockMachine.NAMES[getType()] + ".name";
	}

	/* NETWORK METHODS */
	public Payload getGuiPayload() {

		Payload payload = Payload.getInfoPayload(this);

		payload.addByte(TEProps.PacketID.GUI.ordinal());
		payload.addBool(isActive);
		payload.addInt(processMax);
		payload.addInt(processRem);

		return payload;
	}

	public Payload getFluidPayload() {

		Payload payload = Payload.getInfoPayload(this);

		payload.addByte(TEProps.PacketID.FLUID.ordinal());

		return payload;
	}

	public Payload getModePayload() {

		Payload payload = Payload.getInfoPayload(this);

		payload.addByte(TEProps.PacketID.MODE.ordinal());

		return payload;
	}

	public void sendFluidPacket() {

		PacketUtils.sendToAllPlayers(getFluidPayload().getPacket(), worldObj);
	}

	public void sendModePacket() {

		if (ServerHelper.isClientWorld(worldObj)) {
			PacketUtils.sendToServer(getModePayload().getPacket());
		}
	}

	/* ITileInfoPacketHandler */
	@Override
	public void handleTileInfoPacket(Payload payload, NetHandler handler) {

		switch (TEProps.PacketID.values()[payload.getByte()]) {
		case GUI:
			isActive = payload.getBool();
			processMax = payload.getInt();
			processRem = payload.getInt();
			return;
		default:
		}
	}

	/* GUI METHODS */
	@Override
	public boolean openGui(EntityPlayer player) {

		player.openGui(ThermalExpansion.instance, guiIds[getType()], worldObj, xCoord, yCoord, zCoord);
		return true;
	}

	public int getScaledProgress(int scale) {

		if (!isActive || processMax <= 0 || processRem <= 0) {
			return 0;
		}
		return scale * (processMax - processRem) / processMax;
	}

	@Override
	public void sendGuiNetworkData(Container container, ICrafting iCrafting) {

		if (iCrafting instanceof EntityPlayer) {
			if (ServerHelper.isServerWorld(worldObj)) {
				PacketUtils.sendToPlayer(getGuiPayload().getPacket(), (EntityPlayer) iCrafting);
			}
		}
	}

	public boolean canAcceptItem(ItemStack stack, int slot, int side) {

		return true;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		processMax = nbt.getInteger("ProcMax");
		processRem = nbt.getInteger("ProcRem");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("ProcMax", processMax);
		nbt.setInteger("ProcRem", processRem);
	}

	/* IReconfigurableFacing */
	@Override
	public boolean setFacing(int side) {

		if (side < 0 || side > 5) {
			return false;
		}
		sideCache[side] = 0;
		sideCache[BlockHelper.SIDE_LEFT[side]] = 1;
		sideCache[BlockHelper.SIDE_OPPOSITE[side]] = 1;
		facing = (byte) side;
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
		sendUpdatePacket(Side.CLIENT);
		return true;
	}

	/* IReconfigurableSides */
	@Override
	public int getNumConfig(int side) {

		return sideData[getType()].numGroup;
	}

	/* ISidedBlockTexture */
	@Override
	public IIcon getBlockTexture(int side, int pass) {

		if (pass == 0) {
			if (side == 0) {
				return IconRegistry.getIcon("MachineBottom");
			} else if (side == 1) {
				return IconRegistry.getIcon("MachineTop");
			}
			return side != facing ? IconRegistry.getIcon("MachineSide") : isActive ? IconRegistry.getIcon("MachineActive_", getType()) : IconRegistry.getIcon(
					"MachineFace_", getType());
		} else if (side < 6) {
			return IconRegistry.getIcon(TEProps.textureSelection, sideData[getType()].sideTex[sideCache[side]]);
		}
		return IconRegistry.getIcon("MachineSide");
	}

	/* ISidedInventory */
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {

		return sideData[getType()].slotGroups[sideCache[side]];
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int side) {

		return sideData[getType()].allowInsertion[sideCache[side]] ? canAcceptItem(stack, slot, side) : false;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int side) {

		return sideData[getType()].allowExtraction[sideCache[side]];
	}

}
