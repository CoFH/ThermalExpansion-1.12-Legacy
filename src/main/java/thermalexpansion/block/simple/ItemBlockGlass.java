package thermalexpansion.block.simple;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import cofh.util.StringHelper;

public class ItemBlockGlass extends ItemBlock {

	public ItemBlockGlass(int id) {

		super(id);
		setHasSubtypes(true);
		setMaxDamage(0);
	}

	@Override
	public String getItemStackDisplayName(ItemStack item) {

		return StringHelper.localize(getUnlocalizedName(item));
	}

	@Override
	public String getUnlocalizedName(ItemStack item) {

		return "tile.thermalexpansion.glass.name";
	}

	@Override
	public int getMetadata(int i) {

		return i;
	}

}
