package thermalexpansion.block.machine;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import cofh.block.ItemBlockCoFHBase;
import cofh.util.StringHelper;

public class ItemBlockMachine extends ItemBlockCoFHBase {

	public ItemBlockMachine(Block block) {

		super(block);
		setHasSubtypes(true);
		setMaxDamage(0);
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {

		return StringHelper.localize(getUnlocalizedName(stack));
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {

		return "tile.thermalexpansion.machine." + BlockMachine.NAMES[stack.getItemDamage()] + ".name";
	}

	@Override
	public int getMetadata(int i) {

		return i;
	}

}
