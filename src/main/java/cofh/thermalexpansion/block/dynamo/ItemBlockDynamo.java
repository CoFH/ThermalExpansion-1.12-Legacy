package cofh.thermalexpansion.block.dynamo;

import cofh.api.tileentity.IRedstoneControl.ControlMode;
import cofh.core.block.BlockCore;
import cofh.core.util.helpers.*;
import cofh.thermalexpansion.block.ItemBlockTEBase;
import cofh.thermalexpansion.block.dynamo.BlockDynamo.Type;
import cofh.thermalexpansion.init.TEProps;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBlockDynamo extends ItemBlockTEBase {

	public ItemBlockDynamo(BlockCore block) {

		super(block);
	}

	@Override
	public ItemStack setDefaultTag(ItemStack stack, int level) {

		ReconfigurableHelper.setFacing(stack, 1);
		RedstoneControlHelper.setControl(stack, ControlMode.DISABLED);
		EnergyHelper.setDefaultEnergyTag(stack, 0);
		stack.getTagCompound().setByte("Level", (byte) level);

		return stack;
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {

		SecurityHelper.addOwnerInformation(stack, tooltip);
		if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
			tooltip.add(StringHelper.shiftForDetails());
		}
		if (!StringHelper.isShiftKeyDown()) {
			return;
		}
		SecurityHelper.addAccessInformation(stack, tooltip);

		tooltip.add(StringHelper.localize("info.thermalexpansion.dynamo.0"));
		String name = Type.values()[ItemHelper.getItemDamage(stack)].getName();
		tooltip.add(StringHelper.getInfoText("info.thermalexpansion.dynamo." + name));

		if (getLevel(stack) >= TEProps.levelRedstoneControl) {
			RedstoneControlHelper.addRSControlInformation(stack, tooltip);
		}
	}

}
