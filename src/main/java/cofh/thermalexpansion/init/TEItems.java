package cofh.thermalexpansion.init;

import cofh.core.util.core.IInitializer;
import cofh.thermalexpansion.item.*;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;

public class TEItems {

	public static final TEItems INSTANCE = new TEItems();

	private TEItems() {

	}

	public static void preInit() {

		itemFrame = new ItemFrame();
		itemUpgrade = new ItemUpgrade();
		itemAugment = new ItemAugment();

		itemCapacitor = new ItemCapacitor();
		itemSatchel = new ItemSatchel();

		itemFlorb = new ItemFlorb();

		initList.add(itemFrame);
		initList.add(itemUpgrade);
		initList.add(itemAugment);

		initList.add(itemCapacitor);
		initList.add(itemSatchel);

		initList.add(itemFlorb);

		for (IInitializer init : initList) {
			init.initialize();
		}
		MinecraftForge.EVENT_BUS.register(INSTANCE);
	}

	/* EVENT HANDLING */
	@SubscribeEvent
	public void registerRecipes(RegistryEvent.Register<IRecipe> event) {

		for (IInitializer init : initList) {
			init.register();
		}
	}

	static ArrayList<IInitializer> initList = new ArrayList<>();

	/* REFERENCES */
	public static ItemFrame itemFrame;
	public static ItemUpgrade itemUpgrade;
	public static ItemAugment itemAugment;

	public static ItemCapacitor itemCapacitor;
	public static ItemSatchel itemSatchel;

	public static ItemFlorb itemFlorb;

}
