package thermalexpansion.block.lamp;

import cofh.util.StringHelper;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockLamp extends ItemBlock {

	public ItemBlockLamp(Block block) {

		super(block);
		setMaxDamage(0);
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemstack) {

		return StringHelper.localize(getUnlocalizedName(itemstack));
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {

		return "tile.thermalexpansion.lamp.name";
	}

}
