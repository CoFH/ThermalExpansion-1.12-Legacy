package thermalexpansion.block.plate;

import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.StringHelper;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockPlate extends ItemBlock {

	public ItemBlockPlate(Block block) {

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

		return "tile.thermalexpansion.plate." + BlockPlate.NAMES[ItemHelper.getItemDamage(stack)] + ".name";
	}

	@Override
	public int getMetadata(int i) {

		return i;
	}

}
