package cofh.thermalexpansion.block.plate;

import codechicken.lib.util.BlockUtils;
import cofh.api.tileentity.IRedstoneControl;
import cofh.core.network.PacketCoFHBase;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.block.TEBlocks;
import cofh.thermalexpansion.block.simple.BlockAirForce;
import cofh.thermalexpansion.gui.client.plate.GuiPlateExcursion;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import cofh.thermalexpansion.network.PacketTEBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;


import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;


public class TilePlateExcursion extends TilePlatePoweredBase implements IRedstoneControl, ITickable {

	public static void initialize() {

		GameRegistry.registerTileEntity(TilePlateExcursion.class, "cofh.thermalexpansion.PlateExcursion");
	}

	public static final byte MIN_DISTANCE = 0;
	public static final byte MAX_DISTANCE = 25;

	public static boolean canFunnelReplaceBlock(World world, IBlockState state, BlockPos pos) {

		return state == null || state.getBlockHardness(world, pos) == 0 || state.getBlock().isAir(state, world, pos);
	}

	public byte distance = 24;
	public byte realDist = -1;

	protected boolean isPowered;

	protected ControlMode rsMode = ControlMode.DISABLED;

	public TilePlateExcursion() {

		super(BlockPlate.Types.EXCURSION, 200000);
	}

	@Override
	public void onEntityCollidedWithBlock(Entity ent) {

		if (realDist == -1 || (!worldObj.isRemote && ent instanceof EntityPlayer)) {
			return;
		}

		int meta = alignment ^ (redstoneControlOrDisable() ? 0 : 1);
		EnumFacing dir = EnumFacing.VALUES[meta ^ 1];
		BlockAirForce.repositionEntity(worldObj, getPos(), ent, dir, .1);
	}

	@Override
	public void blockBroken() {
		removeBeam();
		super.blockBroken();
	}

	@Override
	public void rotated() {
		removeBeam();
	}

	@Override
	public void update() {

		if (shouldCheckBeam()) {
			updateBeam();
		}
		if (realDist > -1) {
			storage.extractEnergy(realDist, false);
		}
	}

	private boolean shouldCheckBeam() {

		return realDist < 0 || (worldObj.getTotalWorldTime() & 31) == 0;
	}

	private void updateBeam() {

		byte i;
		int e = Math.min(storage.getEnergyStored() - 1, distance);
		int forceDir = alignment ^ (redstoneControlOrDisable() ? 0 : 1);
		for (i = 0; i <= e; ++i) {
			int[] v = getVector(i);
            BlockPos offsetPos = getPos().add(v[0], v[1], v[2]);

			if (i == 0) {
				continue;
			}
			if (!worldObj.isBlockLoaded(offsetPos)) {
				return;
			}
			IBlockState state = worldObj.getBlockState(offsetPos);
			if (!state.getBlock().equals(TEBlocks.blockAirForce)) {
				if (!state.getBlock().isAir(state, worldObj, offsetPos) && canFunnelReplaceBlock(worldObj, state, offsetPos)) {
					if (!worldObj.destroyBlock(offsetPos, true)) {
						break;
					}
				}

				if (!worldObj.isAirBlock(offsetPos)) {
					break;
				}
				worldObj.setBlockState(offsetPos, TEBlocks.blockAirForce.getStateFromMeta(forceDir), 2 | 4);
			} else if (state.getBlock().getMetaFromState(state) != forceDir) {
				break;
			}
		}

		int prevDist = realDist + 1;
		realDist = --i;

		for (++i; i < prevDist;) {
			int[] v = getVector(++i);
            BlockPos offsetPos = getPos().add(v[0], v[1], v[2]);

			if (worldObj.getBlockState(offsetPos).getBlock().equals(TEBlocks.blockAirForce)) {
                worldObj.setBlockToAir(offsetPos);
			}
		}
		if (realDist != prevDist) {
            BlockUtils.fireBlockUpdate(worldObj, getPos());
		}
	}

