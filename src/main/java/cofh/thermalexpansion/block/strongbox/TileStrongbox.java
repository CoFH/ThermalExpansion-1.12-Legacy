package cofh.thermalexpansion.block.strongbox;

import cofh.api.tileentity.IInventoryRetainer;
import cofh.api.tileentity.IReconfigurableFacing;
import cofh.core.CoFHProps;
import cofh.core.network.PacketCoFHBase;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.TileInventory;
import cofh.thermalexpansion.gui.client.GuiStrongbox;
import cofh.thermalexpansion.gui.container.ContainerStrongbox;
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

import java.util.List;

public class TileStrongbox extends TileInventory implements ITickable, IReconfigurableFacing, ISidedInventory, IInventoryRetainer {

	public static void initialize() {

		GameRegistry.registerTileEntity(TileStrongbox.class, "thermalexpansion.Strongbox");
		configure();
	}

	public static void configure() {

		String comment = "Enable this to allow for Strongboxes to be securable.";
		enableSecurity = ThermalExpansion.CONFIG.get("Security", "Strongbox.All.Securable", enableSecurity, comment);
	}

	public static boolean enableSecurity = true;

	public byte type = 1;
	public byte enchant = 0;
	public byte facing = 3;

	public double prevLidAngle;
	public double lidAngle;

	public int numUsingPlayers;

	public TileStrongbox() {

	}

	public TileStrongbox(int metadata) {

		type = (byte) metadata;
		createInventory();
	}

	@Override
	public String getName() {

		return "tile.thermalexpansion.strongbox." + BlockStrongbox.NAMES[getType()] + ".name";
	}

	@Override
	public int getType() {

		return type;
	}

	@Override
	public boolean enableSecurity() {

		return enableSecurity;
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
			worldObj.addBlockEvent(getPos(), getBlockType(), 1, numUsingPlayers);
		}
		prevLidAngle = lidAngle;
		lidAngle = MathHelper.approachLinear(lidAngle, numUsingPlayers > 0 ? 1 : 0, 0.1);

		if (prevLidAngle >= 0.5 && lidAngle < 0.5) {
			worldObj.playSound(null, getPos(), SoundEvents.BLOCK_CHEST_CLOSE, SoundCategory.BLOCKS, 0.5F, worldObj.rand.nextFloat() * 0.1F + 0.9F);
		} else if (prevLidAngle == 0 && lidAngle > 0) {
			worldObj.playSound(null, getPos(), SoundEvents.BLOCK_CHEST_OPEN, SoundCategory.BLOCKS, 0.5F, worldObj.rand.nextFloat() * 0.1F + 0.9F);
		}
	}

	public double getRadianLidAngle(float f) {

		double a = MathHelper.interpolate(prevLidAngle, lidAngle, f);
		a = 1.0F - a;
		a = 1.0F - a * a * a;
		return a * Math.PI * -0.5;
	}

	public int getStorageIndex() {

		return type > 0 ? Math.min(2 * type + enchant, CoFHProps.STORAGE_SIZE.length - 1) : 0;
	}

	public void createInventory() {

		inventory = new ItemStack[CoFHProps.STORAGE_SIZE[getStorageIndex()]];
	}

	public void getNumPlayers() {

		if (ServerHelper.isClientWorld(worldObj)) {
			return;
		}
		if (numUsingPlayers != 0 && (worldObj.getTotalWorldTime() + pos.getX() + pos.getY() + pos.getZ()) % 200 == 0) {
			numUsingPlayers = 0;
			float dist = 5.0F;
			List<EntityPlayer> nearbyEntities = worldObj.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(pos.getX() - dist, pos.getY() - dist, pos.getZ() - dist, pos.getX() + 1 + dist, pos.getY() + 1 + dist, pos.getZ() + 1 + dist));

			for (EntityPlayer player : nearbyEntities) {
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

		return new GuiStrongbox(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerStrongbox(inventory, this);
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		type = nbt.getByte("Type");
		enchant = nbt.getByte("Enchant");
		facing = nbt.getByte("Facing");

		if (type > 0) {
			inventory = new ItemStack[CoFHProps.STORAGE_SIZE[getStorageIndex()]];
		} else {
			inventory = new ItemStack[1];
		}
		super.readFromNBT(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setByte("Type", type);
		nbt.setByte("Enchant", enchant);
		nbt.setByte("Facing", facing);
		return nbt;
	}

	/* NETWORK METHODS */
	@Override
	public PacketCoFHBase getPacket() {

		PacketCoFHBase payload = super.getPacket();

		payload.addByte(type);
		payload.addByte(enchant);
		payload.addByte(facing);

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

		type = payload.getByte();

		byte prevEnchant = enchant;
		enchant = payload.getByte();

		if (!isServer) {
			facing = payload.getByte();
			if (enchant != prevEnchant || inventory.length <= 0) {
				createInventory();
			}
		} else {
			payload.getByte();
		}
	}

	/* IInventory */
	@Override
	public void openInventory(EntityPlayer player) {

		if (numUsingPlayers < 0) {
			numUsingPlayers = 0;
		}
		++numUsingPlayers;
		worldObj.addBlockEvent(getPos(), getBlockType(), 1, numUsingPlayers);
		worldObj.notifyBlockOfStateChange(getPos(), getBlockType());
	}

	@Override
	public void closeInventory(EntityPlayer player) {

		if (getBlockType() != null && getBlockType() instanceof BlockStrongbox) {
			--numUsingPlayers;
			worldObj.addBlockEvent(getPos(), getBlockType(), 1, numUsingPlayers);
			worldObj.notifyBlockOfStateChange(getPos(), getBlockType());
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
		markDirty();
		sendUpdatePacket(Side.CLIENT);
		return true;
	}

	/* ISidedInventory */
	@Override
	public int[] getSlotsForFace(EnumFacing side) {

		return access.isPublic() ? CoFHProps.SLOTS[getStorageIndex()] : CoFHProps.EMPTY_INVENTORY;
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
	public boolean retainInventory() {

		return true;
	}
}
