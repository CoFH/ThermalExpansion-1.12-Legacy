package cofh.thermalexpansion.block.dynamo;

import codechicken.lib.texture.TextureUtils;
import cofh.core.fluid.FluidTankCore;
import cofh.core.init.CoreProps;
import cofh.core.network.PacketCoFHBase;
import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.gui.client.dynamo.GuiDynamoReactant;
import cofh.thermalexpansion.gui.container.dynamo.ContainerDynamoReactant;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.util.fuels.ReactantManager;
import cofh.thermalexpansion.util.fuels.ReactantManager.Reaction;
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

import javax.annotation.Nullable;
import java.util.ArrayList;

public class TileDynamoReactant extends TileDynamoBase {

	private static final int TYPE = BlockDynamo.Type.REACTANT.getMetadata();
	public static int basePower = 40;
	public static int fluidAmount = 100;

	public static void initialize() {

		validAugments[TYPE] = new ArrayList<>();

		GameRegistry.registerTileEntity(TileDynamoReactant.class, "thermalexpansion.dynamo_reactant");

		config();
	}

	public static void config() {

		String category = "Dynamo.Reactant";
		BlockDynamo.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);

		defaultEnergyConfig[TYPE] = new EnergyConfig();
		defaultEnergyConfig[TYPE].setDefaultParams(basePower);
	}

	private FluidTankCore tank = new FluidTankCore(TEProps.MAX_FLUID_SMALL);
	private FluidStack renderFluid = new FluidStack(FluidRegistry.LAVA, Fluid.BUCKET_VOLUME);

	private int currentFuelRF = 0;

	public TileDynamoReactant() {

		super();
		inventory = new ItemStack[1];
	}

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	protected boolean canStart() {

		return fuelRF > 0 || tank.getFluidAmount() >= fluidAmount && inventory[0] != null && ReactantManager.reactionExists(inventory[0], tank.getFluid());
	}

	@Override
	protected void processStart() {

		Reaction reaction = ReactantManager.getReaction(inventory[0], tank.getFluid());

		fuelRF += reaction.getEnergy();

		inventory[0] = ItemHelper.consumeItem(inventory[0]);
		tank.drain(fluidAmount, true);
	}

	@Override
	public TextureAtlasSprite getActiveIcon() {

		return TextureUtils.getTexture(renderFluid.getFluid().getStill());
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

		if (currentFuelRF <= 0) {
			currentFuelRF = Math.max(fuelRF, ReactantManager.DEFAULT_ENERGY);
		}
		return fuelRF * scale / currentFuelRF;
	}

	@Override
	public FluidTankCore getTank(int tankIndex) {

		return tank;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		currentFuelRF = nbt.getInteger("FuelMax");
		tank.readFromNBT(nbt);

		if (currentFuelRF <= 0) {
			currentFuelRF = Math.max(fuelRF, ReactantManager.DEFAULT_ENERGY);
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

		nbt.setInteger("FuelMax", currentFuelRF);
		tank.writeToNBT(nbt);
		return nbt;
	}

	/* NETWORK METHODS */

	/* SERVER -> CLIENT */
	@Override
	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = super.getGuiPacket();

		payload.addInt(currentFuelRF);
		payload.addFluidStack(tank.getFluid());

		return payload;
	}

	@Override
	public PacketCoFHBase getTilePacket() {

		PacketCoFHBase payload = super.getTilePacket();

		payload.addFluidStack(tank.getFluid());

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketCoFHBase payload) {

		super.handleGuiPacket(payload);

		currentFuelRF = payload.getInt();
		tank.setFluid(payload.getFluidStack());
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

	/* IInventory */
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		return ReactantManager.validReactant(stack);
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

					if (resource == null || (from != null && from.ordinal() == facing && !augmentCoilDuct)) {
						return 0;
					}
					if (ReactantManager.validFluid(resource)) {
						return tank.fill(resource, doFill);
					}
					return 0;
				}

				@Nullable
				@Override
				public FluidStack drain(FluidStack resource, boolean doDrain) {

					if (resource == null || !augmentCoilDuct && from.ordinal() == facing) {
						return null;
					}
					if (resource.equals(tank.getFluid())) {
						return tank.drain(resource.amount, doDrain);
					}
					return null;
				}

				@Nullable
				@Override
				public FluidStack drain(int maxDrain, boolean doDrain) {

					if (!augmentCoilDuct && from.ordinal() == facing) {
						return null;
					}
					return tank.drain(maxDrain, doDrain);
				}
			});
		}
		return super.getCapability(capability, from);
	}

}
