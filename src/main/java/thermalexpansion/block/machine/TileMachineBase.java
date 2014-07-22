package thermalexpansion.block.machine;

import cofh.api.energy.EnergyStorage;
import cofh.api.item.IAugmentItem;
import cofh.network.CoFHPacket;
import cofh.render.IconRegistry;
import cofh.util.BlockHelper;
import cofh.util.MathHelper;
import cofh.util.RedstoneControlHelper;
import cofh.util.ServerHelper;
import cofh.util.StringHelper;
import cofh.util.TimeTracker;
import cpw.mods.fml.relauncher.Side;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.IIcon;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.block.TileAugmentable;
import thermalexpansion.core.TEProps;
import thermalexpansion.item.TEAugments;
import thermalexpansion.util.ReconfigurableHelper;

public abstract class TileMachineBase extends TileAugmentable {

	protected static final SideConfig[] defaultSideConfig = new SideConfig[BlockMachine.Types.values().length];
	protected static final EnergyConfig[] defaultEnergyConfig = new EnergyConfig[BlockMachine.Types.values().length];
	protected static final int[] lightValue = { 14, 0, 0, 15, 15, 0, 0, 14, 0, 0, 7 };
	public static boolean[] enableSecurity = { true, true, true, true, true, true, true, true, true, true, true };

	protected static final int RATE = 500;
	protected static final int AUGMENT_COUNT[] = new int[] { 3, 4, 5, 6 };
	protected static final int ENERGY_TRANSFER[] = new int[] { 3, 6, 12, 24 };

	public static void configure() {

		for (int i = 0; i < BlockMachine.Types.values().length; i++) {
			String name = StringHelper.titleCase(BlockMachine.NAMES[i]);
			String comment = "Enable this to allow for " + name + "s to be securable. (Default: true)";
			enableSecurity[i] = ThermalExpansion.config.get("security", "Machine." + name + ".Secureable", enableSecurity[i], comment);
		}
	}

	int processMax;
	int processRem;
	boolean wasActive;

	protected EnergyConfig energyConfig;
	protected TimeTracker tracker = new TimeTracker();

	byte level = 0;
	int processMod = 1;
	int energyMod = 1;
	int secondaryChance = 100;

	public TileMachineBase() {

		super();

		sideConfig = defaultSideConfig[getType()];
		energyConfig = defaultEnergyConfig[getType()];
		energyStorage = new EnergyStorage(energyConfig.maxEnergy, energyConfig.maxPower * ENERGY_TRANSFER[level]);
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
	public boolean enableSecurity() {

		return enableSecurity[getType()];
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

	/* GUI METHODS */
	@Override
	public int getScaledProgress(int scale) {

		if (!isActive || processMax <= 0 || processRem <= 0) {
			return 0;
		}
		return scale * (processMax - processRem) / processMax;
	}

	@Override
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

		processMax = nbt.getInteger("ProcMax");
		processRem = nbt.getInteger("ProcRem");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("ProcMax", processMax);
		nbt.setInteger("ProcRem", processRem);
	}

	@Override
	public void readAugmentsFromNBT(NBTTagCompound nbt) {

		level = nbt.getByte("Level");
		energyStorage.setMaxTransfer(energyConfig.maxPower * ENERGY_TRANSFER[level]);

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

	@Override
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

		return payload;
	}

	@Override
	public CoFHPacket getGuiPacket() {

		CoFHPacket payload = super.getGuiPacket();

		payload.addInt(processMax);
		payload.addInt(processRem);
		payload.addInt(processMod);
		payload.addInt(energyMod);

		return payload;
	}

	@Override
	protected void handleGuiPacket(CoFHPacket payload) {

		super.handleGuiPacket(payload);

		processMax = payload.getInt();
		processRem = payload.getInt();
		processMod = payload.getInt();
		energyMod = payload.getInt();
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
		} else {
			payload.getByte();
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

	/* AUGMENT HELPERS */
	@Override
	protected boolean installAugment(int slot) {

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
		if (augmentItem.getAugmentLevel(augments[slot], TEAugments.ENERGY_STORAGE) > 0) {
			int augLevel = Math.min(TEAugments.NUM_ENERGY_STORAGE, augmentItem.getAugmentLevel(augments[slot], TEAugments.ENERGY_STORAGE));

			if (augLevel > level) {
				return false;
			}
			if (hasDuplicateAugment(TEAugments.ENERGY_STORAGE, augLevel, slot)) {
				return false;
			}
			if (hasAugmentChain(TEAugments.ENERGY_STORAGE, augLevel)) {
				energyStorage.setCapacity(Math.max(energyStorage.getMaxEnergyStored(), energyConfig.maxEnergy * TEAugments.ENERGY_STORAGE_MOD[augLevel]));
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

	@Override
	protected void onInstalled() {

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

	@Override
	protected void resetAugments() {

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
		if (augmentRedstoneControl || augmentReconfigSides) {
			sendUpdatePacket(Side.CLIENT);
		}
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
		markDirty();
		sendUpdatePacket(Side.CLIENT);
		return true;
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
