package thermalexpansion.block.dynamo;

import cofh.CoFHCore;
import cofh.api.core.IAugmentable;
import cofh.api.core.IEnergyInfo;
import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import cofh.api.item.IAugmentItem;
import cofh.api.tileentity.IReconfigurableFacing;
import cofh.network.CoFHPacket;
import cofh.network.ITileInfoPacketHandler;
import cofh.util.BlockHelper;
import cofh.util.EnergyHelper;
import cofh.util.ServerHelper;
import cofh.util.fluid.FluidTankAdv;
import cpw.mods.fml.relauncher.Side;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;

import thermalexpansion.block.TileRSControl;
import thermalexpansion.core.TEProps;
import thermalexpansion.item.TEAugments;
import thermalexpansion.util.Utils;

public abstract class TileDynamoBase extends TileRSControl implements ITileInfoPacketHandler, IAugmentable, IEnergyInfo, IReconfigurableFacing, ISidedInventory {

	protected static final EnergyConfig defaultConfig = new EnergyConfig();

	protected static final int MAX_FLUID = FluidContainerRegistry.BUCKET_VOLUME * 4;
	protected static final int[] SLOTS = { 0 };

	int compareTracker;
	int fuelRF;
	byte facing = 1;

	boolean cached = false;
	IEnergyHandler adjacentHandler = null;

	EnergyConfig config;

	/* Augment Variables */
	ItemStack[] augments = new ItemStack[3];
	boolean[] augmentStatus = new boolean[3];

	int energyMod = 1;
	int fuelMod = 100;

	public boolean augmentRedstoneControl;
	public boolean augmentThrottle;
	public boolean augmentCoilDuct;

	public TileDynamoBase() {

		config = defaultConfig;
		energyStorage = new EnergyStorage(config.maxEnergy, config.maxPower * 5);
	}

	@Override
	public String getName() {

		return "tile.thermalexpansion.dynamo." + BlockDynamo.NAMES[getType()] + ".name";
	}

	@Override
	public int getComparatorInput(int side) {

		return compareTracker;
	}

	@Override
	public int getLightValue() {

		return isActive ? 7 : 0;
	}

