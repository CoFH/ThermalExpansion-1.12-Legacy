package cofh.thermalexpansion.util;

import cofh.api.item.IToolHammer;
import cofh.lib.util.helpers.InventoryHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

public class Utils {

	/* QUERY FUNCTIONS */
	public static boolean isAccessibleInput(TileEntity tile, EnumFacing side) {

		return InventoryHelper.hasItemHandlerCap(tile, side.getOpposite()) && InventoryHelper.getItemHandlerCap(tile, side.getOpposite()).getSlots() > 0;
	}

	public static boolean isAccessibleOutput(TileEntity tile, EnumFacing side) {

		return InventoryHelper.hasItemHandlerCap(tile, side.getOpposite()) && InventoryHelper.getItemHandlerCap(tile, side.getOpposite()).getSlots() > 0;
	}

	public static boolean isHoldingUsableWrench(EntityPlayer player, RayTraceResult traceResult) {

		EnumHand hand = EnumHand.MAIN_HAND;
		ItemStack stack = player.getHeldItem(hand);
		if (stack.isEmpty()) {
			hand = EnumHand.OFF_HAND;
			stack = player.getHeldItem(hand);
		}
		if (stack.isEmpty()) {
			return false;
		}
		if (stack.getItem() instanceof IToolHammer) {
			BlockPos pos = traceResult.getBlockPos();
			return ((IToolHammer) stack.getItem()).isUsable(stack, player, pos);
		} else if (bcWrenchExists) {
			return canHandleBCWrench(player, hand, stack, traceResult);
		}
		return false;
	}

	public static void usedWrench(EntityPlayer player, RayTraceResult traceResult) {

		EnumHand hand = EnumHand.MAIN_HAND;
		ItemStack stack = player.getHeldItem(hand);
		if (stack.isEmpty()) {
			hand = EnumHand.OFF_HAND;
			stack = player.getHeldItem(hand);
		}
		if (stack.isEmpty()) {
			return;
		}
		if (stack.getItem() instanceof IToolHammer) {
			BlockPos pos = traceResult.getBlockPos();
			((IToolHammer) stack.getItem()).toolUsed(stack, player, pos);
		} else if (bcWrenchExists) {
			bcWrenchUsed(player, hand, stack, traceResult);
		}
	}

	// BCHelper {
	private static boolean bcWrenchExists = false;
	private static boolean bcPipeExists = false;

	static {
		try {
			Class.forName("buildcraft.api.tools.IToolWrench");
			bcWrenchExists = true;
		} catch (Throwable t) {
			// pokemon!
		}
		try {
			Class.forName("buildcraft.api.transport.IPipeTile");
			bcPipeExists = true;
		} catch (Throwable t) {
			// pokemon!
		}
	}

	private static boolean canHandleBCWrench(EntityPlayer player, EnumHand hand, ItemStack wrench, RayTraceResult result) {

		return false;//wrench.getItem() instanceof IToolWrench && ((IToolWrench) wrench.getItem()).canWrench(player, hand, wrench, result);
	}

	private static void bcWrenchUsed(EntityPlayer player, EnumHand hand, ItemStack wrench, RayTraceResult result) {

		//if (wrench.getItem() instanceof IToolWrench) {
		//    ((IToolWrench) wrench.getItem()).wrenchUsed(player, hand, wrench, result);
		//}
	}

	public static boolean isPipeTile(TileEntity tile) {

		return bcPipeExists && isPipeTile_do(tile);
	}

	private static boolean isPipeTile_do(TileEntity tile) {

		return false;//tile instanceof IPipeTile;
	}

	// }

}
