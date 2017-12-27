package cofh.thermalexpansion.block.storage;

import cofh.api.item.IUpgradeItem;
import cofh.api.item.IUpgradeItem.UpgradeType;
import cofh.api.tileentity.IInventoryRetainer;
import cofh.api.tileentity.IReconfigurableFacing;
import cofh.api.tileentity.ITileInfo;
import cofh.core.gui.container.ICustomInventory;
import cofh.core.network.PacketBase;
import cofh.core.render.ISidedTexture;
import cofh.core.util.helpers.*;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.TileInventory;
import cofh.thermalexpansion.gui.client.storage.GuiCache;
import cofh.thermalexpansion.gui.container.storage.ContainerCache;
import cofh.thermalexpansion.init.TETextures;
import cofh.thermalexpansion.plugins.top.PluginTOP;
import com.jaquadro.minecraft.storagedrawers.api.capabilities.IItemRepository;
import mcjty.theoneprobe.api.ElementAlignment;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.api.TextStyleClass;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

@Optional.Interface (iface = "com.jaquadro.minecraft.storagedrawers.api.capabilities.IItemRepository", modid = "storagedrawers")
public class TileCache extends TileInventory implements ISidedInventory, IReconfigurableFacing, ISidedTexture, ITileInfo, ICustomInventory, IInventoryRetainer, IItemRepository {

	public static final int CAPACITY_BASE = 20000;
	public static final int[] CAPACITY = { 1, 4, 9, 16, 25 };
	public static final int[] SLOTS = { 0, 1 };

	public static void initialize() {

		GameRegistry.registerTileEntity(TileCache.class, "thermalexpansion:storage_cache");

		config();
	}

	public static void config() {

		String category = "Storage.Cache";
		String comment = "If TRUE, Caches are enabled.";
		BlockCache.enable = ThermalExpansion.CONFIG.get(category, "Enable", BlockCache.enable, comment);

		comment = "If TRUE, Caches may be turned into Creative versions using a Creative Conversion Kit.";
		BlockCache.enableCreative = ThermalExpansion.CONFIG.get(category, "Creative", BlockCache.enableCreative, comment);

		comment = "If TRUE, Caches are securable.";
		BlockCache.enableSecurity = ThermalExpansion.CONFIG.get(category, "Securable", BlockCache.enableSecurity, comment);

		comment = "If TRUE, 'Classic' Crafting is enabled - Non-Creative Upgrade Kits WILL NOT WORK in a Crafting Grid.";
		BlockCache.enableClassicRecipes = ThermalExpansion.CONFIG.get(category, "ClassicCrafting", BlockCache.enableClassicRecipes, comment);

		comment = "If TRUE, Caches can be upgraded in a Crafting Grid using Kits. If Classic Crafting is enabled, only the Creative Conversion Kit may be used in this fashion.";
		BlockCache.enableUpgradeKitCrafting = ThermalExpansion.CONFIG.get(category, "UpgradeKitCrafting", BlockCache.enableUpgradeKitCrafting, comment);

		int capacity = CAPACITY_BASE;
		comment = "Adjust this value to change the amount of Items stored by a Basic Cache. This base value will scale with block level.";
		capacity = ThermalExpansion.CONFIG.getConfiguration().getInt("BaseCapacity", category, capacity, capacity / 5, capacity * 5, comment);

		for (int i = 0; i < CAPACITY.length; i++) {
			CAPACITY[i] *= capacity;
		}
	}

	private int compareTracker;
	private int meterTracker;

	byte facing = 3;

	public byte enchantHolding;

	boolean lock = false;
	int cacheStackSize;
	int maxCacheStackSize;
	int maxCapacity;

	public ItemStack storedStack = ItemStack.EMPTY;

