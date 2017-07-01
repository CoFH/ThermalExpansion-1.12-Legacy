package cofh.thermalexpansion.gui;

import cofh.CoFHCore;
import cofh.core.util.CoreUtils;
import cofh.core.util.TimeTracker;
import cofh.core.util.helpers.MathHelper;
import cofh.thermalexpansion.init.TEFlorbs;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CreativeTabTEFlorbs extends CreativeTabs {

	int iconIndex = 0;
	TimeTracker iconTracker = new TimeTracker();

	public CreativeTabTEFlorbs() {

		super("ThermalExpansionFlorbs");
	}

	@Override
	@SideOnly (Side.CLIENT)
	public ItemStack getIconItemStack() {

		updateIcon();
		return TEFlorbs.florbList.get(iconIndex);
	}

	@Override
	@SideOnly (Side.CLIENT)
	public ItemStack getTabIconItem() {

		return getIconItemStack();
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
