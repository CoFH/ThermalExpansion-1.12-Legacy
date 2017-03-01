package cofh.thermalexpansion.block.storage;

import cofh.api.item.IUpgradeItem;
import cofh.api.item.IUpgradeItem.UpgradeType;
import cofh.api.tileentity.IReconfigurableFacing;
import cofh.api.tileentity.ISidedTexture;
import cofh.api.tileentity.ITileInfo;
import cofh.core.network.PacketCoFHBase;
import cofh.lib.util.helpers.*;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.TileInventory;
import cofh.thermalexpansion.init.TETextures;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;

public class TileCache extends TileInventory implements ISidedInventory, IReconfigurableFacing, ISidedTexture, ITileInfo {

	public static final int[] CAPACITY = { 1, 4, 9, 16, 25 };
	public static final int[] SLOTS = { 0, 1 };

	static {
		for (int i = 0; i < CAPACITY.length; i++) {
			CAPACITY[i] *= 20000;
		}
	}

	private static boolean enableSecurity = true;

	public static void initialize() {

		GameRegistry.registerTileEntity(TileCache.class, "thermalexpansion:storage_cache");

		config();
	}

	public static void config() {

		String comment = "Enable this to allow for Caches to be securable.";
		enableSecurity = ThermalExpansion.CONFIG.get("Security", "Cache.Securable", true, comment);

		String category = "Storage.Cache";
		BlockCache.enable = ThermalExpansion.CONFIG.get(category, "Enable", true);
	}

	private int compareTracker;
	private int meterTracker;

	byte facing = 3;
	public boolean locked;
	int maxCacheStackSize;
	public ItemStack storedStack;

	public TileCache() {

		inventory = new ItemStack[2];
		maxCacheStackSize = getCapacity(0) - 64 * 2;
	}

	@Override
	public String getTileName() {

		return "tile.thermalexpansion.storage.cache.name";
	}

	@Override
	public int getType() {

		return 0;
	}

	@Override
	public int getComparatorInputOverride() {

		return compareTracker;
	}

	@Override
	public boolean enableSecurity() {

		return enableSecurity;
	}

	@Override
	protected boolean setLevel(int level) {

		if (super.setLevel(level)) {
			if (storedStack != null) {
				maxCacheStackSize = getCapacity(level) - storedStack.getMaxStackSize() * 2;
			} else {
				maxCacheStackSize = getCapacity(level) - 64 * 2;
			}
			return true;
		}
		return false;
	}

	/* COMMON METHODS */
	public static int getCapacity(int level) {

		return CAPACITY[MathHelper.clamp(level, 0, 4)];
	}

	public int getScaledItemsStored(int scale) {

		return MathHelper.round((long) getStoredCount() * scale / getCapacity(level));
	}

	public boolean toggleLock() {

		locked = !locked;

		if (getStoredCount() <= 0 && !locked) {
			clearInventory();
		}
		sendTilePacket(Side.CLIENT);
		return locked;
	}

	public int getStoredCount() {

		return storedStack == null ? 0 : storedStack.stackSize + (inventory[0] == null ? 0 : inventory[0].stackSize) + (inventory[1] == null ? 0 : inventory[1].stackSize);
	}

	public ItemStack insertItem(EnumFacing from, ItemStack stack, boolean simulate) {

		if (stack == null) {
			return null;
		}
		if (isCreative) {
			if (!simulate && !locked) {
				setStoredItemType(stack, getCapacity(level));
			}
			return stack;
		}
		if (storedStack == null) {
			if (!simulate) {
				setStoredItemType(stack, stack.stackSize);
			}
			return null;
		}
		if (getStoredCount() == getCapacity(level)) {
			return stack;
		}
		if (ItemHelper.itemsIdentical(stack, storedStack)) {
			if (getStoredCount() + stack.stackSize > getCapacity(level)) {
				ItemStack retStack = ItemHelper.cloneStack(stack, getCapacity(level) - getStoredCount());
				if (!simulate) {
					setStoredItemCount(getCapacity(level));
				}
				return retStack;
			}
			if (!simulate) {
				setStoredItemCount(getStoredCount() + stack.stackSize);
			}
			return null;
		}
		return stack;
	}

	public ItemStack extractItem(EnumFacing from, int maxExtract, boolean simulate) {

		if (storedStack == null) {
			return null;
		}
		ItemStack ret = ItemHelper.cloneStack(storedStack, Math.min(getStoredCount(), Math.min(maxExtract, storedStack.getMaxStackSize())));

		if (!simulate && !isCreative) {
			setStoredItemCount(getStoredCount() - ret.stackSize);
		}
		return ret;
	}

