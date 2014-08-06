package thermalexpansion.block.sponge;

import cofh.lib.util.helpers.FluidHelper;
import cofh.lib.util.helpers.ServerHelper;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.block.Block;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import thermalexpansion.block.TEBlocks;

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
							if (fluid == null) {
								fluid = new FluidStack(queryFluid, 1000);
								bucketCounter = 1;
								worldObj.setBlock(i, j, k, TEBlocks.blockAirBarrier, 0, 3);
							} else if (fluid.fluidID == queryFluid.getID()) {
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
		if (fluid != null) {
			fluid.amount = bucketCounter * 1000;
			full = true;
		}
	}

}
