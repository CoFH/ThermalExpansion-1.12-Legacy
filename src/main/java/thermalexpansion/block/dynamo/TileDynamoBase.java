package thermalexpansion.block.dynamo;

import cofh.api.core.IAugmentable;
import cofh.api.core.IEnergyInfo;
import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyStorage;
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
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.block.TileRSInventory;
import thermalexpansion.core.TEProps;
import thermalexpansion.gui.GuiHandler;

public abstract class TileDynamoBase extends TileRSInventory implements ITileInfoPacketHandler, IReconfigurableFacing, ISidedInventory, IEnergyHandler,
		IEnergyInfo, IAugmentable {

	protected static final EnergyConfig defaultConfig = new EnergyConfig();

	protected static final int MAX_FLUID = FluidContainerRegistry.BUCKET_VOLUME * 4;
	protected static final int[] SLOTS = { 0 };

	EnergyConfig config;
	EnergyStorage energyStorage;

	boolean cached = false;
	IEnergyHandler adjacentHandler = null;

	byte facing = 1;
	boolean isActive;
	int fuelRF;
	int compareTracker;

	/* Augment Variables */
	ItemStack[] augments = new ItemStack[3];
	boolean[] augmentStatus = new boolean[3];

	int energyMod = 1;
	int fuelMod = 100;

	public TileDynamoBase() {

		config = defaultConfig;
		energyStorage = new EnergyStorage(config.maxEnergy, config.maxPower * 2);
	}

	protected abstract boolean canGenerate();

	protected abstract void generate();

	protected boolean hasStoredEnergy() {

		return energyStorage.getEnergyStored() > 0;
	}

	protected void transferEnergy(int bSide) {

		if (adjacentHandler == null) {
			return;
		}
		energyStorage.modifyEnergyStored(-adjacentHandler.receiveEnergy(ForgeDirection.VALID_DIRECTIONS[bSide ^ 1],
				Math.min(config.maxPower * 2, energyStorage.getEnergyStored()), false));
	}

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

		player.openGui(ThermalExpansion.instance, GuiHandler.TILE_ID, worldObj, xCoord, yCoord, zCoord);
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

	public CoFHPacket getGuiPacket() {

		CoFHPacket payload = CoFHTileInfoPacket.newPacket(this);
		payload.addByte(TEProps.PacketID.GUI.ordinal());
		payload.addInt(energyStorage.getEnergyStored());
		payload.addInt(fuelRF);
		return payload;
	}

	protected void handleGuiPacket(CoFHPacket payload) {

		energyStorage.setEnergyStored(payload.getInt());
		fuelRF = payload.getInt();
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
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
	}

	/* ITileInfoPacketHandler */
	@Override
	public void handleTileInfoPacket(CoFHPacket payload, boolean isServer, EntityPlayer thePlayer) {

		switch (TEProps.PacketID.values()[payload.getByte()]) {
		case GUI:
			handleGuiPacket(payload);
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
				PacketHandler.sendTo(getGuiPacket(), (EntityPlayer) iCrafting);
			}
		}
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		readAugmentsFromNBT(nbt);
		installAugments();

		facing = nbt.getByte("Facing");
		isActive = nbt.getBoolean("Active");
		fuelRF = nbt.getInteger("Fuel");
		energyStorage.readFromNBT(nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		writeAugmentsToNBT(nbt);

		nbt.setByte("Facing", facing);
		nbt.setBoolean("Active", isActive);
		nbt.setInteger("Fuel", fuelRF);

		energyStorage.writeToNBT(nbt);
	}

	public void readAugmentsFromNBT(NBTTagCompound nbt) {

		NBTTagList list = nbt.getTagList("Augments", 10);
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound tag = list.getCompoundTagAt(i);
			int slot = tag.getInteger("Slot");
			if (slot >= 0 && slot < augments.length) {
				augments[slot] = ItemStack.loadItemStackFromNBT(tag);
			}
		}
	}

	public void writeAugmentsToNBT(NBTTagCompound nbt) {

		if (augments.length <= 0) {
			return;
		}
		NBTTagList list = new NBTTagList();
		for (int i = 0; i < augments.length; i++) {
			if (augments[i] != null) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setInteger("Slot", i);
				augments[i].writeToNBT(tag);
				list.appendTag(tag);
			}
		}
		nbt.setTag("Augments", list);
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
		for (int i = facing + 1; i < facing + 6; i++) {
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

		return calcEnergy() * energyMod;
	}

	@Override
	public int getInfoMaxEnergyPerTick() {

		return config.maxPower * energyMod;
	}

	@Override
	public int getInfoEnergyStored() {

		return energyStorage.getEnergyStored();
	}

	@Override
	public int getInfoMaxEnergyStored() {

		return config.maxEnergy;
	}

	/* IAugmentableTile */
	@Override
	public ItemStack[] getAugmentSlots() {

		return augments;
	}

	@Override
	public boolean[] getAugmentStatus() {

		return augmentStatus;
	}

	@Override
	public boolean installAugments() {

		return false;
	}

}
