package cofh.thermalexpansion.block.sponge;

import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.init.TEBlocksOld;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class TileSpongeCreative extends TileSponge {

	public static void initialize() {

		GameRegistry.registerTileEntity(TileSpongeCreative.class, "thermalexpansion.SpongeCreative");
	}

	public TileSpongeCreative() {

	}

	public TileSpongeCreative(int metadata) {

		super(metadata);
	}

	@Override
	public int getType() {

		return BlockSponge.Types.CREATIVE.ordinal();
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
		// int queryMeta;
		// Fluid queryFluid;
		// int bucketCounter = 0;
		for (int x = -1; x <= 1; x++) {
			for (int y = -1; y <= 1; y++) {
				for (int z = -1; z <= 1; z++) {
					BlockPos offsetPos = getPos().add(x, y, z);
					query = worldObj.getBlockState(offsetPos);
					if (query.getBlock().isAir(query, worldObj, offsetPos) || query.getMaterial().isLiquid()) {
						worldObj.setBlockState(offsetPos, TEBlocksOld.blockAirBarrier.getDefaultState(), 3);
					}
				}
			}
		}
	}

}
