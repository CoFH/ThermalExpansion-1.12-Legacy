package thermalexpansion.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import thermalexpansion.block.machine.BlockMachine;

public class CreativeTabBlocks extends CreativeTabs {

	public CreativeTabBlocks() {

		super("ThermalExpansionBlocks");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getIconItemStack() {

		return BlockMachine.furnace;// BlockEnergyCell.cellReinforcedFrameFull;
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
