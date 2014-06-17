package thermalexpansion.block.cell;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyStorage;
import cofh.network.CoFHPacket;
import cofh.network.CoFHTileInfoPacket;
import cofh.network.ITileInfoPacketHandler;
import cofh.network.PacketHandler;
import cofh.render.IconRegistry;
import cofh.util.BlockHelper;
import cofh.util.EnergyHelper;
import cofh.util.MathHelper;
import cofh.util.ServerHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.block.TileReconfigurableBase;
import thermalexpansion.core.TEProps;
import thermalexpansion.gui.client.GuiCell;
import thermalexpansion.gui.container.ContainerTEBase;

public class TileCell extends TileReconfigurableBase implements ITileInfoPacketHandler, IEnergyHandler {

	public static void initialize() {

		GameRegistry.registerTileEntity(TileCell.class, "thermalexpansion.Cell");
	}

	public static int[] MAX_SEND = { 10000, 80, 400, 2000, 10000 };
	public static int[] MAX_RECEIVE = { 0, 80, 400, 2000, 10000 };
	public static int[] STORAGE = { 10000, 400000, 2000000, 10000000, 50000000 };
	public static final byte[] DEFAULT_SIDES = { 1, 2, 2, 2, 2, 2 };

	static {
		String category = "block.tweak";
		STORAGE[4] = MathHelper.clampI(ThermalExpansion.config.get(category, "Cell.Resonant.Storage", STORAGE[4]), STORAGE[4] / 10, STORAGE[4] * 40);
		STORAGE[3] = MathHelper.clampI(ThermalExpansion.config.get(category, "Cell.Reinforced.Storage", STORAGE[3]), STORAGE[3] / 10, STORAGE[4]);
		STORAGE[2] = MathHelper.clampI(ThermalExpansion.config.get(category, "Cell.Hardened.Storage", STORAGE[2]), STORAGE[2] / 10, STORAGE[3]);
		STORAGE[1] = MathHelper.clampI(ThermalExpansion.config.get(category, "Cell.Basic.Storage", STORAGE[1]), STORAGE[1] / 10, STORAGE[2]);

		MAX_SEND[4] = MathHelper.clampI(ThermalExpansion.config.get(category, "Cell.Resonant.MaxSend", MAX_SEND[4]), MAX_SEND[4] / 10, MAX_SEND[4] * 1000);
		MAX_SEND[3] = MathHelper.clampI(ThermalExpansion.config.get(category, "Cell.Reinforced.MaxSend", MAX_SEND[3]), MAX_SEND[3] / 10, MAX_SEND[3] * 1000);
		MAX_SEND[2] = MathHelper.clampI(ThermalExpansion.config.get(category, "Cell.Hardened.MaxSend", MAX_SEND[2]), MAX_SEND[2] / 10, MAX_SEND[2] * 1000);
		MAX_SEND[1] = MathHelper.clampI(ThermalExpansion.config.get(category, "Cell.Basic.MaxSend", MAX_SEND[1]), MAX_SEND[1] / 10, MAX_SEND[1] * 1000);

		MAX_RECEIVE[4] = MathHelper.clampI(ThermalExpansion.config.get(category, "Cell.Resonant.MaxReceive", MAX_RECEIVE[4]), MAX_RECEIVE[4] / 10,
				MAX_RECEIVE[4] * 1000);
		MAX_RECEIVE[3] = MathHelper.clampI(ThermalExpansion.config.get(category, "Cell.Reinforced.MaxReceive", MAX_RECEIVE[3]), MAX_RECEIVE[3] / 10,
				MAX_RECEIVE[3] * 1000);
		MAX_RECEIVE[2] = MathHelper.clampI(ThermalExpansion.config.get(category, "Cell.Hardened.MaxReceive", MAX_RECEIVE[2]), MAX_RECEIVE[2] / 10,
				MAX_RECEIVE[2] * 1000);
		MAX_RECEIVE[1] = MathHelper.clampI(ThermalExpansion.config.get(category, "Cell.Basic.MaxReceive", MAX_RECEIVE[1]), MAX_RECEIVE[1] / 10,
				MAX_RECEIVE[1] * 1000);

		MAX_SEND[0] = MathHelper.clampI(ThermalExpansion.config.get(category, "Cell.Creative.MaxSend", MAX_SEND[0]), MAX_SEND[0] / 10, MAX_SEND[0] * 1000);
		STORAGE[0] = MAX_SEND[0];
	}

