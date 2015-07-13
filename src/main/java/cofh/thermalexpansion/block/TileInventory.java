package cofh.thermalexpansion.block;

import cofh.api.tileentity.ISecurable;
import cofh.core.CoFHProps;
import cofh.core.network.PacketCoFHBase;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.SecurityHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.gui.GuiHandler;
import cofh.thermalexpansion.util.Utils;
import com.google.common.base.Strings;
import com.mojang.authlib.GameProfile;
import cpw.mods.fml.relauncher.Side;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;

public abstract class TileInventory extends TileTEBase implements IInventory, ISecurable {

	protected GameProfile owner = CoFHProps.DEFAULT_OWNER;
	protected AccessMode access = AccessMode.PUBLIC;
	protected boolean canAccess = true;
	protected boolean inWorld = false;

	public ItemStack[] inventory = new ItemStack[0];

	public void cofh_validate() {

		inWorld = true;
	}

	public void cofh_invalidate() {

		inWorld = false;
	}

	public boolean canAccess() {

		return canAccess;
	}

	public boolean isSecured() {

		return !SecurityHelper.isDefaultUUID(owner.getId());
	}

	public boolean enableSecurity() {

		return true;
	}

	public boolean extractItem(int slot, int amount, int side) {

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
			if (adjInv instanceof ISidedInventory) {
				ISidedInventory sidedInv = (ISidedInventory) adjInv;
				int slots[] = sidedInv.getAccessibleSlotsFromSide(side);

				if (slots == null) {
					return false;
				}
				for (int i = 0; i < slots.length && amount > 0; i++) {
					ItemStack queryStack = sidedInv.getStackInSlot(slots[i]);
					if (sidedInv.canExtractItem(slots[i], queryStack, side ^ 1)) {
						if (stack == null) {
							if (isItemValidForSlot(slot, queryStack)) {
								int toExtract = Math.min(amount, queryStack.stackSize);
								stack = ItemHelper.cloneStack(queryStack, toExtract);
								queryStack.stackSize -= toExtract;

								if (queryStack.stackSize <= 0) {
									sidedInv.setInventorySlotContents(slots[i], null);
								} else {
									sidedInv.setInventorySlotContents(slots[i], queryStack);
								}
								amount -= toExtract;
							}
						} else if (ItemHelper.itemsEqualWithMetadata(stack, queryStack)) {
							int toExtract = Math.min(stack.getMaxStackSize() - stack.stackSize, Math.min(amount, queryStack.stackSize));
							stack.stackSize += toExtract;
							queryStack.stackSize -= toExtract;

							if (queryStack.stackSize <= 0) {
								sidedInv.setInventorySlotContents(slots[i], null);
							} else {
								sidedInv.setInventorySlotContents(slots[i], queryStack);
							}
							amount -= toExtract;
						}
					}
				}
			} else {
				IInventory inv = (IInventory) adjInv;
				for (int i = 0; i < inv.getSizeInventory() && amount > 0; i++) {
					ItemStack queryStack = inv.getStackInSlot(i);
					if (stack == null) {
						if (isItemValidForSlot(slot, queryStack)) {
							int toExtract = Math.min(amount, queryStack.stackSize);
							stack = ItemHelper.cloneStack(queryStack, toExtract);
							queryStack.stackSize -= toExtract;

							if (queryStack.stackSize <= 0) {
								inv.setInventorySlotContents(i, null);
							} else {
								inv.setInventorySlotContents(i, queryStack);
							}
							amount -= toExtract;
						}
					} else if (ItemHelper.itemsEqualWithMetadata(stack, queryStack)) {
						int toExtract = Math.min(stack.getMaxStackSize() - stack.stackSize, Math.min(amount, queryStack.stackSize));
						stack.stackSize += toExtract;
						queryStack.stackSize -= toExtract;

						if (queryStack.stackSize <= 0) {
							inv.setInventorySlotContents(i, null);
						} else {
							inv.setInventorySlotContents(i, queryStack);
						}
						amount -= toExtract;
					}
				}
			}
			if (initialAmount != amount) {
				inventory[slot] = stack;
				adjInv.markDirty();
			}
			return true;
		}
		return false;
	}

	public boolean transferItem(int slot, int amount, int side) {

		if (inventory[slot] == null || slot > inventory.length) {
			return false;
		}
		ItemStack stack = inventory[slot].copy();
		amount = Math.min(amount, stack.stackSize);
		stack.stackSize = amount;
		int added = 0;

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
		added = 0;
		/* Add to Adjacent Pipe */
		if (Utils.isPipeTile(curTile)) {
			added = Utils.addToPipeTile(curTile, side, stack);
			if (added <= 0) {
				return false;
			}
			inventory[slot].stackSize -= added;
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

		if (canPlayerAccess(player)) {
			if (hasGui()) {
				player.openGui(ThermalExpansion.instance, GuiHandler.TILE_ID, worldObj, xCoord, yCoord, zCoord);
			}
			return hasGui();
		}
		if (ServerHelper.isServerWorld(worldObj)) {
			player.addChatMessage(new ChatComponentText(StringHelper.localize("chat.cofh.secure.1") + " " + getOwnerName() + "! "
					+ StringHelper.localize("chat.cofh.secure.2")));
		}
		return false;
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

		player.sendProgressBarUpdate(container, 0, canPlayerAccess(((EntityPlayer) player)) ? 1 : 0);
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		owner = CoFHProps.DEFAULT_OWNER;
		access = AccessMode.values()[nbt.getByte("Access")];

		String uuid = nbt.getString("OwnerUUID");
		String name = nbt.getString("Owner");
		if (!Strings.isNullOrEmpty(uuid)) {
			setOwner(new GameProfile(UUID.fromString(uuid), name));
		} else {
			setOwnerName(name);
		}

		if (!enableSecurity()) {
			access = AccessMode.PUBLIC;
		}
		readInventoryFromNBT(nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setByte("Access", (byte) access.ordinal());
		nbt.setString("OwnerUUID", owner.getId().toString());
		nbt.setString("Owner", owner.getName());

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
	public PacketCoFHBase getPacket() {

		PacketCoFHBase payload = super.getPacket();

		payload.addByte((byte) access.ordinal());
		payload.addUUID(owner.getId());
		payload.addString(owner.getName());

		return payload;
	}

	/* ITilePacketHandler */
	@Override
	public void handleTilePacket(PacketCoFHBase payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		access = ISecurable.AccessMode.values()[payload.getByte()];

		if (!isServer) {
			owner = CoFHProps.DEFAULT_OWNER;
			setOwner(new GameProfile(payload.getUUID(), payload.getString()));
		} else {
			payload.getUUID();
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
		if (inWorld) {
			markChunkDirty();
		}
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

	/* ISecurable */
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

		if (MinecraftServer.getServer() == null) {
			return false;
		}
		if (Strings.isNullOrEmpty(name) || CoFHProps.DEFAULT_OWNER.getName().equalsIgnoreCase(name)) {
			return false;
		}
		String uuid = PreYggdrasilConverter.func_152719_a(name);
		if (Strings.isNullOrEmpty(uuid)) {
			return false;
		}
		return setOwner(new GameProfile(UUID.fromString(uuid), name));
	}

	@Override
	public boolean setOwner(GameProfile profile) {

		if (SecurityHelper.isDefaultUUID(owner.getId())) {
			owner = profile;
			if (!SecurityHelper.isDefaultUUID(owner.getId())) {
				if (MinecraftServer.getServer() != null) {
					new Thread("CoFH User Loader") {

						@Override
						public void run() {

							owner = SecurityHelper.getProfile(owner.getId(), owner.getName());
						}
					}.start();
				}
				if (inWorld) {
					markChunkDirty();
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public GameProfile getOwner() {

		return owner;
	}

	@Override
	public String getOwnerName() {

		String name = owner.getName();
		if (name == null) {
			return StringHelper.localize("info.cofh.anotherplayer");
		}
		return name;
	}

}
