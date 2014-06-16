package thermalexpansion.block;

import cofh.network.CoFHPacket;
import cofh.network.ITilePacketHandler;
import cofh.util.BlockHelper;
import cofh.util.ServerHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

import thermalexpansion.util.Utils;

public abstract class TileInventory extends TileTEBase implements IInventory, ITilePacketHandler {

	public ItemStack[] inventory = new ItemStack[0];
	protected String invName = "";

	TileEntity curTile;

	public boolean transferItem(int slot, int amount, int side) {

		if (inventory[slot] == null || slot > inventory.length) {
			return false;
		}
		ItemStack stack = inventory[slot].copy();
		amount = Math.min(amount, stack.stackSize);
		stack.stackSize = amount;

		curTile = BlockHelper.getAdjacentTileEntity(this, side);
		/* Add to Adjacent Inventory */
		if (Utils.isInventory(curTile, side)) {
			inventory[slot].stackSize -= amount - Utils.addToInventory(curTile, side, stack);
			if (inventory[slot].stackSize <= 0) {
				inventory[slot] = null;
			}
			return true;
		}
		/* Add to Adjacent Pipe */
		if (Utils.isPipeTile(curTile)) {
			inventory[slot].stackSize -= Utils.addToPipeTile(curTile, side, stack);
			if (inventory[slot].stackSize <= 0) {
				inventory[slot] = null;
			}
			return true;
		}
		return false;
	}

	public boolean setInvName(String name) {

		if (name.isEmpty()) {
			return false;
		}
		invName = name;
		return true;
	}

	/* NETWORK METHODS */
	@Override
	public CoFHPacket getPacket() {

		CoFHPacket payload = super.getPacket();
		payload.addString(invName);
		return payload;
	}

	@Override
	public void handleTilePacket(CoFHPacket payload, boolean isServer) {

		if (ServerHelper.isClientWorld(worldObj)) {
			invName = payload.getString();
		} else {
			payload.getString();
		}
	}

	/* GUI METHODS */
	@Override
	public int getInvSlotCount() {

		return inventory.length;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		if (nbt.hasKey("Name")) {
			invName = nbt.getString("Name");
		}
		readInventoryFromNBT(nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		if (!invName.isEmpty()) {
			nbt.setString("Name", invName);
		}
		writeInventoryToNBT(nbt);
	}

	public void readInventoryFromNBT(NBTTagCompound nbt) {

		NBTTagList list = nbt.getTagList("Inventory", 10);
		inventory = new ItemStack[inventory.length];
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound tag = list.getCompoundTagAt(i);
			int slot = tag.getInteger("Slot");

			if (slot >= 0 && slot < inventory.length) {
				inventory[slot] = ItemStack.loadItemStackFromNBT(tag);
			}
		}
	}

	public void writeInventoryToNBT(NBTTagCompound nbt) {

		if (inventory.length <= 0) {
			return;
		}
		NBTTagList list = new NBTTagList();
		for (int i = 0; i < inventory.length; i++) {
			if (inventory[i] != null) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setInteger("Slot", i);
				inventory[i].writeToNBT(tag);
				list.appendTag(tag);
			}
		}
		nbt.setTag("Inventory", list);
	}

	/* IInventory */
	@Override
	public int getSizeInventory() {

		return inventory.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {

		return inventory[slot];
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {

		if (inventory[slot] == null) {
			return null;
		}
		if (inventory[slot].stackSize <= amount) {
			amount = inventory[slot].stackSize;
		}
		ItemStack stack = inventory[slot].splitStack(amount);

		if (inventory[slot].stackSize <= 0) {
			inventory[slot] = null;
		}
		return stack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {

		if (inventory[slot] == null) {
			return null;
		}
		ItemStack stack = inventory[slot];
		inventory[slot] = null;
		return stack;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {

		inventory[slot] = stack;

		if (stack != null && stack.stackSize > getInventoryStackLimit()) {
			stack.stackSize = getInventoryStackLimit();
		}
	}

	@Override
	public String getInventoryName() {

		return invName.isEmpty() ? getName() : invName;
	}

	@Override
	public boolean hasCustomInventoryName() {

		return true;
	}

	@Override
	public int getInventoryStackLimit() {

		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {

		return isUseable(player);
	}

	@Override
	public void openInventory() {

	}

	@Override
	public void closeInventory() {

	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		return true;
	}

}
