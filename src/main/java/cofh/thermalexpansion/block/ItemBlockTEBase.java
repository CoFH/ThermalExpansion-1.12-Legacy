package cofh.thermalexpansion.block;

import cofh.api.item.ICreativeItem;
import cofh.api.item.INBTCopyIngredient;
import cofh.core.block.BlockCore;
import cofh.core.block.ItemBlockCore;
import cofh.core.init.CoreProps;
import cofh.core.util.helpers.StringHelper;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

public abstract class ItemBlockTEBase extends ItemBlockCore implements ICreativeItem, INBTCopyIngredient {

	public ItemBlockTEBase(BlockCore block) {

		super(block);
	}

	/* ILeveledItem */
	@Override
	public int getMaxLevel(ItemStack stack) {

		return CoreProps.LEVEL_MAX;
	}

	public abstract ItemStack setDefaultTag(ItemStack stack, int level);

	@Override
	public String getItemStackDisplayName(ItemStack stack) {

		return StringHelper.localize(getUnlocalizedName(stack)) + " (" + StringHelper.localize("info.thermalexpansion.level." + (isCreative(stack) ? "creative" : getLevel(stack))) + ")";
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {

		if (isCreative(stack)) {
			return EnumRarity.EPIC;
		}
		switch (getLevel(stack)) {
			case 4:
				return EnumRarity.RARE;
			case 3:
			case 2:
				return EnumRarity.UNCOMMON;
			default:
				return EnumRarity.COMMON;
		}
	}

}
