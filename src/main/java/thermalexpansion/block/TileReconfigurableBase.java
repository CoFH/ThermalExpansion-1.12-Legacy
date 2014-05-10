package thermalexpansion.block;

import cofh.api.tileentity.IReconfigurableFacing;
import cofh.api.tileentity.IReconfigurableSides;
import cofh.api.tileentity.ISidedBlockTexture;
import cofh.network.CoFHPacket;
import cofh.network.ITilePacketHandler;
import cofh.util.BlockHelper;
import cofh.util.ServerHelper;
import cpw.mods.fml.relauncher.Side;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;

public abstract class TileReconfigurableBase extends TileRSBase implements IReconfigurableFacing, IReconfigurableSides, ISidedBlockTexture, ITilePacketHandler {

	protected boolean isActive = false;
	protected byte facing = 3;
	public byte[] sideCache = { 0, 0, 0, 0, 0, 0 };

	@Override
	public boolean onWrench(EntityPlayer player, int hitSide) {

		return rotateBlock();
	}

	/* NETWORK METHODS */
	@Override
	public CoFHPacket getPacket() {

		CoFHPacket payload = super.getPacket();

		payload.addByteArray(sideCache);
		payload.addByte(facing);
		payload.addBool(isActive);
		return payload;
	}

	/* ITilePacketHandler */
	@Override
	public void handleTilePacket(CoFHPacket payload, boolean isServer) {

		super.handleTilePacket(payload);

		payload.getByteArray(sideCache);

		if (ServerHelper.isClientWorld(worldObj)) {
			facing = payload.getByte();
			isActive = payload.getBool();
		} else {
			payload.getByte();
			payload.getBool();
		}
		for (int i = 0; i < 6; ++i) {
			if (sideCache[i] >= getNumConfig(i)) {
				sideCache[i] = 0;
			}
		}
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		worldObj.updateAllLightTypes(xCoord, yCoord, zCoord);
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
	}

	/* GUI METHODS */
	public boolean isActive() {

		return isActive;
	}

	public boolean hasSide(int side) {

		for (int i = 0; i < 6; ++i) {
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

		isActive = nbt.getBoolean("Active");
		facing = nbt.getByte("Facing");
		sideCache = nbt.getByteArray("SideCache");

		if (sideCache.length == 0) {
			sideCache = new byte[] { 0, 0, 0, 0, 0, 0 };
		}
		for (int i = 0; i < 6; ++i) {
			if (sideCache[i] >= getNumConfig(i)) {
				sideCache[i] = 0;
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setBoolean("Active", isActive);
		nbt.setByte("Facing", facing);
		nbt.setByteArray("SideCache", sideCache);
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
			worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
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
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
		sendUpdatePacket(Side.CLIENT);
		return true;
	}

	@Override
	public boolean setFacing(int side) {

		if (side < 0 || side > 5) {
			return false;
		}
		if (allowYAxisFacing() && side < 2) {
			return false;
		}
		facing = (byte) side;
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
		sendUpdatePacket(Side.CLIENT);
		return true;
	}

	/* IReconfigurableSides */
	@Override
	public boolean decrSide(int side) {

		sideCache[side] += getNumConfig(side) - 1;
		sideCache[side] %= getNumConfig(side);
		sendUpdatePacket(Side.SERVER);
		return true;
	}

	@Override
	public boolean incrSide(int side) {

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
		for (int i = 0; i < 6; ++i) {
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
	public abstract int getNumConfig(int side);

	/* ISidedBlockTexture */
	@Override
	public abstract IIcon getBlockTexture(int side, int pass);

}
