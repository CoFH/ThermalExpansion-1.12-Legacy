package cofh.thermalexpansion.block.device;

import cofh.thermalexpansion.block.TEBlocks;
import cofh.thermalexpansion.block.TileInventory;
import cofh.thermalexpansion.block.workbench.TileWorkbench;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.nbt.NBTTagCompound;

public class TileWorkbenchFalse extends TileInventory {

	public static void initialize() {

		GameRegistry.registerTileEntity(TileWorkbenchFalse.class, "thermalexpansion.WorkbenchFalse");
	}

	// Conversion Code
	@Override
	public void cofh_validate() {

		worldObj.setBlock(xCoord, yCoord, zCoord, TEBlocks.blockWorkbench, 1, 3);

		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		tag.setByte("Type", (byte) 1);
		invalidate();
		TileWorkbench tile = (TileWorkbench) worldObj.getTileEntity(xCoord, yCoord, zCoord);
		tile.readFromNBT(tag);
		worldObj.func_147451_t(xCoord, yCoord, zCoord);
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	// This is really client only

	public TileWorkbenchFalse() {

	}

	@Override
	public String getName() {

		return "tile.invalid";
	}

	@Override
	public int getType() {

		return 0;
	}

}
