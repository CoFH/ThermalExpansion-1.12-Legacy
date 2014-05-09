package thermalexpansion.block.device;

import cofh.block.ItemBlockCoFHBase;
import cofh.util.ItemHelper;
import cofh.util.StringHelper;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ItemBlockDevice extends ItemBlockCoFHBase {

	public ItemBlockDevice(Block block) {

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

		return "tile.thermalexpansion.device." + BlockDevice.NAMES[stack.getItemDamage()] + ".name";
	}

	@Override
	public int getMetadata(int i) {

		return i;
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean check) {

		if (IS_SECURE[stack.getItemDamage()]) {
			if (stack.stackTagCompound == null || !stack.stackTagCompound.hasKey("Owner")) {
				list.add(StringHelper.localize("info.cofh.owner") + ": " + StringHelper.localize("info.cofh.none"));
				return;
			} else {
				list.add(StringHelper.localize("info.cofh.owner") + ": " + stack.stackTagCompound.getString("Owner"));
			}
		}
		if (IS_INVENTORY[stack.getItemDamage()]) {
			ItemHelper.addInventoryInformation(stack, list, 0, 20);
		}
	}

	public static final boolean[] IS_SECURE = { true, false, false, false, false, false, false, false };
	public static final boolean[] IS_INVENTORY = { true, false, false, false, false, false, false, false };

}
