package cofh.thermalexpansion.block.plate;

import cofh.api.tileentity.IRedstoneControl;
import cofh.core.network.PacketCoFHBase;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.block.TEBlocks;
import cofh.thermalexpansion.block.simple.BlockAirForce;
import cofh.thermalexpansion.gui.client.plate.GuiPlateExcursion;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import cofh.thermalexpansion.network.PacketTEBase;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

import net.minecraft.block.Block;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class TilePlateExcursion extends TilePlatePoweredBase implements IRedstoneControl {

	public static void initialize() {

		GameRegistry.registerTileEntity(TilePlateExcursion.class, "cofh.thermalexpansion.PlateExcursion");
	}

	public static final byte MIN_DISTANCE = 0;
	public static final byte MAX_DISTANCE = 25;

	public static boolean canFunnelReplaceBlock(Block block, World world, int x, int y, int z) {

		return block == null || block.getBlockHardness(world, x, y, z) == 0 || block.isAir(world, x, y, z);
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

		if (realDist == -1 || (worldObj.isRemote ? ent instanceof EntityFX : ent instanceof EntityPlayer)) {
			return;
		}

		int meta = alignment ^ (redstoneControlOrDisable() ? 0 : 1);
		ForgeDirection dir = ForgeDirection.getOrientation(meta ^ 1);
		BlockAirForce.repositionEntity(worldObj, xCoord, yCoord, zCoord, ent, dir, .1);
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
	public boolean canUpdate() {

		// can this be done otherwise?
		return true;
	}

	@Override
	public void updateEntity() {

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
			int x = xCoord + v[0], y = yCoord + v[1], z = zCoord + v[2];

			if (i == 0) {
				continue;
			}
			if (!worldObj.blockExists(x, y, z)) {
				return;
			}
			Block block = worldObj.getBlock(x, y, z);
			if (!block.equals(TEBlocks.blockAirForce)) {
				if (!block.isAir(worldObj, x, y, z) && canFunnelReplaceBlock(block, worldObj, x, y, z)) {
					if (!worldObj.func_147480_a(x, y, z, true)) {
						break;
					}
				}

				if (!worldObj.isAirBlock(x, y, z)) {
					break;
				}
				worldObj.setBlock(x, y, z, TEBlocks.blockAirForce, forceDir, 2 | 4);
			} else if (worldObj.getBlockMetadata(x, y, z) != forceDir) {
				break;
			}
		}

		int prevDist = realDist + 1;
		realDist = --i;

		for (++i; i < prevDist;) {
			int[] v = getVector(++i);
			int x = xCoord + v[0], y = yCoord + v[1], z = zCoord + v[2];

			if (worldObj.getBlock(x, y, z).equals(TEBlocks.blockAirForce)) {
				worldObj.setBlock(x, y, z, Blocks.air, 0, 3);
			}
		}
		if (realDist != prevDist) {
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	}

	private void removeBeam() {

		for (int i = 1; i <= realDist; ++i) {
			int[] v = getVector(i);
			int x = xCoord + v[0], y = yCoord + v[1], z = zCoord + v[2];

			if (worldObj.getBlock(x, y, z).equals(TEBlocks.blockAirForce)) {
				worldObj.setBlock(x, y, z, Blocks.air, 0, 3);
			}
		}
		realDist = -1;
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
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

		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
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

		return true;
	}

	@Override
	protected boolean writePortableTagInternal(EntityPlayer player, NBTTagCompound tag) {

		tag.setByte("Dist", distance);

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
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setByte("Dist", distance);
		nbt.setByte("rDist", realDist);
		NBTTagCompound rsTag = new NBTTagCompound();

		rsTag.setBoolean("Power", isPowered);
		rsTag.setByte("Mode", (byte) rsMode.ordinal());
		nbt.setTag("RS", rsTag);
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

		setPowered(worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord));
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
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
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
			PacketTEBase.sendRSConfigUpdatePacketToServer(this, this.xCoord, this.yCoord, this.zCoord);
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
