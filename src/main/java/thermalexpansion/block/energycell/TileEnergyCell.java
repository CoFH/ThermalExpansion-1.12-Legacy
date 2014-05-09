package thermalexpansion.block.energycell;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyStorage;
import cofh.network.ITileInfoPacketHandler;
import cofh.render.IconRegistry;
import cofh.util.BlockHelper;
import cofh.util.EnergyHelper;
import cofh.util.MathHelper;
import cofh.util.ServerHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.block.TileReconfigurableBase;
import thermalexpansion.core.TEProps;

public class TileEnergyCell extends TileReconfigurableBase implements ITileInfoPacketHandler, IEnergyHandler {

	public static void initialize() {

		GameRegistry.registerTileEntity(TileEnergyCell.class, "cofh.thermalexpansion.EnergyCell");
		guiId = ThermalExpansion.proxy.registerGui("EnergyCell", null, "TEBase", null, true);
	}

	protected static int guiId;

	public static final int[] MAX_SEND = { 10000, 80, 400, 2000, 10000 };
	public static final int[] MAX_RECEIVE = { 0, 80, 400, 2000, 10000 };
	public static final int[] STORAGE = { 10000, 400000, 2000000, 10000000, 50000000 };
	public static final byte[] DEFAULT_SIDES = { 1, 2, 2, 2, 2, 2 };

	public byte type;

	int energyTracker;
	int compareTracker;
	byte outputTracker;
	EnergyStorage energyStorage;

	boolean cached = false;
	IEnergyHandler[] adjacentHandlers = new IEnergyHandler[6];

	public int energyReceive;
	public int energySend;

	public TileEnergyCell() {

		energyStorage = new EnergyStorage(STORAGE[1], MAX_RECEIVE[1]);
	}

	public TileEnergyCell(int metadata) {

		energyStorage = new EnergyStorage(STORAGE[metadata], MAX_RECEIVE[metadata]);
		type = (byte) metadata;
	}

	@Override
	public int getComparatorInput(int side) {

		return compareTracker;
	}

	@Override
	public int getLightValue() {

		return Math.min(8, getScaledEnergyStored(9));
	}

	@Override
	public String getName() {

		return "tile.thermalexpansion.energycell." + BlockEnergyCell.NAMES[getType()] + ".name";
	}

	@Override
	public int getType() {

		return type;
	}

	@Override
	public boolean openGui(EntityPlayer player) {

		player.openGui(ThermalExpansion.instance, guiId, worldObj, xCoord, yCoord, zCoord);
		return true;
	}

	@Override
	public void onNeighborBlockChange() {

		super.onNeighborBlockChange();
		updateAdjacentHandlers();
	}

	@Override
	public void onNeighborTileChange(int tileX, int tileY, int tileZ) {

		super.onNeighborTileChange(tileX, tileY, tileZ);
		updateAdjacentHandler(tileX, tileY, tileZ);
	}

	protected void updateAdjacentHandlers() {

		if (ServerHelper.isClientWorld(worldObj)) {
			return;
		}
		for (int i = 0; i < 6; i++) {
			TileEntity tile = BlockHelper.getAdjacentTileEntity(this, i);

			if (EnergyHelper.isEnergyHandlerFromSide(tile, ForgeDirection.VALID_DIRECTIONS[i ^ 1])) {
				adjacentHandlers[i] = (IEnergyHandler) tile;
			} else {
				adjacentHandlers[i] = null;
			}
		}
		cached = true;
	}

	protected void updateAdjacentHandler(int x, int y, int z) {

		if (ServerHelper.isClientWorld(worldObj)) {
			return;
		}
		int side = BlockHelper.determineAdjacentSide(this, x, y, z);

		TileEntity tile = worldObj.getTileEntity(x, y, z);

		if (EnergyHelper.isEnergyHandlerFromSide(tile, ForgeDirection.VALID_DIRECTIONS[side ^ 1])) {
			adjacentHandlers[side] = (IEnergyHandler) tile;
		} else {
			adjacentHandlers[side] = null;
		}
	}

	@Override
	public void updateEntity() {

		if (ServerHelper.isClientWorld(worldObj)) {
			return;
		}
		if (!cached) {
			onNeighborBlockChange();
		}
		if (redstoneControlOrDisable()) {
			for (int i = outputTracker; i < 6 && energyStorage.getEnergyStored() > 0; i++) {
				transferEnergy(i);
			}
			for (int i = 0; i < outputTracker && energyStorage.getEnergyStored() > 0; i++) {
				transferEnergy(i);
			}
			++outputTracker;
			outputTracker %= 6;
		}
		if (timeCheck()) {

			int curScale = getScaledEnergyStored(15);
			if (curScale != compareTracker) {
				compareTracker = curScale;
				callNeighborTileChange();
			}

			int energyStage = getLightValue();

			if (energyTracker != energyStage) {
				energyTracker = energyStage;
				sendUpdatePacket(Side.CLIENT);
			}
		}
	}

	public void setEnergyStored(int quantity) {

		energyStorage.setEnergyStored(quantity);
	}

	protected void transferEnergy(int bSide) {

		if (sideCache[bSide] != SideType.OUTPUT) {
			return;
		}
		if (adjacentHandlers[bSide] == null) {
			return;
		}
		energyStorage.modifyEnergyStored(-adjacentHandlers[bSide].receiveEnergy(ForgeDirection.VALID_DIRECTIONS[bSide ^ 1],
				Math.min(energySend, energyStorage.getEnergyStored()), false));
	}

