package cofh.thermalexpansion.block.tank;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nullable;

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
		adjacentHandlers[0].fill(new FluidStack(tank.getFluid(), Fluid.BUCKET_VOLUME * 64), true);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, final EnumFacing facing) {

		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(new IFluidHandler() {
				@Override
				public IFluidTankProperties[] getTankProperties() {

					return FluidTankProperties.convert(new FluidTankInfo[] { tank.getInfo() });
				}

				@Override
				public int fill(FluidStack resource, boolean doFill) {

					int ordinal = facing == null ? 6 : facing.ordinal();

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

				@Nullable
				@Override
				public FluidStack drain(FluidStack resource, boolean doDrain) {

					int ordinal = facing == null ? 6 : facing.ordinal();

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

				@Nullable
				@Override
				public FluidStack drain(int maxDrain, boolean doDrain) {

					int ordinal = facing == null ? 6 : facing.ordinal();

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
			});
		}
		return super.getCapability(capability, facing);
	}

}
