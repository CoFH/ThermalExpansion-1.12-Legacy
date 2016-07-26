package cofh.thermalexpansion.block.workbench;

import cofh.api.core.ICustomInventory;
import cofh.api.inventory.IInventoryRetainer;
import cofh.core.network.PacketCoFHBase;
import cofh.core.network.PacketHandler;
import cofh.core.network.PacketTileInfo;
import cofh.core.util.oredict.OreDictionaryArbiter;
import cofh.lib.util.helpers.InventoryHelper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.TileInventory;
import cofh.thermalexpansion.gui.client.GuiWorkbench;
import cofh.thermalexpansion.gui.container.ContainerWorkbench;
import cofh.thermalexpansion.util.helpers.SchematicHelper;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class TileWorkbench extends TileInventory implements ICustomInventory, ISidedInventory, IInventoryRetainer {

	public static void initialize() {

		GameRegistry.registerTileEntity(TileWorkbench.class, "thermalexpansion.NewWorkbench");
		configure();
	}

	public static void configure() {

		String comment = "Enable this to allow for Workbenches to be securable.";
		enableSecurity = ThermalExpansion.config.get("Security", "Workbench.All.Securable", enableSecurity, comment);
	}

	public static int[] INVENTORY = { 36, 18, 27, 33, 36 };
	public static int[] SCHEMATICS = { 12, 3, 6, 9, 12 };

	private static int[][] SLOTS;
	static {
		SLOTS = new int[INVENTORY.length][];
		for (int i = 0; i < SLOTS.length; ++i) {
			int[] data = new int[INVENTORY[i]];
			for (int j = 0, k = SCHEMATICS[i]; j < data.length; ++j) {
				data[j] = k++;
			}
			SLOTS[i] = data;
		}
	}

	public static enum PacketInfoID {
		CLEAR_GRID, SET_GRID, NEI_SUP
	}

	public static boolean enableSecurity = true;

	public byte type = 1;

	public int selectedSchematic = 0;
	public boolean[] missingItem = { false, false, false, false, false, false, false, false, false };
	public ItemStack[] craftingGrid = new ItemStack[9];

	// This is really client only
	public boolean updateClient = false;

	public TileWorkbench() {

		inventory = new ItemStack[SCHEMATICS[1] + INVENTORY[1]];
	}

	public TileWorkbench(int metadata) {

		type = (byte) metadata;
		inventory = new ItemStack[SCHEMATICS[metadata] + INVENTORY[metadata]];
	}

	@Override
	public String getName() {

		return "tile.thermalexpansion.workbench." + BlockWorkbench.NAMES[getType()] + ".name";
	}

	@Override
	public int getType() {

		return type;
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
												invCopy, containerStack, SCHEMATICS[type]))) {
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

							if (containerStack == null || containerStack.isItemStackDamageable() && containerStack.getItemDamage() > containerStack.getMaxDamage()) {
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

		updateClient = true;
		for (int i = 0; i < 9; i++) {
			craftingGrid[i] = null;
		}
		PacketHandler.sendToServer(PacketTileInfo.newPacket(this).addByte(PacketInfoID.CLEAR_GRID.ordinal()));
	}

	public void setCraftingGrid() {

		updateClient = true;
		for (int i = 0; i < 9; i++) {
			craftingGrid[i] = SchematicHelper.getSchematicSlot(getStackInSlot(getCurrentSchematicSlot()), i);
		}
		PacketHandler.sendToServer(PacketTileInfo.newPacket(this).addByte(PacketInfoID.SET_GRID.ordinal()));
	}

	public void setCurrentSchematicSlot(int slotIndex) {

		selectedSchematic = slotIndex;
	}

	/* NETWORK METHODS */
	@Override
	public PacketCoFHBase getPacket() {

		PacketCoFHBase payload = super.getPacket();

		payload.addByte(type);
		payload.addByte(selectedSchematic);

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
		selectedSchematic = payload.getByte();
	}

	/* ITileInfoPacketHandler */
	@Override
	public void handleTileInfoPacket(PacketCoFHBase payload, boolean isServer, EntityPlayer thePlayer) {

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
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiWorkbench(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerWorkbench(inventory, this);
	}

	@Override
	public void sendGuiNetworkData(Container container, ICrafting player) {

		player.sendProgressBarUpdate(container, 0, canPlayerAccess(((EntityPlayer) player)) ? 1 : 0);
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		type = nbt.getByte("Type");
		selectedSchematic = nbt.getByte("Mode");
		readCraftingFromNBT(nbt);

		inventory = new ItemStack[SCHEMATICS[type] + INVENTORY[type]];

		super.readFromNBT(nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setByte("Type", type);
		nbt.setByte("Mode", (byte) selectedSchematic);
		writeCraftingToNBT(nbt);
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

	/* IInventory */
	@Override
	public ItemStack decrStackSize(int slot, int amount) {

		updateClient = true;
		return super.decrStackSize(slot, amount);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {

		updateClient = true;
		super.setInventorySlotContents(slot, stack);
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

		return SLOTS[type];
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int side) {

		return slot >= SCHEMATICS[type];
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int side) {

		return slot >= SCHEMATICS[type];
	}

}
