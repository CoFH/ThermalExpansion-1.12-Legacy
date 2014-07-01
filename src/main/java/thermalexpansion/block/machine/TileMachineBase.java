package thermalexpansion.block.machine;

import cofh.api.core.IAugmentable;
import cofh.api.core.IEnergyInfo;
import cofh.api.energy.EnergyStorage;
import cofh.api.item.IAugmentItem;
import cofh.network.CoFHPacket;
import cofh.render.IconRegistry;
import cofh.util.MathHelper;
import cofh.util.ServerHelper;
import cofh.util.TimeTracker;
import cpw.mods.fml.relauncher.Side;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.IIcon;

import thermalexpansion.block.TileReconfigurable;
import thermalexpansion.core.TEProps;
import thermalexpansion.util.Utils;

public abstract class TileMachineBase extends TileReconfigurable implements IAugmentable, IEnergyInfo, ISidedInventory {

	protected static final SideConfig[] defaultSideConfig = new SideConfig[BlockMachine.Types.values().length];
	protected static final EnergyConfig[] defaultEnergyConfig = new EnergyConfig[BlockMachine.Types.values().length];
	protected static final int[] lightValue = { 14, 0, 0, 15, 15, 0, 0, 14, 0, 0, 7 };

	protected static final int RATE = 100;

	int processMax;
	int processRem;
	boolean wasActive;

	SideConfig sideConfig;
	EnergyConfig energyConfig;
	TimeTracker tracker = new TimeTracker();

	/* Augment Variables */
	boolean[] augmentStatus = new boolean[3];
	ItemStack[] augments = new ItemStack[3];

	int level = 0;
	int processMod = 1;
	int energyMod = 1;
	int secondaryChance = 100;

	public boolean augmentAutoTransfer = true;
	public boolean augmentReconfigSides = true;
	public boolean augmentRSControl = true;

	public TileMachineBase() {

		super();

		sideConfig = defaultSideConfig[getType()];
		energyConfig = defaultEnergyConfig[getType()];
		energyStorage = new EnergyStorage(energyConfig.maxEnergy, energyConfig.maxPower * 10);
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

	/* NETWORK METHODS */
	@Override
	public CoFHPacket getGuiPacket() {

		CoFHPacket payload = super.getGuiPacket();
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

	@Override
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
	}

	/* AUGMENT HELPERS */
	private boolean hasAugment(String type, int level) {

		for (int i = 0; i < augments.length; i++) {
			if (Utils.isAugmentItem(augments[i]) && ((IAugmentItem) augments[i].getItem()).getAugmentLevel(augments[i], type) == level) {
				return true;
			}
		}
		return false;
	}

	private boolean hasAugmentChain(String type, int level) {

		boolean preReq = true;
		for (int i = 1; i < level; i++) {
			preReq = preReq && hasAugment(type, i);
		}
		return preReq;
	}

	private boolean installAugment(int slot) {

		IAugmentItem augmentItem = (IAugmentItem) augments[slot].getItem();
		boolean installed = false;

		if (augmentItem.getAugmentLevel(augments[slot], MACHINE_SECONDARY) > 0) {
			int level = Math.min(NUM_MACHINE_SECONDARY, augmentItem.getAugmentLevel(augments[slot], MACHINE_SECONDARY));
			if (hasAugment(MACHINE_SECONDARY, level)) {
				return false;
			}
			if (hasAugmentChain(MACHINE_SECONDARY, level)) {
				secondaryChance -= 15;
				installed = true;
			} else {
				return false;
			}
		}
		if (augmentItem.getAugmentLevel(augments[slot], MACHINE_SPEED) > 0) {
			int level = Math.min(NUM_MACHINE_SPEED, augmentItem.getAugmentLevel(augments[slot], MACHINE_SPEED));
			if (hasAugment(MACHINE_SPEED, level)) {
				return false;
			}
			if (hasAugmentChain(MACHINE_SPEED, level)) {
				secondaryChance += 5;
				processMod = Math.max(processMod, MACHINE_SPEED_PROCESS_MOD[level]);
				energyMod = Math.max(energyMod, MACHINE_SPEED_ENERGY_MOD[level]);
				installed = true;
			} else {
				return false;
			}
		}
		if (augmentItem.getAugmentLevel(augments[slot], GENERAL_AUTO_TRANSFER) > 0) {
			augmentAutoTransfer = true;
			installed = true;
		}
		if (augmentItem.getAugmentLevel(augments[slot], GENERAL_RECONFIG_SIDES) > 0) {
			augmentReconfigSides = true;
			installed = true;
		}
		if (augmentItem.getAugmentLevel(augments[slot], GENERAL_RS_CONTROL) > 0) {
			augmentRSControl = true;
			installed = true;
		}
		return installed;
	}

	private void onInstalled() {

		if (!augmentReconfigSides) {
			setDefaultSides();
		}
		if (!augmentRSControl) {
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
		augmentRSControl = false;
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
