package thermalexpansion.block.machine;

import cofh.api.core.IAugmentable;
import cofh.api.core.IEnergyInfo;
import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyContainerItem;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyStorage;
import cofh.network.CoFHPacket;
import cofh.network.CoFHTileInfoPacket;
import cofh.network.ITileInfoPacketHandler;
import cofh.network.ITilePacketHandler;
import cofh.network.PacketHandler;
import cofh.render.IconRegistry;
import cofh.util.BlockHelper;
import cofh.util.EnergyHelper;
import cofh.util.MathHelper;
import cofh.util.ServerHelper;
import cofh.util.TimeTracker;
import cpw.mods.fml.relauncher.Side;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.block.TileReconfigurableInventory;
import thermalexpansion.core.TEProps;
import thermalexpansion.gui.GuiHandler;
import thermalexpansion.util.Utils;

public abstract class TileMachineBase extends TileReconfigurableInventory implements ISidedInventory, ITilePacketHandler, ITileInfoPacketHandler, IAugmentable,
		IEnergyHandler, IEnergyInfo {

	public static class SideConfig {

		public int numGroup;
		public int[][] slotGroups;
		public boolean[] allowInsertion;
		public boolean[] allowExtraction;
		public int[] sideTex;
	}

	protected static final SideConfig[] defaultSideConfig = new SideConfig[BlockMachine.Types.values().length];
	protected static final EnergyConfig[] defaultEnergyConfig = new EnergyConfig[BlockMachine.Types.values().length];
	protected static final int[] lightValue = { 14, 0, 0, 15, 15, 0, 0, 14, 0, 0, 7 };

	protected static final int RATE = 100;

	SideConfig sideConfig;
	EnergyConfig energyConfig;
	EnergyStorage energyStorage;

	TimeTracker tracker = new TimeTracker();

	boolean wasActive;

	int processMax;
	int processRem;

	/* Augment Variables */
	int level = 0;
	ItemStack[] augments = new ItemStack[3];
	boolean[] augmentStatus = new boolean[3];

	public boolean augmentRSControl = true;
	public boolean augmentReconfigSides = true;
	public boolean augmentAutoTransfer = true;
	int processMod = 1;
	int energyMod = 1;
	int secondaryChance = 100;

	public TileMachineBase() {

		super();

		sideConfig = defaultSideConfig[getType()];
		energyConfig = defaultEnergyConfig[getType()];
		energyStorage = new EnergyStorage(energyConfig.maxEnergy, energyConfig.maxPower * 4);
	}

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

	protected int calcEnergy() {

		if (!isActive) {
			return 0;
		}
		if (energyStorage.getEnergyStored() > energyConfig.maxPowerLevel) {
			return energyConfig.maxPower;
		}
		if (energyStorage.getEnergyStored() < energyConfig.minPowerLevel) {
			return energyConfig.minPower;
		}
		return energyStorage.getEnergyStored() / energyConfig.energyRamp;
	}

	protected boolean canStart() {

		return false;
	}

	public boolean canFinish() {

		return processRem > 0 ? false : hasValidInput();
	}

	protected boolean hasValidInput() {

		return true;
	}

	protected void processStart() {

	}

	protected void processFinish() {

	}

	protected void transferProducts() {

	}

	@Override
	public void updateEntity() {

		if (ServerHelper.isClientWorld(worldObj)) {
			return;
		}
		boolean curActive = isActive;

		if (isActive) {
			if (processRem > 0) {
				int energy = calcEnergy();
				energyStorage.modifyEnergyStored(-energy * energyMod);
				processRem -= energy * processMod;
			}
			if (canFinish()) {
				processFinish();
				transferProducts();
				energyStorage.modifyEnergyStored(-processRem * energyMod / processMod);

				if (!redstoneControlOrDisable() || !canStart()) {
					isActive = false;
					wasActive = true;
					tracker.markTime(worldObj);
				} else {
					processStart();
				}
			}
		} else if (redstoneControlOrDisable()) {
			if (timeCheck()) {
				transferProducts();
			}
			if (timeCheckEighth() && canStart()) {
				processStart();
				int energy = calcEnergy();
				energyStorage.modifyEnergyStored(-energy * energyMod);
				processRem -= energy * processMod;
				isActive = true;
			}
		}
		updateIfChanged(curActive);
		chargeEnergy();
	}

	public void chargeEnergy() {

		int chargeSlot = getChargeSlot();

		if (hasChargeSlot() && EnergyHelper.isEnergyContainerItem(inventory[chargeSlot])) {
			int energyRequest = Math.min(energyStorage.getMaxReceive(), energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored());
			energyStorage.receiveEnergy(((IEnergyContainerItem) inventory[chargeSlot].getItem()).extractEnergy(inventory[chargeSlot], energyRequest, false),
					false);
			if (inventory[chargeSlot].stackSize <= 0) {
				inventory[chargeSlot] = null;
			}
		}
	}

	public boolean hasChargeSlot() {

		return true;
	}

	public int getChargeSlot() {

		return inventory.length - 1;
	}

	public IEnergyStorage getEnergyStorage() {

		return energyStorage;
	}

	@Override
	public int getLightValue() {

		return isActive ? lightValue[getType()] : 0;
	}

	@Override
	public String getName() {

		return "tile.thermalexpansion.machine." + BlockMachine.NAMES[getType()] + ".name";
	}

	/* NETWORK METHODS */
	public CoFHPacket getGuiPacket() {

		CoFHPacket payload = CoFHTileInfoPacket.newPacket(this);
		payload.addByte(TEProps.PacketID.GUI.ordinal());
		payload.addBool(isActive);
		payload.addInt(processMax);
		payload.addInt(processRem);
		payload.addInt(processMod);
		payload.addInt(energyStorage.getEnergyStored());
		payload.addInt(energyMod);

		payload.addBool(augmentRSControl);
		payload.addBool(augmentReconfigSides);
		return payload;
	}

	public CoFHPacket getFluidPacket() {

		CoFHPacket payload = CoFHTileInfoPacket.newPacket(this);
		payload.addByte(TEProps.PacketID.FLUID.ordinal());
		return payload;
	}

	public CoFHPacket getModePacket() {

		CoFHPacket payload = CoFHTileInfoPacket.newPacket(this);
		payload.addByte(TEProps.PacketID.MODE.ordinal());
		return payload;
	}

	protected void handleGuiPacket(CoFHPacket payload) {

		isActive = payload.getBool();
		processMax = payload.getInt();
		processRem = payload.getInt();
		processMod = payload.getInt();
		energyStorage.setEnergyStored(payload.getInt());
		energyMod = payload.getInt();

		augmentRSControl = payload.getBool();
		augmentReconfigSides = payload.getBool();
	}

	protected void handleFluidPacket(CoFHPacket payload) {

	}

	protected void handleModePacket(CoFHPacket payload) {

	}

	protected void handleAugmentPacket(CoFHPacket payload) {

	}

	public void sendFluidPacket() {

		PacketHandler.sendToDimension(getFluidPacket(), worldObj.provider.dimensionId);
	}

	public void sendModePacket() {

		if (ServerHelper.isClientWorld(worldObj)) {
			PacketHandler.sendToServer(getModePacket());
		}
	}

	/* ITileInfoPacketHandler */
	@Override
	public void handleTileInfoPacket(CoFHPacket payload, boolean isServer, EntityPlayer thePlayer) {

		switch (TEProps.PacketID.values()[payload.getByte()]) {
		case GUI:
			handleGuiPacket(payload);
			return;
		case FLUID:
			handleFluidPacket(payload);
			return;
		case MODE:
			handleModePacket(payload);
			return;
		case AUGMENT:
			handleAugmentPacket(payload);
			return;
		default:
		}
	}

	/* GUI METHODS */
	@Override
	public boolean openGui(EntityPlayer player) {

		player.openGui(ThermalExpansion.instance, GuiHandler.TILE_ID, worldObj, xCoord, yCoord, zCoord);
		return true;
	}

	public int getScaledProgress(int scale) {

		if (!isActive || processMax <= 0 || processRem <= 0) {
			return 0;
		}
		return scale * (processMax - processRem) / processMax;
	}

	public int getScaledSpeed(int scale) {

		if (!isActive) {
			return 0;
		}
		double power = energyStorage.getEnergyStored() / energyConfig.energyRamp;
		power = MathHelper.clip(power, energyConfig.minPower, energyConfig.maxPower);

		return MathHelper.round(scale * power / energyConfig.maxPower);
	}

	@Override
	public void sendGuiNetworkData(Container container, ICrafting iCrafting) {

		if (iCrafting instanceof EntityPlayer) {
			if (ServerHelper.isServerWorld(worldObj)) {
				PacketHandler.sendTo(getGuiPacket(), (EntityPlayer) iCrafting);
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

		readAugmentsFromNBT(nbt);
		installAugments();

		energyStorage.readFromNBT(nbt);

		processMax = nbt.getInteger("ProcMax");
		processRem = nbt.getInteger("ProcRem");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		writeAugmentsToNBT(nbt);

		energyStorage.writeToNBT(nbt);

		nbt.setInteger("ProcMax", processMax);
		nbt.setInteger("ProcRem", processRem);
	}

	public void readAugmentsFromNBT(NBTTagCompound nbt) {

		level = nbt.getInteger("Level");

		NBTTagList list = nbt.getTagList("Augments", 10);
		augments = new ItemStack[augments.length + level];
		augmentStatus = new boolean[augments.length];
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound tag = list.getCompoundTagAt(i);
			int slot = tag.getInteger("Slot");

			if (slot >= 0 && slot < augments.length) {
				augments[slot] = ItemStack.loadItemStackFromNBT(tag);
			}
		}
	}

	public void writeAugmentsToNBT(NBTTagCompound nbt) {

		nbt.setInteger("Level", level);

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

	/* IInventory */
	@Override
	public ItemStack decrStackSize(int slot, int amount) {

		ItemStack stack = super.decrStackSize(slot, amount);

		if (ServerHelper.isServerWorld(worldObj) && slot <= getMaxInputSlot()) {
			if (isActive && (inventory[slot] == null || !hasValidInput())) {
				isActive = false;
				wasActive = true;
				tracker.markTime(worldObj);
				processRem = 0;
			}
		}
		return stack;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {

		if (ServerHelper.isServerWorld(worldObj) && slot <= getMaxInputSlot()) {
			if (isActive && inventory[slot] != null) {
				if (stack == null || !stack.isItemEqual(inventory[slot]) || !hasValidInput()) {
					isActive = false;
					wasActive = true;
					tracker.markTime(worldObj);
					processRem = 0;
				}
			}
		}
		super.setInventorySlotContents(slot, stack);
	}

	@Override
	public void markDirty() {

		if (isActive && !hasValidInput()) {
			isActive = false;
			wasActive = true;
			tracker.markTime(worldObj);
			processRem = 0;
		}
		super.markDirty();
	}

	/* IEnergyHandler */
	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {

		return energyStorage.receiveEnergy(maxReceive, simulate);
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {

		return 0;
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {

		return energyStorage.getMaxEnergyStored() > 0;
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

		return energyConfig.maxPower * energyMod;
	}

	@Override
	public int getInfoEnergyStored() {

		return energyStorage.getEnergyStored();
	}

	@Override
	public int getInfoMaxEnergyStored() {

		return energyStorage.getMaxEnergyStored();
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

		return sideConfig.numGroup;
	}

	/* ISidedTexture */
	@Override
	public IIcon getTexture(int side, int pass) {

		if (pass == 0) {
			if (side == 0) {
				return IconRegistry.getIcon("MachineBottom");
			} else if (side == 1) {
				return IconRegistry.getIcon("MachineTop");
			}
			return side != facing ? IconRegistry.getIcon("MachineSide") : isActive ? IconRegistry.getIcon("MachineActive", getType()) : IconRegistry.getIcon(
					"MachineFace", getType());
		} else if (side < 6) {
			return IconRegistry.getIcon(TEProps.textureSelection, sideConfig.sideTex[sideCache[side]]);
		}
		return IconRegistry.getIcon("MachineSide");
	}

	/* ISidedInventory */
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {

		return sideConfig.slotGroups[sideCache[side]];
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int side) {

		return sideConfig.allowInsertion[sideCache[side]] ? canAcceptItem(stack, slot, side) : false;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int side) {

		return sideConfig.allowExtraction[sideCache[side]];
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
	public boolean installAugments() {

		for (int i = 0; i < augments.length; i++) {
			if (Utils.isAugmentItem(augments[i])) {

			}
		}
		return false;
	}

}
