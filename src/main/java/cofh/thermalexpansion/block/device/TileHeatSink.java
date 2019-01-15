package cofh.thermalexpansion.block.device;

import cofh.api.core.IAccelerable;
import cofh.core.fluid.FluidTankCore;
import cofh.core.gui.GuiContainerCore;
import cofh.core.gui.container.ContainerTileAugmentable;
import cofh.core.network.PacketBase;
import cofh.core.util.core.SideConfig;
import cofh.core.util.core.SlotConfig;
import cofh.core.util.helpers.BlockHelper;
import cofh.core.util.helpers.RenderHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.device.BlockDevice.Type;
import cofh.thermalexpansion.gui.client.device.GuiHeatSink;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.init.TETextures;
import cofh.thermalexpansion.util.managers.device.CoolantManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
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

import static cofh.core.util.core.SideConfig.*;

public class TileHeatSink extends TileDeviceBase implements ITickable {

	private static final int TYPE = Type.HEAT_SINK.getMetadata();
	public static int fluidAmount = 100;

	public static final int USE_FACTOR = 5;

	public static void initialize() {

		SIDE_CONFIGS[TYPE] = new SideConfig();
		SIDE_CONFIGS[TYPE].numConfig = 2;
		SIDE_CONFIGS[TYPE].slotGroups = new int[][] { {}, {} };
		SIDE_CONFIGS[TYPE].sideTypes = new int[] { NONE, INPUT_ALL };
		SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 0, 1, 1, 1, 1, 1 };

		SLOT_CONFIGS[TYPE] = new SlotConfig();
		SLOT_CONFIGS[TYPE].allowInsertionSlot = new boolean[] {};
		SLOT_CONFIGS[TYPE].allowExtractionSlot = new boolean[] {};

		LIGHT_VALUES[TYPE] = 3;

		GameRegistry.registerTileEntity(TileHeatSink.class, "thermalexpansion:device_heat_sink");

