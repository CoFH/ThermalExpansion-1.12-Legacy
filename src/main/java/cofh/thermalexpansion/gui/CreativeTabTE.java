package cofh.thermalexpansion.gui;

import cofh.thermalexpansion.block.machine.BlockMachine;
import net.minecraft.creativetab.CreativeTabs;
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

		return BlockMachine.machineFurnace;
	}

	@Override
	@SideOnly (Side.CLIENT)
	public ItemStack getIconItemStack() {

		return getStack();
	}

	@Override
	@SideOnly (Side.CLIENT)
	public ItemStack getTabIconItem() {

		return getIconItemStack();
	}

	@Override
	@SideOnly (Side.CLIENT)
	public String getTabLabel() {

		return "thermalexpansion.creativeTab" + label;
	}

}
