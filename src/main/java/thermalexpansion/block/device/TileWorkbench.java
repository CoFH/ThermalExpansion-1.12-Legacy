package thermalexpansion.block.device;

import cofh.api.tileentity.ISecureTile;
import cofh.api.tileentity.ISidedBlockTexture;
import cofh.core.CoFHProps;
import cofh.network.CoFHPacket;
import cofh.network.CoFHTileInfoPacket;
import cofh.network.ITileInfoPacketHandler;
import cofh.network.ITilePacketHandler;
import cofh.network.PacketHandler;
import cofh.render.IconRegistry;
import cofh.util.InventoryHelper;
import cofh.util.ItemHelper;
import cofh.util.ServerHelper;
import cofh.util.StringHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraftforge.oredict.OreDictionary;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.block.TileInventory;
import thermalexpansion.core.TEProps;
import thermalexpansion.item.SchematicHelper;

public class TileWorkbench extends TileInventory implements ISecureTile, ISidedInventory, ITilePacketHandler, ITileInfoPacketHandler, ISidedBlockTexture {

	public static void initialize() {

		GameRegistry.registerTileEntity(TileWorkbench.class, "thermalexpansion.Workbench");
		guiId = ThermalExpansion.proxy.registerGui("Workbench", "device", true);
		configure();
	}

	protected static int guiId;

	public static final int[] SLOTS = { 0, 1, 2, 3, 4, 5, 6, 7, 8 };

	public static enum PacketInfoID {
		CLEAR_GRID, SET_GRID, NEI_SUP
	}

	public static boolean enableSecurity = true;

	public static void configure() {

		String comment = "Enable this to allow for Machinist Workbenches to be secure inventories. (Default: true)";
		enableSecurity = ThermalExpansion.config.get("block.security", "Workbench.Secure", enableSecurity, comment);
	}

	String owner = CoFHProps.DEFAULT_OWNER;
	private AccessMode access = AccessMode.PUBLIC;

	public int selectedSchematic = 0;

	public boolean[] missingItem = { false, false, false, false, false, false, false, false, false };

	/* Client-Side Only */
	public boolean canAccess = true;

	public TileWorkbench() {

		inventory = new ItemStack[30];
	}

	@Override
	public boolean canUpdate() {

		return false;
	}

	@Override
	public String getName() {

		return "tile.thermalexpansion.device." + BlockDevice.NAMES[getType()] + ".name";
	}

	@Override
	public int getType() {

		return BlockDevice.Types.WORKBENCH.ordinal();
	}

	@Override
	public boolean openGui(EntityPlayer player) {

		if (canPlayerAccess(player.getDisplayName())) {
			player.openGui(ThermalExpansion.instance, guiId, worldObj, xCoord, yCoord, zCoord);
			return true;
		}
		if (ServerHelper.isServerWorld(worldObj)) {
			player.addChatMessage(new ChatComponentText(StringHelper.localize("message.cofh.secure1") + " " + owner + "! "
					+ StringHelper.localize("message.cofh.secure2")));
		}
		return true;
	}

	public boolean createItem(boolean doCreate, ItemStack output) {

		ItemStack[] invCopy = InventoryHelper.cloneInventory(inventory);
		ItemStack recipeSlot;
		String recipeOre;
		boolean found = false;

		for (int i = 0; i < 9; i++) {
			recipeSlot = getStackInSlot(i + getMatrixOffset());
			recipeOre = OreDictionary.getOreName(OreDictionary.getOreID(recipeSlot));

			if (recipeSlot != null) {
				for (int j = 0; j < getSizeInventory(); j++) {
					if (invCopy[j] != null && ItemHelper.craftingEquivalent(invCopy[j], recipeSlot, recipeOre, output)) {
						inventory[i + getMatrixOffset()] = ItemHelper.cloneStack(invCopy[j], 1);
						invCopy[j].stackSize--;

						if (invCopy[j].getItem().hasContainerItem(invCopy[j])) {
							ItemStack containerStack = invCopy[j].getItem().getContainerItem(invCopy[j]);

							if (containerStack == null) {
								// this is absolutely stupid and nobody should ever make a container item where this gets called
							} else {
								if (containerStack.isItemStackDamageable() && containerStack.getItemDamage() > containerStack.getMaxDamage()) {
									containerStack = null;
								}
								if (containerStack != null
										&& (!invCopy[j].getItem().doesContainerItemLeaveCraftingGrid(invCopy[j]) || !InventoryHelper.addItemStackToInventory(
												invCopy, containerStack, 2))) {

									if (invCopy[j].stackSize <= 0) {
										invCopy[j] = containerStack;
										if (containerStack.stackSize <= 0) {
											invCopy[j].stackSize = 1;
										}
									} else {
										return false;
									}
								}
							}
						}
						if (invCopy[j].stackSize <= 0) {
							invCopy[j] = null;
						}
						found = true;
						break;
					}
				}
				if (!found) {
					return false;
				}
				found = false;
			}
		}
		if (doCreate) {
			// Update the inventories since we can make it.
			inventory = invCopy;
		}
		return true;
	}

