package thermalexpansion.block.dynamo;

import cofh.api.tileentity.IRedstoneControl.ControlMode;
import cofh.item.ItemBlockBase;
import cofh.util.AugmentHelper;
import cofh.util.EnergyHelper;
import cofh.util.RedstoneControlHelper;
import cofh.util.SecurityHelper;
import cofh.util.StringHelper;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import thermalexpansion.util.ReconfigurableHelper;

public class ItemBlockDynamo extends ItemBlockBase {

	public static ItemStack setDefaultTag(ItemStack container) {

		ReconfigurableHelper.setFacing(container, 1);
		RedstoneControlHelper.setControl(container, ControlMode.DISABLED);
		EnergyHelper.setDefaultEnergyTag(container, 0);
		AugmentHelper.writeAugments(container, BlockDynamo.defaultAugments);

		return container;
	}

	public ItemBlockDynamo(Block block) {

		super(block);
		setHasSubtypes(true);
		setMaxDamage(0);
		setNoRepair();
	}

	@Override
	public String getUnlocalizedName(ItemStack item) {

		return "tile.thermalexpansion.dynamo." + BlockDynamo.NAMES[item.getItemDamage()] + ".name";
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
		// RSControlHelper.addRSControlInformation(stack, list);

		list.add(StringHelper.localize("info.thermalexpansion.dynamo.generate"));
		list.add(StringHelper.localize("info.thermalexpansion.dynamo." + BlockDynamo.NAMES[stack.getItemDamage()]));

		if (stack.getItemDamage() == BlockDynamo.Types.STEAM.ordinal()) {
			list.add(StringHelper.getInfoText("info.thermalexpansion.dynamo.steam.0"));
		}
	}

}