	private void removeBeam() {

		for (int i = 1; i <= realDist; ++i) {
			int[] v = getVector(i);
            BlockPos offsetPos = getPos().add(v[0], v[1], v[2]);

			if (worldObj.getBlockState(offsetPos).getBlock().equals(TEBlocks.blockAirForce)) {
				worldObj.setBlockToAir(offsetPos);
			}
		}
		realDist = -1;
        BlockUtils.fireBlockUpdate(worldObj, getPos());
	}

	@Override
	public PacketCoFHBase getPacket() {

		PacketCoFHBase payload = super.getPacket();

		payload.addByte(distance);
		payload.addByte(realDist);

		payload.addBool(isPowered);
		payload.addByte(rsMode.ordinal());

		return payload;
	}

	@Override
	public void handleTilePacket(PacketCoFHBase payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		if (!isServer) {
			distance = payload.getByte();
			realDist = payload.getByte();

			isPowered = payload.getBool();
			rsMode = ControlMode.values()[payload.getByte()];
		}

        BlockUtils.fireBlockUpdate(worldObj, getPos());
	}

	@Override
	public PacketCoFHBase getModePacket() {

		PacketCoFHBase payload = super.getModePacket();

		payload.addByte(MathHelper.clamp(distance, MIN_DISTANCE, MAX_DISTANCE));

		return payload;
	}

	@Override
	protected void handleModePacket(PacketCoFHBase payload) {

		super.handleModePacket(payload);

		byte newDist = payload.getByte();

		if (newDist != distance) {
			removeBeam();
			distance = (byte) MathHelper.clamp(newDist, MIN_DISTANCE, MAX_DISTANCE);
		}
	}

	@Override
	protected boolean readPortableTagInternal(EntityPlayer player, NBTTagCompound tag) {

		distance = tag.getByte("Dist");
		NBTTagCompound rsTag = tag.getCompoundTag("RS");

		rsMode = ControlMode.values()[rsTag.getByte("Mode")];

		return true;
	}

	@Override
	protected boolean writePortableTagInternal(EntityPlayer player, NBTTagCompound tag) {

		tag.setByte("Dist", distance);
		NBTTagCompound rsTag = new NBTTagCompound();

		rsTag.setByte("Mode", (byte) rsMode.ordinal());
		tag.setTag("RS", rsTag);

		return true;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		distance = nbt.getByte("Dist");
		realDist = nbt.getByte("rDist");

		NBTTagCompound rsTag = nbt.getCompoundTag("RS");
		isPowered = rsTag.getBoolean("Power");
		rsMode = ControlMode.values()[rsTag.getByte("Mode")];
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setByte("Dist", distance);
		nbt.setByte("rDist", realDist);

		NBTTagCompound rsTag = new NBTTagCompound();
		rsTag.setBoolean("Power", isPowered);
		rsTag.setByte("Mode", (byte) rsMode.ordinal());
		nbt.setTag("RS", rsTag);
        return nbt;
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiPlateExcursion(inventory, this);
	}

	@Override
	public ContainerTEBase getGuiServer(InventoryPlayer inventory) {

		return new ContainerTEBase(inventory, this, false, false);
	}

	@Override
	public void onNeighborBlockChange() {

		setPowered(worldObj.isBlockPowered(getPos()));
	}

	public final boolean redstoneControlOrDisable() {

		return rsMode.isDisabled() || isPowered == rsMode.getState();
	}

	/* IRedstoneControl */
	@Override
	public final void setPowered(boolean powered) {

		boolean wasPowered = isPowered;
		isPowered = powered;
		if (wasPowered != isPowered) {
			if (ServerHelper.isClientWorld(worldObj)) {
                BlockUtils.fireBlockUpdate(worldObj, getPos());
			} else {
				removeBeam();
				updateBeam();
			}
		}
	}

	@Override
	public final boolean isPowered() {

		return isPowered;
	}

	@Override
	public final void setControl(ControlMode control) {

		rsMode = control;
		if (ServerHelper.isClientWorld(worldObj)) {
			PacketTEBase.sendRSConfigUpdatePacketToServer(this, getPos());
		} else {
			sendUpdatePacket(Side.CLIENT);
			boolean powered = isPowered;
			isPowered = !powered;
			setPowered(powered);
		}
	}

	@Override
	public final ControlMode getControl() {

		return rsMode;
	}

}
