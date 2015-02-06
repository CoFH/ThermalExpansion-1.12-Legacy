package cofh.thermalexpansion.gui;

import cofh.thermalexpansion.item.TEItems;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;


public class CreativeTabTools extends CreativeTabs {

	public CreativeTabTools() {

		super("ThermalExpansionTools");
	}

	@Override
	public ItemStack getIconItemStack() {

		return TEItems.toolWrench;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getTabIconItem() {

		return getIconItemStack().getItem();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getTabLabel() {

		return "thermalexpansion.creativeTabTools";
	}

}
