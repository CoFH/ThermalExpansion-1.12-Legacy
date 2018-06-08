package cofh.thermalexpansion.block.light;

import cofh.core.block.BlockCore;
import cofh.core.init.CoreProps;
import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.block.ItemBlockTEBase;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBlockLight extends ItemBlockTEBase {

	public ItemBlockLight(BlockCore block) {

		super(block);
	}

	@Override
	public ItemStack setDefaultTag(ItemStack stack, int level) {

		if (stack.getTagCompound() == null) {
			stack.setTagCompound(new NBTTagCompound());
		}
		stack.getTagCompound().setInteger(CoreProps.COLOR, -1);
		stack.getTagCompound().setByte(CoreProps.MODE, (byte) 0);
		return stack;
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {

		return StringHelper.localize(getUnlocalizedName(stack));
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {

		if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
			tooltip.add(StringHelper.shiftForDetails());
		}
		if (!StringHelper.isShiftKeyDown()) {
			return;
		}
		tooltip.add(StringHelper.getInfoText("info.thermalexpansion.light.0"));
		tooltip.add(StringHelper.getInfoText("info.thermalexpansion.light.1"));
	}

}
