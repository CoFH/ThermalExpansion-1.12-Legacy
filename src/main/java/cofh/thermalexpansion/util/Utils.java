package cofh.thermalexpansion.util;


import codechicken.lib.inventory.InventoryUtils;
import codechicken.lib.util.ItemUtils;
import cofh.api.item.IAugmentItem;
import cofh.api.item.IToolHammer;
import cofh.api.tileentity.IItemDuct;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.InventoryHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class Utils {

	/* ITEM FUNCTIONS */
	public static boolean isAugmentItem(ItemStack container) {

		return container != null && container.getItem() instanceof IAugmentItem;
	}

	/* TILE FUNCTIONS - INSERTION */
	public static int addToAdjacentInsertion(TileEntity tile, EnumFacing from, ItemStack stack) {

		return addToAdjacentInsertion(tile.getPos(), tile.getWorld(), from, stack);
	}

	public static int addToAdjacentInsertion(BlockPos pos, World worldObj, EnumFacing from, ItemStack stack) {

		TileEntity theTile = BlockHelper.getAdjacentTileEntity(worldObj, pos, from);

		if (!InventoryHelper.isInsertion(theTile)) {
			return stack.stackSize;
		}
		stack = InventoryHelper.addToInsertion(theTile, from, stack);
		return stack == null ? 0 : stack.stackSize;
	}

	public static int addToInsertion(TileEntity theTile, EnumFacing from, ItemStack stack) {

		if (!(InventoryHelper.isInsertion(theTile))) {
			return stack.stackSize;
		}
		stack = InventoryHelper.addToInsertion(theTile, from, stack);

		return stack == null ? 0 : stack.stackSize;
	}

	public static int addToInsertion(BlockPos pos, World worldObj, EnumFacing from, ItemStack stack) {

		TileEntity theTile = worldObj.getTileEntity(pos);

		if (!InventoryHelper.isInsertion(theTile)) {
			return stack.stackSize;
		}
		stack = InventoryHelper.addToInsertion(theTile, from, stack);

		return stack == null ? 0 : stack.stackSize;
	}

//	@Deprecated
//	public static int addToInsertion(IInventory tile, EnumFacing from, ItemStack stack) {
//
//		if (!InventoryHelper.isInsertion(tile)) {
//			return stack.stackSize;
//		}
//		stack = InventoryHelper.addToInsertion(tile, from, stack);
//
//		return stack == null ? 0 : stack.stackSize;
//	}
//
//  @Deprecated
//	public static int canAddToInventory(BlockPos pos, World worldObj, EnumFacing from, ItemStack stack) {
//
//		TileEntity tile = worldObj.getTileEntity(pos);
//
//		if (!InventoryHelper.isInventory(tile)) {
//			return stack.stackSize;
//		}
//		stack = InventoryHelper.simulateInsertItemStackIntoInventory((IInventory) tile, stack, from.getOpposite());
//
//		return stack == null ? 0 : stack.stackSize;
//	}

	public static int addToPipeTile(TileEntity theTile, EnumFacing side, ItemStack stack) {

		if (bcPipeExists) {
			return addToPipeTile_do(theTile, side, stack);
		}
		return 0;
	}

	private static int addToPipeTile_do(TileEntity tile, EnumFacing side, ItemStack stack) {

		//if (tile instanceof IPipeTile) {
		//	@SuppressWarnings("deprecation")
		//	int used = ((IPipeTile) tile).injectItem(stack, true, ForgeDirection.VALID_DIRECTIONS[side ^ 1]);
		//	return used;
		//}
		return 0;
	}

	/* TILE FUNCTIONS - EXTRACTION */
	// public static ItemStack extractFromAdjacentInventoryIntoSlot(TileEntity tile, int from, int slot, int amount) {
	//
	// IInventory theInv = (IInventory) tile;
	// TileEntity theTile = BlockHelper.getAdjacentTileEntity(tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord, from);
	// ItemStack stack = theInv.getStackInSlot(slot);
	//
	// if (!InventoryHelper.isInventory(theTile)) {
	// return stack;
	// }
	// stack = InventoryHelper.addToInsertion(theTile, from, stack);
	// return stack == null ? 0 : stack.stackSize;
	// }

	/* QUERY FUNCTIONS */
	public static boolean isAdjacentInput(TileEntity tile, EnumFacing side) {

		return isAdjacentInput(tile.getPos(), tile.getWorld(), side);
	}

	public static boolean isAdjacentInput(BlockPos pos, World worldObj, EnumFacing side) {

		TileEntity tile = BlockHelper.getAdjacentTileEntity(worldObj, pos, side);

		return isAccessibleInput(tile, side);
	}

	public static boolean isAdjacentOutput(TileEntity tile, EnumFacing side) {

		return isAdjacentOutput(tile.getPos(), tile.getWorld(), side);
	}

	public static boolean isAdjacentOutput(BlockPos pos, World worldObj, EnumFacing side) {

		TileEntity tile = BlockHelper.getAdjacentTileEntity(worldObj, pos, side);

		return isAccessibleOutput(tile, side);
	}

	public static boolean isAccessibleInput(TileEntity tile, EnumFacing side) {
        return InventoryUtils.hasItemHandlerCap(tile, side.getOpposite()) && InventoryUtils.getItemHandlerCap(tile, side.getOpposite()).getSlots() > 0;
    }

	public static boolean isAccessibleOutput(TileEntity tile, EnumFacing side) {
	    if (InventoryUtils.hasItemHandlerCap(tile, side.getOpposite())) {
	        return InventoryUtils.getItemHandlerCap(tile, side.getOpposite()).getSlots() > 0;
        }
        return tile instanceof IItemDuct;
    }

	public static boolean isHoldingBlock(EntityPlayer player) {
        ItemStack held = ItemUtils.getHeldStack(player);
        if (held == null){
            return false;
        }
        Item equipped = held.getItem();
		return equipped instanceof ItemBlock;
	}

    public static boolean isHoldingUsableWrench(EntityPlayer player, RayTraceResult traceResult) {

        EnumHand hand = EnumHand.MAIN_HAND;
        ItemStack stack = player.getHeldItem(hand);
        if (stack == null) {
            hand = EnumHand.OFF_HAND;
            stack = player.getHeldItem(hand);
        }
        if (stack == null) {
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
        if (stack == null) {
            hand = EnumHand.OFF_HAND;
            stack = player.getHeldItem(hand);
        }
        if (stack == null) {
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
