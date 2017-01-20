package cofh.thermalexpansion.block;

import cofh.api.tileentity.IReconfigurableFacing;
import cofh.api.tileentity.IReconfigurableSides;
import cofh.api.tileentity.ISidedTexture;
import cofh.core.network.PacketCoFHBase;
import cofh.lib.util.helpers.BlockHelper;
import cofh.thermalexpansion.util.ReconfigurableHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;

public abstract class TileReconfigurable extends TileRSControl implements IReconfigurableFacing, IReconfigurableSides, ISidedInventory, ISidedTexture {

	protected SideConfig sideConfig;

	protected byte facing = 3;
	public byte[] sideCache = { 0, 0, 0, 0, 0, 0 };

	@Override
	protected boolean readPortableTagInternal(EntityPlayer player, NBTTagCompound tag) {

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
		return super.readPortableTagInternal(player, tag);
	}

	@Override
	protected boolean writePortableTagInternal(EntityPlayer player, NBTTagCompound tag) {

		ReconfigurableHelper.setItemStackTagReconfig(tag, this);
		return super.writePortableTagInternal(player, tag);
	}

	@Override
	public boolean onWrench(EntityPlayer player, EnumFacing side) {

		return rotateBlock();
	}

	public byte[] getDefaultSides() {

		return sideConfig.defaultSides.clone();
	}

	public void setDefaultSides() {

		sideCache = getDefaultSides();
	}

	/* GUI METHODS */
	public final boolean hasSide(int side) {

		for (int i = 0; i < 6; i++) {
			if (sideCache[i] == side) {
				return true;
			}
		}
		return false;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		facing = ReconfigurableHelper.getFacingFromNBT(nbt);
		sideCache = ReconfigurableHelper.getSideCacheFromNBT(nbt, getDefaultSides());
		for (int i = 0; i < 6; i++) {
			if (sideCache[i] >= getNumConfig(i)) {
				sideCache[i] = 0;
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setByte("Facing", facing);
		nbt.setByteArray("SideCache", sideCache);
		return nbt;
	}

	/* NETWORK METHODS */
	@Override
	public PacketCoFHBase getPacket() {

		PacketCoFHBase payload = super.getPacket();

		payload.addByteArray(sideCache);
		payload.addByte(facing);

		return payload;
	}

	/* ITilePacketHandler */
	@Override
	public void handleTilePacket(PacketCoFHBase payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		payload.getByteArray(sideCache);

		for (int i = 0; i < 6; i++) {
			if (sideCache[i] >= getNumConfig(i)) {
				sideCache[i] = 0;
			}
		}
		if (!isServer) {
			facing = payload.getByte();
		} else {
			payload.getByte();
		}
		callNeighborTileChange();
	}

	/* IReconfigurableFacing */
	@Override
	public final int getFacing() {

		return facing;
	}

	@Override
	public boolean allowYAxisFacing() {

		return false;
	}

	@Override
	public boolean rotateBlock() {

		if (allowYAxisFacing()) {
			byte[] tempCache = new byte[6];

			switch (facing) {
				case 0:
					for (int i = 0; i < 6; i++) {
						tempCache[i] = sideCache[BlockHelper.INVERT_AROUND_X[i]];
					}
					break;
				case 1:
					for (int i = 0; i < 6; i++) {
						tempCache[i] = sideCache[BlockHelper.ROTATE_CLOCK_X[i]];
					}
					break;
				case 2:
					for (int i = 0; i < 6; i++) {
						tempCache[i] = sideCache[BlockHelper.INVERT_AROUND_Y[i]];
					}
					break;
				case 3:
					for (int i = 0; i < 6; i++) {
						tempCache[i] = sideCache[BlockHelper.ROTATE_CLOCK_Y[i]];
					}
					break;
				case 4:
					for (int i = 0; i < 6; i++) {
						tempCache[i] = sideCache[BlockHelper.INVERT_AROUND_Z[i]];
					}
					break;
				case 5:
					for (int i = 0; i < 6; i++) {
						tempCache[i] = sideCache[BlockHelper.ROTATE_CLOCK_Z[i]];
					}
					break;
			}
			sideCache = tempCache.clone();
			facing++;
			facing %= 6;
			markDirty();
			sendUpdatePacket(Side.CLIENT);
			return true;
		}
		if (isActive) {
			return false;
		}
		byte[] tempCache = new byte[6];
		for (int i = 0; i < 6; i++) {
			tempCache[i] = sideCache[BlockHelper.ROTATE_CLOCK_Y[i]];
		}
		sideCache = tempCache.clone();
		facing = BlockHelper.SIDE_LEFT[facing];
		markDirty();
		sendUpdatePacket(Side.CLIENT);
		return true;
	}

	@Override
	public boolean setFacing(int side) {

		if (side < 0 || side > 5) {
			return false;
		}
		if (!allowYAxisFacing() && side < 2) {
			return false;
		}
		facing = (byte) side;
		markDirty();
		sendUpdatePacket(Side.CLIENT);
		return true;
	}

	/* IReconfigurableSides */
	@Override
	public boolean decrSide(int side) {

		if (side == facing) {
			return false;
		}
		sideCache[side] += getNumConfig(side) - 1;
		sideCache[side] %= getNumConfig(side);
		sendUpdatePacket(Side.SERVER);
		return true;
	}

	@Override
	public boolean incrSide(int side) {

		if (side == facing) {
			return false;
		}
		sideCache[side] += 1;
		sideCache[side] %= getNumConfig(side);
		sendUpdatePacket(Side.SERVER);
		return true;
	}

	@Override
	public boolean setSide(int side, int config) {

		if (side == facing || sideCache[side] == config || config >= getNumConfig(side)) {
			return false;
		}
		sideCache[side] = (byte) config;
		sendUpdatePacket(Side.SERVER);
		return true;
	}

	@Override
	public boolean resetSides() {

		boolean update = false;
		for (int i = 0; i < 6; i++) {
			if (sideCache[i] > 0) {
				sideCache[i] = 0;
				update = true;
			}
		}
		if (update) {
			sendUpdatePacket(Side.SERVER);
		}
		return update;
	}

	@Override
	public int getNumConfig(int side) {

		return sideConfig.numConfig;
	}

	/* ISidedInventory */
	@Override
	public int[] getSlotsForFace(EnumFacing side) {

		return sideConfig.slotGroups[sideCache[side.ordinal()]];
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, EnumFacing side) {

		return (sideConfig.allowInsertionSide[sideCache[side.ordinal()]] && sideConfig.allowInsertionSlot[slot]) && isItemValidForSlot(slot, stack);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, EnumFacing side) {

		return sideConfig.allowExtractionSide[sideCache[side.ordinal()]] && sideConfig.allowExtractionSlot[slot];
	}

	/* ISidedTexture */
	@Override
	public abstract TextureAtlasSprite getTexture(int side, int pass);

}
