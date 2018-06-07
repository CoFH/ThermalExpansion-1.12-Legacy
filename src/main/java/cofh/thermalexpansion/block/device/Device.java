package cofh.thermalexpansion.block.device;

import cofh.core.util.core.SideConfig;
import cofh.core.util.core.SlotConfig;

public class Device {

	SideConfig sideConfig = new SideConfig();
	SlotConfig slotConfig = new SlotConfig();
	int activeLight = 0;

	final String name;

	public Device(String name) {

		this.name = name;
	}

	public void setSideConfig(SideConfig config) {

		sideConfig = config;
	}

	public void setSlotConfig(SlotConfig config) {

		slotConfig = config;
	}

	public SideConfig getSideConfig() {

		return sideConfig;
	}

	public SlotConfig getSlotConfig() {

		return slotConfig;
	}

	public int getActiveLight() {

		return activeLight;
	}

}
