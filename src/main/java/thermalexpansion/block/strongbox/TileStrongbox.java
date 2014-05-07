package thermalexpansion.block.strongbox;

import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import thermalexpansion.ThermalExpansion;
import thermalexpansion.block.TileInventory;
import thermalexpansion.core.TEProps;
import thermalexpansion.gui.container.ContainerStrongbox;
import cofh.api.tileentity.IReconfigurableFacing;
import cofh.api.tileentity.ISecureTile;
import cofh.core.CoFHProps;
import cofh.util.BlockHelper;
import cofh.util.MathHelper;
import cofh.util.ServerHelper;
import cofh.util.StringHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

public class TileStrongbox extends TileInventory implements ISecureTile, IReconfigurableFacing, ISidedInventory {

	public static void initialize() {

		GameRegistry.registerTileEntity(TileStrongbox.class, "cofh.thermalexpansion.Strongbox");
		guiId = ThermalExpansion.proxy.registerGui("Strongbox", null, true);
		configure();
	}

	protected static int guiId;

	protected static final int[] INV_SIZE = { 1, 18, 36, 54, 72 };
	protected static final int[][] SLOTS = new int[5][];
	public static boolean enableSecurity = true;

	static {
		for (int i = 0; i < 5; i++) {
			SLOTS[i] = new int[INV_SIZE[i]];
			for (int j = 0; j < INV_SIZE[i]; j++) {
				SLOTS[i][j] = j;
			}
		}
	}

	public static void configure() {

		String comment = "Enable this to allow for Strongboxes to be secure inventories. (Default: true)";
		enableSecurity = ThermalExpansion.config.get("block.security", "Strongbox.Secure", enableSecurity, comment);
	}

	String owner = CoFHProps.DEFAULT_OWNER;
	private AccessMode access = AccessMode.PUBLIC;

	public byte type;
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
		inventory = new ItemStack[INV_SIZE[type]];
	}

	@Override
	public String getName() {

		return "tile.thermalexpansion.strongbox." + BlockStrongbox.NAMES[getType()] + ".name";
	}

	@Override
	public int getType() {

		return type;
	}

	public double getRadianLidAngle(float f) {

		double a = MathHelper.interpolate(prevLidAngle, lidAngle, f);
		a = 1.0F - a;
		a = 1.0F - a * a * a;
		return a * Math.PI * -0.5;
	}

	public void getNumPlayers() {

		if (ServerHelper.isClientWorld(worldObj)) {
			return;
		}
		if (numUsingPlayers != 0 && (worldObj.getTotalWorldTime() + xCoord + yCoord + zCoord) % 200 == 0) {
			numUsingPlayers = 0;
			float dist = 5.0F;
			List nearbyEntities = worldObj.getEntitiesWithinAABB(EntityPlayer.class,
					AxisAlignedBB.getAABBPool().getAABB(xCoord - dist, yCoord - dist, zCoord - dist, xCoord + 1 + dist, yCoord + 1 + dist, zCoord + 1 + dist));
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

	@Override
	public boolean receiveClientEvent(int i, int j) {

		if (i == 1) {
			numUsingPlayers = j;
			return true;
		}
		return false;
	}

	@Override
	public boolean onWrench(EntityPlayer player, int hitSide) {

		return rotateBlock();
	}

	@Override
	public boolean openGui(EntityPlayer player) {

		if (canPlayerAccess(player.getDisplayName())) {
			player.openGui(ThermalExpansion.instance, guiId, worldObj, xCoord, yCoord, zCoord);
			return true;
		}
		if (ServerHelper.isServerWorld(worldObj)) {
			player.addChatMessage(StringHelper.localize("message.cofh.secure1") + " " + owner + "! " + StringHelper.localize("message.cofh.secure2"));
		}
		return true;
	}

	/* NETWORK METHODS */
	@Override
	public Payload getDescriptionPayload() {

		Payload payload = super.getDescriptionPayload();

		payload.addByte(type);
		payload.addByte((byte) access.ordinal());
		payload.addByte(facing);
		payload.addString(owner);
		return payload;
	}

	/* ITilePacketHandler */
	@Override
	public void handleTilePacket(Payload payload) {

		super.handleTilePacket(payload);

		type = payload.getByte();
		access = ISecureTile.AccessMode.values()[payload.getByte()];
		if (ServerHelper.isClientWorld(worldObj)) {
			facing = payload.getByte();
			owner = payload.getString();
			if (inventory.length <= 0) {
				inventory = new ItemStack[INV_SIZE[type]];
			}
		} else {
			payload.getByte();
			payload.getString();
		}
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
	}

	/* GUI METHODS */
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
		facing = nbt.getByte("Facing");
		access = AccessMode.values()[nbt.getByte("Access")];
		owner = nbt.getString("Owner");
		inventory = new ItemStack[INV_SIZE[type]];

		if (!enableSecurity) {
			access = AccessMode.PUBLIC;
		}
		super.readFromNBT(nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setByte("Type", type);
		nbt.setByte("Facing", facing);
		nbt.setByte("Access", (byte) access.ordinal());
		nbt.setString("Owner", owner);
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

	/* ISidedInventory */
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {

		return access.isPublic() ? SLOTS[type] : TEProps.EMPTY_INVENTORY;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int side) {

		return access.isPublic();
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int side) {

		return access.isPublic();
	}

	/* ISecureTile */
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

}
