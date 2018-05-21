package cofh.thermalexpansion.block.device;

import cofh.core.fluid.FluidTankCore;
import cofh.core.init.CoreProps;
import cofh.core.network.PacketBase;
import cofh.core.util.BlockWrapper;
import cofh.core.util.core.SideConfig;
import cofh.core.util.core.SlotConfig;
import cofh.core.util.helpers.FluidHelper;
import cofh.core.util.helpers.MathHelper;
import cofh.core.util.helpers.RenderHelper;
import cofh.core.util.helpers.ServerHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.device.BlockDevice.Type;
import cofh.thermalexpansion.gui.client.device.GuiTapper;
import cofh.thermalexpansion.gui.container.device.ContainerTapper;
import cofh.thermalexpansion.init.TEBlocks;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.init.TETextures;
import cofh.thermalexpansion.util.managers.device.TapperManager;
import cofh.thermalfoundation.init.TFFluids;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
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
import java.util.Arrays;
import java.util.Set;

import static cofh.core.util.core.SideConfig.*;

public class TileTapper extends TileDeviceBase implements ITickable {

	private static final int TYPE = Type.TAPPER.getMetadata();

	public static void initialize() {

		SIDE_CONFIGS[TYPE] = new SideConfig();
		SIDE_CONFIGS[TYPE].numConfig = 5;
		SIDE_CONFIGS[TYPE].slotGroups = new int[][] { {}, { 0 }, {}, { 0 }, { 0 } };
		SIDE_CONFIGS[TYPE].sideTypes = new int[] { NONE, INPUT_ALL, OUTPUT_ALL, OPEN, OMNI };
		SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 1, 1, 2, 2, 2, 2 };

		SLOT_CONFIGS[TYPE] = new SlotConfig();
		SLOT_CONFIGS[TYPE].allowInsertionSlot = new boolean[] { true };
		SLOT_CONFIGS[TYPE].allowExtractionSlot = new boolean[] { false };

		GameRegistry.registerTileEntity(TileTapper.class, "thermalexpansion:device_tapper");

