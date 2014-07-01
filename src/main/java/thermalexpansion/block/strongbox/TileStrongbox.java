package thermalexpansion.block.strongbox;

import cofh.api.core.ISecurable;
import cofh.api.tileentity.IReconfigurableFacing;
import cofh.core.CoFHProps;
import cofh.network.CoFHPacket;
import cofh.util.BlockHelper;
import cofh.util.MathHelper;
import cofh.util.ServerHelper;
import cofh.util.StringHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

import java.util.Iterator;
import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.block.TileTEBase;
import thermalexpansion.core.TEProps;
import thermalexpansion.gui.GuiHandler;
import thermalexpansion.gui.client.GuiStrongbox;
import thermalexpansion.gui.container.ContainerStrongbox;

public class TileStrongbox extends TileTEBase implements IReconfigurableFacing, ISecurable, ISidedInventory {

	public static void initialize() {

		GameRegistry.registerTileEntity(TileStrongbox.class, "thermalexpansion.Strongbox");
		configure();
	}

	public static void configure() {

		String comment = "Enable this to allow for Strongboxes to be secure inventories. (Default: true)";
		enableSecurity = ThermalExpansion.config.get("block.security", "Strongbox.Secure", enableSecurity, comment);
	}

	public static boolean enableSecurity = true;

	String owner = CoFHProps.DEFAULT_OWNER;
	private AccessMode access = AccessMode.PUBLIC;

	public byte type;
	public byte enchant = 0;
	public byte facing = 3;

	public double prevLidAngle;
	public double lidAngle;

	public int numUsingPlayers;

	/* Client-Side Only */
	public boolean canAccess = true;

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

		return type > 0 ? 2 * type + enchant : 0;
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
			List nearbyEntities = worldObj.getEntitiesWithinAABB(EntityPlayer.class,
					AxisAlignedBB.getBoundingBox(xCoord - dist, yCoord - dist, zCoord - dist, xCoord + 1 + dist, yCoord + 1 + dist, zCoord + 1 + dist));
			Iterator anIt = nearbyEntities.iterator();

			while (anIt.hasNext()) {
				EntityPlayer player = (EntityPlayer) anIt.next();
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
	public GuiContainer getGuiClient(InventoryPlayer inventory) {

		return new GuiStrongbox(inventory, this);
	}

	@Override
	public Container getGuiServer(InventoryPlayer inventory) {

		return new ContainerStrongbox(inventory, this);
	}

	@Override
	public boolean openGui(EntityPlayer player) {

		if (canPlayerAccess(player.getDisplayName())) {
			player.openGui(ThermalExpansion.instance, GuiHandler.TILE_ID, worldObj, xCoord, yCoord, zCoord);
			return true;
		}
		if (ServerHelper.isServerWorld(worldObj)) {
			player.addChatMessage(new ChatComponentText(StringHelper.localize("message.cofh.secure1") + " " + owner + "! "
					+ StringHelper.localize("message.cofh.secure2")));
		}
		return true;
	}

	@Override
	public void receiveGuiNetworkData(int i, int j) {

		if (j == 0) {
			canAccess = false;
		} else {
			canAccess = true;
		}
	}

	@Override
	public void sendGuiNetworkData(Container container, ICrafting player) {

		int access = 0;
		if (canPlayerAccess(((EntityPlayer) player).getDisplayName())) {
			access = 1;
		}
		player.sendProgressBarUpdate(container, 0, access);
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		type = nbt.getByte("Type");
		enchant = nbt.getByte("Enchant");
		facing = nbt.getByte("Facing");
		access = AccessMode.values()[nbt.getByte("Access")];
		owner = nbt.getString("Owner");

		if (type > 0) {
			inventory = new ItemStack[CoFHProps.STORAGE_SIZE[2 * type + enchant]];
		} else {
			inventory = new ItemStack[1];
		}
		if (!enableSecurity) {
			access = AccessMode.PUBLIC;
		}
		super.readFromNBT(nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setByte("Type", type);
		nbt.setByte("Enchant", enchant);
		nbt.setByte("Facing", facing);
		nbt.setByte("Access", (byte) access.ordinal());
		nbt.setString("Owner", owner);
	}

	/* NETWORK METHODS */
	@Override
	public CoFHPacket getPacket() {

		CoFHPacket payload = super.getPacket();

		payload.addByte(type);
		payload.addByte(enchant);
		payload.addByte((byte) access.ordinal());
		payload.addByte(facing);
		payload.addString(owner);

		return payload;
	}

	/* ITilePacketHandler */
	@Override
	public void handleTilePacket(CoFHPacket payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		type = payload.getByte();
		enchant = payload.getByte();
		access = ISecurable.AccessMode.values()[payload.getByte()];

		if (!isServer) {
			facing = payload.getByte();
			owner = payload.getString();
			if (inventory.length <= 0) {
				createInventory();
			}
		} else {
			payload.getByte();
			payload.getString();
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
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
		sendUpdatePacket(Side.CLIENT);
		return true;
	}

	/* ISecureable */
	@Override
	public boolean setAccess(AccessMode access) {

		this.access = access;
		sendUpdatePacket(Side.SERVER);
		return true;
	}

	@Override
	public AccessMode getAccess() {

		return access;
	}

	@Override
	public boolean setOwnerName(String name) {

		if (owner.equals(CoFHProps.DEFAULT_OWNER)) {
			owner = name;
			return true;
		}
		return false;
	}

	@Override
	public String getOwnerName() {

		return owner;
	}

	/* ISidedInventory */
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {

		return access.isPublic() ? CoFHProps.SLOTS[type] : TEProps.EMPTY_INVENTORY;
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
