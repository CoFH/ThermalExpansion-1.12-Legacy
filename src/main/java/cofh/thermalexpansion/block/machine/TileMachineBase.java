package cofh.thermalexpansion.block.machine;

import cofh.api.energy.EnergyStorage;
import cofh.api.item.IAugmentItem.AugmentType;
import cofh.api.tileentity.IAccelerable;
import cofh.core.network.PacketCoFHBase;
import cofh.lib.util.TimeTracker;
import cofh.lib.util.helpers.AugmentHelper;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.TilePowered;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.init.TETextures;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;

public abstract class TileMachineBase extends TilePowered implements IAccelerable, ITickable {

	public static final SideConfig[] SIDE_CONFIGS = new SideConfig[BlockMachine.Type.values().length];
	public static final SlotConfig[] SLOT_CONFIGS = new SlotConfig[BlockMachine.Type.values().length];
	public static final EnergyConfig[] ENERGY_CONFIGS = new EnergyConfig[BlockMachine.Type.values().length];
	public static final ArrayList<String>[] VALID_AUGMENTS = new ArrayList[BlockMachine.Type.values().length];
	public static final int[] LIGHT_VALUES = new int[BlockMachine.Type.values().length];

	public static final ResourceLocation[] SOUNDS = new ResourceLocation[BlockMachine.Type.values().length];

	private static boolean enableSecurity = true;

	protected static final ArrayList<String> VALID_AUGMENTS_BASE = new ArrayList<>();
	protected static final int ENERGY_BASE = 100;
	protected static final int SECONDARY_BASE = 100;

	static {
		VALID_AUGMENTS_BASE.add(TEProps.MACHINE_POWER);
		VALID_AUGMENTS_BASE.add(TEProps.MACHINE_SECONDARY);
		VALID_AUGMENTS_BASE.add(TEProps.MACHINE_SECONDARY_NULL);
	}

	public static void config() {

		String comment = "Enable this to allow for Machines to be securable.";
		enableSecurity = ThermalExpansion.CONFIG.get("Security", "Machine.Securable", true, comment);
	}

	int processMax;
	int processRem;
	boolean wasActive;
	boolean hasModeAugment;

	EnergyConfig energyConfig;
	TimeTracker tracker = new TimeTracker();

	int energyMod = ENERGY_BASE;
	int secondaryChance = SECONDARY_BASE;

	/* AUGMENTS */
	protected boolean augmentSecondaryNull;

	public TileMachineBase() {

		sideConfig = SIDE_CONFIGS[this.getType()];
		slotConfig = SLOT_CONFIGS[this.getType()];
		energyConfig = ENERGY_CONFIGS[this.getType()].copy();
		energyStorage = new EnergyStorage(energyConfig.maxEnergy, energyConfig.maxPower * 4);
		setDefaultSides();
		enableAutoOutput = true;
	}

	@Override
	public String getTileName() {

		return "tile.thermalexpansion.machine." + BlockMachine.Type.byMetadata(getType()).getName() + ".name";
	}

	@Override
	public int getLightValue() {

		return isActive ? LIGHT_VALUES[getType()] : 0;
	}

	@Override
	public boolean enableSecurity() {

		return enableSecurity;
	}

	@Override
	protected boolean setLevel(int level) {

		if (super.setLevel(level)) {
			energyConfig.setDefaultParams(getBasePower(this.level));
			energyStorage.setCapacity(energyConfig.maxEnergy).setMaxTransfer(energyConfig.maxPower * 4);
			return true;
		}
		return false;
	}

	@Override
	public void update() {

		if (ServerHelper.isClientWorld(worldObj)) {
			return;
		}
		boolean curActive = isActive;

		if (isActive) {
			processTick();

			if (canFinish()) {
				processFinish();
				transferOutput();
				transferInput();
				energyStorage.modifyEnergyStored(-processRem);

				if (!redstoneControlOrDisable() || !canStart()) {
					processOff();
				} else {
					processStart();
				}
			} else if (energyStorage.getEnergyStored() <= 0) {
				processOff();
			}
		} else if (redstoneControlOrDisable()) {
			if (timeCheck()) {
				transferOutput();
				transferInput();
			}
			if (timeCheckEighth() && canStart()) {
				processStart();
				processTick();
				isActive = true;
			}
		}
		updateIfChanged(curActive);
		chargeEnergy();
	}

	/* COMMON METHODS */
	protected int getBasePower(int level) {

		return ENERGY_CONFIGS[getType()].maxPower + level * ENERGY_CONFIGS[getType()].maxPower / 2;
	}