	public IEnergyStorage getEnergyStorage() {

		return energyStorage;
	}

	/* NETWORK METHODS */
	@Override
	public Payload getDescriptionPayload() {

		Payload payload = super.getDescriptionPayload();

		payload.addInt(energyStorage.getEnergyStored());

		return payload;
	}

	public Payload getGuiPayload() {

		Payload payload = Payload.getInfoPayload(this);

		payload.addByte(TEProps.PacketID.GUI.ordinal());

		payload.addInt(energySend);
		payload.addInt(energyReceive);
		payload.addInt(energyStorage.getEnergyStored());

		return payload;
	}

	public Payload getModePayload() {

		Payload payload = Payload.getInfoPayload(this);

		payload.addByte(TEProps.PacketID.MODE.ordinal());
		payload.addInt(MathHelper.clampI(energySend, 0, MAX_SEND[getType()]));
		payload.addInt(MathHelper.clampI(energyReceive, 0, MAX_RECEIVE[getType()]));

		return payload;
	}

	/* ITilePacketHandler */
	@Override
	public void handleTilePacket(Payload payload) {

		super.handleTilePacket(payload);

		if (ServerHelper.isClientWorld(worldObj)) {
			energyStorage.setEnergyStored(payload.getInt());
		}
	}

	/* ITileInfoPacketHandler */
	@Override
	public void handleTileInfoPacket(Payload payload, NetHandler handler) {

		switch (TEProps.PacketID.values()[payload.getByte()]) {
		case GUI:
			energySend = payload.getInt();
			energyReceive = payload.getInt();
			energyStorage.setEnergyStored(payload.getInt());
			return;
		case MODE:
			energySend = payload.getInt();
			energyReceive = payload.getInt();
			return;
		default:
		}
	}

	public void sendModePacket() {

		if (ServerHelper.isClientWorld(worldObj)) {
			PacketUtils.sendToServer(getModePayload().getPacket());
		}
	}

	/* GUI METHODS */
	public int getEnergy() {

		return energyStorage.getEnergyStored();
	}

	public int getMaxEnergy() {

		return energyStorage.getMaxEnergyStored();
	}

	public int getScaledEnergyStored(int scale) {

		return energyStorage.getEnergyStored() * scale / energyStorage.getMaxEnergyStored();
	}

	@Override
	public void sendGuiNetworkData(Container container, ICrafting iCrafting) {

		if (iCrafting instanceof EntityPlayer) {
			if (ServerHelper.isServerWorld(worldObj)) {
				PacketUtils.sendToPlayer(getGuiPayload().getPacket(), (EntityPlayer) iCrafting);
			}
		}
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		type = nbt.getByte("Type");
		outputTracker = nbt.getByte("Tracker");
		energySend = nbt.getInteger("Send");
		energyReceive = nbt.getInteger("Recv");

		energySend = MathHelper.clampI(energySend, 0, MAX_SEND[type]);
		energyReceive = MathHelper.clampI(energyReceive, 0, MAX_RECEIVE[type]);

		energyStorage = new EnergyStorage(STORAGE[type], MAX_RECEIVE[type]);
		energyStorage.readFromNBT(nbt);
		energyTracker = Math.min(8, getScaledEnergyStored(9));
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setByte("Type", type);
		nbt.setByte("Tracker", outputTracker);
		nbt.setInteger("Send", energySend);
		nbt.setInteger("Recv", energyReceive);

		energyStorage.writeToNBT(nbt);
	}

	/* IEnergyHandler */
	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {

		if (from == ForgeDirection.UNKNOWN || sideCache[from.ordinal()] == SideType.INPUT) {
			return energyStorage.receiveEnergy(Math.min(maxReceive, energyReceive), simulate);
		}
		return 0;
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {

		if (from == ForgeDirection.UNKNOWN || sideCache[from.ordinal()] == SideType.OUTPUT) {
			return energyStorage.extractEnergy(Math.min(maxExtract, energySend), simulate);
		}
		return 0;
	}

	@Override
	public boolean canInterface(ForgeDirection from) {

		if (from == ForgeDirection.UNKNOWN) {
			return false;
		}
		return sideCache[from.ordinal()] > 0;
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {

		return energyStorage.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {

		return energyStorage.getMaxEnergyStored();
	}

	/* IReconfigurableFacing */
	@Override
	public boolean allowYAxisFacing() {

		return false;
	}

	@Override
	public boolean rotateBlock() {

		super.rotateBlock();
		updateAdjacentHandlers();
		return true;
	}

	/* IReconfigurableSides */
	@Override
	public int getNumConfig(int side) {

		return 3;
	}

	/* ISidedBlockTexture */
	@Override
	public IIcon getBlockTexture(int side, int pass) {

		if (pass == 0) {
			return type < 2 ? IconRegistry.getIcon("StorageRedstone") : IconRegistry.getIcon("FluidRedstone");
		} else if (pass == 1) {
			return IconRegistry.getIcon("Cell", type * 2);
		} else if (pass == 2) {
			return IconRegistry.getIcon(BlockEnergyCell.textureSelection, sideCache[side]);
		}
		int energy = Math.min(8, getScaledEnergyStored(9));
		return side != facing ? IconRegistry.getIcon(BlockEnergyCell.textureSelection, 0) : IconRegistry.getIcon("CellMeter", energy);
	}

	/* SIDE TYPE */
	public static class SideType {

		public static final byte NONE = 0;
		public static final byte OUTPUT = 1;
		public static final byte INPUT = 2;
	}

}
