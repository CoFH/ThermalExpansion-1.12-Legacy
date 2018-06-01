package cofh.thermalexpansion.block.storage;

import cofh.api.item.IUpgradeItem;
import cofh.api.item.IUpgradeItem.UpgradeType;
import cofh.api.tileentity.IInventoryRetainer;
import cofh.api.tileentity.IReconfigurableFacing;
import cofh.core.block.TileInventory;
import cofh.core.init.CoreProps;
import cofh.core.network.PacketBase;
import cofh.core.util.helpers.*;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.gui.client.storage.GuiStrongbox;
import cofh.thermalexpansion.gui.container.storage.ContainerStrongbox;
import cofh.thermalexpansion.init.TETextures;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.api.IDropoffManager;

import java.util.Arrays;

@Optional.Interface (iface = "vazkii.quark.api.IDropoffManager", modid = "quark")
public class TileStrongbox extends TileInventory implements ITickable, ISidedInventory, IReconfigurableFacing, IInventoryRetainer, IDropoffManager {

	public static void initialize() {

		GameRegistry.registerTileEntity(TileStrongbox.class, "thermalexpansion:storage_strongbox");

		config();
	}

	public static void config() {

		String category = "Storage.Strongbox";
		String comment = "If TRUE, Strongboxes are enabled.";
		BlockStrongbox.enable = ThermalExpansion.CONFIG.get(category, "Enable", BlockStrongbox.enable, comment);

		comment = "If TRUE, Strongboxes may be turned into Creative versions using a Creative Conversion Kit.";
		BlockStrongbox.enableCreative = ThermalExpansion.CONFIG.get(category, "Creative", BlockStrongbox.enableCreative, comment);

		comment = "If TRUE, Strongboxes are securable.";
		BlockStrongbox.enableSecurity = ThermalExpansion.CONFIG.get(category, "Securable", BlockStrongbox.enableSecurity, comment);

		comment = "If TRUE, 'Classic' Crafting is enabled - Non-Creative Upgrade Kits WILL NOT WORK in a Crafting Grid.";
		BlockStrongbox.enableClassicRecipes = ThermalExpansion.CONFIG.get(category, "ClassicCrafting", BlockStrongbox.enableClassicRecipes, comment);

		comment = "If TRUE, Strongboxes can be upgraded in a Crafting Grid using Kits. If Classic Crafting is enabled, only the Creative Conversion Kit may be used in this fashion.";
		BlockStrongbox.enableUpgradeKitCrafting = ThermalExpansion.CONFIG.get(category, "UpgradeKitCrafting", BlockStrongbox.enableUpgradeKitCrafting, comment);
	}

	private static final int TIME_CONSTANT = 200;

	byte facing = 3;
	public byte enchantHolding;

	private int offset;

	/* CLIENT */
	public float prevLidAngle;
	public float lidAngle;
	public int numUsingPlayers;

	public TileStrongbox() {

		offset = MathHelper.RANDOM.nextInt(TIME_CONSTANT);
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

		return "tile.thermalexpansion.storage.strongbox.name";
	}

	@Override
	public int getComparatorInputOverride() {

		return getAccess().isPublic() ? Container.calcRedstoneFromInventory(this) : 0;
	}

