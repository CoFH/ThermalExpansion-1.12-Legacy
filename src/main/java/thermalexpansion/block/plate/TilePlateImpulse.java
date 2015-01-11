package thermalexpansion.block.plate;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public class TilePlateImpulse extends TilePlateBase {// implements IItemDuct {

	public static void initialize() {

		GameRegistry.registerTileEntity(TilePlateImpulse.class, "cofh.thermalexpansion.PlateImpulse");
	}

	public static final double MIN_INTENSITY = 0;
	public static final double MAX_INTENSITY = 10;

	public static final double MIN_ANGLE = 0;
	public static final double MAX_ANGLE = Math.PI / 3;

	public double intensity;
	public double angle;

	double intensityX;
	double intensityY;

	public TilePlateImpulse() {

		super(BlockPlate.Types.IMPULSE);
	}

	@Override
	public void onEntityCollidedWithBlock(Entity theEntity) {

		double[] v = getVector(50, 2, 0D);
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

	/* IPortableData */
	@Override
	public void readPortableData(EntityPlayer player, NBTTagCompound tag) {

		if (!canPlayerAccess(player.getCommandSenderName())) {
			return;
		}
		direction = tag.getByte("Dir");

		intensity = tag.getDouble("Int");
		angle = tag.getDouble("Angle");

		intensityX = intensity * Math.cos(angle);
		intensityY = intensity * Math.sin(angle);

		markDirty();
		sendUpdatePacket(Side.CLIENT);
	}

	@Override
	public void writePortableData(EntityPlayer player, NBTTagCompound tag) {

		if (!canPlayerAccess(player.getCommandSenderName())) {
			return;
		}
		tag.setByte("Dir", direction);

		tag.setDouble("Int", intensity);
		tag.setDouble("Angle", angle);
	}

	// @Override
	public ItemStack insertItem(ForgeDirection from, ItemStack item) {

		if (from.ordinal() >> 1 == alignment >> 1)
			return item;

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
