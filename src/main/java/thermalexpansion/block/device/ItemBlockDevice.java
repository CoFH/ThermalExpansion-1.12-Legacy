package thermalexpansion.block.device;

import cofh.item.ItemBlockBase;
import cofh.util.ItemHelper;
import cofh.util.SecurityHelper;
import cofh.util.StringHelper;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ItemBlockDevice extends ItemBlockBase {

	public ItemBlockDevice(Block block) {

		super(block);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {

		return "tile.thermalexpansion.device." + BlockDevice.NAMES[stack.getItemDamage()] + ".name";
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean check) {

		SecurityHelper.addOwnerInformation(stack, list);
		if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
			list.add(StringHelper.shiftForDetails());
		}
		if (!StringHelper.isShiftKeyDown()) {
			return;
		}
		SecurityHelper.addAccessInformation(stack, list);
		if (stack.getItemDamage() == BlockDevice.Types.WORKBENCH.ordinal()) {
			ItemHelper.addInventoryInformation(stack, list, 0, 20);
		}
	}

}