		config();
	}

	public static void config() {

		String category = "Device.Tapper";
		BlockDevice.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);

		String comment = "If TRUE, the Arboreal Extractor will REQUIRE Phyto-Gro to operate.";
		requireFertilizer = ThermalExpansion.CONFIG.get(category, "RequireFertilizer", requireFertilizer, comment);

		comment = "Adjust this value to set the number of cycles Phyto-Gro lasts.";
		boostCycles = ThermalExpansion.CONFIG.getConfiguration().getInt("FertilizerDuration", category, boostCycles, 2, 64, comment);
	}

	private static final int NUM_LEAVES = 3;
	private static final int TIME_CONSTANT = 500;

	private static boolean requireFertilizer = false;
	private static int boostCycles = 8;

	private int timeConstant = TIME_CONSTANT;

	private FluidStack genFluid = new FluidStack(TFFluids.fluidResin, 50);

	private boolean cached;

	private int inputTracker;
	private int outputTrackerFluid;
	private boolean validTree;

	private int boostMult;
	private int boostTime;

	private FluidTankCore tank = new FluidTankCore(TEProps.MAX_FLUID_MEDIUM);

	private BlockPos trunkPos;
	private BlockPos[] leafPos = new BlockPos[NUM_LEAVES];

	private int offset;

	public TileTapper() {

		super();
		inventory = new ItemStack[1];
		Arrays.fill(inventory, ItemStack.EMPTY);
		createAllSlots(inventory.length);

		offset = MathHelper.RANDOM.nextInt(TIME_CONSTANT);

		hasAutoInput = true;
		hasAutoOutput = true;

		enableAutoInput = true;
		enableAutoOutput = true;

		trunkPos = new BlockPos(pos);
		for (int i = 0; i < NUM_LEAVES; i++) {
			leafPos[i] = new BlockPos(pos);
		}
	}

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	public void blockPlaced() {

		super.blockPlaced();

		if (validTree && redstoneControlOrDisable()) {
			isActive = true;
			sendTilePacket(Side.CLIENT);
		}
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
		transferInput();

		boolean curActive = isActive;
		Fluid curFluid = genFluid.getFluid();

		if (isActive) {
			if (validTree) {
				genFluid = TapperManager.getFluid(world.getBlockState(trunkPos));
				if (boostTime > 0) {
					tank.fill(new FluidStack(genFluid, genFluid.amount * boostMult), true);
					boostTime--;
				} else {
					boostMult = TapperManager.getFertilizerMultiplier(inventory[0]);
					if (boostMult > 0) {
						tank.fill(new FluidStack(genFluid, genFluid.amount * boostMult), true);
						boostTime = boostCycles - 1;
						inventory[0].shrink(1);
						if (inventory[0].getCount() <= 0) {
							inventory[0] = ItemStack.EMPTY;
						}
					} else if (!requireFertilizer) {
						tank.fill(genFluid, true);
					}
				}
				updateValidity();
			}
			if (!redstoneControlOrDisable() || !validTree) {
				isActive = false;
			}
		} else if (validTree && redstoneControlOrDisable()) {
			isActive = true;
		}
		if (!cached) {
			updateValidity();
		}
		if (curFluid != genFluid.getFluid()) {
			sendTilePacket(Side.CLIENT);
		}
		transferOutput();

		updateIfChanged(curActive);
	}

	protected void transferInput() {

		if (!getTransferIn()) {
			return;
		}
		int side;
		for (int i = inputTracker + 1; i <= inputTracker + 6; i++) {
			side = i % 6;
			if (isPrimaryInput(sideConfig.sideTypes[sideCache[side]])) {
				if (extractItem(0, ITEM_TRANSFER[level], EnumFacing.VALUES[side])) {
					inputTracker = side;
					break;
				}
			}
		}
	}

	protected void transferOutput() {

		if (!getTransferOut() || tank.getFluidAmount() <= 0) {
			return;
		}
		int side;
		FluidStack output = new FluidStack(tank.getFluid(), Math.min(tank.getFluidAmount(), Fluid.BUCKET_VOLUME));
		for (int i = outputTrackerFluid + 1; i <= outputTrackerFluid + 6; i++) {
			side = i % 6;
			if (isPrimaryOutput(sideConfig.sideTypes[sideCache[side]])) {
				int toDrain = FluidHelper.insertFluidIntoAdjacentFluidHandler(this, EnumFacing.VALUES[side], output, true);
				if (toDrain > 0) {
					tank.drain(toDrain, true);
					outputTrackerFluid = side;
					break;
				}
			}
		}
	}

	protected void updateValidity() {

		if (ServerHelper.isClientWorld(world)) {
			return;
		}
		timeConstant = getTimeConstant();

		if (validTree) {
			if (isTrunkBase(trunkPos)) {
				Set<BlockWrapper> leafSet = TapperManager.getLeaf(world.getBlockState(trunkPos));
				int leafCount = 0;

				for (int i = 0; i < NUM_LEAVES; i++) {
					IBlockState state = world.getBlockState(leafPos[i]);
					BlockWrapper target = new BlockWrapper(state.getBlock(), state.getBlock().getMetaFromState(state));

					if (leafSet.contains(target)) {
						leafCount++;
					}
				}
				if (leafCount >= NUM_LEAVES) {
					Iterable<BlockPos.MutableBlockPos> scanArea = BlockPos.getAllInBoxMutable(trunkPos, trunkPos.add(0, leafPos[0].getY() - trunkPos.getY(), 0));

					for (BlockPos scan : scanArea) {
						IBlockState state = world.getBlockState(scan);
						Material material = state.getMaterial();

						if (material == Material.GRASS || material == Material.GROUND || material == Material.ROCK) {
							validTree = false;
							cached = true;
							return;
						}
					}
					scanArea = BlockPos.getAllInBoxMutable(pos.add(0, 1, 0), pos.add(0, leafPos[0].getY() - pos.getY(), 0));

					for (BlockPos scan : scanArea) {
						IBlockState state = world.getBlockState(scan);

						if (state.getBlock() == TEBlocks.blockDevice && TEBlocks.blockDevice.getMetaFromState(state) == TYPE) {
							validTree = false;
							cached = true;
							return;
						}
					}
					cached = true;
					genFluid = TapperManager.getFluid(world.getBlockState(trunkPos));
					return;
				}
			}
			validTree = false;
		}
		if (isTrunkBase(pos.west())) {
			trunkPos = pos.west();
		} else if (isTrunkBase(pos.east())) {
			trunkPos = pos.east();
		} else if (isTrunkBase(pos.north())) {
			trunkPos = pos.north();
		} else if (isTrunkBase(pos.south())) {
			trunkPos = pos.south();
		}
		if (!isTrunkBase(trunkPos)) {
			validTree = false;
			cached = true;
			return;
		}
		Iterable<BlockPos.MutableBlockPos> area = BlockPos.getAllInBoxMutable(pos.add(-1, 0, -1), pos.add(1, Math.min(256 - pos.getY(), 40), 1));

		Set<BlockWrapper> leafSet = TapperManager.getLeaf(world.getBlockState(trunkPos));
		int leafCount = 0;

		for (BlockPos scan : area) {
			IBlockState state = world.getBlockState(scan);
			BlockWrapper target = new BlockWrapper(state.getBlock(), state.getBlock().getMetaFromState(state));

			if (leafSet.contains(target)) {
				leafPos[leafCount] = new BlockPos(scan);
				leafCount++;
				if (leafCount >= NUM_LEAVES) {
					break;
				}
			}
		}
		if (leafCount >= NUM_LEAVES) {
			Iterable<BlockPos.MutableBlockPos> scanArea = BlockPos.getAllInBoxMutable(trunkPos, trunkPos.add(0, leafPos[0].getY() - trunkPos.getY(), 0));

			for (BlockPos scan : scanArea) {
				IBlockState state = world.getBlockState(scan);
				Material material = state.getMaterial();

				if (material == Material.GRASS || material == Material.GROUND || material == Material.ROCK) {
					validTree = false;
					cached = true;
					return;
				}
			}
			scanArea = BlockPos.getAllInBoxMutable(pos.add(0, 1, 0), pos.add(0, leafPos[0].getY() - pos.getY(), 0));

			for (BlockPos scan : scanArea) {
				IBlockState state = world.getBlockState(scan);

				if (state.getBlock() == TEBlocks.blockDevice && TEBlocks.blockDevice.getMetaFromState(state) == TYPE) {
					validTree = false;
					cached = true;
					return;
				}
			}
			validTree = true;
			genFluid = TapperManager.getFluid(world.getBlockState(trunkPos));
		}
		cached = true;
	}

	protected boolean timeCheckOffset() {

		return (world.getTotalWorldTime() + offset) % timeConstant == 0;
	}

	protected int getTimeConstant() {

		int constant = TIME_CONSTANT / 2;
		Iterable<BlockPos.MutableBlockPos> area = BlockPos.getAllInBoxMutable(pos.add(-1, 0, -1), pos.add(1, 0, 1));

		for (BlockPos scan : area) {
			if (isTapper(world.getBlockState(scan))) {
				constant += TIME_CONSTANT / 2;
			}
		}
		return MathHelper.clamp(constant, TIME_CONSTANT, TIME_CONSTANT * 2);
	}

	protected boolean isTrunkBase(BlockPos checkPos) {

		IBlockState state = world.getBlockState(checkPos.down());
		Material material = state.getMaterial();

		if (material != Material.GRASS && material != Material.GROUND && material != Material.ROCK) {
			return false;
		}
		return TapperManager.mappingExists(world.getBlockState(checkPos)) && TapperManager.mappingExists(world.getBlockState(checkPos.up())) && TapperManager.mappingExists(world.getBlockState(checkPos.up(2)));
	}

	protected static boolean isTapper(IBlockState state) {

		return state.getBlock() == TEBlocks.blockDevice && state.getValue(BlockDevice.VARIANT) == Type.TAPPER;
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiTapper(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerTapper(inventory, this);
	}

	@Override
	public int getScaledSpeed(int scale) {

		if (!isActive) {
			return 0;
		}
		return MathHelper.round(scale * boostTime / boostCycles);
	}

	@Override
	public FluidTankCore getTank() {

		return tank;
	}

	@Override
	public FluidStack getTankFluid() {

		return tank.getFluid();
	}

	public int getBoostMult() {

		return boostMult;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		validTree = nbt.getBoolean("Tree");
		inputTracker = nbt.getInteger(CoreProps.TRACK_IN);
		outputTrackerFluid = nbt.getInteger(CoreProps.TRACK_OUT);
		tank.readFromNBT(nbt);

		boostMult = nbt.getInteger("BoostMult");
		boostTime = nbt.getInteger("BoostTime");
		timeConstant = nbt.getInteger("TimeConstant");

		if (timeConstant <= 0) {
			timeConstant = TIME_CONSTANT;
		}
		for (int i = 0; i < NUM_LEAVES; i++) {
			leafPos[i] = new BlockPos(nbt.getInteger("LeafX" + i), nbt.getInteger("LeafY" + i), nbt.getInteger("LeafZ" + i));
		}
		trunkPos = new BlockPos(nbt.getInteger("TrunkX"), nbt.getInteger("TrunkY"), nbt.getInteger("TrunkZ"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setBoolean("Tree", validTree);
		nbt.setInteger(CoreProps.TRACK_IN, inputTracker);
		nbt.setInteger(CoreProps.TRACK_OUT, outputTrackerFluid);
		tank.writeToNBT(nbt);

		nbt.setInteger("BoostMult", boostMult);
		nbt.setInteger("BoostTime", boostTime);
		nbt.setInteger("TimeConstant", timeConstant);

		for (int i = 0; i < NUM_LEAVES; i++) {
			nbt.setInteger("LeafX" + i, leafPos[i].getX());
			nbt.setInteger("LeafY" + i, leafPos[i].getY());
			nbt.setInteger("LeafZ" + i, leafPos[i].getZ());
		}
		nbt.setInteger("TrunkX", trunkPos.getX());
		nbt.setInteger("TrunkY", trunkPos.getY());
		nbt.setInteger("TrunkZ", trunkPos.getZ());

		return nbt;
	}

	/* NETWORK METHODS */

	/* SERVER -> CLIENT */
	@Override
	public PacketBase getGuiPacket() {

		PacketBase payload = super.getGuiPacket();

		payload.addInt(boostTime);
		payload.addInt(boostMult);
		payload.addFluidStack(tank.getFluid());

		return payload;
	}

	@Override
	public PacketBase getTilePacket() {

		PacketBase payload = super.getTilePacket();

		payload.addBool(validTree);
		payload.addFluidStack(genFluid);

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketBase payload) {

		super.handleGuiPacket(payload);

		boostTime = payload.getInt();
		boostMult = payload.getInt();
		tank.setFluid(payload.getFluidStack());
	}

	@Override
	@SideOnly (Side.CLIENT)
	public void handleTilePacket(PacketBase payload) {

		super.handleTilePacket(payload);

		validTree = payload.getBool();
		genFluid = payload.getFluidStack();
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
			return side != facing ? TETextures.DEVICE_SIDE : isActive ? RenderHelper.getFluidTexture(genFluid) : TETextures.DEVICE_FACE[TYPE];
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

		return genFluid;
	}

	@Override
	public int getColorMask(BlockRenderLayer layer, EnumFacing side) {

		return layer == BlockRenderLayer.SOLID && side.ordinal() == facing && isActive ? genFluid.getFluid().getColor(genFluid) << 8 | 0xFF : super.getColorMask(layer, side);
	}

	/* IInventory */
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		return TapperManager.getFertilizerMultiplier(stack) > 0;
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
