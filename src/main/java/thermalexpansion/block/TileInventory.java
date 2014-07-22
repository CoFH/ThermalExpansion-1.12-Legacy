package thermalexpansion.block;

import cofh.api.tileentity.ISecurable;
import cofh.core.CoFHProps;
import cofh.network.CoFHPacket;
import cofh.util.BlockHelper;
import cofh.util.ServerHelper;
import cofh.util.StringHelper;
import cpw.mods.fml.relauncher.Side;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.gui.GuiHandler;
import thermalexpansion.util.Utils;

public abstract class TileInventory extends TileTEBase implements IInventory, ISecurable {

	protected String owner = CoFHProps.DEFAULT_OWNER;
	protected AccessMode access = AccessMode.PUBLIC;
	protected boolean canAccess = true;

	public ItemStack[] inventory = new ItemStack[0];

	public boolean canAccess() {

		return canAccess;
	}

	public boolean isSecured() {

		return !owner.equals(CoFHProps.DEFAULT_OWNER);
	}

	public boolean enableSecurity() {

		return true;
	}

	public boolean transferItem(int slot, int amount, int side) {

		if (inventory[slot] == null || slot > inventory.length) {
			return false;
		}
		ItemStack stack = inventory[slot].copy();
		amount = Math.min(amount, stack.stackSize);
		stack.stackSize = amount;

		TileEntity curTile = BlockHelper.getAdjacentTileEntity(this, side);
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

	/* GUI METHODS */
	@Override
	public int getInvSlotCount() {

		return inventory.length;
	}

	@Override
	public boolean hasGui() {

		return true;
	}

	@Override
	public boolean openGui(EntityPlayer player) {

		if (canPlayerAccess(player.getDisplayName())) {
			if (hasGui()) {
				player.openGui(ThermalExpansion.instance, GuiHandler.TILE_ID, worldObj, xCoord, yCoord, zCoord);
			}
			return hasGui();
		}
		if (ServerHelper.isServerWorld(worldObj)) {
			player.addChatMessage(new ChatComponentText(StringHelper.localize("chat.cofh.secure1") + " " + owner + "! "
					+ StringHelper.localize("chat.cofh.secure2")));
		}
		return hasGui();
	}

	@Override
	public void receiveGuiNetworkData(int i, int j) {

		if (j == 0) {
			canAccess = false;
		} else {
			canAccess = true;
		}
	}

	@Override
	public void sendGuiNetworkData(Container container, ICrafting player) {

		super.sendGuiNetworkData(container, player);

		player.sendProgressBarUpdate(container, 0, canPlayerAccess(((EntityPlayer) player).getDisplayName()) ? 1 : 0);
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		access = AccessMode.values()[nbt.getByte("Access")];
		owner = nbt.getString("Owner");

		if (!enableSecurity()) {
			access = AccessMode.PUBLIC;
		}
		readInventoryFromNBT(nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setByte("Access", (byte) access.ordinal());
		nbt.setString("Owner", owner);

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

	/* NETWORK METHODS */
	@Override
	public CoFHPacket getPacket() {

		CoFHPacket payload = super.getPacket();

		payload.addByte((byte) access.ordinal());
		payload.addString(owner);

		return payload;
	}

	/* ITilePacketHandler */
	@Override
	public void handleTilePacket(CoFHPacket payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		access = ISecurable.AccessMode.values()[payload.getByte()];

		if (!isServer) {
			owner = payload.getString();
		} else {
			payload.getString();
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
		markChunkDirty();
	}

	@Override
	public String getInventoryName() {

		return tileName.isEmpty() ? getName() : tileName;
	}

	@Override
	public boolean hasCustomInventoryName() {

		return !tileName.isEmpty();
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

	/* ISecureable */
	@Override
	public boolean setAccess(AccessMode access) {

		this.access = access;
		sendUpdatePacket(Side.SERVER);
		return true;
	}

	@Override
	public AccessMode getAccess() {

		return access;
	}

	@Override
	public boolean setOwnerName(String name) {

		if (owner.equals(CoFHProps.DEFAULT_OWNER)) {
			owner = name;
			markChunkDirty();
			return true;
		}
		return false;
	}

	@Override
	public String getOwnerName() {

		return owner;
	}

}
