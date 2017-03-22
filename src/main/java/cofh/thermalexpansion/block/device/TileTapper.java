package cofh.thermalexpansion.block.device;

import cofh.core.fluid.FluidTankCore;
import cofh.core.network.PacketCoFHBase;
import cofh.lib.util.BlockWrapper;
import cofh.lib.util.helpers.FluidHelper;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.helpers.RenderHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.gui.client.device.GuiTapper;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.init.TETextures;
import cofh.thermalexpansion.util.crafting.TapperManager;
import cofh.thermalfoundation.init.TFFluids;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
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

import javax.annotation.Nullable;
import java.util.Set;

public class TileTapper extends TileDeviceBase implements ITickable {

	private static final int TYPE = BlockDevice.Type.TAPPER.getMetadata();

	public static void initialize() {

		SIDE_CONFIGS[TYPE] = new SideConfig();
		SIDE_CONFIGS[TYPE].numConfig = 2;
		SIDE_CONFIGS[TYPE].slotGroups = new int[][] { {}, {} };
		SIDE_CONFIGS[TYPE].sideTypes = new int[] { 0, 4 };
		SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 0, 1, 1, 1, 1, 1 };

		SLOT_CONFIGS[TYPE] = new SlotConfig();
		SLOT_CONFIGS[TYPE].allowInsertionSlot = new boolean[] {};
		SLOT_CONFIGS[TYPE].allowExtractionSlot = new boolean[] {};

		LIGHT_VALUES[TYPE] = 3;

		GameRegistry.registerTileEntity(TileTapper.class, "thermalexpansion:device_tapper");

		config();
	}

	public static void config() {

		String category = "Device.Tapper";
		BlockDevice.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);
	}

	private static final int TIME_CONSTANT = 500;
	private static final int NUM_LEAVES = 3;

	private FluidStack genFluid = new FluidStack(TFFluids.fluidResin, 25);

	private boolean cached;
	private int outputTrackerFluid;
	private boolean validTree;

	private FluidTankCore tank = new FluidTankCore(TEProps.MAX_FLUID_MEDIUM);

	private BlockPos trunkPos;
	private BlockPos[] leafPos = new BlockPos[NUM_LEAVES];

	private int offset;

	public TileTapper() {

		super();
		offset = MathHelper.RANDOM.nextInt(TIME_CONSTANT);

		hasAutoOutput = true;

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

		if (validTree && redstoneControlOrDisable()) {
			isActive = true;
			sendTilePacket(Side.CLIENT);
		}
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
		transferOutputFluid();

		boolean curActive = isActive;

		if (isActive) {
			if (validTree) {
				tank.fill(genFluid, true);
				updateAdjacentHandlers();
			}
			if (!redstoneControlOrDisable()) {
				isActive = false;
			}
		} else if (validTree && redstoneControlOrDisable()) {
			isActive = true;
		}
		if (!cached) {
			updateAdjacentHandlers();
		}
		updateIfChanged(curActive);
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

	protected void updateAdjacentHandlers() {

		if (ServerHelper.isClientWorld(worldObj)) {
			return;
		}
		if (validTree) {
			if (isTrunkBase(trunkPos)) {
				Set<BlockWrapper> leafSet = TapperManager.getLeaf(worldObj.getBlockState(trunkPos));
				int leafCount = 0;

				for (int i = 0; i < NUM_LEAVES; i++) {
					IBlockState state = worldObj.getBlockState(leafPos[i]);
					BlockWrapper target = new BlockWrapper(state.getBlock(), state.getBlock().getMetaFromState(state));

					if (leafSet.contains(target)) {
						leafCount++;
					}
				}
				if (leafCount >= NUM_LEAVES) {
					Iterable<BlockPos.MutableBlockPos> trunk = BlockPos.getAllInBoxMutable(trunkPos, trunkPos.add(0, leafPos[0].getY(), 0));

					for (BlockPos scan : trunk) {
						IBlockState state = worldObj.getBlockState(scan);
						Material material = state.getBlock().getMaterial(state);

						if (material == Material.GROUND || material == Material.GRASS) {
							cached = true;
							return;
						}
					}
					cached = true;
					genFluid = TapperManager.getFluid(worldObj.getBlockState(trunkPos));
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

		Set<BlockWrapper> leafSet = TapperManager.getLeaf(worldObj.getBlockState(trunkPos));
		int leafCount = 0;

		for (BlockPos scan : area) {
			IBlockState state = worldObj.getBlockState(scan);
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
			Iterable<BlockPos.MutableBlockPos> trunk = BlockPos.getAllInBoxMutable(trunkPos, trunkPos.add(0, leafPos[0].getY(), 0));

			for (BlockPos scan : trunk) {
				IBlockState state = worldObj.getBlockState(scan);
				Material material = state.getBlock().getMaterial(state);

				if (material == Material.GROUND || material == Material.GRASS) {
					cached = true;
					return;
				}
			}
			validTree = true;
			genFluid = TapperManager.getFluid(worldObj.getBlockState(trunkPos));
		}
		cached = true;
	}

	protected boolean timeCheckOffset() {

		return (worldObj.getTotalWorldTime() + offset) % TIME_CONSTANT == 0;
	}

	private boolean isTrunkBase(BlockPos checkPos) {

		IBlockState state = worldObj.getBlockState(checkPos.down());

		Material material = state.getBlock().getMaterial(state);
		if (material != Material.GROUND && material != Material.GRASS) {
			return false;
		}

		return TapperManager.mappingExists(worldObj.getBlockState(checkPos));
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiTapper(inventory, this);
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

		validTree = nbt.getBoolean("Tree");
		outputTrackerFluid = nbt.getInteger("TrackOut");
		tank.readFromNBT(nbt);

		// validTree &= nbt.hasKey("LeafX" + (NUM_LEAVES - 1));

		for (int i = 0; i < NUM_LEAVES; i++) {
			leafPos[i] = new BlockPos(nbt.getInteger("LeafX" + i), nbt.getInteger("LeafY" + i), nbt.getInteger("LeafZ" + i));
		}
		trunkPos = new BlockPos(nbt.getInteger("TrunkX"), nbt.getInteger("TrunkY"), nbt.getInteger("TrunkZ"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setBoolean("Tree", validTree);
		nbt.setInteger("TrackOut", outputTrackerFluid);
		tank.writeToNBT(nbt);

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
	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = super.getGuiPacket();

		payload.addFluidStack(tank.getFluid());

		return payload;
	}

	@Override
	public PacketCoFHBase getTilePacket() {

		PacketCoFHBase payload = super.getTilePacket();

		payload.addBool(validTree);
		payload.addFluidStack(genFluid);

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketCoFHBase payload) {

		super.handleGuiPacket(payload);

		tank.setFluid(payload.getFluidStack());
	}

	@Override
	public void handleTilePacket(PacketCoFHBase payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		validTree = payload.getBool();
		genFluid = payload.getFluidStack();
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
			return side != facing ? TETextures.DEVICE_SIDE : isActive ? RenderHelper.getFluidTexture(genFluid) : TETextures.DEVICE_FACE[TYPE];
		} else if (side < 6) {
			return side != facing ? TETextures.CONFIG[sideConfig.sideTypes[sideCache[side]]] : isActive ? TETextures.DEVICE_ACTIVE[TYPE] : TETextures.DEVICE_FACE[TYPE];
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
