package cofh.thermalexpansion.block.cache;

import cofh.api.inventory.IInventoryRetainer;
import cofh.api.tileentity.ITileInfo;
import cofh.core.network.PacketCoFHBase;
import cofh.core.render.IconRegistry;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.TileReconfigurable;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import powercrystals.minefactoryreloaded.api.IDeepStorageUnit;

public class TileCache extends TileReconfigurable implements IDeepStorageUnit, ISidedInventory, IInventoryRetainer, ITileInfo {

	public static void initialize() {

		GameRegistry.registerTileEntity(TileCache.class, "thermalexpansion.Cache");
	}

	public static int[] CAPACITY = { Integer.MAX_VALUE, 10000, 40000, 160000, 640000 };
	public static final int[] SLOTS = { 0, 1 };
	public static final byte[] DEFAULT_SIDES = { 1, 0, 0, 0, 0, 0 };

	static {
		String category = "Cache.";
		CAPACITY[4] = MathHelper.clamp(ThermalExpansion.config.get(category + StringHelper.titleCase(BlockCache.NAMES[4]), "Capacity", CAPACITY[4]),
				CAPACITY[4] / 8, 1000000 * 1000);
		CAPACITY[3] = MathHelper.clamp(ThermalExpansion.config.get(category + StringHelper.titleCase(BlockCache.NAMES[3]), "Capacity", CAPACITY[3]),
				CAPACITY[3] / 8, CAPACITY[4]);
		CAPACITY[2] = MathHelper.clamp(ThermalExpansion.config.get(category + StringHelper.titleCase(BlockCache.NAMES[2]), "Capacity", CAPACITY[2]),
				CAPACITY[2] / 8, CAPACITY[3]);
		CAPACITY[1] = MathHelper.clamp(ThermalExpansion.config.get(category + StringHelper.titleCase(BlockCache.NAMES[1]), "Capacity", CAPACITY[1]),
				CAPACITY[1] / 8, CAPACITY[2]);
	}

	int meterTracker;
	int compareTracker;

	public byte type = 1;

	public boolean locked;
	public int maxCacheStackSize;
	public ItemStack storedStack;

	public TileCache() {

		inventory = new ItemStack[2];
	}

	public TileCache(int metadata) {

		type = (byte) metadata;
		inventory = new ItemStack[2];
		maxCacheStackSize = CAPACITY[type] - 64 * 2;
	}

	@Override
	public String getName() {

		return "tile.thermalexpansion.cache." + BlockCache.NAMES[getType()] + ".name";
	}

	@Override
	public int getType() {

		return type;
	}

	@Override
	public int getComparatorInput() {

		return compareTracker;
	}

	public int getScaledItemsStored(int scale) {

		return MathHelper.round((long) getStoredCount() * scale / CAPACITY[type]);
	}

