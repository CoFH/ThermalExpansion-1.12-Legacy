package cofh.thermalexpansion.gui;

import cofh.CoFHCore;
import cofh.core.util.CoreUtils;
import cofh.lib.util.TimeTracker;
import cofh.lib.util.helpers.MathHelper;
import cofh.thermalexpansion.item.TEFlorbs;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TECreativeTabFlorbs extends CreativeTabs {

	int iconIndex = 0;
	TimeTracker iconTracker = new TimeTracker();

	public TECreativeTabFlorbs() {

		super("ThermalExpansionFlorbs");
	}

	@Override
	public ItemStack getIconItemStack() {

		updateIcon();
		return TEFlorbs.florbList.get(iconIndex);
	}

	@Override
	@SideOnly (Side.CLIENT)
	public Item getTabIconItem() {

		return getIconItemStack().getItem();
	}

	@Override
	@SideOnly (Side.CLIENT)
	public String getTabLabel() {

		return "thermalexpansion.creativeTabFlorbs";
	}

	private void updateIcon() {

		World world = CoFHCore.proxy.getClientWorld();

		if (CoreUtils.isClient() && iconTracker.hasDelayPassed(world, 80)) {
			int next = MathHelper.RANDOM.nextInt(TEFlorbs.florbList.size() - 1);
			iconIndex = next >= iconIndex ? next + 1 : next;
			iconTracker.markTime(world);
		}
	}

}
