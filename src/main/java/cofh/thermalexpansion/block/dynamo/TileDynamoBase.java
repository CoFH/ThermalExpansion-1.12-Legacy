package cofh.thermalexpansion.block.dynamo;

import codechicken.lib.texture.TextureUtils;
import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import cofh.api.energy.IEnergyStorage;
import cofh.api.item.IAugmentItem;
import cofh.api.tileentity.IEnergyInfo;
import cofh.api.tileentity.IReconfigurableFacing;
import cofh.core.init.CoreProps;
import cofh.core.network.PacketCoFHBase;
import cofh.core.fluid.FluidTankCore;
import cofh.lib.util.TimeTracker;
import cofh.lib.util.helpers.*;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.TileInventory;
import cofh.thermalexpansion.util.Utils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.relauncher.Side;

public abstract class TileDynamoBase extends TileInventory implements ITickable, IEnergyProvider, IEnergyInfo, IReconfigurableFacing, ISidedInventory {

	protected static final EnergyConfig[] defaultEnergyConfig = new EnergyConfig[BlockDynamo.Type.values().length];
	protected static final int[] SLOTS = { 0 };
	protected static final int FUEL_MOD = 100;
	private static boolean enableSecurity = true;

	public static void config() {

		String comment = "Enable this to allow for Dynamos to be securable.";
		enableSecurity = ThermalExpansion.CONFIG.get("Security", "Dynamo.All.Securable", enableSecurity, comment);

		for (int i = 0; i < BlockDynamo.Type.values().length; i++) {
			String name = StringHelper.titleCase(BlockDynamo.Type.values()[i].getName());

			int maxPower = MathHelper.clamp(ThermalExpansion.CONFIG.get("Dynamo." + name, "BasePower", 80), 10, 160);
			ThermalExpansion.CONFIG.set("Dynamo." + name, "BasePower", maxPower);

			maxPower /= 10;
			maxPower *= 10;

			defaultEnergyConfig[i] = new EnergyConfig();
			defaultEnergyConfig[i].setDefaultParams(maxPower);
		}
	}

	int compareTracker;
	int fuelRF;
	byte facing = 1;
	boolean wasActive;

	boolean cached = false;
	IEnergyReceiver adjacentReceiver = null;

	protected EnergyStorage energyStorage = new EnergyStorage(0);
	protected EnergyConfig config;
	protected TimeTracker tracker = new TimeTracker();

	/* AUGMENTS */
	public boolean augmentThrottle;
	public boolean augmentCoilDuct;

	int energyMod = 1;
	int fuelMod = FUEL_MOD;

