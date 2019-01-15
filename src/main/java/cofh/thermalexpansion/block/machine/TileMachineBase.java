package cofh.thermalexpansion.block.machine;

import cofh.api.core.IAccelerable;
import cofh.api.item.IAugmentItem.AugmentType;
import cofh.api.item.IUpgradeItem;
import cofh.api.item.IUpgradeItem.UpgradeType;
import cofh.core.block.TilePowered;
import cofh.core.init.CoreProps;
import cofh.core.network.PacketBase;
import cofh.core.util.TimeTracker;
import cofh.core.util.core.EnergyConfig;
import cofh.core.util.core.SideConfig;
import cofh.core.util.core.SlotConfig;
import cofh.core.util.helpers.AugmentHelper;
import cofh.core.util.helpers.MathHelper;
import cofh.core.util.helpers.ServerHelper;
import cofh.redstoneflux.impl.EnergyStorage;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.machine.BlockMachine.Type;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.init.TETextures;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;
import java.util.HashSet;

public abstract class TileMachineBase extends TilePowered implements IAccelerable, ITickable {

	public static final SideConfig[] SIDE_CONFIGS = new SideConfig[Type.values().length];
	public static final SideConfig[] ALT_SIDE_CONFIGS = new SideConfig[Type.values().length];

	public static final SlotConfig[] SLOT_CONFIGS = new SlotConfig[Type.values().length];
	public static final EnergyConfig[] ENERGY_CONFIGS = new EnergyConfig[Type.values().length];
	public static final HashSet<String>[] VALID_AUGMENTS = new HashSet[Type.values().length];
	public static final int[] LIGHT_VALUES = new int[Type.values().length];

	public static final int MIN_BASE_POWER = 10;
	public static final int MAX_BASE_POWER = 200;
	public static int[] POWER_SCALING = { 100, 150, 200, 250, 300 };
	public static int[] ENERGY_SCALING = { 100, 100, 100, 100, 100 };
	public static byte[] NUM_AUGMENTS = { 0, 1, 2, 3, 4 };

	protected static boolean enableCreative = false;
	protected static boolean enableSecurity = true;
	protected static boolean enableUpgrades = true;
	protected static boolean customAugmentScaling = false;
	protected static boolean customPowerScaling = false;
	protected static boolean customEnergyScaling = false;
	public static boolean disableAutoInput = false;
	public static boolean disableAutoOutput = false;
	// protected static boolean limitedConfig = false;
	public static boolean smallStorage = false;

	protected static final HashSet<String> VALID_AUGMENTS_BASE = new HashSet<>();
	protected static final int ENERGY_BASE = 100;
	protected static final int POWER_BASE = 100;
	protected static final int SECONDARY_BASE = 100;
	protected static final int SECONDARY_MIN = 5;
	protected static final int REUSE_BASE = 0;
	protected static final int REUSE_MAX = 95;

	static {
		VALID_AUGMENTS_BASE.add(TEProps.MACHINE_POWER);
	}

