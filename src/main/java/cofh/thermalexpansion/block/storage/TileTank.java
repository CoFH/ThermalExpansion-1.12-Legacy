package cofh.thermalexpansion.block.storage;

import cofh.core.fluid.FluidTankCore;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.FluidHelper;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.TileAugmentableSecure;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nullable;

public class TileTank extends TileAugmentableSecure implements ITickable {

	public static int[] CAPACITY = { 1, 4, 9, 16, 25 };

	static {
		for (int i = 0; i < CAPACITY.length; i++) {
			CAPACITY[i] *= 10000;
		}
	}
	private static boolean enableSecurity = true;

	public static void initialize() {

		GameRegistry.registerTileEntity(TileTank.class, "thermalexpansion:storage_tank");

		config();
	}

	public static void config() {

		String comment = "Enable this to allow for Tanks to be securable.";
		enableSecurity = ThermalExpansion.CONFIG.get("Security", "Tank.Securable", true, comment);
	}

	int compareTracker;
	boolean cached = false;
	boolean adjacentTanks[] = new boolean[2];

	private FluidTankCore tank = new FluidTankCore(getCapacity(0));

	@Override
	public String getTileName() {

		return "tile.thermalexpansion.storage.tank.name";
	}

	@Override
	public int getType() {

		return 0;
	}

	@Override
	public int getComparatorInputOverride() {

		return compareTracker;
	}

	@Override
	public boolean enableSecurity() {

		return enableSecurity;
	}

	@Override
	protected boolean setLevel(int level) {

		if (super.setLevel(level)) {
			tank.setCapacity(getCapacity(level));
			return true;
		}
		return false;
	}

	@Override
	public void invalidate() {

		cached = false;
		super.invalidate();
	}

	@Override
	public void onNeighborBlockChange() {

		super.onNeighborBlockChange();
		updateAdjacentHandlers();
	}

	@Override
	public void onNeighborTileChange(BlockPos pos) {

		super.onNeighborTileChange(pos);
		updateAdjacentHandlers();
	}

	@Override
	public void update() {

		if (ServerHelper.isClientWorld(worldObj)) {
			return;
		}
		transferFluid();

		if (timeCheck()) {
			int curScale = getScaledFluidStored(15);
			if (curScale != compareTracker) {
				compareTracker = curScale;
				callNeighborTileChange();
			}
			if (!cached) {
				updateAdjacentHandlers();
			}
		}
	}

	@Override
	public FluidTankCore getTank() {

		return tank;
	}

	@Override
	public FluidStack getTankFluid() {

		return tank.getFluid();
	}

	/* COMMON METHODS */
	protected static int getCapacity(int level) {

		return CAPACITY[MathHelper.clamp(level, 0, 4)];
	}

	protected int getScaledFluidStored(int scale) {

		return tank.getFluid() == null ? 0 : tank.getFluid().amount * scale / tank.getCapacity();
	}

	protected void transferFluid() {

		if (!enableAutoOutput || tank.getFluidAmount() <= 0) {
			return;
		}
		tank.drain(FluidHelper.insertFluidIntoAdjacentFluidHandler(this, EnumFacing.DOWN, new FluidStack(tank.getFluid(), Math.min(getFluidTransfer(level), tank.getFluidAmount())), true), true);
	}

	protected void updateAdjacentHandlers() {

		if (ServerHelper.isClientWorld(worldObj)) {
			return;
		}
		boolean curAutoOutput = enableAutoOutput;

		adjacentTanks[0] = BlockHelper.getAdjacentTileEntity(this, EnumFacing.DOWN) instanceof TileTank;
		enableAutoOutput |= adjacentTanks[0];

		adjacentTanks[1] = BlockHelper.getAdjacentTileEntity(this, EnumFacing.UP) instanceof TileTank;

		if (curAutoOutput != enableAutoOutput) {
			sendUpdatePacket(Side.CLIENT);
		}
		cached = true;
	}

	public int getTankCapacity() {

		return tank.getCapacity();
	}

	public int getTankFluidAmount() {

		return tank.getFluidAmount();
	}

	/* GUI METHODS */
	@Override
	public boolean hasGui() {

		return false;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		tank.readFromNBT(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		tank.writeToNBT(nbt);
		return nbt;
	}

	/* CAPABILITIES */
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing from) {

		return super.hasCapability(capability, from) || capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, final EnumFacing from) {

		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(new IFluidHandler() {
				@Override
				public IFluidTankProperties[] getTankProperties() {

					FluidTankInfo info = tank.getInfo();
					return new IFluidTankProperties[] { new FluidTankProperties(info.fluid, info.capacity, true, true) };
				}

				@Override
				public int fill(FluidStack resource, boolean doFill) {

					if (from == EnumFacing.DOWN && !adjacentTanks[0]) {
						return 0;
					}
					if (adjacentTanks[1]) {
						if (from == EnumFacing.UP) {
							return tank.fill(resource, doFill);
						}
						if (resource == null) {
							return 0;
						}
						int amount = tank.fill(resource, doFill);

						if (amount != resource.amount) {
							FluidStack remaining = resource.copy();
							remaining.amount -= amount;
							return amount + FluidHelper.insertFluidIntoAdjacentFluidHandler(worldObj, pos, EnumFacing.UP, remaining, true);
						}

					}
					return tank.fill(resource, doFill);
				}

				@Nullable
				@Override
				public FluidStack drain(FluidStack resource, boolean doDrain) {

					return tank.drain(resource, doDrain);
				}

				@Nullable
				@Override
				public FluidStack drain(int maxDrain, boolean doDrain) {

					return tank.drain(maxDrain, doDrain);
				}
			});
		}
		return super.getCapability(capability, from);
	}

}
