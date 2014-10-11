package thermalexpansion.block.plate;

import cofh.core.network.PacketCoFHBase;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

import thermalexpansion.block.TileTEBase;

public abstract class TilePlateBase extends TileTEBase {

	byte alignment;
	byte direction;

	@Override
	public boolean canUpdate() {

		return false;
	}

	@Override
	public String getName() {

		return "tile.thermalexpansion.plate." + BlockPlate.NAMES[getType()] + ".name";
	}

	public abstract void onEntityCollidedWithBlock(Entity theEntity);

	public int getAlignment() {

		return alignment;
	}

	/* NETWORK METHODS */
	@Override
	public PacketCoFHBase getPacket() {

		PacketCoFHBase payload = super.getPacket();

		payload.addByte(alignment);
		payload.addByte(direction);

		return payload;
	}

	/* ITilePacketHandler */
	@Override
	public void handleTilePacket(PacketCoFHBase payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		if (!isServer) {

			alignment = payload.getByte();
			direction = payload.getByte();
		} else {

		}
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		alignment = nbt.getByte("Align");
		direction = nbt.getByte("Dir");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setByte("Align", alignment);
		nbt.setByte("Dir", direction);
	}

}
