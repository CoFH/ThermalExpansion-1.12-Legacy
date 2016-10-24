package cofh.thermalexpansion.block.plate;

import codechicken.lib.util.BlockUtils;
import cofh.api.tileentity.ITileInfo;
import cofh.core.network.PacketCoFHBase;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.TileInventory;
import cofh.thermalexpansion.block.plate.BlockPlate.Types;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class TilePlateBase extends TileInventory implements ITileInfo {

	public static void initialize() {

		GameRegistry.registerTileEntity(TilePlateBase.class, "cofh.thermalexpansion.PlateFrame");
	}

	public static void configure() {

		String comment = "Enable this to allow for Plates to be securable.";
		enableSecurity = ThermalExpansion.config.get("Security", "Plate.Securable", enableSecurity, comment);
	}

	public static boolean enableSecurity = true;

	byte alignment;
	byte direction;
	protected final byte type;
	protected boolean filterSecure = false;
	// TODO: gui option for filtering on friends api ^

	public TilePlateBase() {

		this(Types.FRAME);
		if (getClass() != TilePlateBase.class) {
			throw new IllegalArgumentException();
		}
	}

	protected TilePlateBase(Types type) {

		this.type = (byte) type.ordinal();
	}

	@Override
	public int getType() {

		return type;
	}

	@Override
	public String getName() {

		return "tile.thermalexpansion.plate." + BlockPlate.NAMES[getType()] + ".name";
	}

	public void onEntityCollidedWithBlock(Entity entity) {

	}

	public boolean canRotate() {

		return true;
	}

	public void rotated() {

	}

	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, Random r) {

	}

	public int getAlignment() {

		return alignment;
	}

	public int getFacing() {

		return direction;
	}

	public boolean setFacing(int facing) {

		if (facing != direction && facing >= 0 && facing < 6) {
			direction = (byte) facing;
			return true;
		}
		return false;
	}

	void setAlignment(int side, float hitX, float hitY, float hitZ) {

		alignment = (byte) side;

		float x = 0, y = 0;
		switch (side >> 1) {
		case 0:
			x = hitX;
			y = hitZ;
			break;
		case 1:
			x = hitX;
			y = hitY;
			break;
		case 2:
			x = hitY;
			y = hitZ;
			break;
		}

		float degreeCenter = 0.32f / 2;

		if (x * x + y * y < degreeCenter * degreeCenter) {
			direction = 1;
		} else {
			int a = (int) ((Math.atan2(x, y) + Math.PI) * 4 / Math.PI);
			a = ++a & 7;
			switch (a >> 1) {
			case 0:
			case 4:
				direction = 2;
				break;
			case 1:
				direction = 4;
				break;
			case 2:
				direction = 3;
				break;
			case 3:
				direction = 5;
				break;
			}
		}
		return;
	}

	/* HELPERS */
	protected int[] getVector(int distance) {

		int x = 0, y = 0, z = 0;
		if ((direction & 1) == 0) {
			distance = -distance;
		}
		switch (direction >> 1) {
		case 0:
		case 3:
			y = distance;
			break;
		case 1:
			z = distance;
			break;
		case 2:
			x = distance;
			break;
		}
		return fixVector(x, y, z);
	}

	protected int[] getVector(int x, int y, int z) {

		int t;
		switch (direction) {
		case 0:
			y = -x;
			x = 0;
			z = 0;
			break;
		case 1:
			y = x;
			x = 0;
			z = 0;
			break;
		case 2:
			x = -x;
			z = -z;
		case 3:
			t = x;
			x = z;
			z = t;
			break;
		case 4:
			x = -x;
			z = -z;
		case 5:
		}
		return fixVector(x, y, z);
	}

	protected int[] fixVector(int x, int y, int z) {

		int[] a = { x, y, z };
		int t;
		switch (alignment) {
		case 0:
			break;
		case 1:
			a[1] = -a[1];
			break;
		case 2:
			a[1] = -a[1];
		case 3:
			t = a[2];
			a[2] = -a[1];
			a[1] = t;
			break;
		case 4:
			a[1] = -a[1];
		case 5:
			t = a[0];
			a[0] = -a[1];
			a[1] = t;
			break;
		}
		return a;
	}

	protected double[] getVector(double distance) {

		double x = 0, y = 0, z = 0;
		if ((direction & 1) == 0) {
			distance = -distance;
		}
		switch (direction >> 1) {
		case 0:
		case 3:
			y = distance;
			break;
		case 1:
			z = distance;
			break;
		case 2:
			x = distance;
			break;
		}
		return fixVector(x, y, z);
	}

	protected double[] getVector(double x, double y, double z) {

		double t;
		switch (direction) {
		case 0:
			y = -x;
			x = 0;
			z = 0;
			break;
		case 1:
			y = x;
			x = 0;
			z = 0;
			break;
		case 2:
			x = -x;
			z = -z;
		case 3:
			t = x;
			x = z;
			z = t;
			break;
		case 4:
			x = -x;
			z = -z;
		case 5:
		}
		return fixVector(x, y, z);
	}

	protected double[] fixVector(double x, double y, double z) {

		double[] a = { x, y, z };
		double t;
		switch (alignment) {
		case 0:
			break;
		case 1:
			a[1] = -a[1];
			break;
		case 2:
			a[1] = -a[1];
		case 3:
			t = a[2];
			a[2] = -a[1];
			a[1] = t;
			break;
		case 4:
			a[1] = -a[1];
		case 5:
			t = a[0];
			a[0] = -a[1];
			a[1] = t;
			break;
		}
		return a;
	}

	protected double[] fixPosition(double x, double y, double z) {

		double[] a = { x, y, z };
		double t;
		switch (alignment) {
		case 0:
			break;
		case 1:
			a[1] = 1 - a[1];
			break;
		case 2:
			a[1] = 1 - a[1];
		case 3:
			t = a[2];
			a[2] = 1 - a[1];
			a[1] = t;
			break;
		case 4:
			a[1] = 1 - a[1];
		case 5:
			t = a[0];
			a[0] = 1 - a[1];
			a[1] = t;
			break;
		}
		return a;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		alignment = nbt.getByte("Align");
		direction = nbt.getByte("Dir");
		filterSecure = nbt.getBoolean("SecureFilter");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setByte("Align", alignment);
		nbt.setByte("Dir", direction);
		nbt.setBoolean("SecureFilter", filterSecure);
        return nbt;
	}

	/* NETWORK METHODS */
	@Override
	public PacketCoFHBase getPacket() {

		PacketCoFHBase payload = super.getPacket();

		payload.addByte(alignment);
		payload.addByte(direction);
		payload.addBool(filterSecure);

		return payload;
	}

	@Override
	public PacketCoFHBase getModePacket() {

		PacketCoFHBase payload = super.getModePacket();

		payload.addByte(direction);

		return payload;
	}

	@Override
	protected void handleModePacket(PacketCoFHBase payload) {

		super.handleModePacket(payload);

		byte newDir = payload.getByte();

		if (newDir != direction) {
			rotated();
			direction = newDir;
            BlockUtils.fireBlockUpdate(worldObj, pos);
		}
	}

	/* ITilePacketHandler */
	@Override
	public void handleTilePacket(PacketCoFHBase payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		if (!isServer) {
			alignment = payload.getByte();
			direction = payload.getByte();
			filterSecure = payload.getBool();
		} else {
			payload.getByte();
			payload.getByte();
			payload.getBool();
		}
	}

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {

		return oldState.getBlock() != newSate.getBlock() || newSate.getBlock().getMetaFromState(newSate) != type;
	}

	/* ITileInfo */
	@Override
	public void getTileInfo(List<ITextComponent> info, EnumFacing side, EntityPlayer player, boolean debug) {

		if (debug) {
			info.add(new TextComponentString("Alignment: " + alignment + ":" + EnumFacing.VALUES[alignment]));
			info.add(new TextComponentString("Direction: " + direction + ":" + EnumFacing.VALUES[direction]));
		}
	}

}
