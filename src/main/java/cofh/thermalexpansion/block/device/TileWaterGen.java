package cofh.thermalexpansion.block.device;

import cofh.core.CoFHProps;
import cofh.core.network.PacketCoFHBase;
import cofh.core.util.fluid.FluidTankCore;
import cofh.lib.util.helpers.FluidHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.gui.client.device.GuiWaterGen;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.init.TETextures;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
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

public class TileWaterGen extends TileDeviceBase implements ITickable {

	private static final int TYPE = BlockDevice.Type.WATER_GEN.getMetadata();

	public static void initialize() {

		defaultSideConfig[TYPE] = new SideConfig();
		defaultSideConfig[TYPE].numConfig = 2;
		defaultSideConfig[TYPE].slotGroups = new int[][] { {}, {} };
		defaultSideConfig[TYPE].allowInsertionSide = new boolean[] { false, false };
		defaultSideConfig[TYPE].allowExtractionSide = new boolean[] { false, false };
		defaultSideConfig[TYPE].allowInsertionSlot = new boolean[] {};
		defaultSideConfig[TYPE].allowExtractionSlot = new boolean[] {};
		defaultSideConfig[TYPE].sideTex = new int[] { 0, 4 };
		defaultSideConfig[TYPE].defaultSides = new byte[] { 1, 1, 1, 1, 1, 1 };

		GameRegistry.registerTileEntity(TileWaterGen.class, "thermalexpansion:device_water_gen");

		config();
	}

	public static void config() {

		String category = "Device.WaterGen";
		BlockDevice.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);

		String comment = "Set this to TRUE to enable passive generation (less than two adjacent sources) for the Aqueous Accumulator.";
		passiveGen = ThermalExpansion.CONFIG.getConfiguration().get(category, "PassiveGeneration", false, comment).getBoolean();
	}

	private static int genRate = 50 * CoFHProps.TIME_CONSTANT;
	private static int genRatePassive = 1 * CoFHProps.TIME_CONSTANT;
	private static boolean passiveGen = false;

	private int adjacentSources = -1;
	private int outputTrackerFluid;
	private boolean inHell;

	private FluidTankCore tank = new FluidTankCore(TEProps.MAX_FLUID_SMALL);

	public TileWaterGen() {

		super();
		tank.setLock(FluidRegistry.WATER);
	}

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	public void onNeighborBlockChange() {

		super.onNeighborBlockChange();
		updateAdjacentSources();
	}

	@Override
	public void update() {

		if (ServerHelper.isClientWorld(worldObj)) {
			return;
		}
		if (!timeCheck()) {
			return;
		}
		transferOutputFluid();

		if (isActive) {
			if (adjacentSources >= 2) {
				tank.fillLocked(genRate, true);
			} else {
				if (worldObj.isRaining() && worldObj.canSeeSky(getPos())) {
					tank.fillLocked(genRate, true);
				} else if (passiveGen) {
					tank.fillLocked(genRatePassive, true);
				}
			}
			if (!redstoneControlOrDisable()) {
				isActive = false;
			}
		} else if (redstoneControlOrDisable() && !inHell) {
			isActive = true;
		}
		if (adjacentSources < 0) {
			updateAdjacentSources();
		}
	}

	protected void updateAdjacentSources() {

		inHell = worldObj.getBiome(getPos()) == Biomes.HELL;
		adjacentSources = 0;

		if (isWater(worldObj.getBlockState(getPos().down()))) {
			++adjacentSources;
		}

		if (isWater(worldObj.getBlockState(getPos().up()))) {
			++adjacentSources;
		}

		if (isWater(worldObj.getBlockState(getPos().west()))) {
			++adjacentSources;
		}

		if (isWater(worldObj.getBlockState(getPos().east()))) {
			++adjacentSources;
		}

		if (isWater(worldObj.getBlockState(getPos().north()))) {
			++adjacentSources;
		}

		if (isWater(worldObj.getBlockState(getPos().south()))) {
			++adjacentSources;
		}
	}

	protected void transferOutputFluid() {

		if (tank.getFluidAmount() <= 0) {
			return;
		}
		int side;
		FluidStack output = new FluidStack(tank.getFluid(), Math.min(tank.getFluidAmount(), Fluid.BUCKET_VOLUME));
		for (int i = outputTrackerFluid + 1; i <= outputTrackerFluid + 6; i++) {
			side = i % 6;

			if (sideCache[side] == 1) {
				int toDrain = FluidHelper.insertFluidIntoAdjacentFluidHandler(this, EnumFacing.VALUES[side], output, true);

				if (toDrain > 0) {
					tank.drain(toDrain, true);
					outputTrackerFluid = side;
					break;
				}
			}
		}
	}

	private static boolean isWater(IBlockState state) {

		if (state.getBlock() == Blocks.WATER || state.getBlock() == Blocks.FLOWING_WATER) {
			return state.getValue(BlockLiquid.LEVEL) == 0;
		}
		return false;
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiWaterGen(inventory, this);
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

		inHell = nbt.getBoolean("Hell");
		adjacentSources = nbt.getInteger("Sources");
		outputTrackerFluid = nbt.getInteger("TrackOut");
		tank.readFromNBT(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setBoolean("Hell", inHell);
		nbt.setInteger("Sources", adjacentSources);
		nbt.setInteger("TrackOut", outputTrackerFluid);
		tank.writeToNBT(nbt);
		return nbt;
	}

	/* NETWORK METHODS */
	@Override
	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = super.getGuiPacket();
		payload.addInt(tank.getFluidAmount());
		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketCoFHBase payload) {

		super.handleGuiPacket(payload);
		tank.getFluid().amount = payload.getInt();
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

					return 0;
				}

				@Nullable
				@Override
				public FluidStack drain(FluidStack resource, boolean doDrain) {

					if (from == null || sideCache[from.ordinal()] < 1) {
						return null;
					}
					if (resource == null || !resource.isFluidEqual(tank.getFluid())) {
						return null;
					}
					return tank.drain(resource, doDrain);
				}

				@Nullable
				@Override
				public FluidStack drain(int maxDrain, boolean doDrain) {

					if (from == null || sideCache[from.ordinal()] < 1) {
						return null;
					}
					return tank.drain(maxDrain, doDrain);
				}
			});
		}
		return super.getCapability(capability, from);
	}

}
