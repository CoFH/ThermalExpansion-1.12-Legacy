package cofh.thermalexpansion.block.machine;

import cofh.api.energy.EnergyStorage;
import cofh.api.item.IAugmentItem;
import cofh.core.network.PacketCoFHBase;
import cofh.lib.util.TimeTracker;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.TileAugmentable;
import cofh.thermalexpansion.block.machine.BlockMachine.Type;
import cofh.thermalexpansion.init.TETextures;
import cofh.thermalexpansion.item.TEAugments;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.relauncher.Side;

public abstract class TileMachineBase extends TileAugmentable implements ITickable {

	protected static final SideConfig[] defaultSideConfig = new SideConfig[BlockMachine.Type.values().length];
	protected static final EnergyConfig[] defaultEnergyConfig = new EnergyConfig[BlockMachine.Type.values().length];
	protected static final String[] sounds = new String[BlockMachine.Type.values().length];
	protected static final boolean[] enableSound = new boolean[BlockMachine.Type.values().length];
	protected static final int[] lightValue = { 14, 0, 0, 15, 15, 1, 0, 14, 0, 0, 7, 15 };
	public static final boolean[] enableSecurity = new boolean[BlockMachine.Type.values().length];

	protected static final int RATE = 500;
	protected static final int AUGMENT_COUNT[] = new int[] { 3, 4, 5, 6 };
	protected static final int ENERGY_CAPACITY[] = new int[] { 2, 3, 4, 5 };
	protected static final int ENERGY_TRANSFER[] = new int[] { 3, 6, 12, 24 };
	protected static final int AUTO_TRANSFER[] = new int[] { 8, 16, 32, 64 };
	protected static final int FLUID_CAPACITY[] = new int[] { 1, 2, 4, 8 };

	public static void config() {

		for (int i = 0; i < BlockMachine.Type.values().length; i++) {
			String name = StringHelper.titleCase(BlockMachine.Type.values()[i].getName());
			String comment = "Enable this to allow for " + name + "s to be securable.";
			enableSecurity[i] = ThermalExpansion.CONFIG.get("Security", "Machine." + name + ".Securable", true, comment);

			comment = "Enable sounds for the " + name + ".";
			enableSound[i] = ThermalExpansion.CONFIG_CLIENT.get("Machine." + name, "Sound.Enable", true, comment);
		}
	}

	int processMax;
	int processRem;
	boolean wasActive;

	protected final byte type;
	protected EnergyConfig energyConfig;
	protected TimeTracker tracker = new TimeTracker();

	boolean augmentSecondaryNull;

	byte level = 0;
	int processMod = 1;
	int energyMod = 1;
	int secondaryChance = 100;

	public TileMachineBase() {

		this(Type.FURNACE);
		if (getClass() != TileMachineBase.class) {
			throw new IllegalArgumentException();
		}
	}

	public TileMachineBase(Type type) {

		this.type = (byte) type.ordinal();

		sideConfig = defaultSideConfig[this.type];
		energyConfig = defaultEnergyConfig[this.type].copy();
		energyStorage = new EnergyStorage(energyConfig.maxEnergy, energyConfig.maxPower * ENERGY_TRANSFER[level]);
		setDefaultSides();
	}

	@Override
	public int getType() {

		return type;
	}

	@Override
	public String getName() {

		return BlockMachine.Type.values()[type].getName();
	}

	@Override
	public int getLightValue() {

		return isActive ? lightValue[type] : 0;
	}

	@Override
	public boolean enableSecurity() {

		return enableSecurity[type];
	}

	@Override
	public void update() {

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
				transferOutput();
				transferInput();
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
				transferOutput();
				transferInput();
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

		return processRem <= 0 && hasValidInput();
	}

	protected boolean hasValidInput() {

		return true;
	}

	protected void processStart() {

	}

	protected void processFinish() {

	}

	protected void transferInput() {

	}

	protected void transferOutput() {

	}

