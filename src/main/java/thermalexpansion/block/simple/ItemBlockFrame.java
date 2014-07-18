package thermalexpansion.block.simple;

import cofh.item.ItemBlockBase;
import cofh.util.ItemHelper;

import net.minecraft.block.Block;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

import thermalfoundation.item.TFItems;

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
		case MACHINE_ENDERIUM:
		case TESSERACT_FULL:
			return EnumRarity.rare;
		case MACHINE_REINFORCED:
		case CELL_REINFORCED_FULL:
			return EnumRarity.uncommon;
		default:
			return EnumRarity.common;
		}
	}

	@Override
	public boolean hasContainerItem(ItemStack stack) {

		return stack.getItemDamage() < BlockFrame.Types.MACHINE_ENDERIUM.ordinal();
	}

	@Override
	public ItemStack getContainerItem(ItemStack stack) {

		switch (BlockFrame.Types.values()[stack.getItemDamage()]) {
		case MACHINE_REINFORCED:
			return ItemHelper.cloneStack(TFItems.gearSignalum, 1);
		case MACHINE_HARDENED:
			return ItemHelper.cloneStack(TFItems.gearElectrum, 1);
		case MACHINE_BASIC:
			return ItemHelper.cloneStack(TFItems.gearTin, 1);
		default:
			return null;
		}
	}

}
