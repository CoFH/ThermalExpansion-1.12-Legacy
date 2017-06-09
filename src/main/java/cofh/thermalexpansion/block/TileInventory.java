package cofh.thermalexpansion.block;

import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalexpansion.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

public abstract class TileInventory extends TileAugmentableSecure implements IInventory {

	public ItemStack[] inventory = new ItemStack[0];

	/* ITEM TRANSFER */
	public boolean extractItem(int slot, int amount, EnumFacing side) {

		if (slot > inventory.length) {
			return false;
		}
		ItemStack stack = inventory[slot];

		if (stack != null) {
			amount = Math.min(amount, stack.getMaxStackSize() - stack.stackSize);
			stack = inventory[slot].copy();
		}
		int initialAmount = amount;
		TileEntity adjInv = BlockHelper.getAdjacentTileEntity(this, side);

		if (Utils.isAccessibleInput(adjInv, side)) {
			IItemHandler inv = adjInv.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite());

			if (inv == null) {
				return false;
			}
			for (int i = 0; i < inv.getSlots() && amount > 0; i++) {
				ItemStack queryStack = inv.extractItem(i, amount, true);
				if (queryStack == null) {
					continue;
				}
				if (stack == null) {
					if (isItemValidForSlot(slot, queryStack)) {
						int toExtract = Math.min(amount, queryStack.stackSize);
						stack = inv.extractItem(i, toExtract, false);
						amount -= toExtract;
					}
				} else if (ItemHelper.itemsEqualWithMetadata(stack, queryStack, true)) {
					int toExtract = Math.min(stack.getMaxStackSize() - stack.stackSize, Math.min(amount, queryStack.stackSize));
					ItemStack extracted = inv.extractItem(i, toExtract, false);
					toExtract = Math.min(toExtract, extracted == null ? 0 : extracted.stackSize);
					stack.stackSize += toExtract;
					amount -= toExtract;
				}
			}
			if (initialAmount != amount) {
				inventory[slot] = stack;
				adjInv.markDirty();
				return true;
			}
		}
		return false;
	}

	public boolean transferItem(int slot, int amount, EnumFacing side) {

		if (inventory[slot] == null || slot > inventory.length) {
			return false;
		}
		ItemStack stack = inventory[slot].copy();
		amount = Math.min(amount, stack.stackSize);
		stack.stackSize = amount;
		int added;

		TileEntity curTile = BlockHelper.getAdjacentTileEntity(this, side);
		/* Add to Adjacent Inventory */
		if (Utils.isAccessibleOutput(curTile, side)) {
			added = Utils.addToInsertion(curTile, side, stack);
			if (added >= amount) {
				return false;
			}
			inventory[slot].stackSize -= amount - added;
			if (inventory[slot].stackSize <= 0) {
				inventory[slot] = null;
			}
			return true;
		}
		return false;
	}

	/* GUI METHODS */
	@Override
	public int getInvSlotCount() {

		return inventory.length;
	}

	@Override
	public boolean hasGui() {

		return true;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);
		readInventoryFromNBT(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);
		writeInventoryToNBT(nbt);
		return nbt;
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
		if (list.tagCount() > 0) {
			nbt.setTag("Inventory", list);
		}
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
	public ItemStack removeStackFromSlot(int slot) {

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
		markChunkDirty();
	}

	@Override
	public int getInventoryStackLimit() {

		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {

		return isUsable(player);
	}

	@Override
	public void openInventory(EntityPlayer player) {

	}

	@Override
	public void closeInventory(EntityPlayer player) {

	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		return true;
	}

	@Override
	public int getField(int id) {

		return 0;
	}

	@Override
	public void setField(int id, int value) {

	}

	@Override
	public int getFieldCount() {

		return 0;
	}

	@Override
	public void clear() {

	}

	/* CAPABILITIES */
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing from) {

		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, from);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {

		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if (this instanceof ISidedInventory && facing != null) {
				return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(new SidedInvWrapper(((ISidedInventory) this), facing));
			} else {
				return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(new InvWrapper(this));
			}
		}
		return super.getCapability(capability, facing);
	}

}
