package thermalexpansion.block.machine;

import cofh.CoFHCore;
import cofh.api.core.IAugmentable;
import cofh.api.core.IEnergyInfo;
import cofh.api.energy.EnergyStorage;
import cofh.api.item.IAugmentItem;
import cofh.network.CoFHPacket;
import cofh.render.IconRegistry;
import cofh.util.BlockHelper;
import cofh.util.MathHelper;
import cofh.util.RedstoneControlHelper;
import cofh.util.ServerHelper;
import cofh.util.TimeTracker;
import cofh.util.fluid.FluidTankAdv;
import cpw.mods.fml.relauncher.Side;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.FluidStack;

import thermalexpansion.block.TileReconfigurable;
import thermalexpansion.core.TEProps;
import thermalexpansion.item.TEAugments;
import thermalexpansion.util.ReconfigurableHelper;
import thermalexpansion.util.Utils;

public abstract class TileMachineBase extends TileReconfigurable implements IAugmentable, IEnergyInfo, ISidedInventory {

	protected static final SideConfig[] defaultSideConfig = new SideConfig[BlockMachine.Types.values().length];
	protected static final EnergyConfig[] defaultEnergyConfig = new EnergyConfig[BlockMachine.Types.values().length];
	protected static final int[] lightValue = { 14, 0, 0, 15, 15, 0, 0, 14, 0, 0, 7 };

	protected static final int RATE = 500;
	protected static final int AUGMENT_COUNT[] = new int[] { 3, 4, 5, 6 };

	int processMax;
	int processRem;
	boolean wasActive;

	SideConfig sideConfig;
	EnergyConfig energyConfig;
	TimeTracker tracker = new TimeTracker();

	/* Augment Variables */
	boolean[] augmentStatus = new boolean[3];
	ItemStack[] augments = new ItemStack[3];

	byte level = 0;
	int processMod = 1;
	int energyMod = 1;
	int secondaryChance = 100;

	public boolean augmentAutoTransfer;
	public boolean augmentReconfigSides;
	public boolean augmentRedstoneControl;

	public TileMachineBase() {

		super();

		sideConfig = defaultSideConfig[getType()];
		energyConfig = defaultEnergyConfig[getType()];
		energyStorage = new EnergyStorage(energyConfig.maxEnergy, energyConfig.maxPower * 10);
		setDefaultSides();
	}

	@Override
	public String getName() {

		return "tile.thermalexpansion.machine." + BlockMachine.NAMES[getType()] + ".name";
	}

	@Override
	public int getLightValue() {

		return isActive ? lightValue[getType()] : 0;
	}

