package cofh.thermalexpansion.block.device;

import cofh.core.fluid.FluidTankCore;
import cofh.core.gui.container.ContainerTileAugmentable;
import cofh.core.init.CoreProps;
import cofh.core.network.PacketBase;
import cofh.core.util.core.SideConfig;
import cofh.core.util.core.SlotConfig;
import cofh.core.util.helpers.FluidHelper;
import cofh.core.util.helpers.MathHelper;
import cofh.core.util.helpers.RenderHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.device.BlockDevice.Type;
import cofh.thermalexpansion.gui.client.device.GuiWaterGen;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.init.TESounds;
import cofh.thermalexpansion.init.TETextures;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.BiomeDictionary;
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

public class TileWaterGen extends TileDeviceBase implements ITickable {

	private static final int TYPE = Type.WATER_GEN.getMetadata();

	public static void initialize() {

		SIDE_CONFIGS[TYPE] = new SideConfig();
		SIDE_CONFIGS[TYPE].numConfig = 2;
		SIDE_CONFIGS[TYPE].slotGroups = new int[][] { {}, {} };
		SIDE_CONFIGS[TYPE].sideTypes = new int[] { NONE, OUTPUT_ALL };
		SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 0, 1, 0, 0, 0, 0 };

		SLOT_CONFIGS[TYPE] = new SlotConfig();
		SLOT_CONFIGS[TYPE].allowInsertionSlot = new boolean[] {};
		SLOT_CONFIGS[TYPE].allowExtractionSlot = new boolean[] {};

		GameRegistry.registerTileEntity(TileWaterGen.class, "thermalexpansion:device_water_gen");

		config();
	}

	public static void config() {

		String category = "Device.WaterGen";
		BlockDevice.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);

		String comment = "If TRUE, the Aqueous Accumulator will act as an Infinite Source and will also function in the Nether.";
		infiniteSource = ThermalExpansion.CONFIG.get(category, "Infinite", infiniteSource, comment);

		comment = "If TRUE, the Aqueous Accumulator will produce water very slowly even without adjacent source blocks.";
		passiveGen = ThermalExpansion.CONFIG.get(category, "PassiveGeneration", passiveGen, comment);
	}

	private static final int TIME_CONSTANT = 40;
	private static int genRate = 100 * TIME_CONSTANT;
	private static int genRatePassive = 2 * TIME_CONSTANT;
	private static boolean infiniteSource = false;
	private static boolean passiveGen = false;

	private int adjacentSources = -1;
	private int outputTracker;
	private boolean inNether;

	private FluidTankCore tank = new FluidTankCore(TEProps.MAX_FLUID_MEDIUM);

	private int offset;

	public TileWaterGen() {

		super();
		offset = MathHelper.RANDOM.nextInt(TIME_CONSTANT);
		tank.setLock(FluidRegistry.WATER);

		if (infiniteSource) {
			tank.modifyFluidStored(tank.getCapacity());
		}
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

		if (!timeCheckOffset()) {
			return;
		}
		boolean curActive = isActive;

		if (isActive) {
			if (!infiniteSource) {
				if (adjacentSources >= 2) {
					tank.modifyFluidStored(genRate * (adjacentSources - 1));
				} else if (world.isRaining() && world.canSeeSky(getPos())) {
					tank.modifyFluidStored(genRate);
				} else if (passiveGen) {
					tank.modifyFluidStored(genRatePassive);
				}
			}
			if (!redstoneControlOrDisable()) {
				isActive = false;
			}
		} else if (redstoneControlOrDisable() && !inNether) {
			isActive = true;
		}
		if (adjacentSources < 0) {
			updateValidity();
		}
		transferOutput();

		updateIfChanged(curActive);
	}

	protected void updateValidity() {

		inNether = BiomeDictionary.hasType(world.getBiome(pos), BiomeDictionary.Type.NETHER) && !infiniteSource;
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

	protected void transferOutput() {

		if (!getTransferOut() || tank.getFluidAmount() <= 0) {
			return;
		}
		int side;
		FluidStack output = new FluidStack(tank.getFluid(), tank.getFluidAmount());
		for (int i = outputTracker + 1; i <= outputTracker + 6; i++) {
			side = i % 6;
			if (isPrimaryOutput(sideConfig.sideTypes[sideCache[side]])) {
				int toDrain = FluidHelper.insertFluidIntoAdjacentFluidHandler(this, EnumFacing.VALUES[side], output, true);
				if (toDrain > 0) {
					tank.drain(toDrain, !infiniteSource);
					outputTracker = side;
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

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		inNether = nbt.getBoolean("Hell");
		adjacentSources = nbt.getInteger("Sources");
		outputTracker = nbt.getInteger(CoreProps.TRACK_OUT);
		tank.readFromNBT(nbt);

		if (infiniteSource) {
			tank.modifyFluidStored(tank.getCapacity());
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setBoolean("Hell", inNether);
		nbt.setInteger("Sources", adjacentSources);
		nbt.setInteger(CoreProps.TRACK_OUT, outputTracker);
		tank.writeToNBT(nbt);
		return nbt;
	}

	/* NETWORK METHODS */
	@Override
	public PacketBase getGuiPacket() {

		PacketBase payload = super.getGuiPacket();
		payload.addFluidStack(tank.getFluid());

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketBase payload) {

		super.handleGuiPacket(payload);

		tank.setFluid(payload.getFluidStack());
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
			return side != facing ? TETextures.DEVICE_SIDE : isActive ? RenderHelper.getFluidTexture(FluidRegistry.WATER) : TETextures.DEVICE_FACE[TYPE];
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

		return new FluidStack(FluidRegistry.WATER, 1);
	}

	@Override
	public int getColorMask(BlockRenderLayer layer, EnumFacing side) {

		return layer == BlockRenderLayer.SOLID && side.ordinal() == facing && isActive ? FluidRegistry.WATER.getColor() << 8 | 0xFF : super.getColorMask(layer, side);
	}

	/* ISoundSource */
	@Override
	public SoundEvent getSoundEvent() {

		return TEProps.enableSounds ? TESounds.deviceWaterGen : null;
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

					if (from == null || allowExtraction(sideConfig.sideTypes[sideCache[from.ordinal()]])) {
						return infiniteSource ? resource : tank.drain(resource, doDrain);
					}
					return null;
				}

				@Nullable
				@Override
				public FluidStack drain(int maxDrain, boolean doDrain) {

					if (from == null || allowExtraction(sideConfig.sideTypes[sideCache[from.ordinal()]])) {
						return infiniteSource ? new FluidStack(tank.getFluid(), maxDrain) : tank.drain(maxDrain, doDrain);
					}
					return null;
				}
			});
		}
		return super.getCapability(capability, from);
	}

}
