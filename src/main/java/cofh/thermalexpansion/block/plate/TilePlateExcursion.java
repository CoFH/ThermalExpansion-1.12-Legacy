package cofh.thermalexpansion.block.plate;

import cofh.core.network.PacketCoFHBase;
import cofh.thermalexpansion.block.TEBlocks;
import cofh.thermalexpansion.gui.client.plate.GuiPlateExcursion;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.block.Block;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class TilePlateExcursion extends TilePlatePoweredBase {

	public static final byte MIN_DISTANCE = 0;
	public static final byte MAX_DISTANCE = 24;

	public static void initialize() {

		GameRegistry.registerTileEntity(TilePlateExcursion.class, "cofh.thermalexpansion.PlateExcursion");
	}

	public static boolean canReplaceBlock(Block block, World world, int x, int y, int z)
	{

		return block == null || block.getBlockHardness(world, x, y, z) == 0 || block.isAir(world, x, y, z);
	}

	public byte distance = 24;
	public byte realDist = -1;

	public TilePlateExcursion() {

		super(BlockPlate.Types.POWERED_IMPULSE, 200000);
	}

	@Override
	public void onEntityCollidedWithBlock(Entity ent) {

		if (realDist == -1 || (ent instanceof EntityFX) || (ent instanceof EntityPlayer && !worldObj.isRemote))
			return;

		int x = xCoord, y = yCoord, z = zCoord;
		int meta = alignment;
		ForgeDirection dir = ForgeDirection.getOrientation(meta ^ 1);
		double l = .1, xO = dir.offsetX * l, yO = dir.offsetY * l, zO = dir.offsetZ * l;
		xO += ent.motionX * l;
		zO += ent.motionZ * l;
		if (AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1).isVecInside(
			Vec3.createVectorHelper(ent.prevPosX, ent.prevPosY - ent.yOffset, ent.prevPosZ))) {
			if (ent instanceof EntityLivingBase) {
				((EntityLivingBase) ent).setPositionAndUpdate(ent.prevPosX + xO, ent.prevPosY - ent.yOffset + yO, ent.prevPosZ +
						zO);
			} else {
				ent.setLocationAndAngles(ent.prevPosX + xO, ent.prevPosY - ent.yOffset + yO, ent.prevPosZ + zO, ent.rotationYaw,
					ent.rotationPitch);
			}
			ent.motionY = 0;
		}
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

		return realDist <= 0 || (worldObj.getTotalWorldTime() & 31) == 0;
	}

	private void updateBeam() {

		byte i;
		int e = Math.min(storage.getEnergyStored(), distance);
		for (i = 0; i < e; ) {
			int[] v = getVector(++i);
			int x = xCoord + v[0], y = yCoord + v[1], z = zCoord + v[2];

			if (!worldObj.blockExists(x, y, z)) {
				return;
			}
			Block block = worldObj.getBlock(x, y, z);
			if (!block.equals(TEBlocks.blockAirForce)) {
				if (!block.isAir(worldObj, x, y, z) &&
						canReplaceBlock(block, worldObj, x, y, z)) {
					if (!worldObj.func_147480_a(x, y, z, true)) {
						break;
					}
				}

				if (!worldObj.isAirBlock(x, y, z)) {
					break;
				}
				worldObj.setBlock(x, y, z, TEBlocks.blockAirForce);
			}
			worldObj.setBlockMetadataWithNotify(x, y, z, alignment, 3);
		}

		int prevDist = realDist + 1;
		realDist = --i;

		for (++i; i <= prevDist; ) {
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

		for (int i = 1; i <= distance; ++i) {
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

		return payload;
	}

	@Override
	public void handleTilePacket(PacketCoFHBase payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		if (!isServer) {
			distance = payload.getByte();
			realDist = payload.getByte();
		}

		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		distance = nbt.getByte("Dist");
		realDist = nbt.getByte("rDist");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setByte("Dist", distance);
		nbt.setByte("rDist", realDist);
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
}