	public TileDynamoBase() {

		config = defaultEnergyConfig[getType()];
		energyStorage = new EnergyStorage(config.maxEnergy, config.maxPower * 2);
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
			if (redstoneControlOrDisable() && canGenerate()) {
				generate();
				transferEnergy(facing);
			} else {
				isActive = false;
				wasActive = true;
				tracker.markTime(worldObj);
			}
		} else if (redstoneControlOrDisable() && canGenerate()) {
			isActive = true;
			generate();
			transferEnergy(facing);
		} else {
			attenuate();
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

	@Override
	public void invalidate() {

		cached = false;
		super.invalidate();
	}

	protected abstract boolean canGenerate();

	protected abstract void generate();

	protected int calcEnergy() {

		if (!isActive) {
			return 0;
		}
		if (augmentThrottle) {
			return calcEnergyAugment();
		}
		if (energyStorage.getEnergyStored() < config.minPowerLevel) {
			return config.maxPower;
		}
		if (energyStorage.getEnergyStored() > config.maxPowerLevel) {
			return config.minPower;
		}
		return (energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored()) / config.energyRamp;
	}

	protected int calcEnergyAugment() {

		if (energyStorage.getEnergyStored() >= energyStorage.getMaxEnergyStored()) {
			return 0;
		}
		if (energyStorage.getEnergyStored() < config.minPowerLevel) {
			return config.maxPower;
		}
		if (energyStorage.getEnergyStored() >= energyStorage.getMaxEnergyStored() - config.energyRamp) {
			return 1;
		}
		return (energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored()) / config.energyRamp;
	}

	protected boolean hasStoredEnergy() {

		return energyStorage.getEnergyStored() > 0;
	}

	protected void attenuate() {

		if (timeCheck() && fuelRF > 0) {
			fuelRF -= 10;

			if (fuelRF < 0) {
				fuelRF = 0;
			}
		}
	}

	protected void transferEnergy(int bSide) {

		if (adjacentReceiver == null) {
			return;
		}
		energyStorage.modifyEnergyStored(-adjacentReceiver.receiveEnergy(EnumFacing.VALUES[bSide ^ 1], Math.min(energyStorage.getMaxExtract(), energyStorage.getEnergyStored()), false));
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

	public TextureAtlasSprite getActiveIcon() {

		return TextureUtils.getTexture(FluidRegistry.WATER.getStill());
	}

	/* GUI METHODS */
	public IEnergyStorage getEnergyStorage() {

		return energyStorage;
	}

	public int getScaledEnergyStored(int scale) {

		return energyStorage.getEnergyStored() * scale / energyStorage.getMaxEnergyStored();
	}

	public FluidTankCore getTank(int tankIndex) {

		return null;
	}

	public int getScaledDuration(int scale) {

		return 0;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		readAugmentsFromNBT(nbt);
		installAugments();
		energyStorage.readFromNBT(nbt);

		facing = (byte) (nbt.getByte("Facing") % 6);
		isActive = nbt.getBoolean("Active");
		fuelRF = nbt.getInteger("Fuel");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		writeAugmentsToNBT(nbt);
		energyStorage.writeToNBT(nbt);

		nbt.setByte("Facing", facing);
		nbt.setBoolean("Active", isActive);
		nbt.setInteger("Fuel", fuelRF);
		return nbt;
	}

	public void readAugmentsFromNBT(NBTTagCompound nbt) {

		NBTTagList list = nbt.getTagList("Augments", 10);

		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound tag = list.getCompoundTagAt(i);
			int slot = tag.getInteger("Slot");
			if (slot >= 0 && slot < augments.length) {
				augments[slot] = ItemStack.loadItemStackFromNBT(tag);
			}
		}
	}

	public void writeAugmentsToNBT(NBTTagCompound nbt) {

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

		payload.addByte(facing);
		payload.addBool(hasRedstoneControl);

		return payload;
	}

	@Override
	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = super.getGuiPacket();

		payload.addInt(energyStorage.getMaxEnergyStored());
		payload.addInt(energyStorage.getEnergyStored());
		payload.addInt(fuelRF);

		payload.addBool(hasRedstoneControl);

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketCoFHBase payload) {

		super.handleGuiPacket(payload);

		energyStorage.setCapacity(payload.getInt());
		energyStorage.setEnergyStored(payload.getInt());
		fuelRF = payload.getInt();

		boolean prevControl = hasRedstoneControl;
		hasRedstoneControl = payload.getBool();

		if (hasRedstoneControl != prevControl) {
			onAugmentInstalled();
			sendUpdatePacket(Side.SERVER);
		}
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
		if (worldObj != null && ServerHelper.isServerWorld(worldObj)) {
			onAugmentInstalled();
			sendUpdatePacket(Side.CLIENT);
		}
	}

	/* AUGMENT HELPERS */
	protected boolean hasAugment(String type, int augLevel) {

		for (int i = 0; i < augments.length; i++) {
			if (Utils.isAugmentItem(augments[i]) && ((IAugmentItem) augments[i].getItem()).getAugmentLevel(augments[i], type) == augLevel) {
				return true;
			}
		}
		return false;
	}

	protected boolean hasDuplicateAugment(String type, int augLevel, int slot) {

		for (int i = 0; i < augments.length; i++) {
			if (i != slot && Utils.isAugmentItem(augments[i]) && ((IAugmentItem) augments[i].getItem()).getAugmentLevel(augments[i], type) == augLevel) {
				return true;
			}
		}
		return false;
	}

	protected boolean hasAugmentChain(String type, int augLevel) {

		boolean preReq = true;
		for (int i = 1; i < augLevel; i++) {
			preReq = preReq && hasAugment(type, i);
		}
		return preReq;
	}

