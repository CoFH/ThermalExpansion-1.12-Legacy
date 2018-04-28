package cofh.thermalexpansion.block.dynamo;

import cofh.api.core.IAccelerable;
import cofh.api.item.IAugmentItem.AugmentType;
import cofh.api.item.IUpgradeItem;
import cofh.api.item.IUpgradeItem.UpgradeType;
import cofh.api.tileentity.IEnergyInfo;
import cofh.api.tileentity.IReconfigurableFacing;
import cofh.api.tileentity.ISteamInfo;
import cofh.core.block.TileInventory;
import cofh.core.fluid.FluidTankCore;
import cofh.core.init.CoreProps;
import cofh.core.network.PacketBase;
import cofh.core.render.TextureHelper;
import cofh.core.util.TimeTracker;
import cofh.core.util.helpers.*;
import cofh.redstoneflux.api.IEnergyProvider;
import cofh.redstoneflux.api.IEnergyReceiver;
import cofh.redstoneflux.api.IEnergyStorage;
import cofh.redstoneflux.impl.EnergyStorage;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.dynamo.BlockDynamo.Type;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalfoundation.init.TFFluids;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashSet;

public abstract class TileDynamoBase extends TileInventory implements ITickable, IAccelerable, IEnergyProvider, IReconfigurableFacing, ISidedInventory, IEnergyInfo, ISteamInfo {

	protected static final EnergyConfig[] ENERGY_CONFIGS = new EnergyConfig[Type.values().length];
	protected static final HashSet<String>[] VALID_AUGMENTS = new HashSet[Type.values().length];

	public static final int MIN_BASE_POWER = 10;
	public static final int MAX_BASE_POWER = 200;
	public static int[] POWER_SCALING = { 100, 150, 200, 250, 300 };
	public static int[] CUSTOM_POWER_SCALING = { 100, 150, 250, 400, 600 };

	protected static boolean enableCreative = false;
	protected static boolean enableSecurity = true;
	protected static boolean customScaling = false;
	public static boolean smallStorage = false;

	protected static final HashSet<String> VALID_AUGMENTS_BASE = new HashSet<>();
	protected static final int ENERGY_BASE = 100;
	protected static final int POWER_BASE = 100;

	public static final int[] COIL_LIGHT = { 7, 0 };
	public static final boolean[] COIL_UNDERLAY = { false, true };

	static {
		VALID_AUGMENTS_BASE.add(TEProps.DYNAMO_POWER);
		VALID_AUGMENTS_BASE.add(TEProps.DYNAMO_EFFICIENCY);
		VALID_AUGMENTS_BASE.add(TEProps.DYNAMO_COIL_DUCT);
		VALID_AUGMENTS_BASE.add(TEProps.DYNAMO_THROTTLE);
	}

