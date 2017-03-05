package cofh.thermalexpansion.block.dynamo;

import codechicken.lib.texture.TextureUtils;
import cofh.core.fluid.FluidTankCore;
import cofh.core.init.CoreProps;
import cofh.core.network.PacketCoFHBase;
import cofh.lib.inventory.ComparableItemStack;
import cofh.lib.util.helpers.AugmentHelper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.gui.client.dynamo.GuiDynamoSteam;
import cofh.thermalexpansion.gui.container.dynamo.ContainerDynamoSteam;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalfoundation.init.TFFluids;
import com.google.common.collect.ImmutableSet;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
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
import java.util.ArrayList;
import java.util.Set;

public class TileDynamoSteam extends TileDynamoBase {

	private static final int TYPE = BlockDynamo.Type.STEAM.getMetadata();

	public static void initialize() {

		validAugments[TYPE] = new ArrayList<>();
		validAugments[TYPE].add(TEProps.DYNAMO_STEAM_TURBINE);

		GameRegistry.registerTileEntity(TileDynamoSteam.class, "thermalexpansion.dynamo_steam");

		config();
	}

	public static void config() {

		String category = "Dynamo.Steam";
		BlockDynamo.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);

		defaultEnergyConfig[TYPE] = new EnergyConfig();
		defaultEnergyConfig[TYPE].setDefaultParams(40);
	}

	private static final int STEAM_HIGH = TEProps.MAX_FLUID_SMALL * 3 / 4;

	private FluidTankCore steamTank = new FluidTankCore(TEProps.MAX_FLUID_SMALL);
	private FluidTankCore waterTank = new FluidTankCore(TEProps.MAX_FLUID_SMALL);

	private int waterRF;
	private int currentFuelRF = DEFAULT_RF;

	/* AUGMENTS */
	protected boolean augmentTurbine;

	public TileDynamoSteam() {

		super();
		inventory = new ItemStack[1];
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
		return steamTank.getFluidAmount() > STEAM_HIGH || (waterRF > 0 || waterTank.getFluidAmount() > 50) && (fuelRF > 0 || getEnergyValue(inventory[0]) > 0);
	}

	@Override
	protected boolean canFinish() {

		return steamTank.getFluidAmount() <= STEAM_HIGH;
	}

	@Override
	protected void processStart() {

		if (augmentTurbine) {
			return;
		}
		if (fuelRF <= 0) {
			currentFuelRF = getEnergyValue(inventory[0]) * energyMod / ENERGY_BASE;
			fuelRF += currentFuelRF;
			inventory[0] = ItemHelper.consumeItem(inventory[0]);
		}
		if (waterRF <= 0) {
			waterRF += 8000;
			waterTank.modifyFluidStored(-50);
		}
	}

	@Override
	protected int processTick() {

		int energy = calcEnergy();

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

		return energy;
	}

	@Override
	protected void processIdle() {

		steamTank.modifyFluidStored(-50);
	}

	@Override
	public TextureAtlasSprite getActiveIcon() {

		return TextureUtils.getTexture(TFFluids.fluidSteam.getStill());
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
			currentFuelRF = DEFAULT_RF;
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
			currentFuelRF = DEFAULT_RF;
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
	}

	@Override
	protected boolean installAugmentToSlot(int slot) {

		String id = AugmentHelper.getAugmentIdentifier(augments[slot]);

		if (!augmentTurbine && TEProps.DYNAMO_STEAM_TURBINE.equals(id)) {
			augmentTurbine = true;
			hasModeAugment = true;
			energyConfig.setDefaultParams(energyConfig.maxPower + getBasePower(this.level * 2));
			fuelRF = 0;
			waterRF = 0;
			return true;
		}
		return super.installAugmentToSlot(slot);
	}

	/* IInventory */
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		return !augmentTurbine && getEnergyValue(stack) > 0;
	}

	/* ISidedInventory */
	@Override
	public int[] getSlotsForFace(EnumFacing side) {

		return side.ordinal() != facing || augmentCoilDuct ? CoreProps.SINGLE_INVENTORY : CoreProps.EMPTY_INVENTORY;
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

	/* FUEL MANAGER */
	private static TObjectIntHashMap<ComparableItemStack> fuels = new TObjectIntHashMap<>();

	private static int DEFAULT_RF = 48000;

	public static Set<ComparableItemStack> getOverriddenFuelStacks() {
		return ImmutableSet.copyOf(fuels.keySet());
	}

	public static boolean addFuel(ItemStack stack, int energy) {

		if (stack == null || energy < 1600 || energy > 200000000) {
			return false;
		}
		fuels.put(new ComparableItemStack(stack), energy);
		return true;
	}

	public static boolean removeFuel(ItemStack stack) {

		fuels.remove(new ComparableItemStack(stack));
		return true;
	}

	public static int getEnergyValue(ItemStack stack) {

		if (stack == null) {
			return 0;
		}
		if (stack.getItem().hasContainerItem(stack)) {
			return 0;
		}
		int energy = fuels.get(new ComparableItemStack(stack));

		return energy > 0 ? energy : TileEntityFurnace.getItemBurnTime(stack) * CoreProps.RF_PER_MJ;
	}

}
