package thermalexpansion.block.dynamo;

import cofh.network.CoFHPacket;
import cofh.util.fluid.FluidTankAdv;
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
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import thermalexpansion.gui.client.dynamo.GuiDynamoCompression;
import thermalexpansion.gui.container.ContainerTEBase;

public class TileDynamoCompression extends TileDynamoBase implements IFluidHandler {

	static final int TYPE = BlockDynamo.Types.COMPRESSION.ordinal();

	public static void initialize() {

		GameRegistry.registerTileEntity(TileDynamoCompression.class, "thermalexpansion.DynamoCompression");
	}

	static TMap fuels = new THashMap<Fluid, Integer>();
	static TMap coolants = new THashMap<Fluid, Integer>();

	FluidTankAdv fuelTank = new FluidTankAdv(MAX_FLUID);
	FluidTankAdv coolantTank = new FluidTankAdv(MAX_FLUID);

	FluidStack renderFluid = new FluidStack(FluidRegistry.LAVA, FluidContainerRegistry.BUCKET_VOLUME);
	int coolantRF;

	public static int getCoolantEnergy(FluidStack stack) {

		return stack == null ? 0 : (Integer) coolants.get(stack.getFluid());
	}

	public static int getFuelEnergy(FluidStack stack) {

		return stack == null ? 0 : (Integer) fuels.get(stack.getFluid());
	}

	public static boolean isValidCoolant(FluidStack stack) {

		return stack == null ? false : coolants.containsKey(stack.getFluid());
	}

	public static boolean isValidFuel(FluidStack stack) {

		return stack == null ? false : fuels.containsKey(stack.getFluid());
	}

	public static boolean registerCoolant(Fluid fluid, int cooling) {

		if (fluid == null || cooling <= 10000) {
			return false;
		}
		coolants.put(fluid, cooling / 20);
		return true;
	}

	public static boolean registerFuel(Fluid fluid, int energy) {

		if (fluid == null || energy <= 10000) {
			return false;
		}
		fuels.put(fluid, energy / 20);
		return true;
	}

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	protected boolean canGenerate() {

		if (fuelRF > 0) {
			return coolantRF > 0 || coolantTank.getFluidAmount() >= 50;
		}
		if (coolantRF > 0) {
			return fuelTank.getFluidAmount() >= 50;
		}
		return fuelTank.getFluidAmount() >= 50 && coolantTank.getFluidAmount() >= 50;
	}

	@Override
	protected void generate() {

		if (fuelRF <= 0) {
			fuelRF = getFuelEnergy(fuelTank.getFluid()) * fuelMod / 100;
			fuelTank.drain(50, true);
		}
		if (coolantRF <= 0) {
			coolantRF = getCoolantEnergy(coolantTank.getFluid()) * fuelMod / 100;
			coolantTank.drain(50, true);
		}
		int energy = calcEnergy() * energyMod;
		energyStorage.modifyEnergyStored(energy);
		fuelRF -= energy;
		coolantRF -= energy;
	}

	@Override
	public IIcon getActiveIcon() {

		return renderFluid.getFluid().getIcon(renderFluid);
	}

	/* GUI METHODS */
	@Override
	public GuiContainer getGuiClient(InventoryPlayer inventory) {

		return new GuiDynamoCompression(inventory, this);
	}

	@Override
	public Container getGuiServer(InventoryPlayer inventory) {

		return new ContainerTEBase(inventory, this);
	}

	@Override
	public FluidTankAdv getTank(int tankIndex) {

		if (tankIndex == 0) {
			return fuelTank;
		}
		return coolantTank;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);
		coolantRF = nbt.getInteger("Coolant");
		fuelTank.readFromNBT(nbt.getCompoundTag("FuelTank"));
		coolantTank.readFromNBT(nbt.getCompoundTag("CoolantTank"));

		if (!isValidFuel(fuelTank.getFluid())) {
			fuelTank.setFluid(null);
		}
		if (!isValidCoolant(coolantTank.getFluid())) {
			coolantTank.setFluid(null);
		}
		if (fuelTank.getFluid() != null) {
			renderFluid = fuelTank.getFluid();
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);
		nbt.setInteger("Coolant", coolantRF);
		nbt.setTag("FuelTank", fuelTank.writeToNBT(new NBTTagCompound()));
		nbt.setTag("CoolantTank", coolantTank.writeToNBT(new NBTTagCompound()));
	}

	/* NETWORK METHODS */
	@Override
	public CoFHPacket getPacket() {

		CoFHPacket payload = super.getPacket();

		payload.addFluidStack(fuelTank.getFluid());

		return payload;
	}

	@Override
	public CoFHPacket getGuiPacket() {

		CoFHPacket payload = super.getGuiPacket();

		payload.addFluidStack(fuelTank.getFluid());
		payload.addFluidStack(coolantTank.getFluid());

		return payload;
	}

	@Override
	protected void handleGuiPacket(CoFHPacket payload) {

		super.handleGuiPacket(payload);

		fuelTank.setFluid(payload.getFluidStack());
		coolantTank.setFluid(payload.getFluidStack());
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

	/* IFluidHandler */
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {

		if (resource == null || from != ForgeDirection.UNKNOWN && from.ordinal() == facing) {
			return 0;
		}
		if (isValidFuel(resource)) {
			return fuelTank.fill(resource, doFill);
		}
		if (isValidCoolant(resource)) {
			return coolantTank.fill(resource, doFill);
		}
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {

		if (resource == null || from != ForgeDirection.UNKNOWN && from.ordinal() == facing) {
			return null;
		}
		if (isValidFuel(resource)) {
			return fuelTank.drain(resource.amount, doDrain);
		}
		if (isValidCoolant(resource)) {
			return coolantTank.drain(resource.amount, doDrain);
		}
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {

		return fuelTank.drain(maxDrain, doDrain);
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

		return new FluidTankInfo[] { fuelTank.getInfo(), coolantTank.getInfo() };
	}

}
