package thermalexpansion.block.plate;

import cofh.core.network.ITilePacketHandler;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

import thermalexpansion.block.TileTEBase;

public abstract class TilePlateBase extends TileTEBase implements ITilePacketHandler {

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
