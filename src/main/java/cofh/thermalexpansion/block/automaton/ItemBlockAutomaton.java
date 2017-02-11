package cofh.thermalexpansion.block.automaton;

import cofh.api.tileentity.IRedstoneControl.ControlMode;
import cofh.core.block.ItemBlockCore;
import cofh.lib.util.helpers.*;
import cofh.thermalexpansion.util.ReconfigurableHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ItemBlockAutomaton extends ItemBlockCore {

	public static ItemStack setDefaultTag(ItemStack stack) {

		return setDefaultTag(stack, (byte) 0);
	}

	public static ItemStack setDefaultTag(ItemStack stack, byte level) {

		ReconfigurableHelper.setFacing(stack, 3);
		ReconfigurableHelper.setSideCache(stack, TileAutomatonBase.defaultSideConfig[stack.getItemDamage()].defaultSides);
		RedstoneControlHelper.setControl(stack, ControlMode.DISABLED);
		EnergyHelper.setDefaultEnergyTag(stack, 0);
		stack.getTagCompound().setByte("Level", level);

		return stack;
	}

	public static byte getLevel(ItemStack stack) {

		if (stack.getTagCompound() == null) {
			setDefaultTag(stack);
		}
		return stack.getTagCompound().getByte("Level");
	}

	public ItemBlockAutomaton(Block block) {

		super(block);
		setHasSubtypes(true);
		setMaxDamage(0);
		setNoRepair();
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {

		return "tile.thermalexpansion.automaton." + BlockAutomaton.Type.byMetadata(ItemHelper.getItemDamage(stack)).getName() + ".name";
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {

		SecurityHelper.addOwnerInformation(stack, tooltip);
		if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
			tooltip.add(StringHelper.shiftForDetails());
		}
		if (!StringHelper.isShiftKeyDown()) {
			return;
		}
		SecurityHelper.addAccessInformation(stack, tooltip);

		String name = BlockAutomaton.Type.byMetadata(ItemHelper.getItemDamage(stack)).getName();
		tooltip.add(StringHelper.getInfoText("info.thermalexpansion.automaton." + name));

		RedstoneControlHelper.addRSControlInformation(stack, tooltip);
	}

}
