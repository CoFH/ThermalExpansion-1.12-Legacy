package cofh.thermalexpansion.block.light;

import codechicken.lib.util.BlockUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.fml.common.registry.GameRegistry;

import net.minecraft.nbt.NBTTagCompound;

public class TileLightFalse extends TileLight {

	public static void initialize() {

		GameRegistry.registerTileEntity(TileLightFalse.class, "thermalexpansion.LightFalse");
	}

	@Override
	public int getLightValue() {

		return 0;
	}

	public void cofh_validate() {

		IBlockState state = worldObj.getBlockState(getPos());
        int meta = state.getBlock().getMetaFromState(state);
		worldObj.setBlockState(getPos(), state.getBlock().getStateFromMeta(meta & 3), 2);
		dim = true;
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		invalidate();
		TileLight tile = (TileLight) worldObj.getTileEntity(getPos());
		tile.readFromNBT(tag);
		updateLighting();
        BlockUtils.fireBlockUpdate(worldObj, getPos());
	}

}
