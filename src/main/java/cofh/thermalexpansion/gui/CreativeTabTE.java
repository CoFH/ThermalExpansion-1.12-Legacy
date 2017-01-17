package cofh.thermalexpansion.gui;

import cofh.thermalexpansion.block.machine.BlockMachine;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CreativeTabTE extends CreativeTabs {

	private final String label;

	public CreativeTabTE() {

		this("");
	}

	public CreativeTabTE(String label) {

		super("ThermalExpansion" + label);
		this.label = label;
	}

	protected ItemStack getStack() {

		return BlockMachine.furnace;
	}

	@Override
	@SideOnly (Side.CLIENT)
	public ItemStack getIconItemStack() {

		return getStack();
	}

	@Override
	@SideOnly (Side.CLIENT)
	public Item getTabIconItem() {

		return getIconItemStack().getItem();
	}

	@Override
	@SideOnly (Side.CLIENT)
	public String getTabLabel() {

		return "thermalexpansion.creativeTab" + label;
	}

}
