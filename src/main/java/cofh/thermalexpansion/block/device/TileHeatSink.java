package cofh.thermalexpansion.block.device;

import cofh.api.tileentity.IAccelerable;
import cofh.core.fluid.FluidTankCore;
import cofh.core.init.CoreProps;
import cofh.core.network.PacketCoFHBase;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.gui.client.device.GuiHeatSink;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.init.TETextures;
import cofh.thermalexpansion.util.fuels.CoolantManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
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

import javax.annotation.Nullable;

public class TileHeatSink extends TileDeviceBase implements ITickable {

	private static final int TYPE = BlockDevice.Type.HEAT_SINK.getMetadata();

	public static void initialize() {

		defaultSideConfig[TYPE] = new SideConfig();
		defaultSideConfig[TYPE].numConfig = 2;
		defaultSideConfig[TYPE].slotGroups = new int[][] { {}, {} };
		defaultSideConfig[TYPE].allowInsertionSide = new boolean[] { false, false };
		defaultSideConfig[TYPE].allowExtractionSide = new boolean[] { false, false };
		defaultSideConfig[TYPE].allowInsertionSlot = new boolean[] {};
		defaultSideConfig[TYPE].allowExtractionSlot = new boolean[] {};
		defaultSideConfig[TYPE].sideTex = new int[] { 0, 1 };
		defaultSideConfig[TYPE].defaultSides = new byte[] { 1, 1, 1, 1, 1, 1 };

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
	private int coolantFactor;
	private int offset;

	public TileHeatSink() {

		super();
		offset = MathHelper.RANDOM.nextInt(CoreProps.TIME_CONSTANT);
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

		if (ServerHelper.isClientWorld(worldObj)) {
			return;
		}
		if (!timeCheckOffset()) {
			return;
		}
		if (isActive) {
			if (coolantRF <= 0) {
				coolantFactor = 0;
				if (tank.getFluidAmount() >= 50) {
					coolantRF += CoolantManager.getCoolantRF50mB(tank.getFluid());
					coolantFactor = CoolantManager.getCoolantFactor(tank.getFluid());
					tank.drain(50, true);

					for (int i = 0; i < 6; i++) {
						if (accelerables[i] != null) {
							coolantRF -= 2000 >> coolantFactor;
							accelerables[i].updateAccelerable();
						}
					}
				}
			} else {
				for (int i = 0; i < 6; i++) {
					if (accelerables[i] != null) {
						coolantRF -= 2000 >> coolantFactor;
						accelerables[i].updateAccelerable();
					}
				}
			}
			if (!redstoneControlOrDisable()) {
				isActive = false;
			}
		} else if (redstoneControlOrDisable()) {
			isActive = true;
		}
		if (!cached) {
			updateAdjacentHandlers();
		}
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

	protected boolean timeCheckOffset() {

		return (worldObj.getTotalWorldTime() + offset) % CoreProps.TIME_CONSTANT >> coolantFactor == 0;
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiHeatSink(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerTEBase(inventory, this);
	}

	@Override
	public FluidTankCore getTank() {

		return tank;
	}

	@Override
	public FluidStack getTankFluid() {

		return tank.getFluid();
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		coolantRF = nbt.getInteger("Coolant");
		coolantFactor = nbt.getInteger("Factor");
		tank.readFromNBT(nbt);

		if (!CoolantManager.isValidCoolant(tank.getFluid())) {
			tank.setFluid(null);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("Coolant", coolantRF);
		nbt.setInteger("Factor", coolantFactor);
		tank.writeToNBT(nbt);
		return nbt;
	}

	/* NETWORK METHODS */

	/* SERVER -> CLIENT */
	@Override
	public PacketCoFHBase getFluidPacket() {

		PacketCoFHBase payload = super.getFluidPacket();

		payload.addFluidStack(renderFluid);

		return payload;
	}

	@Override
	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = super.getGuiPacket();

		if (tank.getFluid() == null) {
			payload.addFluidStack(renderFluid);
		} else {
			payload.addFluidStack(tank.getFluid());
		}
		return payload;
	}

	@Override
	public PacketCoFHBase getTilePacket() {

		PacketCoFHBase payload = super.getTilePacket();

		payload.addFluidStack(renderFluid);

		return payload;
	}

	@Override
	protected void handleFluidPacket(PacketCoFHBase payload) {

		super.handleFluidPacket(payload);

		renderFluid = payload.getFluidStack();

		callBlockUpdate();
	}

	@Override
	protected void handleGuiPacket(PacketCoFHBase payload) {

		super.handleGuiPacket(payload);

		tank.setFluid(payload.getFluidStack());
	}

	@Override
	public void handleTilePacket(PacketCoFHBase payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		renderFluid = payload.getFluidStack();
	}

	/* ISidedTexture */
	@Override
	public TextureAtlasSprite getTexture(int side, int layer, int pass) {

		if (layer == 0) {
			return side != facing ? TETextures.DEVICE_SIDE : isActive ? TETextures.DEVICE_ACTIVE[TYPE] : TETextures.DEVICE_FACE[TYPE];
		} else if (side < 6) {
			return TETextures.CONFIG[sideConfig.sideTex[sideCache[side]]];
		}
		return TETextures.DEVICE_SIDE;
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
					return new IFluidTankProperties[] { new FluidTankProperties(info.fluid, info.capacity, false, from != null && sideCache[from.ordinal()] > 0) };
				}

				@Override
				public int fill(FluidStack resource, boolean doFill) {

					if (from != null && sideCache[from.ordinal()] < 1 || !CoolantManager.isValidCoolant(resource)) {
						return 0;
					}
					return tank.fill(resource, doFill);
				}

				@Nullable
				@Override
				public FluidStack drain(FluidStack resource, boolean doDrain) {

					if (from != null && sideCache[from.ordinal()] < 1) {
						return null;
					}
					return tank.drain(resource, doDrain);
				}

				@Nullable
				@Override
				public FluidStack drain(int maxDrain, boolean doDrain) {

					if (from != null && sideCache[from.ordinal()] < 1) {
						return null;
					}
					return tank.drain(maxDrain, doDrain);
				}
			});
		}
		return super.getCapability(capability, from);
	}

}
