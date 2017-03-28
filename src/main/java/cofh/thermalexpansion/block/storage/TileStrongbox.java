package cofh.thermalexpansion.block.storage;

import cofh.api.tileentity.IReconfigurableFacing;
import cofh.core.init.CoreProps;
import cofh.core.network.PacketCoFHBase;
import cofh.core.util.tileentity.IInventoryRetainer;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.TileInventory;
import cofh.thermalexpansion.gui.container.storage.ContainerStrongbox;
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

	public static void initialize() {

		GameRegistry.registerTileEntity(TileStrongbox.class, "thermalexpansion.Strongbox");
		config();
	}

	public static void config() {

		String comment = "Enable this to allow for Strongboxes to be securable.";
		enableSecurity = ThermalExpansion.CONFIG.get("Security", "Strongbox.Securable", enableSecurity, comment);
	}

	public static boolean enableSecurity = true;

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
	protected int getNumAugmentSlots(int level) {

		return 0;
	}

	@Override
	public boolean onWrench(EntityPlayer player, EnumFacing side) {

		return rotateBlock();
	}

	@Override
	public boolean receiveClientEvent(int i, int j) {

		if (i == 1) {
			numUsingPlayers = j;
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

	public double getRadianLidAngle(float f) {

		float angle = MathHelper.interpolate(prevLidAngle, lidAngle, f);
		angle = 1.0F - angle;
		angle = 1.0F - angle * angle * angle;
		return angle * Math.PI * -0.5;
	}

	public int getStorageIndex() {

		return 0;
		//return type > 0 ? Math.min(2 * type + enchant, CoreProps.STORAGE_SIZE.length - 1) : 0;
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

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return null;
		//return new GuiStrongbox(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return null;
		//return new ContainerStrongbox(inventory, this);
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		facing = nbt.getByte("Facing");
		enchantHolding = nbt.getByte("EncHolding");
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
		enchantHolding = payload.getByte();
	}

	/* IInventory */
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

	/* IInventoryRetainer */
	@Override
	public boolean retainInventory() {

		return true;
	}

}
