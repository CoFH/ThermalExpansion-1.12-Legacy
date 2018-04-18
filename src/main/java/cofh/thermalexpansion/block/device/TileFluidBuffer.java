package cofh.thermalexpansion.block.device;

import cofh.core.fluid.FluidTankCore;
import cofh.core.init.CoreProps;
import cofh.core.network.PacketBase;
import cofh.core.util.helpers.FluidHelper;
import cofh.core.util.helpers.MathHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.device.BlockDevice.Type;
import cofh.thermalexpansion.gui.client.device.GuiFluidBuffer;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import cofh.thermalexpansion.init.TEProps;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
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

public class TileFluidBuffer extends TileDeviceBase implements ITickable {

	private static final int TYPE = Type.FLUID_BUFFER.getMetadata();

	public static void initialize() {

		SIDE_CONFIGS[TYPE] = new SideConfig();
		SIDE_CONFIGS[TYPE].numConfig = 5;
		SIDE_CONFIGS[TYPE].slotGroups = new int[][] { {}, {}, {}, {}, {} };
		SIDE_CONFIGS[TYPE].sideTypes = new int[] { NONE, INPUT_ALL, OUTPUT_ALL, OPEN, OMNI };
		SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 2, 1, 1, 1, 1, 1 };

		SLOT_CONFIGS[TYPE] = new SlotConfig();
		SLOT_CONFIGS[TYPE].allowInsertionSlot = new boolean[] {};
		SLOT_CONFIGS[TYPE].allowExtractionSlot = new boolean[] {};

		LIGHT_VALUES[TYPE] = 5;

		GameRegistry.registerTileEntity(TileFluidBuffer.class, "thermalexpansion:device_fluid_buffer");

