package cofh.thermalexpansion.block.automaton;

import cofh.core.util.helpers.RedstoneControlHelper;
import cofh.core.util.helpers.SecurityHelper;
import cofh.core.util.tileentity.IRedstoneControl.ControlMode;
import cofh.lib.util.helpers.EnergyHelper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.block.ItemBlockTEBase;
import cofh.thermalexpansion.util.ReconfigurableHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ItemBlockAutomaton extends ItemBlockTEBase {

	public ItemBlockAutomaton(Block block) {

		super(block);
	}

	@Override
	public ItemStack setDefaultTag(ItemStack stack, int level) {

		ReconfigurableHelper.setFacing(stack, 3);
		ReconfigurableHelper.setSideCache(stack, TileAutomatonBase.SIDE_CONFIGS[ItemHelper.getItemDamage(stack)].defaultSides);
		RedstoneControlHelper.setControl(stack, ControlMode.DISABLED);
		EnergyHelper.setDefaultEnergyTag(stack, 0);
		stack.getTagCompound().setByte("Level", (byte) level);

		return stack;
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
