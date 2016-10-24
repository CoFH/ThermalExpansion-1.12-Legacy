package cofh.thermalexpansion.gui.slot;

import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.block.workbench.TileWorkbench;
import cofh.thermalexpansion.gui.container.ContainerWorkbench;
import net.minecraftforge.fml.common.FMLCommonHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.stats.AchievementList;

public class SlotCraftingOutputWorkbench extends Slot {

	EntityPlayer myPlayer;
	TileWorkbench myTile;
	ContainerWorkbench myContainer;

	int amountCrafted;

	public SlotCraftingOutputWorkbench(TileWorkbench tile, ContainerWorkbench container, EntityPlayer player, IInventory inventory, int slotIndex, int x, int y) {

		super(inventory, slotIndex, x, y);
		myPlayer = player;
		myTile = tile;
		myContainer = container;
	}

	@Override
	public boolean canTakeStack(EntityPlayer player) {

		return ServerHelper.isClientWorld(player.worldObj) ? myTile.createItemClient(false, inventory.getStackInSlot(getSlotIndex())) : myTile.createItem(
				false, inventory.getStackInSlot(getSlotIndex()));
	}

	@Override
	public void onSlotChanged() {

		myContainer.onCraftMatrixChanged(null);
	}

	@Override
	public boolean isItemValid(ItemStack stack) {

		return false;
	}

	@Override
	public void onPickupFromSlot(EntityPlayer player, ItemStack stack) {

		myTile.createItem(true, inventory.getStackInSlot(getSlotIndex()));
		FMLCommonHandler.instance().firePlayerCraftingEvent(player, stack, myContainer.craftMatrix);
		this.onCrafting(stack);
		super.onPickupFromSlot(player, stack);
	}

	@Override
	protected void onCrafting(ItemStack stack, int count) {

		this.amountCrafted += count;
		this.onCrafting(stack);
	}

	@Override
	public ItemStack getStack() {

		myTile.createItem(false, inventory.getStackInSlot(getSlotIndex()));
		//myContainer.onCraftMatrixChanged(null);
		return this.inventory.getStackInSlot(getSlotIndex());
	}

	public ItemStack getStackNoUpdate() {

		return this.inventory.getStackInSlot(getSlotIndex());
	}

	@Override
	protected void onCrafting(ItemStack stack) {

		stack.onCrafting(this.myPlayer.worldObj, this.myPlayer, this.amountCrafted);
		this.amountCrafted = 0;

		if (stack.getItem() == Item.getItemFromBlock(Blocks.CRAFTING_TABLE)) {
			myPlayer.addStat(AchievementList.BUILD_WORK_BENCH, 1);
		} else if (stack.getItem() instanceof ItemPickaxe) {
			myPlayer.addStat(AchievementList.BUILD_PICKAXE, 1);
		} else if (stack.getItem() == Item.getItemFromBlock(Blocks.FURNACE)) {
			myPlayer.addStat(AchievementList.BUILD_FURNACE, 1);
		} else if (stack.getItem() instanceof ItemHoe) {
			myPlayer.addStat(AchievementList.BUILD_HOE, 1);
		} else if (stack.getItem() == Items.BREAD) {
			myPlayer.addStat(AchievementList.MAKE_BREAD, 1);
		} else if (stack.getItem() == Items.CAKE) {
			myPlayer.addStat(AchievementList.BAKE_CAKE, 1);
		} else if (stack.getItem() instanceof ItemPickaxe && ((ItemPickaxe) stack.getItem()).getToolMaterial() != Item.ToolMaterial.WOOD) {
			myPlayer.addStat(AchievementList.BUILD_BETTER_PICKAXE, 1);
		} else if (stack.getItem() instanceof ItemSword) {
			myPlayer.addStat(AchievementList.BUILD_SWORD, 1);
		} else if (stack.getItem() == Item.getItemFromBlock(Blocks.ENCHANTING_TABLE)) {
			myPlayer.addStat(AchievementList.ENCHANTMENTS, 1);
		} else if (stack.getItem() == Item.getItemFromBlock(Blocks.BOOKSHELF)) {
			myPlayer.addStat(AchievementList.BOOKCASE, 1);
		}
	}

}