		config();
	}

	public static void config() {

		String category = "Device.FluidBuffer";
		BlockDevice.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);
	}

	private int inputTracker;
	private int outputTracker;

	private FluidTankCore[] tanks = new FluidTankCore[3];
	public boolean[] locks = new boolean[3];

	public int amountInput = Fluid.BUCKET_VOLUME;
	public int amountOutput = Fluid.BUCKET_VOLUME;

	public TileFluidBuffer() {

		super();

		for (int i = 0; i < tanks.length; i++) {
			tanks[i] = new FluidTankCore(TEProps.MAX_FLUID_MEDIUM);
			locks[i] = false;
		}
		hasAutoInput = true;
		hasAutoOutput = true;

		enableAutoInput = true;
		enableAutoOutput = true;
	}

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	protected void setLevelFlags() {

		level = 0;
		hasRedstoneControl = true;
	}

	@Override
	public void update() {

		boolean curActive = isActive;

		if (isActive) {
			transferOutput();
			transferInput();

			if (!redstoneControlOrDisable()) {
				isActive = false;
			}
		} else if (redstoneControlOrDisable()) {
			isActive = true;
		}
		updateIfChanged(curActive);
	}

	protected void transferInput() {

		if (!getTransferIn() || amountInput <= 0) {
			return;
		}
		int side;
		for (int i = inputTracker + 1; i <= inputTracker + 6; i++) {
			side = i % 6;
			if (isPrimaryInput(sideConfig.sideTypes[sideCache[side]]) && FluidHelper.isAdjacentFluidHandler(this, EnumFacing.VALUES[side])) {
				FluidStack input = FluidHelper.extractFluidFromAdjacentFluidHandler(this, EnumFacing.VALUES[side], amountInput, false);

				if (input != null) {
					for (int j = 0; j < tanks.length; j++) {
						if (tanks[j].getSpace() > 0) {
							int toFill = tanks[j].fill(input, true);

							if (toFill > 0) {
								FluidHelper.extractFluidFromAdjacentFluidHandler(this, EnumFacing.VALUES[side], toFill, true);
								inputTracker = side;
								break;
							}
						}
					}
				}
			}
		}
	}

	protected void transferOutput() {

		if (!getTransferOut() || amountOutput <= 0) {
			return;
		}
		int side;
		for (int i = outputTracker + 1; i <= outputTracker + 6; i++) {
			side = i % 6;
			if (isPrimaryOutput(sideConfig.sideTypes[sideCache[side]])) {
				for (int j = tanks.length - 1; j >= 0; j--) {
					if (tanks[j].getFluidAmount() > 0) {
						FluidStack output = new FluidStack(tanks[j].getFluid(), Math.min(tanks[j].getFluidAmount(), amountOutput));
						int toDrain = FluidHelper.insertFluidIntoAdjacentFluidHandler(this, EnumFacing.VALUES[side], output, true);

						if (toDrain > 0) {
							tanks[j].drain(toDrain, true);
							outputTracker = side;
							break;
						}
					}
				}
			}
		}
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiFluidBuffer(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerTEBase(inventory, this);
	}

	public FluidTankCore getTank(int tankIndex) {

		return tanks[tankIndex % tanks.length];
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		inputTracker = nbt.getInteger("TrackIn");
		outputTracker = nbt.getInteger("TrackOut");

		amountInput = MathHelper.clamp(nbt.getInteger("AmountIn"), 0, 8000);
		amountOutput = MathHelper.clamp(nbt.getInteger("AmountOut"), 0, 8000);

		for (int i = 0; i < tanks.length; i++) {
			tanks[i].readFromNBT(nbt.getCompoundTag("Tank" + i));
			locks[i] = tanks[i].isLocked();
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("TrackIn", inputTracker);
		nbt.setInteger("TrackOut", outputTracker);

		nbt.setInteger("AmountIn", amountInput);
		nbt.setInteger("AmountOut", amountOutput);

		for (int i = 0; i < tanks.length; i++) {
			nbt.setTag("Tank" + i, tanks[i].writeToNBT(new NBTTagCompound()));
		}
		return nbt;
	}

	/* NETWORK METHODS */

	/* CLIENT -> SERVER */
	@Override
	public PacketBase getModePacket() {

		PacketBase payload = super.getModePacket();

		payload.addInt(MathHelper.clamp(amountInput, 0, 8000));
		payload.addInt(MathHelper.clamp(amountOutput, 0, 8000));

		for (boolean lock : locks) {
			payload.addBool(lock);
		}
		return payload;
	}

	@Override
	protected void handleModePacket(PacketBase payload) {

		super.handleModePacket(payload);

		amountInput = payload.getInt();
		amountOutput = payload.getInt();

		for (int i = 0; i < locks.length; i++) {
			locks[i] = payload.getBool();

			if (locks[i]) {
				tanks[i].setLocked();
			} else {
				tanks[i].clearLocked();
			}
		}
	}

	/* SERVER -> CLIENT */
	@Override
	public PacketBase getGuiPacket() {

		PacketBase payload = super.getGuiPacket();

		payload.addInt(amountInput);
		payload.addInt(amountOutput);

		for (FluidTankCore tank : tanks) {
			payload.addFluidStack(tank.getFluid());
		}
		for (boolean lock : locks) {
			payload.addBool(lock);
		}
		return payload;
	}

	@Override
	public PacketBase getTilePacket() {

		PacketBase payload = super.getTilePacket();

		payload.addInt(amountInput);
		payload.addInt(amountOutput);

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketBase payload) {

		super.handleGuiPacket(payload);

		amountInput = payload.getInt();
		amountOutput = payload.getInt();

		for (FluidTankCore tank : tanks) {
			tank.setFluid(payload.getFluidStack());
		}
		for (int i = 0; i < locks.length; i++) {
			locks[i] = payload.getBool();
		}
	}

	@Override
	@SideOnly (Side.CLIENT)
	public void handleTilePacket(PacketBase payload) {

		super.handleTilePacket(payload);

		amountInput = payload.getInt();
		amountOutput = payload.getInt();
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

					FluidTankProperties[] properties = new FluidTankProperties[tanks.length];

					for (int i = 0; i < tanks.length; i++) {
						FluidTankInfo info = tanks[i].getInfo();
						properties[i] = new FluidTankProperties(info.fluid, info.capacity);
					}
					return properties;
				}

				@Override
				public int fill(FluidStack resource, boolean doFill) {

					if (from == null || allowInsertion(sideConfig.sideTypes[sideCache[from.ordinal()]])) {
						if (resource == null) {
							return 0;
						}
						for (int j = 0; j < tanks.length && tanks[j].getSpace() > 0; j++) {
							int toFill = tanks[j].fill(resource, doFill);
							if (toFill > 0) {
								return toFill;
							}
						}
					}
					return 0;
				}

				@Nullable
				@Override
				public FluidStack drain(FluidStack resource, boolean doDrain) {

					if (from == null || allowExtraction(sideConfig.sideTypes[sideCache[from.ordinal()]])) {
						if (resource == null) {
							return null;
						}
						for (int j = tanks.length - 1; j >= 0 && tanks[j].getFluidAmount() > 0; j--) {
							FluidStack toDrain = tanks[j].drain(resource, doDrain);
							if (toDrain != null) {
								return toDrain;
							}
						}
					}
					return null;
				}

				@Nullable
				@Override
				public FluidStack drain(int maxDrain, boolean doDrain) {

					if (from == null || allowExtraction(sideConfig.sideTypes[sideCache[from.ordinal()]])) {
						if (maxDrain <= 0) {
							return null;
						}
						for (int j = tanks.length - 1; j >= 0 && tanks[j].getFluidAmount() > 0; j--) {
							FluidStack toDrain = tanks[j].drain(maxDrain, doDrain);
							if (toDrain != null) {
								return toDrain;
							}
						}
					}
					return null;
				}
			});
		}
		return super.getCapability(capability, from);
	}

}
