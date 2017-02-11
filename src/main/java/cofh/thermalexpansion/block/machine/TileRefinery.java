package cofh.thermalexpansion.block.machine;

import cofh.core.fluid.FluidTankCore;
import cofh.core.network.PacketCoFHBase;
import cofh.lib.render.RenderHelper;
import cofh.lib.util.helpers.FluidHelper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.gui.client.machine.GuiRefinery;
import cofh.thermalexpansion.gui.container.machine.ContainerRefinery;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.init.TETextures;
import cofh.thermalexpansion.util.crafting.RefineryManager;
import cofh.thermalexpansion.util.crafting.RefineryManager.RecipeRefinery;
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
import java.util.ArrayList;

public class TileRefinery extends TileMachineBase {

	private static final int TYPE = BlockMachine.Type.REFINERY.getMetadata();

	public static void initialize() {

		defaultSideConfig[TYPE] = new SideConfig();
		defaultSideConfig[TYPE].numConfig = 6;
		defaultSideConfig[TYPE].slotGroups = new int[][] { {}, {}, { 0 }, {}, { 0 }, { 0 } };
		defaultSideConfig[TYPE].allowInsertionSide = new boolean[] { false, true, false, true };
		defaultSideConfig[TYPE].allowExtractionSide = new boolean[] { false, true, false, true };
		defaultSideConfig[TYPE].allowInsertionSlot = new boolean[] { true, false };
		defaultSideConfig[TYPE].allowExtractionSlot = new boolean[] { true, false };
		defaultSideConfig[TYPE].sideTex = new int[] { 0, 1, 2, 3, 4, 7 };
		defaultSideConfig[TYPE].defaultSides = new byte[] { 1, 2, 3, 3, 3, 3 };

		validAugments[TYPE] = new ArrayList<String>();

		GameRegistry.registerTileEntity(TileRefinery.class, "thermalexpansion:machine_refinery");

		config();
	}

	public static void config() {

		String category = "Machine.Refinery";
		BlockMachine.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);