	public static void config() {

		String category = "Dynamo";
		String comment = "If TRUE, Dynamos are securable.";
		enableSecurity = ThermalExpansion.CONFIG.get(category, "Securable", enableSecurity, comment);

		comment = "If TRUE, 'Classic' Crafting is enabled - Non-Creative Upgrade Kits WILL NOT WORK in a Crafting Grid.";
		BlockDynamo.enableClassicRecipes = ThermalExpansion.CONFIG.get(category, "ClassicCrafting", BlockDynamo.enableClassicRecipes, comment);

		comment = "If TRUE, Dynamos can be upgraded in a Crafting Grid using Kits. If Classic Crafting is enabled, only the Creative Conversion Kit may be used in this fashion.";
		BlockDynamo.enableUpgradeKitCrafting = ThermalExpansion.CONFIG.get(category, "UpgradeKitCrafting", BlockDynamo.enableUpgradeKitCrafting, comment);

		comment = "If TRUE, Dynamo RF/t (POWER) scaling will use a custom set of values rather than default behavior. The default custom configuration provides a reasonable alternate progression.";
		customScaling = ThermalExpansion.CONFIG.get(category, "CustomPowerScaling", customScaling, comment);

		comment = "If TRUE, Dynamos will have much smaller internal energy (RF) storage. Generation speed will no longer scale with internal energy.";
		smallStorage = ThermalExpansion.CONFIG.get(category, "SmallStorage", smallStorage, comment);

		/* CUSTOM SCALING */
		category = "Dynamo.CustomPowerScaling";
		comment = "ADVANCED FEATURE - ONLY EDIT IF YOU KNOW WHAT YOU ARE DOING.\nValues are expressed as a percentage of Base Power; Base Scale Factor is 100 percent.\nValues will be checked for validity and rounded down to the nearest 10.";

		ThermalExpansion.CONFIG.getCategory(category).setComment(comment);
		boolean validScaling = true;

		for (int i = CoreProps.LEVEL_MIN + 1; i <= CoreProps.LEVEL_MAX; i++) {
			CUSTOM_POWER_SCALING[i] = ThermalExpansion.CONFIG.getConfiguration().getInt("Level" + i, category, CUSTOM_POWER_SCALING[i], POWER_BASE, POWER_BASE * ((i + 1) * (i + 1)), "Scale Factor for Level " + i + " Dynamos.");
		}
		for (int i = 1; i < CUSTOM_POWER_SCALING.length; i++) {
			CUSTOM_POWER_SCALING[i] /= 10;
			CUSTOM_POWER_SCALING[i] *= 10;

			if (CUSTOM_POWER_SCALING[i] <= CUSTOM_POWER_SCALING[i - 1]) {
				validScaling = false;
			}
		}
		if (customScaling) {
			if (!validScaling) {
				ThermalExpansion.LOG.error(category + " settings are invalid. They will not be used.");
			} else {
				System.arraycopy(CUSTOM_POWER_SCALING, 0, POWER_SCALING, 0, POWER_SCALING.length);
			}
		}
	}

	byte facing = 1;
	int fuelRF;
	int maxFuelRF;
	boolean hasModeAugment;

	int compareTracker;
	boolean cached = false;
	IEnergyReceiver adjacentReceiver = null;
	boolean adjacentHandler = false;

	EnergyStorage energyStorage;
	EnergyConfig energyConfig;
	TimeTracker tracker = new TimeTracker();

	/* AUGMENTS */
	protected boolean augmentCoilDuct;
	protected boolean augmentThrottle;

	protected boolean augmentBoiler;

	protected int renderCoil = 0;
	protected int lastEnergy;

	int energyMod = ENERGY_BASE;

	public TileDynamoBase() {

		energyConfig = ENERGY_CONFIGS[this.getType()].copy();
		energyStorage = new EnergyStorage(energyConfig.maxEnergy, energyConfig.maxPower * 2);
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

		return "tile.thermalexpansion.dynamo." + Type.values()[getType()].getName() + ".name";
	}

	@Override
	protected int getLevelRSControl() {

		return TEProps.levelRedstoneControl;
	}

	@Override
	public int getComparatorInputOverride() {

		return compareTracker;
	}

	@Override
	public int getLightValue() {

		return isActive ? COIL_LIGHT[getCoil()] : 0;
	}

	@Override
	public boolean enableSecurity() {

		return enableSecurity;
	}

	/* IUpgradeable */
	@Override
	public boolean canUpgrade(ItemStack upgrade) {

		if (!AugmentHelper.isUpgradeItem(upgrade)) {
			return false;
		}
		UpgradeType uType = ((IUpgradeItem) upgrade.getItem()).getUpgradeType(upgrade);
		int uLevel = ((IUpgradeItem) upgrade.getItem()).getUpgradeLevel(upgrade);

		switch (uType) {
			case INCREMENTAL:
				if (uLevel == level + 1) {
					return !BlockDynamo.enableClassicRecipes;
				}
				break;
			case FULL:
				if (uLevel > level) {
					return !BlockDynamo.enableClassicRecipes;
				}
				break;
			case CREATIVE:
				return !isCreative && enableCreative;
		}
		return false;
	}

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
	public boolean onWrench(EntityPlayer player, EnumFacing side) {

		return rotateBlock();
	}

	@Override
	public void invalidate() {

		cached = false;
		super.invalidate();
	}

	@Override
	public void onNeighborBlockChange() {

		super.onNeighborBlockChange();
		updateAdjacentHandlers();
	}

	@Override
	public void onNeighborTileChange(BlockPos pos) {

		super.onNeighborTileChange(pos);
		updateAdjacentHandlers();
	}

