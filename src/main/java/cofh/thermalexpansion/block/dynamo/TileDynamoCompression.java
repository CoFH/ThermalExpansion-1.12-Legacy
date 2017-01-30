package cofh.thermalexpansion.block.dynamo;

import codechicken.lib.texture.TextureUtils;
import cofh.core.network.PacketCoFHBase;
import cofh.core.util.fluid.FluidTankCore;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.gui.client.dynamo.GuiDynamoCompression;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import cofh.thermalexpansion.init.TEProps;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nullable;

public class TileDynamoCompression extends TileDynamoBase {

	private static final int TYPE = BlockDynamo.Type.COMPRESSION.getMetadata();

	public static void initialize() {

		GameRegistry.registerTileEntity(TileDynamoCompression.class, "thermalexpansion:dynamo_compression");

		config();
	}

	public static void config() {

		String category = "Dynamo.Compression";
		BlockDynamo.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);
	}

	private FluidTankCore fuelTank = new FluidTankCore(TEProps.MAX_FLUID_SMALL);
	private FluidTankCore coolantTank = new FluidTankCore(TEProps.MAX_FLUID_SMALL);
	private FluidStack renderFluid = new FluidStack(FluidRegistry.LAVA, Fluid.BUCKET_VOLUME);
	private int coolantRF;

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
	public TextureAtlasSprite getActiveIcon() {

		return TextureUtils.getTexture(renderFluid.getFluid().getStill(renderFluid));
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
	public FluidTankCore getTank(int tankIndex) {

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
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);
		nbt.setInteger("Coolant", coolantRF);
		nbt.setTag("FuelTank", fuelTank.writeToNBT(new NBTTagCompound()));
		nbt.setTag("CoolantTank", coolantTank.writeToNBT(new NBTTagCompound()));
		return nbt;
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
			renderFluid = new FluidStack(FluidRegistry.LAVA, Fluid.BUCKET_VOLUME);
		}
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing from) {

		return super.hasCapability(capability, from) || capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, final EnumFacing from) {

		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(new net.minecraftforge.fluids.capability.IFluidHandler() {
				@Override
				public IFluidTankProperties[] getTankProperties() {

					return FluidTankProperties.convert(new FluidTankInfo[] { fuelTank.getInfo(), coolantTank.getInfo() });
				}

				@Override
				public int fill(FluidStack resource, boolean doFill) {

					if (resource == null || (from != null && from.ordinal() == facing && !augmentCoilDuct)) {
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

				@Nullable
				@Override
				public FluidStack drain(FluidStack resource, boolean doDrain) {

					if (resource == null || !augmentCoilDuct && from.ordinal() == facing) {
						return null;
					}
					if (resource.equals(fuelTank.getFluid())) {
						return fuelTank.drain(resource.amount, doDrain);
					}
					if (resource.equals(coolantTank.getFluid())) {
						return coolantTank.drain(resource.amount, doDrain);
					}
					return null;
				}

				@Nullable
				@Override
				public FluidStack drain(int maxDrain, boolean doDrain) {

					if (!augmentCoilDuct && from.ordinal() == facing) {
						return null;
					}
					if (fuelTank.getFluidAmount() <= 0) {
						return coolantTank.drain(maxDrain, doDrain);
					}
					return fuelTank.drain(maxDrain, doDrain);
				}
			});
		}
		return super.getCapability(capability, from);
	}

	/* FUEL MANAGER */
	private static TObjectIntHashMap<Fluid> fuels = new TObjectIntHashMap<Fluid>();
	private static TObjectIntHashMap<Fluid> coolants = new TObjectIntHashMap<Fluid>();

	public static boolean isValidFuel(FluidStack stack) {

		return stack != null && fuels.containsKey(stack.getFluid());
	}

	public static boolean isValidCoolant(FluidStack stack) {

		return stack != null && coolants.containsKey(stack.getFluid());
	}

	public static boolean addFuel(Fluid fluid, int energy) {

		if (fluid == null || energy < 10000 || energy > 200000000) {
			return false;
		}
		fuels.put(fluid, energy / 20);
		return true;
	}

	public static boolean addCoolant(Fluid fluid, int cooling) {

		if (fluid == null || cooling < 10000 || cooling > 200000000) {
			return false;
		}
		coolants.put(fluid, cooling / 20);
		return true;
	}

	public static boolean removeFuel(Fluid fluid) {

		fuels.remove(fluid);
		return true;
	}

	public static boolean removeCoolant(Fluid fluid) {

		coolants.remove(fluid);
		return true;
	}

	public static int getFuelEnergy(FluidStack stack) {

		return stack == null ? 0 : fuels.get(stack.getFluid());
	}

	public static int getCoolantEnergy(FluidStack stack) {

		return stack == null ? 0 : coolants.get(stack.getFluid());
	}

}
