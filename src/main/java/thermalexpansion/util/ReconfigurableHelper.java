package thermalexpansion.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import thermalexpansion.block.TileReconfigurable;

public class ReconfigurableHelper {

	public static final byte DEFAULT_FACING = 3;
	public static final byte[] DEFAULT_SIDES = new byte[] { 0, 0, 0, 0, 0, 0 };

	private ReconfigurableHelper() {

	}

	/* NBT TAG HELPER */
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

	/* ITEM HELPERS */
	public static boolean hasReconfigInfo(ItemStack stack) {

		return stack.stackTagCompound == null ? false : stack.stackTagCompound.hasKey("Facing") && stack.stackTagCompound.hasKey("SideCache");
	}

	public static boolean setFacing(ItemStack stack, int facing) {

		if (facing < 0 || facing > 5) {
			return false;
		}
		if (stack.stackTagCompound == null) {
			stack.setTagCompound(new NBTTagCompound());
		}
		stack.stackTagCompound.setByte("Facing", (byte) facing);
		return true;
	}

	public static boolean setSideCache(ItemStack stack, byte[] sideCache) {

		if (sideCache.length < 6) {
			return false;
		}
		if (stack.stackTagCompound == null) {
			stack.setTagCompound(new NBTTagCompound());
		}
		stack.stackTagCompound.setByteArray("SideCache", sideCache.clone());
		return true;
	}

	public static byte getFacing(ItemStack stack) {

		return stack.stackTagCompound == null || !stack.stackTagCompound.hasKey("Facing") ? DEFAULT_FACING : stack.stackTagCompound.getByte("Facing");
	}

	public static byte[] getSideCache(ItemStack stack) {

		if (stack.stackTagCompound == null) {
			return DEFAULT_SIDES.clone();
		}
		byte[] retSides = stack.stackTagCompound.getByteArray("SideCache");
		return retSides.length < 6 ? DEFAULT_SIDES.clone() : retSides;
	}

	public static byte[] getSideCache(ItemStack stack, byte[] defaultSides) {

		if (stack.stackTagCompound == null) {
			return defaultSides.clone();
		}
		byte[] retSides = stack.stackTagCompound.getByteArray("SideCache");
		return retSides.length < 6 ? defaultSides.clone() : retSides;
	}

}
