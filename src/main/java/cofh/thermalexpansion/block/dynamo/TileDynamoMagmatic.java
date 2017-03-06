package cofh.thermalexpansion.block.dynamo;

import codechicken.lib.texture.TextureUtils;
import cofh.core.fluid.FluidTankCore;
import cofh.core.network.PacketCoFHBase;
import cofh.lib.util.helpers.AugmentHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.gui.client.dynamo.GuiDynamoMagmatic;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.util.fuels.CoolantManager;
import com.google.common.collect.ImmutableSet;
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
import java.util.ArrayList;
import java.util.Set;

public class TileDynamoMagmatic extends TileDynamoBase {

	private static final int TYPE = BlockDynamo.Type.MAGMATIC.getMetadata();
	public static int fluidAmount = 100;

	public static void initialize() {

		validAugments[TYPE] = new ArrayList<>();
		validAugments[TYPE].add(TEProps.DYNAMO_MAGMATIC_COOLANT);

		GameRegistry.registerTileEntity(TileDynamoMagmatic.class, "thermalexpansion.dynamo_magmatic");

		config();
	}

	public static void config() {

		String category = "Dynamo.Magmatic";
		BlockDynamo.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);

		defaultEnergyConfig[TYPE] = new EnergyConfig();
		defaultEnergyConfig[TYPE].setDefaultParams(40);
	}

	private FluidTankCore fuelTank = new FluidTankCore(TEProps.MAX_FLUID_SMALL);
	private FluidTankCore coolantTank = new FluidTankCore(TEProps.MAX_FLUID_SMALL);
	private FluidStack renderFluid = new FluidStack(FluidRegistry.LAVA, Fluid.BUCKET_VOLUME);

	private int coolantRF;

	/* AUGMENTS */
	protected boolean augmentCoolant;
	protected boolean flagCoolant;

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	public int getLightValue() {

		return isActive ? 14 : 0;
	}

	@Override
	protected boolean canStart() {

		if (augmentCoolant) {
			return (fuelRF > 0 || fuelTank.getFluidAmount() >= fluidAmount) && (coolantRF > 0 || coolantTank.getFluidAmount() >= fluidAmount);
		}
		return fuelTank.getFluidAmount() >= fluidAmount;
	}

	@Override
	protected void processStart() {

		if (augmentCoolant) {
			if (fuelRF <= 0) {
				fuelRF += getFuelEnergy100mB(fuelTank.getFluid()) * energyMod / ENERGY_BASE;
				fuelTank.drain(fluidAmount, true);
			}
			if (coolantRF <= 0) {
				coolantRF += CoolantManager.getCoolantRF100mB(coolantTank.getFluid());
				coolantTank.drain(fluidAmount, true);
			}
			return;
		}
		fuelRF += getFuelEnergy100mB(fuelTank.getFluid()) * energyMod / ENERGY_BASE;
		fuelTank.drain(fluidAmount, true);
	}

	@Override
	public TextureAtlasSprite getActiveIcon() {

		return TextureUtils.getTexture(renderFluid.getFluid().getStill(renderFluid));
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiDynamoMagmatic(inventory, this);
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

	public boolean augmentCoolant() {

		return augmentCoolant && flagCoolant;
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
	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = super.getGuiPacket();

		payload.addBool(augmentCoolant);
		payload.addFluidStack(fuelTank.getFluid());
		payload.addFluidStack(coolantTank.getFluid());

		return payload;
	}

	@Override
	public PacketCoFHBase getTilePacket() {

		PacketCoFHBase payload = super.getTilePacket();

		payload.addFluidStack(fuelTank.getFluid());

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketCoFHBase payload) {

		super.handleGuiPacket(payload);

		augmentCoolant = payload.getBool();
		flagCoolant = augmentCoolant;
		fuelTank.setFluid(payload.getFluidStack());
		coolantTank.setFluid(payload.getFluidStack());
	}

	@Override
	public void handleTilePacket(PacketCoFHBase payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		renderFluid = payload.getFluidStack();
		if (renderFluid == null) {
			renderFluid = new FluidStack(FluidRegistry.LAVA, Fluid.BUCKET_VOLUME);
		}
	}

	/* HELPERS */
	@Override
	protected void preAugmentInstall() {

		super.preAugmentInstall();

		augmentCoolant = false;
	}

	@Override
	protected void postAugmentInstall() {

		super.postAugmentInstall();

		if (!augmentCoolant) {
			coolantTank.drain(coolantTank.getCapacity(), true);
			coolantRF = 0;
		}
	}

	@Override
	protected boolean installAugmentToSlot(int slot) {

		String id = AugmentHelper.getAugmentIdentifier(augments[slot]);

		if (!augmentCoolant && TEProps.DYNAMO_MAGMATIC_COOLANT.equals(id)) {
			augmentCoolant = true;
			hasModeAugment = true;
			energyConfig.setDefaultParams(energyConfig.maxPower + getBasePower(this.level * 2));
			energyMod += 20;
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
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(new net.minecraftforge.fluids.capability.IFluidHandler() {
				@Override
				public IFluidTankProperties[] getTankProperties() {

					return FluidTankProperties.convert(new FluidTankInfo[] { fuelTank.getInfo() });
				}

				@Override
				public int fill(FluidStack resource, boolean doFill) {

					if (resource == null || (from != null && from.ordinal() == facing && !augmentCoilDuct)) {
						return 0;
					}
					if (isValidFuel(resource)) {
						return fuelTank.fill(resource, doFill);
					}
					if (augmentCoolant && CoolantManager.isValidCoolant(resource)) {
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
					if (augmentCoolant && resource.equals(coolantTank.getFluid())) {
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
					return fuelTank.drain(maxDrain, doDrain);
				}
			});
		}
		return super.getCapability(capability, from);
	}

	/* FUEL MANAGER */
	private static TObjectIntHashMap<Fluid> fuels = new TObjectIntHashMap<>();

	public static Set<Fluid> getMagmaticFuelFluids() {

		return ImmutableSet.copyOf(fuels.keySet());
	}

	public static boolean isValidFuel(FluidStack stack) {

		return stack != null && fuels.containsKey(stack.getFluid());
	}

	public static boolean addFuel(Fluid fluid, int energy) {

		if (fluid == null || energy < 10000 || energy > 200000000) {
			return false;
		}
		fuels.put(fluid, energy);
		return true;
	}

	public static boolean removeFuel(Fluid fluid) {

		fuels.remove(fluid);
		return true;
	}

	public static int getFuelEnergy(FluidStack stack) {

		return stack == null ? 0 : fuels.get(stack.getFluid());
	}

	public static int getFuelEnergy100mB(FluidStack stack) {

		return stack == null ? 0 : fuels.get(stack.getFluid()) / 10;
	}

}
