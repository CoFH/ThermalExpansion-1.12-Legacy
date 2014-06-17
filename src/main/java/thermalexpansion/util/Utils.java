package thermalexpansion.util;

import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.tools.IToolWrench;
import buildcraft.api.transport.IPipeTile;

import cofh.api.item.IAugmentItem;
import cofh.api.transport.IItemDuct;
import cofh.util.BlockHelper;
import cofh.util.InventoryHelper;
import cofh.util.ItemHelper;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.core.TEProps;
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

	public static int addToInventory(IInventory theTile, int from, ItemStack stack) {

		if (!InventoryHelper.isInsertion(theTile)) {
			return stack.stackSize;
		}
		stack = InventoryHelper.addToInsertion(theTile, from, stack);

		return stack == null ? 0 : stack.stackSize;
	}

	public static int canAddToInventory(int xCoord, int yCoord, int zCoord, World worldObj, int from, ItemStack stack) {

		TileEntity theTile = worldObj.getTileEntity(xCoord, yCoord, zCoord);

		if (!InventoryHelper.isInventory(theTile)) {
			return stack.stackSize;
		}
		stack = InventoryHelper.simulateInsertItemStackIntoInventory((IInventory) theTile, stack, from ^ 1);

		return stack == null ? 0 : stack.stackSize;
	}

	public static int addToAdjacentPipeTile(TileEntity tile, int side, ItemStack stack) {

		TileEntity theTile = BlockHelper.getAdjacentTileEntity(tile, side);

		return addToPipeTile(theTile, side, stack);
	}

	public static int addToPipeTile(TileEntity theTile, int side, ItemStack stack) {

		if (theTile instanceof IPipeTile) {
			int used = ((IPipeTile) theTile).injectItem(stack, true, ForgeDirection.VALID_DIRECTIONS[side ^ 1]);
			return used;
		}
		return 0;
	}

	/* QUERY FUNCTIONS */
	public static boolean isAdjacentInventory(TileEntity tile, int side) {

		return isAdjacentInventory(tile.xCoord, tile.yCoord, tile.zCoord, tile.getWorldObj(), side);
	}

	public static boolean isAdjacentInventory(int x, int y, int z, World worldObj, int side) {

		TileEntity theTile = BlockHelper.getAdjacentTileEntity(worldObj, x, y, z, side);

		return isInventory(theTile, side);
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

	public static boolean isAdjacentPoweredTile(TileEntity tile, int side) {

		return isPoweredTile(BlockHelper.getAdjacentTileEntity(tile, side), side);
	}

	public static boolean isHoldingBlock(EntityPlayer player) {

		Item equipped = player.getCurrentEquippedItem() != null ? player.getCurrentEquippedItem().getItem() : null;
		return equipped instanceof ItemBlock;
	}

	public static boolean isHoldingUsableWrench(EntityPlayer player, int x, int y, int z) {

		Item equipped = player.getCurrentEquippedItem() != null ? player.getCurrentEquippedItem().getItem() : null;
		return equipped instanceof IToolWrench && ((IToolWrench) equipped).canWrench(player, x, y, z);
	}

	public static boolean isHoldingUsableWrench(EntityPlayer player) {

		Item equipped = player.getCurrentEquippedItem() != null ? player.getCurrentEquippedItem().getItem() : null;
		return equipped instanceof IToolWrench;
	}

	public static boolean isHoldingMultimeter(EntityPlayer player, int x, int y, int z) {

		return ItemHelper.isPlayerHoldingItemStack(TEItems.toolMultimeter, player);
	}

	public static boolean isHoldingServo(EntityPlayer player) {

		return ItemHelper.isPlayerHoldingItemStack(TEItems.pneumaticServo, player);
	}

	public static boolean isPoweredTile(TileEntity tile, int from) {

		if (tile instanceof IPowerReceptor) {
			PowerReceiver tilePP = ((IPowerReceptor) tile).getPowerReceiver(ForgeDirection.VALID_DIRECTIONS[from ^ 1]);
			return tilePP != null;
		}
		return false;
	}

	public static boolean isPowerReceptorFromSide(TileEntity tile, ForgeDirection orientation) {

		if (tile instanceof IPowerReceptor) {
			return ((IPowerReceptor) tile).getPowerReceiver(orientation) != null;
		}
		return false;
	}

	public static boolean isAdjacentPipeTile(TileEntity tile, int from) {

		return BlockHelper.getAdjacentTileEntity(tile, from) instanceof IPipeTile;
	}

	public static boolean isPipeTile(TileEntity tile) {

		return tile instanceof IPipeTile;
	}

	public static void dismantleLog(String playerName, Block block, int bMeta, double x, double y, double z) {

		if (TEProps.enableDismantleLogging) {
			ThermalExpansion.log.info("Player " + playerName + " dismantled " + " (" + block + ":" + bMeta + ") at (" + x + "," + y + "," + z + ")");
		}
	}

}
