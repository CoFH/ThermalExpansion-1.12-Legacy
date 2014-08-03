package thermalexpansion.block.machine;

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
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

import thermalexpansion.util.ReconfigurableHelper;

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

		return StringHelper.localize(getUnlocalizedName(stack)) + " (" + StringHelper.localize("info.thermalexpansion." + NAMES[getLevel(stack)]) + ")";
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {

		return "tile.thermalexpansion.machine." + BlockMachine.NAMES[ItemHelper.getItemDamage(stack)] + ".name";
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
		// RSControlHelper.addRSControlInformation(stack, list);

		list.add(StringHelper.getInfoText("info.thermalexpansion.machine." + BlockMachine.NAMES[ItemHelper.getItemDamage(stack)]));
	}

	public static final String[] NAMES = { "basic", "hardened", "reinforced", "resonant" };

}
