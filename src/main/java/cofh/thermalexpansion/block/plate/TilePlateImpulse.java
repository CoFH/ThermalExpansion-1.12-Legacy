package cofh.thermalexpansion.block.plate;

import cofh.core.network.PacketCoFHBase;
import cofh.lib.util.helpers.MathHelper;
import cofh.thermalexpansion.gui.client.plate.GuiPlateImpulse;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;


public class TilePlateImpulse extends TilePlateBase { // implements IItemDuct {

	public static void initialize() {

		GameRegistry.registerTileEntity(TilePlateImpulse.class, "cofh.thermalexpansion.PlateImpulse");
	}

	public static final int MIN_INTENSITY = 0;
	public static final int MAX_INTENSITY = 100;
	public static final int MIN_ANGLE = 0;
	public static final int MAX_ANGLE = 900;

	public int intensity = 10;
	public int angle;

	double intensityX;
	double intensityY;

	public TilePlateImpulse() {

		super(BlockPlate.Types.IMPULSE);
		updateForce();
	}

	@Override
	public void onEntityCollidedWithBlock(Entity theEntity) {

		double[] v = getVector(intensityX, intensityY, 0D);
		accelerateEntity(theEntity, v[0], v[1], v[2]);
	}

	protected void accelerateEntity(Entity theEntity, double x, double y, double z) {

		theEntity.onGround = false;

		long tv = Double.doubleToRawLongBits(theEntity.motionX);
		long tc = Double.doubleToRawLongBits(x);
		tv &= ~((tv & 0x8000000000000000l) ^ (tc & 0x8000000000000000l)) >> 63;
		// the section above ensures the motion* value will be non-0 only if its sign bit matches
		// the sign bit of the value we're adding to it. this clamps the velocity to always be
		// in the direction we want to move the entity, and adds to it if it is, else setting it
		theEntity.motionX = Double.longBitsToDouble(tv) + x;

		tv = Double.doubleToRawLongBits(theEntity.motionY);
		tc = Double.doubleToRawLongBits(y);
		tv &= ~((tv & 0x8000000000000000l) ^ (tc & 0x8000000000000000l)) >> 63;
		theEntity.motionY = Double.longBitsToDouble(tv) + y;

		tv = Double.doubleToRawLongBits(theEntity.motionZ);
		tc = Double.doubleToRawLongBits(z);
		tv &= ~((tv & 0x8000000000000000l) ^ (tc & 0x8000000000000000l)) >> 63;
		theEntity.motionZ = Double.longBitsToDouble(tv) + z;

		/**
		 * Truth table: x = -5; motionX = -5; sign(x) = 1000 0000 0000 0000 sign(motionX) = 1000 0000 0000 0000 xSign ^ motionXSign = 0000 0000 0000 0000
		 * ~(xSign ^ motionXSign) = 1111 1111 1111 1111 combinedSign >> 63 = 1111 1111 1111 1111 ***** x = 5; motionX = 5; sign(x) = 0000 0000 0000 0000
		 * sign(motionX) = 0000 0000 0000 0000 xSign ^ motionXSign = 0000 0000 0000 0000 ~(xSign ^ motionXSign) = 1111 1111 1111 1111 combinedSign >> 63 = 1111
		 * 1111 1111 1111 ***** x = -5; motionX = 5; sign(x) = 1000 0000 0000 0000 sign(motionX) = 0000 0000 0000 0000 xSign ^ motionXSign = 1000 0000 0000 0000
		 * ~(xSign ^ motionXSign) = 0111 1111 1111 1111 combinedSign >> 63 = 0000 0000 0000 0000
		 */
	}

	private void updateForce() {

		double fAngle = angle * Math.PI / 1800D;
		intensityX = Math.cos(fAngle) * intensity / 10D;
		intensityY = Math.sin(fAngle) * intensity / 10D;
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiPlateImpulse(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerTEBase(inventory, this);
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		intensity = nbt.getInteger("Int");
		angle = nbt.getInteger("Angle");

	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("Int", intensity);
		nbt.setInteger("Angle", angle);

	}

	/* NETWORK METHODS */
	@Override
	public PacketCoFHBase getPacket() {

		PacketCoFHBase payload = super.getPacket();

		payload.addInt(intensity);
		payload.addInt(angle);

		return payload;
	}

	@Override
	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = super.getGuiPacket();

		payload.addInt(intensity);
		payload.addInt(angle);

		return payload;
	}

	@Override
	public PacketCoFHBase getModePacket() {

		PacketCoFHBase payload = super.getModePacket();

		payload.addInt(MathHelper.clampI(intensity, MIN_INTENSITY, MAX_INTENSITY));
		payload.addInt(MathHelper.clampI(angle, MIN_ANGLE, MAX_ANGLE));

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketCoFHBase payload) {

		super.handleGuiPacket(payload);

		intensity = payload.getInt();
		angle = payload.getInt();
	}

	@Override
	protected void handleModePacket(PacketCoFHBase payload) {

		super.handleModePacket(payload);

		intensity = payload.getInt();
		angle = payload.getInt();

		updateForce();
		sendDescPacket();
	}

	/* ITilePacketHandler */
	@Override
	public void handleTilePacket(PacketCoFHBase payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		if (!isServer) {
			intensity = payload.getInt();
			angle = payload.getInt();
			updateForce();
		} else {
			payload.getInt();
			payload.getInt();
		}
	}

	/* IPortableData */
	@Override
	public void readPortableData(EntityPlayer player, NBTTagCompound tag) {

		if (!canPlayerAccess(player.getCommandSenderName())) {
			return;
		}
		direction = tag.getByte("Dir");

		intensity = tag.getInteger("Int");
		angle = tag.getInteger("Angle");

		updateForce();

		markDirty();
		sendUpdatePacket(Side.CLIENT);
	}

	@Override
	public void writePortableData(EntityPlayer player, NBTTagCompound tag) {

		if (!canPlayerAccess(player.getCommandSenderName())) {
			return;
		}
		tag.setByte("Dir", direction);

		tag.setInteger("Int", intensity);
		tag.setInteger("Angle", angle);
	}

	// @Override
	public ItemStack insertItem(ForgeDirection from, ItemStack item) {

		if (from.ordinal() >> 1 == alignment >> 1) {
			return item;
		}

		double v[] = fixVector(0, -0.25, 0);
		EntityItem ent = new EntityItem(worldObj, xCoord + .5, yCoord + .5, zCoord + .5, item);
		ent.delayBeforeCanPickup = 10;
		ent.motionX = v[0];
		ent.motionY = v[1];
		ent.motionZ = v[2];
		worldObj.spawnEntityInWorld(ent);

		return null;
	}

}
