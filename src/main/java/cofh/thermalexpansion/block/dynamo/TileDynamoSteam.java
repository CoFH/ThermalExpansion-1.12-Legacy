package cofh.thermalexpansion.block.dynamo;

import cofh.core.fluid.FluidTankCore;
import cofh.core.init.CoreProps;
import cofh.core.network.PacketBase;
import cofh.core.render.TextureHelper;
import cofh.core.util.core.EnergyConfig;
import cofh.core.util.helpers.AugmentHelper;
import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.gui.client.dynamo.GuiDynamoSteam;
import cofh.thermalexpansion.gui.container.dynamo.ContainerDynamoSteam;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.util.managers.dynamo.SteamManager;
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

public class TileDynamoSteam extends TileDynamoBase {

	protected static final EnergyConfig ENERGY_CONFIG = new EnergyConfig();
	protected static final HashSet<String> VALID_AUGMENTS = new HashSet<>();

	public static boolean enable = true;
	public static int basePower = 40;
	public static int fluidAmount = 100;

	public static void initialize() {

		VALID_AUGMENTS.addAll(VALID_AUGMENTS_BASE);

		VALID_AUGMENTS.add(TEProps.DYNAMO_BOILER);
		VALID_AUGMENTS.add(TEProps.DYNAMO_STEAM_TURBINE);

		GameRegistry.registerTileEntity(TileDynamoSteam.class, "thermalexpansion:dynamo_steam");

		config();
	}

	public static void config() {

		String category = "Dynamo.Steam";
		enable = ThermalExpansion.CONFIG.get(category, "Enable", true);

		String comment = "Adjust this value to change the Energy generation (in RF/t) for a Steam Dynamo. This base value will scale with block level and Augments.";
		basePower = ThermalExpansion.CONFIG.getConfiguration().getInt("BasePower", category, basePower, MIN_BASE_POWER, MAX_BASE_POWER, comment);
		ENERGY_CONFIG.setDefaultParams(basePower, smallStorage);
	}

	public static final int STEAM_RF = 25000;
	public static final int STEAM_MINIMUM = TEProps.MAX_FLUID_SMALL / 2;

	private FluidTankCore tank = new FluidTankCore(TEProps.MAX_FLUID_SMALL);

	private int waterRF;

	/* AUGMENTS */
	protected boolean augmentTurbine;

	public TileDynamoSteam() {

		super();
		inventory = new ItemStack[1];
		Arrays.fill(inventory, ItemStack.EMPTY);
		tank.setLock(FluidRegistry.WATER);
	}

	@Override
	protected String getTileName() {

		return "tile.thermalexpansion.dynamo.steam.name";
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

		if (augmentTurbine) {
			return tank.getFluidAmount() > STEAM_MINIMUM;
		}
		return (waterRF > 0 || tank.getFluidAmount() >= fluidAmount) && (fuelRF > 0 || SteamManager.getFuelEnergy(inventory[0]) > 0);
	}

	@Override
	protected boolean canFinish() {

		if (augmentTurbine) {
			return tank.getFluidAmount() <= STEAM_MINIMUM || !redstoneControlOrDisable();
		}
		return fuelRF <= 0 || waterRF <= 0;
	}

