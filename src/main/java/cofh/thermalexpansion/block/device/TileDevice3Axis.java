package cofh.thermalexpansion.block.device;

import net.minecraftforge.fml.relauncher.Side;

public abstract class TileDevice3Axis extends TileDeviceBase {

	/* IReconfigurableFacing */
	@Override
	public boolean allowYAxisFacing() {

		return true;
	}

	@Override
	public boolean setFacing(int side) {

		if (side < 0 || side > 5) {
			return false;
		}
		facing = (byte) side;
		sideCache[facing] = 0;
		sideCache[facing ^ 1] = 1;
		markChunkDirty();
		sendTilePacket(Side.CLIENT);
		return true;
	}

}
