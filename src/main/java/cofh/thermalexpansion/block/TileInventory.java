package cofh.thermalexpansion.block;

import codechicken.lib.util.ServerUtils;
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
import net.minecraft.inventory.IContainerListener;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

import javax.annotation.Nullable;

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

	//Extracts an item FROM an inventory to the machine.
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
						ItemStack extracted = inv.extractItem(slot, toExtract, false);
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
				player.openGui(ThermalExpansion.instance, GuiHandler.TILE_ID, worldObj, getPos().getX(), getPos().getY(), getPos().getZ());
			}
			return hasGui();
		}
		if (ServerHelper.isServerWorld(worldObj)) {
			player.addChatMessage(new TextComponentTranslation("chat.cofh.secure", getOwnerName()));
		}
		return false;
	}

	@Override
	public void receiveGuiNetworkData(int i, int j) {

        canAccess = j != 0;
	}

	@Override
	public void sendGuiNetworkData(Container container, IContainerListener player) {

		super.sendGuiNetworkData(container, player);
		if (player instanceof EntityPlayer) {
            player.sendProgressBarUpdate(container, 0, canPlayerAccess(((EntityPlayer) player)) ? 1 : 0);
        }
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
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setByte("Access", (byte) access.ordinal());
		nbt.setString("OwnerUUID", owner.getId().toString());
		nbt.setString("Owner", owner.getName());

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
		if (inWorld) {
			markChunkDirty();
		}
	}

	@Override
	public String getName() {

		return tileName.isEmpty() ? getName() : tileName;
	}

	@Override
	public boolean hasCustomName() {

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

    @Nullable
    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentString(getName());
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
        MinecraftServer server = ServerUtils.mc();
		if (server == null) {
			return false;
		}
		if (Strings.isNullOrEmpty(name) || CoFHProps.DEFAULT_OWNER.getName().equalsIgnoreCase(name)) {
			return false;
		}
		String uuid = PreYggdrasilConverter.convertMobOwnerIfNeeded(server, name);
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
				if (ServerUtils.mc() != null) {
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

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return super.hasCapability(capability, facing) || capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
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
