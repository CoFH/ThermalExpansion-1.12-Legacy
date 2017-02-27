package cofh.thermalexpansion.gui;

import cofh.thermalexpansion.ThermalExpansion;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.ArrayList;
import java.util.List;

public class GuiConfigTE extends GuiConfig {

	public GuiConfigTE(GuiScreen parentScreen) {

		super(parentScreen, getConfigElements(parentScreen), ThermalExpansion.MOD_ID, false, false, ThermalExpansion.MOD_NAME);
	}

	public static final String[] CATEGORIES_CLIENT = { "Machine" };
	public static final String[] CATEGORIES_COMMON = { "Machine" };

	private static List<IConfigElement> getConfigElements(GuiScreen parent) {

		List<IConfigElement> list = new ArrayList<>();

		list.add(new DummyCategoryElement("Client", "config.Client", getClientConfigElements()));
		list.add(new DummyCategoryElement("Common", "config.Common", getCommonConfigElements()));

		return list;
	}

	private static List<IConfigElement> getClientConfigElements() {

		List<IConfigElement> list = new ArrayList<>();

		for (int i = 0; i < CATEGORIES_CLIENT.length; i++) {
			list.add(new ConfigElement(ThermalExpansion.CONFIG_CLIENT.getCategory(CATEGORIES_CLIENT[i])));
		}
		return list;
	}

	private static List<IConfigElement> getCommonConfigElements() {

		List<IConfigElement> list = new ArrayList<>();

		for (int i = 0; i < CATEGORIES_COMMON.length; i++) {
			list.add(new ConfigElement(ThermalExpansion.CONFIG.getCategory(CATEGORIES_COMMON[i])));
		}
		return list;
	}

}
