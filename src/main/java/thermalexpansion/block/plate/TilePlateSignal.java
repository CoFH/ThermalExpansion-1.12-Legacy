package thermalexpansion.block.plate;

import cofh.core.network.PacketCoFHBase;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;

import thermalexpansion.block.TEBlocks;

public class TilePlateSignal extends TilePlateBase {

	public static void initialize() {

		GameRegistry.registerTileEntity(TilePlateSignal.class, "cofh.thermalexpansion.PlateSignal");
	}

	byte distance = 16;
	byte intensity = 15;
	byte activationTime = 15;
	byte collided = 0;

	@Override
	public int getType() {

		return BlockPlate.Types.SIGNAL.ordinal();
	}

	@Override
	public boolean canUpdate() {

		// FIXME: in 1.8 we can differentiate random world ticks and update ticks on the block.
		// we can use that to destroy the block
		return true;
	}

	@Override
	public void updateEntity() {

		if (collided > 0) {
			markChunkDirty();
			if (--collided == 0) {
				int x = 0, y = 0, z = 0;
				switch (direction) {
				case 0:
					y = -distance;
					break;
				case 1:
					y = distance;
					break;
				case 2:
					z = -distance;
					break;
				case 3:
					z = distance;
					break;
				case 4:
					x = -distance;
					break;
				case 5:
					x = distance;
					break;
				}
				if (worldObj.getBlock(xCoord + x, yCoord + y, zCoord + z).equals(TEBlocks.blockAirSignal)) {
					worldObj.setBlock(xCoord + x, yCoord + y, zCoord + z, Blocks.air, 0, 3);
				}
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
		}
	}

	/* NETWORK METHODS */
	@Override
	public PacketCoFHBase getPacket() {

		PacketCoFHBase payload = super.getPacket();

		payload.addByte(distance);
		payload.addByte(intensity);
		payload.addByte(activationTime);
		payload.addByte(collided);

		return payload;
	}

	@Override
	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = super.getGuiPacket();

		payload.addByte(distance);
		payload.addByte(intensity);
		payload.addByte(activationTime);

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketCoFHBase payload) {

		super.handleGuiPacket(payload);

		distance = payload.getByte();
		intensity = payload.getByte();
		activationTime = payload.getByte();
		collided = payload.getByte();
	}

	/* ITilePacketHandler */
	@Override
	public void handleTilePacket(PacketCoFHBase payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		if (!isServer) {

			distance = payload.getByte();
			intensity = payload.getByte();
			activationTime = payload.getByte();
		} else {

		}
	}

	@Override
	public void onEntityCollidedWithBlock(Entity theEntity) {

		if (collided > 0) {
			collided = activationTime;
			return;
		}
		int x = 0;
		int y = 0;
		int z = 0;

		switch (direction) {
		case 0:
			y = -distance;
			break;
		case 1:
			y = distance;
			break;
		case 2:
			z = -distance;
			break;
		case 3:
			z = distance;
			break;
		case 4:
			x = -distance;
			break;
		case 5:
			x = distance;
			break;
		}
		if (worldObj.isAirBlock(xCoord + x, yCoord + y, zCoord + z)) {
			if (worldObj.setBlock(xCoord + x, yCoord + y, zCoord + z, TEBlocks.blockAirSignal, intensity, 3)) {
				markChunkDirty();
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
			collided = activationTime;
		}
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		distance = nbt.getByte("Dist");
		intensity = nbt.getByte("Int");
		activationTime = nbt.getByte("Act");
		collided = nbt.getByte("Col");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setByte("Dist", distance);
		nbt.setByte("Int", intensity);
		nbt.setByte("Act", activationTime);
		nbt.setByte("Col", collided);
	}

}
