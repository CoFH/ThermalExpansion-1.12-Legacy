package thermalexpansion.block.cache;

import cofh.api.tileentity.IReconfigurableFacing;
import cofh.api.tileentity.ISidedBlockTexture;
import cofh.api.tileentity.ITileInfo;
import cofh.network.CoFHPacket;
import cofh.render.IconRegistry;
import cofh.util.BlockHelper;
import cofh.util.ItemHelper;
import cofh.util.MathHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import powercrystals.minefactoryreloaded.api.IDeepStorageUnit;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.block.TileInventory;

public class TileCache extends TileInventory implements IReconfigurableFacing, ISidedInventory, ISidedBlockTexture, ITileInfo, IDeepStorageUnit {

	public static void initialize() {

		GameRegistry.registerTileEntity(TileCache.class, "thermalexpansion.Cache");
	}

	public static int[] SIZE = { Integer.MAX_VALUE, 10000, 40000, 160000, 640000 };
	public static final int[] SLOTS = { 0, 1 };

	static {
		String category = "block.tweak";
		SIZE[4] = MathHelper.clampI(ThermalExpansion.config.get(category, "Cache.Resonant.Capacity", SIZE[4]), SIZE[4] / 8, SIZE[4] * 1000);
		SIZE[3] = MathHelper.clampI(ThermalExpansion.config.get(category, "Cache.Reinforced.Capacity", SIZE[3]), SIZE[3] / 8, SIZE[4]);
		SIZE[2] = MathHelper.clampI(ThermalExpansion.config.get(category, "Cache.Hardened.Capacity", SIZE[2]), SIZE[2] / 8, SIZE[3]);
		SIZE[1] = MathHelper.clampI(ThermalExpansion.config.get(category, "Cache.Basic.Capacity", SIZE[1]), SIZE[1] / 8, SIZE[2]);
	}

	public byte type;
	public byte facing = 3;
	public boolean locked;

	int meterTracker;
	int compareTracker;

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
	public int getComparatorInput(int side) {

		return compareTracker;
	}

	@Override
	public String getName() {

		return "tile.thermalexpansion.cache." + BlockCache.NAMES[getType()] + ".name";
	}

	@Override
	public int getType() {

		return type;
	}

	protected void balanceStacks() {

		inventory[1] = ItemHelper.cloneStack(storedStack, Math.min(storedStack.getMaxStackSize(), storedStack.stackSize));
		storedStack.stackSize -= inventory[1].stackSize;

		if (storedStack.stackSize > maxCacheStackSize) {
			inventory[0] = ItemHelper.cloneStack(storedStack, storedStack.stackSize - maxCacheStackSize);
			storedStack.stackSize = maxCacheStackSize;
		}
		updateTrackers();
	}

	protected void clearInventory() {

		if (!locked) {
			storedStack = null;
		} else {
			storedStack.stackSize = 0;
		}
		inventory[0] = null;
		inventory[1] = null;
		updateTrackers();
	}

	protected void updateTrackers() {

		int curScale = getScaledItemsStored(15);

		if (compareTracker != curScale) {
			compareTracker = curScale;
			callNeighborTileChange();
		}
		curScale = Math.min(8, getScaledItemsStored(9));

		if (meterTracker != curScale) {
			meterTracker = curScale;
			sendUpdatePacket(Side.CLIENT);
		}
	}

	public int getScaledItemsStored(int scale) {

		if (storedStack == null) {
			return 0;
		}
		int inv0 = 0;
		int inv1 = 0;

		if (inventory[1] != null) {
			inv1 = inventory[1].stackSize;

			if (inventory[0] != null) {
				inv0 = inventory[0].stackSize;
			}
		}
		return (storedStack.stackSize + inv0 + inv1) * scale / SIZE[type];
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

		if (nbt.hasKey("Item")) {
			storedStack = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("Item"));
			maxCacheStackSize = SIZE[type] - storedStack.getMaxStackSize() * 2;
		} else {
			maxCacheStackSize = SIZE[type] - 64 * 2;
		}
		super.readFromNBT(nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setByte("Type", type);
		nbt.setByte("Facing", facing);
		nbt.setBoolean("Lock", locked);

		if (storedStack != null) {
			nbt.setTag("Item", storedStack.writeToNBT(new NBTTagCompound()));
		}
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
			}
			balanceStacks();
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

	/* ISidedBlockTexture */
	@Override
	public IIcon getBlockTexture(int side, int pass) {

		if (pass == 1) {
			if (side != facing) {
				return IconRegistry.getIcon("CacheBlank");
			}
			int stored = Math.min(8, getScaledItemsStored(9));
			return facing == 3 || facing == 4 ? IconRegistry.getIcon("CacheMeter", stored) : IconRegistry.getIcon("CacheMeterInv", stored);
		}
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

	/* ITileInfo */
	@Override
	public void getTileInfo(List<String> info, ForgeDirection side, EntityPlayer player, boolean debug) {

		if (debug) {
			return;
		}
		if (storedStack != null) {
			info.add("Item: " + storedStack.getDisplayName());
			info.add("Amount: " + storedStack.stackSize + (inventory[0] == null ? 0 : inventory[0].stackSize)
					+ (inventory[1] == null ? 0 : inventory[1].stackSize));
		} else {
			info.add("Item: Empty");
		}
		info.add(locked ? "Locked" : "Unlocked");
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