	@Override
	public byte[] getDefaultSides() {

		return sideConfig.defaultSides.clone();
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

	protected int getMaxInputSlot() {

		return 0;
	}

	protected boolean canStart() {

		return false;
	}

	protected boolean canFinish() {

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

	protected void updateIfChanged(boolean curActive) {

		if (curActive != isActive && isActive == true) {
			sendUpdatePacket(Side.CLIENT);
		} else if (tracker.hasDelayPassed(worldObj, 200) && wasActive) {
			wasActive = false;
			sendUpdatePacket(Side.CLIENT);
		}
	}

	public boolean isItemValid(ItemStack stack, int slot, int side) {

		return true;
	}

	/* GUI METHODS */
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

	public FluidTankAdv getTank() {

		return null;
	}

	public FluidStack getTankFluid() {

		return null;
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

		nbt.setInteger("ProcMax", processMax);
		nbt.setInteger("ProcRem", processRem);
	}

	public void readAugmentsFromNBT(NBTTagCompound nbt) {

		level = nbt.getByte("Level");

		NBTTagList list = nbt.getTagList("Augments", 10);
		augments = new ItemStack[AUGMENT_COUNT[level]];
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

		nbt.setByte("Level", level);

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

		payload.addByte(level);
		payload.addBool(augmentReconfigSides);
		payload.addBool(augmentRedstoneControl);

		return payload;
	}

	@Override
	public CoFHPacket getGuiPacket() {

		CoFHPacket payload = super.getGuiPacket();
		payload.addBool(isActive);
		payload.addInt(processMax);
		payload.addInt(processRem);
		payload.addInt(processMod);
		payload.addInt(energyStorage.getEnergyStored());
		payload.addInt(energyMod);

		payload.addBool(augmentReconfigSides);
		payload.addBool(augmentRedstoneControl);

		return payload;
	}

	@Override
	protected void handleGuiPacket(CoFHPacket payload) {

		isActive = payload.getBool();
		processMax = payload.getInt();
		processRem = payload.getInt();
		processMod = payload.getInt();
		energyStorage.setEnergyStored(payload.getInt());
		energyMod = payload.getInt();

		boolean prevReconfig = augmentReconfigSides;
		boolean prevControl = augmentRedstoneControl;
		augmentReconfigSides = payload.getBool();
		augmentRedstoneControl = payload.getBool();

		if (augmentReconfigSides != prevReconfig || augmentRedstoneControl != prevControl) {
			onInstalled();
			sendUpdatePacket(Side.SERVER);
		}
	}

	/* ITilePacketHandler */
	@Override
	public void handleTilePacket(CoFHPacket payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		if (!isServer) {
			byte curLevel = level;
			level = payload.getByte();

			if (curLevel != level) {
				augments = new ItemStack[AUGMENT_COUNT[level]];
				augmentStatus = new boolean[augments.length];
			}
			augmentReconfigSides = payload.getBool();
			augmentRedstoneControl = payload.getBool();
		} else {
			payload.getByte();
			payload.getBool();
			payload.getBool();
		}
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

		if (augmentItem.getAugmentLevel(augments[slot], TEAugments.MACHINE_SECONDARY) > 0) {
			int augLevel = Math.min(TEAugments.NUM_MACHINE_SECONDARY, augmentItem.getAugmentLevel(augments[slot], TEAugments.MACHINE_SECONDARY));

			if (augLevel > level) {
				return false;
			}
			if (hasDuplicateAugment(TEAugments.MACHINE_SECONDARY, augLevel, slot)) {
				return false;
			}
			if (hasAugmentChain(TEAugments.MACHINE_SECONDARY, augLevel)) {
				secondaryChance -= 15;
				installed = true;
			} else {
				return false;
			}
		}
		if (augmentItem.getAugmentLevel(augments[slot], TEAugments.MACHINE_SPEED) > 0) {
			int augLevel = Math.min(TEAugments.NUM_MACHINE_SPEED, augmentItem.getAugmentLevel(augments[slot], TEAugments.MACHINE_SPEED));

			if (augLevel > level) {
				return false;
			}
			if (hasDuplicateAugment(TEAugments.MACHINE_SPEED, augLevel, slot)) {
				return false;
			}
			if (hasAugmentChain(TEAugments.MACHINE_SPEED, augLevel)) {
				secondaryChance += 5;
				processMod = Math.max(processMod, TEAugments.MACHINE_SPEED_PROCESS_MOD[augLevel]);
				energyMod = Math.max(energyMod, TEAugments.MACHINE_SPEED_ENERGY_MOD[augLevel]);
				installed = true;
			} else {
				return false;
			}
		}
		if (augmentItem.getAugmentLevel(augments[slot], TEAugments.GENERAL_AUTO_TRANSFER) > 0) {
			augmentAutoTransfer = true;
			installed = true;
		}
		if (augmentItem.getAugmentLevel(augments[slot], TEAugments.GENERAL_RECONFIG_SIDES) > 0) {
			augmentReconfigSides = true;
			installed = true;
		}
		if (augmentItem.getAugmentLevel(augments[slot], TEAugments.GENERAL_REDSTONE_CONTROL) > 0) {
			augmentRedstoneControl = true;
			installed = true;
		}
		return installed;
	}

	private void onInstalled() {

		if (!augmentReconfigSides) {
			setDefaultSides();
			sideCache[facing] = 0;
		}
		if (!augmentRedstoneControl) {
			this.rsMode = ControlMode.DISABLED;
		}
		if (isActive && energyStorage.getMaxEnergyStored() > 0 && processRem * energyMod / processMod > energyStorage.getEnergyStored()) {
			processRem = 0;
			isActive = false;
			wasActive = true;
			tracker.markTime(worldObj);
		}
	}

	private void resetAugments() {

		processMod = 1;
		energyMod = 1;
		secondaryChance = 100;

		augmentAutoTransfer = false;
		augmentReconfigSides = false;
		augmentRedstoneControl = false;
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

	/* IPortableData */
	@Override
	public void readPortableData(EntityPlayer player, NBTTagCompound tag) {

		if (!canPlayerAccess(player.getCommandSenderName())) {
			return;
		}
		if (augmentRedstoneControl) {
			RedstoneControlHelper.getControlFromNBT(tag);
		}
		if (augmentReconfigSides) {
			int storedFacing = ReconfigurableHelper.getFacingFromNBT(tag);
			byte[] storedSideCache = ReconfigurableHelper.getSideCacheFromNBT(tag, getDefaultSides());

			sideCache[0] = storedSideCache[0];
			sideCache[1] = storedSideCache[1];
			sideCache[facing] = 0;
			sideCache[BlockHelper.getLeftSide(facing)] = storedSideCache[BlockHelper.getLeftSide(storedFacing)];
			sideCache[BlockHelper.getRightSide(facing)] = storedSideCache[BlockHelper.getRightSide(storedFacing)];
			sideCache[BlockHelper.getOppositeSide(facing)] = storedSideCache[BlockHelper.getOppositeSide(storedFacing)];

			for (int i = 0; i < 6; i++) {
				if (sideCache[i] >= getNumConfig(i)) {
					sideCache[i] = 0;
				}
			}
		}
		sendUpdatePacket(Side.CLIENT);
	}

	@Override
	public void writePortableData(EntityPlayer player, NBTTagCompound tag) {

		if (!canPlayerAccess(player.getCommandSenderName())) {
			return;
		}
		RedstoneControlHelper.setItemStackTagRS(tag, this);
		ReconfigurableHelper.setItemStackTagReconfig(tag, this);
	}

	/* IReconfigurableFacing */
	@Override
	public boolean setFacing(int side) {

		if (side < 0 || side > 5) {
			return false;
		}
		if (!allowYAxisFacing() && side < 2) {
			return false;
		}
		sideCache[side] = 0;
		facing = (byte) side;
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
		sendUpdatePacket(Side.CLIENT);
		return true;
	}

	/* IReconfigurableSides */
	@Override
	public boolean decrSide(int side) {

		return augmentReconfigSides ? super.decrSide(side) : false;
	}

	@Override
	public boolean incrSide(int side) {

		return augmentReconfigSides ? super.incrSide(side) : false;
	}

	@Override
	public boolean setSide(int side, int config) {

		return augmentReconfigSides ? super.setSide(side, config) : false;
	}

	@Override
	public boolean resetSides() {

		return augmentReconfigSides ? super.resetSides() : false;
	}

	@Override
	public int getNumConfig(int side) {

		return sideConfig.numGroup;
	}

	/* ISidedInventory */
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {

		return sideConfig.slotGroups[sideCache[side]];
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int side) {

		return sideConfig.allowInsertion[sideCache[side]] ? isItemValid(stack, slot, side) : false;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int side) {

		return sideConfig.allowExtraction[sideCache[side]];
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

}
