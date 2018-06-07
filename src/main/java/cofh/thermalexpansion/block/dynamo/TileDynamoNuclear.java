package cofh.thermalexpansion.block.dynamo;

import cofh.core.fluid.FluidTankCore;
import cofh.core.init.CoreProps;
import cofh.core.network.PacketBase;
import cofh.core.render.TextureHelper;
import cofh.core.util.core.EnergyConfig;
import cofh.core.util.helpers.AugmentHelper;
import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.gui.client.dynamo.GuiDynamoNuclear;
import cofh.thermalexpansion.gui.container.dynamo.ContainerDynamoNuclear;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.util.managers.dynamo.NuclearManager;
import cofh.thermalfoundation.init.TFFluids;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
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
import java.util.Arrays;
import java.util.HashSet;

public class TileDynamoNuclear extends TileDynamoBase {

	protected static final EnergyConfig ENERGY_CONFIG = new EnergyConfig();
	protected static final HashSet<String> VALID_AUGMENTS = new HashSet<>();

	public static boolean enable = true;
	public static int basePower = 80;
	public static int fluidAmount = 100;

	public static void initialize() {

		VALID_AUGMENTS.add(TEProps.DYNAMO_BOILER);

		GameRegistry.registerTileEntity(TileDynamoNuclear.class, "thermalexpansion:dynamo_nuclear");

		config();
	}

	public static void config() {

		String category = "Dynamo.Nuclear";
		enable = ThermalExpansion.CONFIG.get(category, "Enable", true);

		String comment = "Adjust this value to change the Energy generation (in RF/t) for a Nuclear Dynamo. This base value will scale with block level and Augments.";
		basePower = ThermalExpansion.CONFIG.getConfiguration().getInt("BasePower", category, basePower, MIN_BASE_POWER, MAX_BASE_POWER, comment);
		ENERGY_CONFIG.setDefaultParams(basePower, smallStorage);
	}

	public static final int STEAM_RF = 25000;

	private FluidTankCore tank = new FluidTankCore(TEProps.MAX_FLUID_SMALL);

	private int waterRF;

	/* AUGMENTS */
	// protected boolean augmentTurbine;

	public TileDynamoNuclear() {

		super();
		inventory = new ItemStack[2];
		Arrays.fill(inventory, ItemStack.EMPTY);
		tank.setLock(FluidRegistry.WATER);
	}

	@Override
	protected String getTileName() {

		return "tile.thermalexpansion.dynamo.nuclear.name";
	}

	@Override
	protected EnergyConfig getEnergyConfig() {

		return ENERGY_CONFIG;
	}

	@Override
	protected HashSet<String> getValidAugments() {

		return VALID_AUGMENTS;
	}

	@Override
	protected boolean canStart() {

		return (waterRF > 0 || tank.getFluidAmount() >= fluidAmount) && (fuelRF > 0 || NuclearManager.getFuelEnergy(inventory[0]) > 0);
	}

	@Override
	protected boolean canFinish() {

		return fuelRF <= 0 || waterRF <= 0;
	}

	@Override
	protected void processStart() {

		if (fuelRF <= 0) {
			maxFuelRF = NuclearManager.getFuelEnergy(inventory[0]) * energyMod / ENERGY_BASE;
			fuelRF += maxFuelRF;
			inventory[0] = ItemHelper.consumeItem(inventory[0]);
		}
		if (waterRF <= 0) {
			waterRF += STEAM_RF;
			tank.modifyFluidStored(-fluidAmount);
		}
	}

	@Override
	protected int processTick() {

		if (augmentBoiler) {
			fuelRF -= energyConfig.maxPower;
			waterRF -= energyConfig.maxPower;
			transferSteam();
			return energyConfig.maxPower;
		}
		lastEnergy = calcEnergy();
		energyStorage.modifyEnergyStored(lastEnergy);
		fuelRF -= lastEnergy;
		waterRF -= lastEnergy;
		transferEnergy();
		return lastEnergy;
	}

	@Override
	protected void processIdle() {

		tank.modifyFluidStored(-250);
	}