	@Override
	protected void processStart() {

		if (augmentTurbine) {
			return;
		}
		if (fuelRF <= 0) {
			maxFuelRF = SteamManager.getFuelEnergy(inventory[0]) * energyMod / ENERGY_BASE;
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
		if (augmentTurbine) {
			lastEnergy = Math.min(calcEnergy(), 2 * (tank.getFluidAmount() - STEAM_MINIMUM));
			energyStorage.modifyEnergyStored(lastEnergy);
			tank.modifyFluidStored(-lastEnergy / 2);
			transferEnergy();
			return lastEnergy;
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

		if (augmentTurbine) {
			tank.modifyFluidStored(-250);
		}
	}

	@Override
	@SideOnly (Side.CLIENT)
	public TextureAtlasSprite getBaseUnderlayTexture() {

		return TextureHelper.getTexture(TFFluids.fluidSteam.getStill());
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiDynamoSteam(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerDynamoSteam(inventory, this);
	}

	@Override
	public int getScaledDuration(int scale) {

		if (maxFuelRF <= 0) {
			maxFuelRF = Math.max(fuelRF, SteamManager.DEFAULT_ENERGY);
		}
		return fuelRF * scale / maxFuelRF;
	}

	@Override
	public FluidTankCore getTank(int tankIndex) {

		return tank;
	}

	@Override
	public boolean showSteamTab() {

		return augmentBoiler || augmentTurbine;
	}

	@Override
	public int getFuelEnergy(ItemStack stack) {

		return (augmentTurbine ? 0 : SteamManager.getFuelEnergy(stack)) * energyMod / ENERGY_BASE;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		maxFuelRF = nbt.getInteger("FuelMax");
		waterRF = nbt.getInteger("Water");
		tank.readFromNBT(nbt);

		if (maxFuelRF <= 0) {
			maxFuelRF = Math.max(fuelRF, SteamManager.DEFAULT_ENERGY);
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

		payload.addBool(augmentTurbine);
		payload.addInt(maxFuelRF);
		payload.addFluidStack(tank.getFluid());

		return payload;
	}

	@Override
	public PacketBase getTilePacket() {

		PacketBase payload = super.getTilePacket();

		payload.addBool(augmentTurbine);

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketBase payload) {

		super.handleGuiPacket(payload);

		augmentTurbine = payload.getBool();
		maxFuelRF = payload.getInt();
		tank.setFluid(payload.getFluidStack());
	}

	@Override
	@SideOnly (Side.CLIENT)
	public void handleTilePacket(PacketBase payload) {

		super.handleTilePacket(payload);

		augmentTurbine = payload.getBool();
	}

	/* HELPERS */
	@Override
	protected void preAugmentInstall() {

		super.preAugmentInstall();

		augmentTurbine = false;

		tank.clearLocked();
	}

	@Override
	protected void postAugmentInstall() {

		super.postAugmentInstall();

		if (augmentTurbine) {
			tank.setLock(TFFluids.fluidSteam);
		} else {
			tank.setLock(FluidRegistry.WATER);
		}
	}

	@Override
	protected boolean installAugmentToSlot(int slot) {

		String id = AugmentHelper.getAugmentIdentifier(augments[slot]);

		if (!augmentBoiler && TEProps.DYNAMO_BOILER.equals(id)) {
			augmentBoiler = true;
			hasModeAugment = true;
			energyConfig.setDefaultParams(energyConfig.maxPower + 3 * getBasePower(this.level), smallStorage);
			energyStorage.setEnergyStored(0);
			energyMod += 15;
			return true;
		}
		if (!augmentTurbine && TEProps.DYNAMO_STEAM_TURBINE.equals(id)) {
			augmentTurbine = true;
			hasModeAugment = true;
			energyConfig.setDefaultParams(energyConfig.maxPower + 3 * getBasePower(this.level), smallStorage);
			fuelRF = 0;
			waterRF = 0;
			return true;
		}
		return super.installAugmentToSlot(slot);
	}

	/* ISteamInfo */
	@Override
	public int getInfoSteamPerTick() {

		if (!isActive) {
			return 0;
		}
		if (augmentTurbine) {
			return lastEnergy / 2;
		}
		return augmentBoiler ? energyConfig.maxPower : lastEnergy;
	}

	@Override
	public int getInfoMaxSteamPerTick() {

		return augmentTurbine ? energyConfig.maxPower / 2 : energyConfig.maxPower;
	}

	/* IInventory */
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		return !augmentTurbine && SteamManager.getFuelEnergy(stack) > 0;
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
						if (augmentTurbine) {
							if (resource.getFluid() == TFFluids.fluidSteam) {
								return tank.fill(resource, doFill);
							}
							return 0;
						}
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