	public static void config() {

		String category = "Machine";
		String comment = "If TRUE, Machines are securable.";
		enableSecurity = ThermalExpansion.CONFIG.get(category, "Securable", true, comment);

		comment = "If TRUE, Machines are upgradable. If disabled, be sure and change the Augment Scaling config options as well.";
		enableUpgrades = ThermalExpansion.CONFIG.get(category, "Upgradable", enableUpgrades, comment);

		comment = "If TRUE, 'Classic' Crafting is enabled - Non-Creative Upgrade Kits WILL NOT WORK in a Crafting Grid.";
		BlockMachine.enableClassicRecipes = ThermalExpansion.CONFIG.get(category, "ClassicCrafting", BlockMachine.enableClassicRecipes, comment);

		comment = "If TRUE, Machines can be upgraded in a Crafting Grid using Kits. If Classic Crafting is enabled, only the Creative Conversion Kit may be used in this fashion.";
		BlockMachine.enableUpgradeKitCrafting = ThermalExpansion.CONFIG.get(category, "UpgradeKitCrafting", BlockMachine.enableUpgradeKitCrafting, comment);

		comment = "If TRUE, Machine Augment Slot scaling will use a custom set of values rather than default behavior (1/level).";
		customAugmentScaling = ThermalExpansion.CONFIG.get(category, "CustomAugmentScaling", customAugmentScaling, comment);

		comment = "If TRUE, Machine RF/t (POWER) scaling will use a custom set of values rather than default behavior. The default custom configuration provides a reasonable alternate progression.";
		customPowerScaling = ThermalExpansion.CONFIG.get(category, "CustomPowerScaling", customPowerScaling, comment);

		comment = "If TRUE, Machine Total RF (ENERGY) scaling will use a custom set of values rather than default behavior (no scaling). The default custom configuration provides an alternate progression where machines use 5% additional total RF per tier.";
		customEnergyScaling = ThermalExpansion.CONFIG.get(category, "CustomEnergyScaling", customEnergyScaling, comment);

		comment = "If TRUE, Machines will no longer have Auto-Input functionality. Not recommended, but knock yourself out.";
		disableAutoInput = ThermalExpansion.CONFIG.get(category, "DisableAutoInput", disableAutoInput, comment);

		comment = "If TRUE, Machines will no longer have Auto-Output functionality. Not recommended, but knock yourself out.";
		disableAutoOutput = ThermalExpansion.CONFIG.get(category, "DisableAutoOutput", disableAutoOutput, comment);

		//		comment = "If TRUE, Machines will only have two side configurations: Open and Closed. Machines will not be able to Auto-Input or Auto-Output.";
		//		limitedConfig = ThermalExpansion.CONFIG.get(category, "LimitedConfig", limitedConfig, comment);

		comment = "If TRUE, Machines will have much smaller internal energy (RF) storage. Processing speed will no longer scale with internal energy.";
		smallStorage = ThermalExpansion.CONFIG.get(category, "SmallStorage", smallStorage, comment);

		/* CUSTOM SCALING */
		boolean validScaling;
		byte[] customAugments = { 0, 1, 2, 3, 4 };
		int[] customPowerScale = { 100, 150, 250, 400, 600 };
		int[] customEnergyScale = { 100, 105, 110, 115, 120 };

		category = "Machine.AugmentSlots";
		comment = "Adjust the number of augments that Machines have at any given Level.\nProgression will be checked for validity - upgrading a block cannot result in fewer slots.";
		ThermalExpansion.CONFIG.getCategory(category).setComment(comment);
		validScaling = true;

		for (int i = CoreProps.LEVEL_MIN; i <= CoreProps.LEVEL_MAX; i++) {
			customAugments[i] = (byte) ThermalExpansion.CONFIG.getConfiguration().getInt("Level" + i, category, customAugments[i], CoreProps.AUGMENT_MIN, CoreProps.AUGMENT_MAX, "Augment Slots for Level " + i + " Machines.");
		}
		for (int i = 1; i < customAugments.length; i++) {

			if (customAugments[i] < customAugments[i - 1]) {
				validScaling = false;
			}
		}
		if (customAugmentScaling) {
			if (!validScaling) {
				ThermalExpansion.LOG.error(category + " settings are invalid. They will not be used.");
			} else {
				System.arraycopy(customAugments, 0, NUM_AUGMENTS, 0, NUM_AUGMENTS.length);
			}
		}
		category = "Machine.CustomPowerScaling";
		comment = "ADVANCED FEATURE - ONLY EDIT IF YOU KNOW WHAT YOU ARE DOING.\nValues are expressed as a percentage of Base Power; Base Scale Factor is 100 percent.\nValues will be checked for validity and rounded down to the nearest 10.";
		ThermalExpansion.CONFIG.getCategory(category).setComment(comment);
		validScaling = true;

		for (int i = CoreProps.LEVEL_MIN + 1; i <= CoreProps.LEVEL_MAX; i++) {
			customPowerScale[i] = ThermalExpansion.CONFIG.getConfiguration().getInt("Level" + i, category, customPowerScale[i], POWER_BASE, POWER_BASE * ((i + 1) * (i + 1)), "Scale Factor for Level " + i + " Machines.");
		}
		for (int i = 1; i < customPowerScale.length; i++) {
			customPowerScale[i] /= 10;
			customPowerScale[i] *= 10;

			if (customPowerScale[i] < customPowerScale[i - 1]) {
				validScaling = false;
			}
		}
		if (customPowerScaling) {
			if (!validScaling) {
				ThermalExpansion.LOG.error(category + " settings are invalid. They will not be used.");
			} else {
				System.arraycopy(customPowerScale, 0, POWER_SCALING, 0, POWER_SCALING.length);
			}
		}
		category = "Machine.CustomEnergyScaling";
		comment = "ADVANCED FEATURE - ONLY EDIT IF YOU KNOW WHAT YOU ARE DOING.\nValues are expressed as a percentage of Base Energy; Base Scale Factor is 100 percent.\nValues will be checked for validity and rounded down to the nearest 5.";
		ThermalExpansion.CONFIG.getCategory(category).setComment(comment);
		validScaling = true;

		for (int i = CoreProps.LEVEL_MIN + 1; i <= CoreProps.LEVEL_MAX; i++) {
			customEnergyScale[i] = ThermalExpansion.CONFIG.getConfiguration().getInt("Level" + i, category, customEnergyScale[i], ENERGY_BASE, ENERGY_BASE * ((i + 1) * (i + 1)), "Scale Factor for Level " + i + " Machines.");
		}
		for (int i = 1; i < customEnergyScale.length; i++) {
			customEnergyScale[i] /= 20;
			customEnergyScale[i] *= 20;

			if (customEnergyScale[i] < customEnergyScale[i - 1]) {
				validScaling = false;
			}
		}
		if (customEnergyScaling) {
			if (!validScaling) {
				ThermalExpansion.LOG.error(category + " settings are invalid. They will not be used.");
			} else {
				System.arraycopy(customEnergyScale, 0, ENERGY_SCALING, 0, ENERGY_SCALING.length);
			}
		}
	}

