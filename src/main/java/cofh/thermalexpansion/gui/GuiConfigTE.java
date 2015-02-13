package cofh.thermalexpansion.gui;

import cofh.thermalexpansion.ThermalExpansion;
import cpw.mods.fml.client.config.DummyConfigElement.DummyCategoryElement;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;

public class GuiConfigTE extends GuiConfig {

	public GuiConfigTE(GuiScreen parentScreen) {

		super(parentScreen, getConfigElements(parentScreen), ThermalExpansion.modId, false, false, ThermalExpansion.modName);
	}

	public static final String[] CATEGORIES_CLIENT = { "Machine" };
	public static final String[] CATEGORIES_COMMON = { "Machine" };

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static List<IConfigElement> getConfigElements(GuiScreen parent) {

		List<IConfigElement> list = new ArrayList<IConfigElement>();

		list.add(new DummyCategoryElement("Client", "config.Client", getClientConfigElements()));
		list.add(new DummyCategoryElement("Common", "config.Common", getCommonConfigElements()));

		return list;
	}

	@SuppressWarnings("rawtypes")
	private static List<IConfigElement> getClientConfigElements() {

		List<IConfigElement> list = new ArrayList<IConfigElement>();

		for (int i = 0; i < CATEGORIES_CLIENT.length; i++) {
			list.add(new ConfigElement<ConfigCategory>(ThermalExpansion.configClient.getCategory(CATEGORIES_CLIENT[i])));
		}
		return list;
	}

	@SuppressWarnings("rawtypes")
	private static List<IConfigElement> getCommonConfigElements() {

		List<IConfigElement> list = new ArrayList<IConfigElement>();

		for (int i = 0; i < CATEGORIES_COMMON.length; i++) {
			list.add(new ConfigElement<ConfigCategory>(ThermalExpansion.config.getCategory(CATEGORIES_COMMON[i])));
		}
		return list;
	}

}