	public TileCache() {

		inventory = new ItemStack[2];
		Arrays.fill(inventory, ItemStack.EMPTY);

		maxCapacity = getCapacity(0, 0);
		maxCacheStackSize = maxCapacity - 64 * 2;
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
	public boolean onWrench(EntityPlayer player, EnumFacing side) {

		return rotateBlock();
	}

	@Override
	public int getComparatorInputOverride() {

		return compareTracker;
	}

	@Override
	public boolean enableSecurity() {

		return BlockCache.enableSecurity;
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
					return !BlockCache.enableClassicRecipes;
				}
				break;
			case FULL:
				if (uLevel > level) {
					return !BlockCache.enableClassicRecipes;
				}
				break;
			case CREATIVE:
				return !isCreative && BlockCache.enableCreative;
		}
		return false;
	}

	@Override
	protected boolean setLevel(int level) {

		if (super.setLevel(level)) {
			if (!storedStack.isEmpty()) {
				maxCapacity = getCapacity(level, enchantHolding);
				maxCacheStackSize = maxCapacity - storedStack.getMaxStackSize() * 2;
				balanceStacks();
			} else {
				maxCapacity = getCapacity(level, enchantHolding);
				maxCacheStackSize = maxCapacity - 64 * 2;
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

	protected void balanceStacks() {

		inventory[0] = ItemStack.EMPTY;
		inventory[1] = ItemHelper.cloneStack(storedStack, Math.min(storedStack.getMaxStackSize(), cacheStackSize));
		cacheStackSize -= inventory[1].getCount();

		if (cacheStackSize > maxCacheStackSize) {
			inventory[0] = ItemHelper.cloneStack(storedStack, cacheStackSize - maxCacheStackSize);
			cacheStackSize = maxCacheStackSize;
		}
	}

	protected void clearInventory() {

		if (!lock) {
			storedStack = ItemStack.EMPTY;
			cacheStackSize = 0;
			sendTilePacket(Side.CLIENT);
		} else {
			if (!storedStack.isEmpty()) {
				cacheStackSize = 0;
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

	public void setLocked(boolean lock) {

		if (storedStack.isEmpty()) {
			lock = false;
		}
		this.lock = lock;
		if (getStoredCount() <= 0 && !lock) {
			clearInventory();
		}
		sendTilePacket(Side.CLIENT);
	}

	public boolean isLocked() {

		return lock;
	}

	public int getScaledItemsStored(int scale) {

		return MathHelper.round((long) getStoredCount() * scale / getCapacity(level, enchantHolding));
	}

	public int getStoredCount() {

		return storedStack.isEmpty() ? 0 : cacheStackSize + inventory[0].getCount() + inventory[1].getCount();
	}

	public ItemStack insertItem(ItemStack stack, boolean simulate) {

		if (stack.isEmpty()) {
			return ItemStack.EMPTY;
		}
		if (isCreative) {
			if (!simulate && !lock) {
				setStoredItemType(stack, getCapacity(level, enchantHolding));
			}
			return stack;
		}
		if (storedStack.isEmpty()) {
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

	public ItemStack extractItem(int maxExtract, boolean simulate) {

		if (storedStack.isEmpty()) {
			return ItemStack.EMPTY;
		}
		ItemStack ret = ItemHelper.cloneStack(storedStack, Math.min(getStoredCount(), Math.min(maxExtract, storedStack.getMaxStackSize())));

		if (!simulate && !isCreative) {
			setStoredItemCount(getStoredCount() - ret.getCount());
		}
		return ret;
	}

	/* GUI METHODS */
	@Override
	public Object getConfigGuiClient(InventoryPlayer inventory) {

		return new GuiCache(inventory, this);
	}

	@Override
	public Object getConfigGuiServer(InventoryPlayer inventory) {

		return new ContainerCache(inventory, this);
	}

	@Override
	public boolean hasConfigGui() {

		return false;
	}

	// This is ONLY used in GUIs.
	public void toggleLock() {

		lock = !lock;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		enchantHolding = nbt.getByte("EncHolding");

		super.readFromNBT(nbt);

		facing = nbt.getByte("Facing");
		lock = nbt.getBoolean("Lock");

		if (nbt.hasKey("Item")) {
			storedStack = ItemHelper.readItemStackFromNBT(nbt.getCompoundTag("Item"));
			cacheStackSize = nbt.getInteger("CacheCount");
			maxCapacity = getCapacity(level, enchantHolding);
			maxCacheStackSize = maxCapacity - storedStack.getMaxStackSize() * 2;
		} else {
			maxCapacity = getCapacity(level, enchantHolding);
			maxCacheStackSize = maxCapacity - 64 * 2;
			lock = false;
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setByte("Facing", facing);
		nbt.setByte("EncHolding", enchantHolding);
		nbt.setBoolean("Lock", lock);

		if (!storedStack.isEmpty()) {
			nbt.setTag("Item", ItemHelper.writeItemStackToNBT(storedStack, new NBTTagCompound()));
			nbt.setInteger("CacheCount", cacheStackSize);
		}
		return nbt;
	}

	/* NETWORK METHODS */

	/* CLIENT -> SERVER */
	@Override
	public PacketBase getModePacket() {

		PacketBase payload = super.getModePacket();

		payload.addBool(lock);

		return payload;
	}

	@Override
	protected void handleModePacket(PacketBase payload) {

		super.handleModePacket(payload);

		setLocked(payload.getBool());
	}

	/* SERVER -> CLIENT */
	@Override
	public PacketBase getGuiPacket() {

		PacketBase payload = super.getGuiPacket();

		payload.addBool(lock);

		return payload;
	}

	@Override
	public PacketBase getTilePacket() {

		PacketBase payload = super.getTilePacket();

		payload.addByte(facing);
		payload.addByte(enchantHolding);
		payload.addBool(lock);
		payload.addItemStack(storedStack);

		if (!storedStack.isEmpty()) {
			payload.addInt(getStoredCount());
		}
		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketBase payload) {

		super.handleGuiPacket(payload);

		lock = payload.getBool();
	}

	@Override
	@SideOnly (Side.CLIENT)
	public void handleTilePacket(PacketBase payload) {

		super.handleTilePacket(payload);

		facing = payload.getByte();
		enchantHolding = payload.getByte();
		lock = payload.getBool();
		storedStack = payload.getItemStack();

		if (!storedStack.isEmpty()) {
			cacheStackSize = payload.getInt();
			inventory[1] = ItemStack.EMPTY;
			balanceStacks();
		} else {
			cacheStackSize = 0;
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

		if (storedStack.isEmpty()) {
			return;
		}
		cacheStackSize = Math.min(amount, getMaxStoredCount());

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
			storedStack = ItemHelper.cloneStack(stack, 1);
			cacheStackSize = Math.min(amount, getMaxStoredCount());
			maxCapacity = getCapacity(level, enchantHolding);
			maxCacheStackSize = maxCapacity - storedStack.getMaxStackSize() * 2;
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
	public boolean setFacing(int side, boolean alternate) {

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
		cacheStackSize += inventory[0].getCount() + inventory[1].getCount();

		if (cacheStackSize > 0) {
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

		boolean stackCheck = storedStack.isEmpty();

		if (slot == 0) { // insertion!
			if (inventory[0].isEmpty()) {
				return;
			}
			if (storedStack.isEmpty()) {
				storedStack = ItemHelper.cloneStack(inventory[0], 1);
				cacheStackSize = inventory[0].getCount();
				inventory[0] = ItemStack.EMPTY;
				maxCapacity = getCapacity(level, enchantHolding);
				maxCacheStackSize = maxCapacity - storedStack.getMaxStackSize() * 2;
			} else {
				cacheStackSize += inventory[0].getCount() + inventory[1].getCount();
			}
			balanceStacks();
		} else { // extraction!
			cacheStackSize += inventory[0].getCount() + inventory[1].getCount();

			if (cacheStackSize > 0) {
				balanceStacks();
			} else {
				clearInventory();
			}
		}
		updateTrackers();
		markChunkDirty();
		if (stackCheck != (storedStack.isEmpty())) {
			sendTilePacket(Side.CLIENT);
		}
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		return slot == 0 && (storedStack.isEmpty() || ItemHelper.itemsIdentical(stack, storedStack));
	}

	/* ISidedInventory */
	@Override
	public int[] getSlotsForFace(EnumFacing side) {

		return SLOTS;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, EnumFacing side) {

		return slot == 0 && (storedStack.isEmpty() || ItemHelper.itemsIdentical(stack, storedStack));
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
		if (!storedStack.isEmpty()) {
			info.add(new TextComponentString(StringHelper.localize("info.cofh.item") + ": " + StringHelper.getItemName(storedStack)));
			info.add(new TextComponentString(StringHelper.localize("info.cofh.amount") + ": " + StringHelper.formatNumber(getStoredCount()) + " / " + StringHelper.formatNumber(getCapacity(level, enchantHolding))));
			info.add(new TextComponentString(lock ? StringHelper.localize("info.cofh.locked") : StringHelper.localize("info.cofh.unlocked")));
		} else {
			info.add(new TextComponentString(StringHelper.localize("info.cofh.item") + ": " + StringHelper.localize("info.cofh.empty")));
		}
	}

	/* ICustomInventory */
	@Override
	public ItemStack[] getInventorySlots(int inventoryIndex) {

		return new ItemStack[] { storedStack };
	}

	@Override
	public int getSlotStackLimit(int slotIndex) {

		return 1;
	}

	@Override
	public void onSlotUpdate(int slotIndex) {

		markChunkDirty();
	}

	/* IInventoryRetainer */
	@Override
	public boolean retainInventory() {

		return true;
	}

	/* PLUGIN METHODS */
	@Override
	public void provideInfo(ProbeMode mode, IProbeInfo info, EnumFacing facing, EntityPlayer player) {

		if (mode != ProbeMode.NORMAL) {
			IProbeInfo infoSub = info.horizontal(info.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER).borderColor(PluginTOP.chestContentsBorderColor).spacing(10));

			ItemStack stored = getStoredItemType();
			infoSub.item(stored, info.defaultItemStyle().width(16).height(16)).text(TextStyleClass.INFO + stored.getDisplayName());
		}
	}

	/* IItemRepository */
	@Nonnull
	@Override
	public NonNullList<ItemRecord> getAllItems() {

		return NonNullList.from(new ItemRecord(storedStack.copy(), getStoredCount()));
	}

	@Nonnull
	@Override
	public ItemStack insertItem(@Nonnull ItemStack stack, boolean simulate, Predicate<ItemStack> predicate) {

		return insertItem(stack, simulate);
	}

	@Nonnull
	@Override
	public ItemStack extractItem(@Nonnull ItemStack stack, int amount, boolean simulate, Predicate<ItemStack> predicate) {

		return ItemHelper.itemsIdentical(stack, storedStack) ? extractItem(amount, simulate) : ItemStack.EMPTY;
	}

}
