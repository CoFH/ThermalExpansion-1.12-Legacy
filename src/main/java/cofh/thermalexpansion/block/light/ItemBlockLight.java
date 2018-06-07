package cofh.thermalexpansion.block.light;

import cofh.core.block.BlockCore;
import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.block.ItemBlockTEBase;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBlockLight extends ItemBlockTEBase {

	public ItemBlockLight(BlockCore block) {

		super(block);
	}

	@Override
	public ItemStack setDefaultTag(ItemStack stack, int level) {

		return stack;
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
