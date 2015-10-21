package cofh.thermalexpansion.block.plate;

import cofh.api.tileentity.IRedstoneControl;
import cofh.core.network.PacketCoFHBase;
import cofh.core.util.CoreUtils;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.gui.client.plate.GuiPlateTranslocate;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import cofh.thermalexpansion.network.PacketTEBase;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class TilePlateTranslocate extends TilePlateBase implements IRedstoneControl {

	public static void initialize() {

		GameRegistry.registerTileEntity(TilePlateTranslocate.class, "cofh.thermalexpansion.PlateTranslocate");
	}

	public static final byte MIN_DISTANCE = 0;
	public static final byte MAX_DISTANCE = 16;

	public TilePlateTranslocate() {

		super(BlockPlate.Types.TRANSLOCATE);
	}

	public byte distance = 16;

	protected boolean isPowered;

	protected ControlMode rsMode = ControlMode.DISABLED;

	@Override
	public void onEntityCollidedWithBlock(Entity entity) {

		if (!redstoneControlOrDisable()) {
			return;
		}

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

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiPlateTranslocate(inventory, this);
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
				PacketTEBase.sendRSPowerUpdatePacketToClients(this, worldObj, xCoord, yCoord, zCoord);
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
