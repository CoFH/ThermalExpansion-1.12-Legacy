package cofh.thermalexpansion.util.helpers;

import cofh.thermalexpansion.block.TileReconfigurable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ReconfigurableHelper {

	public static final byte DEFAULT_FACING = 3;
	public static final byte[] DEFAULT_SIDES = new byte[] { 0, 0, 0, 0, 0, 0 };

	private ReconfigurableHelper() {

	}

	/* NBT TAG HELPERS */
	public static NBTTagCompound setItemStackTagReconfig(NBTTagCompound tag, TileReconfigurable tile) {

		if (tile == null) {
			return null;
		}
		if (tag == null) {
			tag = new NBTTagCompound();
		}
		tag.setByte("Facing", (byte) tile.getFacing());
		tag.setByteArray("SideCache", tile.sideCache);
		return tag;
	}

	public static byte getFacingFromNBT(NBTTagCompound tag) {

		return !tag.hasKey("Facing") ? DEFAULT_FACING : tag.getByte("Facing");
	}

	public static byte[] getSideCacheFromNBT(NBTTagCompound tag, byte[] defaultSides) {

		if (tag == null) {
			return defaultSides.clone();
		}
		byte[] retSides = tag.getByteArray("SideCache");
		return retSides.length < 6 ? defaultSides.clone() : retSides;
	}

	/* ITEM HELPERS */
	public static boolean hasReconfigInfo(ItemStack stack) {

		return stack.getTagCompound() != null && (stack.getTagCompound().hasKey("Facing") && stack.getTagCompound().hasKey("SideCache"));
	}

	public static boolean setFacing(ItemStack stack, int facing) {

		if (facing < 0 || facing > 5) {
			return false;
		}
		if (stack.getTagCompound() == null) {
			stack.setTagCompound(new NBTTagCompound());
		}
		stack.getTagCompound().setByte("Facing", (byte) facing);
		return true;
	}

	public static boolean setSideCache(ItemStack stack, byte[] sideCache) {

		if (sideCache.length < 6) {
			return false;
		}
		if (stack.getTagCompound() == null) {
			stack.setTagCompound(new NBTTagCompound());
		}
		stack.getTagCompound().setByteArray("SideCache", sideCache);
		return true;
	}

	public static byte getFacing(ItemStack stack) {

		return stack.getTagCompound() == null || !stack.getTagCompound().hasKey("Facing") ? DEFAULT_FACING : stack.getTagCompound().getByte("Facing");
	}

	public static byte[] getSideCache(ItemStack stack) {

		if (stack.getTagCompound() == null) {
			return DEFAULT_SIDES.clone();
		}
		byte[] retSides = stack.getTagCompound().getByteArray("SideCache");
		return retSides.length < 6 ? DEFAULT_SIDES.clone() : retSides;
	}

	public static byte[] getSideCache(ItemStack stack, byte[] defaultSides) {

		if (stack.getTagCompound() == null) {
			return defaultSides.clone();
		}
		byte[] retSides = stack.getTagCompound().getByteArray("SideCache");
		return retSides.length < 6 ? defaultSides.clone() : retSides;
	}

}
