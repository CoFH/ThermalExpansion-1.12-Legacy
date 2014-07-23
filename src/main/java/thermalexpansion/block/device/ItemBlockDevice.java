package thermalexpansion.block.device;

import cofh.api.tileentity.IRedstoneControl.ControlMode;
import cofh.item.ItemBlockBase;
import cofh.util.AugmentHelper;
import cofh.util.EnergyHelper;
import cofh.util.ItemHelper;
import cofh.util.RedstoneControlHelper;
import cofh.util.SecurityHelper;
import cofh.util.StringHelper;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import thermalexpansion.util.ReconfigurableHelper;

public class ItemBlockDevice extends ItemBlockBase {

	public static ItemStack setDefaultTag(ItemStack container) {

		ReconfigurableHelper.setFacing(container, 3);
		ReconfigurableHelper.setSideCache(container, new byte[] { 0, 0, 0, 0, 0, 0 });
		RedstoneControlHelper.setControl(container, ControlMode.DISABLED);
		EnergyHelper.setDefaultEnergyTag(container, 0);
		AugmentHelper.writeAugments(container, BlockDevice.defaultAugments);

		return container;
	}

	public ItemBlockDevice(Block block) {

		super(block);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {

		return "tile.thermalexpansion.device." + BlockDevice.NAMES[ItemHelper.getItemDamage(stack)] + ".name";
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
		if (ItemHelper.getItemDamage(stack) == BlockDevice.Types.WORKBENCH.ordinal()) {
			ItemHelper.addInventoryInformation(stack, list, 0, 20);
		}
	}

}
