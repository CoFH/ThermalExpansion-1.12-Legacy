package thermalexpansion.block.cache;

import cofh.api.tileentity.IReconfigurableFacing;
import cofh.api.tileentity.ISidedBlockTexture;
import cofh.network.CoFHPacket;
import cofh.render.IconRegistry;
import cofh.util.BlockHelper;
import cofh.util.ItemHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;

import powercrystals.minefactoryreloaded.api.IDeepStorageUnit;

import thermalexpansion.block.TileInventory;

public class TileCache extends TileInventory implements IReconfigurableFacing, ISidedInventory, ISidedBlockTexture, IDeepStorageUnit {

	public static void initialize() {

		GameRegistry.registerTileEntity(TileCache.class, "thermalexpansion.Cache");
	}

	public static final int[] SIZE = { Integer.MAX_VALUE, 10000, 40000, 160000, 640000 };
	public static final int[] SLOTS = { 0, 1 };

	public byte type;
	public byte facing = 3;
	public boolean locked;

	public ItemStack storedStack;
	public int maxCacheStackSize;

	public TileCache() {

		inventory = new ItemStack[2];
	}

	public TileCache(int metadata) {

		type = (byte) metadata;
		inventory = new ItemStack[2];
		maxCacheStackSize = SIZE[type] - 64 * 2;
	}

	@Override
	public String getName() {

		return "tile.thermalexpansion.cache." + BlockCache.NAMES[getType()] + ".name";
	}

	@Override
	public int getType() {

		return type;
	}

	/* NETWORK METHODS */
	@Override
	public CoFHPacket getPacket() {

		CoFHPacket payload = super.getPacket();

		payload.addByte(type);
		payload.addByte(facing);
		payload.addBool(locked);
		payload.addItemStack(storedStack);
		return payload;
	}

	/* ITilePacketHandler */
	@Override
	public void handleTilePacket(CoFHPacket payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		type = payload.getByte();
		facing = payload.getByte();
		locked = payload.getBool();
		storedStack = payload.getItemStack();
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		type = nbt.getByte("Type");
		facing = nbt.getByte("Facing");
		locked = nbt.getBoolean("Lock");
		storedStack = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("Item"));
		maxCacheStackSize = SIZE[type] - storedStack.getMaxStackSize() * 2;

		super.readFromNBT(nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setByte("Type", type);
		nbt.setByte("Facing", facing);
		nbt.setBoolean("Lock", locked);
		nbt.setTag("Item", storedStack.writeToNBT(new NBTTagCompound()));
	}

	/* IReconfigurableFacing */
	@Override
	public int getFacing() {

		return facing;
	}

	@Override
	public boolean allowYAxisFacing() {

		return false;
	}

	@Override
	public boolean rotateBlock() {

		facing = BlockHelper.SIDE_LEFT[facing];
		sendUpdatePacket(Side.CLIENT);
		return true;
	}

	@Override
	public boolean setFacing(int side) {

		if (side < 2 || side > 5) {
			return false;
		}
		facing = (byte) side;
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
		sendUpdatePacket(Side.CLIENT);
		return true;
	}

	/* IInventory */
	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {

		inventory[slot] = stack;

		if (slot == 0) { // insertion!
			if (inventory[0] == null) {
				return;
			}
			if (storedStack == null) {
				storedStack = inventory[0].copy();
				inventory[0] = null;
				maxCacheStackSize = SIZE[type] - storedStack.getMaxStackSize() * 2;
			} else {
				storedStack.stackSize += inventory[0].stackSize + (inventory[1] == null ? 0 : inventory[1].stackSize);
				balanceStacks();
			}
		} else { // extraction!
			if (storedStack == null) {
				return;
			}
			storedStack.stackSize += (inventory[0] == null ? 0 : inventory[0].stackSize) + (inventory[1] == null ? 0 : inventory[1].stackSize);

			if (storedStack.stackSize > 0) {
				balanceStacks();
			} else {
				clearInventory();
			}
		}
		markDirty();
	}

	protected void balanceStacks() {

		inventory[1] = ItemHelper.cloneStack(storedStack, Math.min(storedStack.getMaxStackSize(), storedStack.stackSize));
		storedStack.stackSize -= inventory[1].stackSize;

		if (storedStack.stackSize > maxCacheStackSize) {
			inventory[0] = ItemHelper.cloneStack(storedStack, storedStack.stackSize - maxCacheStackSize);
			storedStack.stackSize = maxCacheStackSize;
		}
	}

	protected void clearInventory() {

		if (!locked) {
			storedStack = null;
		} else {
			storedStack.stackSize = 0;
		}
		inventory[0] = null;
		inventory[1] = null;
	}

	/* ISidedBlockTexture */
	@Override
	public IIcon getBlockTexture(int side, int pass) {

		if (side == 0) {
			return IconRegistry.getIcon("CacheBottom", type);
		} else if (side == 1) {
			return IconRegistry.getIcon("CacheTop", type);
		}
		return side != facing ? IconRegistry.getIcon("CacheSide", type) : IconRegistry.getIcon("CacheFace", type);
	}

	/* ISidedInventory */
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {

		return SLOTS;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int side) {

		return slot == 0 && (storedStack == null || stack != null && ItemHelper.itemsEqualWithMetadata(stack, storedStack, true));
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int side) {

		return slot == 1;
	}

	/* IDeepStorageUnit */
	@Override
	public ItemStack getStoredItemType() {

		return storedStack;
	}

	@Override
	public void setStoredItemCount(int amount) {

		if (storedStack != null && amount >= 0) {
			storedStack.stackSize = Math.min(amount, getMaxStoredCount());

			if (amount > 0) {
				balanceStacks();
			} else {
				clearInventory();
			}
		}
		markDirty();
	}

	@Override
	public void setStoredItemType(ItemStack stack, int amount) {

		if (stack != null && amount >= 0) {
			storedStack = ItemHelper.cloneStack(stack, Math.min(amount, getMaxStoredCount()));
			maxCacheStackSize = SIZE[type] - storedStack.getMaxStackSize() * 2;

			if (amount > 0) {
				balanceStacks();
			} else {
				clearInventory();
			}
		} else {
			clearInventory();
		}
		markDirty();
	}

	@Override
	public int getMaxStoredCount() {

		return SIZE[type];
	}

}
