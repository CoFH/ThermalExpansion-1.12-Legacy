package cofh.thermalexpansion.block.tank;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;

public class TileTankCreative extends TileTank {

	public static void initialize() {

		GameRegistry.registerTileEntity(TileTankCreative.class, "thermalexpansion.TankCreative");
	}

	public TileTankCreative() {

	}

	public TileTankCreative(int metadata) {

		super(metadata);
	}

	@Override
	protected void transferFluid() {

		if (tank.getFluidAmount() <= 0 || adjacentHandlers[0] == null) {
			return;
		}
		adjacentHandlers[0].fill(EnumFacing.UP, new FluidStack(tank.getFluid(), FluidContainerRegistry.BUCKET_VOLUME * 64), true);
	}

	/* IFluidHandler */
	@Override
	public int fill(EnumFacing from, FluidStack resource, boolean doFill) {

        int ordinal = from == null ? 6 : from.ordinal();

		if (ordinal == 0 && mode == 1) {
			return 0;
		}
		if (ordinal > 1 && ordinal < 6) {
			return 0;
		}
		if (resource == null || resource.getFluid() == null || resource.isFluidEqual(tank.getFluid())) {
			return 0;
		}
		tank.setFluid(new FluidStack(resource.getFluid(), FluidContainerRegistry.BUCKET_VOLUME));
		sendUpdatePacket(Side.CLIENT);
		updateRender();
		return 0;
	}

	@Override
	public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {

        int ordinal = from == null ? 6 : from.ordinal();

		if (ordinal == 0 && mode == 1) {
			return null;
		}
		if (ordinal > 1 && ordinal < 6) {
			return null;
		}
		if (resource == null || !resource.isFluidEqual(tank.getFluid())) {
			return null;
		}
		return resource.copy();
	}

	@Override
	public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {

        int ordinal = from == null ? 6 : from.ordinal();

		if (ordinal == 0 && mode == 1) {
			return null;
		}
		if (ordinal > 1 && ordinal < 6) {
			return null;
		}
		if (tank.getFluid() == null) {
			return null;
		}
		return new FluidStack(tank.getFluid(), maxDrain);
	}

}
