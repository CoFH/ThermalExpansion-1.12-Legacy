package thermalexpansion.util;

import buildcraft.api.tools.IToolWrench;
import buildcraft.api.transport.IPipeTile;

import cofh.api.item.IAugmentItem;
import cofh.api.item.IToolHammer;
import cofh.api.transport.IItemDuct;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.InventoryHelper;
import cofh.lib.util.helpers.ItemHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import thermalexpansion.item.TEItems;

public class Utils {

	public static int statId = 20000;

	/* ID FUNCTIONS */
	public static int getStatId() {

		statId++;
		return statId;
	}

	/* ITEM FUNCTIONS */
	public static boolean isAugmentItem(ItemStack container) {

		return container != null && container.getItem() instanceof IAugmentItem;
	}

	/* TILE FUNCTIONS */
	public static int addToAdjacentInventory(TileEntity tile, int from, ItemStack stack) {

		return addToAdjacentInventory(tile.xCoord, tile.yCoord, tile.zCoord, tile.getWorldObj(), from, stack);
	}

	public static int addToAdjacentInventory(int x, int y, int z, World worldObj, int from, ItemStack stack) {

		TileEntity theTile = BlockHelper.getAdjacentTileEntity(worldObj, x, y, z, from);

		if (!(InventoryHelper.isInsertion(theTile))) {
			return stack.stackSize;
		}
		stack = InventoryHelper.addToInsertion(theTile, from, stack);

		return stack == null ? 0 : stack.stackSize;
	}

	public static int addToInventory(TileEntity theTile, int from, ItemStack stack) {

		if (!(InventoryHelper.isInsertion(theTile))) {
			return stack.stackSize;
		}
		stack = InventoryHelper.addToInsertion(theTile, from, stack);

		return stack == null ? 0 : stack.stackSize;
	}

	public static int addToInventory(int xCoord, int yCoord, int zCoord, World worldObj, int from, ItemStack stack) {

		TileEntity theTile = worldObj.getTileEntity(xCoord, yCoord, zCoord);

		if (!InventoryHelper.isInsertion(theTile)) {
			return stack.stackSize;
		}
		stack = InventoryHelper.addToInsertion(theTile, from, stack);

		return stack == null ? 0 : stack.stackSize;
	}

	public static int addToInventory(IInventory tile, int from, ItemStack stack) {

		if (!InventoryHelper.isInsertion(tile)) {
			return stack.stackSize;
		}
		stack = InventoryHelper.addToInsertion(tile, from, stack);

		return stack == null ? 0 : stack.stackSize;
	}

	public static int canAddToInventory(int xCoord, int yCoord, int zCoord, World worldObj, int from, ItemStack stack) {

		TileEntity tile = worldObj.getTileEntity(xCoord, yCoord, zCoord);

		if (!InventoryHelper.isInventory(tile)) {
			return stack.stackSize;
		}
		stack = InventoryHelper.simulateInsertItemStackIntoInventory((IInventory) tile, stack, from ^ 1);

		return stack == null ? 0 : stack.stackSize;
	}

	public static int addToPipeTile(TileEntity theTile, int side, ItemStack stack) {

		if (bcPipeExists) {
			return addToPipeTile_do(theTile, side, stack);
		}
		return 0;
	}

	private static int addToPipeTile_do(TileEntity tile, int side, ItemStack stack) {

		if (tile instanceof IPipeTile) {
			int used = ((IPipeTile) tile).injectItem(stack, true, ForgeDirection.VALID_DIRECTIONS[side ^ 1]);
			return used;
		}
		return 0;
	}

	/* QUERY FUNCTIONS */
	public static boolean isAdjacentInventory(TileEntity tile, int side) {

		return isAdjacentInventory(tile.xCoord, tile.yCoord, tile.zCoord, tile.getWorldObj(), side);
	}

	public static boolean isAdjacentInventory(int x, int y, int z, World worldObj, int side) {

		TileEntity tile = BlockHelper.getAdjacentTileEntity(worldObj, x, y, z, side);

		return isInventory(tile, side);
	}

	public static boolean isInventory(TileEntity tile, int side) {

		if (tile instanceof ISidedInventory && ((ISidedInventory) tile).getAccessibleSlotsFromSide(BlockHelper.SIDE_OPPOSITE[side]).length <= 0) {
			return false;
		}
		if (tile instanceof IInventory && ((IInventory) tile).getSizeInventory() > 0) {
			return true;
		}
		if (tile instanceof IItemDuct) {
			return true;
		}
		return false;
	}

	public static boolean isHoldingBlock(EntityPlayer player) {

		Item equipped = player.getCurrentEquippedItem() != null ? player.getCurrentEquippedItem().getItem() : null;
		return equipped instanceof ItemBlock;
	}

	public static boolean isHoldingUsableWrench(EntityPlayer player, int x, int y, int z) {

		Item equipped = player.getCurrentEquippedItem() != null ? player.getCurrentEquippedItem().getItem() : null;
		if (equipped instanceof IToolHammer) {
			return ((IToolHammer) equipped).isUsable(player.getCurrentEquippedItem(), player, x, y, z);
		} else if (bcWrenchExists) {
			return canHandleBCWrench(equipped, player, x, y, z);
		}
		return false;
	}

	public static void usedWrench(EntityPlayer player, int x, int y, int z) {

		Item equipped = player.getCurrentEquippedItem() != null ? player.getCurrentEquippedItem().getItem() : null;
		if (equipped instanceof IToolHammer) {
			((IToolHammer) equipped).toolUsed(player.getCurrentEquippedItem(), player, x, y, z);
		} else if (bcWrenchExists) {
			bcWrenchUsed(equipped, player, x, y, z);
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

	private static boolean canHandleBCWrench(Item item, EntityPlayer p, int x, int y, int z) {

		return item instanceof IToolWrench && ((IToolWrench) item).canWrench(p, x, y, z);
	}

	private static void bcWrenchUsed(Item item, EntityPlayer p, int x, int y, int z) {

		if (item instanceof IToolWrench) {
			((IToolWrench) item).wrenchUsed(p, x, y, z);
		}
	}

	// }

	public static boolean isHoldingMultimeter(EntityPlayer player) {

		return ItemHelper.isPlayerHoldingItemStack(TEItems.toolMultimeter, player);
	}

	public static boolean isHoldingDebugger(EntityPlayer player) {

		return ItemHelper.isPlayerHoldingItemStack(TEItems.toolDebugger, player);
	}

	public static boolean isHoldingServo(EntityPlayer player) {

		return ItemHelper.isPlayerHoldingItemStack(TEItems.pneumaticServo, player);
	}

	public static boolean isPipeTile(TileEntity tile) {

		return bcPipeExists && isPipeTile_do(tile);
	}

	private static boolean isPipeTile_do(TileEntity tile) {

		return tile instanceof IPipeTile;
	}

}
