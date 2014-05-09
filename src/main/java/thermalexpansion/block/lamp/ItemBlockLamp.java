package thermalexpansion.block.lamp;

import cofh.block.ItemBlockCoFHBase;
import cofh.util.StringHelper;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class ItemBlockLamp extends ItemBlockCoFHBase {

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
