package cofh.thermalexpansion.block.cell;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyReceiver;
import cofh.core.network.PacketCoFHBase;
import cofh.core.render.IconRegistry;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.EnergyHelper;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.helpers.RedstoneControlHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.TileReconfigurable;
import cofh.thermalexpansion.gui.client.GuiCell;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import cofh.thermalexpansion.util.helpers.ReconfigurableHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

public class TileCell extends TileReconfigurable implements IEnergyHandler {

	public static void initialize() {

		GameRegistry.registerTileEntity(TileCell.class, "thermalexpansion.Cell");
		configure();
	}

	public static void configure() {

		String comment = "Enable this to allow for Energy Cells to be securable.";
		enableSecurity = ThermalExpansion.config.get("Security", "Cell.All.Securable", enableSecurity, comment);
	}

	public static boolean enableSecurity = true;

	public static int[] MAX_SEND = { 100000, 200, 800, 8000, 32000 };
	public static int[] MAX_RECEIVE = { 100000, 200, 800, 8000, 32000 };
	public static int[] CAPACITY = { 100000, 400000, 2000000, 20000000, 80000000 };
	public static final byte[] DEFAULT_SIDES = { 1, 2, 2, 2, 2, 2 };

	static {
		String category2 = "Cell.";

		String category = category2 + StringHelper.titleCase(BlockCell.NAMES[4]);
		CAPACITY[4] = MathHelper.clamp(ThermalExpansion.config.get(category, "Capacity", CAPACITY[4]), CAPACITY[4] / 10, 1000000 * 1000);
		MAX_SEND[4] = MathHelper.clamp(ThermalExpansion.config.get(category, "MaxSend", MAX_SEND[4]), MAX_SEND[4] / 10, MAX_SEND[4] * 1000);
		MAX_RECEIVE[4] = MathHelper.clamp(ThermalExpansion.config.get(category, "MaxReceive", MAX_RECEIVE[4]), MAX_RECEIVE[4] / 10, MAX_RECEIVE[4] * 1000);

		category = category2 + StringHelper.titleCase(BlockCell.NAMES[3]);
		CAPACITY[3] = MathHelper.clamp(ThermalExpansion.config.get(category, "Capacity", CAPACITY[3]), CAPACITY[3] / 10, CAPACITY[4]);
		MAX_SEND[3] = MathHelper.clamp(ThermalExpansion.config.get(category, "MaxSend", MAX_SEND[3]), MAX_SEND[3] / 10, MAX_SEND[3] * 1000);
		MAX_RECEIVE[3] = MathHelper.clamp(ThermalExpansion.config.get(category, "MaxReceive", MAX_RECEIVE[3]), MAX_RECEIVE[3] / 10, MAX_RECEIVE[3] * 1000);

		category = category2 + StringHelper.titleCase(BlockCell.NAMES[2]);
		CAPACITY[2] = MathHelper.clamp(ThermalExpansion.config.get(category, "Capacity", CAPACITY[2]), CAPACITY[2] / 10, CAPACITY[3]);
		MAX_RECEIVE[2] = MathHelper.clamp(ThermalExpansion.config.get(category, "MaxReceive", MAX_RECEIVE[2]), MAX_RECEIVE[2] / 10, MAX_RECEIVE[2] * 1000);
		MAX_SEND[2] = MathHelper.clamp(ThermalExpansion.config.get(category, "MaxSend", MAX_SEND[2]), MAX_SEND[2] / 10, MAX_SEND[2] * 1000);

		category = category2 + StringHelper.titleCase(BlockCell.NAMES[1]);
		CAPACITY[1] = MathHelper.clamp(ThermalExpansion.config.get(category, "Capacity", CAPACITY[1]), CAPACITY[1] / 10, CAPACITY[2]);
		MAX_SEND[1] = MathHelper.clamp(ThermalExpansion.config.get(category, "MaxSend", MAX_SEND[1]), MAX_SEND[1] / 10, MAX_SEND[1] * 1000);
		MAX_RECEIVE[1] = MathHelper.clamp(ThermalExpansion.config.get(category, "MaxReceive", MAX_RECEIVE[1]), MAX_RECEIVE[1] / 10, MAX_RECEIVE[1] * 1000);

		category = category2 + StringHelper.titleCase(BlockCell.NAMES[0]);
		MAX_SEND[0] = MathHelper.clamp(ThermalExpansion.config.get(category, "MaxValue", MAX_SEND[0]), MAX_SEND[0] / 10, MAX_SEND[0] * 1000);
		MAX_RECEIVE[0] = MAX_SEND[0];
		CAPACITY[0] = -1;
	}

