package cofh.thermalexpansion.block.automaton;

import cofh.api.tileentity.IRedstoneControl.ControlMode;
import cofh.core.block.ItemBlockCore;
import cofh.lib.util.helpers.*;
import cofh.thermalexpansion.block.device.BlockDevice;
import cofh.thermalexpansion.util.ReconfigurableHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ItemBlockAutomaton extends ItemBlockCore {

	public static ItemStack setDefaultTag(ItemStack container) {

		ReconfigurableHelper.setFacing(container, 3);
		ReconfigurableHelper.setSideCache(container, TileAutomatonBase.defaultSideConfig[container.getItemDamage()].defaultSides);
		RedstoneControlHelper.setControl(container, ControlMode.DISABLED);
		EnergyHelper.setDefaultEnergyTag(container, 0);

		return container;
	}

	public ItemBlockAutomaton(Block block) {

		super(block);
		setHasSubtypes(true);
		setMaxDamage(0);
		setNoRepair();
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {

		return "tile.thermalexpansion.automaton." + BlockDevice.Type.byMetadata(ItemHelper.getItemDamage(stack)).getName() + ".name";
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean check) {

		SecurityHelper.addOwnerInformation(stack, list);
		if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
			list.add(StringHelper.shiftForDetails());
		}
		if (!StringHelper.isShiftKeyDown()) {
			return;
		}
		SecurityHelper.addAccessInformation(stack, list);

		list.add(StringHelper.getInfoText("info.thermalexpansion.automaton." + BlockDevice.Type.byMetadata(ItemHelper.getItemDamage(stack)).getName()));

		RedstoneControlHelper.addRSControlInformation(stack, list);
	}

}
