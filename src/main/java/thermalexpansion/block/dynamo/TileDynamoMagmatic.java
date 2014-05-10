package thermalexpansion.block.dynamo;

import cofh.network.CoFHPacket;
import cofh.network.CoFHTileInfoPacket;
import cpw.mods.fml.common.registry.GameRegistry;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.core.TEProps;

public class TileDynamoMagmatic extends TileDynamoBase implements IFluidHandler {

	public static void initialize() {

		guiIds[BlockDynamo.Types.MAGMATIC.ordinal()] = ThermalExpansion.proxy.registerGui("DynamoMagmatic", "dynamo", "TEBase", null, true);
		GameRegistry.registerTileEntity(TileDynamoMagmatic.class, "cofh.thermalexpansion.DynamoMagmatic");
	}

	static Map fuels = new HashMap<Fluid, Integer>();

	FluidTank tank = new FluidTank(MAX_FLUID);
	FluidStack renderFluid = new FluidStack(FluidRegistry.LAVA, FluidContainerRegistry.BUCKET_VOLUME);

	@Override
	public int getType() {

		return BlockDynamo.Types.MAGMATIC.ordinal();
	}

	public static boolean registerFuel(Fluid fluid, int energy) {

		if (fluid == null || energy <= 10000) {
			return false;
		}
		fuels.put(fluid, energy / 100);
		return true;
	}

	public static int getFuelEnergy(FluidStack stack) {

		return stack == null ? 0 : (Integer) fuels.get(stack.getFluid());
	}

	public static boolean isValidFuel(FluidStack stack) {

		return stack == null ? false : fuels.containsKey(stack.getFluid());
	}

	public FluidTank getTank(int tankIndex) {

		return tank;
	}

	@Override
	protected boolean canGenerate() {

		return fuelRF > 0 ? true : tank.getFluidAmount() >= 10;
	}

	@Override
	public void generate() {

		if (fuelRF <= 0) {
			fuelRF += getFuelEnergy(tank.getFluid());
			tank.drain(10, true);
		}
		int energy = calcEnergy();
		energyStorage.modifyEnergyStored(energy);
		fuelRF -= energy;
	}

	@Override
	public IIcon getActiveIcon() {

		return renderFluid.getFluid().getIcon(renderFluid);
	}

	@Override
	public int getLightValue() {

		return isActive ? 14 : 0;
	}

	/* NETWORK METHODS */
	@Override
	public CoFHPacket getPacket() {

		CoFHPacket payload = super.getPacket();

		payload.addFluidStack(tank.getFluid());
		return payload;
	}

	@Override
	public CoFHPacket getGuiCoFHPacket() {

		CoFHPacket payload = CoFHTileInfoPacket.getTileInfoPacket(this);

		payload.addByte(TEProps.PacketID.GUI.ordinal());
		payload.addFluidStack(tank.getFluid());
		payload.addInt(energyStorage.getEnergyStored());

		return payload;
	}

	/* ITilePacketHandler */
	@Override
	public void handleTilePacket(CoFHPacket payload, boolean isServer) {

		super.handleTilePacket(payload);

		renderFluid = payload.getFluidStack();

		if (renderFluid == null) {
			renderFluid = new FluidStack(FluidRegistry.LAVA, FluidContainerRegistry.BUCKET_VOLUME);
		}
	}

	/* ITileInfoPacketHandler */
	@Override
	public void handleTileInfoPacket(CoFHPacket payload, boolean isServer, EntityPlayer thePlayer) {

		switch (TEProps.PacketID.values()[payload.getByte()]) {
		case GUI:
			tank.setFluid(payload.getFluidStack());
			energyStorage.setEnergyStored(payload.getInt());
			return;
		default:
		}
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		tank.readFromNBT(nbt);

		if (!isValidFuel(tank.getFluid())) {
			tank.setFluid(null);
		}
		if (tank.getFluid() != null) {
			renderFluid = tank.getFluid();
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		tank.writeToNBT(nbt);
	}

	/* IFluidHandler */
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {

		if (resource == null || from != ForgeDirection.UNKNOWN && from.ordinal() == facing) {
			return 0;
		}
		if (isValidFuel(resource)) {
			return tank.fill(resource, doFill);
		}
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {

		if (resource == null || from != ForgeDirection.UNKNOWN && from.ordinal() == facing) {
			return null;
		}
		if (isValidFuel(resource)) {
			return tank.drain(resource.amount, doDrain);
		}
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {

		return tank.drain(maxDrain, doDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {

		return from.ordinal() != facing;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {

		return from.ordinal() != facing;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {

		return new FluidTankInfo[] { tank.getInfo() };
	}

}
