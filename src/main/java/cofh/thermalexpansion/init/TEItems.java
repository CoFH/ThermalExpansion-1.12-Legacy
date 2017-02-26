package cofh.thermalexpansion.init;

import cofh.api.core.IInitializer;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.item.ItemAugment;
import cofh.thermalexpansion.item.ItemFrame;
import cofh.thermalexpansion.item.ItemUpgrade;

import java.util.ArrayList;

public class TEItems {

	private TEItems() {

	}

	public static void preInit() {

		itemFrame = new ItemFrame();
		itemUpgrade = new ItemUpgrade();
		itemAugment = new ItemAugment();

		initList.add(itemFrame);
		initList.add(itemUpgrade);
		initList.add(itemAugment);

		ThermalExpansion.proxy.addIModelRegister(itemFrame);
		ThermalExpansion.proxy.addIModelRegister(itemUpgrade);
		ThermalExpansion.proxy.addIModelRegister(itemAugment);

		for (IInitializer init : initList) {
			init.preInit();
		}
	}

	public static void initialize() {

		for (IInitializer init : initList) {
			init.initialize();
		}
	}

	public static void postInit() {

		for (IInitializer init : initList) {
			init.postInit();
		}
		initList.clear();
	}

	static ArrayList<IInitializer> initList = new ArrayList<IInitializer>();

	/* REFERENCES */
	public static ItemFrame itemFrame;
	public static ItemUpgrade itemUpgrade;
	public static ItemAugment itemAugment;

}