	protected void updateIfChanged(boolean curActive) {

		if (curActive != isActive && !wasActive) {
			if (lightValue[type] != 0) {
				updateLighting();
			}
			sendUpdatePacket(Side.CLIENT);
		} else if (wasActive && tracker.hasDelayPassed(worldObj, 100)) {
			wasActive = false;
			if (lightValue[type] != 0) {
				updateLighting();
			}
			sendUpdatePacket(Side.CLIENT);
		}
	}

	protected void onLevelChange() {

		augments = new ItemStack[AUGMENT_COUNT[level]];
		augmentStatus = new boolean[augments.length];
		energyConfig.setParams(energyConfig.minPower, energyConfig.maxPower, energyConfig.maxEnergy * ENERGY_CAPACITY[level] / 2);
		energyStorage.setCapacity(energyConfig.maxEnergy);
		energyStorage.setMaxTransfer(energyConfig.maxPower * ENERGY_TRANSFER[level]);
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
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("ProcMax", processMax);
		nbt.setInteger("ProcRem", processRem);
		return nbt;
	}

	@Override
	public void readAugmentsFromNBT(NBTTagCompound nbt) {

		level = nbt.getByte("Level");
		onLevelChange();

		NBTTagList list = nbt.getTagList("Augments", 10);

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
	public PacketCoFHBase getPacket() {

		PacketCoFHBase payload = super.getPacket();

		payload.addByte(level);

		return payload;
	}

	@Override
	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = super.getGuiPacket();

		payload.addInt(processMax);
		payload.addInt(processRem);
		payload.addInt(processMod);
		payload.addInt(energyMod);

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketCoFHBase payload) {

		super.handleGuiPacket(payload);

		processMax = payload.getInt();
		processRem = payload.getInt();
		processMod = payload.getInt();
		energyMod = payload.getInt();
	}

	/* ITilePacketHandler */
	@Override
	public void handleTilePacket(PacketCoFHBase payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		if (!isServer) {
			byte curLevel = level;
			level = payload.getByte();

			if (curLevel != level) {
				onLevelChange();
			}
		} else {
			payload.getByte();
		}
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
				secondaryChance -= TEAugments.MACHINE_SECONDARY_MOD[augLevel];
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
				// secondaryChance += TEAugments.MACHINE_SPEED_SECONDARY_MOD[augLevel]; TODO: May bring this back; not sure.
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
		if (augmentItem.getAugmentLevel(augments[slot], TEAugments.MACHINE_NULL) > 0) {
			augmentSecondaryNull = true;
			installed = true;
		}
		if (augmentItem.getAugmentLevel(augments[slot], TEAugments.GENERAL_AUTO_OUTPUT) > 0) {
			augmentAutoOutput = true;
			installed = true;
		}
		if (augmentItem.getAugmentLevel(augments[slot], TEAugments.GENERAL_AUTO_INPUT) > 0) {
			augmentAutoInput = true;
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

		super.resetAugments();

		augmentSecondaryNull = false;

		processMod = 1;
		energyMod = 1;
		secondaryChance = 100;
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

	/* IEnergyInfo */
	@Override
	public int getInfoEnergyPerTick() {

		return calcEnergy() * energyMod;
	}

	@Override
	public int getInfoMaxEnergyPerTick() {

		return energyConfig.maxPower * energyMod;
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
	public TextureAtlasSprite getTexture(int side, int pass) {

		if (pass == 0) {
			if (side == 0) {
				return TETextures.MACHINE_BOTTOM;
			} else if (side == 1) {
				return TETextures.MACHINE_TOP;
			}
			return side != facing ? TETextures.MACHINE_SIDE : isActive ? TETextures.MACHINE_ACTIVE[type] : TETextures.MACHINE_FACE[type];
		} else if (side < 6) {
			return TETextures.CONFIG[sideConfig.sideTex[sideCache[side]]];
		}
		return TETextures.MACHINE_SIDE;
	}

	/* ISoundSource */
	@Override
	public String getSoundName() {

		return enableSound[type] ? sounds[type] : null;
	}

}
