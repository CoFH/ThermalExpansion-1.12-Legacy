package cofh.thermalexpansion.block;

import cofh.api.item.IAugmentItem;
import cofh.api.tileentity.IAugmentable;
import cofh.api.tileentity.IEnergyInfo;
import cofh.core.network.PacketCoFHBase;
import cofh.core.util.fluid.FluidTankAdv;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.RedstoneControlHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.item.TEAugments;
import cofh.thermalexpansion.util.ReconfigurableHelper;
import cofh.thermalexpansion.util.Utils;
import cpw.mods.fml.relauncher.Side;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fluids.FluidStack;

public abstract class TileAugmentable extends TileReconfigurable implements IAugmentable, IEnergyInfo, ISidedInventory {

	protected SideConfig sideConfig;

	/* Augment Variables */
	protected boolean[] augmentStatus = new boolean[3];
	protected ItemStack[] augments = new ItemStack[3];

	public boolean augmentAutoTransfer;
	public boolean augmentReconfigSides;
	public boolean augmentRedstoneControl;

	@Override
	public byte[] getDefaultSides() {

		return sideConfig.defaultSides.clone();
	}

	public boolean isItemValid(ItemStack stack, int slot, int side) {

		return true;
	}

	/* GUI METHODS */
	public int getScaledProgress(int scale) {

		return 0;
	}

	public int getScaledSpeed(int scale) {

		return 0;
	}

	public FluidTankAdv getTank() {

		return null;
	}

	public FluidStack getTankFluid() {

		return null;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		readAugmentsFromNBT(nbt);
		installAugments();
		energyStorage.readFromNBT(nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		writeAugmentsToNBT(nbt);
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

		payload.addBool(augmentReconfigSides);
		payload.addBool(augmentRedstoneControl);

		return payload;
	}

	@Override
	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = super.getGuiPacket();

		payload.addBool(isActive);
		payload.addInt(energyStorage.getMaxEnergyStored());
		payload.addInt(energyStorage.getEnergyStored());

		payload.addBool(augmentReconfigSides);
		payload.addBool(augmentRedstoneControl);

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketCoFHBase payload) {

		isActive = payload.getBool();
		energyStorage.setCapacity(payload.getInt());
		energyStorage.setEnergyStored(payload.getInt());

		boolean prevReconfig = augmentReconfigSides;
		boolean prevControl = augmentRedstoneControl;
		augmentReconfigSides = payload.getBool();
		augmentRedstoneControl = payload.getBool();

		if (augmentReconfigSides != prevReconfig || augmentRedstoneControl != prevControl) {
			onInstalled();
		}
	}

	/* ITilePacketHandler */
	@Override
	public void handleTilePacket(PacketCoFHBase payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		if (!isServer) {
			augmentReconfigSides = payload.getBool();
			augmentRedstoneControl = payload.getBool();
		} else {
			payload.getBool();
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
			onInstalled();
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

		IAugmentItem augmentItem = (IAugmentItem) augments[slot].getItem();
		boolean installed = false;

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

	protected void onInstalled() {

		if (!augmentReconfigSides) {
			setDefaultSides();
			sideCache[facing] = 0;
		}
		if (!augmentRedstoneControl) {
			this.rsMode = ControlMode.DISABLED;
		}
	}

	protected void resetAugments() {

		augmentAutoTransfer = false;
		augmentReconfigSides = false;
		augmentRedstoneControl = false;
	}

	/* IEnergyInfo */
	@Override
	public int getInfoEnergyPerTick() {

		return 0;
	}

	@Override
	public int getInfoMaxEnergyPerTick() {

		return 0;
	}

	@Override
	public int getInfoEnergyStored() {

		return energyStorage.getEnergyStored();
	}

	@Override
	public int getInfoMaxEnergyStored() {

		return energyStorage.getMaxEnergyStored();
	}

	/* IPortableData */
	@Override
	public void readPortableData(EntityPlayer player, NBTTagCompound tag) {

		if (!canPlayerAccess(player.getCommandSenderName())) {
			return;
		}
		if (augmentRedstoneControl) {
			rsMode = RedstoneControlHelper.getControlFromNBT(tag);
		}
		if (augmentReconfigSides) {
			int storedFacing = ReconfigurableHelper.getFacingFromNBT(tag);
			byte[] storedSideCache = ReconfigurableHelper.getSideCacheFromNBT(tag, getDefaultSides());

			sideCache[0] = storedSideCache[0];
			sideCache[1] = storedSideCache[1];
			sideCache[facing] = storedSideCache[storedFacing];
			sideCache[BlockHelper.getLeftSide(facing)] = storedSideCache[BlockHelper.getLeftSide(storedFacing)];
			sideCache[BlockHelper.getRightSide(facing)] = storedSideCache[BlockHelper.getRightSide(storedFacing)];
			sideCache[BlockHelper.getOppositeSide(facing)] = storedSideCache[BlockHelper.getOppositeSide(storedFacing)];

			for (int i = 0; i < 6; i++) {
				if (sideCache[i] >= getNumConfig(i)) {
					sideCache[i] = 0;
				}
			}
			markDirty();
			sendUpdatePacket(Side.CLIENT);
		}
	}

	@Override
	public void writePortableData(EntityPlayer player, NBTTagCompound tag) {

		if (!canPlayerAccess(player.getCommandSenderName())) {
			return;
		}
		RedstoneControlHelper.setItemStackTagRS(tag, this);
		ReconfigurableHelper.setItemStackTagReconfig(tag, this);
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

}
