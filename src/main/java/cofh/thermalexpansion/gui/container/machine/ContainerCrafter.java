package cofh.thermalexpansion.gui.container.machine;

import cofh.core.util.CoreUtils;
import cofh.lib.gui.slot.*;
import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalexpansion.block.machine.TileCrafter;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import cofh.thermalexpansion.gui.container.ISchematicContainer;
import cofh.thermalexpansion.network.PacketTEBase;
import cofh.thermalfoundation.init.TFItems;
import cofh.thermalfoundation.item.ItemDiagram;
import cofh.thermalfoundation.util.helpers.SchematicHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class ContainerCrafter extends ContainerTEBase implements ISchematicContainer {

	protected TileCrafter myTile;
	protected InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
	protected IInventory craftResult = new InventoryCraftResult();

	private Slot craftSlots[] = new Slot[9];
	private Slot resultSlot;

	public ContainerCrafter(InventoryPlayer inventory, TileEntity tile) {

		super(inventory, tile);

		myTile = (TileCrafter) tile;
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(myTile, 3 + j + i * 9, 8 + j * 18, 74 + i * 18));
			}
		}
		addSlotToContainer(new SlotSpecificItem(myTile, 0, 56, 34, ItemDiagram.schematic).setSlotStackLimit(1));
		addSlotToContainer(new SlotRemoveOnly(myTile, 1, 116, 35));
		addSlotToContainer(new SlotEnergy(myTile, myTile.getChargeSlot(), 8, 53));

		/* Crafting Grid */
		for (int i = 0; i < 9; i++) {
			craftSlots[i] = addSlotToContainer(new SlotFalseCopy(craftMatrix, i, 0, 0));
		}
		resultSlot = addSlotToContainer(new SlotCraftingLocked(inventory.player, craftMatrix, craftResult, 0, 0, 0));
	}

	@Override
	protected int getPlayerInventoryVerticalOffset() {

		return 123;
	}

	@Override
	public ItemStack slotClick(int slot, int mouseButton, ClickType modifier, EntityPlayer player) {

		if (slot == resultSlot.slotNumber && resultSlot.getHasStack() && CoreUtils.isClient()) {
			if (SchematicHelper.isSchematic(myTile.getStackInSlot(0))) {
				PacketTEBase.sendTabSchematicPacketToServer();
				writeSchematic();
			}
		}
		return super.slotClick(slot, mouseButton, modifier, player);
	}

	@Override
	public void onCraftMatrixChanged(IInventory inventory) {

		// InventoryCrafting craftMatrixTemp = new InventoryCrafting(new ContainerFalse(), 3, 3);
		this.craftResult.setInventorySlotContents(0, ItemHelper.findMatchingRecipe(this.craftMatrix, myTile.getWorld()));
	}

	/* ISetSchematic */
	@Override
	public void writeSchematic() {

		ItemStack schematic = myTile.getStackInSlot(0);

		if (!schematic.isEmpty() && resultSlot.getHasStack()) {
			ItemStack newSchematic = SchematicHelper.writeNBTToSchematic(schematic, SchematicHelper.getNBTForSchematic(craftMatrix, myTile.getWorld(), craftResult.getStackInSlot(0)));
			newSchematic.setCount(schematic.getCount());
			myTile.setInventorySlotContents(0, newSchematic);
			for (int i = 0; i < 9; i++) {
				craftSlots[i].putStack(ItemStack.EMPTY);
			}
			resultSlot.putStack(ItemStack.EMPTY);
		}
	}

	@Override
	public boolean canWriteSchematic() {

		return SchematicHelper.isSchematic(myTile.getStackInSlot(0)) && !craftResult.getStackInSlot(0).isEmpty();
	}

	@Override
	public Slot[] getCraftingSlots() {

		return craftSlots;
	}

	@Override
	public Slot getResultSlot() {

		return resultSlot;
	}

}
