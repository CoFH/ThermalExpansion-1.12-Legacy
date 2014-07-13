package thermalexpansion.block.dynamo;

import cofh.api.tileentity.IRedstoneControl.ControlMode;
import cofh.item.ItemBlockBase;
import cofh.util.EnergyHelper;
import cofh.util.RSControlHelper;
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
		RSControlHelper.setControl(container, ControlMode.DISABLED);
		EnergyHelper.setDefaultEnergyTag(container, 0);

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
			list.add(StringHelper.shiftForInfo());
		}
		if (!StringHelper.isShiftKeyDown()) {
			return;
		}
		SecurityHelper.addAccessInformation(stack, list);
		RSControlHelper.addRSControlInformation(stack, list);

		list.add(StringHelper.localize("info.thermalexpansion.dynamo.generate"));

		switch (BlockDynamo.Types.values()[stack.getItemDamage()]) {
		case STEAM:
			list.add(StringHelper.localize("info.thermalexpansion.dynamo.steam1"));
			list.add(StringHelper.getInfoText("info.thermalexpansion.dynamo.steam2"));
			break;
		case MAGMATIC:
			list.add(StringHelper.localize("info.thermalexpansion.dynamo.magmatic"));
			break;
		case COMPRESSION:
			list.add(StringHelper.localize("info.thermalexpansion.dynamo.compression"));
			break;
		case REACTANT:
			list.add(StringHelper.localize("info.thermalexpansion.dynamo.reactant"));
			break;
		case ENERVATION:
			list.add(StringHelper.localize("info.thermalexpansion.dynamo.enervation"));
			break;
		}
	}

}