	@Override
	@SideOnly (Side.CLIENT)
	public TextureAtlasSprite getBaseUnderlayTexture() {

		return TextureHelper.getTexture(TFFluids.fluidSteam.getStill());
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiDynamoNuclear(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerDynamoNuclear(inventory, this);
	}

	@Override
	public int getScaledDuration(int scale) {

		if (maxFuelRF <= 0) {
			maxFuelRF = Math.max(fuelRF, NuclearManager.DEFAULT_ENERGY);
		}
		return fuelRF * scale / maxFuelRF;
	}

	@Override
	public FluidTankCore getTank(int tankIndex) {

		return tank;
	}

	@Override
	public int getFuelEnergy(ItemStack stack) {

		return NuclearManager.getFuelEnergy(stack) * energyMod / ENERGY_BASE;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		maxFuelRF = nbt.getInteger("FuelMax");
		waterRF = nbt.getInteger("Water");
		tank.readFromNBT(nbt);

		if (maxFuelRF <= 0) {
			maxFuelRF = Math.max(fuelRF, NuclearManager.DEFAULT_ENERGY);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("FuelMax", maxFuelRF);
		nbt.setInteger("Water", waterRF);
		tank.writeToNBT(nbt);
		return nbt;
	}

	/* NETWORK METHODS */

	/* SERVER -> CLIENT */
	@Override
	public PacketBase getGuiPacket() {

		PacketBase payload = super.getGuiPacket();

		payload.addInt(maxFuelRF);
		payload.addFluidStack(tank.getFluid());

		return payload;
	}

	@Override
	public PacketBase getTilePacket() {

		PacketBase payload = super.getTilePacket();

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketBase payload) {

		super.handleGuiPacket(payload);

		maxFuelRF = payload.getInt();
		tank.setFluid(payload.getFluidStack());
	}

	@Override
	@SideOnly (Side.CLIENT)
	public void handleTilePacket(PacketBase payload) {

		super.handleTilePacket(payload);
	}

	/* HELPERS */
	@Override
	protected void preAugmentInstall() {

		super.preAugmentInstall();
	}

	@Override
	protected void postAugmentInstall() {

		super.postAugmentInstall();
	}

	@Override
	protected boolean installAugmentToSlot(int slot) {

		String id = AugmentHelper.getAugmentIdentifier(augments[slot]);

		if (!augmentBoiler && TEProps.DYNAMO_BOILER.equals(id)) {
			augmentBoiler = true;
			hasModeAugment = true;
			energyConfig.setDefaultParams(energyConfig.maxPower + 3 * getBasePower(this.level), smallStorage);
			energyStorage.setEnergyStored(0);
			energyMod -= 35;
			return true;
		}
		return super.installAugmentToSlot(slot);
	}

	/* IInventory */
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		return NuclearManager.getFuelEnergy(stack) > 0;
	}

	/* ISidedInventory */
	@Override
	public int[] getSlotsForFace(EnumFacing side) {

		return side == null || side.ordinal() != facing || augmentCoilDuct ? CoreProps.SINGLE_INVENTORY : CoreProps.EMPTY_INVENTORY;
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

					return FluidTankProperties.convert(new FluidTankInfo[] { tank.getInfo() });
				}

				@Override
				public int fill(FluidStack resource, boolean doFill) {

					if (from == null || augmentCoilDuct || from.ordinal() != facing) {
						if (resource.getFluid() == FluidRegistry.WATER) {
							return tank.fill(resource, doFill);
						}
					}
					return 0;
				}

				@Nullable
				@Override
				public FluidStack drain(FluidStack resource, boolean doDrain) {

					if (from == null || augmentCoilDuct || from.ordinal() != facing) {
						return tank.drain(resource, doDrain);
					}
					return null;
				}

				@Nullable
				@Override
				public FluidStack drain(int maxDrain, boolean doDrain) {

					if (from == null || augmentCoilDuct || from.ordinal() != facing) {
						return tank.drain(maxDrain, doDrain);
					}
					return null;
				}
			});
		}
		return super.getCapability(capability, from);
	}

}
