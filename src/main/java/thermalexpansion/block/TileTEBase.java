package thermalexpansion.block;

import net.minecraft.nbt.NBTTagCompound;
import thermalexpansion.core.TEProps;
import cofh.block.TileCoFHBase;

public abstract class TileTEBase extends TileCoFHBase {

	/* NBT METHODS */
	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);
		nbt.setString("Version", TEProps.VERSION);
	}

}