	@Override
	public boolean enableSecurity() {

		return BlockStrongbox.enableSecurity;
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
					return !BlockStrongbox.enableClassicRecipes;
				}
				break;
			case FULL:
				if (uLevel > level) {
					return !BlockStrongbox.enableClassicRecipes;
				}
				break;
			case CREATIVE:
				return !isCreative && BlockStrongbox.enableCreative;
		}
		return false;
	}

	@Override
	protected boolean setLevel(int level) {

		if (super.setLevel(level)) {
			// Keep Inventory
			if (inventory.length != CoreProps.STORAGE_SIZE[getStorageIndex()]) {
				ItemStack[] tempInv = new ItemStack[inventory.length];
				for (int i = 0; i < tempInv.length && i < inventory.length; i++) {
					tempInv[i] = inventory[i].isEmpty() ? ItemStack.EMPTY : inventory[i].copy();
				}
				createInventory();
				for (int i = 0; i < tempInv.length && i < inventory.length; i++) {
					inventory[i] = tempInv[i].isEmpty() ? ItemStack.EMPTY : tempInv[i].copy();
				}
			}
			return true;
		}
		return false;
	}

	@Override
	protected int getNumAugmentSlots(int level) {

		return 0;
	}

	@Override
	public boolean hasClientUpdate() {

		return true;
	}

	@Override
	public boolean onWrench(EntityPlayer player, EnumFacing side) {

		return rotateBlock();
	}

	@Override
	public boolean receiveClientEvent(int id, int type) {

		if (id == 1) {
			numUsingPlayers = type;
			return true;
		}
		return false;
	}

	@Override
	public void update() {

		getNumPlayers();

		if (numUsingPlayers > 0 && !world.isRemote && world.getTotalWorldTime() % 200 == 0) {
			world.addBlockEvent(pos, getBlockType(), 1, numUsingPlayers);
		}
		prevLidAngle = lidAngle;
		lidAngle = MathHelper.approachLinear(lidAngle, numUsingPlayers > 0 ? 1F : 0F, 0.1F);

		if (prevLidAngle >= 0.5 && lidAngle < 0.5) {
			world.playSound(null, pos, SoundEvents.BLOCK_CHEST_CLOSE, SoundCategory.BLOCKS, 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
		} else if (prevLidAngle == 0 && lidAngle > 0) {
			world.playSound(null, pos, SoundEvents.BLOCK_CHEST_OPEN, SoundCategory.BLOCKS, 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
		}
	}

	/* COMMON METHODS */
	public static int getCapacity(int level, int enchant) {

		return CoreProps.STORAGE_SIZE[Math.min(2 * (1 + level) + enchant, CoreProps.STORAGE_SIZE.length - 1)];
	}

	public void createInventory() {

		inventory = new ItemStack[CoreProps.STORAGE_SIZE[getStorageIndex()]];
		Arrays.fill(inventory, ItemStack.EMPTY);
	}

	public void getNumPlayers() {

		if (ServerHelper.isClientWorld(world)) {
			return;
		}
		if (numUsingPlayers != 0 && (world.getTotalWorldTime() + offset) % TIME_CONSTANT == 0) {
			numUsingPlayers = 0;
			float dist = 5.0F;

			for (EntityPlayer player : world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(pos.getX() - dist, pos.getY() - dist, pos.getZ() - dist, pos.getX() + 1 + dist, pos.getY() + 1 + dist, pos.getZ() + 1 + dist))) {
				if (player.openContainer instanceof ContainerStrongbox) {
					TileStrongbox box = ((ContainerStrongbox) player.openContainer).getTile();
					if (box == this) {
						++numUsingPlayers;
					}
				}
			}
		}
	}

	public int getStorageIndex() {

		if (isCreative) {
			return 0;
		}
		return Math.min(2 * (1 + level) + enchantHolding, CoreProps.STORAGE_SIZE.length - 1);
	}

	public double getRadianLidAngle(float f) {

		float angle = MathHelper.interpolate(prevLidAngle, lidAngle, f);
		angle = 1.0F - angle;
		angle = 1.0F - angle * angle * angle;
		return angle * Math.PI * -0.5;
	}

	@SideOnly (Side.CLIENT)
	public TextureAtlasSprite getBreakTexture() {

		if (isCreative) {
			return TETextures.STRONGBOX_TOP_C;
		}
		return TETextures.STRONGBOX_TOP[level];
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiStrongbox(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerStrongbox(inventory, this);
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		enchantHolding = nbt.getByte("EncHolding");

		super.readFromNBT(nbt);

		facing = nbt.getByte("Facing");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setByte("Facing", facing);
		nbt.setByte("EncHolding", enchantHolding);
		return nbt;
	}

	/* NETWORK METHODS */
	@Override
	public PacketBase getTilePacket() {

		PacketBase payload = super.getTilePacket();

		payload.addByte(facing);
		payload.addByte(enchantHolding);

		return payload;
	}

	@Override
	public PacketBase getGuiPacket() {

		return null;
	}

	@Override
	@SideOnly (Side.CLIENT)
	public void handleTilePacket(PacketBase payload) {

		super.handleTilePacket(payload);

		facing = payload.getByte();

		byte tempEnchant = enchantHolding;
		enchantHolding = payload.getByte();

		if (inventory.length <= 0 || enchantHolding != tempEnchant) {
			createInventory();
		}
	}

	/* IInventory */
	@Override
	public ItemStack decrStackSize(int slot, int amount) {

		if (isCreative) {
			return ItemHelper.cloneStack(inventory[slot], amount);
		}
		return super.decrStackSize(slot, amount);
	}

	@Override
	public ItemStack getStackInSlot(int slot) {

		if (isCreative) {
			return ItemHelper.cloneStack(inventory[slot]);
		}
		return super.getStackInSlot(slot);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {

		if (isCreative) {
			if (stack.isEmpty()) {
				return;
			}
			inventory[slot] = stack;
			inventory[slot].setCount(stack.getMaxStackSize());
			return;
		}
		super.setInventorySlotContents(slot, stack);
	}

	@Override
	public void openInventory(EntityPlayer player) {

		if (player.isSpectator()) {
			return;
		}
		if (numUsingPlayers < 0) {
			numUsingPlayers = 0;
		}
		++numUsingPlayers;
		world.addBlockEvent(pos, getBlockType(), 1, numUsingPlayers);
		callNeighborStateChange();
	}

	@Override
	public void closeInventory(EntityPlayer player) {

		if (player.isSpectator()) {
			return;
		}
		if (getBlockType() instanceof BlockStrongbox) {
			--numUsingPlayers;
			world.addBlockEvent(pos, getBlockType(), 1, numUsingPlayers);
			callNeighborStateChange();
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
		sendTilePacket(Side.CLIENT);
		return true;
	}

	@Override
	public boolean setFacing(int side, boolean alternate) {

		if (side < 2 || side > 5) {
			return false;
		}
		facing = (byte) side;
		markDirty();
		sendTilePacket(Side.CLIENT);
		return true;
	}

	/* ISidedInventory */
	@Override
	public int[] getSlotsForFace(EnumFacing side) {

		return access.isPublic() ? CoreProps.SLOTS[getStorageIndex()] : CoreProps.EMPTY_INVENTORY;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, EnumFacing side) {

		return access.isPublic();
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, EnumFacing side) {

		return access.isPublic();
	}

	/* IInventoryRetainer */
	@Override
	public boolean retainInventory() {

		return true;
	}

	/* IDropoffManager */
	@Override
	public boolean acceptsDropoff(EntityPlayer player) {

		return player != null && canPlayerAccess(player);
	}

}
