package thermalexpansion.gui;

import cofh.util.MathHelper;
import cofh.util.TimeTracker;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import thermalexpansion.item.TEFlorbs;

public class CreativeTabFlorbs extends CreativeTabs {

	int iconIndex = 0;
	TimeTracker iconTracker = new TimeTracker();

	public CreativeTabFlorbs() {

		super("ThermalExpansionFlorbs");
	}

	@Override
	public ItemStack getIconItemStack() {

		updateIcon();
		return TEFlorbs.florbList.get(iconIndex);
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

	private void updateIcon() {

		World world = Minecraft.getMinecraft().theWorld;

		if (iconTracker.hasDelayPassed(world, 80)) {
			int next = MathHelper.RANDOM.nextInt(TEFlorbs.florbList.size() - 1);
			iconIndex = next >= iconIndex ? next + 1 : next;
			iconTracker.markTime(world);
		}
	}

}
