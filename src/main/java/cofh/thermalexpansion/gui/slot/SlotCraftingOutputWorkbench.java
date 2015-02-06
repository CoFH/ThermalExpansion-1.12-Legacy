package cofh.thermalexpansion.gui.slot;

import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.block.device.TileWorkbench;
import cofh.thermalexpansion.gui.container.device.ContainerWorkbench;

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
		myContainer.onCraftMatrixChanged(null);
		return this.inventory.getStackInSlot(getSlotIndex());
	}

	public ItemStack getStackNoUpdate() {

		return this.inventory.getStackInSlot(getSlotIndex());
	}

	@Override
	protected void onCrafting(ItemStack stack) {

		stack.onCrafting(this.myPlayer.worldObj, this.myPlayer, this.amountCrafted);
		this.amountCrafted = 0;

		if (stack.getItem() == Item.getItemFromBlock(Blocks.crafting_table)) {
			myPlayer.addStat(AchievementList.buildWorkBench, 1);
		} else if (stack.getItem() instanceof ItemPickaxe) {
			myPlayer.addStat(AchievementList.buildPickaxe, 1);
		} else if (stack.getItem() == Item.getItemFromBlock(Blocks.furnace)) {
			myPlayer.addStat(AchievementList.buildFurnace, 1);
		} else if (stack.getItem() instanceof ItemHoe) {
			myPlayer.addStat(AchievementList.buildHoe, 1);
		} else if (stack.getItem() == Items.bread) {
			myPlayer.addStat(AchievementList.makeBread, 1);
		} else if (stack.getItem() == Items.cake) {
			myPlayer.addStat(AchievementList.bakeCake, 1);
		} else if (stack.getItem() instanceof ItemPickaxe && ((ItemPickaxe) stack.getItem()).func_150913_i() != Item.ToolMaterial.WOOD) {
			myPlayer.addStat(AchievementList.buildBetterPickaxe, 1);
		} else if (stack.getItem() instanceof ItemSword) {
			myPlayer.addStat(AchievementList.buildSword, 1);
		} else if (stack.getItem() == Item.getItemFromBlock(Blocks.enchanting_table)) {
			myPlayer.addStat(AchievementList.enchantments, 1);
		} else if (stack.getItem() == Item.getItemFromBlock(Blocks.bookshelf)) {
			myPlayer.addStat(AchievementList.bookcase, 1);
		}
	}

}
