package cofh.thermalexpansion.block;

import cofh.api.item.ICreativeItem;
import cofh.api.item.INBTCopyIngredient;
import cofh.core.block.ItemBlockCore;
import cofh.core.init.CoreProps;
import cofh.core.util.helpers.StringHelper;
import net.minecraft.block.Block;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

public abstract class ItemBlockTEBase extends ItemBlockCore implements ICreativeItem, INBTCopyIngredient {

	public ItemBlockTEBase(Block block) {

		super(block);
	}

	/* ILeveledItem */
	public int getLevel(ItemStack stack) {

		if (stack.getTagCompound() == null) {
			setDefaultTag(stack);
		}
		return stack.getTagCompound().getByte("Level");
	}

	public ItemStack setLevel(ItemStack stack, int level) {

		if (stack.getTagCompound() == null) {
			return setDefaultTag(stack, level);
		}
		stack.getTagCompound().setByte("Level", (byte) level);
		return stack;
	}

	public ItemStack setDefaultTag(ItemStack stack) {

		return setDefaultTag(stack, 0);
	}

	public abstract ItemStack setDefaultTag(ItemStack stack, int level);

	/* ICreativeItem */
	@Override
	public boolean isCreative(ItemStack stack) {

		if (stack.getTagCompound() == null) {
			setDefaultTag(stack);
		}
		return stack.getTagCompound().getBoolean("Creative");
	}

	@Override
	public ItemStack setCreativeTag(ItemStack stack) {

		if (stack.getTagCompound() == null) {
			setDefaultTag(stack, CoreProps.LEVEL_MAX);
		}
		stack.getTagCompound().setBoolean("Creative", true);
		return stack;
	}

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