	public final void setEnergyStored(int quantity) {

		energyStorage.setEnergyStored(quantity);
	}

	@Override
	public void update() {

		boolean curActive = isActive;

		if (isActive) {
			processTick();

			if (canFinish()) {
				if (!redstoneControlOrDisable() || !canStart()) {
					processOff();
				} else {
					processStart();
				}
			}
		} else if (redstoneControlOrDisable()) {
			if (timeCheck()) {
				if (canStart()) {
					processStart();
					processTick();
					isActive = true;
				}
			}
		}
		if (timeCheck()) {
			int curScale = energyStorage.getEnergyStored() > 0 ? 1 + getScaledEnergyStored(14) : 0;
			if (curScale != compareTracker) {
				compareTracker = curScale;
				callNeighborTileChange();
			}
			if (!cached) {
				updateAdjacentHandlers();
			}
			if (!isActive) {
				processIdle();
			}
		}
		updateIfChanged(curActive);
	}

	/* COMMON METHODS */
	protected int getBasePower(int level) {

		return ENERGY_CONFIGS[getType()].maxPower * POWER_SCALING[MathHelper.clamp(level, CoreProps.LEVEL_MIN, CoreProps.LEVEL_MAX)] / POWER_BASE;
	}

	protected int calcEnergy() {

		if (energyStorage.getEnergyStored() <= energyConfig.minPowerLevel) {
			return energyConfig.maxPower;
		}
		if (energyStorage.getEnergyStored() > energyConfig.maxPowerLevel) {
			return energyConfig.minPower;
		}
		return (energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored()) / energyConfig.energyRamp;
	}

	protected int getScaledEnergyStored(int scale) {

		return energyStorage.getEnergyStored() * scale / energyStorage.getMaxEnergyStored();
	}

	protected abstract boolean canStart();

	protected boolean canFinish() {

		return fuelRF <= 0;
	}

	protected abstract void processStart();

	protected void processFinish() {

	}

	protected void processIdle() {

	}

	protected void processOff() {

		isActive = false;
		wasActive = true;
		tracker.markTime(world);
	}

	protected int processTick() {

		lastEnergy = calcEnergy();
		energyStorage.modifyEnergyStored(lastEnergy);
		fuelRF -= lastEnergy;
		transferEnergy();

		return lastEnergy;
	}

	protected void transferSteam() {

		FluidHelper.insertFluidIntoAdjacentFluidHandler(world, pos, EnumFacing.values()[facing], new FluidStack(TFFluids.fluidSteam, energyConfig.maxPower), true);
	}

	protected void transferEnergy() {

		if (adjacentReceiver == null) {
			if (adjacentHandler) {
				energyStorage.modifyEnergyStored(-EnergyHelper.insertEnergyIntoAdjacentEnergyReceiver(this, EnumFacing.VALUES[facing], Math.min(energyStorage.getMaxExtract(), energyStorage.getEnergyStored()), false));
			}
			return;
		}
		energyStorage.modifyEnergyStored(-adjacentReceiver.receiveEnergy(EnumFacing.VALUES[facing ^ 1], Math.min(energyStorage.getMaxExtract(), energyStorage.getEnergyStored()), false));
	}

	protected void updateAdjacentHandlers() {

		if (ServerHelper.isClientWorld(world)) {
			return;
		}
		TileEntity tile = BlockHelper.getAdjacentTileEntity(this, facing);

		if (EnergyHelper.isEnergyReceiverFromSide(tile, EnumFacing.VALUES[facing ^ 1])) {
			adjacentReceiver = (IEnergyReceiver) tile;
			adjacentHandler = false;
		} else if (EnergyHelper.isEnergyHandler(tile, EnumFacing.VALUES[facing ^ 1])) {
			adjacentReceiver = null;
			adjacentHandler = true;
		} else {
			adjacentReceiver = null;
			adjacentHandler = false;
		}
		cached = true;
	}

	protected void updateIfChanged(boolean curActive) {

		if (curActive != isActive && !wasActive) {
			updateLighting();
			sendTilePacket(Side.CLIENT);
		} else if (wasActive && tracker.hasDelayPassed(world, CoreProps.tileUpdateDelay)) {
			wasActive = false;
			updateLighting();
			sendTilePacket(Side.CLIENT);
		}
	}

