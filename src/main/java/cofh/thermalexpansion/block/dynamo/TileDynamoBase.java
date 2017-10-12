package cofh.thermalexpansion.block.dynamo;

import cofh.api.core.IAccelerable;
import cofh.api.item.IAugmentItem.AugmentType;
import cofh.api.tileentity.IEnergyInfo;
import cofh.api.tileentity.IReconfigurableFacing;
import cofh.api.tileentity.ISteamInfo;
import cofh.core.fluid.FluidTankCore;
import cofh.core.init.CoreProps;
import cofh.core.network.PacketCoFHBase;
import cofh.core.render.TextureHelper;
import cofh.core.util.TimeTracker;
import cofh.core.util.helpers.*;
import cofh.redstoneflux.api.IEnergyProvider;
import cofh.redstoneflux.api.IEnergyReceiver;
import cofh.redstoneflux.api.IEnergyStorage;
import cofh.redstoneflux.impl.EnergyStorage;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.TileInventory;
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

	protected static final EnergyConfig[] DEFAULT_ENERGY_CONFIG = new EnergyConfig[Type.values().length];
	protected static final HashSet<String>[] VALID_AUGMENTS = new HashSet[Type.values().length];

	public static final int MIN_BASE_POWER = 10;
	public static final int MAX_BASE_POWER = 200;

	protected static boolean enableSecurity = true;
	protected static boolean smallStorage = false;

	protected static final HashSet<String> VALID_AUGMENTS_BASE = new HashSet<>();
	protected static final int ENERGY_BASE = 100;

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

		comment = "If TRUE, 'Classic' Crafting is enabled - Non-Creative Upgrade Kits WILL NOT WORK.";
		BlockDynamo.enableClassicRecipes = ThermalExpansion.CONFIG.get(category, "ClassicCrafting", BlockDynamo.enableClassicRecipes, comment);

		comment = "If TRUE, Dynamos can be upgraded in a Crafting Table using Kits. If Classic Crafting is enabled, only the Creative Conversion Kit may be used in this fashion.";
		BlockDynamo.enableUpgradeKitCrafting = ThermalExpansion.CONFIG.get(category, "UpgradeKitCrafting", BlockDynamo.enableUpgradeKitCrafting, comment);

		comment = "If TRUE, Dynamos will have much smaller internal energy (RF) storage. Generation speed will no longer scale with internal energy.";
		smallStorage = ThermalExpansion.CONFIG.get(category, "SmallStorage", smallStorage, comment);
	}

	byte facing = 1;
	int fuelRF;
	int currentFuelRF;
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

		energyConfig = DEFAULT_ENERGY_CONFIG[this.getType()].copy();
		energyStorage = new EnergyStorage(energyConfig.maxEnergy, energyConfig.maxPower * 2);
	}

	@Override
	public String getTileName() {

		return "tile.thermalexpansion.dynamo." + Type.values()[getType()].getName() + ".name";
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
	public void blockPlaced() {

		super.blockPlaced();

		byte oldFacing = facing;
		for (int i = facing + 1, e = facing + 6; i < e; i++) {
			if (EnergyHelper.isAdjacentEnergyReceiverFromSide(this, EnumFacing.VALUES[i % 6]) || (EnergyHelper.isAdjacentEnergyHandler(this, EnumFacing.VALUES[i % 6]) && EnergyHelper.canAdjacentEnergyHandlerReceive(this, EnumFacing.VALUES[i % 6]))) {
				facing = (byte) (i % 6);
				if (facing != oldFacing) {
					updateAdjacentHandlers();
					markChunkDirty();
					sendTilePacket(Side.CLIENT);
				}
			}
		}
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

		if (ServerHelper.isClientWorld(world)) {
			return;
		}
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
				} else {
					processIdle();
				}
			}
		}
		if (timeCheck()) {
			int curScale = getScaledEnergyStored(15);
			if (curScale != compareTracker) {
				compareTracker = curScale;
				callNeighborTileChange();
			}
			if (!cached) {
				updateAdjacentHandlers();
			}
		}
		updateIfChanged(curActive);
	}

	/* COMMON METHODS */
	int getBasePower(int level) {

		return DEFAULT_ENERGY_CONFIG[getType()].maxPower + level * DEFAULT_ENERGY_CONFIG[getType()].maxPower / 2;
	}

	int calcEnergy() {

		if (energyStorage.getEnergyStored() <= energyConfig.minPowerLevel) {
			return energyConfig.maxPower;
		}
		if (energyStorage.getEnergyStored() > energyConfig.maxPowerLevel) {
			return energyConfig.minPower;
		}
		return (energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored()) / energyConfig.energyRamp;
	}

	int getScaledEnergyStored(int scale) {

		return energyStorage.getEnergyStored() * scale / energyStorage.getMaxEnergyStored();
	}

	abstract boolean canStart();

	boolean canFinish() {

		return fuelRF <= 0;
	}

	abstract void processStart();

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
		} else if (wasActive && tracker.hasDelayPassed(world, CoreProps.TILE_UPDATE_DELAY)) {
			wasActive = false;
			updateLighting();
			sendTilePacket(Side.CLIENT);
		}
	}

	public int getCoil() {

		return augmentBoiler ? 1 : 0;
	}

	public TextureAtlasSprite getCoilUnderlayTexture() {

		return TextureHelper.getTexture(TFFluids.fluidSteam.getStill());
	}

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
	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = super.getGuiPacket();

		payload.addInt(energyStorage.getMaxEnergyStored());
		payload.addInt(energyStorage.getEnergyStored());
		payload.addInt(fuelRF);
		payload.addInt(lastEnergy);

		payload.addBool(augmentBoiler);

		return payload;
	}

	@Override
	public PacketCoFHBase getTilePacket() {

		PacketCoFHBase payload = super.getTilePacket();

		payload.addByte(facing);
		payload.addBool(hasRedstoneControl);

		payload.addBool(augmentBoiler);

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketCoFHBase payload) {

		super.handleGuiPacket(payload);

		energyStorage.setCapacity(payload.getInt());
		energyStorage.setEnergyStored(payload.getInt());
		fuelRF = payload.getInt();
		lastEnergy = payload.getInt();

		augmentBoiler = payload.getBool();
	}

	@Override
	@SideOnly (Side.CLIENT)
	public void handleTilePacket(PacketCoFHBase payload) {

		super.handleTilePacket(payload);

		facing = payload.getByte();
		hasRedstoneControl = payload.getBool();

		augmentBoiler = payload.getBool();
	}

	/* HELPERS */
	protected void preAugmentInstall() {

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

			// Efficiency Loss
			energyMod -= 10;
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
	public boolean setFacing(int side) {

		return false;
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
