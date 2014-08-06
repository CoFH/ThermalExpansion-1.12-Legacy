package thermalexpansion.block.device;

import cofh.api.energy.EnergyStorage;
import cofh.core.util.fluid.FluidTankAdv;
import cofh.lib.util.helpers.ServerHelper;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import thermalexpansion.block.TileReconfigurable;
import thermalexpansion.core.TEProps;
import thermalexpansion.gui.client.machine.GuiTransposer;
import thermalexpansion.gui.container.machine.ContainerTransposer;

public class TilePump extends TileReconfigurable implements IFluidHandler {

	public static void initialize() {

	}

	int outputTracker;
	FluidTankAdv tank = new FluidTankAdv(TEProps.MAX_FLUID_LARGE);

	public boolean reverse;

	public TilePump() {

		sideCache = new byte[] { 0, 0, 1, 1, 1, 1 };
		energyStorage = new EnergyStorage(0);
	}

	@Override
	public String getName() {

		return "tile.thermalexpansion.device." + BlockDevice.NAMES[getType()] + ".name";
	}

	@Override
	public int getType() {

		return BlockDevice.Types.PUMP.ordinal();
	}

	@Override
	public void updateEntity() {

		if (ServerHelper.isClientWorld(worldObj)) {
			return;
		}
	}

	@Override
	public void invalidate() {

		super.invalidate();
	}

	@Override
	public void validate() {

		super.validate();
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiTransposer(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerTransposer(inventory, this);
	}

	public FluidTankAdv getTank() {

		return tank;
	}

	public FluidStack getTankFluid() {

		return tank.getFluid();
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		outputTracker = nbt.getInteger("Tracker2");
		reverse = nbt.getBoolean("Rev");
		tank.readFromNBT(nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("Tracker", outputTracker);
		nbt.setBoolean("Rev", reverse);
		tank.writeToNBT(nbt);
	}

	/* IReconfigurableSides */
	@Override
	public int getNumConfig(int side) {

		return 3;
	}

	/* ISidedTexture */
	@Override
	public IIcon getTexture(int side, int pass) {

		return null;
	}

	/* IFluidHandler */
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {

		if (!reverse || from == ForgeDirection.UNKNOWN || sideCache[from.ordinal()] != 1) {
			return 0;
		}
		return tank.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {

		if (reverse || from == ForgeDirection.UNKNOWN || sideCache[from.ordinal()] != 2) {
			return null;
		}
		return tank.drain(resource, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {

		if (reverse || from == ForgeDirection.UNKNOWN || sideCache[from.ordinal()] != 2) {
			return null;
		}
		return tank.drain(maxDrain, doDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {

		if (from == ForgeDirection.UNKNOWN) {
			return false;
		}
		return !reverse && sideCache[from.ordinal()] == 1;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {

		if (from == ForgeDirection.UNKNOWN) {
			return false;
		}
		return reverse && sideCache[from.ordinal()] == 2;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {

		return new FluidTankInfo[] { tank.getInfo() };
	}

}
