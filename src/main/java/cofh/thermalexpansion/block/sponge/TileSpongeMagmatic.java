package cofh.thermalexpansion.block.sponge;

import cofh.lib.util.helpers.FluidHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.block.TEBlocks;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.block.Block;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class TileSpongeMagmatic extends TileSponge {

	public static void initialize() {

		GameRegistry.registerTileEntity(TileSpongeMagmatic.class, "thermalexpansion.SpongeMagmatic");
	}

	public TileSpongeMagmatic() {

	}

	public TileSpongeMagmatic(int metadata) {

		super(metadata);
	}

	@Override
	public int getType() {

		return BlockSponge.Types.MAGMATIC.ordinal();
	}

	@Override
	public void placeAir() {

		if (ServerHelper.isClientWorld(worldObj)) {
			return;
		}
		if (fullOnPlace) {
			return;
		}
		Block query;
		int queryMeta;
		Fluid queryFluid;
		int bucketCounter = 0;
		for (int i = xCoord - 1; i <= xCoord + 1; i++) {
			for (int j = yCoord - 1; j <= yCoord + 1; j++) {
				for (int k = zCoord - 1; k <= zCoord + 1; k++) {
					query = worldObj.getBlock(i, j, k);
					queryMeta = worldObj.getBlockMetadata(i, j, k);

					if (queryMeta == 0) {
						queryFluid = FluidHelper.lookupFluidForBlock(query);
						if (!full && queryFluid != null) {
							if (myFluidStack == null) {
								myFluidStack = new FluidStack(queryFluid, 1000);
								bucketCounter = 1;
								worldObj.setBlock(i, j, k, TEBlocks.blockAirBarrier, 0, 3);
							} else if (myFluidStack.getFluid() == queryFluid) {
								bucketCounter++;
								worldObj.setBlock(i, j, k, TEBlocks.blockAirBarrier, 0, 3);
							}
						} else if (query.isAir(worldObj, i, j, k)) {
							worldObj.setBlock(i, j, k, TEBlocks.blockAirBarrier, 0, 3);
						}
					} else if (query.isAir(worldObj, i, j, k) || query.getMaterial().isLiquid()) {
						worldObj.setBlock(i, j, k, TEBlocks.blockAirBarrier, 0, 3);
					}
				}
			}
		}
		if (myFluidStack != null) {
			myFluidStack.amount = bucketCounter * 1000;
			full = true;
		}
	}

}
