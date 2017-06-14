package cofh.thermalexpansion.block.storage;

import cofh.api.item.IUpgradeItem;
import cofh.api.item.IUpgradeItem.UpgradeType;
import cofh.api.tileentity.IReconfigurableFacing;
import cofh.api.tileentity.ITileInfo;
import cofh.core.network.PacketCoFHBase;
import cofh.core.render.ISidedTexture;
import cofh.core.util.helpers.AugmentHelper;
import cofh.core.util.tileentity.IInventoryRetainer;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.helpers.StringHelper;
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

import java.util.Arrays;
import java.util.List;

public class TileCache extends TileInventory implements ISidedInventory, IReconfigurableFacing, ISidedTexture, ITileInfo, IInventoryRetainer {

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

	public byte enchantHolding;
	public boolean locked;

	int maxCacheStackSize;
	public ItemStack storedStack = ItemStack.EMPTY;

	public TileCache() {

		inventory = new ItemStack[2];
		Arrays.fill(inventory, ItemStack.EMPTY);

		maxCacheStackSize = getCapacity(0, 0) - 64 * 2;
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
			if (storedStack != ItemStack.EMPTY) {
				maxCacheStackSize = getCapacity(level, enchantHolding) - storedStack.getMaxStackSize() * 2;
			} else {
				maxCacheStackSize = getCapacity(level, enchantHolding) - 64 * 2;
			}
			return true;
		}
		return false;
	}

	@Override
	protected int getNumAugmentSlots(int level) {

		return 0;
	}

	/* COMMON METHODS */
	public static int getCapacity(int level, int enchant) {

		return CAPACITY[MathHelper.clamp(level, 0, 4)] + (CAPACITY[MathHelper.clamp(level, 0, 4)] * enchant) / 2;
	}

	public int getScaledItemsStored(int scale) {

		return MathHelper.round((long) getStoredCount() * scale / getCapacity(level, enchantHolding));
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

		return storedStack == ItemStack.EMPTY ? 0 : storedStack.getCount() + inventory[0].getCount() + inventory[1].getCount();
	}

	public ItemStack insertItem(EnumFacing from, ItemStack stack, boolean simulate) {

		if (stack.isEmpty()) {
			return ItemStack.EMPTY;
		}
		if (isCreative) {
			if (!simulate && !locked) {
				setStoredItemType(stack, getCapacity(level, enchantHolding));
			}
			return stack;
		}
		if (storedStack == ItemStack.EMPTY) {
			if (!simulate) {
				setStoredItemType(stack, stack.getCount());
			}
			return ItemStack.EMPTY;
		}
		if (getStoredCount() == getCapacity(level, enchantHolding)) {
			return stack;
		}
		if (ItemHelper.itemsIdentical(stack, storedStack)) {
			if (getStoredCount() + stack.getCount() > getCapacity(level, enchantHolding)) {
				ItemStack retStack = ItemHelper.cloneStack(stack, getCapacity(level, enchantHolding) - getStoredCount());
				if (!simulate) {
					setStoredItemCount(getCapacity(level, enchantHolding));
				}
				return retStack;
			}
			if (!simulate) {
				setStoredItemCount(getStoredCount() + stack.getCount());
			}
			return ItemStack.EMPTY;
		}
		return stack;
	}

	public ItemStack extractItem(EnumFacing from, int maxExtract, boolean simulate) {

		if (storedStack == ItemStack.EMPTY) {
			return ItemStack.EMPTY;
		}
		ItemStack ret = ItemHelper.cloneStack(storedStack, Math.min(getStoredCount(), Math.min(maxExtract, storedStack.getMaxStackSize())));

		if (!simulate && !isCreative) {
			setStoredItemCount(getStoredCount() - ret.getCount());
		}
		return ret;
	}

	protected void balanceStacks() {

		inventory[0] = ItemStack.EMPTY;
		inventory[1] = ItemHelper.cloneStack(storedStack, Math.min(storedStack.getMaxStackSize(), storedStack.getCount()));
		storedStack.shrink(inventory[1].getCount());

		if (storedStack.getCount() > maxCacheStackSize) {
			inventory[0] = ItemHelper.cloneStack(storedStack, storedStack.getCount() - maxCacheStackSize);
			storedStack.setCount(maxCacheStackSize);
		}
	}

	protected void clearInventory() {

		if (!locked) {
			storedStack = ItemStack.EMPTY;
			sendTilePacket(Side.CLIENT);
		} else {
			if (storedStack != ItemStack.EMPTY) {
				storedStack.setCount(0);
			}
		}
		inventory[0] = ItemStack.EMPTY;
		inventory[1] = ItemStack.EMPTY;
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

		enchantHolding = nbt.getByte("EncHolding");

		super.readFromNBT(nbt);

		facing = nbt.getByte("Facing");
		locked = nbt.getBoolean("Lock");

		if (nbt.hasKey("Item")) {
			storedStack = ItemHelper.readItemStackFromNBT(nbt.getCompoundTag("Item"));
			maxCacheStackSize = getCapacity(level, enchantHolding) - storedStack.getMaxStackSize() * 2;
		} else {
			maxCacheStackSize = getCapacity(level, enchantHolding) - 64 * 2;
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setByte("Facing", facing);
		nbt.setByte("EncHolding", enchantHolding);
		nbt.setBoolean("Lock", locked);

		if (storedStack != ItemStack.EMPTY) {
			nbt.setTag("Item", ItemHelper.writeItemStackToNBT(storedStack, new NBTTagCompound()));
		}
		return nbt;
	}

	/* NETWORK METHODS */
	@Override
	public PacketCoFHBase getTilePacket() {

		PacketCoFHBase payload = super.getTilePacket();

		payload.addByte(facing);
		payload.addByte(enchantHolding);
		payload.addBool(locked);
		payload.addItemStack(storedStack);

		if (storedStack != ItemStack.EMPTY) {
			payload.addInt(getStoredCount());
		}
		return payload;
	}

	@Override
	public void handleTilePacket(PacketCoFHBase payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		facing = payload.getByte();
		enchantHolding = payload.getByte();
		locked = payload.getBool();
		storedStack = payload.getItemStack();

		if (storedStack != ItemStack.EMPTY) {
			storedStack.setCount(payload.getInt());
			inventory[1] = ItemStack.EMPTY;
			balanceStacks();
		} else {
			storedStack = ItemStack.EMPTY;
			inventory[0] = ItemStack.EMPTY;
			inventory[1] = ItemStack.EMPTY;
		}
	}

	/* IDeepStorageUnit */
	//@Override
	public ItemStack getStoredItemType() {

		return ItemHelper.cloneStack(storedStack, getStoredCount());
	}

	//@Override
	public void setStoredItemCount(int amount) {

		if (storedStack == ItemStack.EMPTY) {
			return;
		}
		storedStack.setCount(Math.min(amount, getMaxStoredCount()));

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

		if (stack.isEmpty()) {
			clearInventory();
		} else {
			storedStack = ItemHelper.cloneStack(stack, Math.min(amount, getMaxStoredCount()));
			maxCacheStackSize = getCapacity(level, enchantHolding) - storedStack.getMaxStackSize() * 2;
			balanceStacks();
		}
		updateTrackers();
		sendTilePacket(Side.CLIENT);
		markChunkDirty();
	}

	//@Override
	public int getMaxStoredCount() {

		return getCapacity(level, enchantHolding);
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
		if (inventory[slot].isEmpty()) {
			return ItemStack.EMPTY;
		}
		if (inventory[slot].getCount() <= amount) {
			amount = inventory[slot].getCount();
		}
		ItemStack stack = inventory[slot].splitStack(amount);

		if (inventory[slot].getCount() <= 0) {
			inventory[slot] = ItemStack.EMPTY;
		}
		storedStack.grow(inventory[0].getCount() + inventory[1].getCount());

		if (storedStack.getCount() > 0) {
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

		boolean stackCheck = storedStack == ItemStack.EMPTY;

		if (slot == 0) { // insertion!
			if (inventory[0].isEmpty()) {
				return;
			}
			if (storedStack == ItemStack.EMPTY) {
				storedStack = inventory[0].copy();
				inventory[0] = ItemStack.EMPTY;
				maxCacheStackSize = getCapacity(level, enchantHolding) - storedStack.getMaxStackSize() * 2;
			} else {
				storedStack.grow(inventory[0].getCount() + inventory[1].getCount());
			}
			balanceStacks();
		} else { // extraction!
			if (storedStack == ItemStack.EMPTY) {
				if (inventory[1].isEmpty()) {
					return;
				}
				storedStack = inventory[1].copy();
				storedStack.setCount(0);
				maxCacheStackSize = getCapacity(level, enchantHolding) - storedStack.getMaxStackSize() * 2;
			}
			storedStack.grow(inventory[0].getCount() + inventory[1].getCount());

			if (storedStack.getCount() > 0) {
				balanceStacks();
			} else {
				clearInventory();
			}
		}
		updateTrackers();
		markChunkDirty();
		if (stackCheck != (storedStack == ItemStack.EMPTY)) {
			sendTilePacket(Side.CLIENT);
		}
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		return slot == 0 && (storedStack == ItemStack.EMPTY || ItemHelper.itemsIdentical(stack, storedStack));
	}

	/* ISidedInventory */
	@Override
	public int[] getSlotsForFace(EnumFacing side) {

		return SLOTS;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, EnumFacing side) {

		return slot == 0 && (storedStack == ItemStack.EMPTY || ItemHelper.itemsIdentical(stack, storedStack));
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
			return side != facing ? TETextures.CONFIG_NONE : isCreative ? TETextures.CACHE_METER_C : TETextures.CACHE_METER[MathHelper.clamp(getScaledItemsStored(9), 0, 8)];
		}
		return isCreative ? TETextures.CACHE_SIDE_C : TETextures.CACHE_SIDE[level];
	}

	/* ITileInfo */
	@Override
	public void getTileInfo(List<ITextComponent> info, EnumFacing side, EntityPlayer player, boolean debug) {

		if (debug) {
			return;
		}
		if (storedStack != ItemStack.EMPTY) {
			info.add(new TextComponentString(StringHelper.localize("info.cofh.item") + ": " + StringHelper.getItemName(storedStack)));
			info.add(new TextComponentString(StringHelper.localize("info.cofh.amount") + ": " + getStoredCount() + " / " + getCapacity(level, enchantHolding)));
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

	/* IInventoryRetainer */
	@Override
	public boolean retainInventory() {

		return true;
	}

}
