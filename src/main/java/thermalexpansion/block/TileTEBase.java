package thermalexpansion.block;

import cofh.block.TileCoFHBase;

import net.minecraft.nbt.NBTTagCompound;

import thermalexpansion.core.TEProps;

public abstract class TileTEBase extends TileCoFHBase {

	/* NBT METHODS */
	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);
		nbt.setString("Version", TEProps.VERSION);
	}

}
