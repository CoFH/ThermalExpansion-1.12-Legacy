package cofh.thermalexpansion.block.dynamo;

import cofh.core.fluid.FluidTankCore;
import cofh.core.init.CoreProps;
import cofh.core.network.PacketBase;
import cofh.core.render.TextureHelper;
import cofh.core.util.helpers.AugmentHelper;
import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.dynamo.BlockDynamo.Type;
import cofh.thermalexpansion.gui.client.dynamo.GuiDynamoReactant;
import cofh.thermalexpansion.gui.container.dynamo.ContainerDynamoReactant;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.util.managers.dynamo.ReactantManager;
import cofh.thermalexpansion.util.managers.dynamo.ReactantManager.Reaction;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
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
import java.util.Arrays;
import java.util.HashSet;

public class TileDynamoReactant extends TileDynamoBase {

	private static final int TYPE = Type.REACTANT.getMetadata();
	public static int basePower = 40;
	public static int fluidAmount = 100;

	public static void initialize() {

		VALID_AUGMENTS[TYPE] = new HashSet<>();
		VALID_AUGMENTS[TYPE].add(TEProps.DYNAMO_REACTANT_ELEMENTAL);

		GameRegistry.registerTileEntity(TileDynamoReactant.class, "thermalexpansion:dynamo_reactant");

		config();
	}

	public static void config() {

		String category = "Dynamo.Reactant";
		BlockDynamo.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);

		String comment = "Adjust this value to change the Energy generation (in RF/t) for a Reactant Dynamo. This base value will scale with block level and Augments.";
		basePower = ThermalExpansion.CONFIG.getConfiguration().getInt("BasePower", category, basePower, MIN_BASE_POWER, MAX_BASE_POWER, comment);

		ENERGY_CONFIGS[TYPE] = new EnergyConfig();
		ENERGY_CONFIGS[TYPE].setDefaultParams(basePower, smallStorage);
	}

	private FluidTankCore tank = new FluidTankCore(TEProps.MAX_FLUID_SMALL);
	private FluidStack renderFluid = new FluidStack(FluidRegistry.LAVA, Fluid.BUCKET_VOLUME);

	/* AUGMENTS */
	public boolean augmentElemental;

	public TileDynamoReactant() {

		super();
		inventory = new ItemStack[1];
		Arrays.fill(inventory, ItemStack.EMPTY);
	}

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	protected boolean canStart() {

		if (fuelRF > 0) {
			return true;
		}
		if (inventory[0] == null || tank.getFluidAmount() < fluidAmount) {
			return false;
		}
		if (augmentElemental) {
			if (!ReactantManager.validReactantElemental(inventory[0]) || !ReactantManager.validFluidElemental(tank.getFluid())) {
				return false;
			}
		}
		return ReactantManager.reactionExists(inventory[0], tank.getFluid());
	}

	@Override
	protected void processStart() {

		Reaction reaction = ReactantManager.getReaction(inventory[0], tank.getFluid());

		maxFuelRF = reaction.getEnergy() * energyMod / ENERGY_BASE;
		fuelRF += maxFuelRF;

		inventory[0] = ItemHelper.consumeItem(inventory[0]);
		tank.drain(fluidAmount, true);
	}

	@Override
	@SideOnly (Side.CLIENT)
	public TextureAtlasSprite getBaseUnderlayTexture() {

		return TextureHelper.getTexture(renderFluid.getFluid().getStill());
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiDynamoReactant(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerDynamoReactant(inventory, this);
	}

	@Override
	public int getScaledDuration(int scale) {

		if (maxFuelRF <= 0) {
			maxFuelRF = Math.max(fuelRF, ReactantManager.DEFAULT_ENERGY);
		}
		return fuelRF * scale / maxFuelRF;
	}

	@Override
	public FluidTankCore getTank(int tankIndex) {

		return tank;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		maxFuelRF = nbt.getInteger("FuelMax");
		tank.readFromNBT(nbt);

		if (maxFuelRF <= 0) {
			maxFuelRF = Math.max(fuelRF, ReactantManager.DEFAULT_ENERGY);
		}
		if (!ReactantManager.validFluid(tank.getFluid())) {
			tank.setFluid(null);
		}
		if (tank.getFluid() != null) {
			renderFluid = tank.getFluid();
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("FuelMax", maxFuelRF);
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

		payload.addFluidStack(tank.getFluid());

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

		augmentElemental = false;
	}

	@Override
	protected boolean installAugmentToSlot(int slot) {

		String id = AugmentHelper.getAugmentIdentifier(augments[slot]);

		if (!augmentElemental && TEProps.DYNAMO_REACTANT_ELEMENTAL.equals(id)) {
			augmentElemental = true;
			hasModeAugment = true;
			energyConfig.setDefaultParams(energyConfig.maxPower + 4 * getBasePower(this.level), smallStorage);
			energyMod += 25;
			return true;
		}
		return super.installAugmentToSlot(slot);
	}

	/* IInventory */
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		return augmentElemental ? ReactantManager.validReactantElemental(stack) : ReactantManager.validReactant(stack);
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

					return FluidTankProperties.convert(new FluidTankInfo[] { tank.getInfo() });
				}

				@Override
				public int fill(FluidStack resource, boolean doFill) {

					if (from == null || augmentCoilDuct || from.ordinal() != facing) {
						if (augmentElemental) {
							if (ReactantManager.validFluidElemental(resource)) {
								return tank.fill(resource, doFill);
							}
							return 0;
						} else if (ReactantManager.validFluid(resource)) {
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
