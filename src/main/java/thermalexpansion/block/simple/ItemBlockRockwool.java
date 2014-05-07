package thermalexpansion.block.simple;

import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import cofh.util.StringHelper;

public class ItemBlockRockwool extends ItemBlock {

	public ItemBlockRockwool(Block block) {

		super(block);
		setHasSubtypes(true);
		setMaxDamage(0);
	}

	@Override
	public String getItemStackDisplayName(ItemStack item) {

		return StringHelper.localize(getUnlocalizedName(item));
	}

	@Override
	public String getUnlocalizedName(ItemStack item) {

		return "tile.thermalexpansion.rockwool." + ItemDye.dyeColorNames[BlockColored.getBlockFromDye(item.getItemDamage())] + ".name";
	}

	@Override
	public int getMetadata(int i) {

		return i;
	}

}
