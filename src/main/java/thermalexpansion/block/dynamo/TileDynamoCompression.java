package thermalexpansion.block.dynamo;

import cofh.network.PacketCoFHBase;
import cofh.util.MathHelper;
import cofh.util.fluid.FluidTankAdv;
import cpw.mods.fml.common.registry.GameRegistry;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.gui.client.dynamo.GuiDynamoCompression;
import thermalexpansion.gui.container.ContainerTEBase;

public class TileDynamoCompression extends TileDynamoBase implements IFluidHandler {

	static final int TYPE = BlockDynamo.Types.COMPRESSION.ordinal();

	public static void initialize() {

		int maxPower = MathHelper.clampI(ThermalExpansion.config.get("block.tweak", "Dynamo.Compression.BasePower", 80), 10, 160);
		ThermalExpansion.config.set("block.tweak", "Dynamo.Compression.BasePower", maxPower);
		maxPower /= 10;
		maxPower *= 10;
		defaultEnergyConfig[TYPE] = new EnergyConfig();
		defaultEnergyConfig[TYPE].setParamsDefault(maxPower);

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

		if (fluid == null || cooling < 10000) {
			return false;
		}
		coolants.put(fluid, cooling / 20);
		return true;
	}

	public static boolean registerFuel(Fluid fluid, int energy) {

		if (fluid == null || energy < 10000) {
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
			fuelRF = getFuelEnergy(fuelTank.getFluid()) * fuelMod / FUEL_MOD;
			fuelTank.drain(50, true);
		}
		if (coolantRF <= 0) {
			coolantRF = getCoolantEnergy(coolantTank.getFluid()) * fuelMod / FUEL_MOD;
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
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiDynamoCompression(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

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
	public PacketCoFHBase getPacket() {

		PacketCoFHBase payload = super.getPacket();

		payload.addFluidStack(fuelTank.getFluid());

		return payload;
	}

	@Override
	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = super.getGuiPacket();

		payload.addFluidStack(fuelTank.getFluid());
		payload.addFluidStack(coolantTank.getFluid());

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketCoFHBase payload) {

		super.handleGuiPacket(payload);

		fuelTank.setFluid(payload.getFluidStack());
		coolantTank.setFluid(payload.getFluidStack());
	}

	/* ITilePacketHandler */
	@Override
	public void handleTilePacket(PacketCoFHBase payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		renderFluid = payload.getFluidStack();
		if (renderFluid == null) {
			renderFluid = new FluidStack(FluidRegistry.LAVA, FluidContainerRegistry.BUCKET_VOLUME);
		}
	}

	/* IFluidHandler */
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {

		if (resource == null || from.ordinal() == facing && !augmentCoilDuct) {
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

		if (resource == null || !augmentCoilDuct) {
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

		if (!augmentCoilDuct) {
			return null;
		}
		return fuelTank.drain(maxDrain, doDrain);
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {

		return new FluidTankInfo[] { fuelTank.getInfo(), coolantTank.getInfo() };
	}

}
