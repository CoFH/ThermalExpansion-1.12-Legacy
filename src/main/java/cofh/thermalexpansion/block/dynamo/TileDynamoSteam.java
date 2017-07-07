package cofh.thermalexpansion.block.dynamo;

import cofh.core.fluid.FluidTankCore;
import cofh.core.init.CoreProps;
import cofh.core.network.PacketCoFHBase;
import cofh.core.render.TextureHelper;
import cofh.core.util.helpers.AugmentHelper;
import cofh.core.util.helpers.FluidHelper;
import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.dynamo.BlockDynamo.Type;
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

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashSet;

public class TileDynamoSteam extends TileDynamoBase {

	private static final int TYPE = Type.STEAM.getMetadata();
	public static int basePower = 40;

	public static void initialize() {

		VALID_AUGMENTS[TYPE] = new HashSet<>();
		VALID_AUGMENTS[TYPE].add(TEProps.DYNAMO_STEAM_TURBINE);
		VALID_AUGMENTS[TYPE].add(TEProps.DYNAMO_STEAM_BOILER);

		GameRegistry.registerTileEntity(TileDynamoSteam.class, "thermalexpansion:dynamo_steam");

		config();
	}

	public static void config() {

		String category = "Dynamo.Steam";
		BlockDynamo.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);

