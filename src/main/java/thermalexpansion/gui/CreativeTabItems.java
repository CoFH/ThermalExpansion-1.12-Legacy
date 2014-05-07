package thermalexpansion.gui;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thermalexpansion.item.TEItems;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CreativeTabItems extends CreativeTabs {

	public CreativeTabItems() {

		super("ThermalExpansionItems");
	}

	@Override
	public ItemStack getIconItemStack() {

		return TEItems.powerCoilElectrum;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getTabIconItem() {

		return getIconItemStack().getItem();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getTabLabel() {

		return "thermalexpansion.creativeTabItems";
	}

}
