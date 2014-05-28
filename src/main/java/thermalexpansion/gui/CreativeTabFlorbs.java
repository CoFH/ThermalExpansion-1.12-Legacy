package thermalexpansion.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import thermalexpansion.item.TEFlorbs;

public class CreativeTabFlorbs extends CreativeTabs {

	public CreativeTabFlorbs() {

		super("ThermalExpansionFlorbs");
	}

	@Override
	public ItemStack getIconItemStack() {

		return TEFlorbs.florb;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getTabIconItem() {

		return getIconItemStack().getItem();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getTabLabel() {

		return "thermalexpansion.creativeTabFlorbs";
	}

}