	protected int calcEnergy() {

		if (energyStorage.getEnergyStored() > energyConfig.maxPowerLevel) {
			return energyConfig.maxPower;
		}
		if (energyStorage.getEnergyStored() < energyConfig.minPowerLevel) {
			return Math.min(energyConfig.minPower, energyStorage.getEnergyStored());
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

	protected void processOff() {

		processRem = 0;
		isActive = false;
		wasActive = true;

		if (worldObj != null) {
			tracker.markTime(worldObj);
		}
	}

	protected int processTick() {

		if (processRem <= 0) {
			return 0;
		}
		int energy = calcEnergy();
		energyStorage.modifyEnergyStored(-energy);
		processRem -= energy;

		return energy;
	}

	protected void transferInput() {

	}

	protected void transferOutput() {

	}

	protected void updateIfChanged(boolean curActive) {

		if (curActive != isActive && !wasActive) {
			if (LIGHT_VALUES[getType()] != 0) {
				updateLighting();
			}
			sendTilePacket(Side.CLIENT);
		} else if (wasActive && tracker.hasDelayPassed(worldObj, 100)) {
			wasActive = false;
			if (LIGHT_VALUES[getType()] != 0) {
				updateLighting();
			}
			sendTilePacket(Side.CLIENT);
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
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("ProcMax", processMax);
		nbt.setInteger("ProcRem", processRem);
		return nbt;
	}

	/* NETWORK METHODS */

	/* SERVER -> CLIENT */
	@Override
	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = super.getGuiPacket();

		payload.addInt(processMax);
		payload.addInt(processRem);

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketCoFHBase payload) {

		super.handleGuiPacket(payload);

		processMax = payload.getInt();
		processRem = payload.getInt();
	}

	/* HELPERS */
	@Override
	protected void preAugmentInstall() {

		energyConfig.setDefaultParams(getBasePower(this.level));

		energyMod = ENERGY_BASE;
		secondaryChance = SECONDARY_BASE;
		hasModeAugment = false;

		augmentSecondaryNull = false;
	}

	@Override
	protected void postAugmentInstall() {

		energyStorage.setCapacity(energyConfig.maxEnergy).setMaxTransfer(energyConfig.maxPower * 2);
	}

	@Override
	protected boolean isValidAugment(AugmentType type, String id) {

		if (type == AugmentType.CREATIVE && !isCreative) {
			return false;
		}
		if (type == AugmentType.MODE && hasModeAugment) {
			return false;
		}
		return VALID_AUGMENTS_BASE.contains(id) || VALID_AUGMENTS[getType()].contains(id) || super.isValidAugment(type, id);
	}

	@Override
	protected boolean installAugmentToSlot(int slot) {

		String id = AugmentHelper.getAugmentIdentifier(augments[slot]);

		if (TEProps.MACHINE_POWER.equals(id)) {
			// Power Boost
			energyConfig.setDefaultParams(energyConfig.maxPower + getBasePower(this.level));

			// Efficiency Loss
			energyMod += 20;
			return true;
		}
		if (TEProps.MACHINE_SECONDARY.equals(id)) {
			// SeoondaryChance
			secondaryChance -= 15;

			// Efficiency Loss
			energyMod += 10;
			return true;
		}
		if (!augmentSecondaryNull && TEProps.MACHINE_SECONDARY_NULL.equals(id)) {
			augmentSecondaryNull = true;
			return true;
		}
		return super.installAugmentToSlot(slot);
	}

	/* IAccelerable */
	@Override
	public int updateAccelerable() {

		return processTick();
	}

	/* IInventory */
	@Override
	public ItemStack decrStackSize(int slot, int amount) {

		ItemStack stack = super.decrStackSize(slot, amount);

		if (ServerHelper.isServerWorld(worldObj) && slot <= getMaxInputSlot()) {
			if (isActive && (inventory[slot] == null || !hasValidInput())) {
				processOff();
			}
		}
		return stack;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {

		if (ServerHelper.isServerWorld(worldObj) && slot <= getMaxInputSlot()) {
			if (isActive && inventory[slot] != null) {
				if (stack == null || !stack.isItemEqual(inventory[slot]) || !hasValidInput()) {
					processOff();
				}
			}
		}
		super.setInventorySlotContents(slot, stack);
	}

	@Override
	public void markDirty() {

		if (isActive && !hasValidInput()) {
			processOff();
		}
		super.markDirty();
	}

	/* IEnergyInfo */
	@Override
	public int getInfoEnergyPerTick() {

		if (!isActive) {
			return 0;
		}
		return calcEnergy();
	}

	@Override
	public int getInfoMaxEnergyPerTick() {

		return energyConfig.maxPower;
	}

	/* IReconfigurableFacing */
	@Override
	public boolean setFacing(int side) {

		if (side < 2 || side > 5) {
			return false;
		}
		sideCache[side] = 0;
		facing = (byte) side;
		markChunkDirty();
		sendTilePacket(Side.CLIENT);
		return true;
	}

	/* ISidedTexture */
	@Override
	public int getNumPasses() {

		return 2;
	}

	@Override
	public TextureAtlasSprite getTexture(int side, int pass) {

		if (pass == 0) {
			if (side == 0) {
				return TETextures.MACHINE_BOTTOM[level];
			} else if (side == 1) {
				return TETextures.MACHINE_TOP[level];
			}
			return side != facing ? TETextures.MACHINE_SIDE[level] : isActive ? TETextures.MACHINE_ACTIVE[getType()] : TETextures.MACHINE_FACE[getType()];
		} else if (side < 6) {
			return TETextures.CONFIG[sideConfig.sideTex[sideCache[side]]];
		}
		return TETextures.MACHINE_SIDE[level];
	}

	/* ISoundSource */
	@Override
	public ResourceLocation getSoundLocation() {

		return SOUNDS[getType()];
	}

}
