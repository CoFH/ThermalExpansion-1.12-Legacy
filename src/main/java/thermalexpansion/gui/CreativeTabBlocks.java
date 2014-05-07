package thermalexpansion.gui;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thermalexpansion.block.energycell.BlockEnergyCell;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CreativeTabBlocks extends CreativeTabs {

	public CreativeTabBlocks() {

		super("ThermalExpansionBlocks");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getIconItemStack() {

		return BlockEnergyCell.cellReinforcedFrameFull;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getTabIconItem() {

		return getIconItemStack().getItem();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getTabLabel() {

		return "thermalexpansion.creativeTabBlocks";
	}

}
