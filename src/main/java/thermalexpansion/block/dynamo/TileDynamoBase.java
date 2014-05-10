package thermalexpansion.block.dynamo;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyStorage;
import cofh.api.tileentity.IEnergyInfo;
import cofh.api.tileentity.IReconfigurableFacing;
import cofh.network.CoFHPacket;
import cofh.network.CoFHTileInfoPacket;
import cofh.network.ITileInfoPacketHandler;
import cofh.network.PacketHandler;
import cofh.util.BlockHelper;
import cofh.util.EnergyHelper;
import cofh.util.ServerHelper;
import cpw.mods.fml.relauncher.Side;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.block.TileRSInventory;
import thermalexpansion.core.TEProps;

public abstract class TileDynamoBase extends TileRSInventory implements ITileInfoPacketHandler, IReconfigurableFacing, ISidedInventory, IEnergyHandler,
		IEnergyInfo {

	protected static final int[] guiIds = new int[BlockDynamo.Types.values().length];

	protected static final DynamoConfig defaultConfig = new DynamoConfig();
	protected static final int MAX_FLUID = FluidContainerRegistry.BUCKET_VOLUME * 4;
	protected static final int[] SLOTS = { 0 };

	public static class DynamoConfig {

		public int minPower;
		public int maxPower;
		public int maxEnergy;
		public int maxTransfer;
		public int minPowerLevel;
		public int maxPowerLevel;
		public int energyRamp;

		public DynamoConfig() {

		}

		public DynamoConfig(DynamoConfig config) {

			this.minPower = config.minPower;
			this.maxPower = config.maxPower;
			this.maxEnergy = config.maxEnergy;
			this.maxTransfer = config.maxTransfer;
			this.minPowerLevel = config.minPowerLevel;
			this.maxPowerLevel = config.maxPowerLevel;
			this.energyRamp = config.energyRamp;
		}

		public DynamoConfig copy() {

			return new DynamoConfig(this);
		}
	}

	static {
		defaultConfig.minPower = 4;
		defaultConfig.maxPower = 80;
		defaultConfig.maxEnergy = 40000;
		defaultConfig.maxTransfer = 160;
		defaultConfig.minPowerLevel = 9 * defaultConfig.maxEnergy / 10;
		defaultConfig.maxPowerLevel = 1 * defaultConfig.maxEnergy / 10;
		defaultConfig.energyRamp = defaultConfig.minPowerLevel / defaultConfig.maxPower;
	}

	DynamoConfig config = new DynamoConfig(defaultConfig);
	EnergyStorage energyStorage;

	boolean cached = false;
	IEnergyHandler adjacentHandler = null;

	byte facing = 1;
	boolean isActive;
	int fuelRF;
	int compareTracker;

	public TileDynamoBase() {

		energyStorage = new EnergyStorage(config.maxEnergy, config.maxTransfer);
	}

	protected abstract boolean canGenerate();

	protected abstract void generate();

	public IIcon getActiveIcon() {

		return FluidRegistry.WATER.getIcon();
	}

	protected void attenuate() {

		if (timeCheck() && fuelRF > 0) {
			fuelRF -= 10;

			if (fuelRF < 0) {
				fuelRF = 0;
			}
		}
	}

	public int calcEnergy() {

		if (!isActive) {
			return 0;
		}
		if (energyStorage.getEnergyStored() < config.maxPowerLevel) {
			return config.maxPower;
		}
		if (energyStorage.getEnergyStored() > config.minPowerLevel) {
			return config.minPower;
		}
		return (energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored()) / config.energyRamp;
	}

	protected void transferEnergy(int bSide) {

		if (adjacentHandler == null) {
			return;
		}
		energyStorage.modifyEnergyStored(-adjacentHandler.receiveEnergy(ForgeDirection.VALID_DIRECTIONS[bSide ^ 1],
				Math.min(config.maxTransfer, energyStorage.getEnergyStored()), false));
	}

	@Override
	public void updateEntity() {

		if (ServerHelper.isClientWorld(worldObj)) {
			return;
		}
		if (!cached) {
			onNeighborBlockChange();
		}
		boolean curActive = isActive;

		if (redstoneControlOrDisable()) {
			if (isActive) {
				if (canGenerate()) {
					generate();
					transferEnergy(facing);
				} else {
					isActive = false;
				}
			} else if (canGenerate()) {
				isActive = true;
				generate();
				transferEnergy(facing);
			} else {
				attenuate();
			}
			if (timeCheck()) {
				int curScale = getScaledEnergyStored(15);
				if (curScale != compareTracker) {
					compareTracker = curScale;
					callNeighborTileChange();
				}
			}
		} else {
			isActive = false;
			attenuate();
		}
		if (curActive != isActive) {
			sendUpdatePacket(Side.CLIENT);
		}
	}

	@Override
	public int getComparatorInput(int side) {

		return compareTracker;
	}

	public int getScaledEnergyStored(int scale) {

		return energyStorage.getEnergyStored() * scale / energyStorage.getMaxEnergyStored();
	}

	@Override
	public int getLightValue() {

		return isActive ? 7 : 0;
	}

	@Override
	public String getName() {

		return "tile.thermalexpansion.dynamo." + BlockDynamo.NAMES[getType()] + ".name";
	}

	@Override
	public boolean openGui(EntityPlayer player) {

		player.openGui(ThermalExpansion.instance, guiIds[getType()], worldObj, xCoord, yCoord, zCoord);
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
		updateAdjacentHandlers();
	}

	protected void updateAdjacentHandlers() {

		if (ServerHelper.isClientWorld(worldObj)) {
			return;
		}
		TileEntity tile = BlockHelper.getAdjacentTileEntity(this, facing);

		if (EnergyHelper.isEnergyHandlerFromSide(tile, ForgeDirection.VALID_DIRECTIONS[facing ^ 1])) {
			adjacentHandler = (IEnergyHandler) tile;
		} else {
			adjacentHandler = null;
		}
		cached = true;
	}

	public IEnergyStorage getEnergyStorage() {

		return energyStorage;
	}

	/* NETWORK METHODS */
	@Override
	public CoFHPacket getPacket() {

		CoFHPacket payload = super.getPacket();

		payload.addByte(facing);
		payload.addBool(isActive);
		return payload;
	}

	public CoFHPacket getGuiCoFHPacket() {

		CoFHPacket payload = CoFHTileInfoPacket.getTileInfoPacket(this);

		payload.addByte(TEProps.PacketID.GUI.ordinal());
		payload.addInt(energyStorage.getEnergyStored());

		return payload;
	}

	/* ITilePacketHandler */
	@Override
	public void handleTilePacket(CoFHPacket payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		if (ServerHelper.isClientWorld(worldObj)) {
			facing = payload.getByte();
			isActive = payload.getBool();
		} else {
			payload.getByte();
			payload.getBool();
		}
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		worldObj.updateAllLightTypes(xCoord, yCoord, zCoord);
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
	}

	/* ITileInfoPacketHandler */
	@Override
	public void handleTileInfoPacket(CoFHPacket payload, boolean isServer, EntityPlayer thePlayer) {

		switch (TEProps.PacketID.values()[payload.getByte()]) {
		case GUI:
			energyStorage.setEnergyStored(payload.getInt());
			return;
		default:
		}
	}

	/* GUI METHODS */
	public boolean isActive() {

		return isActive;
	}

	@Override
	public void sendGuiNetworkData(Container container, ICrafting iCrafting) {

		if (iCrafting instanceof EntityPlayer) {
			if (ServerHelper.isServerWorld(worldObj)) {
				PacketHandler.sendToPlayer(getGuiCoFHPacket(), (EntityPlayer) iCrafting);
			}
		}
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		facing = nbt.getByte("Facing");
		isActive = nbt.getBoolean("Active");
		fuelRF = nbt.getInteger("Fuel");

		energyStorage.readFromNBT(nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setByte("Facing", facing);
		nbt.setBoolean("Active", isActive);
		nbt.setInteger("Fuel", fuelRF);

		energyStorage.writeToNBT(nbt);
	}

	/* ISidedInventory */
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {

		return TEProps.EMPTY_INVENTORY;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int side) {

		return side != facing;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int side) {

		return side != facing;
	}

	/* IReconfigurableFacing */
	@Override
	public int getFacing() {

		return facing;
	}

	@Override
	public boolean rotateBlock() {

		int[] coords;
		for (int i = facing + 1; i < facing + 6; ++i) {
			if (EnergyHelper.isAdjacentEnergyHandlerFromSide(this, i % 6)) {
				facing = (byte) (i % 6);
				worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
				updateAdjacentHandlers();
				sendUpdatePacket(Side.CLIENT);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onWrench(EntityPlayer player, int hitSide) {

		rotateBlock();
		return true;
	}

	@Override
	public boolean setFacing(int side) {

		return false;
	}

	@Override
	public boolean allowYAxisFacing() {

		return true;
	}

	/* IEnergyHandler */
	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {

		return 0;
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {

		return 0;
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {

		return from.ordinal() == facing;
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {

		return energyStorage.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {

		return energyStorage.getMaxEnergyStored();
	}

	/* IEnergyInfo */
	@Override
	public int getInfoEnergyPerTick() {

		return calcEnergy();
	}

	@Override
	public int getInfoMaxEnergyPerTick() {

		return config.maxPower;
	}

	@Override
	public int getInfoEnergy() {

		return energyStorage.getEnergyStored();
	}

	@Override
	public int getInfoMaxEnergy() {

		return config.maxEnergy;
	}

}