	public int getCoil() {

		return augmentBoiler ? 1 : 0;
	}

	@SideOnly (Side.CLIENT)
	public TextureAtlasSprite getCoilUnderlayTexture() {

		return TextureHelper.getTexture(TFFluids.fluidSteam.getStill());
	}

	@SideOnly (Side.CLIENT)
	public TextureAtlasSprite getBaseUnderlayTexture() {

		return TextureHelper.getTexture(FluidRegistry.WATER.getStill());
	}

	/* GUI METHODS */
	public int getScaledDuration(int scale) {

		return 0;
	}

	public IEnergyStorage getEnergyStorage() {

		return energyStorage;
	}

	public FluidTankCore getTank(int tankIndex) {

		return null;
	}

	public boolean showEnergyTab() {

		return !augmentBoiler;
	}

	public boolean showSteamTab() {

		return augmentBoiler;
	}

	public boolean isSteamProducer() {

		return augmentBoiler;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		energyStorage.readFromNBT(nbt);

		facing = (byte) (nbt.getByte("Facing") % 6);
		fuelRF = nbt.getInteger("Fuel");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		energyStorage.writeToNBT(nbt);

		nbt.setByte("Facing", facing);
		nbt.setInteger("Fuel", fuelRF);
		return nbt;
	}

	/* NETWORK METHODS */

	/* SERVER -> CLIENT */
	@Override
	public PacketBase getGuiPacket() {

		PacketBase payload = super.getGuiPacket();

		payload.addInt(energyStorage.getMaxEnergyStored());
		payload.addInt(energyStorage.getEnergyStored());
		payload.addInt(fuelRF);
		payload.addInt(lastEnergy);

		payload.addBool(augmentBoiler);

		return payload;
	}

	@Override
	public PacketBase getTilePacket() {

		PacketBase payload = super.getTilePacket();

		payload.addByte(facing);
		payload.addBool(hasRedstoneControl);

		payload.addBool(augmentBoiler);

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketBase payload) {

		super.handleGuiPacket(payload);

		energyStorage.setCapacity(payload.getInt());
		energyStorage.setEnergyStored(payload.getInt());
		fuelRF = payload.getInt();
		lastEnergy = payload.getInt();

		augmentBoiler = payload.getBool();
	}

	@Override
	@SideOnly (Side.CLIENT)
	public void handleTilePacket(PacketBase payload) {

		super.handleTilePacket(payload);

		facing = payload.getByte();
		hasRedstoneControl = payload.getBool();

		augmentBoiler = payload.getBool();
	}

	/* HELPERS */
	protected void preAugmentInstall() {

		renderCoil = getCoil();
		energyConfig.setDefaultParams(getBasePower(this.level), smallStorage);

		energyMod = ENERGY_BASE;
		hasModeAugment = false;

		augmentThrottle = false;
		augmentCoilDuct = false;

		augmentBoiler = false;

		renderCoil = getCoil();
	}

	@Override
	protected void postAugmentInstall() {

		if (augmentThrottle) {
			energyConfig.minPower = 0;
		}
		energyStorage.setCapacity(energyConfig.maxEnergy).setMaxTransfer(energyConfig.maxPower * 4);

		if (world != null && ServerHelper.isServerWorld(world) && renderCoil != getCoil()) {
			sendTilePacket(Side.CLIENT);
		}
	}

	@Override
	protected boolean isValidAugment(AugmentType type, String id) {

		if (type == AugmentType.CREATIVE && !isCreative) {
			return false;
		}
		if (type == AugmentType.MODE && hasModeAugment) {
			return false;
		}
		if (augmentCoilDuct && TEProps.DYNAMO_COIL_DUCT.equals(id)) {
			return false;
		}
		if (augmentThrottle && TEProps.DYNAMO_THROTTLE.equals(id)) {
			return false;
		}
		return VALID_AUGMENTS_BASE.contains(id) || VALID_AUGMENTS[getType()].contains(id) || super.isValidAugment(type, id);
	}

