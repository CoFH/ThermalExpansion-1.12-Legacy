package cofh.thermalexpansion.block.plate;

import cofh.core.network.PacketCoFHBase;
import cofh.lib.util.helpers.MathHelper;
import cofh.thermalexpansion.block.TEBlocks;
import cofh.thermalexpansion.gui.client.plate.GuiPlateSignal;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;

//import thermalexpansion.gui.client.plate.GuiPlateSignal;

public class TilePlateSignal extends TilePlateBase {

	public static void initialize() {

		GameRegistry.registerTileEntity(TilePlateSignal.class, "cofh.thermalexpansion.PlateSignal");
	}

	public static final byte MIN_DISTANCE = 0;
	public static final byte MAX_DISTANCE = 15;
	public static final byte MIN_INTENSITY = 0;
	public static final byte MAX_INTENSITY = 15;
	public static final byte MIN_DURATION = 2;
	public static final byte MAX_DURATION = 40;

	public byte distance = 15;
	public byte intensity = 15;
	public byte duration = 20;
	public byte collisionMode = 0;
	byte collided = 0;

	public TilePlateSignal() {

		super(BlockPlate.Types.SIGNAL);
	}

	@Override
	public void blockBroken() {

		removeSignal();
		super.blockBroken();
	}

	@Override
	public void rotated() {

		removeSignal();
	}

	@Override
	public boolean canUpdate() {

		// FIXME: in 1.8 we can differentiate random world ticks and update ticks on the block.
		// we can use that to destroy the block
		return true;
	}

	private void removeSignal() {

		int[] v = getVector(distance + 1);
		int x = v[0], y = v[1], z = v[2];

		if (worldObj.getBlock(xCoord + x, yCoord + y, zCoord + z).equals(TEBlocks.blockAirSignal)) {
			worldObj.setBlock(xCoord + x, yCoord + y, zCoord + z, Blocks.air, 0, 3);
		}
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public void updateEntity() {

		if (collided > 0) {
			markChunkDirty();
			if (--collided == 0) {
				removeSignal();
			}
		}
	}

	@Override
	public void onEntityCollidedWithBlock(Entity theEntity) {

		if (worldObj.isRemote) {
			return;
		}

		switch (collisionMode) {
		case 3:
			if (!(theEntity instanceof IMob)) {
				return;
			}
			break;
		case 2:
			if (!(theEntity instanceof EntityPlayer)) {
				return;
			}
			break;
		case 1:
			if (!(theEntity instanceof EntityLivingBase)) {
				return;
			}
			break;
		case 0:
		default:
			break;
		}

		if (collided > 0) {
			collided = duration;
			if (worldObj.getTotalWorldTime() % 10 != 0) {
				return;
			}
		}

		int[] v = getVector(distance + 1);
		int x = v[0], y = v[1], z = v[2];

		if (worldObj.isAirBlock(xCoord + x, yCoord + y, zCoord + z)) {
			if (worldObj.setBlock(xCoord + x, yCoord + y, zCoord + z, TEBlocks.blockAirSignal, intensity, 3)) {
				markChunkDirty();
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
			collided = duration;
		}
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiPlateSignal(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerTEBase(inventory, this, false, false);
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		distance = nbt.getByte("Dist");
		intensity = nbt.getByte("Int");
		duration = nbt.getByte("Time");
		collided = nbt.getByte("Col");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setByte("Dist", distance);
		nbt.setByte("Int", intensity);
		nbt.setByte("Time", duration);
		nbt.setByte("Col", collided);
	}

	/* NETWORK METHODS */
	@Override
	public PacketCoFHBase getPacket() {

		PacketCoFHBase payload = super.getPacket();

		payload.addByte(distance);
		payload.addByte(intensity);
		payload.addByte(duration);
		payload.addByte(collided);

		return payload;
	}

	@Override
	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = super.getGuiPacket();

		payload.addByte(distance);
		payload.addByte(intensity);
		payload.addByte(duration);

		return payload;
	}

	@Override
	public PacketCoFHBase getModePacket() {

		PacketCoFHBase payload = super.getModePacket();

		payload.addByte(MathHelper.clampI(distance, MIN_DISTANCE, MAX_DISTANCE));
		payload.addByte(MathHelper.clampI(intensity, MIN_INTENSITY, MAX_INTENSITY));
		payload.addByte(MathHelper.clampI(duration, MIN_DURATION, MAX_DURATION));

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketCoFHBase payload) {

		super.handleGuiPacket(payload);

		distance = payload.getByte();
		intensity = payload.getByte();
		duration = payload.getByte();
	}

	@Override
	protected void handleModePacket(PacketCoFHBase payload) {

		super.handleModePacket(payload);

		byte newDist = payload.getByte();

		if (newDist != distance) {
			removeSignal();
			distance = newDist;
		}
		intensity = payload.getByte();
		duration = payload.getByte();
	}

	/* ITilePacketHandler */
	@Override
	public void handleTilePacket(PacketCoFHBase payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		if (!isServer) {
			distance = payload.getByte();
			intensity = payload.getByte();
			duration = payload.getByte();
		} else {
			payload.getByte();
			payload.getByte();
			payload.getByte();
		}
	}

	/* IPortableData */
	@Override
	public void readPortableData(EntityPlayer player, NBTTagCompound tag) {

		if (!canPlayerAccess(player)) {
			return;
		}

		distance = tag.getByte("Dist");
		intensity = tag.getByte("Int");
		duration = tag.getByte("Time");

		markDirty();
		sendUpdatePacket(Side.CLIENT);
	}

	@Override
	public void writePortableData(EntityPlayer player, NBTTagCompound tag) {

		if (!canPlayerAccess(player)) {
			return;
		}

		tag.setByte("Dist", distance);
		tag.setByte("Int", intensity);
		tag.setByte("Time", duration);
	}

}
