package cofh.thermalexpansion.block.device;

import cofh.api.tileentity.IRedstoneControl.ControlMode;
import cofh.core.block.ItemBlockCore;
import cofh.lib.util.helpers.*;
import cofh.thermalexpansion.block.device.BlockDevice.Types;
import cofh.thermalexpansion.util.ReconfigurableHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

public class ItemBlockDevice extends ItemBlockCore {

	public static ItemStack setDefaultTag(ItemStack container) {

		ReconfigurableHelper.setFacing(container, 3);
		ReconfigurableHelper.setSideCache(container, TileDeviceBase.defaultSideConfig[container.getItemDamage()].defaultSides);
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
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean check) {

		if (BlockDevice.Types.fromMeta(stack.getMetadata()) == Types.ACTIVATOR) {
			list.add(TextFormatting.RED + "WIP, May work, may not.");
		}
		SecurityHelper.addOwnerInformation(stack, list);
		if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
			list.add(StringHelper.shiftForDetails());
		}
		if (!StringHelper.isShiftKeyDown()) {
			return;
		}
		SecurityHelper.addAccessInformation(stack, list);
		list.add(StringHelper.getInfoText("info.thermalexpansion.device." + BlockDevice.NAMES[ItemHelper.getItemDamage(stack)]));

		if (ItemHelper.getItemDamage(stack) == BlockDevice.Types.WORKBENCH_FALSE.ordinal()) {
			ItemHelper.addInventoryInformation(stack, list, 0, 20);
		}

	}

}