	protected void balanceStacks() {

		inventory[0] = null;
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
			sendTilePacket(Side.CLIENT);
		} else {
			if (storedStack != null) {
				storedStack.stackSize = 0;
			}
		}
		inventory[0] = null;
		inventory[1] = null;
	}

	protected void updateTrackers() {

		int curScale = getScaledItemsStored(14) + (getStoredCount() > 0 ? 1 : 0);
		if (compareTracker != curScale) {
			compareTracker = curScale;
			callNeighborTileChange();
		}
		curScale = Math.min(8, getScaledItemsStored(9));
		if (meterTracker != curScale) {
			meterTracker = curScale;
			sendTilePacket(Side.CLIENT);
		}
	}

	/* GUI METHODS */
	@Override
	public boolean hasGui() {

		return false;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		facing = nbt.getByte("Facing");
		locked = nbt.getBoolean("Lock");

		if (nbt.hasKey("Item")) {
			storedStack = ItemHelper.readItemStackFromNBT(nbt.getCompoundTag("Item"));
			maxCacheStackSize = getCapacity(level) - storedStack.getMaxStackSize() * 2;
		} else {
			maxCacheStackSize = getCapacity(level) - 64 * 2;
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setByte("Facing", facing);
		nbt.setBoolean("Lock", locked);

		if (storedStack != null) {
			nbt.setTag("Item", ItemHelper.writeItemStackToNBT(storedStack, new NBTTagCompound()));
		}
		return nbt;
	}

	/* NETWORK METHODS */
	@Override
	public PacketCoFHBase getTilePacket() {

		PacketCoFHBase payload = super.getTilePacket();

		payload.addByte(facing);
		payload.addBool(locked);
		payload.addItemStack(storedStack);

		if (storedStack != null) {
			payload.addInt(getStoredCount());
		}
		return payload;
	}

	@Override
	public void handleTilePacket(PacketCoFHBase payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		facing = payload.getByte();
		locked = payload.getBool();
		storedStack = payload.getItemStack();

		if (storedStack != null) {
			storedStack.stackSize = payload.getInt();
			inventory[1] = null;
			balanceStacks();
		} else {
			storedStack = null;
			inventory[0] = null;
			inventory[1] = null;
		}
	}

	/* IDeepStorageUnit */
	//@Override
	public ItemStack getStoredItemType() {

		return ItemHelper.cloneStack(storedStack, getStoredCount());
	}

	//@Override
	public void setStoredItemCount(int amount) {

		if (storedStack == null) {
			return;
		}
		storedStack.stackSize = Math.min(amount, getMaxStoredCount());

		if (amount > 0) {
			balanceStacks();
		} else {
			clearInventory();
		}
		updateTrackers();
		markChunkDirty();
	}

	//@Override
	public void setStoredItemType(ItemStack stack, int amount) {

		if (stack == null) {
			clearInventory();
		} else {
			storedStack = ItemHelper.cloneStack(stack, Math.min(amount, getMaxStoredCount()));
			maxCacheStackSize = getCapacity(level) - storedStack.getMaxStackSize() * 2;
			balanceStacks();
		}
		updateTrackers();
		sendTilePacket(Side.CLIENT);
		markChunkDirty();
	}

	//@Override
	public int getMaxStoredCount() {

		return getCapacity(level);
	}

	/* IReconfigurableFacing */
	@Override
	public final int getFacing() {

		return facing;
	}

	@Override
	public boolean allowYAxisFacing() {

		return false;
	}

	@Override
	public boolean rotateBlock() {

		facing = BlockHelper.SIDE_LEFT[facing];
		markChunkDirty();
		sendTilePacket(Side.CLIENT);
		return true;
	}

	@Override
	public boolean setFacing(int side) {

		if (side < 2 || side > 5) {
			return false;
		}
		facing = (byte) side;
		markChunkDirty();
		sendTilePacket(Side.CLIENT);
		return true;
	}

	/* IInventory */
	@Override
	public ItemStack decrStackSize(int slot, int amount) {

		if (isCreative) {
			return ItemHelper.cloneStack(inventory[slot], amount);
		}
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
		storedStack.stackSize += (inventory[0] == null ? 0 : inventory[0].stackSize) + (inventory[1] == null ? 0 : inventory[1].stackSize);

		if (storedStack.stackSize > 0) {
			balanceStacks();
		} else {
			clearInventory();
		}
		updateTrackers();
		markChunkDirty();
		return stack;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {

		if (isCreative) {
			return;
		}
		inventory[slot] = stack;

		boolean stackCheck = storedStack == null;

		if (slot == 0) { // insertion!
			if (inventory[0] == null) {
				return;
			}
			if (storedStack == null) {
				storedStack = inventory[0].copy();
				inventory[0] = null;
				maxCacheStackSize = getCapacity(level) - storedStack.getMaxStackSize() * 2;
			} else {
				storedStack.stackSize += inventory[0].stackSize + (inventory[1] == null ? 0 : inventory[1].stackSize);
			}
			balanceStacks();
		} else { // extraction!
			if (storedStack == null) {
				if (inventory[1] == null) {
					return;
				}
				storedStack = inventory[1].copy();
				storedStack.stackSize = 0;
				maxCacheStackSize = getCapacity(level) - storedStack.getMaxStackSize() * 2;
			}
			storedStack.stackSize += (inventory[0] == null ? 0 : inventory[0].stackSize) + (inventory[1] == null ? 0 : inventory[1].stackSize);

			if (storedStack.stackSize > 0) {
				balanceStacks();
			} else {
				clearInventory();
			}
		}
		updateTrackers();
		markChunkDirty();
		if (stackCheck != (storedStack == null)) {
			sendTilePacket(Side.CLIENT);
		}
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		return slot == 0 && (storedStack == null || ItemHelper.itemsIdentical(stack, storedStack));
	}

	/* ISidedInventory */
	@Override
	public int[] getSlotsForFace(EnumFacing side) {

		return SLOTS;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, EnumFacing side) {

		return slot == 0 && (storedStack == null || ItemHelper.itemsIdentical(stack, storedStack));
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, EnumFacing side) {

		return slot == 1;
	}

	/* ISidedTexture */
	@Override
	public int getNumPasses() {

		return 2;
	}

	@Override
	public TextureAtlasSprite getTexture(int side, int pass) {

		if (pass == 0) {
			if (side == 0) {
				return isCreative ? TETextures.CACHE_BOTTOM_C : TETextures.CACHE_BOTTOM[level];
			} else if (side == 1) {
				return isCreative ? TETextures.CACHE_TOP_C : TETextures.CACHE_TOP[level];
			}
			return side != facing ? isCreative ? TETextures.CACHE_SIDE_C : TETextures.CACHE_SIDE[level] : isCreative ? TETextures.CACHE_FACE_C : TETextures.CACHE_FACE[level];
		} else if (side < 6) {
			return side != facing ? TETextures.CONFIG_NONE : isCreative ? TETextures.CACHE_METER_C : TETextures.CACHE_METER[meterTracker];
		}
		return isCreative ? TETextures.CACHE_SIDE_C : TETextures.CACHE_SIDE[level];
	}

	/* ITileInfo */
	@Override
	public void getTileInfo(List<ITextComponent> info, EnumFacing side, EntityPlayer player, boolean debug) {

		if (debug) {
			return;
		}
		if (storedStack != null) {
			info.add(new TextComponentString(StringHelper.localize("info.cofh.item") + ": " + StringHelper.getItemName(storedStack)));
			info.add(new TextComponentString(StringHelper.localize("info.cofh.amount") + ": " + getStoredCount() + " / " + getCapacity(level)));
		} else {
			info.add(new TextComponentString(StringHelper.localize("info.cofh.item") + ": " + StringHelper.localize("info.cofh.empty")));
		}
		info.add(new TextComponentString(locked ? StringHelper.localize("info.cofh.locked") : StringHelper.localize("info.cofh.unlocked")));
	}

	/* IUpgradeable */
	@Override
	public boolean canUpgrade(ItemStack upgrade) {

		if (!AugmentHelper.isUpgradeItem(upgrade)) {
			return false;
		}
		UpgradeType uType = ((IUpgradeItem) upgrade.getItem()).getUpgradeType(upgrade);
		int uLevel = ((IUpgradeItem) upgrade.getItem()).getUpgradeLevel(upgrade);

		switch (uType) {
			case INCREMENTAL:
				if (uLevel == level + 1) {
					return true;
				}
				break;
			case FULL:
				if (uLevel > level) {
					return true;
				}
				break;
			case CREATIVE:
				return !isCreative;
		}
		return false;
	}

}