	public boolean createItemClient(boolean doCreate, ItemStack output) {

		ItemStack[] invCopy = InventoryHelper.cloneInventory(inventory);
		ItemStack recipeSlot;
		String recipeOre;
		boolean found = false;
		boolean masterFound = true;
		missingItem = new boolean[] { false, false, false, false, false, false, false, false, false };

		for (int i = 0; i < 9; i++) {
			recipeSlot = getStackInSlot(i + getMatrixOffset());
			recipeOre = OreDictionary.getOreName(OreDictionary.getOreID(recipeSlot));

			if (recipeSlot != null) {
				for (int j = 0; j < getSizeInventory(); j++) {
					if (invCopy[j] != null && ItemHelper.craftingEquivalent(invCopy[j], recipeSlot, recipeOre, output)) {
						inventory[i + getMatrixOffset()] = ItemHelper.cloneStack(invCopy[j], 1);
						invCopy[j].stackSize--;

						if (invCopy[j].getItem().hasContainerItem(invCopy[j])) {
							ItemStack containerStack = invCopy[j].getItem().getContainerItem(invCopy[j]);

							if (containerStack.isItemStackDamageable() && containerStack.getItemDamage() > containerStack.getMaxDamage()) {
								containerStack = null;
							}
							if (containerStack != null
									&& (!invCopy[j].getItem().doesContainerItemLeaveCraftingGrid(invCopy[j]) || !InventoryHelper.addItemStackToInventory(
											invCopy, containerStack, 2))) {

								if (invCopy[j].stackSize <= 0) {
									invCopy[j] = containerStack;
									if (containerStack.stackSize <= 0) {
										invCopy[j].stackSize = 1;
									}
								} else {
									return false;
								}
							}
						}
						if (invCopy[j].stackSize <= 0) {
							invCopy[j] = null;
						}
						found = true;
						break;
					}
				}
				if (!found) {
					masterFound = false;
					missingItem[i] = true;
				}
				found = false;
			}
		}
		if (!masterFound) {
			return false;
		}
		if (doCreate) {
			// Update the inventories since we can make it.
			inventory = invCopy;
		}
		return true;
	}

	public int getCurrentSchematicSlot() {

		return 18 + selectedSchematic;
	}

	public int getMatrixOffset() {

		return 21;
	}

	public void setCurrentSchematicSlot(int slotIndex) {

		selectedSchematic = slotIndex - 18;
	}

	public void clearCraftingGrid() {

		for (int i = 0; i < 9; i++) {
			inventory[getMatrixOffset() + i] = null;
		}
		PacketHandler.sendToServer(CoFHTileInfoPacket.newPacket(this).addByte(PacketInfoID.CLEAR_GRID.ordinal()));
	}

	public void setCraftingGrid() {

		for (int i = 0; i < 9; i++) {
			inventory[getMatrixOffset() + i] = SchematicHelper.getSchematicSlot(getStackInSlot(getCurrentSchematicSlot()), i);
		}
		PacketHandler.sendToServer(CoFHTileInfoPacket.newPacket(this).addByte(PacketInfoID.SET_GRID.ordinal()));
	}

	/* NETWORK METHODS */
	@Override
	public CoFHPacket getPacket() {

		CoFHPacket payload = super.getPacket();

		payload.addByte((byte) access.ordinal());
		payload.addByte(selectedSchematic);
		payload.addString(owner);
		return payload;
	}

	/* ITilePacketHandler */
	@Override
	public void handleTilePacket(CoFHPacket payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		access = ISecureTile.AccessMode.values()[payload.getByte()];
		selectedSchematic = payload.getByte();

		if (ServerHelper.isClientWorld(worldObj)) {
			owner = payload.getString();
		} else {
			payload.getString();
		}
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
	}

	/* ITileInfoPacketHandler */
	@Override
	public void handleTileInfoPacket(CoFHPacket payload, boolean isServer, EntityPlayer thePlayer) {

		int type = payload.getByte();

		if (type == PacketInfoID.CLEAR_GRID.ordinal()) {
			for (int i = 0; i < 9; i++) {
				inventory[getMatrixOffset() + i] = null;
				if (thePlayer.openContainer != null) {
					thePlayer.openContainer.onCraftMatrixChanged(null);
				}
			}
		} else if (type == PacketInfoID.SET_GRID.ordinal()) {
			for (int i = 0; i < 9; i++) {
				inventory[getMatrixOffset() + i] = SchematicHelper.getSchematicSlot(getStackInSlot(getCurrentSchematicSlot()), i);
				if (thePlayer.openContainer != null) {
					thePlayer.openContainer.onCraftMatrixChanged(null);
				}
			}
		} else if (type == PacketInfoID.NEI_SUP.ordinal()) {
			int slot;
			for (int i = 0; i < 9; i++) {
				inventory[getMatrixOffset() + i] = null;
			}
			while ((slot = payload.getByte()) >= 0) {
				inventory[slot + getMatrixOffset()] = payload.getItemStack();
			}
			Container container = thePlayer.openContainer;
			if (container != null) {
				((ICrafting) thePlayer).sendContainerAndContentsToPlayer(container, container.getInventory());
				container.onCraftMatrixChanged(null);
			}
		}
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

		super.readFromNBT(nbt);

		access = AccessMode.values()[nbt.getByte("Access")];
		owner = nbt.getString("Owner");
		selectedSchematic = nbt.getByte("Mode");

		if (!enableSecurity) {
			access = AccessMode.PUBLIC;
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setByte("Access", (byte) access.ordinal());
		nbt.setString("Owner", owner);
		nbt.setByte("Mode", (byte) selectedSchematic);
	}

	/* IInventory */
	@Override
	public int getSizeInventory() {

		return inventory.length - 9;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {

		inventory[slot] = stack;

		if (stack != null && stack.stackSize > getInventoryStackLimit()) {
			stack.stackSize = getInventoryStackLimit();
		}
	}

	/* ISidedInventory */
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {

		return TEProps.EMPTY_INVENTORY;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int side) {

		return false;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int side) {

		return false;
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

	/* ISidedBlockTexture */
	@Override
	public IIcon getBlockTexture(int side, int pass) {

		if (side == 0) {
			return IconRegistry.getIcon("WorkbenchBottom");
		} else if (side == 1) {
			return IconRegistry.getIcon("WorkbenchTop");
		}
		return IconRegistry.getIcon("WorkbenchSide");
	}

}
