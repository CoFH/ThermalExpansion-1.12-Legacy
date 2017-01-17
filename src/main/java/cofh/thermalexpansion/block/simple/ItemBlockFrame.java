package cofh.thermalexpansion.block.simple;

import cofh.core.block.ItemBlockCore;
import cofh.lib.util.helpers.ItemHelper;
import net.minecraft.block.Block;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

public class ItemBlockFrame extends ItemBlockCore {

	public ItemBlockFrame(Block block) {

		super(block);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {

		return "tile.thermalexpansion.frame." + BlockFrame.NAMES[ItemHelper.getItemDamage(stack)] + ".name";
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {

		switch (BlockFrame.Types.values()[ItemHelper.getItemDamage(stack)]) {
			case MACHINE_RESONANT:
			case CELL_RESONANT_FULL:
			case TESSERACT_FULL:
				return EnumRarity.RARE;
			case MACHINE_REINFORCED:
			case CELL_REINFORCED_FULL:
				return EnumRarity.UNCOMMON;
			default:
				return EnumRarity.COMMON;
		}
	}

}