		config();
	}

	public static void config() {

		String category = "Device.HeatSink";
		BlockDevice.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);
	}

	private boolean cached;
	private IAccelerable[] accelerables = new IAccelerable[6];

	private FluidTankCore tank = new FluidTankCore(TEProps.MAX_FLUID_SMALL);
	private FluidStack renderFluid = new FluidStack(FluidRegistry.WATER, 0);

	private int coolantRF;
	private int coolantFactor = CoolantManager.WATER_FACTOR;

	public TileHeatSink() {

		super();
	}

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	public void onNeighborBlockChange() {

		super.onNeighborBlockChange();
		updateAdjacentHandlers();
	}

	@Override
	public void update() {

		boolean curActive = isActive;

		if (isActive) {
			if (coolantRF <= 0) {
				// get coolant info
				if (tank.getFluidAmount() >= fluidAmount) {
					String prevID = renderFluid.getFluid().getName();
					coolantRF += CoolantManager.getCoolantRF100mB(tank.getFluid());
					coolantFactor = CoolantManager.getCoolantFactor(tank.getFluid());

					if (!prevID.equals(renderFluid.getFluid().getName())) {
						sendFluidPacket();
					}
					tank.drain(fluidAmount, true);

					if (world.rand.nextInt(100) < coolantFactor) {
						updateAccelerables();
					}
				} else {
					isActive = false;
				}
			} else if (world.rand.nextInt(100) < coolantFactor) {
				updateAccelerables();
			}
			if (!redstoneControlOrDisable()) {
				isActive = false;
			}
		} else if (redstoneControlOrDisable() && canStart()) {
			isActive = true;
		}
		if (!cached) {
			updateAdjacentHandlers();
		}
		updateIfChanged(curActive);
	}

	protected void updateAdjacentHandlers() {

		for (int i = 0; i < 6; i++) {
			accelerables[i] = null;

			TileEntity tile = BlockHelper.getAdjacentTileEntity(this, i);
			if (tile instanceof IAccelerable) {
				accelerables[i] = (IAccelerable) tile;
			}
		}
		cached = true;
	}

	protected boolean canStart() {

		return coolantRF > 0 || tank.getFluidAmount() >= fluidAmount;
	}

	protected void updateAccelerables() {

		for (int i = 0; i < 6; i++) {
			if (accelerables[i] != null) {
				coolantRF -= USE_FACTOR * accelerables[i].updateAccelerable();
			}
		}
	}

	/* GUI METHODS */
	@Override
	public int getScaledSpeed(int scale) {

		return isActive ? GuiContainerCore.SPEED : 0;
	}

	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiHeatSink(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerTileAugmentable(inventory, this);
	}

	@Override
	public FluidTankCore getTank() {

		return tank;
	}

	@Override
	public FluidStack getTankFluid() {

		return tank.getFluid();
	}

	public int getCoolantRF() {

		return coolantRF;
	}

	public int getCoolantFactor() {

		return coolantFactor;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		coolantRF = nbt.getInteger("Coolant");
		coolantFactor = nbt.getInteger("CoolantFactor");
		tank.readFromNBT(nbt);

		if (!CoolantManager.isValidCoolant(tank.getFluid())) {
			tank.setFluid(null);
		}
		if (coolantFactor <= 0) {
			coolantFactor = CoolantManager.WATER_FACTOR;
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("Coolant", coolantRF);
		nbt.setInteger("CoolantFactor", coolantFactor);
		tank.writeToNBT(nbt);
		return nbt;
	}

	/* NETWORK METHODS */

	/* SERVER -> CLIENT */
	@Override
	public PacketBase getFluidPacket() {

		PacketBase payload = super.getFluidPacket();

		payload.addFluidStack(renderFluid);

		return payload;
	}

	@Override
	public PacketBase getGuiPacket() {

		PacketBase payload = super.getGuiPacket();

		payload.addInt(coolantRF);
		payload.addInt(coolantFactor);

		if (tank.getFluid() == null) {
			payload.addFluidStack(renderFluid);
		} else {
			payload.addFluidStack(tank.getFluid());
		}
		return payload;
	}

	@Override
	public PacketBase getTilePacket() {

		PacketBase payload = super.getTilePacket();

		if (tank.getFluid() == null) {
			payload.addFluidStack(renderFluid);
		} else {
			payload.addFluidStack(tank.getFluid());
		}
		return payload;
	}

	@Override
	protected void handleFluidPacket(PacketBase payload) {

		super.handleFluidPacket(payload);

		renderFluid = payload.getFluidStack();

		callBlockUpdate();
	}

	@Override
	protected void handleGuiPacket(PacketBase payload) {

		super.handleGuiPacket(payload);

		coolantRF = payload.getInt();
		coolantFactor = payload.getInt();
		tank.setFluid(payload.getFluidStack());
	}

	@Override
	@SideOnly (Side.CLIENT)
	public void handleTilePacket(PacketBase payload) {

		super.handleTilePacket(payload);

		renderFluid = payload.getFluidStack();
	}

	/* ISidedTexture */
	@Override
	@SideOnly (Side.CLIENT)
	public TextureAtlasSprite getTexture(int side, int pass) {

		if (pass == 0) {
			if (side == 0) {
				return TETextures.DEVICE_BOTTOM;
			} else if (side == 1) {
				return TETextures.DEVICE_TOP;
			}
			return side != facing ? TETextures.DEVICE_SIDE : isActive ? RenderHelper.getFluidTexture(renderFluid) : TETextures.DEVICE_FACE[TYPE];
		} else if (side < 6) {
			return side != facing ? TETextures.CONFIG[sideConfig.sideTypes[sideCache[side]]] : isActive ? TETextures.DEVICE_ACTIVE[TYPE] : TETextures.DEVICE_FACE[TYPE];
		}
		return TETextures.DEVICE_SIDE;
	}

	@Override
	public boolean hasFluidUnderlay() {

		return true;
	}

	@Override
	public FluidStack getRenderFluid() {

		return renderFluid;
	}

	@Override
	public int getColorMask(BlockRenderLayer layer, EnumFacing side) {

		return layer == BlockRenderLayer.SOLID && side.ordinal() == facing && isActive ? renderFluid.getFluid().getColor(renderFluid) << 8 | 0xFF : super.getColorMask(layer, side);
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

					FluidTankInfo info = tank.getInfo();
					return new IFluidTankProperties[] { new FluidTankProperties(info.fluid, info.capacity, true, true) };
				}

				@Override
				public int fill(FluidStack resource, boolean doFill) {

					if (CoolantManager.isValidCoolant(resource) && (from == null || allowInsertion(sideConfig.sideTypes[sideCache[from.ordinal()]]))) {
						return tank.fill(resource, doFill);
					}
					return 0;
				}

				@Nullable
				@Override
				public FluidStack drain(FluidStack resource, boolean doDrain) {

					if (from == null || allowExtraction(sideConfig.sideTypes[sideCache[from.ordinal()]])) {
						return tank.drain(resource, doDrain);
					}
					return null;
				}

				@Nullable
				@Override
				public FluidStack drain(int maxDrain, boolean doDrain) {

					if (from == null || allowExtraction(sideConfig.sideTypes[sideCache[from.ordinal()]])) {
						return tank.drain(maxDrain, doDrain);
					}
					return null;
				}
			});
		}
		return super.getCapability(capability, from);
	}

}