	int compareTracker;
	byte meterTracker;
	byte outputTracker;

	boolean cached = false;
	IEnergyReceiver[] adjacentHandlers = new IEnergyReceiver[6];

	public int energyReceive;
	public int energySend;
	public byte type = 1;

	public TileCell() {

		energyStorage = new EnergyStorage(CAPACITY[1], MAX_RECEIVE[1]);
	}

	public TileCell(int metadata) {

		type = (byte) metadata;
		energyStorage = new EnergyStorage(CAPACITY[type], MAX_RECEIVE[type]);
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
	public int getComparatorInput(int side) {

		return compareTracker;
	}

	@Override
	public int getLightValue() {

		return Math.min(8, getScaledEnergyStored(9));
	}

	@Override
	public byte[] getDefaultSides() {

		return DEFAULT_SIDES.clone();
	}

	@Override
	public boolean enableSecurity() {

		return enableSecurity;
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
				meterTracker = (byte) curScale;
				updateLighting();
				sendUpdatePacket(Side.CLIENT);
			}
		}
	}

	@Override
	public void invalidate() {

		cached = false;
		super.invalidate();
	}

	protected void transferEnergy(int bSide) {

		if (sideCache[bSide] != 1) {
			return;
		}
		if (adjacentHandlers[bSide] == null) {
			return;
		}
		energyStorage.modifyEnergyStored(-adjacentHandlers[bSide].receiveEnergy(ForgeDirection.VALID_DIRECTIONS[bSide ^ 1],
				Math.min(energySend, energyStorage.getEnergyStored()), false));
	}

	protected void updateAdjacentHandlers() {

		if (ServerHelper.isClientWorld(worldObj)) {
			return;
		}
		for (int i = 0; i < 6; i++) {
			TileEntity tile = BlockHelper.getAdjacentTileEntity(this, i);

			if (EnergyHelper.isEnergyReceiverFromSide(tile, ForgeDirection.VALID_DIRECTIONS[i ^ 1])) {
				adjacentHandlers[i] = (IEnergyReceiver) tile;
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

		if (EnergyHelper.isEnergyReceiverFromSide(tile, ForgeDirection.VALID_DIRECTIONS[side ^ 1])) {
			adjacentHandlers[side] = (IEnergyReceiver) tile;
		} else {
			adjacentHandlers[side] = null;
		}
	}

	@Override
	protected boolean readPortableTagInternal(EntityPlayer player, NBTTagCompound tag) {

		rsMode = RedstoneControlHelper.getControlFromNBT(tag);
		int storedFacing = ReconfigurableHelper.getFacingFromNBT(tag);
		byte[] storedSideCache = ReconfigurableHelper.getSideCacheFromNBT(tag, getDefaultSides());

		sideCache[0] = storedSideCache[0];
		sideCache[1] = storedSideCache[1];
		sideCache[facing] = storedSideCache[storedFacing];
		sideCache[BlockHelper.getLeftSide(facing)] = storedSideCache[BlockHelper.getLeftSide(storedFacing)];
		sideCache[BlockHelper.getRightSide(facing)] = storedSideCache[BlockHelper.getRightSide(storedFacing)];
		sideCache[BlockHelper.getOppositeSide(facing)] = storedSideCache[BlockHelper.getOppositeSide(storedFacing)];

		for (int i = 0; i < 6; i++) {
			if (sideCache[i] >= getNumConfig(i)) {
				sideCache[i] = 0;
			}
		}
		energySend = (tag.getInteger("Send") * MAX_SEND[getType()]) / 1000;
		energyReceive = (tag.getInteger("Recv") * MAX_RECEIVE[getType()]) / 1000;

		return true;
	}

	@Override
	protected boolean writePortableTagInternal(EntityPlayer player, NBTTagCompound tag) {

		RedstoneControlHelper.setItemStackTagRS(tag, this);
		ReconfigurableHelper.setItemStackTagReconfig(tag, this);

		tag.setInteger("Send", (energySend * 1000) / MAX_SEND[getType()]);
		tag.setInteger("Recv", (energyReceive * 1000) / MAX_RECEIVE[getType()]);

		return true;
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiCell(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerTEBase(inventory, this);
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		type = nbt.getByte("Type");
		outputTracker = nbt.getByte("Tracker");
		energySend = MathHelper.clamp(nbt.getInteger("Send"), 0, MAX_SEND[type]);
		energyReceive = MathHelper.clamp(nbt.getInteger("Recv"), 0, MAX_RECEIVE[type]);

		energyStorage = new EnergyStorage(CAPACITY[type], MAX_RECEIVE[type]);
		energyStorage.readFromNBT(nbt);
		meterTracker = (byte) Math.min(8, getScaledEnergyStored(9));
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setByte("Type", type);
		nbt.setByte("Tracker", outputTracker);
		nbt.setInteger("Send", energySend);
		nbt.setInteger("Recv", energyReceive);
	}

	/* NETWORK METHODS */
	@Override
	public PacketCoFHBase getPacket() {

		PacketCoFHBase payload = super.getPacket();

		payload.addInt(energySend);
		payload.addInt(energyReceive);
		payload.addInt(energyStorage.getEnergyStored());

		return payload;
	}

	@Override
	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = super.getGuiPacket();

		payload.addInt(energySend);
		payload.addInt(energyReceive);
		payload.addInt(energyStorage.getEnergyStored());

		return payload;
	}

	@Override
	public PacketCoFHBase getModePacket() {

		PacketCoFHBase payload = super.getModePacket();

		payload.addInt(MathHelper.clamp(energySend, 0, MAX_SEND[getType()]));
		payload.addInt(MathHelper.clamp(energyReceive, 0, MAX_RECEIVE[getType()]));

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketCoFHBase payload) {

		super.handleGuiPacket(payload);

		energySend = payload.getInt();
		energyReceive = payload.getInt();
		energyStorage.setEnergyStored(payload.getInt());
	}

	@Override
	protected void handleModePacket(PacketCoFHBase payload) {

		super.handleModePacket(payload);

		energySend = payload.getInt();
		energyReceive = payload.getInt();
	}

	/* ITilePacketHandler */
	@Override
	public void handleTilePacket(PacketCoFHBase payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		if (!isServer) {
			energySend = payload.getInt();
			energyReceive = payload.getInt();
		} else {
			payload.getInt();
			payload.getInt();
		}
	}

	/* IEnergyHandler */
	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {

		if (from == ForgeDirection.UNKNOWN || sideCache[from.ordinal()] == 2) {
			return energyStorage.receiveEnergy(Math.min(maxReceive, energyReceive), simulate);
		}
		return 0;
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {

		if (from == ForgeDirection.UNKNOWN || sideCache[from.ordinal()] == 1) {
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

	/* IPortableData */
	@Override
	public String getDataType() {

		return "tile.thermalexpansion.cell";
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
	public final boolean decrSide(int side) {

		sideCache[side] += getNumConfig(side) - 1;
		sideCache[side] %= getNumConfig(side);
		sendUpdatePacket(Side.SERVER);
		return true;
	}

	@Override
	public final boolean incrSide(int side) {

		sideCache[side] += 1;
		sideCache[side] %= getNumConfig(side);
		sendUpdatePacket(Side.SERVER);
		return true;
	}

	@Override
	public boolean setSide(int side, int config) {

		if (sideCache[side] == config || config >= getNumConfig(side)) {
			return false;
		}
		sideCache[side] = (byte) config;
		sendUpdatePacket(Side.SERVER);
		return true;
	}

	@Override
	public int getNumConfig(int side) {

		return 3;
	}

	/* ISidedTexture */
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

}
