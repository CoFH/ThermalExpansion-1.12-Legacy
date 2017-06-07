package cofh.thermalexpansion.block.storage;

import cofh.api.item.IUpgradeItem;
import cofh.api.item.IUpgradeItem.UpgradeType;
import cofh.api.tileentity.IReconfigurableFacing;
import cofh.core.init.CoreProps;
import cofh.core.network.PacketCoFHBase;
import cofh.core.util.helpers.AugmentHelper;
import cofh.core.util.tileentity.IInventoryRetainer;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.TileInventory;
import cofh.thermalexpansion.gui.client.storage.GuiStrongbox;
import cofh.thermalexpansion.gui.container.storage.ContainerStrongbox;
import cofh.thermalexpansion.init.TETextures;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class TileStrongbox extends TileInventory implements ITickable, ISidedInventory, IReconfigurableFacing, IInventoryRetainer {

	private static boolean enableSecurity = true;

	public static void initialize() {

		GameRegistry.registerTileEntity(TileStrongbox.class, "thermalexpansion:storage_strongbox");

		config();
	}

	public static void config() {

		String comment = "Enable this to allow for Strongboxes to be securable.";
		enableSecurity = ThermalExpansion.CONFIG.get("Security", "Strongbox.Securable", enableSecurity, comment);

		String category = "Storage.Strongbox";
		BlockStrongbox.enable = ThermalExpansion.CONFIG.get(category, "Enable", true);
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
	public String getTileName() {

		return "tile.thermalexpansion.storage.strongbox.name";
	}

	@Override
	public int getType() {

		return 0;
	}

	@Override
	public boolean enableSecurity() {

		return enableSecurity;
	}

	@Override
	protected boolean setLevel(int level) {

		if (super.setLevel(level)) {
			// Keep Inventory
			if (inventory.length != CoreProps.STORAGE_SIZE[getStorageIndex()]) {
				if (isCreative) {
					createInventory();
				} else {
					ItemStack[] tempInv = new ItemStack[inventory.length];
					for (int i = 0; i < inventory.length; i++) {
						tempInv[i] = inventory[i] == null ? null : inventory[i].copy();
					}
					createInventory();
					for (int i = 0; i < tempInv.length; i++) {
						inventory[i] = tempInv[i] == null ? null : tempInv[i].copy();
					}
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

		if (numUsingPlayers > 0 && !worldObj.isRemote && worldObj.getTotalWorldTime() % 200 == 0) {
			worldObj.addBlockEvent(pos, getBlockType(), 1, numUsingPlayers);
		}
		prevLidAngle = lidAngle;
		lidAngle = MathHelper.approachLinear(lidAngle, numUsingPlayers > 0 ? 1F : 0F, 0.1F);

		if (prevLidAngle >= 0.5 && lidAngle < 0.5) {
			worldObj.playSound(null, pos, SoundEvents.BLOCK_CHEST_CLOSE, SoundCategory.BLOCKS, 0.5F, worldObj.rand.nextFloat() * 0.1F + 0.9F);
		} else if (prevLidAngle == 0 && lidAngle > 0) {
			worldObj.playSound(null, pos, SoundEvents.BLOCK_CHEST_OPEN, SoundCategory.BLOCKS, 0.5F, worldObj.rand.nextFloat() * 0.1F + 0.9F);
		}
	}

	/* COMMON METHODS */
	public static int getCapacity(int level, int enchant) {

		return CoreProps.STORAGE_SIZE[Math.min(2 * (1 + level) + enchant, CoreProps.STORAGE_SIZE.length - 1)];
	}

	public void createInventory() {

		inventory = new ItemStack[CoreProps.STORAGE_SIZE[getStorageIndex()]];
	}

	public void getNumPlayers() {

		if (ServerHelper.isClientWorld(worldObj)) {
			return;
		}
		if (numUsingPlayers != 0 && (worldObj.getTotalWorldTime() + offset) % TIME_CONSTANT == 0) {
			numUsingPlayers = 0;
			float dist = 5.0F;

			for (EntityPlayer player : worldObj.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(pos.getX() - dist, pos.getY() - dist, pos.getZ() - dist, pos.getX() + 1 + dist, pos.getY() + 1 + dist, pos.getZ() + 1 + dist))) {
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
	public PacketCoFHBase getTilePacket() {

		PacketCoFHBase payload = super.getTilePacket();

		payload.addByte(facing);
		payload.addByte(enchantHolding);

		return payload;
	}

	@Override
	public PacketCoFHBase getGuiPacket() {

		return null;
	}

	/* ITilePacketHandler */
	@Override
	public void handleTilePacket(PacketCoFHBase payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

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
			if (stack == null) {
				return;
			}
			inventory[slot] = stack;
			inventory[slot].stackSize = stack.getMaxStackSize();
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
		worldObj.addBlockEvent(pos, getBlockType(), 1, numUsingPlayers);
		callNeighborStateChange();
	}

	@Override
	public void closeInventory(EntityPlayer player) {

		if (player.isSpectator()) {
			return;
		}
		if (getBlockType() instanceof BlockStrongbox) {
			--numUsingPlayers;
			worldObj.addBlockEvent(pos, getBlockType(), 1, numUsingPlayers);
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
	public boolean setFacing(int side) {

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
