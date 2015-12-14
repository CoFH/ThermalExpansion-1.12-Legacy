package cofh.thermalexpansion.block.machine;

import cofh.api.tileentity.IRedstoneControl.ControlMode;
import cofh.core.item.ItemBlockBase;
import cofh.lib.util.helpers.AugmentHelper;
import cofh.lib.util.helpers.EnergyHelper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.RedstoneControlHelper;
import cofh.lib.util.helpers.SecurityHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.util.helpers.ReconfigurableHelper;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class ItemBlockMachine extends ItemBlockBase {

	public static ItemStack setDefaultTag(ItemStack container) {

		return setDefaultTag(container, (byte) 0);
	}

	public static ItemStack setDefaultTag(ItemStack container, byte level) {

		ReconfigurableHelper.setFacing(container, 3);
		ReconfigurableHelper.setSideCache(container, TileMachineBase.defaultSideConfig[container.getItemDamage()].defaultSides);
		RedstoneControlHelper.setControl(container, ControlMode.DISABLED);
		EnergyHelper.setDefaultEnergyTag(container, 0);
		container.stackTagCompound.setByte("Level", level);
		AugmentHelper.writeAugments(container, BlockMachine.defaultAugments);

		return container;
	}

	public static byte getLevel(ItemStack container) {

		if (container.stackTagCompound == null) {
			setDefaultTag(container);
		}
		return container.stackTagCompound.getByte("Level");
	}

	public ItemBlockMachine(Block block) {

		super(block);
		setHasSubtypes(true);
		setMaxDamage(0);
		setNoRepair();
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {

		String unloc = getUnlocalizedNameInefficiently(stack);
		String unloc2 = '.' + NAMES[getLevel(stack)];

		if (StatCollector.canTranslate(unloc + unloc2 + ".name")) {
			return StringHelper.localize(unloc + unloc2 + ".name");
		}

		return StringHelper.localize(unloc + ".name") + " (" + StringHelper.localize("info.thermalexpansion" + unloc2) + ")";
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {

		return "tile.thermalexpansion.machine." + BlockMachine.NAMES[ItemHelper.getItemDamage(stack)];
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {

		switch (getLevel(stack)) {
		case 3:
			return EnumRarity.rare;
		case 2:
			return EnumRarity.uncommon;
		default:
			return EnumRarity.common;
		}
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

		list.add(StringHelper.getInfoText("info.thermalexpansion.machine." + BlockMachine.NAMES[ItemHelper.getItemDamage(stack)]));

		RedstoneControlHelper.addRSControlInformation(stack, list);
	}

	public static final String[] NAMES = { "basic", "hardened", "reinforced", "resonant" };

}
