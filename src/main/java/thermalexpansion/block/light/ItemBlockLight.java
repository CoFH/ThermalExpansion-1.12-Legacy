package thermalexpansion.block.light;

import cofh.util.ItemHelper;
import cofh.util.StringHelper;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockLight extends ItemBlock {

	public ItemBlockLight(Block block) {

		super(block);
		setHasSubtypes(true);
		setMaxDamage(0);
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {

		return StringHelper.localize(getUnlocalizedName(stack));
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {

		return "tile.thermalexpansion.light." + BlockLight.NAMES[ItemHelper.getItemDamage(stack)] + ".name";
	}

	@Override
	public int getMetadata(int i) {

		return i;
	}

}