	protected boolean installAugment(int slot) {

		//		IAugmentItem augmentItem = (IAugmentItem) augments[slot].getItem();
		//		boolean installed = false;
		//
		//		if (augmentItem.getAugmentLevel(augments[slot], TEAugments.DYNAMO_EFFICIENCY) > 0) {
		//			if (augmentItem.getAugmentLevel(augments[slot], TEAugments.DYNAMO_OUTPUT) > 0) {
		//				return false;
		//			}
		//			int augLevel = Math.min(TEAugments.NUM_DYNAMO_EFFICIENCY, augmentItem.getAugmentLevel(augments[slot], TEAugments.DYNAMO_EFFICIENCY));
		//			if (hasDuplicateAugment(TEAugments.DYNAMO_EFFICIENCY, augLevel, slot)) {
		//				return false;
		//			}
		//			if (hasAugmentChain(TEAugments.DYNAMO_EFFICIENCY, augLevel)) {
		//				fuelMod += TEAugments.DYNAMO_EFFICIENCY_MOD[augLevel];
		//				installed = true;
		//			} else {
		//				return false;
		//			}
		//		}
		//		if (augmentItem.getAugmentLevel(augments[slot], TEAugments.DYNAMO_OUTPUT) > 0) {
		//			int augLevel = augmentItem.getAugmentLevel(augments[slot], TEAugments.DYNAMO_OUTPUT);
		//			if (hasDuplicateAugment(TEAugments.DYNAMO_OUTPUT, augLevel, slot)) {
		//				return false;
		//			}
		//			if (hasAugmentChain(TEAugments.DYNAMO_OUTPUT, augLevel)) {
		//				energyMod = Math.max(energyMod, TEAugments.DYNAMO_OUTPUT_MOD[augLevel]);
		//				energyStorage.setMaxTransfer(Math.max(energyStorage.getMaxExtract(), config.maxPower * 2 * TEAugments.DYNAMO_OUTPUT_MOD[augLevel]));
		//				fuelMod -= TEAugments.DYNAMO_OUTPUT_EFFICIENCY_MOD[augLevel];
		//				installed = true;
		//			} else {
		//				return false;
		//			}
		//		}
		//		if (augmentItem.getAugmentLevel(augments[slot], TEAugments.DYNAMO_COIL_DUCT) > 0) {
		//			augmentCoilDuct = true;
		//			installed = true;
		//		}
		//		if (augmentItem.getAugmentLevel(augments[slot], TEAugments.DYNAMO_THROTTLE) > 0) {
		//			if (hasAugment(TEAugments.GENERAL_REDSTONE_CONTROL, 0)) {
		//				augmentThrottle = true;
		//				installed = true;
		//			} else {
		//				return false;
		//			}
		//		}
		//		if (augmentItem.getAugmentLevel(augments[slot], TEAugments.ENDER_ENERGY) > 0) {
		//
		//		}
		//		if (augmentItem.getAugmentLevel(augments[slot], TEAugments.GENERAL_REDSTONE_CONTROL) > 0) {
		//			hasRedstoneControl = true;
		//			installed = true;
		//		}
		//		return installed;
		return true;
	}

	protected void resetAugments() {

		energyMod = 1;
		fuelMod = FUEL_MOD;
		energyStorage.setMaxTransfer(config.maxPower * 2);

		hasRedstoneControl = false;
		augmentThrottle = false;
		augmentCoilDuct = false;
	}

	/* IEnergyProvider */
	@Override
	public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {

		return from.ordinal() != facing ? 0 : energyStorage.extractEnergy(Math.min(config.maxPower * 2, maxExtract), simulate);
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

		return calcEnergy() * energyMod;
	}

	@Override
	public int getInfoMaxEnergyPerTick() {

		return config.maxPower * energyMod;
	}

	@Override
	public int getInfoEnergyStored() {

		return energyStorage.getEnergyStored();
	}

	@Override
	public int getInfoMaxEnergyStored() {

		return config.maxEnergy;
	}

	/* IFluidHandler */
	public boolean canFill(EnumFacing from, Fluid fluid) {

		return augmentCoilDuct || from.ordinal() != facing;
	}

	public boolean canDrain(EnumFacing from, Fluid fluid) {

		return augmentCoilDuct || from.ordinal() != facing;
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
