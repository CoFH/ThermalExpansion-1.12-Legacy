package thermalexpansion.block.dynamo;

import cofh.network.CoFHPacket;
import cpw.mods.fml.common.registry.GameRegistry;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
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

import thermalexpansion.gui.client.dynamo.GuiDynamoMagmatic;
import thermalexpansion.gui.container.ContainerTEBase;

public class TileDynamoMagmatic extends TileDynamoBase implements IFluidHandler {

	static final int TYPE = BlockDynamo.Types.MAGMATIC.ordinal();

	public static void initialize() {

		GameRegistry.registerTileEntity(TileDynamoMagmatic.class, "thermalexpansion.DynamoMagmatic");
	}

	static TMap fuels = new THashMap<Fluid, Integer>();

	FluidTank tank = new FluidTank(MAX_FLUID);
	FluidStack renderFluid = new FluidStack(FluidRegistry.LAVA, FluidContainerRegistry.BUCKET_VOLUME);

	@Override
	public int getType() {

		return TYPE;
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
			fuelRF += getFuelEnergy(tank.getFluid()) * fuelMod / 100;
			tank.drain(10, true);
		}
		int energy = calcEnergy() * energyMod;
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
	public CoFHPacket getGuiPacket() {

		CoFHPacket payload = super.getGuiPacket();
		payload.addFluidStack(tank.getFluid());
		return payload;
	}

	@Override
	protected void handleGuiPacket(CoFHPacket payload) {

		super.handleGuiPacket(payload);
		tank.setFluid(payload.getFluidStack());
	}

	/* ITilePacketHandler */
	@Override
	public void handleTilePacket(CoFHPacket payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);
		renderFluid = payload.getFluidStack();
		if (renderFluid == null) {
			renderFluid = new FluidStack(FluidRegistry.LAVA, FluidContainerRegistry.BUCKET_VOLUME);
		}
	}

	/* GUI METHODS */
	@Override
	public GuiContainer getGuiClient(InventoryPlayer inventory) {

		return new GuiDynamoMagmatic(inventory, this);
	}

	@Override
	public Container getGuiServer(InventoryPlayer inventory) {

		return new ContainerTEBase(inventory, this);
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
