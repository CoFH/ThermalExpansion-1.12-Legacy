package cofh.thermalexpansion.block.tank;

import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraftforge.common.util.ForgeDirection;
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
		adjacentHandlers[0].fill(ForgeDirection.VALID_DIRECTIONS[1], new FluidStack(tank.getFluid(), FluidContainerRegistry.BUCKET_VOLUME * 64), true);
	}

	/* IFluidHandler */
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {

		if (from.ordinal() == 0 && mode == 1) {
			return 0;
		}
		if (from.ordinal() > 1 && from.ordinal() < 6) {
			return 0;
		}
		if (resource == null || resource.getFluid() == null || resource.isFluidEqual(tank.getFluid())) {
			return 0;
		}
		tank.setFluid(new FluidStack(resource.getFluid(), FluidContainerRegistry.BUCKET_VOLUME));
		updateRender();
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {

		if (from.ordinal() == 0 && mode == 1) {
			return null;
		}
		if (from.ordinal() > 1 && from.ordinal() < 6) {
			return null;
		}
		if (resource == null || !resource.isFluidEqual(tank.getFluid())) {
			return null;
		}
		return resource.copy();
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {

		if (from.ordinal() == 0 && mode == 1) {
			return null;
		}
		if (from.ordinal() > 1 && from.ordinal() < 6) {
			return null;
		}
		if (tank.getFluid() == null) {
			return null;
		}
		return new FluidStack(tank.getFluid(), maxDrain);
	}

}
