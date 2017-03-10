package cofh.thermalexpansion.block.dynamo;

import codechicken.lib.texture.TextureUtils;
import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import cofh.api.energy.IEnergyStorage;
import cofh.api.item.IAugmentItem.AugmentType;
import cofh.api.core.IAccelerable;
import cofh.api.tileentity.IEnergyInfo;
import cofh.api.tileentity.IReconfigurableFacing;
import cofh.core.fluid.FluidTankCore;
import cofh.core.init.CoreProps;
import cofh.core.network.PacketCoFHBase;
import cofh.lib.util.TimeTracker;
import cofh.core.util.helpers.AugmentHelper;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.EnergyHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.TileInventory;
import cofh.thermalexpansion.init.TEProps;
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
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;

public abstract class TileDynamoBase extends TileInventory implements ITickable, IAccelerable, IEnergyProvider, IEnergyInfo, IReconfigurableFacing, ISidedInventory {

	protected static final EnergyConfig[] defaultEnergyConfig = new EnergyConfig[BlockDynamo.Type.values().length];
	protected static final ArrayList<String>[] validAugments = new ArrayList[BlockDynamo.Type.values().length];
	private static boolean enableSecurity = true;

	protected static final ArrayList<String> VALID_AUGMENTS_BASE = new ArrayList<>();
	protected static final int ENERGY_BASE = 100;

	static {
		VALID_AUGMENTS_BASE.add(TEProps.DYNAMO_POWER);
		VALID_AUGMENTS_BASE.add(TEProps.DYNAMO_EFFICIENCY);
		VALID_AUGMENTS_BASE.add(TEProps.DYNAMO_COIL_DUCT);
		VALID_AUGMENTS_BASE.add(TEProps.DYNAMO_THROTTLE);
	}

	public static void config() {

		String comment = "Enable this to allow for Dynamos to be securable.";
		enableSecurity = ThermalExpansion.CONFIG.get("Security", "Dynamo.Securable", enableSecurity, comment);
	}

	byte facing = 1;
	int fuelRF;
	boolean wasActive;
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

	int energyMod = ENERGY_BASE;

	public TileDynamoBase() {

		energyConfig = defaultEnergyConfig[this.getType()].copy();
		energyStorage = new EnergyStorage(energyConfig.maxEnergy, energyConfig.maxPower * 2);
	}

	@Override
	public String getTileName() {

		return "tile.thermalexpansion.dynamo." + BlockDynamo.Type.values()[getType()].getName() + ".name";
	}

	@Override
	public int getComparatorInputOverride() {

		return compareTracker;
	}

	@Override
	public int getLightValue() {

		return isActive ? 7 : 0;
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
	public boolean onWrench(EntityPlayer player, EnumFacing side) {

		rotateBlock();
		return true;
	}

	@Override
	public void blockPlaced() {

		byte oldFacing = facing;
		for (int i = facing + 1, e = facing + 6; i < e; i++) {
			if (EnergyHelper.isAdjacentEnergyReceiverFromSide(this, EnumFacing.VALUES[i % 6]) || EnergyHelper.isAdjacentEnergyHandler(this, EnumFacing.VALUES[i % 6])) {
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

		if (ServerHelper.isClientWorld(worldObj)) {
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
				onNeighborBlockChange();
			}
		}
		updateIfChanged(curActive);
	}

	/* COMMON METHODS */
	int getBasePower(int level) {

		return defaultEnergyConfig[getType()].maxPower + level * defaultEnergyConfig[getType()].maxPower / 2;
	}

	int calcEnergy() {

		if (energyStorage.getEnergyStored() < energyConfig.minPowerLevel) {
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
		tracker.markTime(worldObj);
	}

	protected int processTick() {

		int energy = calcEnergy();
		energyStorage.modifyEnergyStored(energy);
		fuelRF -= energy;
		transferEnergy();

		return energy;
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

		if (ServerHelper.isClientWorld(worldObj)) {
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
		} else if (wasActive && tracker.hasDelayPassed(worldObj, 100)) {
			wasActive = false;
			updateLighting();
			sendTilePacket(Side.CLIENT);
		}
	}

	public TextureAtlasSprite getActiveIcon() {

		return TextureUtils.getTexture(FluidRegistry.WATER.getStill());
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

		return payload;
	}

	@Override
	public PacketCoFHBase getTilePacket() {

		PacketCoFHBase payload = super.getTilePacket();

		payload.addByte(facing);
		payload.addBool(hasRedstoneControl);

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketCoFHBase payload) {

		super.handleGuiPacket(payload);

		energyStorage.setCapacity(payload.getInt());
		energyStorage.setEnergyStored(payload.getInt());
		fuelRF = payload.getInt();
	}

	@Override
	public void handleTilePacket(PacketCoFHBase payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		facing = payload.getByte();
		hasRedstoneControl = payload.getBool();

	}

	/* HELPERS */
	protected void preAugmentInstall() {

		energyConfig.setDefaultParams(getBasePower(this.level));

		energyMod = ENERGY_BASE;
		hasModeAugment = false;

		augmentThrottle = false;
		augmentCoilDuct = false;
	}

	@Override
	protected void postAugmentInstall() {

		if (augmentThrottle) {
			energyConfig.minPower = 0;
		}
		energyStorage.setCapacity(energyConfig.maxEnergy).setMaxTransfer(energyConfig.maxPower * 4);
	}

	@Override
	protected boolean isValidAugment(AugmentType type, String id) {

		if (type == AugmentType.CREATIVE && level != -1) {
			return false;
		}
		if (type == AugmentType.MODE && hasModeAugment) {
			return false;
		}
		return VALID_AUGMENTS_BASE.contains(id) || validAugments[getType()].contains(id) || super.isValidAugment(type, id);
	}

	@Override
	protected boolean installAugmentToSlot(int slot) {

		String id = AugmentHelper.getAugmentIdentifier(augments[slot]);

		if (TEProps.DYNAMO_POWER.equals(id)) {
			// Power Boost
			energyConfig.setDefaultParams(energyConfig.maxPower + getBasePower(this.level));

			// Efficiency Loss
			energyMod -= 10;
			return true;
		}
		if (TEProps.DYNAMO_EFFICIENCY.equals(id)) {
			// Efficiency Gain
			energyMod += 10;
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

		return from.ordinal() == facing;
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

	@Override
	public int getInfoEnergyStored() {

		return energyStorage.getEnergyStored();
	}

	@Override
	public int getInfoMaxEnergyStored() {

		return energyConfig.maxEnergy;
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

		if (ServerHelper.isClientWorld(worldObj)) {
			return false;
		}
		if (worldObj.getEntitiesWithinAABB(Entity.class, getBlockType().getBoundingBox(worldObj.getBlockState(pos), worldObj, pos)).size() != 0) {
			return false;
		}
		if (adjacentReceiver != null || adjacentHandler) {
			byte oldFacing = facing;
			for (int i = facing + 1, e = facing + 6; i < e; i++) {
				if (EnergyHelper.isAdjacentEnergyReceiverFromSide(this, EnumFacing.VALUES[i % 6]) || EnergyHelper.isAdjacentEnergyHandler(this, EnumFacing.VALUES[i % 6])) {
					facing = (byte) (i % 6);
					if (facing != oldFacing) {
						updateAdjacentHandlers();
						markChunkDirty();
						sendTilePacket(Side.CLIENT);
					}
					return true;
				}
			}
			return false;
		}
		facing = (byte) ((facing + 1) % 6);
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
