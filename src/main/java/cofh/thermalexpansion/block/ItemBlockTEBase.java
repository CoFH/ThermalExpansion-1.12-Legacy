package cofh.thermalexpansion.block;

import cofh.core.block.ItemBlockCore;
import cofh.lib.util.helpers.StringHelper;
import net.minecraft.block.Block;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

public abstract class ItemBlockTEBase extends ItemBlockCore {

	public ItemBlockTEBase(Block block) {

		super(block);
	}

	public boolean isCreative(ItemStack stack) {

		if (stack.getTagCompound() == null) {
			setDefaultTag(stack);
		}
		return stack.getTagCompound().getBoolean("Creative");
	}

	public byte getLevel(ItemStack stack) {

		if (stack.getTagCompound() == null) {
			setDefaultTag(stack);
		}
		return stack.getTagCompound().getByte("Level");
	}

	public ItemStack setDefaultTag(ItemStack stack) {

		return setDefaultTag(stack, 0);
	}

	public abstract ItemStack setDefaultTag(ItemStack stack, int level);

	public ItemStack setCreativeTag(ItemStack stack, int level) {

		if (stack.getTagCompound() == null) {
			setDefaultTag(stack, level);
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
