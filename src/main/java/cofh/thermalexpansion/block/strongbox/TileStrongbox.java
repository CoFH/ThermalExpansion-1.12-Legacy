package cofh.thermalexpansion.block.strongbox;

import cofh.api.inventory.IInventoryRetainer;
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
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;

public class TileStrongbox extends TileInventory implements IReconfigurableFacing, ISidedInventory, IInventoryRetainer {

	public static void initialize() {

		GameRegistry.registerTileEntity(TileStrongbox.class, "thermalexpansion.Strongbox");
		configure();
	}

	public static void configure() {

		String comment = "Enable this to allow for Strongboxes to be securable. (Default: true)";
		enableSecurity = ThermalExpansion.config.get("security", "Strongbox.All.Securable", enableSecurity, comment);
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
	public boolean onWrench(EntityPlayer player, int hitSide) {

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
	public void updateEntity() {

		getNumPlayers();

		if (numUsingPlayers > 0 && !worldObj.isRemote && worldObj.getTotalWorldTime() % 200 == 0) {
			worldObj.addBlockEvent(xCoord, yCoord, zCoord, getBlockType(), 1, numUsingPlayers);
		}
		prevLidAngle = lidAngle;
		lidAngle = MathHelper.approachLinear(lidAngle, numUsingPlayers > 0 ? 1 : 0, 0.1);

		if (prevLidAngle >= 0.5 && lidAngle < 0.5) {
			worldObj.playSoundEffect(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, "random.chestclosed", 0.5F, worldObj.rand.nextFloat() * 0.1F + 0.9F);
		} else if (prevLidAngle == 0 && lidAngle > 0) {
			worldObj.playSoundEffect(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, "random.chestopen", 0.5F, worldObj.rand.nextFloat() * 0.1F + 0.9F);
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
		if (numUsingPlayers != 0 && (worldObj.getTotalWorldTime() + xCoord + yCoord + zCoord) % 200 == 0) {
			numUsingPlayers = 0;
			float dist = 5.0F;
			List<EntityPlayer> nearbyEntities = worldObj.getEntitiesWithinAABB(EntityPlayer.class,
					AxisAlignedBB.getBoundingBox(xCoord - dist, yCoord - dist, zCoord - dist, xCoord + 1 + dist, yCoord + 1 + dist, zCoord + 1 + dist));
			Iterator<EntityPlayer> anIt = nearbyEntities.iterator();

			while (anIt.hasNext()) {
				EntityPlayer player = anIt.next();
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
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setByte("Type", type);
		nbt.setByte("Enchant", enchant);
		nbt.setByte("Facing", facing);
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
	public void openInventory() {

		if (numUsingPlayers < 0) {
			numUsingPlayers = 0;
		}
		++numUsingPlayers;
		worldObj.addBlockEvent(xCoord, yCoord, zCoord, getBlockType(), 1, numUsingPlayers);
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
	}

	@Override
	public void closeInventory() {

		if (getBlockType() != null && getBlockType() instanceof BlockStrongbox) {
			--numUsingPlayers;
			worldObj.addBlockEvent(xCoord, yCoord, zCoord, getBlockType(), 1, numUsingPlayers);
			worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
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
	public int[] getAccessibleSlotsFromSide(int side) {

		return access.isPublic() ? CoFHProps.SLOTS[getStorageIndex()] : CoFHProps.EMPTY_INVENTORY;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int side) {

		return access.isPublic();
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int side) {

		return access.isPublic();
	}

}
