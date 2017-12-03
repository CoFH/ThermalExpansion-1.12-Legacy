package cofh.thermalexpansion.gui.container.machine;

import cofh.core.gui.slot.SlotEnergy;
import cofh.core.gui.slot.SlotFalseCopy;
import cofh.core.gui.slot.SlotRemoveOnly;
import cofh.core.util.helpers.ServerHelper;
import cofh.thermalexpansion.block.machine.TileCrafter;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ContainerCrafter extends ContainerTEBase {

	TileCrafter myTile;
	InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
	InventoryCraftResult craftResult = new InventoryCraftResult();
	EntityPlayer player;

	public ContainerCrafter(InventoryPlayer inventory, TileEntity tile) {

		super(inventory, tile);

		myTile = (TileCrafter) tile;
		player = inventory.player;

		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(myTile, j + i * 9, 8 + j * 18, 77 + i * 18));
			}
		}
		addSlotToContainer(new SlotRemoveOnly(myTile, TileCrafter.SLOT_OUTPUT, 125, 21));
		addSlotToContainer(new SlotEnergy(myTile, myTile.getChargeSlot(), 8, 53));

		/* Crafting Grid */
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				addSlotToContainer(new SlotFalseCopy(craftMatrix, j + i * 3, 35 + j * 18, 17 + i * 18));
				//addSlotToContainer(new SlotFalseCopy(myTile, TileCrafter.SLOT_CRAFTING_START + j + i * 3, 35 + j * 18, 17 + i * 18));
			}
		}
		addSlotToContainer(new SlotCrafting(player, craftMatrix, craftResult, 0, 98, 53) {

			@Override
			public boolean canTakeStack(EntityPlayer player) {

				return false;
			}
		});
	}

	@Override
	protected int getPlayerInventoryVerticalOffset() {

		return 126;
	}

	@Override
	public void onCraftMatrixChanged(IInventory inventoryIn) {

		super.onCraftMatrixChanged(inventoryIn);
		slotChangedCraftingGrid();
	}

	public void slotChangedCraftingGrid() {

		World world = myTile.getWorld();

		if (ServerHelper.isServerWorld(world)) {
			EntityPlayerMP playerMP = (EntityPlayerMP) player;
			ItemStack stack = ItemStack.EMPTY;
			IRecipe recipe = CraftingManager.findMatchingRecipe(craftMatrix, world);

			if (recipe != null && (recipe.isDynamic() || !world.getGameRules().getBoolean("doLimitedCrafting") || playerMP.getRecipeBook().isUnlocked(recipe))) {
				craftResult.setRecipeUsed(recipe);
				stack = recipe.getCraftingResult(craftMatrix);
			}
			craftResult.setInventorySlotContents(0, stack);
			playerMP.connection.sendPacket(new SPacketSetSlot(this.windowId, inventorySlots.size() - 1, stack));
		}
	}

}
