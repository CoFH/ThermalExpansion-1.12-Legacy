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

		itemCapacitor = new ItemCapacitor();
		itemReservoir = new ItemReservoir();
		itemSatchel = new ItemSatchel();

		itemFrame = new ItemFrame();
		itemAugment = new ItemAugment();

		itemFlorb = new ItemFlorb();
		itemMorb = new ItemMorb();

		initList.add(itemCapacitor);
		initList.add(itemReservoir);
		initList.add(itemSatchel);

		initList.add(itemFrame);
		initList.add(itemAugment);

		initList.add(itemFlorb);
		initList.add(itemMorb);

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
	public static ItemCapacitor itemCapacitor;
	public static ItemReservoir itemReservoir;
	public static ItemSatchel itemSatchel;

	public static ItemFrame itemFrame;
	public static ItemAugment itemAugment;

	public static ItemFlorb itemFlorb;
	public static ItemMorb itemMorb;

}