	public byte type;

	int meterTracker;
	int compareTracker;
	byte outputTracker;
	EnergyStorage energyStorage;

	boolean cached = false;
	IEnergyHandler[] adjacentHandlers = new IEnergyHandler[6];

	public int energyReceive;
	public int energySend;

	public TileCell() {

		energyStorage = new EnergyStorage(STORAGE[1], MAX_RECEIVE[1]);
	}

	public TileCell(int metadata) {

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

		return "tile.thermalexpansion.cell." + BlockCell.NAMES[getType()] + ".name";
	}

	@Override
	public int getType() {

		return type;
	}

	@Override
	public boolean openGui(EntityPlayer player) {

		player.openGui(ThermalExpansion.instance, 0, worldObj, xCoord, yCoord, zCoord);
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

			if (compareTracker != curScale) {
				compareTracker = curScale;
				callNeighborTileChange();
			}
			curScale = getLightValue();

			if (meterTracker != curScale) {
				meterTracker = curScale;
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
	public CoFHPacket getPacket() {

		CoFHPacket payload = super.getPacket();
		payload.addInt(energyStorage.getEnergyStored());
		return payload;
	}

	public CoFHPacket getGuiPacket() {

		CoFHPacket payload = CoFHTileInfoPacket.newPacket(this);
		payload.addByte(TEProps.PacketID.GUI.ordinal());
		payload.addInt(energySend);
		payload.addInt(energyReceive);
		payload.addInt(energyStorage.getEnergyStored());
		return payload;
	}

	public CoFHPacket getModePacket() {

		CoFHPacket payload = CoFHTileInfoPacket.newPacket(this);
		payload.addByte(TEProps.PacketID.MODE.ordinal());
		payload.addInt(MathHelper.clampI(energySend, 0, MAX_SEND[getType()]));
		payload.addInt(MathHelper.clampI(energyReceive, 0, MAX_RECEIVE[getType()]));
		return payload;
	}

	/* ITilePacketHandler */
	@Override
	public void handleTilePacket(CoFHPacket payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		if (ServerHelper.isClientWorld(worldObj)) {
			energyStorage.setEnergyStored(payload.getInt());
		}
	}

	/* ITileInfoPacketHandler */
	@Override
	public void handleTileInfoPacket(CoFHPacket payload, boolean isServer, EntityPlayer thePlayer) {

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
			PacketHandler.sendToServer(getModePacket());
		}
	}

	/* GUI METHODS */
	@Override
	public GuiContainer getGuiClient(InventoryPlayer inventory) {

		return new GuiCell(inventory, this);
	}

	@Override
	public Container getGuiServer(InventoryPlayer inventory) {

		return new ContainerTEBase(inventory, this);
	}

	@Override
	public void sendGuiNetworkData(Container container, ICrafting iCrafting) {

		if (iCrafting instanceof EntityPlayer) {
			if (ServerHelper.isServerWorld(worldObj)) {
				PacketHandler.sendTo(getGuiPacket(), (EntityPlayer) iCrafting);
			}
		}
	}

	public int getEnergy() {

		return energyStorage.getEnergyStored();
	}

	public int getMaxEnergy() {

		return energyStorage.getMaxEnergyStored();
	}

	public int getScaledEnergyStored(int scale) {

		return energyStorage.getEnergyStored() * scale / energyStorage.getMaxEnergyStored();
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
		meterTracker = Math.min(8, getScaledEnergyStored(9));
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
	public boolean canConnectEnergy(ForgeDirection from) {

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
	public IIcon getTexture(int side, int pass) {

		if (pass == 0) {
			return type < 2 ? IconRegistry.getIcon("StorageRedstone") : IconRegistry.getIcon("FluidRedstone");
		} else if (pass == 1) {
			return IconRegistry.getIcon("Cell", type * 2);
		} else if (pass == 2) {
			return IconRegistry.getIcon(BlockCell.textureSelection, sideCache[side]);
		}
		if (side != facing) {
			return IconRegistry.getIcon(BlockCell.textureSelection, 0);
		}
		int stored = Math.min(8, getScaledEnergyStored(9));
		return IconRegistry.getIcon("CellMeter", stored);
	}

	/* SIDE TYPE */
	public static class SideType {

		public static final byte NONE = 0;
		public static final byte OUTPUT = 1;
		public static final byte INPUT = 2;
	}

}
