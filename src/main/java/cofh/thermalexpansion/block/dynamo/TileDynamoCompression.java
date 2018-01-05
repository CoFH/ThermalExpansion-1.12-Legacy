package cofh.thermalexpansion.block.dynamo;

import cofh.core.fluid.FluidTankCore;
import cofh.core.network.PacketBase;
import cofh.core.render.TextureHelper;
import cofh.core.util.helpers.AugmentHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.dynamo.BlockDynamo.Type;
import cofh.thermalexpansion.gui.client.dynamo.GuiDynamoCompression;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.util.managers.device.CoolantManager;
import cofh.thermalexpansion.util.managers.dynamo.CompressionManager;
import cofh.thermalfoundation.init.TFFluids;
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
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.HashSet;

public class TileDynamoCompression extends TileDynamoBase {

	private static final int TYPE = Type.COMPRESSION.getMetadata();
	public static int basePower = 40;
	public static int fluidAmount = 100;

	public static void initialize() {

		VALID_AUGMENTS[TYPE] = new HashSet<>();
		VALID_AUGMENTS[TYPE].add(TEProps.DYNAMO_BOILER);
		VALID_AUGMENTS[TYPE].add(TEProps.DYNAMO_COMPRESSION_COOLANT);
		VALID_AUGMENTS[TYPE].add(TEProps.DYNAMO_COMPRESSION_FUEL);

		GameRegistry.registerTileEntity(TileDynamoCompression.class, "thermalexpansion:dynamo_compression");

		config();
	}

	public static void config() {

		String category = "Dynamo.Compression";
		BlockDynamo.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);

		String comment = "Adjust this value to change the Energy generation (in RF/t) for a Compression Dynamo. This base value will scale with block level and Augments.";
		basePower = ThermalExpansion.CONFIG.getConfiguration().getInt("BasePower", category, basePower, MIN_BASE_POWER, MAX_BASE_POWER, comment);

