package thermalexpansion.util;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

import net.minecraftforge.oredict.OreDictionary.OreRegisterEvent;

public class GenericEventHandler {

	public static GenericEventHandler instance = new GenericEventHandler();

	public static void initialize() {

		// MinecraftForge.EVENT_BUS.register(instance);
	}

	@SubscribeEvent
	public void handleOreRegisterEvent(OreRegisterEvent event) {

		// String suffix = "";
		//
		// if (event.Name.startsWith("ore")) {
		// suffix = event.Name.substring(3);
		// } else if (event.Name.startsWith("dust")) {
		// suffix = event.Name.substring(4);
		// } else if (event.Name.startsWith("ingot")) {
		// suffix = event.Name.substring(5);
		// } else if (event.Name.startsWith("block")) {
		// suffix = event.Name.substring(5);
		// } else if (event.Name.startsWith("nugget")) {
		// suffix = event.Name.substring(6);
		// } else if (event.Name.startsWith("log")) {
		// suffix = event.Name.substring(3);
		// }
		// System.out.println(event.Name);
	}

}
