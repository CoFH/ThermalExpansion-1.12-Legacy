package thermalexpansion.gui;

import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;

import thermalexpansion.ThermalExpansion;

public class GuiConfigTE extends GuiConfig {

	public GuiConfigTE(GuiScreen parentScreen) {

		super(parentScreen, getConfigElements(parentScreen), ThermalExpansion.modId, false, false, ThermalExpansion.modName);
	}

	public static final String[] CATEGORIES = { "block", "item", "security", "plugins", "tweak" };

	@SuppressWarnings("rawtypes")
	private static List<IConfigElement> getConfigElements(GuiScreen parent) {

		List<IConfigElement> list = new ArrayList<IConfigElement>();

		for (int i = 0; i < CATEGORIES.length; i++) {
			list.add(new ConfigElement<ConfigCategory>(ThermalExpansion.config.getCategory(CATEGORIES[i])));
		}
		return list;
	}

}
