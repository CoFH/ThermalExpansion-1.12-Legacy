package cofh.thermalexpansion.block.simple;

import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.StringHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.util.List;

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

		return "tile.thermalexpansion.rockwool." + EnumDyeColor.byMetadata(item.getItemDamage()) + ".name";
	}

	@Override
	public int getMetadata(int i) {

		return i;
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean check) {

		if (ItemHelper.getItemDamage(stack) == BlockRockwool.DEFAULT_META) {
			list.add(StringHelper.getNoticeText("info.thermalexpansion.rockwool.default"));
		} else {
			list.add(StringHelper.getNoticeText("info.thermalexpansion.rockwool.dyed"));
		}
	}

}
