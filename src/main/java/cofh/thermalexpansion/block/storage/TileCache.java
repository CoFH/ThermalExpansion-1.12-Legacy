package cofh.thermalexpansion.block.storage;

import cofh.api.item.IUpgradeItem;
import cofh.api.item.IUpgradeItem.UpgradeType;
import cofh.api.tileentity.IInventoryRetainer;
import cofh.api.tileentity.IReconfigurableFacing;
import cofh.api.tileentity.ITileInfo;
import cofh.core.block.TileAugmentableSecure;
import cofh.core.init.CoreProps;
import cofh.core.network.PacketBase;
import cofh.core.render.ISidedTexture;
import cofh.core.util.helpers.*;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.gui.client.storage.GuiCache;
import cofh.thermalexpansion.gui.container.storage.ContainerCache;
import cofh.thermalexpansion.init.TETextures;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class TileCache extends TileAugmentableSecure implements IReconfigurableFacing, ISidedTexture, ITileInfo, IInventoryRetainer {

	public static final int CAPACITY_BASE = 20000;
	public static final int[] CAPACITY = { 1, 4, 9, 16, 25 };

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

		// TODO: Cache Size Limiting
		//		comment = "If TRUE, Caches are securable.";
		//		BlockCache.enableSecurity = ThermalExpansion.CONFIG.get(category, "Securable", BlockCache.enableSecurity, comment);

		comment = "If TRUE, 'Classic' Crafting is enabled - Non-Creative Upgrade Kits WILL NOT WORK in a Crafting Grid.";
		BlockCache.enableClassicRecipes = ThermalExpansion.CONFIG.get(category, "ClassicCrafting", BlockCache.enableClassicRecipes, comment);

		comment = "If TRUE, Caches can be upgraded in a Crafting Grid using Kits. If Classic Crafting is enabled, only the Creative Conversion Kit may be used in this fashion.";
		BlockCache.enableUpgradeKitCrafting = ThermalExpansion.CONFIG.get(category, "UpgradeKitCrafting", BlockCache.enableUpgradeKitCrafting, comment);

		int capacity = CAPACITY_BASE;
		comment = "Adjust this value to change the amount of Items stored by a Basic Cache. This base value will scale with block level.";
		capacity = ThermalExpansion.CONFIG.getConfiguration().getInt("BaseCapacity", category, capacity, 500, 500000, comment);

		for (int i = 0; i < CAPACITY.length; i++) {
			CAPACITY[i] *= capacity;
		}
	}

	private int compareTracker;
	private int meterTracker;

	byte facing = 3;

	public byte enchantHolding;
	boolean lock = false;

	private CacheItemHandler handler;

	public TileCache() {

		handler = new CacheItemHandler(this, getMaxCapacity(0, 0));
	}

	@Override
	protected Object getMod() {

		return ThermalExpansion.instance;
	}

	@Override
	protected String getModVersion() {

		return ThermalExpansion.VERSION;
	}

	@Override
	protected String getTileName() {

		return "tile.thermalexpansion.storage.cache.name";
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

		int curLevel = this.level;

		if (super.setLevel(level)) {
			handler.setCapacity(getMaxCapacity(level, enchantHolding));

			// TODO: Cache Size Limiting
			// handler.setCapacity(MathHelper.round((long) handler.getCapacity() * getMaxCapacity(level, enchantHolding) / getMaxCapacity(curLevel, enchantHolding)));
			return true;
		}
		return false;
	}

	@Override
	protected int getNumAugmentSlots(int level) {

		return 0;
	}

	/* COMMON METHODS */
	public static int getMaxCapacity(int level, int enchant) {

		return CAPACITY[MathHelper.clamp(level, 0, 4)] + (CAPACITY[MathHelper.clamp(level, 0, 4)] * enchant) / 2;
	}

	public CacheItemHandler getHandler() {

		return handler;
	}

	public ItemStack getStoredInstance() {

		return handler.storedInstance;
	}

	protected void updateTrackers() {

		int curScale = getStoredCount() > 0 ? 1 + getScaledItemsStored(14) : 0;
		if (compareTracker != curScale) {
			compareTracker = curScale;
			callNeighborTileChange();
		}
		curScale = getStoredCount() > 0 ? 1 + Math.min(getScaledItemsStored(8), 7) : 0;
		if (meterTracker != curScale) {
			meterTracker = curScale;
			sendTilePacket(Side.CLIENT);
		}
	}

	public void setLocked(boolean lock) {

		if (getStoredInstance().isEmpty()) {
			lock = false;
		}
		this.lock = lock;
		handler.setLocked(lock);
		sendTilePacket(Side.CLIENT);
	}

	public boolean isLocked() {

		return lock;
	}

	public int getScaledItemsStored(int scale) {

		return MathHelper.round((long) getStoredCount() * scale / handler.getCapacity());
	}

	public int getStoredCount() {

		return handler.storedStack.getCount();
	}

	public ItemStack insertItem(ItemStack stack, boolean simulate) {

		if (isCreative) {
			handler.setItem(ItemHelper.cloneStack(stack, handler.getSlotLimit(0)));
			return stack;
		}
		return handler.insertItem(0, stack, simulate);
	}

	public ItemStack extractItem(int maxExtract, boolean simulate) {

		return handler.extractItem(0, maxExtract, isCreative || simulate);
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

		// TODO: Cache Size Limiting
		// return true;
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

		facing = nbt.getByte(CoreProps.FACING);
		handler.readFromNBT(nbt);
		lock = handler.isLocked();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setByte("EncHolding", enchantHolding);
		nbt.setByte(CoreProps.FACING, facing);
		handler.writeToNBT(nbt);
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
		payload.addInt(handler.getCapacity());

		return payload;
	}

	@Override
	public PacketBase getTilePacket() {

		PacketBase payload = super.getTilePacket();

		payload.addByte(enchantHolding);
		payload.addByte(facing);
		payload.addBool(lock);
		payload.addItemStack(ItemHelper.cloneStack(getStoredInstance()));
		payload.addInt(handler.getCount());
		payload.addInt(handler.getCapacity());

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketBase payload) {

		super.handleGuiPacket(payload);

		lock = payload.getBool();
		handler.setCapacity(payload.getInt());
	}

	@Override
	@SideOnly (Side.CLIENT)
	public void handleTilePacket(PacketBase payload) {

		super.handleTilePacket(payload);

		enchantHolding = payload.getByte();
		facing = payload.getByte();
		lock = payload.getBool();
		handler.setItem(payload.getItemStack(), payload.getInt());
		handler.setCapacity(payload.getInt());

		callBlockUpdate();
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

	/* ISidedTexture */
	@Override
	public int getNumPasses() {

		return 2;
	}

	@Override
	@SideOnly (Side.CLIENT)
	public TextureAtlasSprite getTexture(int side, int pass) {

		if (pass == 0) {
			if (side == 0) {
				return isCreative ? TETextures.CACHE_BOTTOM_C : TETextures.CACHE_BOTTOM[level];
			} else if (side == 1) {
				return isCreative ? TETextures.CACHE_TOP_C : TETextures.CACHE_TOP[level];
			}
			return side != facing ? isCreative ? TETextures.CACHE_SIDE_C : TETextures.CACHE_SIDE[level] : isCreative ? TETextures.CACHE_FACE_C : TETextures.CACHE_FACE[level];
		} else if (side < 6) {
			return side != facing ? TETextures.CONFIG_NONE : isCreative ? TETextures.CACHE_METER_C : TETextures.CACHE_METER[MathHelper.clamp(getStoredCount() > 0 ? 1 + getScaledItemsStored(8) : 0, 0, 8)];
		}
		return isCreative ? TETextures.CACHE_SIDE_C : TETextures.CACHE_SIDE[level];
	}

	/* ITileInfo */
	@Override
	public void getTileInfo(List<ITextComponent> info, EnumFacing side, EntityPlayer player, boolean debug) {

		if (debug) {
			return;
		}
		if (!getStoredInstance().isEmpty()) {
			info.add(new TextComponentTranslation("info.cofh.item").appendText(": " + StringHelper.getItemName(getStoredInstance())));
			info.add(new TextComponentTranslation("info.cofh.amount").appendText(": " + StringHelper.formatNumber(getStoredCount()) + "/" + StringHelper.formatNumber(handler.capacity)));
			info.add(new TextComponentTranslation(lock ? "info.cofh.locked" : "info.cofh.unlocked"));
		} else {
			info.add(new TextComponentTranslation("info.cofh.item").appendText(": ").appendSibling(new TextComponentTranslation("info.cofh.empty")));
		}
	}

	/* IInventoryRetainer */
	@Override
	public boolean retainInventory() {

		return true;
	}

	/* PLUGIN METHODS */
	//	@Override
	//	public void provideInfo(ProbeMode mode, IProbeInfo info, EnumFacing facing, EntityPlayer player) {
	//
	//		if (mode != ProbeMode.NORMAL) {
	//			IProbeInfo infoSub = info.horizontal(info.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER).borderColor(PluginTOP.chestContentsBorderColor).spacing(10));
	//
	//			ItemStack stored = getStoredItemType();
	//			infoSub.item(stored, info.defaultItemStyle().width(16).height(16)).text(TextStyleClass.INFO + stored.getDisplayName());
	//		}
	//	}

	/* CAPABILITIES */
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing from) {

		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, from);
	}

	@SuppressWarnings ("unchecked")
	@Override
	@Nullable
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {

		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return (T) handler;
		}
		return super.getCapability(capability, facing);
	}

	/* IItemHandler */
	public static class CacheItemHandler implements IItemHandler {

		protected TileCache tile;
		protected ItemStack storedInstance = ItemStack.EMPTY;
		protected ItemStack storedStack = ItemStack.EMPTY;

		protected int capacity;
		protected boolean locked;

		CacheItemHandler(TileCache tile, int capacity) {

			this(tile, ItemStack.EMPTY, capacity);
		}

		CacheItemHandler(TileCache tile, ItemStack stack, int capacity) {

			this.tile = tile;
			setItem(stack);
			this.capacity = capacity;
		}

		public CacheItemHandler readFromNBT(NBTTagCompound nbt) {

			locked = false;

			if (nbt.hasKey("Item")) {
				ItemStack stack = new ItemStack(nbt.getCompoundTag("Item"));
				storedInstance = ItemHelper.cloneStack(stack, 1);
				int storedCount = nbt.getInteger("StoredCount");
				storedStack = ItemHelper.cloneStack(stack, storedCount);
				locked = nbt.getBoolean("Lock");
			}
			return this;
		}

		public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

			nbt.setTag("Item", storedInstance.writeToNBT(new NBTTagCompound()));
			nbt.setInteger("StoredCount", storedStack.getCount());
			nbt.setBoolean("Lock", locked);

			return nbt;
		}

		public void setLocked(boolean lock) {

			if (lock) {
				setLocked();
			} else {
				clearLocked();
			}
		}

		public void setLocked() {

			if (locked || this.storedInstance.isEmpty()) {
				return;
			}
			locked = true;
		}

		public void clearLocked() {

			locked = false;
			if (this.storedStack.isEmpty()) {
				setItem(ItemStack.EMPTY);
			}
		}

		public void setItem(ItemStack stack, int count) {

			this.storedInstance = ItemHelper.cloneStack(stack, 1);
			this.storedStack = ItemHelper.cloneStack(stack, count);
		}

		public void setItem(ItemStack stack) {

			ItemStack curInstance = storedInstance.copy();
			this.storedInstance = ItemHelper.cloneStack(stack, 1);
			this.storedStack = stack;

			if (tile.world != null) {
				tile.updateTrackers();

				if (!ItemHelper.itemsIdentical(curInstance, storedInstance)) {
					tile.markChunkDirty();
				}
			}
		}

		public void setCapacity(int capacity) {

			this.capacity = capacity;
		}

		public boolean isLocked() {

			return locked;
		}

		public int getCapacity() {

			return capacity;
		}

		public int getCount() {

			return storedStack.getCount();
		}

		public int getSpace() {

			int ret = capacity - storedStack.getCount();
			return ret < 0 ? 0 : ret;
		}

		@Override
		public int getSlots() {

			return 1;
		}

		@Nonnull
		@Override
		public ItemStack getStackInSlot(int slot) {

			return storedStack;
		}

		@Nonnull
		@Override
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {

			if (stack.isEmpty()) {
				return stack;
			}
			int toInsert = stack.getCount();
			if (getSpace() < toInsert) {
				toInsert = getSpace();
			}
			ItemStack ret = ItemHelper.cloneStack(stack, stack.getCount() - toInsert);
			if (storedInstance.isEmpty()) {
				if (!simulate) {
					setItem(ItemHelper.cloneStack(stack, toInsert));
				}
				return ret;
			}
			if (!ItemHelper.itemsIdentical(stack, storedInstance)) {
				return stack;
			}
			if (!simulate) {
				storedStack.grow(toInsert);
				tile.updateTrackers();
				tile.markChunkDirty();
			}
			return ret;
		}

		@Nonnull
		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {

			if (storedStack.isEmpty()) {
				return ItemStack.EMPTY;
			}
			int toExtract = Math.min(amount, storedStack.getMaxStackSize());
			if (storedStack.getCount() < toExtract) {
				toExtract = storedStack.getCount();
			}
			ItemStack ret = ItemHelper.cloneStack(storedStack, toExtract);
			simulate |= tile.isCreative;
			if (!simulate) {
				storedStack.shrink(toExtract);
				if (storedStack.isEmpty()) {
					if (!locked) {
						setItem(ItemStack.EMPTY);
						tile.sendTilePacket(Side.CLIENT);
					}
				}
				tile.updateTrackers();
				tile.markChunkDirty();
			}
			return ret;
		}

		@Override
		public int getSlotLimit(int slot) {

			return capacity;
		}

	}

}