	int processMax;
	int processRem;
	boolean hasModeAugment;

	EnergyConfig energyConfig;
	TimeTracker tracker = new TimeTracker();

	int energyMod = ENERGY_BASE;
	int secondaryChance = SECONDARY_BASE;
	int reuseChance = 0;

	/* AUGMENTS */
	protected boolean augmentSecondaryNull;

	public TileMachineBase() {

		sideConfig = SIDE_CONFIGS[this.getType()];
		slotConfig = SLOT_CONFIGS[this.getType()];
		energyConfig = ENERGY_CONFIGS[this.getType()].copy();
		energyStorage = new EnergyStorage(energyConfig.maxEnergy, energyConfig.maxPower * 4);
		Arrays.fill(augments, ItemStack.EMPTY);
		setDefaultSides();
		enableAutoOutput = true;
	}

	@Override
	protected Object getMod() {

		return ThermalExpansion.instance;
	}

	@Override
	protected String getModVersion() {

		return ThermalExpansion.VERSION;
	}

	@Override
	protected String getTileName() {

		return "tile.thermalexpansion.machine." + Type.values()[getType()].getName() + ".name";
	}

	@Override
	protected int getLevelAutoInput() {

		return TEProps.levelAutoInput;
	}

	@Override
	protected int getLevelAutoOutput() {

		return TEProps.levelAutoOutput;
	}

	@Override
	protected int getLevelRSControl() {

		return TEProps.levelRedstoneControl;
	}

	@Override
	public int getLightValue() {

		return isActive ? LIGHT_VALUES[getType()] : 0;
	}

	@Override
	public boolean enableSecurity() {

		return enableSecurity;
	}

	/* IUpgradeable */
	@Override
	public boolean canUpgrade(ItemStack upgrade) {

		if (!AugmentHelper.isUpgradeItem(upgrade) || !enableUpgrades) {
			return false;
		}
		UpgradeType uType = ((IUpgradeItem) upgrade.getItem()).getUpgradeType(upgrade);
		int uLevel = ((IUpgradeItem) upgrade.getItem()).getUpgradeLevel(upgrade);

		switch (uType) {
			case INCREMENTAL:
				if (uLevel == level + 1) {
					return !BlockMachine.enableClassicRecipes;
				}
				break;
			case FULL:
				if (uLevel > level) {
					return !BlockMachine.enableClassicRecipes;
				}
				break;
			case CREATIVE:
				return !isCreative && enableCreative;
		}
		return false;
	}

	@Override
	public boolean smallStorage() {

		return smallStorage;
	}

	@Override
	protected boolean setLevel(int level) {

		if (super.setLevel(level)) {
			energyConfig.setDefaultParams(getBasePower(this.level), smallStorage);
			energyStorage.setCapacity(energyConfig.maxEnergy).setMaxTransfer(energyConfig.maxPower * 4);
			return true;
		}
		return false;
	}

	@Override
	protected int getNumAugmentSlots(int level) {

		return NUM_AUGMENTS[MathHelper.clamp(level, CoreProps.LEVEL_MIN, CoreProps.LEVEL_MAX)];
	}

	@Override
	protected void setLevelFlags() {

		super.setLevelFlags();

		if (disableAutoInput) {
			hasAutoInput = false;
		}
		if (disableAutoOutput) {
			hasAutoOutput = false;
		}

		//		if (limitedConfig) {
		//			hasAutoInput = false;
		//			hasAutoOutput = false;
		//		}
	}

