package cofh.thermalexpansion.init;

import cofh.core.util.core.IInitializer;
import cofh.thermalexpansion.item.*;

import java.util.ArrayList;

public class TEItems {

	private TEItems() {

	}

	public static void preInit() {

		itemFrame = new ItemFrame();
		itemUpgrade = new ItemUpgrade();
		itemAugment = new ItemAugment();

		itemCapacitor = new ItemCapacitor();
		itemSatchel = new ItemSatchel();

		initList.add(itemFrame);
		initList.add(itemUpgrade);
		initList.add(itemAugment);

		initList.add(itemCapacitor);
		initList.add(itemSatchel);

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

	static ArrayList<IInitializer> initList = new ArrayList<>();

	/* REFERENCES */
	public static ItemFrame itemFrame;
	public static ItemUpgrade itemUpgrade;
	public static ItemAugment itemAugment;

	public static ItemCapacitor itemCapacitor;
	public static ItemSatchel itemSatchel;

}
