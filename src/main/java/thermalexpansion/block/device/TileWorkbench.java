package thermalexpansion.block.device;

import cofh.api.core.ICustomInventory;
import cofh.api.core.ISecurable;
import cofh.api.tileentity.ISidedTexture;
import cofh.network.CoFHPacket;
import cofh.network.CoFHTileInfoPacket;
import cofh.network.PacketHandler;
import cofh.render.IconRegistry;
import cofh.util.InventoryHelper;
import cofh.util.ItemHelper;
import cofh.util.ServerHelper;
import cofh.util.StringHelper;
import cofh.util.oredict.OreDictionaryArbiter;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.block.TileInventory;
import thermalexpansion.core.TEProps;
import thermalexpansion.gui.GuiHandler;
import thermalexpansion.gui.client.device.GuiWorkbench;
import thermalexpansion.gui.container.device.ContainerWorkbench;
import thermalexpansion.item.SchematicHelper;

public class TileWorkbench extends TileInventory implements ICustomInventory, ISidedInventory, ISidedTexture {

	public static void initialize() {

		GameRegistry.registerTileEntity(TileWorkbench.class, "thermalexpansion.Workbench");
		configure();
	}

	public static void configure() {

		String comment = "Enable this to allow for Machinist Workbenches to be securable. (Default: true)";
		enableSecurity = ThermalExpansion.config.get("block.security", "Workbench.Secure", enableSecurity, comment);
	}

	public static boolean enableSecurity = true;

	public static enum PacketInfoID {
		CLEAR_GRID, SET_GRID, NEI_SUP
	}

	public int selectedSchematic = 0;
	public boolean[] missingItem = { false, false, false, false, false, false, false, false, false };
	public ItemStack[] craftingGrid = new ItemStack[9];

	public TileWorkbench() {

		inventory = new ItemStack[21];
	}

	@Override
	public String getName() {

		return "tile.thermalexpansion.device." + BlockDevice.NAMES[getType()] + ".name";
	}

	@Override
	public int getType() {

		return BlockDevice.Types.WORKBENCH.ordinal();
	}

	public int getCurrentSchematicSlot() {

		return selectedSchematic;
	}

	@Override
	public boolean canUpdate() {

		return false;
	}