		ENERGY_CONFIGS[TYPE] = new EnergyConfig();
		ENERGY_CONFIGS[TYPE].setDefaultParams(basePower, smallStorage);
	}

	private FluidTankCore fuelTank = new FluidTankCore(TEProps.MAX_FLUID_SMALL);
	private FluidTankCore coolantTank = new FluidTankCore(TEProps.MAX_FLUID_SMALL);
	private FluidStack renderFluid = new FluidStack(FluidRegistry.LAVA, Fluid.BUCKET_VOLUME);

	private int coolantRF;

	/* AUGMENTS */
	protected boolean augmentCoolant;
	protected boolean augmentFuel;

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	protected boolean canStart() {

		return (fuelRF > 0 || fuelTank.getFluidAmount() >= fluidAmount) && (coolantRF > 0 || coolantTank.getFluidAmount() >= fluidAmount);
	}

	@Override
	protected boolean canFinish() {

		return fuelRF <= 0 || coolantRF <= 0;
	}

	@Override
	protected void processStart() {

		if (fuelRF <= 0) {
			fuelRF += CompressionManager.getFuelEnergy100mB(fuelTank.getFluid()) * energyMod / ENERGY_BASE;
			fuelTank.drain(fluidAmount, true);
		}
		if (coolantRF <= 0) {
			coolantRF += CoolantManager.getCoolantRF100mB(coolantTank.getFluid());
			coolantTank.drain(fluidAmount, true);
		}
	}

	@Override
	protected int processTick() {

		if (augmentBoiler) {
			fuelRF -= energyConfig.maxPower;
			coolantRF -= energyConfig.maxPower;
			transferSteam();

			return energyConfig.maxPower;
		}
		lastEnergy = calcEnergy();
		energyStorage.modifyEnergyStored(lastEnergy);
		fuelRF -= lastEnergy;
		coolantRF -= augmentCoolant ? 0 : lastEnergy;
		transferEnergy();

		return lastEnergy;
	}

	@Override
	public TextureAtlasSprite getBaseUnderlayTexture() {

		return TextureHelper.getTexture(renderFluid.getFluid().getStill(renderFluid));
	}

	/* GUI METHODS */
	@Override
	public int getScaledDuration(int scale) {

		return isActive ? scale : 0;
	}

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

		if (!CompressionManager.isValidFuel(fuelTank.getFluid())) {
			fuelTank.setFluid(null);
		}
		if (!CoolantManager.isValidCoolant(coolantTank.getFluid())) {
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

	/* SERVER -> CLIENT */
	@Override
	public PacketBase getGuiPacket() {

		PacketBase payload = super.getGuiPacket();

		payload.addFluidStack(fuelTank.getFluid());
		payload.addFluidStack(coolantTank.getFluid());

		return payload;
	}

	@Override
	public PacketBase getTilePacket() {

		PacketBase payload = super.getTilePacket();

		payload.addFluidStack(fuelTank.getFluid());

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketBase payload) {

		super.handleGuiPacket(payload);

		fuelTank.setFluid(payload.getFluidStack());
		coolantTank.setFluid(payload.getFluidStack());
	}

	@Override
	@SideOnly (Side.CLIENT)
	public void handleTilePacket(PacketBase payload) {

		super.handleTilePacket(payload);

		FluidStack tempRender = payload.getFluidStack();
		if (tempRender == null) {
			renderFluid = new FluidStack(FluidRegistry.LAVA, Fluid.BUCKET_VOLUME);
		} else {
			renderFluid = tempRender;
		}
	}

	/* HELPERS */
	@Override
	protected void preAugmentInstall() {

		super.preAugmentInstall();

		augmentCoolant = false;
		augmentFuel = false;

		fuelTank.clearLocked();
		coolantTank.clearLocked();
	}

	@Override
	protected void postAugmentInstall() {

		super.postAugmentInstall();

		if (augmentFuel) {
			fuelTank.setLock(TFFluids.fluidFuel);
		}
		if (augmentBoiler) {
			coolantTank.setLock(FluidRegistry.WATER);
		}
	}

	@Override
	protected boolean installAugmentToSlot(int slot) {

		String id = AugmentHelper.getAugmentIdentifier(augments[slot]);

		if (!augmentBoiler && TEProps.DYNAMO_BOILER.equals(id)) {
			augmentBoiler = true;
			hasModeAugment = true;
			energyConfig.setDefaultParams(energyConfig.maxPower + getBasePower(this.level), smallStorage);
			energyStorage.setEnergyStored(0);
			energyMod -= 40;
			return true;
		}
		if (!augmentCoolant && TEProps.DYNAMO_COMPRESSION_COOLANT.equals(id)) {
			augmentCoolant = true;
			hasModeAugment = true;
			return true;
		}
		if (!augmentFuel && TEProps.DYNAMO_COMPRESSION_FUEL.equals(id)) {
			augmentFuel = true;
			hasModeAugment = true;
			energyConfig.setDefaultParams(energyConfig.maxPower + 3 * getBasePower(this.level), smallStorage);
			energyMod += 50;
			return true;
		}
		return super.installAugmentToSlot(slot);
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

					return FluidTankProperties.convert(new FluidTankInfo[] { fuelTank.getInfo(), coolantTank.getInfo() });
				}

				@Override
				public int fill(FluidStack resource, boolean doFill) {

					if (from == null || augmentCoilDuct || from.ordinal() != facing) {
						if (CompressionManager.isValidFuel(resource)) {
							return fuelTank.fill(resource, doFill);
						}
						if (augmentBoiler && resource.getFluid() == FluidRegistry.WATER) {
							return coolantTank.fill(resource, doFill);
						} else if (CoolantManager.isValidCoolant(resource)) {
							return coolantTank.fill(resource, doFill);
						}
					}
					return 0;
				}

				@Nullable
				@Override
				public FluidStack drain(FluidStack resource, boolean doDrain) {

					if (from == null || augmentCoilDuct || from.ordinal() != facing) {
						if (resource.equals(fuelTank.getFluid())) {
							return fuelTank.drain(resource.amount, doDrain);
						}
						if (resource.equals(coolantTank.getFluid())) {
							return coolantTank.drain(resource.amount, doDrain);
						}
					}
					return null;
				}

				@Nullable
				@Override
				public FluidStack drain(int maxDrain, boolean doDrain) {

					if (from == null || augmentCoilDuct || from.ordinal() != facing) {
						FluidStack ret = fuelTank.drain(maxDrain, doDrain);
						return ret != null ? ret : coolantTank.drain(maxDrain, doDrain);
					}
					return null;
				}
			});
		}
		return super.getCapability(capability, from);
	}

}
