package cofh.thermalexpansion.block.dynamo;

import codechicken.lib.texture.TextureUtils;
import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import cofh.api.energy.IEnergyStorage;
import cofh.api.tileentity.IEnergyInfo;
import cofh.api.tileentity.IReconfigurableFacing;
import cofh.core.fluid.FluidTankCore;
import cofh.core.init.CoreProps;
import cofh.core.network.PacketCoFHBase;
import cofh.lib.util.TimeTracker;
import cofh.lib.util.helpers.*;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.TileInventory;
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
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;

public abstract class TileDynamoBase extends TileInventory implements ITickable, IEnergyProvider, IEnergyInfo, IReconfigurableFacing, ISidedInventory {

	protected static final EnergyConfig[] defaultEnergyConfig = new EnergyConfig[BlockDynamo.Type.values().length];
	protected static final ArrayList<String>[] validAugments = new ArrayList[BlockDynamo.Type.values().length];
	private static boolean enableSecurity = true;

	protected static final int ENERGY_BASE = 100;

	public static void config() {

		String comment = "Enable this to allow for Dynamos to be securable.";
		enableSecurity = ThermalExpansion.CONFIG.get("Security", "Dynamo.All.Securable", enableSecurity, comment);

		for (int i = 0; i < BlockDynamo.Type.values().length; i++) {
			String name = StringHelper.titleCase(BlockDynamo.Type.values()[i].getName());

			int maxPower = MathHelper.clamp(ThermalExpansion.CONFIG.get("Dynamo." + name, "BasePower", 40), 10, 160);
			ThermalExpansion.CONFIG.set("Dynamo." + name, "BasePower", maxPower);

			maxPower /= 10;
			maxPower *= 10;

			defaultEnergyConfig[i] = new EnergyConfig();
			defaultEnergyConfig[i].setDefaultParams(maxPower);
		}
	}

	int processRem;
	byte facing = 1;
	boolean wasActive;
	boolean hasAdvancedAugment;

	int compareTracker;
	boolean cached = false;
	IEnergyReceiver adjacentReceiver = null;

	EnergyStorage energyStorage;
	EnergyConfig energyConfig;
	TimeTracker tracker = new TimeTracker();

	/* AUGMENTS */
	public boolean augmentCoilDuct;
	public boolean augmentThrottle;

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
	public boolean onWrench(EntityPlayer player, EnumFacing side) {

		rotateBlock();
		return true;
	}

	@Override
	public void blockPlaced() {

		byte oldFacing = facing;
		for (int i = facing + 1, e = facing + 6; i < e; i++) {
			if (EnergyHelper.isAdjacentEnergyReceiverFromSide(this, i % 6)) {
				facing = (byte) (i % 6);
				if (facing != oldFacing) {
					updateAdjacentHandlers();
					markDirty();
					sendUpdatePacket(Side.CLIENT);
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
		if (!cached) {
			onNeighborBlockChange();
		}
		boolean curActive = isActive;

		if (isActive) {
			processTick();

			if (processRem <= 0) {
				if (!redstoneControlOrDisable() || !canStart()) {
					processOff();
				} else {
					processStart();
				}
			}
		} else if (redstoneControlOrDisable()) {
			if (timeCheck() && canStart()) {
				processStart();
				processTick();
				isActive = true;
			} else {
				processIdle();
			}
		}
		if (timeCheck()) {
			int curScale = getScaledEnergyStored(15);
			if (curScale != compareTracker) {
				compareTracker = curScale;
				callNeighborTileChange();
			}
		}
		updateIfChanged(curActive);
	}

	/* COMMON METHODS */
	protected int getBasePower(int level) {

		return defaultEnergyConfig[getType()].maxPower + level * defaultEnergyConfig[getType()].maxPower / 2;
	}

	protected int calcEnergy() {

		if (energyStorage.getEnergyStored() < energyConfig.minPowerLevel) {
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

	protected boolean canStart() {

		return false;
	}

	protected void processStart() {

	}

	protected void processFinish() {

	}

	protected void processIdle() {

	}

	protected void processOff() {

		isActive = false;
		wasActive = true;
		tracker.markTime(worldObj);
	}

	protected void processTick() {

		if (processRem <= 0) {
			return;
		}
		int energy = calcEnergy();
		energyStorage.modifyEnergyStored(energy);
		processRem -= energy;
		transferEnergy();
	}

	protected void transferEnergy() {

		if (adjacentReceiver == null) {
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
		} else {
			adjacentReceiver = null;
		}
		cached = true;
	}

	protected void updateIfChanged(boolean curActive) {

		if (curActive != isActive && !wasActive) {
			updateLighting();
			sendUpdatePacket(Side.CLIENT);
		} else if (wasActive && tracker.hasDelayPassed(worldObj, 100)) {
			wasActive = false;
			updateLighting();
			sendUpdatePacket(Side.CLIENT);
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
		processRem = nbt.getInteger("Fuel");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		energyStorage.writeToNBT(nbt);

		nbt.setByte("Facing", facing);
		nbt.setInteger("Fuel", processRem);
		return nbt;
	}

	/* NETWORK METHODS */
	@Override
	public PacketCoFHBase getPacket() {

		PacketCoFHBase payload = super.getPacket();

		payload.addByte(facing);
		payload.addBool(hasRedstoneControl);

		return payload;
	}

	@Override
	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = super.getGuiPacket();

		payload.addInt(energyStorage.getMaxEnergyStored());
		payload.addInt(energyStorage.getEnergyStored());
		payload.addInt(processRem);

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketCoFHBase payload) {

		super.handleGuiPacket(payload);

		energyStorage.setCapacity(payload.getInt());
		energyStorage.setEnergyStored(payload.getInt());
		processRem = payload.getInt();
	}

	/* ITilePacketHandler */
	@Override
	public void handleTilePacket(PacketCoFHBase payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		if (!isServer) {
			facing = payload.getByte();
			hasRedstoneControl = payload.getBool();
		} else {
			payload.getByte();
			payload.getBool();
		}
	}

	/* HELPERS */
	protected void preAugmentInstall() {

		energyMod = ENERGY_BASE;
		energyStorage.setMaxTransfer(energyConfig.maxPower * 2);

		hasRedstoneControl = false;
		augmentThrottle = false;
		augmentCoilDuct = false;
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
		if (adjacentReceiver != null) {
			byte oldFacing = facing;
			for (int i = facing + 1, e = facing + 6; i < e; i++) {
				if (EnergyHelper.isAdjacentEnergyReceiverFromSide(this, i % 6)) {
					facing = (byte) (i % 6);
					if (facing != oldFacing) {
						updateAdjacentHandlers();
						markDirty();
						sendUpdatePacket(Side.CLIENT);
					}
					return true;
				}
			}
			return false;
		}
		facing = (byte) ((facing + 1) % 6);
		updateAdjacentHandlers();
		markDirty();
		sendUpdatePacket(Side.CLIENT);
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

}
