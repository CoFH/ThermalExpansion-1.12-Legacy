package cofh.thermalexpansion.block.plate;

import cofh.core.network.PacketCoFHBase;
import cofh.core.util.CoreUtils;
import cofh.lib.util.helpers.MathHelper;
import cofh.thermalexpansion.gui.client.plate.GuiPlateTranslocate;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class TilePlateTranslocate extends TilePlateBase {

	public static void initialize() {

		GameRegistry.registerTileEntity(TilePlateTranslocate.class, "cofh.thermalexpansion.PlateTranslocate");
	}

	public static final byte MIN_DISTANCE = 0;
	public static final byte MAX_DISTANCE = 16;

	public TilePlateTranslocate() {

		super(BlockPlate.Types.TRANSLOCATE);
	}

	public byte distance = 16;

	@Override
	public void onEntityCollidedWithBlock(Entity entity) {

		int[] v = getVector(distance);
		double x = xCoord + v[0] + .5;
		double y = yCoord + v[1] + .125;
		double z = zCoord + v[2] + .5;
		if (!(entity instanceof EntityLivingBase) && entity.getBoundingBox() == null) {
			x = entity.posX + v[0];
			y = entity.posY + v[1];
			z = entity.posZ + v[2];
		}

		int x2 = xCoord + v[0];
		int y2 = yCoord + v[1];
		int z2 = zCoord + v[2];

		Block block = worldObj.getBlock(x2, y2, z2);
		if (!(block.isOpaqueCube() || block.getMaterial().isSolid())) {
			if (entity instanceof EntityLivingBase) {
				if (worldObj.isRemote) {
					return;
				}
				CoreUtils.teleportEntityTo((EntityLivingBase) entity, x, y, z, true);
			} else {
				entity.setLocationAndAngles(x, y, z, entity.rotationYaw, entity.rotationPitch);
				entity.worldObj.playSoundAtEntity(entity, "mob.endermen.portal", 0.5F, 1.0F);
			}
		}
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiPlateTranslocate(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerTEBase(inventory, this);
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		distance = nbt.getByte("Dist");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setByte("Dist", distance);
	}

	/* NETWORK METHODS */
	@Override
	public PacketCoFHBase getPacket() {

		PacketCoFHBase payload = super.getPacket();

		payload.addByte(distance);

		return payload;
	}

	@Override
	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = super.getGuiPacket();

		payload.addByte(distance);

		return payload;
	}

	@Override
	public PacketCoFHBase getModePacket() {

		PacketCoFHBase payload = super.getModePacket();

		payload.addByte(MathHelper.clampI(distance, MIN_DISTANCE, MAX_DISTANCE));

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketCoFHBase payload) {

		super.handleGuiPacket(payload);

		distance = payload.getByte();
	}

	@Override
	protected void handleModePacket(PacketCoFHBase payload) {

		super.handleModePacket(payload);

		byte newDist = payload.getByte();

		if (newDist != distance) {
			distance = newDist;
		}
	}

	/* ITilePacketHandler */
	@Override
	public void handleTilePacket(PacketCoFHBase payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		if (!isServer) {
			distance = payload.getByte();
		} else {
			payload.getByte();
		}
	}

	/* IPortableData */
	@Override
	public void readPortableData(EntityPlayer player, NBTTagCompound tag) {

		if (!canPlayerAccess(player.getCommandSenderName())) {
			return;
		}
		direction = tag.getByte("Dir");

		distance = tag.getByte("Dist");

		markDirty();
		sendUpdatePacket(Side.CLIENT);
	}

	@Override
	public void writePortableData(EntityPlayer player, NBTTagCompound tag) {

		if (!canPlayerAccess(player.getCommandSenderName())) {
			return;
		}
		tag.setByte("Dir", direction);

		tag.setByte("Dist", distance);
	}

}