	@Override
	protected boolean installAugmentToSlot(int slot) {

		String id = AugmentHelper.getAugmentIdentifier(augments[slot]);

		if (TEProps.DYNAMO_POWER.equals(id)) {
			// Power Boost
			energyConfig.setDefaultParams(energyConfig.maxPower + getBasePower(this.level), smallStorage);
			return true;
		}
		if (TEProps.DYNAMO_EFFICIENCY.equals(id)) {
			// Efficiency Gain
			energyMod += 15;
			return true;
		}
		if (!augmentCoilDuct && TEProps.DYNAMO_COIL_DUCT.equals(id)) {
			augmentCoilDuct = true;
			return true;
		}
		if (!augmentThrottle && TEProps.DYNAMO_THROTTLE.equals(id)) {
			augmentThrottle = true;
			return true;
		}
		return super.installAugmentToSlot(slot);
	}

	/* IAccelerable */
	@Override
	public int updateAccelerable() {

		if (!isActive || canFinish()) {
			return 0;
		}
		return processTick();
	}

	/* IEnergyProvider */
	@Override
	public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {

		return from.ordinal() != facing ? 0 : energyStorage.extractEnergy(Math.min(energyConfig.maxPower * 2, maxExtract), simulate);
	}

	@Override
	public int getEnergyStored(EnumFacing from) {

		return energyStorage.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {

		return energyStorage.getMaxEnergyStored();
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {

		return true;
	}

	/* IEnergyInfo */
	@Override
	public int getInfoEnergyPerTick() {

		if (!isActive) {
			return 0;
		}
		return lastEnergy;
	}

	@Override
	public int getInfoMaxEnergyPerTick() {

		return energyConfig.maxPower;
	}

	@Override
	public int getInfoEnergyStored() {

		return energyStorage.getEnergyStored();
	}

	/* ISteamInfo */
	@Override
	public int getInfoSteamPerTick() {

		if (!isActive) {
			return 0;
		}
		return augmentBoiler ? energyConfig.maxPower : lastEnergy;
	}

	@Override
	public int getInfoMaxSteamPerTick() {

		return energyConfig.maxPower;
	}

	/* IPortableData */
	@Override
	public String getDataType() {

		return "tile.thermalexpansion.dynamo";
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

		if (world.getEntitiesWithinAABB(Entity.class, getBlockType().getBoundingBox(world.getBlockState(pos), world, pos)).size() != 0) {
			return false;
		}
		facing++;
		facing %= 6;
		updateAdjacentHandlers();
		markChunkDirty();
		sendTilePacket(Side.CLIENT);
		return true;
	}

	@Override
	public boolean setFacing(int side, boolean alternate) {

		if (alternate) {
			facing = (byte) (side ^ 1);
			markChunkDirty();
			sendTilePacket(Side.CLIENT);
		}
		return true;
	}

	/* ISidedInventory */
	@Override
	public int[] getSlotsForFace(EnumFacing side) {

		return CoreProps.EMPTY_INVENTORY;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, EnumFacing side) {

		return (augmentCoilDuct || side.ordinal() != facing) && isItemValidForSlot(slot, stack);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, EnumFacing side) {

		return augmentCoilDuct || side.ordinal() != facing;
	}

	/* CAPABILITIES */
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing from) {

		return super.hasCapability(capability, from) || capability == CapabilityEnergy.ENERGY;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, final EnumFacing from) {

		if (capability == CapabilityEnergy.ENERGY) {
			return CapabilityEnergy.ENERGY.cast(new net.minecraftforge.energy.IEnergyStorage() {

				@Override
				public int receiveEnergy(int maxReceive, boolean simulate) {

					return 0;
				}

				@Override
				public int extractEnergy(int maxExtract, boolean simulate) {

					return TileDynamoBase.this.extractEnergy(from, maxExtract, simulate);
				}

				@Override
				public int getEnergyStored() {

					return TileDynamoBase.this.getEnergyStored(from);
				}

				@Override
				public int getMaxEnergyStored() {

					return TileDynamoBase.this.getMaxEnergyStored(from);
				}

				@Override
				public boolean canExtract() {

					return true;
				}

				@Override
				public boolean canReceive() {

					return false;
				}
			});
		}
		return super.getCapability(capability, from);
	}

}
