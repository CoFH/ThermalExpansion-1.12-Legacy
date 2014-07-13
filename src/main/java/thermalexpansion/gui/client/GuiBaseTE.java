package thermalexpansion.gui.client;

import cofh.gui.GuiBaseAdv;

import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

public abstract class GuiBaseTE extends GuiBaseAdv {

	public GuiBaseTE(Container container) {

		super(container);
	}

	public GuiBaseTE(Container container, ResourceLocation texture) {

		super(container, texture);
	}

}