	public boolean createItem(boolean doCreate, ItemStack output) {

		ItemStack[] invCopy = InventoryHelper.cloneInventory(inventory);
		ItemStack recipeSlot;
		String recipeOreName;
		boolean found = false;

		for (int i = 0; i < 9; i++) {
			recipeSlot = craftingGrid[i];
			recipeOreName = OreDictionaryArbiter.getOreName(recipeSlot);

			if (recipeSlot != null) {
				for (int j = 0; j < getSizeInventory(); j++) {
					if (invCopy[j] != null && ItemHelper.craftingEquivalent(invCopy[j], recipeSlot, recipeOreName, output)) {
						craftingGrid[i] = ItemHelper.cloneStack(invCopy[j], 1);
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
		String recipeOreName;
		boolean found = false;
		boolean masterFound = true;
		missingItem = new boolean[] { false, false, false, false, false, false, false, false, false };

		for (int i = 0; i < 9; i++) {
			recipeSlot = craftingGrid[i];
			recipeOreName = OreDictionaryArbiter.getOreName(recipeSlot);

			if (recipeSlot != null) {
				for (int j = 0; j < getSizeInventory(); j++) {
					if (invCopy[j] != null && ItemHelper.craftingEquivalent(invCopy[j], recipeSlot, recipeOreName, output)) {
						craftingGrid[i] = ItemHelper.cloneStack(invCopy[j], 1);
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

	@Override
	public boolean enableSecurity() {

		return enableSecurity;
	}

	public void clearCraftingGrid() {

		for (int i = 0; i < 9; i++) {
			craftingGrid[i] = null;
		}
		PacketHandler.sendToServer(CoFHTileInfoPacket.newPacket(this).addByte(PacketInfoID.CLEAR_GRID.ordinal()));
	}

	public void setCraftingGrid() {

		for (int i = 0; i < 9; i++) {
			craftingGrid[i] = SchematicHelper.getSchematicSlot(getStackInSlot(getCurrentSchematicSlot()), i);
		}
		PacketHandler.sendToServer(CoFHTileInfoPacket.newPacket(this).addByte(PacketInfoID.SET_GRID.ordinal()));
	}

	public void setCurrentSchematicSlot(int slotIndex) {

		selectedSchematic = slotIndex;
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

		access = ISecurable.AccessMode.values()[payload.getByte()];
		selectedSchematic = payload.getByte();

		if (!isServer) {
			owner = payload.getString();
		} else {
			payload.getString();
		}
	}

	/* ITileInfoPacketHandler */
	@Override
	public void handleTileInfoPacket(CoFHPacket payload, boolean isServer, EntityPlayer thePlayer) {

		int type = payload.getByte();

		if (type == PacketInfoID.CLEAR_GRID.ordinal()) {
			for (int i = 0; i < 9; i++) {
				craftingGrid[i] = null;
				if (thePlayer.openContainer != null) {
					thePlayer.openContainer.onCraftMatrixChanged(null);
				}
			}
		} else if (type == PacketInfoID.SET_GRID.ordinal()) {
			for (int i = 0; i < 9; i++) {
				craftingGrid[i] = SchematicHelper.getSchematicSlot(getStackInSlot(getCurrentSchematicSlot()), i);
				if (thePlayer.openContainer != null) {
					thePlayer.openContainer.onCraftMatrixChanged(null);
				}
			}
		} else if (type == PacketInfoID.NEI_SUP.ordinal()) {
			int slot;
			for (int i = 0; i < 9; i++) {
				craftingGrid[i] = null;
			}
			while ((slot = payload.getByte()) >= 0) {
				craftingGrid[slot] = payload.getItemStack();
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
	public GuiContainer getGuiClient(InventoryPlayer inventory) {

		return new GuiWorkbench(inventory, this);
	}

	@Override
	public Container getGuiServer(InventoryPlayer inventory) {

		return new ContainerWorkbench(inventory, this);
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

		super.readFromNBT(nbt);

		readCraftingFromNBT(nbt);

		selectedSchematic = nbt.getByte("Mode");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		writeCraftingToNBT(nbt);

		nbt.setByte("Mode", (byte) selectedSchematic);
	}

	public void readCraftingFromNBT(NBTTagCompound nbt) {

		NBTTagList list = nbt.getTagList("Crafting", 10);
		craftingGrid = new ItemStack[9];
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound tag = list.getCompoundTagAt(i);
			int slot = tag.getInteger("Slot");

			if (slot >= 0 && slot < craftingGrid.length) {
				craftingGrid[slot] = ItemStack.loadItemStackFromNBT(tag);
			}
		}
	}

	public void writeCraftingToNBT(NBTTagCompound nbt) {

		NBTTagList list = new NBTTagList();
		for (int i = 0; i < craftingGrid.length; i++) {
			if (craftingGrid[i] != null) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setInteger("Slot", i);
				craftingGrid[i].writeToNBT(tag);
				list.appendTag(tag);
			}
		}
		nbt.setTag("Crafting", list);
	}

	/* ICustomInventory */
	@Override
	public ItemStack[] getInventorySlots(int inventoryIndex) {

		return craftingGrid;
	}

	@Override
	public int getSlotStackLimit(int slotIndex) {

		return 1;
	}

	@Override
	public void onSlotUpdate() {

		markDirty();
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

	/* ISidedTexture */
	@Override
	public IIcon getTexture(int side, int pass) {

		if (side == 0) {
			return IconRegistry.getIcon("WorkbenchBottom");
		} else if (side == 1) {
			return IconRegistry.getIcon("WorkbenchTop");
		}
		return IconRegistry.getIcon("WorkbenchSide");
	}

}
