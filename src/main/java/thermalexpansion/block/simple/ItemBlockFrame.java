package thermalexpansion.block.simple;

import cofh.item.ItemBlockBase;

import net.minecraft.block.Block;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

public class ItemBlockFrame extends ItemBlockBase {

	public ItemBlockFrame(Block block) {

		super(block);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {

		return "tile.thermalexpansion.frame." + BlockFrame.NAMES[stack.getItemDamage()] + ".name";
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {

		switch (BlockFrame.Types.values()[stack.getItemDamage()]) {
		case MACHINE_RESONANT:
		case TESSERACT_FULL:
			return EnumRarity.rare;
		case MACHINE_REINFORCED:
		case CELL_REINFORCED_FULL:
			return EnumRarity.uncommon;
		default:
			return EnumRarity.common;
		}
	}

}
