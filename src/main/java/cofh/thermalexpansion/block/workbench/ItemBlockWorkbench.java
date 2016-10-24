package cofh.thermalexpansion.block.workbench;

import cofh.api.item.IInventoryContainerItem;
import cofh.core.item.ItemBlockBase;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.SecurityHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.block.cell.BlockCell;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

public class ItemBlockWorkbench extends ItemBlockBase implements IInventoryContainerItem {

	public ItemBlockWorkbench(Block block) {

		super(block);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {

		return "tile.thermalexpansion.workbench." + BlockWorkbench.NAMES[ItemHelper.getItemDamage(stack)] + ".name";
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {

		switch (BlockCell.Types.values()[ItemHelper.getItemDamage(stack)]) {
		case CREATIVE:
			return EnumRarity.EPIC;
		case RESONANT:
			return EnumRarity.RARE;
		case REINFORCED:
			return EnumRarity.UNCOMMON;
		default:
			return EnumRarity.COMMON;
		}
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean check) {

		SecurityHelper.addOwnerInformation(stack, list);
		if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
			list.add(StringHelper.shiftForDetails());
		}
		if (!StringHelper.isShiftKeyDown()) {
			return;
		}
		int meta = ItemHelper.getItemDamage(stack);
		SecurityHelper.addAccessInformation(stack, list);
		list.add(StringHelper.getInfoText("info.thermalexpansion.workbench"));
		ItemHelper.addInventoryInformation(stack, list, 0, TileWorkbench.INVENTORY[meta]);
	}

	/* IInventoryContainerItem */
	@Override
	public int getSizeInventory(ItemStack container) {

		return TileWorkbench.INVENTORY[ItemHelper.getItemDamage(container)];
	}

}
