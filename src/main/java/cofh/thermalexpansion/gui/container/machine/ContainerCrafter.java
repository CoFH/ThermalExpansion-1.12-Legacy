package cofh.thermalexpansion.gui.container.machine;

import cofh.core.gui.container.ContainerTileAugmentable;
import cofh.core.gui.slot.SlotEnergy;
import cofh.core.gui.slot.SlotFalseCopy;
import cofh.core.gui.slot.SlotRemoveOnly;
import cofh.core.util.helpers.ServerHelper;
import cofh.thermalexpansion.block.machine.TileCrafter;
import cofh.thermalexpansion.block.machine.TileCrafter.CrafterRecipe;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ContainerCrafter extends ContainerTileAugmentable {

	TileCrafter myTile;
	public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
	public InventoryCraftResult craftResult = new InventoryCraftResult();
	EntityPlayer player;

	boolean initialized = false;

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
			}
		}
		addSlotToContainer(new SlotCrafting(player, craftMatrix, craftResult, 0, 125, 48) {

			@Override
			public boolean canTakeStack(EntityPlayer player) {

				return false;
			}
		});

		if (ServerHelper.isServerWorld(myTile.getWorld())) {
			for (int i = 0; i < 9; i++) {
				craftMatrix.setInventorySlotContents(i, myTile.inventory[TileCrafter.SLOT_CRAFTING_START + i]);
			}
			ItemStack stack = ItemStack.EMPTY;
			IRecipe recipe = CraftingManager.findMatchingRecipe(craftMatrix, myTile.getWorld());

			if (recipe != null) {
				craftResult.setRecipeUsed(recipe);
				stack = recipe.getCraftingResult(craftMatrix);
			}
			craftResult.setInventorySlotContents(0, stack);
		}
		initialized = true;
	}

	@Override
	protected int getPlayerInventoryVerticalOffset() {

		return 126;
	}

	@Override
	public void onContainerClosed(EntityPlayer playerIn) {

		super.onContainerClosed(playerIn);
		myTile.clearRecipeChanges();
	}

	@Override
	public void onCraftMatrixChanged(IInventory inventoryIn) {

		super.onCraftMatrixChanged(inventoryIn);
		slotChangedCraftingGrid();
	}

	@Override
	@SideOnly (Side.CLIENT)
	public void setAll(List<ItemStack> stacks) {

		for (int i = 0; i < stacks.size(); ++i) {
			putStackInSlot(i, stacks.get(i));
		}
		calcCraftingGridClient();
	}

	public void slotChangedCraftingGrid() {

		if (!initialized) {
			return;
		}
		World world = myTile.getWorld();

		if (ServerHelper.isServerWorld(world)) {
			EntityPlayerMP playerMP = (EntityPlayerMP) player;
			ItemStack stack = ItemStack.EMPTY;
			IRecipe recipe = CraftingManager.findMatchingRecipe(craftMatrix, world);

			if (recipe != null && CrafterRecipe.validRecipe(recipe)) {
				craftResult.setRecipeUsed(recipe);
				stack = recipe.getCraftingResult(craftMatrix);
			}
			myTile.markRecipeChanges();
			craftResult.setInventorySlotContents(0, stack);
			playerMP.connection.sendPacket(new SPacketSetSlot(this.windowId, inventorySlots.size() - 1, stack));
		} else {
			calcCraftingGridClient();
		}
	}

	public void setRecipe() {

		for (int i = 0; i < craftMatrix.getSizeInventory(); i++) {
			myTile.setInventorySlotContents(TileCrafter.SLOT_CRAFTING_START + i, craftMatrix.getStackInSlot(i));
		}
		myTile.sendModePacket();
	}

	public void calcCraftingGridClient() {

		ItemStack stack = ItemStack.EMPTY;
		IRecipe recipe = CraftingManager.findMatchingRecipe(craftMatrix, myTile.getWorld());

		if (recipe != null && CrafterRecipe.validRecipe(recipe)) {
			craftResult.setRecipeUsed(recipe);
			stack = recipe.getCraftingResult(craftMatrix);
		}
		craftResult.setInventorySlotContents(0, stack);
	}

}