	public boolean toggleLock() {

		locked = !locked;

		if (getStoredCount() <= 0 && !locked) {
			clearInventory();
		}
		sendUpdatePacket(Side.CLIENT);
		return locked;
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
			sendUpdatePacket(Side.CLIENT);
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
			sendUpdatePacket(Side.CLIENT);
		}
	}

	/* GUI METHODS */
	@Override
	public boolean hasGui() {

		return false;
	}

	// @Override
	// public Object getGuiClient(InventoryPlayer inventory) {
	//
	// return new GuiCache(inventory, this);
	// }
	//
	// @Override
	// public Object getGuiServer(InventoryPlayer inventory) {
	//
	// return new ContainerCache(inventory, this);
	// }

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		if (nbt.hasKey("Type")) {
			type = nbt.getByte("Type");
		}
		facing = nbt.getByte("Facing");
		locked = nbt.getBoolean("LockItem");

		if (nbt.hasKey("Item")) {
			storedStack = ItemHelper.readItemStackFromNBT(nbt.getCompoundTag("Item"));
			maxCacheStackSize = CAPACITY[type] - storedStack.getMaxStackSize() * 2;
		} else {
			maxCacheStackSize = CAPACITY[type] - 64 * 2;
		}
		super.readFromNBT(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setByte("Type", type);
		nbt.setByte("Facing", facing);
		nbt.setBoolean("LockItem", locked);

		if (storedStack != null) {
			nbt.setTag("Item", ItemHelper.writeItemStackToNBT(storedStack, new NBTTagCompound()));
		}
		return nbt;
	}

	/* NETWORK METHODS */
	@Override
	public PacketCoFHBase getPacket() {

		PacketCoFHBase payload = super.getPacket();
		payload.addBool(locked);
		payload.addItemStack(storedStack);

		if (storedStack != null) {
			payload.addInt(getStoredCount());
		}
		return payload;
	}

	/* ITilePacketHandler */
	@Override
	public void handleTilePacket(PacketCoFHBase payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

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
	@Override
	public ItemStack getStoredItemType() {

		return ItemHelper.cloneStack(storedStack, getStoredCount());
	}

	@Override
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
		markDirty();
	}

	@Override
	public void setStoredItemType(ItemStack stack, int amount) {

		if (stack == null) {
			clearInventory();
		} else {
			storedStack = ItemHelper.cloneStack(stack, Math.min(amount, getMaxStoredCount()));
			maxCacheStackSize = CAPACITY[type] - storedStack.getMaxStackSize() * 2;
			balanceStacks();
		}
		updateTrackers();
		sendUpdatePacket(Side.CLIENT);
		markDirty();
	}

	@Override
	public int getMaxStoredCount() {

		return CAPACITY[type];
	}

	/* IInventory */
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
		storedStack.stackSize += (inventory[0] == null ? 0 : inventory[0].stackSize) + (inventory[1] == null ? 0 : inventory[1].stackSize);

		if (storedStack.stackSize > 0) {
			balanceStacks();
		} else {
			clearInventory();
		}
		updateTrackers();
		markDirty();
		return stack;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {

		inventory[slot] = stack;

		boolean stackCheck = storedStack == null;

		if (slot == 0) { // insertion!
			if (inventory[0] == null) {
				return;
			}
			if (storedStack == null) {
				storedStack = inventory[0].copy();
				inventory[0] = null;
				maxCacheStackSize = CAPACITY[type] - storedStack.getMaxStackSize() * 2;
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
				maxCacheStackSize = CAPACITY[type] - storedStack.getMaxStackSize() * 2;
			}
			storedStack.stackSize += (inventory[0] == null ? 0 : inventory[0].stackSize) + (inventory[1] == null ? 0 : inventory[1].stackSize);

			if (storedStack.stackSize > 0) {
				balanceStacks();
			} else {
				clearInventory();
			}
		}
		updateTrackers();
		if (stackCheck != (storedStack == null)) {
			sendUpdatePacket(Side.CLIENT);
		}
		if (inWorld) {
			markChunkDirty();
		}
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		return slot == 0 && (storedStack == null || ItemHelper.itemsIdentical(stack, storedStack));
	}

	/* IReconfigurableSides */
	@Override
	public int getNumConfig(int side) {

		return 1;
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
	public TextureAtlasSprite getTexture(int side, int pass) {

		if (pass == 1) {
			if (side != facing) {
				return IconRegistry.getIcon("CacheBlank");
			}
			return IconRegistry.getIcon("CacheMeter", Math.min(8, getScaledItemsStored(9)));
		}
		if (side == 0) {
			return IconRegistry.getIcon("CacheBottom", type);
		} else if (side == 1) {
			return IconRegistry.getIcon("CacheTop", type);
		}
		return side != facing ? IconRegistry.getIcon("CacheSide", type) : IconRegistry.getIcon("CacheFace", type);
	}

	/* ITileInfo */
	@Override
	public void getTileInfo(List<ITextComponent> info, EnumFacing side, EntityPlayer player, boolean debug) {

		if (debug) {
			return;
		}
		if (storedStack != null) {
			info.add(new TextComponentString(StringHelper.localize("info.cofh.item") + ": " + StringHelper.getItemName(storedStack)));
			info.add(new TextComponentString(StringHelper.localize("info.cofh.amount") + ": " + getStoredCount() + " / " + CAPACITY[type]));
		} else {
			info.add(new TextComponentString(StringHelper.localize("info.cofh.item") + ": " + StringHelper.localize("info.cofh.empty")));
		}
		info.add(new TextComponentString(locked ? StringHelper.localize("info.cofh.locked") : StringHelper.localize("info.cofh.unlocked")));
	}

	/* Prototype Handler Stuff */
	public int getStoredCount() {

		return storedStack == null ? 0 : storedStack.stackSize + (inventory[0] == null ? 0 : inventory[0].stackSize)
				+ (inventory[1] == null ? 0 : inventory[1].stackSize);
	}

	public ItemStack insertItem(EnumFacing from, ItemStack stack, boolean simulate) {

		if (stack == null) {
			return null;
		}
		if (storedStack == null) {
			if (!simulate) {
				setStoredItemType(stack, stack.stackSize);
			}
			return null;
		}
		if (getStoredCount() == CAPACITY[type]) {
			return stack;
		}
		if (ItemHelper.itemsIdentical(stack, storedStack)) {
			if (getStoredCount() + stack.stackSize > CAPACITY[type]) {
				ItemStack retStack = ItemHelper.cloneStack(stack, CAPACITY[type] - getStoredCount());
				if (!simulate) {
					setStoredItemCount(CAPACITY[type]);
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

		if (!simulate) {
			setStoredItemCount(getStoredCount() - ret.stackSize);
		}
		return ret;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldRenderInPass(int pass) {

		return pass == 0 && storedStack != null;
	}

}