		defaultEnergyConfig[TYPE] = new EnergyConfig();
		defaultEnergyConfig[TYPE].setDefaultParams(20);
	}

	private int outputTracker;
	private int outputTrackerFluid;

	private FluidTankCore inputTank = new FluidTankCore(TEProps.MAX_FLUID_SMALL);
	private FluidTankCore outputTank = new FluidTankCore(TEProps.MAX_FLUID_LARGE);
	private FluidStack renderFluid = new FluidStack(FluidRegistry.LAVA, 0);

	public TileRefinery() {

		super();
		inventory = new ItemStack[1 + 1];
	}

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	public void update() {

		if (ServerHelper.isClientWorld(worldObj)) {
			return;
		}
		transferOutputFluid();

		super.update();
	}

	@Override
	public int getLightValue() {

		return isActive ? renderFluid.getFluid().getLuminosity(renderFluid) : 0;
	}

	@Override
	protected boolean canStart() {

		if (energyStorage.getEnergyStored() <= 0) {
			return false;
		}
		RecipeRefinery recipe = RefineryManager.getRecipe(inputTank.getFluid());

		if (recipe == null) {
			return false;
		}
		if (inputTank.getFluidAmount() < recipe.getInput().amount) {
			return false;
		}
		FluidStack outputFluid = recipe.getOutputFluid();
		ItemStack outputItem = recipe.getOutputItem();

		if (!augmentSecondaryNull && outputItem != null && inventory[0] != null) {
			if (!inventory[0].isItemEqual(outputItem)) {
				return false;
			}
			if (inventory[0].stackSize + outputItem.stackSize > outputItem.getMaxStackSize()) {
				return false;
			}
		}
		return outputTank.fill(outputFluid, false) == outputFluid.amount;
	}

	@Override
	protected boolean canFinish() {

		return processRem <= 0;
	}

	@Override
	protected void processStart() {

		processMax = RefineryManager.getRecipe(inputTank.getFluid()).getEnergy() * energyMod / ENERGY_BASE;
		processRem = processMax;

		String prevID = renderFluid.getFluid().getName();
		renderFluid = inputTank.getFluid().copy();
		renderFluid.amount = 0;

		if (!prevID.equals(renderFluid.getFluid().getName())) {
			sendFluidPacket();
		}
	}

	@Override
	protected void processFinish() {

		RecipeRefinery recipe = RefineryManager.getRecipe(inputTank.getFluid());

		if (recipe == null) {
			processOff();
			return;
		}
		outputTank.fill(recipe.getOutputFluid(), true);

		ItemStack outputItem = recipe.getOutputItem();

		if (outputItem != null) {
			if (inventory[0] == null) {
				inventory[0] = ItemHelper.cloneStack(outputItem);
			} else {
				inventory[0].stackSize += outputItem.stackSize;
			}
			if (inventory[0].stackSize > inventory[0].getMaxStackSize()) {
				inventory[0].stackSize = inventory[0].getMaxStackSize();
			}
		}
		inputTank.drain(recipe.getInput().amount, true);
	}

	@Override
	protected void transferOutput() {

		if (!enableAutoOutput) {
			return;
		}
		int side;
		if (inventory[0] != null) {
			for (int i = outputTracker + 1; i <= outputTracker + 6; i++) {
				side = i % 6;
				if (sideCache[side] == 3 || sideCache[side] == 4) {
					if (transferItem(0, ITEM_TRANSFER[level], EnumFacing.VALUES[side])) {
						outputTracker = side;
						break;
					}
				}
			}
		}
	}

	private void transferOutputFluid() {

		if (!enableAutoOutput) {
			return;
		}
		if (outputTank.getFluidAmount() <= 0) {
			return;
		}
		int side;
		FluidStack output = new FluidStack(outputTank.getFluid(), Math.min(outputTank.getFluidAmount(), FLUID_TRANSFER[level]));
		for (int i = outputTrackerFluid + 1; i <= outputTrackerFluid + 6; i++) {
			side = i % 6;

			if (sideCache[side] == 2 || sideCache[side] == 4) {
				int toDrain = FluidHelper.insertFluidIntoAdjacentFluidHandler(this, EnumFacing.VALUES[side], output, true);

				if (toDrain > 0) {
					outputTank.drain(toDrain, true);
					outputTrackerFluid = side;
					break;
				}
			}
		}
	}

	protected void setLevelFlags() {

		super.setLevelFlags();

		hasAutoInput = false;
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiRefinery(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerRefinery(inventory, this);
	}

	public FluidTankCore getTank(int tankIndex) {

		if (tankIndex == 0) {
			return inputTank;
		}
		return outputTank;
	}

	public FluidStack getTankFluid(int tankIndex) {

		if (tankIndex == 0) {
			return inputTank.getFluid();
		}
		return outputTank.getFluid();
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		outputTracker = nbt.getInteger("TrackOut1");
		outputTrackerFluid = nbt.getInteger("TrackOut2");

		inputTank.readFromNBT(nbt.getCompoundTag("TankIn"));
		outputTank.readFromNBT(nbt.getCompoundTag("TankOut"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("TrackOut1", outputTracker);
		nbt.setInteger("TrackOut2", outputTrackerFluid);

		nbt.setTag("TankIn", inputTank.writeToNBT(new NBTTagCompound()));
		nbt.setTag("TankOut", outputTank.writeToNBT(new NBTTagCompound()));
		return nbt;
	}

	/* NETWORK METHODS */
	@Override
	public PacketCoFHBase getPacket() {

		PacketCoFHBase payload = super.getPacket();
		payload.addFluidStack(renderFluid);
		return payload;
	}

	@Override
	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = super.getGuiPacket();
		if (inputTank.getFluid() == null) {
			payload.addFluidStack(renderFluid);
		} else {
			payload.addFluidStack(inputTank.getFluid());
		}
		payload.addFluidStack(outputTank.getFluid());
		return payload;
	}

	@Override
	public PacketCoFHBase getFluidPacket() {

		PacketCoFHBase payload = super.getFluidPacket();
		payload.addFluidStack(renderFluid);
		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketCoFHBase payload) {

		super.handleGuiPacket(payload);
		inputTank.setFluid(payload.getFluidStack());
		outputTank.setFluid(payload.getFluidStack());
	}

	@Override
	protected void handleFluidPacket(PacketCoFHBase payload) {

		super.handleFluidPacket(payload);
		renderFluid = payload.getFluidStack();
		callBlockUpdate();
	}

	/* ITilePacketHandler */
	@Override
	public void handleTilePacket(PacketCoFHBase payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		if (!isServer) {
			renderFluid = payload.getFluidStack();
		} else {
			payload.getFluidStack();
		}
	}

	/* ISidedTexture */
	@Override
	public TextureAtlasSprite getTexture(int side, int layer, int pass) {

		if (layer == 0) {
			if (side == 0) {
				return TETextures.MACHINE_BOTTOM;
			} else if (side == 1) {
				return TETextures.MACHINE_TOP;
			}
			return side != facing ? TETextures.MACHINE_SIDE : isActive ? RenderHelper.getFluidTexture(renderFluid) : TETextures.MACHINE_FACE[getType()];
		} else if (side < 6) {
			return side != facing ? TETextures.CONFIG[sideConfig.sideTex[sideCache[side]]] : isActive ? TETextures.MACHINE_ACTIVE[getType()] : TETextures.MACHINE_FACE[getType()];
		}
		return TETextures.MACHINE_SIDE;
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

					FluidTankInfo inputInfo = inputTank.getInfo();
					FluidTankInfo outputInfo = outputTank.getInfo();
					return new IFluidTankProperties[] { new FluidTankProperties(inputInfo.fluid, inputInfo.capacity, true, false), new FluidTankProperties(outputInfo.fluid, outputInfo.capacity, true, false) };
				}

				@Override
				public int fill(FluidStack resource, boolean doFill) {

					if (from != null && sideCache[from.ordinal()] != 1) {
						return 0;
					}
					return inputTank.fill(resource, doFill);
				}

				@Nullable
				@Override
				public FluidStack drain(FluidStack resource, boolean doDrain) {

					if (from != null && (sideCache[from.ordinal()] != 2 || sideCache[from.ordinal()] != 4)) {
						return null;
					}
					if (resource == null || !resource.isFluidEqual(outputTank.getFluid())) {
						return null;
					}
					return outputTank.drain(resource.amount, doDrain);
				}

				@Nullable
				@Override
				public FluidStack drain(int maxDrain, boolean doDrain) {

					if (from != null && (sideCache[from.ordinal()] != 2 || sideCache[from.ordinal()] != 4)) {
						return null;
					}
					return outputTank.drain(maxDrain, doDrain);
				}
			});
		}
		return super.getCapability(capability, from);
	}

}