	@Override
	public boolean onWrench(EntityPlayer player, int hitSide) {

		rotateBlock();
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
	public void invalidate() {

		cached = false;
		super.invalidate();
	}

	protected abstract boolean canGenerate();

	protected abstract void generate();

	protected int calcEnergy() {

		if (!isActive) {
			return 0;
		}
		if (energyStorage.getEnergyStored() < config.minPowerLevel) {
			return config.maxPower;
		}
		if (energyStorage.getEnergyStored() > config.maxPowerLevel) {
			return config.minPower;
		}
		return (energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored()) / config.energyRamp;
	}

	protected int calcEnergy2() {

		if (!isActive || energyStorage.getEnergyStored() == energyStorage.getMaxEnergyStored()) {
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

	protected boolean hasStoredEnergy() {

		return energyStorage.getEnergyStored() > 0;
	}

	protected void attenuate() {

		if (timeCheck() && fuelRF > 0) {
			fuelRF -= 10;

			if (fuelRF < 0) {
				fuelRF = 0;
			}
		}
	}

	protected void transferEnergy(int bSide) {

		if (adjacentHandler == null) {
			return;
		}
		energyStorage.modifyEnergyStored(-adjacentHandler.receiveEnergy(ForgeDirection.VALID_DIRECTIONS[bSide ^ 1],
				Math.min(config.maxPower * 2, energyStorage.getEnergyStored()), false));
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

	public IIcon getActiveIcon() {

		return FluidRegistry.WATER.getIcon();
	}

	/* GUI METHODS */
	public FluidTankAdv getTank(int tankIndex) {

		return null;
	}

	public int getScaledDuration(int scale) {

		return 0;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		readAugmentsFromNBT(nbt);
		installAugments();
		energyStorage.readFromNBT(nbt);

		facing = nbt.getByte("Facing");
		isActive = nbt.getBoolean("Active");
		fuelRF = nbt.getInteger("Fuel");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		writeAugmentsToNBT(nbt);

		nbt.setByte("Facing", facing);
		nbt.setBoolean("Active", isActive);
		nbt.setInteger("Fuel", fuelRF);
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

	/* NETWORK METHODS */
	@Override
	public CoFHPacket getPacket() {

		CoFHPacket payload = super.getPacket();

		payload.addByte(facing);

		return payload;
	}

	@Override
	public CoFHPacket getGuiPacket() {

		CoFHPacket payload = super.getGuiPacket();

		payload.addInt(energyStorage.getEnergyStored());
		payload.addInt(fuelRF);

		payload.addBool(augmentRedstoneControl);

		return payload;
	}

	@Override
	protected void handleGuiPacket(CoFHPacket payload) {

		super.handleGuiPacket(payload);

		energyStorage.setEnergyStored(payload.getInt());
		fuelRF = payload.getInt();

		boolean prevControl = augmentRedstoneControl;
		augmentRedstoneControl = payload.getBool();

		if (augmentRedstoneControl != prevControl) {
			onInstalled();
			sendUpdatePacket(Side.SERVER);
		}
	}

	/* ITilePacketHandler */
	@Override
	public void handleTilePacket(CoFHPacket payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		if (!isServer) {
			facing = payload.getByte();
		} else {
			payload.getByte();
		}
	}

	/* IAugmentable */
	@Override
	public ItemStack[] getAugmentSlots() {

		return augments;
	}

	@Override
	public boolean[] getAugmentStatus() {

		return augmentStatus;
	}

	@Override
	public void installAugments() {

		resetAugments();
		for (int i = 0; i < augments.length; i++) {
			augmentStatus[i] = false;
			if (Utils.isAugmentItem(augments[i])) {
				augmentStatus[i] = installAugment(i);
			}
		}
		if (CoFHCore.proxy.isServer()) {
			onInstalled();
			sendUpdatePacket(Side.CLIENT);
		}
	}

	/* AUGMENT HELPERS */
	private boolean hasAugment(String type, int augLevel) {

		for (int i = 0; i < augments.length; i++) {
			if (Utils.isAugmentItem(augments[i]) && ((IAugmentItem) augments[i].getItem()).getAugmentLevel(augments[i], type) == augLevel) {
				return true;
			}
		}
		return false;
	}

	private boolean hasDuplicateAugment(String type, int augLevel, int slot) {

		for (int i = 0; i < augments.length; i++) {
			if (i != slot && Utils.isAugmentItem(augments[i]) && ((IAugmentItem) augments[i].getItem()).getAugmentLevel(augments[i], type) == augLevel) {
				return true;
			}
		}
		return false;
	}

	private boolean hasAugmentChain(String type, int augLevel) {

		boolean preReq = true;
		for (int i = 1; i < augLevel; i++) {
			preReq = preReq && hasAugment(type, i);
		}
		return preReq;
	}

	private boolean installAugment(int slot) {

		IAugmentItem augmentItem = (IAugmentItem) augments[slot].getItem();
		boolean installed = false;

		if (augmentItem.getAugmentLevel(augments[slot], TEAugments.DYNAMO_EFFICIENCY) > 0) {
			if (augmentItem.getAugmentLevel(augments[slot], TEAugments.DYNAMO_OUTPUT) > 0) {
				return false;
			}
			int level = Math.min(TEAugments.NUM_DYNAMO_EFFICIENCY, augmentItem.getAugmentLevel(augments[slot], TEAugments.DYNAMO_EFFICIENCY));
			if (hasDuplicateAugment(TEAugments.DYNAMO_EFFICIENCY, level, slot)) {
				return false;
			}
			if (hasAugmentChain(TEAugments.DYNAMO_EFFICIENCY, level)) {
				fuelMod += 20;
				installed = true;
			} else {
				return false;
			}
		}
		if (augmentItem.getAugmentLevel(augments[slot], TEAugments.DYNAMO_OUTPUT) > 0) {
			int level = augmentItem.getAugmentLevel(augments[slot], TEAugments.DYNAMO_OUTPUT);
			if (hasDuplicateAugment(TEAugments.DYNAMO_OUTPUT, level, slot)) {
				return false;
			}
			if (hasAugmentChain(TEAugments.DYNAMO_OUTPUT, level)) {
				energyMod += 1;
				fuelMod -= 15;
				installed = true;
			} else {
				return false;
			}
		}
		if (augmentItem.getAugmentLevel(augments[slot], TEAugments.DYNAMO_COIL_DUCT) > 0) {
			augmentCoilDuct = true;
			installed = true;
		}
		if (augmentItem.getAugmentLevel(augments[slot], TEAugments.DYNAMO_THROTTLE) > 0) {
			augmentThrottle = true;
			installed = true;
		}
		if (augmentItem.getAugmentLevel(augments[slot], TEAugments.ENDER_ENERGY) > 0) {

		}
		if (augmentItem.getAugmentLevel(augments[slot], TEAugments.GENERAL_REDSTONE_CONTROL) > 0) {
			augmentRedstoneControl = true;
			installed = true;
		}
		return installed;
	}

	private void onInstalled() {

		if (!augmentRedstoneControl) {
			this.rsMode = ControlMode.DISABLED;
		}
	}

	private void resetAugments() {

		energyMod = 1;
		fuelMod = 100;

		augmentRedstoneControl = false;
		augmentThrottle = false;
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
	public int getEnergyStored(ForgeDirection from) {

		return energyStorage.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {

		return energyStorage.getMaxEnergyStored();
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {

		return from.ordinal() == facing;
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

	/* IFluidHandler */
	public boolean canFill(ForgeDirection from, Fluid fluid) {

		return augmentCoilDuct || from.ordinal() != facing;
	}

	public boolean canDrain(ForgeDirection from, Fluid fluid) {

		return augmentCoilDuct || from.ordinal() != facing;
	}

	/* IReconfigurableFacing */
	@Override
	public int getFacing() {

		return facing;
	}

	@Override
	public boolean allowYAxisFacing() {

		return true;
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
	public boolean setFacing(int side) {

		return false;
	}

	/* ISidedInventory */
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {

		return TEProps.EMPTY_INVENTORY;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int side) {

		return augmentCoilDuct || side != facing;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int side) {

		return augmentCoilDuct || side != facing;
	}

}
