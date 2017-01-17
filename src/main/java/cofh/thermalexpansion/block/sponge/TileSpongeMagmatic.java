package cofh.thermalexpansion.block.sponge;

import cofh.lib.util.helpers.FluidHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.block.TEBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

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
		IBlockState query;
		int queryMeta;
		Fluid queryFluid;
		int bucketCounter = 0;
		for (int x = -1; x <= 1; x++) {
			for (int y = -1; y <= 1; y++) {
				for (int z = -1; z <= 1; z++) {
					BlockPos offsetPos = getPos().add(x, y, z);
					query = worldObj.getBlockState(offsetPos);
					queryMeta = query.getBlock().getMetaFromState(query);

					if (queryMeta == 0) {
						queryFluid = FluidHelper.lookupFluidForBlock(query.getBlock());
						if (!full && queryFluid != null) {
							if (myFluidStack == null) {
								myFluidStack = new FluidStack(queryFluid, 1000);
								bucketCounter = 1;
								worldObj.setBlockState(offsetPos, TEBlocks.blockAirBarrier.getDefaultState(), 3);
							} else if (myFluidStack.getFluid() == queryFluid) {
								bucketCounter++;
								worldObj.setBlockState(offsetPos, TEBlocks.blockAirBarrier.getDefaultState(), 3);
							}
						} else if (query.getBlock().isAir(query, worldObj, offsetPos)) {
							worldObj.setBlockState(offsetPos, TEBlocks.blockAirBarrier.getDefaultState(), 3);
						}
					} else if (query.getBlock().isAir(query, worldObj, offsetPos) || query.getMaterial().isLiquid()) {
						worldObj.setBlockState(offsetPos, TEBlocks.blockAirBarrier.getDefaultState(), 3);
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