		DEFAULT_ENERGY_CONFIG[TYPE] = new EnergyConfig();
		DEFAULT_ENERGY_CONFIG[TYPE].setDefaultParams(basePower);
	}

	private static final int STEAM_AMOUNT = 250;
	private static final int STEAM_RF = 40000;
	private static final int STEAM_HIGH = TEProps.MAX_FLUID_SMALL * 3 / 4;

	private FluidTankCore steamTank = new FluidTankCore(TEProps.MAX_FLUID_SMALL);
	private FluidTankCore waterTank = new FluidTankCore(TEProps.MAX_FLUID_SMALL);

	private int waterRF;
	private int currentFuelRF = 0;

	/* AUGMENTS */
	protected boolean augmentTurbine;
	protected boolean augmentBoiler;

	public TileDynamoSteam() {

		super();
		inventory = new ItemStack[1];
		Arrays.fill(inventory, ItemStack.EMPTY);
		steamTank.setLock(TFFluids.fluidSteam);
		waterTank.setLock(FluidRegistry.WATER);
	}

	@Override
	public int getType() {

		return TYPE;
	}

	protected boolean canStart() {

		if (augmentTurbine) {
			return steamTank.getFluidAmount() > STEAM_HIGH;
		}
		if (augmentBoiler) {
			return (waterRF > 0 || waterTank.getFluidAmount() > STEAM_AMOUNT) && (fuelRF > 0 || SteamManager.getFuelEnergy(inventory[0]) > 0);
		}
		return steamTank.getFluidAmount() > STEAM_HIGH || (waterRF > 0 || waterTank.getFluidAmount() > 200) && (fuelRF > 0 || SteamManager.getFuelEnergy(inventory[0]) > 0);
	}

	@Override
	protected boolean canFinish() {

		if (augmentBoiler) {
			return fuelRF <= 0 || waterRF <= 0;
		}
		if (augmentTurbine) {
			return steamTank.getFluidAmount() <= STEAM_HIGH;
		}
		return fuelRF <= 0 || waterRF <= 0;
	}

	@Override
	protected void processStart() {

		if (augmentTurbine) {
			return;
		}
		if (fuelRF <= 0) {
			currentFuelRF = SteamManager.getFuelEnergy(inventory[0]) * energyMod / ENERGY_BASE;
			fuelRF += currentFuelRF;
			inventory[0] = ItemHelper.consumeItem(inventory[0]);
		}
		if (waterRF <= 0) {
			waterRF += STEAM_RF;
			waterTank.modifyFluidStored(-STEAM_AMOUNT);
		}
	}

	@Override
	protected int processTick() {

		int energy;
		if (augmentBoiler) {
			energy = energyConfig.maxPower * 2;
			if (steamTank.getFluidAmount() > STEAM_HIGH) {
				transferSteam();
			}
			if (fuelRF > 0) {
				fuelRF -= energy;
				waterRF -= energy;
				steamTank.modifyFluidStored(energy * 2);
			}
		} else {
			energy = calcEnergy();
			if (steamTank.getFluidAmount() > STEAM_HIGH) {
				energyStorage.modifyEnergyStored(energy);
				steamTank.modifyFluidStored(-energy / 2);
			}
			if (fuelRF > 0) {
				fuelRF -= energy;
				waterRF -= energy;
				steamTank.modifyFluidStored(energy);
			}
			transferEnergy();
		}
		return energy;
	}

	protected void transferSteam() {

		steamTank.modifyFluidStored(-FluidHelper.insertFluidIntoAdjacentFluidHandler(world, pos, EnumFacing.values()[facing], new FluidStack(steamTank.getFluid(), energyConfig.maxPower * 2), true));
	}

	@Override
	protected void processIdle() {

		steamTank.modifyFluidStored(-50);
	}

	@Override
	public TextureAtlasSprite getActiveIcon() {

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

		if (currentFuelRF <= 0) {
			currentFuelRF = Math.max(fuelRF, SteamManager.DEFAULT_ENERGY);
		}
		return fuelRF * scale / currentFuelRF;
	}

	@Override
	public FluidTankCore getTank(int tankIndex) {

		if (tankIndex == 0) {
			return steamTank;
		}
		return waterTank;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		currentFuelRF = nbt.getInteger("FuelMax");
		steamTank.readFromNBT(nbt.getCompoundTag("SteamTank"));
		waterTank.readFromNBT(nbt.getCompoundTag("WaterTank"));

		if (currentFuelRF <= 0) {
			currentFuelRF = Math.max(fuelRF, SteamManager.DEFAULT_ENERGY);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("FuelMax", currentFuelRF);
		nbt.setTag("SteamTank", steamTank.writeToNBT(new NBTTagCompound()));
		nbt.setTag("WaterTank", waterTank.writeToNBT(new NBTTagCompound()));
		return nbt;
	}

	/* NETWORK METHODS */

	/* SERVER -> CLIENT */
	@Override
	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = super.getGuiPacket();

		payload.addBool(augmentTurbine);
		payload.addInt(currentFuelRF);
		payload.addFluidStack(steamTank.getFluid());
		payload.addFluidStack(waterTank.getFluid());

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketCoFHBase payload) {

		super.handleGuiPacket(payload);

		augmentTurbine = payload.getBool();
		currentFuelRF = payload.getInt();
		steamTank.setFluid(payload.getFluidStack());
		waterTank.setFluid(payload.getFluidStack());
	}

	/* HELPERS */
	@Override
	protected void preAugmentInstall() {

		super.preAugmentInstall();

		augmentTurbine = false;
		augmentBoiler = false;
	}

	@Override
	protected boolean installAugmentToSlot(int slot) {

		String id = AugmentHelper.getAugmentIdentifier(augments[slot]);

		if (!augmentTurbine && TEProps.DYNAMO_STEAM_TURBINE.equals(id)) {
			augmentTurbine = true;
			hasModeAugment = true;
			energyConfig.setDefaultParams(energyConfig.maxPower + getBasePower(this.level * 3));
			waterTank.modifyFluidStored(-waterTank.getCapacity());
			fuelRF = 0;
			waterRF = 0;
			return true;
		}
		if (!augmentBoiler && TEProps.DYNAMO_STEAM_BOILER.equals(id)) {
			augmentBoiler = true;
			hasModeAugment = true;
			energyConfig.setDefaultParams(energyConfig.maxPower + getBasePower(this.level * 3));
			energyStorage.setEnergyStored(0);
			energyMod += 50;
			return true;
		}
		return super.installAugmentToSlot(slot);
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

					return FluidTankProperties.convert(new FluidTankInfo[] { steamTank.getInfo(), waterTank.getInfo() });
				}

				@Override
				public int fill(FluidStack resource, boolean doFill) {

					if (resource == null || (from != null && from.ordinal() == facing && !augmentCoilDuct)) {
						return 0;
					}
					if (augmentTurbine && resource.getFluid() == TFFluids.fluidSteam) {
						return steamTank.fill(resource, doFill);
					}
					if (resource.getFluid() == FluidRegistry.WATER) {
						return waterTank.fill(resource, doFill);
					}
					return 0;
				}

				@Nullable
				@Override
				public FluidStack drain(FluidStack resource, boolean doDrain) {

					if (resource == null || from == null || !augmentCoilDuct && from.ordinal() == facing) {
						return null;
					}
					if (resource.getFluid() == FluidRegistry.WATER) {
						return waterTank.drain(resource.amount, doDrain);
					}
					return null;
				}

				@Nullable
				@Override
				public FluidStack drain(int maxDrain, boolean doDrain) {

					if (!augmentCoilDuct && from.ordinal() == facing) {
						return null;
					}
					return waterTank.drain(maxDrain, doDrain);
				}
			});
		}
		return super.getCapability(capability, from);
	}

}
