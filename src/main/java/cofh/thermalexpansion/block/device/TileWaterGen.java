package cofh.thermalexpansion.block.device;

import cofh.core.fluid.FluidTankCore;
import cofh.core.network.PacketCoFHBase;
import cofh.core.util.helpers.FluidHelper;
import cofh.core.util.helpers.MathHelper;
import cofh.core.util.helpers.RenderHelper;
import cofh.core.util.helpers.ServerHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.gui.client.device.GuiWaterGen;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.init.TESounds;
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
import net.minecraft.util.SoundEvent;
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

		SIDE_CONFIGS[TYPE] = new SideConfig();
		SIDE_CONFIGS[TYPE].numConfig = 2;
		SIDE_CONFIGS[TYPE].slotGroups = new int[][] { {}, {} };
		SIDE_CONFIGS[TYPE].sideTypes = new int[] { 0, 4 };
		SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 0, 1, 1, 1, 1, 1 };

		SLOT_CONFIGS[TYPE] = new SlotConfig();
		SLOT_CONFIGS[TYPE].allowInsertionSlot = new boolean[] {};
		SLOT_CONFIGS[TYPE].allowExtractionSlot = new boolean[] {};

		GameRegistry.registerTileEntity(TileWaterGen.class, "thermalexpansion:device_water_gen");

		config();
	}

	public static void config() {

		String category = "Device.WaterGen";
		BlockDevice.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);

		String comment = "Set this to TRUE to enable passive generation (less than two adjacent sources) for the Aqueous Accumulator.";
		passiveGen = ThermalExpansion.CONFIG.getConfiguration().get(category, "PassiveGeneration", false, comment).getBoolean();
	}

	private static final int TIME_CONSTANT = 40;
	private static int genRate = 25 * TIME_CONSTANT;
	private static int genRatePassive = TIME_CONSTANT;
	private static boolean passiveGen = false;

	private int adjacentSources = -1;
	private int outputTrackerFluid;
	private boolean inHell;

	private FluidTankCore tank = new FluidTankCore(TEProps.MAX_FLUID_MEDIUM);

	private int offset;

	public TileWaterGen() {

		super();
		offset = MathHelper.RANDOM.nextInt(TIME_CONSTANT);
		tank.setLock(FluidRegistry.WATER);

		hasAutoOutput = true;

		enableAutoOutput = true;
	}

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	public void onNeighborBlockChange() {

		super.onNeighborBlockChange();
		updateValidity();
	}

	@Override
	public void update() {

		if (ServerHelper.isClientWorld(world)) {
			return;
		}
		if (!timeCheckOffset()) {
			return;
		}
		transferOutputFluid();

		boolean curActive = isActive;

		if (isActive) {
			if (adjacentSources >= 2) {
				tank.modifyFluidStored(genRate * (adjacentSources - 1));
			} else {
				if (world.isRaining() && world.canSeeSky(getPos())) {
					tank.modifyFluidStored(genRate);
				} else if (passiveGen) {
					tank.modifyFluidStored(genRatePassive);
				}
			}
			if (!redstoneControlOrDisable()) {
				isActive = false;
			}
		} else if (redstoneControlOrDisable() && !inHell) {
			isActive = true;
		}
		if (adjacentSources < 0) {
			updateValidity();
		}
		updateIfChanged(curActive);
	}

	protected void updateValidity() {

		inHell = world.getBiome(pos) == Biomes.HELL;
		adjacentSources = 0;

		if (isWater(world.getBlockState(pos.down()))) {
			adjacentSources++;
		}
		if (isWater(world.getBlockState(pos.up()))) {
			adjacentSources++;
		}
		if (isWater(world.getBlockState(pos.west()))) {
			adjacentSources++;
		}
		if (isWater(world.getBlockState(pos.east()))) {
			adjacentSources++;
		}
		if (isWater(world.getBlockState(pos.north()))) {
			adjacentSources++;
		}
		if (isWater(world.getBlockState(pos.south()))) {
			adjacentSources++;
		}
	}

	protected void transferOutputFluid() {

		if (!enableAutoOutput || tank.getFluidAmount() <= 0) {
			return;
		}
		int side;
		FluidStack output = new FluidStack(tank.getFluid(), Math.min(tank.getFluidAmount(), Fluid.BUCKET_VOLUME * 2));
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

	protected static boolean isWater(IBlockState state) {

		return (state.getBlock() == Blocks.WATER || state.getBlock() == Blocks.FLOWING_WATER) && state.getValue(BlockLiquid.LEVEL) == 0;
	}

	protected boolean timeCheckOffset() {

		return (world.getTotalWorldTime() + offset) % TIME_CONSTANT == 0;
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
	public TextureAtlasSprite getTexture(int side, int pass) {

		if (pass == 0) {
			if (side == 0) {
				return TETextures.DEVICE_BOTTOM;
			} else if (side == 1) {
				return TETextures.DEVICE_TOP;
			}
			return side != facing ? TETextures.DEVICE_SIDE : isActive ? RenderHelper.getFluidTexture(FluidRegistry.WATER) : TETextures.DEVICE_FACE[TYPE];
		} else if (side < 6) {
			return side != facing ? TETextures.CONFIG[sideConfig.sideTypes[sideCache[side]]] : isActive ? TETextures.DEVICE_ACTIVE[TYPE] : TETextures.DEVICE_FACE[TYPE];
		}
		return TETextures.DEVICE_SIDE;
	}

	/* ISoundSource */
	@Override
	public SoundEvent getSoundEvent() {

		return TESounds.DEVICE_WATER_GEN;
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
					return new IFluidTankProperties[] { new FluidTankProperties(info.fluid, info.capacity, false, true) };
				}

				@Override
				public int fill(FluidStack resource, boolean doFill) {

					return 0;
				}

				@Nullable
				@Override
				public FluidStack drain(FluidStack resource, boolean doDrain) {

					if (from != null && !allowExtraction(sideConfig.sideTypes[sideCache[from.ordinal()]])) {
						return null;
					}
					return tank.drain(resource, doDrain);
				}

				@Nullable
				@Override
				public FluidStack drain(int maxDrain, boolean doDrain) {

					if (from != null && !allowExtraction(sideConfig.sideTypes[sideCache[from.ordinal()]])) {
						return null;
					}
					return tank.drain(maxDrain, doDrain);
				}
			});
		}
		return super.getCapability(capability, from);
	}

}