	@Override
	public void update() {

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

		return ENERGY_CONFIGS[getType()].maxPower * POWER_SCALING[MathHelper.clamp(level, CoreProps.LEVEL_MIN, CoreProps.LEVEL_MAX)] / POWER_BASE;
	}

	protected int getBaseEnergy(int level) {

		return ENERGY_SCALING[MathHelper.clamp(level, CoreProps.LEVEL_MIN, CoreProps.LEVEL_MAX)] / ENERGY_BASE;
	}

	protected int calcEnergy() {

		if (energyStorage.getEnergyStored() >= energyConfig.maxPowerLevel) {
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

	protected void clearRecipe() {

	}

	protected void getRecipe() {

	}

	protected void processStart() {

	}

	protected void processFinish() {

	}

	protected void processOff() {

		processRem = 0;
		isActive = false;
		wasActive = true;
		clearRecipe();

		if (world != null) {
			tracker.markTime(world);
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
		} else if (wasActive && tracker.hasDelayPassed(world, CoreProps.tileUpdateDelay)) {
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
	public PacketBase getGuiPacket() {

		PacketBase payload = super.getGuiPacket();

		payload.addInt(processMax);
		payload.addInt(processRem);

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketBase payload) {

		super.handleGuiPacket(payload);

		processMax = payload.getInt();
		processRem = payload.getInt();
	}

	/* HELPERS */
	@Override
	protected void preAugmentInstall() {

		energyConfig.setDefaultParams(getBasePower(this.level), smallStorage);

		energyMod = ENERGY_BASE;
		secondaryChance = SECONDARY_BASE;
		reuseChance = REUSE_BASE;
		hasModeAugment = false;

		augmentSecondaryNull = false;
	}

	@Override
	protected void postAugmentInstall() {

		energyStorage.setCapacity(energyConfig.maxEnergy).setMaxTransfer(energyConfig.maxPower * 4);
		energyMod *= getBaseEnergy(this.level);
		secondaryChance = MathHelper.clamp(secondaryChance, SECONDARY_MIN, SECONDARY_BASE);
		reuseChance = MathHelper.clamp(reuseChance, REUSE_BASE, REUSE_MAX);
	}

	@Override
	protected boolean isValidAugment(AugmentType type, String id) {

		if (type == AugmentType.CREATIVE && !isCreative) {
			return false;
		}
		if (type == AugmentType.MODE && hasModeAugment) {
			return false;
		}
		if (augmentSecondaryNull && TEProps.MACHINE_SECONDARY_NULL.equals(id)) {
			return false;
		}
		return VALID_AUGMENTS_BASE.contains(id) || VALID_AUGMENTS[getType()].contains(id) || super.isValidAugment(type, id);
	}

	@Override
	protected boolean installAugmentToSlot(int slot) {

		String id = AugmentHelper.getAugmentIdentifier(augments[slot]);

		if (TEProps.MACHINE_POWER.equals(id)) {
			energyConfig.setDefaultParams(energyConfig.maxPower + getBasePower(this.level), smallStorage);
			energyMod += 10;
			return true;
		}
		if (TEProps.MACHINE_SECONDARY.equals(id)) {
			secondaryChance -= 15;
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

		if (ServerHelper.isServerWorld(world) && slot <= getMaxInputSlot()) {
			if (isActive && (inventory[slot].isEmpty() || !hasValidInput())) {
				processOff();
			}
		}
		return stack;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {

		if (ServerHelper.isServerWorld(world) && slot <= getMaxInputSlot()) {
			if (isActive && !inventory[slot].isEmpty()) {
				if (stack.isEmpty() || !stack.isItemEqual(inventory[slot]) || !hasValidInput()) {
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
	public boolean setFacing(int side, boolean alternate) {

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
	@SideOnly (Side.CLIENT)
	public TextureAtlasSprite getTexture(int side, int pass) {

		if (pass == 0) {
			if (side == 0) {
				return TETextures.MACHINE_BOTTOM;
			} else if (side == 1) {
				return TETextures.MACHINE_TOP;
			}
			return side != facing ? TETextures.MACHINE_SIDE : isActive ? TETextures.MACHINE_ACTIVE[getType()] : TETextures.MACHINE_FACE[getType()];
		} else if (side < 6) {
			return TETextures.CONFIG[sideConfig.sideTypes[sideCache[side]]];
		}
		return TETextures.MACHINE_SIDE;
	}

	/* RENDERING */
	public boolean hasFluidUnderlay() {

		return false;
	}

	public FluidStack getRenderFluid() {

		return null;
	}

	public int getColorMask(BlockRenderLayer layer, EnumFacing side) {

		return 0xFFFFFFFF;
	}

}
