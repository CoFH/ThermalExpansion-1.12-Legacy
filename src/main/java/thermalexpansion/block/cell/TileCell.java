package thermalexpansion.block.cell;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import cofh.network.CoFHPacket;
import cofh.render.IconRegistry;
import cofh.util.BlockHelper;
import cofh.util.EnergyHelper;
import cofh.util.MathHelper;
import cofh.util.ServerHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.block.TileReconfigurable;
import thermalexpansion.gui.client.GuiCell;
import thermalexpansion.gui.container.ContainerTEBase;

public class TileCell extends TileReconfigurable {

	public static void initialize() {

		GameRegistry.registerTileEntity(TileCell.class, "thermalexpansion.Cell");
		configure();
	}

	public static void configure() {

		String comment = "Enable this to allow for Energy Cells to be securable. (Default: true)";
		enableSecurity = ThermalExpansion.config.get("security", "Cell.All.Secureable", enableSecurity, comment);
	}

	public static boolean enableSecurity = true;

	public static int[] MAX_SEND = { 20000, 80, 400, 2000, 10000 };
	public static int[] MAX_RECEIVE = { 20000, 80, 400, 2000, 10000 };
	public static int[] STORAGE = { 20000, 400000, 2000000, 10000000, 50000000 };
	public static final byte[] DEFAULT_SIDES = { 1, 2, 2, 2, 2, 2 };

	static {
		String category = "block.tweak";
		STORAGE[4] = MathHelper.clampI(ThermalExpansion.config.get(category, "Cell.Resonant.Storage", STORAGE[4]), STORAGE[4] / 10, 1000000 * 1000);
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

		MAX_SEND[0] = MathHelper.clampI(ThermalExpansion.config.get(category, "Cell.Creative.MaxValue", MAX_SEND[0]), MAX_SEND[0] / 10, MAX_SEND[0] * 1000);
		MAX_RECEIVE[0] = MAX_SEND[0];
		STORAGE[0] = MAX_SEND[0];
	}

	int compareTracker;
	byte meterTracker;
	byte outputTracker;

	boolean cached = false;
	IEnergyHandler[] adjacentHandlers = new IEnergyHandler[6];

	public int energyReceive;
	public int energySend;
	public byte type;

	public TileCell() {

		energyStorage = new EnergyStorage(STORAGE[1], MAX_RECEIVE[1]);
	}

	public TileCell(int metadata) {

		energyStorage = new EnergyStorage(STORAGE[metadata], MAX_RECEIVE[metadata]);
		type = (byte) metadata;
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

	/* GUI METHODS */
	@Override
	public GuiContainer getGuiClient(InventoryPlayer inventory) {

		return new GuiCell(inventory, this);
	}

	@Override
	public Container getGuiServer(InventoryPlayer inventory) {

		return new ContainerTEBase(inventory, this);
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		type = nbt.getByte("Type");
		outputTracker = nbt.getByte("Tracker");
		energySend = MathHelper.clampI(nbt.getInteger("Send"), 0, MAX_SEND[type]);
		energyReceive = MathHelper.clampI(nbt.getInteger("Recv"), 0, MAX_RECEIVE[type]);

		energyStorage = new EnergyStorage(STORAGE[type], MAX_RECEIVE[type]);
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
	public CoFHPacket getPacket() {

		CoFHPacket payload = super.getPacket();

		payload.addInt(energySend);
		payload.addInt(energyReceive);
		payload.addInt(energyStorage.getEnergyStored());

		return payload;
	}

	@Override
	public CoFHPacket getGuiPacket() {

		CoFHPacket payload = super.getGuiPacket();

		payload.addInt(energySend);
		payload.addInt(energyReceive);
		payload.addInt(energyStorage.getEnergyStored());

		return payload;
	}

	@Override
	public CoFHPacket getModePacket() {

		CoFHPacket payload = super.getModePacket();

		payload.addInt(MathHelper.clampI(energySend, 0, MAX_SEND[getType()]));
		payload.addInt(MathHelper.clampI(energyReceive, 0, MAX_RECEIVE[getType()]));

		return payload;
	}

	@Override
	protected void handleGuiPacket(CoFHPacket payload) {

		super.handleGuiPacket(payload);

		energySend = payload.getInt();
		energyReceive = payload.getInt();
		energyStorage.setEnergyStored(payload.getInt());
	}

	@Override
	protected void handleModePacket(CoFHPacket payload) {

		super.handleModePacket(payload);

		energySend = payload.getInt();
		energyReceive = payload.getInt();
	}

	/* ITilePacketHandler */
	@Override
	public void handleTilePacket(CoFHPacket payload, boolean isServer) {

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
