package thermalexpansion.block.light;

import cpw.mods.fml.common.registry.GameRegistry;

public class TileLightFalse extends TileLight {

	public static void initialize() {

		GameRegistry.registerTileEntity(TileLightFalse.class, "thermalexpansion.LightFalse");
	}

	@Override
	public int getLightValue() {

		return 0;
	}

	public void cofh_validate() {

		int m = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
		worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, m & 3, 2);
		TileLight tile = (TileLight)worldObj.getTileEntity(xCoord, yCoord, zCoord);
		tile.dim = true;
		worldObj.func_147451_t(xCoord, yCoord, zCoord);
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

}
